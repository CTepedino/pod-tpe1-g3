package ar.edu.itba.pod.server.repository;

import ar.edu.itba.pod.server.Server;
import ar.edu.itba.pod.server.exception.DoctorAlreadyExistsException;
import ar.edu.itba.pod.server.exception.DoctorIsAttendingException;
import ar.edu.itba.pod.server.exception.DoctorNotFoundException;
import ar.edu.itba.pod.server.model.Doctor;
import emergencyRoom.Messages.DoctorStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class DoctorRepository {

    private static final Logger logger = LoggerFactory.getLogger(DoctorRepository.class);


    private final EventRepository er;

    private final Map<String, Doctor> doctors;

    public DoctorRepository(EventRepository er){
        doctors = new ConcurrentHashMap<>();
        this.er = er;
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
        synchronized(doctor) {
            if (doctor.getStatus() == DoctorStatus.DOCTOR_STATUS_UNAVAILABLE && status == DoctorStatus.DOCTOR_STATUS_ATTENDING) {
                throw new DoctorIsAttendingException(name);
            }
            doctor.setStatus(status);
            logger.info("Doctor {} changed status to {}", name, status);
        }

        er.notifyDisponibility(doctor);
        return doctor;
    }

    public Doctor setDoctorStatusAvailability(String name, DoctorStatus status){
        Doctor doctor = doctors.get(name);
        synchronized(doctor) {
            if (doctor.getStatus() == DoctorStatus.DOCTOR_STATUS_ATTENDING) {
                throw new DoctorIsAttendingException(name);
            }
            doctor.setStatus(status);
            logger.info("Doctor {} changed status to {}", name, status);
        }
        er.notifyDisponibility(doctor);
        return doctor;
    }

    Doctor getDoctorForCare(int level){
        Doctor candidate = null;

        for (Doctor doctor : doctors.values()){
            if (doctor.getLevel() >= level && doctor.getStatus().equals(DoctorStatus.DOCTOR_STATUS_AVAILABLE)){
                if (candidate == null){
                    candidate = doctor;
                } else if (doctor.getLevel() < candidate.getLevel()){
                    candidate = doctor;
                } else if (doctor.getLevel() == candidate.getLevel() && doctor.getName().compareToIgnoreCase(candidate.getName()) < 0){
                    candidate = doctor;
                }
            }
        }

        return candidate;
    }

}
