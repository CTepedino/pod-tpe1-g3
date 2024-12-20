package ar.edu.itba.pod.server.repository;

import ar.edu.itba.pod.server.exception.DoctorAlreadyExistsException;
import ar.edu.itba.pod.server.exception.DoctorNotFoundException;
import ar.edu.itba.pod.server.model.Doctor;
import emergencyRoom.Messages.DoctorStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class DoctorRepository {

    private final EventRepository er;

    private final Map<String, Doctor> doctors;

    private static final Logger logger = LoggerFactory.getLogger(DoctorRepository.class);

    public DoctorRepository(EventRepository er){
        doctors = new ConcurrentHashMap<>();
        this.er = er;
    }

    public void addDoctor(String name, int maxLevel){
        Doctor doctor = new Doctor(name, maxLevel);
        if(doctors.putIfAbsent(name, doctor) != null) {
            throw new DoctorAlreadyExistsException(name);
        }
        logger.info("New Doctor {}", name);
    }

    public Doctor getDoctor(String name){
        Doctor doctor = doctors.get(name);
        if (doctor == null){
            throw new DoctorNotFoundException(name);
        }
        return doctor;
    }

    public Doctor setDoctorStatus(String name, DoctorStatus status){
        Doctor doctor = getDoctor(name);
        doctor.setStatus(status);
        er.notifyDisponibility(doctor);
        return doctor;
    }


    Doctor getDoctorForCare(int level){
        Doctor candidate = null;

        for (Doctor doctor : doctors.values()){
            doctor.getLock().lock();
            if (doctor.getLevel() >= level && doctor.getStatus().equals(DoctorStatus.DOCTOR_STATUS_AVAILABLE)) {
                if (candidate == null) {
                    candidate = doctor;
                } else if (doctor.getLevel() < candidate.getLevel()) {
                    candidate = doctor;
                } else if (doctor.getLevel() == candidate.getLevel() && doctor.getName().compareToIgnoreCase(candidate.getName()) < 0) {
                    candidate = doctor;
                }
            }
            if (!doctor.equals(candidate)){
                doctor.getLock().unlock();
            }
        }

        return candidate;
    }

}
