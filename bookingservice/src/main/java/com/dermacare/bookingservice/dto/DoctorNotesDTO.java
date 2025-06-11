package com.dermacare.bookingservice.dto;

import lombok.Data;

@Data
public class DoctorNotesDTO {
	private String id;
    private String bookingId;
    private String doctorId;
    private String patientPhoneNumber;
    private String notes;
}
