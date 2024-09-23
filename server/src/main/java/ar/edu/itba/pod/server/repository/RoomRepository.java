package ar.edu.itba.pod.server.repository;

import ar.edu.itba.pod.server.model.Room;

import java.util.ArrayList;
import java.util.List;


public class RoomRepository {
    private final List<Room> rooms;

    public RoomRepository(){
        rooms = new ArrayList<>();
    }

    public synchronized int addRoom(){
        rooms.add(new Room());
        return rooms.size();
    }

/*
    public Room getRoom(int roomNumber){
        return rooms.get(roomNumber-1);
    }

    public Room getAvailableRoom(){
        for (Room room : rooms){
            if (room.isAvailable()){
                return room;
            }
        }
        return null;
    }

    public void freeRoom(int roomNumber){
        getRoom(roomNumber).free();
    }*/
}
