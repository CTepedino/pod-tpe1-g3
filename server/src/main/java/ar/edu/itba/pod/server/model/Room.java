package ar.edu.itba.pod.server.model;

import ar.edu.itba.pod.server.exception.InvalidPatientDoctorPairException;
import emergencyRoom.Messages;

public class Room {

    private Patient patient;
    private Doctor doctor;

    public synchronized void startCare(Patient patient, Doctor doctor){
        this.patient = patient;
        this.doctor = doctor;
        doctor.setStatus(Messages.DoctorStatus.DOCTOR_STATUS_ATTENDING);
    }

    public synchronized Patient endCare(String patientName, String doctorName){
        Patient toReturn = patient;
        if (patientName.equals(this.patient.getName()) && doctorName.equals(doctor.getName())){
            patient = null;
            doctor.setStatus(Messages.DoctorStatus.DOCTOR_STATUS_AVAILABLE);
            doctor = null;
        } else {
            throw new InvalidPatientDoctorPairException();
        }
        return toReturn;
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
