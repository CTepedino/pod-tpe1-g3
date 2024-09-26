package ar.edu.itba.pod.server.repository;

import ar.edu.itba.pod.server.service.EmergencyCareService;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import emergencyRoom.Messages;
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
    public final void carePatient() throws InterruptedException, ExecutionException {
        waitingRoomRepository.addPatient("pablo", 4);
        waitingRoomRepository.addPatient("juana", 5);
        waitingRoomRepository.addPatient("lucas", 4);
        waitingRoomRepository.addPatient("cris", 4);
        waitingRoomRepository.addPatient("paula", 3);
        waitingRoomRepository.addPatient("manuel", 2);
        waitingRoomRepository.addPatient("mora", 5);
        waitingRoomRepository.addPatient("maria", 1);
        waitingRoomRepository.addPatient("carlos", 2);
        waitingRoomRepository.addPatient("mia", 4);


        doctorRepository.addDoctor("doctor1", 5);
        doctorRepository.setDoctorStatus("doctor1", Messages.DoctorStatus.DOCTOR_STATUS_AVAILABLE);
        doctorRepository.addDoctor("doctor2", 2);
        doctorRepository.setDoctorStatus("doctor2", Messages.DoctorStatus.DOCTOR_STATUS_AVAILABLE);
        doctorRepository.addDoctor("doctor3", 3);

        roomRepository.addRoom();
        roomRepository.addRoom();

        waitingRoomRepository.startCare(1);

        assertEquals(0, waitingRoomRepository.getPatientsAhead(waitingRoomRepository.findByName("mora")));

        waitingRoomRepository.startCare(2);

        assertEquals(0, waitingRoomRepository.getPatientsAhead(waitingRoomRepository.findByName("mora")));
        assertEquals(6, waitingRoomRepository.getPatientsAhead(waitingRoomRepository.findByName("carlos")));

        waitingRoomRepository.endCare(1, "juana", "doctor1");

        waitingRoomRepository.startCare(1);

        assertEquals(0, waitingRoomRepository.getPatientsAhead(waitingRoomRepository.findByName("pablo")));

    }

    @Test
    public final void careAllPatients() throws InterruptedException, ExecutionException {
        waitingRoomRepository.addPatient("pablo", 4);
        waitingRoomRepository.addPatient("juana", 5);
        waitingRoomRepository.addPatient("lucas", 4);
        waitingRoomRepository.addPatient("cris", 4);
        waitingRoomRepository.addPatient("paula", 3);
        waitingRoomRepository.addPatient("manuel", 2);
        waitingRoomRepository.addPatient("mora", 5);
        waitingRoomRepository.addPatient("maria", 1);
        waitingRoomRepository.addPatient("carlos", 2);
        waitingRoomRepository.addPatient("mia", 4);


        doctorRepository.addDoctor("doctor1", 5);
        doctorRepository.setDoctorStatus("doctor1", Messages.DoctorStatus.DOCTOR_STATUS_AVAILABLE);
        doctorRepository.addDoctor("doctor2", 2);
        doctorRepository.setDoctorStatus("doctor2", Messages.DoctorStatus.DOCTOR_STATUS_AVAILABLE);
        doctorRepository.addDoctor("doctor3", 3);

        roomRepository.addRoom();
        roomRepository.addRoom();

        waitingRoomRepository.startAllCare();

        assertEquals(0, waitingRoomRepository.getPatientsAhead(waitingRoomRepository.findByName("mora")));

        waitingRoomRepository.startCare(2);

        waitingRoomRepository.endCare(1, "juana", "doctor1");

        waitingRoomRepository.startAllCare();

        waitingRoomRepository.endCare(1, "mora", "doctor1");

        waitingRoomRepository.endCare(2, "manuel", "doctor2");
    }



}
