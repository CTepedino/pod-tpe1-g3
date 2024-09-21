package ar.edu.itba.pod.server.repository;

import ar.edu.itba.pod.server.model.Doctor;

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

}
