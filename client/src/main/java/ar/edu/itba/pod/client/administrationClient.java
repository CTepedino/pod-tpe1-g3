package ar.edu.itba.pod.client;

import ar.edu.itba.pod.grpc.administration.AdministrationServiceGrpc;
import ar.edu.itba.pod.grpc.administration.AvailabilityInfo;
import com.google.protobuf.StringValue;
import com.google.protobuf.UInt32Value;
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

        int level = Integer.parseInt(levelString);
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
        if(doctor == null){
            System.out.println("No doctor specified");
            return;
        }
        if(availability == null){
            System.out.println("No availability specified");
            return;
        }

        Messages.DoctorInfo doctorInfo = Messages.DoctorInfo.newBuilder().setName(doctor).build();
        AvailabilityInfo availabilityInfo = AvailabilityInfo.newBuilder().setAvailability(availability).setDoctor(doctorInfo).build();

        try {
            blockingStub.setDoctor(availabilityInfo);
            System.out.printf("Doctor %s is %s\n", doctor, availability); //TODO: Agregar el nivel del doctor. (Lo deberia retornar el server)
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
            AvailabilityInfo response = blockingStub.checkDoctor(StringValue.of(doctor));
            System.out.printf("Doctor %s (%d) is %s\n", doctor, response.getDoctor().getMaxLevel(), response.getAvailability());
        } catch (StatusRuntimeException e){
            System.out.println(e.getMessage());
        }
    }
}
