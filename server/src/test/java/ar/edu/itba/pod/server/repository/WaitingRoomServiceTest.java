package ar.edu.itba.pod.server.repository;

import ar.edu.itba.pod.server.exception.DoctorAlreadyExistsException;
import ar.edu.itba.pod.server.exception.PatientAlreadyExistsException;
import ar.edu.itba.pod.server.service.WaitingRoomService;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WaitingRoomServiceTest {

    private static final int THREAD_COUNT = 10;

    private static final String NAME = "name";

    private WaitingRoomRepository waitingRoomRepository;

    private DoctorRepository doctorRepository;

    private RoomRepository roomRepository;

    private EventRepository eventRepository;

    private static final AtomicInteger repeatedPatientExceptions = new AtomicInteger(0);

    private final Runnable newPatient = () -> {
        try {
            waitingRoomRepository.addPatient(NAME, 4);
        } catch (PatientAlreadyExistsException e) {
            repeatedPatientExceptions.incrementAndGet();
        }
    };

    @BeforeEach
    public final void before() {
        // Configurar el nivel de logging solo para las pruebas
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger("ar.edu.itba.pod.server.repository.DoctorRepository").setLevel(Level.DEBUG);

        doctorRepository = new DoctorRepository(new EventRepository());
        roomRepository = new RoomRepository();
        eventRepository = new EventRepository();
        waitingRoomRepository = new WaitingRoomRepository(roomRepository, doctorRepository, eventRepository);
    }

    @AfterEach
    public final void after() {
        // Restaurar el nivel de logging a INFO después de las pruebas
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger("ar.edu.itba.pod.server.repository.DoctorRepository").setLevel(Level.INFO);
    }

    @Test
    public final void repeatedPatient() throws InterruptedException, ExecutionException {
        ExecutorService pool = Executors.newCachedThreadPool();
        for(int i=0; i<THREAD_COUNT; i++){
            pool.submit(newPatient);
        }

        pool.shutdown();
        boolean response = pool.awaitTermination(2, TimeUnit.SECONDS);
        assertTrue(response);
        assertEquals(THREAD_COUNT-1, repeatedPatientExceptions.get());

    }

    @Test
    public final void PatientsAhead() throws InterruptedException, ExecutionException {
        waitingRoomRepository.addPatient(NAME, 4);
        waitingRoomRepository.addPatient("juana", 5);
        waitingRoomRepository.addPatient("luca", 4);
        waitingRoomRepository.addPatient("cristian", 4);
        waitingRoomRepository.addPatient("paula", 3);
        waitingRoomRepository.addPatient("manuel", 2);
        waitingRoomRepository.addPatient("mora", 5);
        waitingRoomRepository.addPatient("maria", 1);
        waitingRoomRepository.addPatient("carlos", 2);
        waitingRoomRepository.addPatient("mia", 4);
        assertEquals(0, waitingRoomRepository.getPatientsAhead(waitingRoomRepository.findByName("juana")));
        assertEquals(1, waitingRoomRepository.getPatientsAhead(waitingRoomRepository.findByName("mora")));
        assertEquals(3, waitingRoomRepository.getPatientsAhead(waitingRoomRepository.findByName("luca")));
        assertEquals(5, waitingRoomRepository.getPatientsAhead(waitingRoomRepository.findByName("mia")));
        assertEquals(9, waitingRoomRepository.getPatientsAhead(waitingRoomRepository.findByName("maria")));

    }
}
