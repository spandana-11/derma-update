package com.AdminService.dto;


import com.AdminService.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingResponse {
	
	private String bookingId;
	private String bookingFor;
	private String name;
	private String age;
	private String gender;
	private String mobileNumber;
	private String problem;
	private String servicename;
	private String serviceId;
	private String doctorId;
	private String serviceDate;
	private String servicetime;
	private String consultationType;
	private double consultattionFee;
	private double serviceCost;
	private String BookeAt;
	private String bookedStatus;
	private double totalFee;
	private Payment payment;

}
