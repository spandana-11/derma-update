package com.dermacare.notificationservice.entity;


import java.util.Map;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "NotificationInfo" )
public class NotificationInfo {
	
	@Id
	private String id;
	private Map<String,Set<String>> sentNotifications;

			
}
