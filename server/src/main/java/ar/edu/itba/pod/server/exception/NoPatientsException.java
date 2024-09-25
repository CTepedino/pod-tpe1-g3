package ar.edu.itba.pod.server.exception;

public class NoPatientsException extends RuntimeException {
    @Override
    public String getMessage() {
        return "There are no patients in the waiting room";
    }
}
