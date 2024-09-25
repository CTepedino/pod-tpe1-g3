package ar.edu.itba.pod.server.model;


public class Patient extends Person {

    public Patient(String name, int level){
        super(name, level);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient patient)) return false;
        return name.equals(patient.name);
    }

}
