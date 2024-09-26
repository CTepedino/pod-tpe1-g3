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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

public class WaitingRoomRepository {

    private final ConcurrentSkipListSet<Patient>[] patientLevels;

    private final List<DischargedEntry> discharged;

    private static final Logger logger = LoggerFactory.getLogger(DoctorRepository.class);

    private final RoomRepository rr;
    private final DoctorRepository dr;
    private final EventRepository er;

    public WaitingRoomRepository(RoomRepository rr, DoctorRepository dr, EventRepository er){
        this.rr = rr;
        this.dr = dr;
        this.er = er;

        patientLevels = new ConcurrentSkipListSet[Patient.getMaxLevel()];
        for (int i = 0; i < patientLevels.length; i++){
            patientLevels[i] = new ConcurrentSkipListSet<Patient>();
        }
        discharged = new ArrayList<>();
    }

    public synchronized void addPatient(String name, int level){
        Patient patient = new Patient(name, level);

        for (DischargedEntry entry : discharged){
            if (entry.getPatient().equals(patient)){
                throw new PatientAlreadyExistsException(name);
            }
        }

        if (rr.hasPatient(patient)){
            throw new PatientAlreadyExistsException(name);
        }

        for (ConcurrentSkipListSet<Patient> patients : patientLevels) {
            if (patients.contains(patient)) {
                throw new PatientAlreadyExistsException(name);
            }
        }

        patientLevels[level-1].add(patient);
        logger.info("New patient {}", name);
    }

    public Patient findByName(String name){
        for (ConcurrentSkipListSet<Patient> patients : patientLevels){
            for (Patient p : patients){
                if (p.getName().equals(name)){
                    return p;
                }
            }
        }

        throw new PatientNotFoundException(name);
    }

    public synchronized void updateLevel(String name, int level){
        Patient patient = findByName(name);
        int oldLevel = patient.getLevel();
        patient.setLevel(level);
        patientLevels[oldLevel-1].remove(patient);
        patientLevels[level-1].add(patient);
    }

    public int getPatientsAhead(Patient patient){
        int ahead = 0;
        for (int i = patientLevels.length -1; i >= patient.getLevel() -1; i--){
            for (Patient p : patientLevels[i]){
                if (p.equals(patient)){
                    return ahead;
                } else {
                    ahead++;
                }
            }
        }

        return ahead;
    }

    public synchronized CarePatientResponse startCare(int roomNumber){
        Room room = rr.getRoom(roomNumber);

        if (!room.isAvailable()){
            return careResponseErrorBuilder(roomNumber, RoomUpdateStatus.ROOM_STATUS_WAS_OCCUPIED);
        }

        Doctor doctor = null;
        Patient patient = null;
        int maxLevel = Patient.getMaxLevel();

        while (doctor == null) {
            if (maxLevel == 0){
                return careResponseErrorBuilder(roomNumber, RoomUpdateStatus.ROOM_STATUS_STILL_FREE);
            }
            patient = getPatientForCare(maxLevel);
            if (patient == null){
                return careResponseErrorBuilder(roomNumber, RoomUpdateStatus.ROOM_STATUS_STILL_FREE);
            }
            doctor = dr.getDoctorForCare(patient.getLevel());
            maxLevel = patient.getLevel() -1;
        }

        room.startCare(patient, doctor);
        doctor.getLock().unlock();
        patientLevels[patient.getLevel()-1].remove(patient);

        er.notifyCareStart(doctor, patient, roomNumber);
        return careResponseBuilder(roomNumber, patient, doctor);
    }

    private Patient getPatientForCare(int maxLevel){
        for (int i = maxLevel -1 ; i >=0 ; i--){
            if (!patientLevels[i].isEmpty()){
                return patientLevels[i].first();
            }
        }
        return null;
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

        er.notifyCareEnd(dischargedEntry.getDoctor(), dischargedEntry.getPatient(), room);
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


    public synchronized List<Messages.PatientInfo> queryWaitingRoom(){

        if (getWaitingPatientsCount() == 0){
            throw new NoPatientsInWaitRoomException();
        }

        List<Messages.PatientInfo> info = new ArrayList<>();
        for (int i = patientLevels.length -1; i >=0 ; i--){
            for (Patient p : patientLevels[i]){
                info.add(p.toPatientInfo());
            }
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

    private int getWaitingPatientsCount(){
        int count = 0;
        for (ConcurrentSkipListSet<Patient> patients : patientLevels){
            count += patients.size();
        }
        return count;
    }
}
