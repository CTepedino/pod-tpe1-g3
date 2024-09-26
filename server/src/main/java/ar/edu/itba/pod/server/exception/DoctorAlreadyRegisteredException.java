package ar.edu.itba.pod.server.exception;

public class DoctorAlreadyRegisteredException extends RuntimeException {
    private final String name;

    public DoctorAlreadyRegisteredException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return name + " already registered for notifications";
    }
}
