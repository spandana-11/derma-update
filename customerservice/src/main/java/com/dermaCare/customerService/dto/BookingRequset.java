package com.dermaCare.customerService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequset {
	
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
	private double totalFee;
	private String bookedAt;

}
