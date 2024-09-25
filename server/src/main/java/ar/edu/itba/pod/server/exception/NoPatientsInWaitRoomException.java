package ar.edu.itba.pod.server.exception;

public class NoPatientsInWaitRoomException extends RuntimeException {
    @Override
    public String getMessage() {
        return "No patients are in the waiting room";
    }

}
