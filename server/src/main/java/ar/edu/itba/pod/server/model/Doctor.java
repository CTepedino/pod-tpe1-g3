package ar.edu.itba.pod.server.model;

import ar.edu.itba.pod.server.exception.DoctorIsAttendingException;
import ar.edu.itba.pod.server.exception.InvalidEmergencyLevelException;
import emergencyRoom.Messages.DoctorStatus;

import java.util.Objects;

public class Doctor extends Person{

    private DoctorStatus status;


    public Doctor(String name, int maxLevel){
        super(name, maxLevel);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Doctor doctor)) return false;
        return name.equals(doctor.name);
    }
}
