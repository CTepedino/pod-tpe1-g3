package ar.edu.itba.pod.server.exception;

public class RoomNotFoundException extends RuntimeException {

    private final int room;

    public RoomNotFoundException(int room) {
        this.room = room;
    }

    @Override
    public String getMessage() {
        return "Room #" + room + " does not exist";
    }
}
