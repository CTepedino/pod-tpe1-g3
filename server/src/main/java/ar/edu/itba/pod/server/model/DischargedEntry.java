package ar.edu.itba.pod.server.model;

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


}
