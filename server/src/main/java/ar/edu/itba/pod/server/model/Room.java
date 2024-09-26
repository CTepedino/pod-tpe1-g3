package ar.edu.itba.pod.server.model;

import ar.edu.itba.pod.grpc.emergencyCare.CarePatientResponse;
import ar.edu.itba.pod.grpc.emergencyCare.RoomUpdateStatus;
import ar.edu.itba.pod.grpc.query.RoomInfo;
import ar.edu.itba.pod.grpc.query.RoomInfoOrBuilder;
import ar.edu.itba.pod.grpc.query.RoomStatus;
import ar.edu.itba.pod.server.exception.InvalidPatientDoctorPairException;
import ar.edu.itba.pod.server.repository.DoctorRepository;
import ar.edu.itba.pod.server.repository.WaitingRoomRepository;
import emergencyRoom.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Room {

    private final int number;
    private Patient patient;
    private Doctor doctor;

    private static final Logger logger = LoggerFactory.getLogger(WaitingRoomRepository.class);

    public Room(int number){
        this.number = number;
    }

    public synchronized void startCare(Patient patient, Doctor doctor){
        this.patient = patient;
        this.doctor = doctor;
        doctor.setStatus(Messages.DoctorStatus.DOCTOR_STATUS_ATTENDING);
        logger.info("Doctor {} attending {} in room {}", doctor.getName(), patient.getName(), number);
    }

    public synchronized DischargedEntry endCare(String patientName, String doctorName){
        DischargedEntry toReturn = new DischargedEntry(number, patient, doctor);
        if (!isAvailable() && patientName.equals(patient.getName()) && doctorName.equals(doctor.getName())){
            patient = null;
            doctor.endCare();
            doctor = null;
            logger.info("Doctor {} finished with {} in room {}", doctorName, patientName, number);
        } else {
            throw new InvalidPatientDoctorPairException();
        }
        return toReturn;
    }

    public synchronized boolean isAvailable() {
        return patient == null && doctor == null;
    }

    public Patient getPatient() {
        return patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public int getNumber() {
        return number;
    }

    public RoomInfo toRoomInfo(){
        RoomInfo.Builder builder = RoomInfo.newBuilder()
                .setRoom(number);
        if (isAvailable()){
            return builder
                    .setStatus(RoomStatus.ROOM_STATUS_FREE)
                    .build();
        } else {
            return builder
                    .setStatus(RoomStatus.ROOM_STATUS_OCCUPIED)
                    .setPatient(patient.toPatientInfo())
                    .setDoctor(doctor.toDoctorInfo())
                    .build();
        }
    }


}
