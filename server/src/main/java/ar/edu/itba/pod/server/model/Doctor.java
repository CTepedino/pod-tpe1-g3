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

public class Doctor extends Person{

    private DoctorStatus status;

    private static final Logger logger = LoggerFactory.getLogger(DoctorRepository.class);



    public Doctor(String name, int maxLevel){
        super(name, maxLevel);
        status = DoctorStatus.DOCTOR_STATUS_UNAVAILABLE;
    }

    public synchronized void setStatus(DoctorStatus status){
        if (this.status == DoctorStatus.DOCTOR_STATUS_ATTENDING){
            throw new DoctorIsAttendingException(name);
        }
        this.status = status;
        logger.info("Doctor {} changed status to {}", name, status);
    }

    public void endCare(){
        status = DoctorStatus.DOCTOR_STATUS_AVAILABLE;
    }

    public synchronized DoctorStatus getStatus(){
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