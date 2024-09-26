package ar.edu.itba.pod.client;

import ar.edu.itba.pod.grpc.administration.AdministrationServiceGrpc;
import ar.edu.itba.pod.grpc.administration.DoctorStatusRequest;
import ar.edu.itba.pod.grpc.administration.DoctorStatusResponse;
import com.google.protobuf.StringValue;
import com.google.protobuf.UInt32Value;
import emergencyRoom.Messages;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class administrationClient {
    private static AdministrationServiceGrpc.AdministrationServiceBlockingStub blockingStub;

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
        blockingStub = AdministrationServiceGrpc.newBlockingStub(channel);

        switch(action){
            case "addRoom":
                addRoom();
                break;
            case "addDoctor":
                addDoctor();
                break;
            case "setDoctor":
                setDoctor();
                break;
            case "checkDoctor":
                checkDoctor();
                break;
            default:
                System.out.println("Unrecognized action: " + action);
        }
    }

    private static void addRoom(){
        try {
            UInt32Value roomNumber = blockingStub.addRoom(com.google.protobuf.Empty.newBuilder().build());
            System.out.printf("Room #%d added successfully\n", roomNumber.getValue());
        } catch (StatusRuntimeException e){
            System.out.println(e.getMessage());
        }
    }

    private static void addDoctor(){
        String doctor = System.getProperty("doctor");
        String levelString = System.getProperty("level");
        if(doctor == null){
            System.out.println("No doctor specified");
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
        Messages.DoctorInfo doctorInfo = Messages.DoctorInfo.newBuilder().setName(doctor).setMaxLevel(level).build();

        try {
            blockingStub.addDoctor(doctorInfo);
            System.out.printf("Doctor %s (%d) added successfully\n", doctor, level);
        } catch (StatusRuntimeException e){
            System.out.println(e.getMessage());
        }
    }

    private static void setDoctor(){
        String doctor = System.getProperty("doctor");
        String availability = System.getProperty("availability");
        Messages.DoctorStatus status;
        if(doctor == null){
            System.out.println("No doctor specified");
            return;
        }
        if(availability == null){
            System.out.println("No availability specified");
            return;
        }

        switch (availability){
            case "available":
                status = Messages.DoctorStatus.DOCTOR_STATUS_AVAILABLE;
                break;
            case "unavailable":
                status = Messages.DoctorStatus.DOCTOR_STATUS_UNAVAILABLE;
                break;
            default:
                System.out.println("Unrecognized availability: " + availability);
                return;
        }

        DoctorStatusRequest request = DoctorStatusRequest.newBuilder().setName(doctor).setStatus(status).build();

        try {
            DoctorStatusResponse response = blockingStub.setDoctor(request);
            System.out.printf("Doctor %s (%d) is %s\n", doctor, response.getDoctor().getMaxLevel(), status == Messages.DoctorStatus.DOCTOR_STATUS_AVAILABLE ? "Available" : "Unavailable");
        } catch (StatusRuntimeException e){
            System.out.println(e.getMessage());
        }
    }

    private static void checkDoctor(){
        String doctor = System.getProperty("doctor");
        if(doctor == null){
            System.out.println("No doctor specified");
            return;
        }

        try {
            DoctorStatusResponse response = blockingStub.checkDoctor(StringValue.of(doctor));
            String availability = switch (response.getStatus()) {
                case DOCTOR_STATUS_AVAILABLE -> "Available";
                case DOCTOR_STATUS_UNAVAILABLE -> "Unavailable";
                case DOCTOR_STATUS_ATTENDING -> "Attending";
                default -> "Unspecified";
            };
            System.out.printf("Doctor %s (%d) is %s\n", doctor, response.getDoctor().getMaxLevel(), availability);
        } catch (StatusRuntimeException e){
            System.out.println(e.getMessage());
        }
    }
}
