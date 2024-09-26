package ar.edu.itba.pod.server.exception;

public class DoctorDidNotRegisterException extends RuntimeException {
    private final String name;

    public DoctorDidNotRegisterException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return name + " did not register for notifications";
    }
}
