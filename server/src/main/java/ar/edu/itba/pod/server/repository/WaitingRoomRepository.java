package ar.edu.itba.pod.server.repository;

import ar.edu.itba.pod.server.model.Doctor;
import ar.edu.itba.pod.server.model.Patient;
import ar.edu.itba.pod.server.model.Room;


import java.util.HashMap;
import java.util.Map;

public class WaitingRoomRepository {

    private final RoomRepository rr;
    private final DoctorRepository dr;

    private final Map<String, Patient> patients; //TODO: change for queue with priority levels

    public WaitingRoomRepository(RoomRepository rr, DoctorRepository dr){
        this.rr = rr;
        this.dr = dr;
        patients = new HashMap<>();
    }

    public void addPatient(String name, int level) throws IllegalArgumentException{

        Patient patient = new Patient(name, level);
        if (patients.putIfAbsent(name, patient) == null){
            throw new IllegalArgumentException("Patient "+ name +" already exists");
        }
    }

    public Patient getPatient(String name){
        return patients.get(name);
    }

    public int getPatientsAhead(String name){
        return 0;//TODO
    }

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

}
