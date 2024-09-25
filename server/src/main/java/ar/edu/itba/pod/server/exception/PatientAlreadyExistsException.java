package ar.edu.itba.pod.server.exception;

public class PatientAlreadyExistsException extends RuntimeException {
    private final String name;

    public PatientAlreadyExistsException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "A patient of the name " + name + " was registered before";
    }
}
