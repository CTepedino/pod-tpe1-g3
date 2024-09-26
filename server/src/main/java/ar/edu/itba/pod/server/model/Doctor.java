package ar.edu.itba.pod.server.model;

import ar.edu.itba.pod.grpc.administration.DoctorStatusResponse;
import ar.edu.itba.pod.server.exception.DoctorIsAttendingException;
import ar.edu.itba.pod.server.exception.InvalidEmergencyLevelException;
import ar.edu.itba.pod.server.repository.DoctorRepository;
import emergencyRoom.Messages;
import emergencyRoom.Messages.DoctorStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public class Doctor extends Person{

    private DoctorStatus status;
    private final ReentrantLock lock;

    private static final Logger logger = LoggerFactory.getLogger(DoctorRepository.class);

    public Doctor(String name, int maxLevel){
        super(name, maxLevel);
        status = DoctorStatus.DOCTOR_STATUS_UNAVAILABLE;
        lock = new ReentrantLock();
    }

    public void setStatus(DoctorStatus status){
        lock.lock();
        if (this.status == DoctorStatus.DOCTOR_STATUS_ATTENDING){
            throw new DoctorIsAttendingException(name);
        }
        this.status = status;
        logger.info("Doctor {} changed status to {}", name, status);
        lock.unlock();
    }

    public void endCare(){
        lock.lock();
        status = DoctorStatus.DOCTOR_STATUS_AVAILABLE;
        lock.unlock();
    }

    public ReentrantLock getLock(){
        return lock;
    }

    public DoctorStatus getStatus(){
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Doctor doctor)) return false;
        return name.equals(doctor.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    public Messages.DoctorInfo toDoctorInfo(){
        return Messages.DoctorInfo.newBuilder()
                .setName(name)
                .setMaxLevel(level)
                .build();
    }

    public DoctorStatusResponse toDoctorStatusResponse(){
        return DoctorStatusResponse.newBuilder()
                .setStatus(status)
                .setDoctor(toDoctorInfo())
                .build();
    }
}