package ar.edu.itba.pod.server.repository;

import ar.edu.itba.pod.server.service.EmergencyCareService;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmergencyCareServiceTest {
    private EmergencyCareService service;

    private WaitingRoomRepository waitingRoomRepository;

    private DoctorRepository doctorRepository;

    private RoomRepository roomRepository;

    private EventRepository eventRepository;

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
    public final void PatientsAhead() throws InterruptedException, ExecutionException {
        waitingRoomRepository.addPatient("pablo", 4);
        waitingRoomRepository.addPatient("juana", 5);
        waitingRoomRepository.addPatient("luca", 4);
        waitingRoomRepository.addPatient("cristian", 4);
        waitingRoomRepository.addPatient("paula", 3);
        waitingRoomRepository.addPatient("manuel", 2);
        waitingRoomRepository.addPatient("mora", 5);
        waitingRoomRepository.addPatient("maria", 1);
        waitingRoomRepository.addPatient("carlos", 2);
        waitingRoomRepository.addPatient("mia", 4);


        doctorRepository.addDoctor("doctor1", 5);
        doctorRepository.addDoctor("doctor2", 2);
        doctorRepository.addDoctor("doctor3", 3);

        roomRepository.addRoom();
        roomRepository.addRoom();

    }



}
