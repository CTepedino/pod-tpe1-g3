package ar.edu.itba.pod.server.repository;

import ar.edu.itba.pod.grpc.query.RoomInfo;
import ar.edu.itba.pod.server.exception.NoRoomsException;
import ar.edu.itba.pod.server.exception.RoomNotFoundException;
import ar.edu.itba.pod.server.model.Patient;
import ar.edu.itba.pod.server.model.Room;

import java.util.ArrayList;
import java.util.List;


public class RoomRepository {
    private final List<Room> rooms;

    public RoomRepository(){
        rooms = new ArrayList<>();
    }

    public synchronized int addRoom(){
        Room room = new Room(rooms.size()+1);
        rooms.add(room);
        return room.getNumber();
    }


    public synchronized Room getRoom(int roomNumber){
        if (roomNumber < 1 || rooms.size() < roomNumber){
            throw new RoomNotFoundException(roomNumber);
        }
        return rooms.get(roomNumber-1);
    }

    int getRoomCount(){
        return rooms.size();
    }

    public List<RoomInfo> queryRooms(){
        if (rooms.isEmpty()){
            throw new NoRoomsException();
        }

        List<RoomInfo> info = new ArrayList<>();
        for (Room room : rooms){
            info.add(room.toRoomInfo());
        }
        return info;
    }


    boolean hasPatient(Patient patient){
        if (patient == null){
            return false;
        }
        for (Room r: rooms){
            synchronized (r) {
                if (patient.equals(r.getPatient())) {
                    return true;
                }
            }
        }
        return false;
    }
}
