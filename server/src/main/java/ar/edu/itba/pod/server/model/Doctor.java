package ar.edu.itba.pod.server.model;

import ar.edu.itba.pod.server.exception.DoctorIsAttendingException;
import ar.edu.itba.pod.server.exception.InvalidEmergencyLevelException;
import emergencyRoom.Messages.DoctorStatus;

import java.util.Objects;

public class Doctor {

    private DoctorStatus status;
    private final String name;
    private final int maxLevel;


    public Doctor(String name, int maxLevel){
        this.name = name;
        if (maxLevel > 5 || maxLevel < 1){
            throw new InvalidEmergencyLevelException("maxLevel");
        }
        this.maxLevel = maxLevel;
        this.status = DoctorStatus.DOCTOR_STATUS_UNAVAILABLE;
    }

    public synchronized void setStatus(DoctorStatus status){
        this.status = status;
    }

    public synchronized DoctorStatus getStatus(){
        if (status == DoctorStatus.DOCTOR_STATUS_ATTENDING){
            throw new DoctorIsAttendingException(name);
        }
        return status;
    }

    public String getName(){
        return name;
    }

    public int getMaxLevel(){
        return maxLevel;
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
}
