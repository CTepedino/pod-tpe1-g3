package ar.edu.itba.pod.server.repository;

import ar.edu.itba.pod.server.exception.RoomNotFoundException;
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


    public synchronized Room getRoom(int roomNumber){
        if (rooms.size() < roomNumber){
            throw new RoomNotFoundException(roomNumber);
        }
        return rooms.get(roomNumber-1);
    }

    public int getRoomCount(){
        return rooms.size();
    }
}
