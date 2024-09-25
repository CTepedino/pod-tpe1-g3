package ar.edu.itba.pod.server.service;

import ar.edu.itba.pod.grpc.waitingRoom.CheckPatientResponse;
import ar.edu.itba.pod.grpc.waitingRoom.WaitingRoomServiceGrpc;
import ar.edu.itba.pod.server.model.Patient;
import ar.edu.itba.pod.server.repository.WaitingRoomRepository;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import emergencyRoom.Messages.PatientInfo;
import io.grpc.stub.StreamObserver;

public class WaitingRoomService extends WaitingRoomServiceGrpc.WaitingRoomServiceImplBase {

    private final WaitingRoomRepository wr;

    public WaitingRoomService(WaitingRoomRepository wr){
        this.wr = wr;
    }

    @Override
    public void addPatient(PatientInfo request, StreamObserver<Empty> responseObserver) {
        wr.addPatient(request.getName(), request.getLevel());

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateLevel(PatientInfo request, StreamObserver<Empty> responseObserver) {
        wr.updateLevel(request.getName(), request.getLevel());

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void checkPatient(StringValue request, StreamObserver<CheckPatientResponse> responseObserver) {
        Patient patient = wr.findByName(request.getValue());
        int ahead = wr.getPatientsAhead(patient);
        CheckPatientResponse response = CheckPatientResponse.newBuilder()
                .setPatient(
                        PatientInfo.newBuilder()
                                .setName(patient.getName())
                                .setLevel(patient.getLevel())
                                .build()
                )
                .setWaitTime(ahead)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
