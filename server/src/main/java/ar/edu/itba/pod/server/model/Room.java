package ar.edu.itba.pod.server.model;

import emergencyRoom.Messages;

public class Room {

    private Patient patient;
    private Doctor doctor;

    public synchronized void startCare(Patient patient, Doctor doctor){
        this.patient = patient;
        this.doctor = doctor;
        doctor.setStatus(Messages.DoctorStatus.DOCTOR_STATUS_ATTENDING);
    }

    public synchronized void endCare(String patientName, String doctorName){
        if (patientName.equals(this.patient.getName()) && doctorName.equals(doctor.getName())){
            patient = null;
            doctor.setStatus(Messages.DoctorStatus.DOCTOR_STATUS_AVAILABLE);
            doctor = null;
        } //else throw...
    }

    public boolean isAvailable() {
        return patient == null && doctor == null;
    }

    public Patient getPatient() {
        return patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

}
