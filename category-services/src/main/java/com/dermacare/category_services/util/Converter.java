package com.dermacare.category_services.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dermacare.category_services.dto.CategoryDto;
import com.dermacare.category_services.dto.ServicesDto;
import com.dermacare.category_services.dto.SubServiceDTO;
import com.dermacare.category_services.dto.SubServicesInfoDto;
import com.dermacare.category_services.entity.SubServiceInfoEntity;
import com.dermacare.category_services.entity.SubServicesInfoEntity;
import com.dermacare.category_services.repository.SubServicesInfoRepository;
import com.dermacare.category_services.service.Impl.CategoryServiceImpl;
import com.dermacare.category_services.service.Impl.ServicesServiceImpl;


@Component
public class Converter {
	
	
	@Autowired
	private SubServicesInfoRepository subServicesRepository;
	
	@Autowired
	private CategoryServiceImpl categoryServiceImpl;
	
	@Autowired
	private ServicesServiceImpl servicesServiceImpl;

	public SubServicesInfoEntity entityConverter(SubServicesInfoDto dto) {
	    CategoryDto c = categoryServiceImpl.getCategorById(dto.getCategoryId());
	    ServicesDto d = servicesServiceImpl.getServiceById(dto.getServiceId());

	    if (c == null) {
	        throw new IllegalArgumentException("Category not found for ID: " + dto.getCategoryId());
	    }
	    if (d == null) {
	        throw new IllegalArgumentException("Service not found for ID: " + dto.getServiceId());
	    }

	    String categoryName = c.getCategoryName();
	    String serviceName = d.getServiceName();

	    SubServicesInfoEntity sub = subServicesRepository.findByCategoryNameAndServiceName(categoryName, serviceName);

	    if (sub != null) {
	        List<SubServiceInfoEntity> listEntity = sub.getSubServices();
	        Set<String> existingSubServiceNames = listEntity.stream()
	            .map(SubServiceInfoEntity::getSubServiceName)
	            .collect(Collectors.toSet()); 

	        boolean duplicateFound = false;

	        for (SubServiceDTO sdto : dto.getSubServices()) {
	            // **Validation: Ignore empty sub-service names**
	            if (sdto.getSubServiceName() == null || sdto.getSubServiceName().trim().isEmpty()) {
	                throw new IllegalArgumentException("Invalid sub-service name found, request ignored.");
	            }

	            // **Validation: Check for duplicates**
	            if (existingSubServiceNames.contains(sdto.getSubServiceName())) {
	                duplicateFound = true;
	                break; // No need to continue checking
	            }
	        }

	        if (duplicateFound) {
	            throw new IllegalArgumentException("Duplicate sub-services found.");
	        }

	        for (SubServiceDTO sdto : dto.getSubServices()) {
	            SubServiceInfoEntity e = new SubServiceInfoEntity();
	            ObjectId objectId = new ObjectId();
	            e.setSubServiceId(objectId.toHexString());
	            e.setSubServiceName(sdto.getSubServiceName());
	            listEntity.add(e);
	        }
	        sub.setSubServices(listEntity);
	        return sub;
	    } else {
	        SubServicesInfoEntity entity = new SubServicesInfoEntity();
	        entity.setCategoryId(dto.getCategoryId());
	        entity.setCategoryName(categoryName);
	        entity.setServiceId(dto.getServiceId());
	        entity.setServiceName(serviceName);

	        List<SubServiceInfoEntity> listEntity = new ArrayList<>();
	        Set<String> existingSubServiceNames = new HashSet<>(); 

	        boolean duplicateFound = false;

	        for (SubServiceDTO sdto : dto.getSubServices()) {
	            // **Validation: Ignore empty sub-service names**
	            if (sdto.getSubServiceName() == null || sdto.getSubServiceName().trim().isEmpty()) {
	                throw new IllegalArgumentException("Invalid sub-service name found.");
	            }

	            // **Validation: Check for duplicates**
	            if (existingSubServiceNames.contains(sdto.getSubServiceName())) {
	                duplicateFound = true;
	                break;
	            } else {
	                SubServiceInfoEntity e = new SubServiceInfoEntity();
	                ObjectId objectId = new ObjectId();
	                e.setSubServiceId(objectId.toHexString());
	                e.setSubServiceName(sdto.getSubServiceName());
	                listEntity.add(e);
	                existingSubServiceNames.add(sdto.getSubServiceName());
	            }
	        }

	        if (duplicateFound) {
	            throw new IllegalArgumentException("Duplicate sub-services found, request ignored.");
	        }

	        entity.setSubServices(listEntity);
	        return entity;
	    }
	}



	

	public SubServicesInfoDto dtoConverter( SubServicesInfoEntity entity) {
		SubServicesInfoDto dto = new SubServicesInfoDto();
		if (entity != null) {  
		        dto.setCategoryId(entity.getCategoryId());
		        dto.setCategoryName(entity.getCategoryName());
		        dto.setServiceId(entity.getServiceId());
		        dto.setServiceName(entity.getServiceName());   	    
		    if(entity.getSubServices()!= null) {
				List<SubServiceDTO> listDTO = new ArrayList<>();
				for(SubServiceInfoEntity s : entity.getSubServices()) {
					SubServiceDTO e = new SubServiceDTO();
					e.setSubServiceName(s.getSubServiceName());
					e.setSubServiceId(s.getSubServiceId());
					listDTO.add(e);
					dto.setSubServices(listDTO);	
				}}}
		return dto;
	}}
