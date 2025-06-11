package com.dermacare.notificationservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dermacare.notificationservice.entity.NotificationInfo;

@Repository
public interface NotificationInfoRepository extends MongoRepository<NotificationInfo, String> {

}
