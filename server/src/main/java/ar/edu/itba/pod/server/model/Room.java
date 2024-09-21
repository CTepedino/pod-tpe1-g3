package ar.edu.itba.pod.server.model;

public class Room {
    private boolean available;

    public Room(){
        available = true;
    }

    public boolean isAvailable() {
        return available;
    }

    public void occupy(){
        available = false;
    }

    public void free(){
        available = true;
    }
}
