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

    }

    @Override
    public void queryCares(QueryCaresRequest request, StreamObserver<QueryCaresResponse> responseObserver) {

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
