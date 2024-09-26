package ar.edu.itba.pod.client;

import ar.edu.itba.pod.grpc.administration.AdministrationServiceGrpc;
import ar.edu.itba.pod.grpc.waitingRoom.CheckPatientResponse;
import ar.edu.itba.pod.grpc.waitingRoom.WaitingRoomServiceGrpc;
import com.google.protobuf.StringValue;
import emergencyRoom.Messages;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class waitingRoomClient {
    private static WaitingRoomServiceGrpc.WaitingRoomServiceBlockingStub blockingStub;

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

        ManagedChannel channel = null;
        try {
            channel = ManagedChannelBuilder.forTarget(address).usePlaintext().build();
            blockingStub = WaitingRoomServiceGrpc.newBlockingStub(channel);
        } catch (Exception e) {
            System.out.println("Failed to connect to the server at " + address + ". Please check the server address and try again.");
            return;
        }

        switch(action){
            case "addPatient":
                addPatient();
                break;
            case "updateLevel":
                updateLevel();
                break;
            case "checkPatient":
                checkPatient();
                break;
            default:
                System.out.println("Unrecognized action: " + action);
        }
    }

    private static void addPatient(){
        String patientName = System.getProperty("patient");
        String levelString = System.getProperty("level");
        if(patientName == null){
            System.out.println("No patient specified");
            return;
        }
        if(levelString == null){
            System.out.println("No level specified");
            return;
        }

        int level;
        try {
            level = Integer.parseInt(levelString);
        } catch (NumberFormatException e) {
            System.out.println("Invalid level. Please provide a valid integer for the level.");
            return;
        }
        Messages.PatientInfo patient = Messages.PatientInfo.newBuilder().setName(patientName).setLevel(level).build();

        try {
            blockingStub.addPatient(patient);
            System.out.printf("Patient %s (%d) is in the waiting room\n", patientName, level);
        } catch (StatusRuntimeException e){
            System.out.println(e.getMessage());
        }
    }

    private static void updateLevel(){
        String patientName = System.getProperty("patient");
        String levelString = System.getProperty("level");
        if(patientName == null){
            System.out.println("No patient specified");
            return;
        }
        if(levelString == null){
            System.out.println("No level specified");
            return;
        }

        int level;
        try {
            level = Integer.parseInt(levelString);
        } catch (NumberFormatException e) {
            System.out.println("Invalid level. Please provide a valid integer for the level.");
            return;
        }
        Messages.PatientInfo patient = Messages.PatientInfo.newBuilder().setName(patientName).setLevel(level).build();

        try {
            blockingStub.updateLevel(patient);
            System.out.printf("Patient %s (%d) is in the waiting room\n", patientName, level);
        } catch (StatusRuntimeException e){
            System.out.println(e.getMessage());
        }
    }

    private static void checkPatient(){
        String patientName = System.getProperty("patient");
        if(patientName == null){
            System.out.println("No patient specified");
            return;
        }

        try {
            CheckPatientResponse response = blockingStub.checkPatient(StringValue.of(patientName));
            System.out.printf("Patient %s (%d) is in the waiting room with %d patients ahead\n", patientName, response.getPatient().getLevel(), response.getWaitTime());
        } catch (StatusRuntimeException e){
            System.out.println(e.getMessage());
        }
    }
}
