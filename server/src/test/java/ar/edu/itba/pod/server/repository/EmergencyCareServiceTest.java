package ar.edu.itba.pod.server.repository;

import ar.edu.itba.pod.server.Server;
import ar.edu.itba.pod.server.exception.DoctorAlreadyExistsException;
import ar.edu.itba.pod.server.exception.DoctorIsAttendingException;
import ar.edu.itba.pod.server.exception.InvalidPatientDoctorPairException;
import ar.edu.itba.pod.server.exception.PatientAlreadyExistsException;
import ar.edu.itba.pod.server.service.EmergencyCareService;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import emergencyRoom.Messages;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmergencyCareServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(EmergencyCareServiceTest.class);

    private static final int THREAD_COUNT = 10;

    private EmergencyCareService service;

    private WaitingRoomRepository waitingRoomRepository;

    private DoctorRepository doctorRepository;

    private RoomRepository roomRepository;

    private EventRepository eventRepository;

    private final Runnable test = () -> {
        try{
            waitingRoomRepository.addPatient("pablo", 4);
        }catch(PatientAlreadyExistsException e){
            logger.info(e.getMessage());
        }
        try{
            waitingRoomRepository.addPatient("juana", 5);
        }catch(PatientAlreadyExistsException e){
            logger.info(e.getMessage());
        }
        try{
            waitingRoomRepository.addPatient("luca", 2);
        }catch(PatientAlreadyExistsException e){
            logger.info(e.getMessage());
        }

        try{
            doctorRepository.addDoctor("doctor1", 5);
        }catch(DoctorAlreadyExistsException e){
            logger.info(e.getMessage());
        }
        try{
            doctorRepository.addDoctor("doctor2", 5);
        }catch(DoctorAlreadyExistsException e){
            logger.info(e.getMessage());
        }

        try{
            doctorRepository.setDoctorStatus("doctor1", Messages.DoctorStatus.DOCTOR_STATUS_AVAILABLE);
        }catch(DoctorIsAttendingException e){
            logger.info(e.getMessage());
        }
        try{
            doctorRepository.setDoctorStatus("doctor2", Messages.DoctorStatus.DOCTOR_STATUS_AVAILABLE);
        }catch(DoctorIsAttendingException e){
            logger.info(e.getMessage());
        }

        roomRepository.addRoom();
        roomRepository.addRoom();

        waitingRoomRepository.startAllCare();


        try{
            waitingRoomRepository.endCare(1, "juana", "doctor1");
        }catch (InvalidPatientDoctorPairException e){
            logger.info(e.getMessage());
        }
        waitingRoomRepository.startAllCare();

        try{
            waitingRoomRepository.endCare(2, "manuel", "doctor2");
        }catch (InvalidPatientDoctorPairException e){
            logger.info(e.getMessage());
        }

        try{
            waitingRoomRepository.endCare(1, "mora", "doctor1");
        }catch (InvalidPatientDoctorPairException e){
            logger.info(e.getMessage());
        }

    };

    private final Runnable test2 = () -> {
        try{
            waitingRoomRepository.addPatient("cris", 3);
        }catch(PatientAlreadyExistsException e){
            logger.info(e.getMessage());
        }

        try{
            doctorRepository.addDoctor("doctor3", 4);
        }catch(DoctorAlreadyExistsException e){
            logger.info(e.getMessage());
        }

        waitingRoomRepository.startAllCare();

        try{
            doctorRepository.setDoctorStatus("doctor3", Messages.DoctorStatus.DOCTOR_STATUS_AVAILABLE);
        }catch(DoctorIsAttendingException e){
            logger.info(e.getMessage());
        }

        waitingRoomRepository.startAllCare();

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


    @Test
    public final void multipleThreadTest() throws InterruptedException, ExecutionException{

        ExecutorService pool = Executors.newCachedThreadPool();
        pool.submit(test);
        pool.submit(test);
        pool.submit(test2);
        pool.shutdown();
        boolean response = pool.awaitTermination(50000, TimeUnit.SECONDS);
        assertTrue(response);
    }





}
