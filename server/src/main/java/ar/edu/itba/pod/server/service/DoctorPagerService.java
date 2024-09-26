package ar.edu.itba.pod.server.service;

import ar.edu.itba.pod.grpc.doctorPager.DoctorEvent;
import ar.edu.itba.pod.grpc.doctorPager.DoctorPagerServiceGrpc;
import ar.edu.itba.pod.grpc.doctorPager.Event;
import ar.edu.itba.pod.server.model.Doctor;
import ar.edu.itba.pod.server.repository.DoctorRepository;
import ar.edu.itba.pod.server.repository.EventRepository;
import com.google.protobuf.StringValue;
import emergencyRoom.Messages;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.BlockingQueue;

public class DoctorPagerService extends DoctorPagerServiceGrpc.DoctorPagerServiceImplBase {

    private final EventRepository er;
    private final DoctorRepository dr;

    public DoctorPagerService(EventRepository er, DoctorRepository dr){
        this.er = er;
        this.dr = dr;
    }

    @Override
    public void unregister(StringValue request, StreamObserver<Messages.DoctorInfo> responseObserver) {
        Doctor doctor = dr.getDoctor(request.getValue());
        er.unsubscribe(doctor);

        responseObserver.onNext(doctor.toDoctorInfo());
        responseObserver.onCompleted();
    }

    @Override
    public void register(StringValue request, StreamObserver<DoctorEvent> responseObserver) {
        Doctor doctor = dr.getDoctor(request.getValue());

        BlockingQueue<DoctorEvent> queue = er.subscribe(doctor);
        while(true){
            try {
                DoctorEvent event = queue.take();
                responseObserver.onNext(event);
                if (event.getEvent().equals(Event.EVENT_UNREGISTER)){
                    break;
                }
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
        responseObserver.onCompleted();
    }

}
