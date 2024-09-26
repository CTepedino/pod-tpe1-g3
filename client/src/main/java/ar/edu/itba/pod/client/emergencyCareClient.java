package ar.edu.itba.pod.client;

import ar.edu.itba.pod.grpc.emergencyCare.*;
import com.google.protobuf.UInt32Value;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class emergencyCareClient {
    private static EmergencyRoomServiceGrpc.EmergencyRoomServiceBlockingStub blockingStub;

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

        ManagedChannel channel = ManagedChannelBuilder.forTarget(address).usePlaintext().build();
        blockingStub = EmergencyRoomServiceGrpc.newBlockingStub(channel);

        switch(action){
            case "carePatient":
                carePatient();
                break;
            case "careAllPatients":
                careAllPatients();
                break;
            case "dischargePatient":
                dischargePatient();
                break;
            default:
                System.out.println("Unrecognized action: " + action);
        }
    }

    private static void printResponse(CarePatientResponse response) {
        if(response.getStatus() == RoomUpdateStatus.ROOM_STATUS_OK){
            System.out.printf("Patient %s (%d) and Doctor %s (%d) are now in Room #%d\n", response.getPatient().getName(), response.getPatient().getLevel(), response.getDoctor().getName(), response.getDoctor().getMaxLevel(), response.getRoom());
        } else {
            System.out.printf("Room #%d remains %s\n", response.getRoom(), response.getStatus() == RoomUpdateStatus.ROOM_STATUS_STILL_FREE ? "Free" : "Occupied");
        }
    }

    private static void carePatient(){
        String roomString = System.getProperty("room");
        if(roomString == null){
            System.out.println("No room specified");
            return;
        }

        int room = Integer.parseInt(roomString);

        try {
            CarePatientResponse response = blockingStub.carePatient(UInt32Value.of(room));
            printResponse(response);
        } catch (StatusRuntimeException e){
            System.out.println(e.getMessage());
        }
    }

    private static void careAllPatients(){
        try {
            CareAllPatientsResponse responseList = blockingStub.careAllPatients(com.google.protobuf.Empty.newBuilder().build());
            for(CarePatientResponse response : responseList.getRoomsList()){
                printResponse(response);
            }
        } catch (StatusRuntimeException e){
            System.out.println(e.getMessage());
        }
    }

    private static void dischargePatient(){
        String roomString = System.getProperty("room");
        String doctorName = System.getProperty("doctor");
        String patientName = System.getProperty("patient");
        if(roomString == null){
            System.out.println("No room specified");
            return;
        }
        if(doctorName == null){
            System.out.println("No doctor specified");
            return;
        }
        if(patientName == null){
            System.out.println("No patient specified");
            return;
        }

        int room = Integer.parseInt(roomString);
        DischargePatientRequest request = DischargePatientRequest.newBuilder().setRoom(room).setDoctorName(doctorName).setPatientName(patientName).build();

        try {
            CarePatientResponse response = blockingStub.dischargePatient(request);
            System.out.printf("Patient %s (%d) has been discharged from Doctor %s (%d) and the Room #%d is now Free", response.getPatient().getName(), response.getPatient().getLevel(), response.getDoctor().getName(), response.getDoctor().getMaxLevel(), room);
        } catch (StatusRuntimeException e){
            System.out.println(e.getMessage());
        }
    }
}