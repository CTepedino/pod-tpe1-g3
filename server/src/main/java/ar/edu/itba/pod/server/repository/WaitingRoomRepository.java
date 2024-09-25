package ar.edu.itba.pod.server.repository;

import ar.edu.itba.pod.grpc.emergencyCare.CarePatientResponse;
import ar.edu.itba.pod.grpc.emergencyCare.RoomUpdateStatus;
import ar.edu.itba.pod.grpc.query.CaredInfo;
import ar.edu.itba.pod.server.exception.NoDischargedPatientsException;
import ar.edu.itba.pod.server.exception.NoPatientsInWaitRoomException;
import ar.edu.itba.pod.server.exception.PatientAlreadyExistsException;
import ar.edu.itba.pod.server.exception.PatientNotFoundException;
import ar.edu.itba.pod.server.model.DischargedEntry;
import ar.edu.itba.pod.server.model.Doctor;
import ar.edu.itba.pod.server.model.Patient;
import ar.edu.itba.pod.server.model.Room;
import emergencyRoom.Messages;


import java.util.*;

public class WaitingRoomRepository {

    private final List<Patient> patients;
    private final List<DischargedEntry> discharged;

    private final RoomRepository rr;
    private final DoctorRepository dr;

    public WaitingRoomRepository(RoomRepository rr, DoctorRepository dr){
        this.rr = rr;
        this.dr = dr;
        patients = new ArrayList<>();
        discharged = new ArrayList<>();
    }

    public synchronized void addPatient(String name, int level){
        Patient patient = new Patient(name, level);
        for (Patient p : patients){
            if (p.equals(patient)){
                throw new PatientAlreadyExistsException(name);
            }
        }
        for (DischargedEntry entry : discharged){
            if (entry.getPatient().equals(patient)){
                throw new PatientAlreadyExistsException(name);
            }
        }
        patients.add(patient);
    }

    public synchronized Patient findByName(String name){
        for (Patient p : patients){
            if (p.getName().equals(name)){
                return p;
            }
        }
        throw new PatientNotFoundException(name);
    }

    public synchronized void updateLevel(String name, int level){
        Patient patient = findByName(name);
        patient.setLevel(level);
    }

    public synchronized int getPatientsAhead(Patient patient){
        int ahead = 0;
        for (Patient p : patients){
            if (patient.equals(p)){
                break;
            }
            if (patient.getLevel() <= p.getLevel()){
                ahead++;
            }
        }
        return ahead;
    }

    public synchronized CarePatientResponse startCare(int roomNumber){
        Room room = rr.getRoom(roomNumber);

        if (!room.isAvailable()){
            return careResponseErrorBuilder(roomNumber, RoomUpdateStatus.ROOM_STATUS_WAS_OCCUPIED);
        }

        if (patients.isEmpty()){
            return careResponseErrorBuilder(roomNumber, RoomUpdateStatus.ROOM_STATUS_STILL_FREE);
        }

        Doctor doctor = null;
        Patient patient = null;
        int maxLevel = Patient.getMaxLevel();

        while (doctor == null) {
            if (maxLevel == 0){
                return careResponseErrorBuilder(roomNumber, RoomUpdateStatus.ROOM_STATUS_STILL_FREE);
            }
            patient = getPatientForCare(maxLevel);
            doctor = dr.getDoctorForCare(patient.getLevel());
            maxLevel = patient.getLevel() -1;
        }

        room.startCare(patient, doctor);
        patients.remove(patient);

        return careResponseBuilder(roomNumber, patient, doctor);
    }

    private Patient getPatientForCare(int maxLevel){
        Patient candidate = patients.getFirst();
        for (Patient p : patients){
            if (candidate.getLevel() == maxLevel){
                break;
            }
            if (p.getLevel() > candidate.getLevel() && p.getLevel() < maxLevel){
                candidate = p;
            }
        }
        return candidate;
    }

    public synchronized List<CarePatientResponse> startAllCare(){
        List<CarePatientResponse> caresInfo = new ArrayList<>();

        for (int room = 1; room <= rr.getRoomCount(); room++){
            caresInfo.add(startCare(room));
        }
        return caresInfo;
    }

    public synchronized CarePatientResponse endCare(int room, String patientName, String doctorName){
        DischargedEntry dischargedEntry = rr.getRoom(room).endCare(patientName, doctorName);
        discharged.add(dischargedEntry);

        return careResponseBuilder(room, dischargedEntry.getPatient(), dischargedEntry.getDoctor());
    }



    private static CarePatientResponse careResponseBuilder(int room, Patient patient, Doctor doctor){
        return CarePatientResponse.newBuilder()
                .setRoom(room)
                .setStatus(RoomUpdateStatus.ROOM_STATUS_OK)
                .setPatient(patient.toPatientInfo())
                .setDoctor(doctor.toDoctorInfo())
                .build();
    }

    private static CarePatientResponse careResponseErrorBuilder(int room, RoomUpdateStatus status){
        return CarePatientResponse.newBuilder()
                .setRoom(room)
                .setStatus(status)
                .build();
    }


    public List<Messages.PatientInfo> queryWaitingRoom(){
        if (patients.isEmpty()){
            throw new NoPatientsInWaitRoomException();
        }

        List<Messages.PatientInfo> info = new ArrayList<>();
        for (Patient p: patients){
            info.add(p.toPatientInfo());
        }
        return info;
    }


    public List<CaredInfo> queryCares(){
        if (discharged.isEmpty()){
            throw new NoDischargedPatientsException();
        }

        List<CaredInfo> info = new ArrayList<>();
        for (DischargedEntry entry : discharged){
            info.add(entry.toCaredInfo());
        }
        return info;
    }

    public List<CaredInfo> queryCares(int room){
        List<CaredInfo> info = new ArrayList<>();
        for (DischargedEntry entry : discharged){
            if (entry.getRoom() == room){
                info.add(entry.toCaredInfo());
            }
        }
        return info;
    }
}
