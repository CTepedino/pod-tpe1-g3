package ar.edu.itba.pod.server;

import ar.edu.itba.pod.server.repository.DoctorRepository;
import ar.edu.itba.pod.server.repository.RoomRepository;
import ar.edu.itba.pod.server.service.AdministrationService;
import io.grpc.ServerBuilder;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Server {
    private static Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws InterruptedException, IOException {
        logger.info("Emergency Room Server Starting ...");

        int port = 50051;

        DoctorRepository dr = new DoctorRepository();
        RoomRepository rr = new RoomRepository();

        io.grpc.Server server = ServerBuilder.forPort(port)
                .addService(new AdministrationService(dr, rr))
                .build();
        server.start();

        logger.info("Server started, listening on " + port);

        server.awaitTermination();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down gRPC server since JVM is shutting down");
            server.shutdown();
            logger.info("Server shut down");
        }));
    }}
