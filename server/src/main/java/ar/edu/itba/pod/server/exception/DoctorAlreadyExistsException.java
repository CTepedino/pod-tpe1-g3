package ar.edu.itba.pod.server.exception;

public class DoctorAlreadyExistsException extends RuntimeException {
    private final String name;

    public DoctorAlreadyExistsException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "A doctor of the name " + name + " already exists";
    }
}
