package ar.edu.itba.pod.server.repository;

import ar.edu.itba.pod.server.service.WaitingRoomService;
import org.junit.jupiter.api.BeforeEach;

public class WaitingRoomServiceTest {

    private WaitingRoomService service;

    @BeforeEach
    public final void before() {
        EventRepository er = new EventRepository();
        service = new WaitingRoomService(new WaitingRoomRepository(new RoomRepository(), new DoctorRepository(er), er));
    }
}
