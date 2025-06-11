package com.dermacare.bookingservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dermacare.bookingservice.dto.DoctorNotesDTO;
import com.dermacare.bookingservice.dto.ReportsDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "bookings")
@Setter
@Getter
@NoArgsConstructor
public class Booking  {
	@Id
	private String bookingId;
	private String bookingFor;
	private String name;
	private String age;
	private String gender;
	private String mobileNumber;
	private String problem;
	private String subServiceName;
	private String subServiceId;
	private String doctorId;
	private String clinicId;
	private String serviceDate;
	private String servicetime;
	private String consultationType;
	private double consultationFee;
	private String reasonForCancel;
	private DoctorNotes notes;
	private Reports reports;
	private String BookedAt;
	private String status;
	private double totalFee;

}
