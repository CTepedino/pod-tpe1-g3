package ar.edu.itba.pod.server.exception;

public class NoDischargedPatientsException extends RuntimeException {

    private final int room;

    public NoDischargedPatientsException(){
        room = 0;
    }

    public NoDischargedPatientsException(int room){
        this.room = room;
    }

    @Override
    public String getMessage() {
        return "No patients were discharged" + (room==0?"":(" in room #" + room));
    }
}
