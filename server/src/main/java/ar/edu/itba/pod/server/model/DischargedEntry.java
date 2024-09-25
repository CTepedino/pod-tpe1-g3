package ar.edu.itba.pod.server.model;

import ar.edu.itba.pod.grpc.query.CaredInfo;

public class DischargedEntry {
    private final int room;
    private final Patient patient;
    private final Doctor doctor;

    public DischargedEntry(int room, Patient patient, Doctor doctor) {
        this.room = room;
        this.patient = patient;
        this.doctor = doctor;
    }

    public int getRoom() {
        return room;
    }

    public Patient getPatient() {
        return patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public CaredInfo toCaredInfo(){
        return CaredInfo.newBuilder()
                .setRoom(room)
                .setDoctor(doctor.toDoctorInfo())
                .setPatient(patient.toPatientInfo())
                .build();
    }
}
