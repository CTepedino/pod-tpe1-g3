package ar.edu.itba.pod.server.exception;

public class InvalidEmergencyLevelException extends RuntimeException {
    private final String levelFieldName;

    public InvalidEmergencyLevelException(String levelFieldName) {
        this.levelFieldName = levelFieldName;
    }

    @Override
    public String getMessage() {
        return levelFieldName + " must be between 1 and 5";
    }
}
