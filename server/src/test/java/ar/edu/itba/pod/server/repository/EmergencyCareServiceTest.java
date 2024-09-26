package ar.edu.itba.pod.server.repository;

import ar.edu.itba.pod.server.service.EmergencyCareService;
import org.junit.jupiter.api.BeforeEach;

public class EmergencyCareServiceTest {
    private EmergencyCareService service;

    @BeforeEach
    public final void before() {
        EventRepository er = new EventRepository();
        service = new EmergencyCareService(new WaitingRoomRepository(new RoomRepository(), new DoctorRepository(er), er));
    }

}
