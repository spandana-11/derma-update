package com.dermacare.notificationservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Document(collection = "Notifications")
public class NotificationEntity {
@Id
    private String id;
	private String message;
	private Booking data;
	private String[] actions;
}
