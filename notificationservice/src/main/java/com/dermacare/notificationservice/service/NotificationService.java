package com.dermacare.notificationservice.service;

import java.util.List;
import com.dermacare.notificationservice.dto.BookingResponse;
import com.dermacare.notificationservice.dto.NotificationDTO;
import com.dermacare.notificationservice.dto.NotificationResponse;
import com.dermacare.notificationservice.util.ResBody;

public interface NotificationService {
	
	public  ResBody<String> notification(NotificationDTO notificationDTO);
	
	public  ResBody<List<NotificationDTO>>  notificationtodoctorandclinic( String hospitalId,
			 String doctorId);
	
	public ResBody<NotificationDTO> notificationResponse(NotificationResponse notificationResponse);
	
	public ResBody<List<NotificationDTO>> sendNotificationToAdmin();
}
