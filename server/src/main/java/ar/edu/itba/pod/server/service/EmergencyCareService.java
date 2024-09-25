package ar.edu.itba.pod.server.service;

import ar.edu.itba.pod.grpc.emergencyCare.CareAllPatientsResponse;
import ar.edu.itba.pod.grpc.emergencyCare.CarePatientResponse;
import ar.edu.itba.pod.grpc.emergencyCare.DischargePatientRequest;
import ar.edu.itba.pod.grpc.emergencyCare.EmergencyRoomServiceGrpc;
import ar.edu.itba.pod.server.repository.WaitingRoomRepository;
import com.google.protobuf.Empty;
import com.google.protobuf.UInt32Value;
import io.grpc.stub.StreamObserver;

public class EmergencyCareService extends EmergencyRoomServiceGrpc.EmergencyRoomServiceImplBase {

    private final WaitingRoomRepository wr;

    public EmergencyCareService(WaitingRoomRepository wr){
        this.wr = wr;
    }

    @Override
    public void carePatient(UInt32Value request, StreamObserver<CarePatientResponse> responseObserver) {
        CarePatientResponse response = wr.startCare(request.getValue());

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void careAllPatients(Empty request, StreamObserver<CareAllPatientsResponse> responseObserver) {
        CareAllPatientsResponse response = CareAllPatientsResponse.newBuilder()
                .addAllRooms(wr.startAllCare())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void dischargePatient(DischargePatientRequest request, StreamObserver<CarePatientResponse> responseObserver) {
        CarePatientResponse response = wr.endCare(request.getRoom(), request.getPatientName(), request.getDoctorName());

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
