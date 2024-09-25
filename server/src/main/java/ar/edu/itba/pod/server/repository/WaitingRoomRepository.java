package ar.edu.itba.pod.server.repository;

import ar.edu.itba.pod.server.exception.PatientAlreadyExistsException;
import ar.edu.itba.pod.server.exception.PatientNotFoundException;
import ar.edu.itba.pod.server.model.Doctor;
import ar.edu.itba.pod.server.model.Patient;
import ar.edu.itba.pod.server.model.Room;
import org.checkerframework.checker.units.qual.C;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WaitingRoomRepository {

    private static final int EMERGENCY_LEVELS = 5;

    private final List<Patient> patients;


    public WaitingRoomRepository(){
        patients = new ArrayList<>(); //Tener muchas queues se complica si cambia el nivel de un paciente, asi que creo que lo mejor es iterar por una sola lista ordenada por llegada y ver el nivel de c/u
    }

    public synchronized void addPatient(String name, int level){
        Patient patient = new Patient(name, level);
        for (Patient p : patients){
            if (p.equals(patient)){
                throw new PatientAlreadyExistsException(name); //TODO: falta ver que no este siendo atendido o que ya haya sido atendido
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

/*


    public void startAttention(int roomNumber){//TODO: tomar en orden correcto los pacientes y removerlos de la lista de espera
        Room room = rr.getRoom(roomNumber);
        Patient patient = patients.values().iterator().next();
        Doctor doctor = dr.getAvailableDoctor(patient.getLevel());
        room.occupy(patient, doctor);
    }

    public void startAttentionForAll(){//TODO: tomar en orden correcto los pacientes y removerlos de la lista de espera
        for(Patient patient : patients.values()){
            Room room = rr.getAvailableRoom();
            if (room == null){
                return;
            }
            Doctor doctor = dr.getAvailableDoctor(patient.getLevel());
            room.occupy(patient, doctor);
        }
    }
*/

}
