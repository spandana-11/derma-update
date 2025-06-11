package com.dermacare.notificationservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationDTO {
	private String id;
	private String message;
	private BookingResponse data;
	private String[] actions;

}
