package ar.edu.itba.pod.server.service;

import ar.edu.itba.pod.grpc.administration.AdministrationServiceGrpc;
import ar.edu.itba.pod.grpc.administration.DoctorStatusRequest;
import ar.edu.itba.pod.grpc.administration.DoctorStatusResponse;
import ar.edu.itba.pod.server.model.Doctor;
import ar.edu.itba.pod.server.repository.DoctorRepository;
import ar.edu.itba.pod.server.repository.RoomRepository;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import com.google.protobuf.UInt32Value;
import emergencyRoom.Messages;
import io.grpc.stub.StreamObserver;

public class AdministrationService extends AdministrationServiceGrpc.AdministrationServiceImplBase {

    private final DoctorRepository dr;
    private final RoomRepository rr;

    public AdministrationService(DoctorRepository dr, RoomRepository rr){
        this.dr = dr;
        this.rr = rr;
    }

    @Override
    public void addRoom(Empty request, StreamObserver<UInt32Value> responseObserver) {
        int room = rr.addRoom();
        UInt32Value response = UInt32Value.newBuilder().setValue(room).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void addDoctor(Messages.DoctorInfo request, StreamObserver<Empty> responseObserver) {
        dr.addDoctor(request.getName(), request.getMaxLevel());

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }


    @Override
    public void setDoctor(DoctorStatusRequest request, StreamObserver<DoctorStatusResponse> responseObserver) {
        Doctor doctor = dr.setDoctorStatus(request.getName(), request.getStatus());

        responseObserver.onNext(doctor.toDoctorStatusResponse());
        responseObserver.onCompleted();
    }

    @Override
    public void checkDoctor(StringValue request, StreamObserver<DoctorStatusResponse> responseObserver) {
        Doctor doctor = dr.getDoctor(request.getValue());

        responseObserver.onNext(doctor.toDoctorStatusResponse());
        responseObserver.onCompleted();
    }


}
