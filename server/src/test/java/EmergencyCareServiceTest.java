import ar.edu.itba.pod.server.repository.DoctorRepository;
import ar.edu.itba.pod.server.repository.RoomRepository;
import ar.edu.itba.pod.server.repository.WaitingRoomRepository;
import ar.edu.itba.pod.server.service.EmergencyCareService;
import org.junit.jupiter.api.BeforeEach;

public class EmergencyCareServiceTest {
    private EmergencyCareService service;

    @BeforeEach
    public final void before() {
        service = new EmergencyCareService(new WaitingRoomRepository(new RoomRepository(), new DoctorRepository()));
    }

}
