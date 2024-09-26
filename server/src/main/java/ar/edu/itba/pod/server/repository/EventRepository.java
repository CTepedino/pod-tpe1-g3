package ar.edu.itba.pod.server.repository;

import ar.edu.itba.pod.grpc.doctorPager.DoctorEvent;
import ar.edu.itba.pod.grpc.doctorPager.Event;
import ar.edu.itba.pod.server.exception.DoctorAlreadyRegisteredException;
import ar.edu.itba.pod.server.exception.DoctorDidNotRegisterException;
import ar.edu.itba.pod.server.model.Doctor;
import ar.edu.itba.pod.server.model.Patient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class EventRepository {

    private final Map<Doctor, BlockingQueue<DoctorEvent>> events;

    public EventRepository(){
        events = new HashMap<>();
    }

    public BlockingQueue<DoctorEvent> subscribe(Doctor doctor){
        BlockingQueue<DoctorEvent> queue = new LinkedBlockingQueue<>();
        synchronized (this) {
            if (events.putIfAbsent(doctor, queue) != null) {
                throw new DoctorAlreadyRegisteredException(doctor.getName());
            }
        }
        queue.add(DoctorEvent.newBuilder()
                .setDoctor(doctor.toDoctorInfo())
                .setEvent(Event.EVENT_REGISTER)
                .build());
        return queue;
    }

    public synchronized void unsubscribe(Doctor doctor){
        if (!events.containsKey(doctor)) {
            throw new DoctorDidNotRegisterException(doctor.getName());
        }
        events.get(doctor).add(DoctorEvent.newBuilder()
                .setEvent(Event.EVENT_UNREGISTER)
                .setDoctor(doctor.toDoctorInfo())
                .build());
        events.remove(doctor);
    }


    private synchronized void emitEvent(Doctor doctor, DoctorEvent event){
        BlockingQueue<DoctorEvent> queue = events.get(doctor);
        if (queue != null){
            try {
                queue.put(event);
            } catch (InterruptedException e){
                throw new DoctorDidNotRegisterException(doctor.getName());
            }
        }
    }

    void notifyDisponibility(Doctor doctor){
        emitEvent(doctor, DoctorEvent.newBuilder()
                .setDoctor(doctor.toDoctorInfo())
                .setEvent(Event.EVENT_DISPONIBILITY)
                .setStatus(doctor.getStatus())
                .build()
        );
    }

    private void notifyCare(Doctor doctor, Patient patient, int room, Event event){
        emitEvent(doctor, DoctorEvent.newBuilder()
                .setDoctor(doctor.toDoctorInfo())
                .setEvent(event)
                .setPatient(patient.toPatientInfo())
                .setRoom(room)
                .build()
        );
    }

    void notifyCareStart(Doctor doctor, Patient patient, int room){
        notifyCare(doctor, patient, room, Event.EVENT_START_CARE);
    }

    void notifyCareEnd(Doctor doctor, Patient patient, int room){
        notifyCare(doctor, patient, room, Event.EVENT_END_CARE);
    }
}
