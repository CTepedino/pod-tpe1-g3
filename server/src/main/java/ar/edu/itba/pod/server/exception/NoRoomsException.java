package ar.edu.itba.pod.server.exception;

public class NoRoomsException extends RuntimeException {
    @Override
    public String getMessage() {
        return "No rooms were added";
    }
}
