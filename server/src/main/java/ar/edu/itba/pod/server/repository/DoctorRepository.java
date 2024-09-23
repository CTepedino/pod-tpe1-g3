package ar.edu.itba.pod.server.repository;

import ar.edu.itba.pod.server.exception.DoctorAlreadyExistsException;
import ar.edu.itba.pod.server.exception.DoctorNotFoundException;
import ar.edu.itba.pod.server.model.Doctor;
import emergencyRoom.Messages.DoctorStatus;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DoctorRepository {

    private final Map<String, Doctor> doctors;

    public DoctorRepository(){
        doctors = new ConcurrentHashMap<>();
    }

    public void addDoctor(String name, int maxLevel){
        Doctor doctor = new Doctor(name, maxLevel);
        if(doctors.putIfAbsent(name, doctor) != null) {
            throw new DoctorAlreadyExistsException(name);
        }
    }

    public Doctor getDoctor(String name){
        Doctor doctor = doctors.get(name);
        if (doctor == null){
            throw new DoctorNotFoundException(name);
        }
        return doctor;
    }

    public Doctor setDoctorStatus(String name, DoctorStatus status){
        Doctor doctor = doctors.get(name);
        doctor.setStatus(status);
        return doctor;
    }

  /*  public Doctor getAvailableDoctor(int level){
        Doctor candidate = null;

        for (Doctor doctor : doctors.values()){
            if (doctor.getMaxLevel() >= level){
                if (candidate==null || candidate.getMaxLevel() > doctor.getMaxLevel() || candidate.getName().compareToIgnoreCase(doctor.getName()) > 0){
                    candidate = doctor;
                }
            }
        }

        return candidate;
    }*/

}
