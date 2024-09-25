package ar.edu.itba.pod.server.exception;

public class PatientNotFoundException extends RuntimeException {
    private final String name;

    public PatientNotFoundException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "No patient of the name " + name;
    }
}
