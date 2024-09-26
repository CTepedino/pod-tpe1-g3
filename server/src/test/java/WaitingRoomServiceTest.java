import ar.edu.itba.pod.server.repository.DoctorRepository;
import ar.edu.itba.pod.server.repository.RoomRepository;
import ar.edu.itba.pod.server.repository.WaitingRoomRepository;
import ar.edu.itba.pod.server.service.EmergencyCareService;
import ar.edu.itba.pod.server.service.WaitingRoomService;
import org.junit.jupiter.api.BeforeEach;

public class WaitingRoomServiceTest {

    private WaitingRoomService service;

    @BeforeEach
    public final void before() {
        service = new WaitingRoomService(new WaitingRoomRepository(new RoomRepository(), new DoctorRepository()));
    }
}
