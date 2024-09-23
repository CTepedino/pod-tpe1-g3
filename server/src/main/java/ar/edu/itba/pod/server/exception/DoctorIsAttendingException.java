package ar.edu.itba.pod.server.exception;

public class DoctorIsAttendingException extends RuntimeException {
    private final String name;

    public DoctorIsAttendingException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Doctor " + name + " is already attending";
    }
}
