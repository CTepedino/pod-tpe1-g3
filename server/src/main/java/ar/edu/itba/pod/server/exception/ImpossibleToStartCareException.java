package ar.edu.itba.pod.server.exception;

public class ImpossibleToStartCareException extends RuntimeException {

    private final int room;

    public ImpossibleToStartCareException(int room) {
        this.room = room;
    }

    @Override
    public String getMessage() {
        return "Impossible to start care in room #" + room;
    }
}
