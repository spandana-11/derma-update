package com.dermacare.notificationservice.serviceimpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.dermacare.notificationservice.dto.BookingResponse;
import com.dermacare.notificationservice.dto.NotificationDTO;
import com.dermacare.notificationservice.dto.NotificationResponse;
import com.dermacare.notificationservice.entity.Booking;
import com.dermacare.notificationservice.entity.NotificationEntity;
import com.dermacare.notificationservice.entity.NotificationInfo;
import com.dermacare.notificationservice.feign.BookServiceFeign;
import com.dermacare.notificationservice.repository.NotificationInfoRepository;
import com.dermacare.notificationservice.repository.NotificationRepository;
import com.dermacare.notificationservice.service.NotificationService;
import com.dermacare.notificationservice.util.ExtractFeignMessage;
import com.dermacare.notificationservice.util.ResBody;
import com.dermacare.notificationservice.util.ResponseStructure;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;


@Service
public class NotificationServiceImpl implements NotificationService {
	
	@Autowired
	private NotificationRepository notificationRepository;
	
	@Autowired
	private  BookServiceFeign  bookServiceFeign;
	
	@Autowired
	private NotificationInfoRepository notificationInfoRepository;
	
	Set<String> notificationtoadmin = new LinkedHashSet<>();
	
	public ResBody<String> notification(NotificationDTO notificationDTO) {
		try {
			NotificationEntity notificationEntity = new NotificationEntity();
			notificationEntity.setMessage(notificationDTO.getMessage());
			notificationEntity.setData(new ObjectMapper().convertValue(notificationDTO.getData(),Booking.class));
			notificationEntity.setActions(notificationDTO.getActions());
			NotificationEntity entity =	notificationRepository.save(notificationEntity);
			if(entity != null) {
				return new ResBody<String>("Notification Saved Sucessfully",200,"Notificcation Successfully Created By Booking");
			}
			return new ResBody<String>("Notification Not Saved In The DataBase",404,null);
			
		}catch(Exception e) {
			return new ResBody<String>(e.getMessage(),500,null);
		}
	}
	
	
	public ResBody<List<NotificationDTO>> notificationtodoctorandclinic( String hospitalId,
			 String doctorId){
		ResBody<List<NotificationDTO>> res = new ResBody<List<NotificationDTO>>();
		List<NotificationDTO> eligibleNotifications = new ArrayList<>();
		try {
		List<NotificationEntity> entity = notificationRepository.findByDataClinicIdAndDataDoctorId(hospitalId, doctorId);
		List<NotificationDTO> dto = new ObjectMapper().convertValue(entity, new TypeReference<List<NotificationDTO>>() {});
		List<NotificationInfo> obj = notificationInfoRepository.findAll();
		if(obj == null || obj.isEmpty()) {
		NotificationInfo f = new NotificationInfo();
		notificationInfoRepository.save(f);}
		List<NotificationInfo> info = notificationInfoRepository.findAll();
		if(!dto.isEmpty()) {
			if(!info.isEmpty()) {
			for(NotificationInfo s:info) {
				if(s.getSentNotifications() != null && !s.getSentNotifications().isEmpty()) {
				Map<String,Set<String>> map = s.getSentNotifications();
				Set<String> send = map.get(doctorId);
		      	  for(NotificationDTO n : dto) {
			      if(!send.contains(n.getId())) {
						send.add(n.getId());
						map.put(doctorId, send);
						notificationInfoRepository.save(s);
					eligibleNotifications.add(n);}}
			res = new ResBody<List<NotificationDTO>>("Notification sent Successfully",200,eligibleNotifications);
					}else{
				Map<String,Set<String>> map = new LinkedHashMap<>();
				Set<String> lst = new LinkedHashSet<>();
					for(NotificationDTO t : dto) {
						lst.add(t.getId());
					map.put(doctorId, lst);
					s.setSentNotifications(map);
					notificationInfoRepository.save(s);
					eligibleNotifications.add(t);}	
					res = new ResBody<List<NotificationDTO>>("Notification sent Successfully",200,eligibleNotifications);
				}}}else {
					res = new ResBody<List<NotificationDTO>>("NotificationInfo Not Found",404,null);
				}}else{
					res = new ResBody<List<NotificationDTO>>("Notifications Not Found",404,null);
			}
		 ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
         scheduler.schedule(() -> {
        	 List<NotificationInfo> infor = notificationInfoRepository.findAll(); 
             for(NotificationInfo notificationInfo : infor) {
            	 Collection<Set<String>> cln = notificationInfo.getSentNotifications().values();
            	 for(Set<String> s : cln) {
            		 for(String a : s) {
            		Optional<NotificationEntity> notificationEntity = notificationRepository.findById(a);
             if (notificationEntity.get() != null && "pending".equalsIgnoreCase(notificationEntity.get().getData().getStatus())) {
            	 notificationtoadmin.add(notificationEntity.get().getId());}}}
         }},1 , TimeUnit.MINUTES);
	}catch(Exception e) {
		res = new ResBody<List<NotificationDTO>>(e.getMessage(),500,null);
	}
		return res;
		}
		
		
	
	public ResBody<List<NotificationDTO>> sendNotificationToAdmin() {
		ResBody<List<NotificationDTO>> r = new ResBody<List<NotificationDTO>>();
		List<NotificationEntity> list = new ArrayList<>();
		try {
			for(String s : notificationtoadmin ) {
				Optional<NotificationEntity> notificationEntity = notificationRepository.findById(s);
				list.add(notificationEntity.get());}
			List<NotificationDTO> res =  new ObjectMapper().convertValue(list, new TypeReference<List<NotificationDTO>>() {} );
		    if(res != null && !res.isEmpty()) {
		    	r = new ResBody<List<NotificationDTO>>("Notifications sent to the admin",200,res);
		    	notificationtoadmin.clear();
		    }else {
		    r = new ResBody<List<NotificationDTO>>("Notifications Not Found",404,null); }  
		}catch(Exception e) {
			r = new ResBody<List<NotificationDTO>>(e.getMessage(),500,null);
		}
		return r;	
	}


	public ResBody<NotificationDTO> notificationResponse(NotificationResponse notificationResponse){
		try {
			ResponseEntity<ResponseStructure<BookingResponse>> res =  bookServiceFeign.getBookedService(notificationResponse.getAppointmentId());
			BookingResponse b = res.getBody().getData();
			if(b.getDoctorId().equalsIgnoreCase(notificationResponse.getDoctorId())&&b.getClinicId().
			equalsIgnoreCase(notificationResponse.getHospitalId())&&b.getBookingId().equalsIgnoreCase(notificationResponse.getAppointmentId())
			&&b.getSubServiceId().equalsIgnoreCase(notificationResponse.getSubServiceId())) {	
				switch(notificationResponse.getStatus()) {
				case "accepted": b.setStatus("accepted");
				Optional<NotificationEntity> notificationEntity = notificationRepository.findById(notificationResponse.getNotificationId());
				NotificationEntity n = notificationEntity.get();
				n.getData().setStatus("accepted");
				notificationRepository.save(n);
				       for(NotificationInfo s : notificationInfoRepository.findAll()) {
				 Set<String> lt = s.getSentNotifications().get(notificationResponse.getDoctorId());
				 lt.removeIf(item -> item.equals(notificationResponse.getNotificationId()));
				 notificationInfoRepository.save(s);
				}				       
				break;
				case "rejected": b.setStatus("rejected");
				 b.setReasonForCancel(notificationResponse.getReasonForCancel());
				 Optional<NotificationEntity> obj = notificationRepository.findById(notificationResponse.getNotificationId());
					NotificationEntity c = obj.get();
					c.getData().setStatus("rejected");
					notificationRepository.save(c);
				 for(NotificationInfo s : notificationInfoRepository.findAll()) {
				 Set<String> lt = s.getSentNotifications().get(notificationResponse.getDoctorId());
				 lt.removeIf(item -> item.equals(notificationResponse.getNotificationId()));
				 notificationInfoRepository.save(s);}
				break;
				case "completed":b.setStatus("completed");
				 Optional<NotificationEntity> nObj = notificationRepository.findById(notificationResponse.getNotificationId());
					NotificationEntity oj = nObj.get();
					oj.getData().setStatus("completed");
					notificationRepository.save(oj);
				for(NotificationInfo s : notificationInfoRepository.findAll()) {
					 Set<String> lt = s.getSentNotifications().get(notificationResponse.getDoctorId());
					 lt.removeIf(item -> item.equals(notificationResponse.getNotificationId()));
					 notificationInfoRepository.save(s);}
				break;
				default:b.setStatus("pending");
				}
		    	ResponseEntity<?> book = bookServiceFeign.updateAppointment(b);
		    	if(book != null) {
		    		return new ResBody<NotificationDTO>("Appointment Status updated",200,null);
		    	}else {
		    	return new ResBody<NotificationDTO>("Appointment Status Not updated",404,null);		    }
		    	}else {
		    	return new ResBody<NotificationDTO>("Appointment Not updated",404,null);
		    }
		}catch(FeignException e) {
			return new ResBody<NotificationDTO>(ExtractFeignMessage.clearMessage(e),500,null);
		}}
	
}
