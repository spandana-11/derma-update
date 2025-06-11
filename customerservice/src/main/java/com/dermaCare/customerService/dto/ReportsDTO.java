package com.dermaCare.customerService.dto;

import lombok.Data;

@Data
public class ReportsDTO {
	private String bookingId;
	private String customerMobileNumber;
	private String reportName;
	private String reportDate;
	private String reportStatus;
	private String reportType;
	private String reportFile;

}