package ar.edu.itba.pod.server.exception;

public class InvalidPatientDoctorPairException extends RuntimeException {
    @Override
    public String getMessage() {
        return "The doctor or patient indicated were not in the room";
    }
}
