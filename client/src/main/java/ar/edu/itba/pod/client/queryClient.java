package ar.edu.itba.pod.client;

import ar.edu.itba.pod.grpc.emergencyCare.EmergencyRoomServiceGrpc;
import ar.edu.itba.pod.grpc.query.*;
import emergencyRoom.Messages;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class queryClient {
    private static QueryServiceGrpc.QueryServiceBlockingStub blockingStub;
    private static Path outPath;

    public static void main(String[] args){
        String address = System.getProperty("serverAddress");
        if (address == null){
            System.out.println("No address specified");
            return;
        }

        String action = System.getProperty("action");
        if (action == null){
            System.out.println("No action specified");
            return;
        }

        String pathString = System.getProperty("outPath");
        if (pathString == null){
            System.out.println("No output path specified");
            return;
        }

        try {
            outPath = Paths.get(pathString);
        } catch (InvalidPathException e) {
            System.out.println("The provided path \"" + pathString + "\" is invalid. Please check the path format and try again.");
            return;
        }

        ManagedChannel channel = null;
        try {
            channel = ManagedChannelBuilder.forTarget(address).usePlaintext().build();
            blockingStub = QueryServiceGrpc.newBlockingStub(channel);
        } catch (Exception e) {
            System.out.println("Failed to connect to the server at " + address + ". Please check the server address and try again.");
            return;
        }

        switch(action){
            case "queryRooms":
                queryRooms();
                break;
            case "queryWaitingRoom":
                queryWaitingRoom();
                break;
            case "queryCares":
                queryCares();
                break;
            default:
                System.out.println("Unrecognized action: " + action);
        }
    }

    private static void queryRooms(){
        try {
            QueryRoomsResponse response = blockingStub.queryRooms(com.google.protobuf.Empty.newBuilder().build());
            List<String> csvData = new ArrayList<>();
            csvData.add("Room,Status,Patient,Doctor");
            for(RoomInfo room : response.getRoomsList()){
                if(room.getStatus() == RoomStatus.ROOM_STATUS_FREE){
                    csvData.add(String.format("%d,Free,,", room.getRoom()));
                } else if (room.getStatus() == RoomStatus.ROOM_STATUS_OCCUPIED) {
                    csvData.add(String.format("%d,Occupied,%s (%d),%s (%d)", room.getRoom(), room.getPatient().getName(), room.getPatient().getLevel(), room.getDoctor().getName(), room.getDoctor().getMaxLevel()));
                }
            }
            Files.write(outPath, csvData);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private static void queryWaitingRoom(){
        try {
            QueryWaitingRoomResponse response = blockingStub.queryWaitingRoom(com.google.protobuf.Empty.newBuilder().build());
            List<String> csvData = new ArrayList<>();
            csvData.add("Patient,Level");
            for(Messages.PatientInfo patient : response.getPatientsList()){
                csvData.add(String.format("%s,%d", patient.getName(), patient.getLevel()));
            }
            Files.write(outPath, csvData);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private static void queryCares(){
        String roomString = System.getProperty("room");
        QueryCaresRequest request;
        if(roomString == null){
            request = QueryCaresRequest.newBuilder().build();
        } else {
            int room;
            try {
                room = Integer.parseInt(roomString);
            } catch (NumberFormatException e) {
                System.out.println("Invalid room number. Please provide a valid integer for the room number.");
                return;
            }
            request = QueryCaresRequest.newBuilder().setRoom(room).build();
        }
        try {
            QueryCaresResponse response = blockingStub.queryCares(request);
            List<String> csvData = new ArrayList<>();
            csvData.add("Room,Patient,Doctor");
            for(CaredInfo row : response.getCaresList()){
                csvData.add(String.format("%d,%s (%d),%s (%d)", row.getRoom(), row.getPatient().getName(), row.getPatient().getLevel(), row.getDoctor().getName(), row.getDoctor().getMaxLevel()));
            }
            Files.write(outPath, csvData);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
