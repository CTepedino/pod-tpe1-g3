package ar.edu.itba.pod.server.repository;

import ar.edu.itba.pod.server.exception.DoctorAlreadyExistsException;
import ar.edu.itba.pod.server.exception.DoctorIsAttendingException;
import ch.qos.logback.classic.LoggerContext;
import emergencyRoom.Messages.DoctorStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdministrationServiceTest {

    private static final int THREAD_COUNT = 10;

    private static final String NAME = "name";

    private static final AtomicInteger repeatedDoctorExceptions = new AtomicInteger(0);

    private DoctorRepository doctorRepository;

    private RoomRepository roomRepository;

    @BeforeEach
    public final void before() {
        // Configurar el nivel de logging solo para las pruebas
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger("ar.edu.itba.pod.server.repository.DoctorRepository").setLevel(Level.DEBUG);

        doctorRepository = new DoctorRepository(new EventRepository());
        roomRepository = new RoomRepository();
    }

    @AfterEach
    public final void after() {
        // Restaurar el nivel de logging a INFO después de las pruebas
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger("ar.edu.itba.pod.server.repository.DoctorRepository").setLevel(Level.INFO);
    }

    private final Runnable newDoctor = () -> {
        try {
            doctorRepository.addDoctor(NAME, 4);
        } catch (DoctorAlreadyExistsException e) {
            repeatedDoctorExceptions.incrementAndGet();
        }
    };

    private final Runnable setDoctorAttending = () -> {
        try{
            doctorRepository.setDoctorStatus(NAME, DoctorStatus.DOCTOR_STATUS_ATTENDING);
        } catch (DoctorIsAttendingException ignored){}
    };

    private final Runnable setDoctorAvailableAfterAttending = () -> {
        try{
            doctorRepository.setDoctorStatus(NAME, DoctorStatus.DOCTOR_STATUS_AVAILABLE);
        } catch (DoctorIsAttendingException ignored){}
    };

    private final Runnable setDoctorAvailable = () -> {
        try{
            doctorRepository.setDoctorStatusAvailability(NAME, DoctorStatus.DOCTOR_STATUS_AVAILABLE);
        } catch (DoctorIsAttendingException ignored){}
    };

    private final Runnable setDoctorUnavailable = () -> {
        try{
            doctorRepository.setDoctorStatusAvailability(NAME, DoctorStatus.DOCTOR_STATUS_UNAVAILABLE);
        } catch (DoctorIsAttendingException ignored){}
    };


    @Test
    public final void repeatedDoctor() throws InterruptedException, ExecutionException{
        ExecutorService pool = Executors.newCachedThreadPool();
        for(int i=0; i<THREAD_COUNT; i++){
            pool.submit(newDoctor);
        }

        pool.shutdown();
        boolean response = pool.awaitTermination(2, TimeUnit.SECONDS);
        assertTrue(response);
        assertEquals(THREAD_COUNT-1, repeatedDoctorExceptions.get());

    }

    @Test
    public final void doctorAvailability() throws InterruptedException, ExecutionException{
        doctorRepository.addDoctor(NAME, 4);

        ExecutorService pool = Executors.newCachedThreadPool();

        for(int i=0; i<THREAD_COUNT; i++){
            if(i%4 == 0){
                pool.submit(setDoctorUnavailable);
            }
            else if(i%4 == 1){
                pool.submit(setDoctorAttending);
            }
            else if(i%4 == 2){
                pool.submit(setDoctorAvailableAfterAttending);
            }
            else{
                pool.submit(setDoctorAvailable);
            }
        }

        pool.shutdown();
        boolean response = pool.awaitTermination(50000, TimeUnit.SECONDS);
        assertTrue(response);
    }





}
