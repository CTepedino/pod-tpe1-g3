package ar.edu.itba.pod.server.repository;

import ar.edu.itba.pod.server.exception.ImpossibleToStartCareException;
import ar.edu.itba.pod.server.exception.NoPatientsException;
import ar.edu.itba.pod.server.exception.PatientAlreadyExistsException;
import ar.edu.itba.pod.server.exception.PatientNotFoundException;
import ar.edu.itba.pod.server.model.Doctor;
import ar.edu.itba.pod.server.model.Patient;
import ar.edu.itba.pod.server.model.Room;


import java.util.*;

public class WaitingRoomRepository {

    private static final int EMERGENCY_LEVELS = 5;

    private final List<Patient> patients;
    private final List<String> discharged;

    private final RoomRepository rr;
    private final DoctorRepository dr;

    public WaitingRoomRepository(RoomRepository rr, DoctorRepository dr){
        this.rr = rr;
        this.dr = dr;
        patients = new ArrayList<>(); //Tener muchas queues se complica si cambia el nivel de un paciente, asi que creo que lo mejor es iterar por una sola lista ordenada por llegada y ver el nivel de c/u
        discharged = new ArrayList<>();
    }

    public synchronized void addPatient(String name, int level){
        Patient patient = new Patient(name, level);
        for (Patient p : patients){
            if (p.equals(patient)){
                throw new PatientAlreadyExistsException(name);
            }
        }
        for (String oldPatient : discharged){
            if (oldPatient.equals(name)){
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

    public synchronized void startCare(int roomNumber){
        Room room = rr.getRoom(roomNumber);

        if (!room.isAvailable()){
            //throw new ...
        }

        if (patients.isEmpty()){
            throw new NoPatientsException();
        }

        Doctor doctor = null;
        Patient patient = null;
        int maxLevel = Patient.getMaxLevel();

        while (doctor == null) {
            if (maxLevel == 0){
                throw new ImpossibleToStartCareException(roomNumber);
            }
            patient = getPatientForCare(maxLevel);
            doctor = dr.getDoctorForCare(patient.getLevel());
            maxLevel = patient.getLevel() -1;
        }

        room.startCare(patient, doctor);
        patients.remove(patient);
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

    public synchronized void startAllCare(){
        for (int room = 1; room <= rr.getRoomCount(); room++){
            startCare(room);
        }
    }

    public synchronized void endCare(int room, String patient, String doctor){
        rr.getRoom(room).endCare(patient, doctor);
        discharged.add(patient);
    }
}
