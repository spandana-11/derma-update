package com.dermacare.category_services.service.Impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dermacare.category_services.dto.SubServiceDTO;
import com.dermacare.category_services.dto.SubServicesInfoDto;
import com.dermacare.category_services.entity.SubServiceInfoEntity;
import com.dermacare.category_services.entity.SubServices;
import com.dermacare.category_services.entity.SubServicesInfoEntity;
import com.dermacare.category_services.repository.SubServiceRepository;
import com.dermacare.category_services.repository.SubServicesInfoRepository;
import com.dermacare.category_services.util.Converter;
import com.dermacare.category_services.util.Response;

@Service
public class SubServiceInfoServiceImpl {
	
	@Autowired
	private Converter converter;
	
	@Autowired
	private SubServicesInfoRepository subServicesInfoRepository;
	
	@Autowired
	private SubServiceRepository subServiceRepository;
	
		
	public  Response addSubService( SubServicesInfoDto dto){
		 Response response = new  Response();
	    	try {
	    	SubServicesInfoEntity entity = converter.entityConverter(dto);
	    	if(entity.getCategoryName() == null) {
	    	response.setStatus(404);
   			response.setSuccess(false);
   			response.setMessage("Incorrect CategoryId");
	    	}
	    	if(entity.getServiceName() == null) {
	    		response.setStatus(404);
   			response.setSuccess(false);
   			response.setMessage("Incorrect ServiceId");
	    	} 
	    	SubServicesInfoEntity e = subServicesInfoRepository.save(entity);
	    	if(e != null) {
		    			response.setData(converter.dtoConverter(entity));
		    			response.setStatus(200);
		    			response.setSuccess(true);
		    			response.setMessage("saved successfully");
	    	}else {
   			response.setStatus(404);
   			response.setSuccess(false);
   			response.setMessage(" Failed To AddSubService");
	    	}
	    	}catch(Exception e) {
	    	            response.setStatus(500);
		    			response.setMessage(e.getMessage());
		    			response.setSuccess(false);
	    	        }
	                    return response;
	    	    } 	 
	
	
	public Response getSubServiceByIdCategory(String categoryId){
		 Response response = new  Response();
	    	try {
	    SubServicesInfoEntity subServicesEntity = subServicesInfoRepository.
	    		findByCategoryId(categoryId);
	    if(subServicesEntity != null) {
	    SubServicesInfoDto dto = converter.dtoConverter(subServicesEntity);
		    			response.setData(dto);
		    			response.setStatus(200);
		    			response.setSuccess(true);
		    			response.setMessage("subservices fetched successfully");
	    }else {
			response.setStatus(404);
			response.setSuccess(false);
			response.setMessage("Subservice Not Found With given Id");
	    }
	    }catch(Exception e) {
	    	            response.setStatus(500);
		    			response.setMessage(e.getMessage());
		    			response.setSuccess(false);
	    	        }
	                    return response;
	    	    } 	 
	
	
	public Response getSubServicesByServiceId(String serviceId){
		 Response response = new  Response();
	    	try {
	    		  SubServicesInfoEntity subServicesEntity = subServicesInfoRepository.
	    				  findByServiceId(serviceId);
	    		  if(subServicesEntity != null) {
	    		    SubServicesInfoDto dto = converter.dtoConverter(subServicesEntity);
	    			    			response.setData(dto);
	    			    			response.setStatus(200);
	    			    			response.setSuccess(true);
	    			    			response.setMessage("subservices fetched successfully");
	    		  }else {
	    			  response.setStatus(404);
		    			response.setSuccess(false);
		    			response.setMessage("SubService Not Found With Given Id");
	    		  }
	    		  }catch(Exception e) {
	    		    	            response.setStatus(500);
	    			    			response.setMessage(e.getMessage());
	    			    			response.setSuccess(false);
	    		    	        }
	    		                    return response;
	    	    } 	
	
	
	
	public Response getSubServiceBySubServiceId(String subServiceId){
		 Response response = new  Response();
	    	try {
	    		SubServicesInfoEntity subServicesEntity = 
	    				subServicesInfoRepository.findBySubServicesSubServiceId(subServiceId);
	    				if(subServicesEntity != null) {	  	
	    		    SubServicesInfoDto dto = converter.dtoConverter(subServicesEntity);
	    			    			response.setData(dto);
	    			    			response.setStatus(200);
	    			    			response.setSuccess(true);
	    			    			response.setMessage("subservices fetched successfully");}
	    				else {
			    			response.setStatus(404);
			    			response.setSuccess(false);
			    			response.setMessage("Subservices Not Found With Given Id");
	    				}
	    			    		}catch(Exception e) {
	    		    	            response.setStatus(500);
	    			    			response.setMessage(e.getMessage());
	    			    			response.setSuccess(false);
	    		    	        }
	    		                    return response;
	    	    } 	
	
	
	public Response deleteSubService(String subServiceId){
		 Response response = new  Response();
	    	try {
	    		SubServicesInfoEntity subServicesEntity = 
	    		subServicesInfoRepository.findBySubServicesSubServiceId(subServiceId);
	    	 List<SubServices> subsrvice = subServiceRepository.findBySubServiceId(new ObjectId(subServiceId));
	    		if(subServicesEntity != null) {
	              List<SubServiceInfoEntity> list = subServicesEntity.getSubServices();
	              if(list != null) {	
	            	 list.removeIf(sub->subServiceId.equals(sub.getSubServiceId()));
	            	 subServicesEntity.setSubServices(list);
	            	 subServicesInfoRepository.save(subServicesEntity);
	            	 if(!subsrvice.isEmpty()) {
	 		        subServiceRepository.deleteAll(subsrvice);
	 		        	}
	    			response.setStatus(200);
	    			response.setSuccess(true);
	    			response.setMessage("subservice deleted successfully");}
	              else {
	            	  response.setStatus(404);
		    		  response.setSuccess(false);
		    		  response.setMessage("SubService Object Not Found With Given Id");
	              }
	    	}else {
	    			response.setStatus(404);
	    			response.setSuccess(false);
	    			response.setMessage("SubService Not Found With Given Id");
	    		}
	    	    }catch(Exception e) {
	    	    	 response.setStatus(500);
		    			response.setMessage(e.getMessage());
		    			response.setSuccess(false);
	    	    }
	    	return response;
	}
	
	public Response updateBySubServiceId(String subServiceId, SubServicesInfoDto domainServices) {
	    Response response = new Response();
	    try {
	        SubServicesInfoEntity subServicesEntity = 
	        subServicesInfoRepository.findBySubServicesSubServiceId(subServiceId);
	      List<SubServices> subsrvice = subServiceRepository.findBySubServiceId(new ObjectId(subServiceId));
	        if (subServicesEntity == null) {
	            response.setStatus(404);
	            response.setMessage("SubService with given ID not found");
	            response.setSuccess(false);
	            return response;}
	        List<SubServiceInfoEntity> listEntity = subServicesEntity.getSubServices();
	        if (domainServices.getSubServices() != null) {
	        	List<SubServiceDTO> domain = domainServices.getSubServices(); 
	        	for(SubServiceDTO s: domain ) {
	            Optional<SubServiceInfoEntity> optional = listEntity.stream().filter(n->n.getSubServiceId().
	            equals(subServiceId)).findFirst();
	            if(optional.isPresent()) {
	            optional.get().setSubServiceName(s.getSubServiceName());
	            if(!subsrvice.isEmpty()) {
		        	for(SubServices sub :  subsrvice ) {
		        		sub.setSubServiceName(s.getSubServiceName());
		        		subServiceRepository.save(sub);}}
	            }else {
	     	        response.setStatus(404);
	     	        response.setSuccess(false);
	     	        response.setMessage("SubService Not Found With Given Id");
	            }}
	        subServicesEntity.setSubServices(listEntity);	 
	        subServicesInfoRepository.save(subServicesEntity);
	        response.setStatus(200);
	        response.setSuccess(true);
	        response.setMessage("SubService updated successfully");
	        }} catch (Exception e) {
	        response.setStatus(500);
	        response.setMessage("Error: " + e.getMessage());
	        response.setSuccess(false);
	    }
	    return response;
	}
	
	
	public Response getAllSubServices(){
		 Response response = new  Response();
	    	try {
	    		List<SubServicesInfoEntity> subServicesEntity = subServicesInfoRepository.findAll();
	    		List<SubServicesInfoDto> list = new ArrayList<>();
	    		if(subServicesEntity != null) {
	    		for(SubServicesInfoEntity s :  subServicesEntity ) {
	    		    SubServicesInfoDto dto = converter.dtoConverter(s);
	    		                   list.add(dto); }
	    		                    response.setData(list);
	    			    			response.setStatus(200);
	    			    			response.setSuccess(true);
	    			    			response.setMessage("subservices fetched successfully");
	    		                   }else {
	    		                      	response.setStatus(404);
	    			                    response.setSuccess(false);
	    		                    	response.setMessage("Subservices Not Found with given Id");
	    		                   }
	    		                   }catch(Exception e) {
	    		    	            response.setStatus(500);
	    			    			response.setMessage(e.getMessage());
	    			    			response.setSuccess(false);
	    		    	        }
	    		                    return response;  }
}
