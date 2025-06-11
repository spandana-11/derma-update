package com.dermacare.notificationservice.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dermacare.notificationservice.entity.NotificationEntity;

@Repository
public interface NotificationRepository extends MongoRepository<NotificationEntity, String> {

	public List<NotificationEntity> findByDataClinicIdAndDataDoctorId(String hospitalId, String doctorId);
	
}
