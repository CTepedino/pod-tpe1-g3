package ar.edu.itba.pod.server.model;


import emergencyRoom.Messages;

import java.time.LocalDateTime;

public class Patient extends Person implements Comparable<Patient>{

    private final LocalDateTime arrivalTime;

    public Patient(String name, int level){
        super(name, level);
        arrivalTime = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient patient)) return false;
        return name.equals(patient.name);
    }

    public Messages.PatientInfo toPatientInfo(){
        return Messages.PatientInfo.newBuilder()
                .setName(name)
                .setLevel(level)
                .build();
    }

    @Override
    public int compareTo(Patient o) {
        if (level == o.level){
            return arrivalTime.compareTo(o.arrivalTime);
        }
        return level > o.level? 1: -1;
    }
}
