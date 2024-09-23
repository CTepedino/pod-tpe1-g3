package ar.edu.itba.pod.server.exception;

public class DoctorNotFoundException extends RuntimeException {
    private final String name;

    public DoctorNotFoundException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "No doctor of the name " + name;
    }
}
