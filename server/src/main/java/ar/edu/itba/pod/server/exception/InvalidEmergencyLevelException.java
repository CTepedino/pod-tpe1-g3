package ar.edu.itba.pod.server.exception;

public class InvalidEmergencyLevelException extends RuntimeException {
    private final int maxLevel;

    public InvalidEmergencyLevelException(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    @Override
    public String getMessage() {
        return "Level must be between 1 and " + maxLevel;
    }
}
