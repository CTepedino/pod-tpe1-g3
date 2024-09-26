package ar.edu.itba.pod.server.model;


import emergencyRoom.Messages;

public class Patient extends Person implements Comparable<Person>{

    public Patient(String name, int level){
        super(name, level);
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
    public int compareTo(Person o) {
        if (level == o.level){
            return name.compareTo(o.name);
        }
        return level > o.level? 1: -1;
    }
}
