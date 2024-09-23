package ar.edu.itba.pod.server.repository;

import ar.edu.itba.pod.server.model.Doctor;
import emergencyRoom.Messages.DoctorStatus;


import java.util.HashMap;
import java.util.Map;

public class DoctorRepository {

    private final Map<String, Doctor> doctors;

    public DoctorRepository(){
        doctors = new HashMap<>();
    }

    public void addDoctor(String name, int maxLevel) throws IllegalArgumentException{

        Doctor doctor = new Doctor(name, maxLevel);
        if(doctors.putIfAbsent(name, doctor) == null) {
            throw new IllegalArgumentException("Doctor " + name + " already exists");
        }
    }

    public Doctor getDoctor(String name){
        return doctors.get(name);
    }

    public Doctor setDoctorStatus(String name, DoctorStatus status){
        Doctor doctor = doctors.get(name);
        doctor.setStatus(status);
        return doctor;
    }

    public Doctor getAvailableDoctor(int level){
        Doctor candidate = null;

        for (Doctor doctor : doctors.values()){
            if (doctor.getMaxLevel() >= level){
                if (candidate==null || candidate.getMaxLevel() > doctor.getMaxLevel() || candidate.getName().compareToIgnoreCase(doctor.getName()) > 0){
                    candidate = doctor;
                }
            }
        }

        return candidate;
    }

}
