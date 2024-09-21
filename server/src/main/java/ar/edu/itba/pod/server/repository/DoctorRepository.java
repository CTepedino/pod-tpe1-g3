package ar.edu.itba.pod.server.repository;

import ar.edu.itba.pod.server.model.Doctor;

import java.util.HashSet;
import java.util.Set;

public class DoctorRepository {

    private final Set<Doctor> doctors;

    public DoctorRepository(){
        doctors = new HashSet<>();
    }

    public void addDoctor(String name, int maxLevel) throws IllegalArgumentException{

        Doctor doctor = new Doctor(name, maxLevel);
        if (!doctors.add(doctor)){
            throw new IllegalArgumentException("Doctor " + name + " already exists");
        }
    }
}
