package ar.edu.itba.pod.server.model;

import java.util.Objects;

public class Patient {
    private final String name;
    private int level;

    public Patient(String name, int level) throws IllegalArgumentException{
        this.name = name;
        setLevel(level);
    }

    public void setLevel(int level) {
        if (level > 5 || level < 1){
            throw new IllegalArgumentException("level must be between 1 and 5");
        }
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient patient)) return false;
        return name.equals(patient.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
