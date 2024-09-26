package ar.edu.itba.pod.client;

import ar.edu.itba.pod.grpc.doctorPager.DoctorEvent;
import ar.edu.itba.pod.grpc.doctorPager.DoctorPagerServiceGrpc;
import ar.edu.itba.pod.grpc.doctorPager.Event;
import com.google.protobuf.StringValue;
import emergencyRoom.Messages;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;

public class doctorPagerClient {
    private static DoctorPagerServiceGrpc.DoctorPagerServiceBlockingStub blockingStub;
    private static DoctorPagerServiceGrpc.DoctorPagerServiceStub stub;

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
        stub = DoctorPagerServiceGrpc.newStub(channel);
        blockingStub = DoctorPagerServiceGrpc.newBlockingStub(channel);

        switch(action){
            case "register":
                register();
                break;
            case "unregister":
                unregister();
                break;
            default:
                System.out.println("Unrecognized action: " + action);
        }
    }

    private static void register(){
        String doctorName = System.getProperty("doctor");
        if (doctorName == null){
            System.out.println("No doctor specified");
            return;
        }

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<DoctorEvent> observer = new StreamObserver<DoctorEvent>() {
            @Override
            public void onNext(DoctorEvent event) {
                if(event.getEvent() == Event.EVENT_REGISTER){
                    System.out.printf("Doctor %s (%d) registered successfully for pager\n", event.getDoctor().getName(), event.getDoctor().getMaxLevel());
                }
                if(event.getEvent() == Event.EVENT_DISPONIBILITY){
                    String availability = switch (event.getStatus()) {
                        case DOCTOR_STATUS_AVAILABLE -> "Available";
                        case DOCTOR_STATUS_UNAVAILABLE -> "Unavailable";
                        case DOCTOR_STATUS_ATTENDING -> "Attending";
                        default -> "Unspecified";
                    };
                    System.out.printf("Doctor %s (%d) is %s\n", event.getDoctor().getName(), event.getDoctor().getMaxLevel(), availability);
                }
                if(event.getEvent() == Event.EVENT_START_CARE){
                    System.out.printf("Patient %s (%d) and Doctor %s (%d) are now in Room #%d\n", event.getPatient().getName(), event.getPatient().getLevel(), event.getDoctor().getName(), event.getDoctor().getMaxLevel(), event.getRoom());
                }
                if(event.getEvent() == Event.EVENT_END_CARE){
                    System.out.printf("Patient %s (%d) has been discharged from Doctor %s (%d) and the Room #%d is now Free\n", event.getPatient().getName(), event.getPatient().getLevel(), event.getDoctor().getName(), event.getDoctor().getMaxLevel(), event.getRoom());
                }
                if(event.getEvent() == Event.EVENT_UNREGISTER){
                    System.out.printf("Doctor %s (%d) unregistered successfully for pager\n", event.getDoctor().getName(), event.getDoctor().getMaxLevel());
                }
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(t.getMessage());
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        };

        stub.register(StringValue.of(doctorName), observer);
        try {
            latch.await();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void unregister(){
        String doctorName = System.getProperty("doctor");
        if (doctorName == null){
            System.out.println("No doctor specified");
            return;
        }

        try {
            Messages.DoctorInfo response = blockingStub.unregister(StringValue.of(doctorName));
            System.out.printf("Doctor %s (%d) unregistered successfully for pager\n", response.getName(), response.getMaxLevel());
        } catch (StatusRuntimeException e){
            System.out.println(e.getMessage());
        }
    }
}