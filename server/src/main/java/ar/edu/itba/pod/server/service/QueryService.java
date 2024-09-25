package ar.edu.itba.pod.server.service;

import ar.edu.itba.pod.grpc.query.*;
import ar.edu.itba.pod.server.repository.RoomRepository;
import ar.edu.itba.pod.server.repository.WaitingRoomRepository;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;

public class QueryService extends QueryServiceGrpc.QueryServiceImplBase {

    private final RoomRepository rr;
    private final WaitingRoomRepository wr;

    public QueryService(RoomRepository rr, WaitingRoomRepository wr) {
        this.rr = rr;
        this.wr = wr;
    }

    @Override
    public void queryWaitingRoom(Empty request, StreamObserver<QueryWaitingRoomResponse> responseObserver) {
        QueryWaitingRoomResponse response = QueryWaitingRoomResponse.newBuilder()
                .addAllPatients(wr.queryWaitingRoom())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void queryCares(QueryCaresRequest request, StreamObserver<QueryCaresResponse> responseObserver) {
        QueryCaresResponse.Builder builder = QueryCaresResponse.newBuilder();

        if (request.hasRoom()){
             builder.addAllCares(wr.queryCares(request.getRoom()));
        } else {
            builder.addAllCares(wr.queryCares());
        }

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void queryRooms(Empty request, StreamObserver<QueryRoomsResponse> responseObserver) {
        QueryRoomsResponse response = QueryRoomsResponse.newBuilder()
                .addAllRooms(rr.queryRooms())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
