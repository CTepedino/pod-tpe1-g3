package ar.edu.itba.pod.server.model;

import ar.edu.itba.pod.server.exception.InvalidEmergencyLevelException;

import java.util.Objects;

public abstract class Person {
    private static final int MAX_LEVEL = 5;

    protected final String name;
    protected int level;

    protected Person(String name, int level) {
        this.name = name;
        setLevel(level);
    }

    public void setLevel(int level) {
        if (level > MAX_LEVEL || level < 1){
            throw new InvalidEmergencyLevelException(MAX_LEVEL);
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
    public int hashCode() {
        return Objects.hashCode(name);
    }


    public static int getMaxLevel(){
        return MAX_LEVEL;
    }
}
