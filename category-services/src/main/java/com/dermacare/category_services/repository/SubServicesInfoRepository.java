package com.dermacare.category_services.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dermacare.category_services.entity.SubServicesInfoEntity;

public interface SubServicesInfoRepository extends MongoRepository<SubServicesInfoEntity, String> {

	public SubServicesInfoEntity findByCategoryId(String categoryId);

	public SubServicesInfoEntity findByServiceId(String serviceId);

	public SubServicesInfoEntity findBySubServicesSubServiceId(String subServiceId);

	public SubServicesInfoEntity findByCategoryNameAndServiceName(String CategoryName, String ServiceName);

	 boolean existsBySubServices_SubServiceId(String subServiceId);

}
