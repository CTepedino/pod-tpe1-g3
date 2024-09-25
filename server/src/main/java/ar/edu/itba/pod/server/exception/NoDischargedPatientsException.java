package ar.edu.itba.pod.server.exception;

public class NoDischargedPatientsException extends RuntimeException {
    @Override
    public String getMessage() {
        return "No patients were discharged";
    }
}
