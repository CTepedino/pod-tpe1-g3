package ar.edu.itba.pod.server.repository;

import ar.edu.itba.pod.server.service.AdministrationService;
import org.junit.jupiter.api.BeforeEach;

public class AdministrationServiceTest {

    private AdministrationService service;

    @BeforeEach
    public final void before() {
        service = new AdministrationService(new DoctorRepository(new EventRepository()), new RoomRepository());
    }



}
