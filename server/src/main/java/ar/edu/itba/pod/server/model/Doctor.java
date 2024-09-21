package ar.edu.itba.pod.server.model;

import java.util.Objects;

public class Doctor {

    private boolean available;
    private final String name;
    private final int maxLevel;


    public Doctor(String name, int maxLevel) throws IllegalArgumentException{
        this.name = name;
        if (maxLevel > 5 || maxLevel < 1){
            throw new IllegalArgumentException("maxLevel must be between 1 and 5");
        }
        this.maxLevel = maxLevel;
        available = false;
    }

    public void makeAvailable(){
        available = true;
    }

    public void makeUnavailable(){
        available = false;
    }

    public boolean isAvailable(){
        return available;
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
