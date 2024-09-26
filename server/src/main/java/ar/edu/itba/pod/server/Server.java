package ar.edu.itba.pod.server;

import ar.edu.itba.pod.server.repository.DoctorRepository;
import ar.edu.itba.pod.server.repository.EventRepository;
import ar.edu.itba.pod.server.repository.RoomRepository;
import ar.edu.itba.pod.server.repository.WaitingRoomRepository;
import ar.edu.itba.pod.server.service.*;
import io.grpc.BindableService;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import io.grpc.ServerServiceDefinition;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Function;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    private static final Function<BindableService, ServerServiceDefinition> exceptionHandler =
            service -> ServerInterceptors.intercept(service, new GlobalExceptionHandlerInterceptor());

    public static void main(String[] args) throws InterruptedException, IOException {
        logger.info("Emergency Room Server Starting ...");

        String portStr = System.getProperty("serverPort");
        int port;
        try {
            port = portStr != null ? Integer.parseInt(portStr) : 50051;
        } catch (NumberFormatException e){
            logger.error("Invalid port: {}", portStr);
            return;
        }

        EventRepository er = new EventRepository();
        DoctorRepository dr = new DoctorRepository(er);
        RoomRepository rr = new RoomRepository();
        WaitingRoomRepository wr = new WaitingRoomRepository(rr, dr, er);

        io.grpc.Server server = ServerBuilder.forPort(port)
                .addService(exceptionHandler.apply(new AdministrationService(dr, rr)))
                .addService(exceptionHandler.apply(new WaitingRoomService(wr)))
                .addService(exceptionHandler.apply(new EmergencyCareService(wr)))
                .addService(exceptionHandler.apply(new DoctorPagerService(er, dr)))
                .addService(exceptionHandler.apply(new QueryService(rr, wr)))
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
