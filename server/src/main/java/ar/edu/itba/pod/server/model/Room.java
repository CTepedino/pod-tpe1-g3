package ar.edu.itba.pod.server.model;

import ar.edu.itba.pod.grpc.emergencyCare.CarePatientResponse;
import ar.edu.itba.pod.grpc.emergencyCare.RoomUpdateStatus;
import ar.edu.itba.pod.grpc.query.RoomInfo;
import ar.edu.itba.pod.grpc.query.RoomInfoOrBuilder;
import ar.edu.itba.pod.grpc.query.RoomStatus;
import ar.edu.itba.pod.server.exception.InvalidPatientDoctorPairException;
import emergencyRoom.Messages;

public class Room {

    private final int number;
    private Patient patient;
    private Doctor doctor;

    public Room(int number){
        this.number = number;
    }

    public synchronized void startCare(Patient patient, Doctor doctor){
        this.patient = patient;
        this.doctor = doctor;
        doctor.setStatus(Messages.DoctorStatus.DOCTOR_STATUS_ATTENDING);
    }

    public synchronized DischargedEntry endCare(String patientName, String doctorName){
        DischargedEntry toReturn = new DischargedEntry(number, patient, doctor);
        if (!isAvailable() && patientName.equals(patient.getName()) && doctorName.equals(doctor.getName())){
            patient = null;
            doctor.endCare();
            doctor = null;
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
