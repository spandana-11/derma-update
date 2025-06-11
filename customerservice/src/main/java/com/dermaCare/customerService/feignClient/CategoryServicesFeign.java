package com.dermaCare.customerService.feignClient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.dermaCare.customerService.dto.SubServicesDto;
import com.dermaCare.customerService.util.ResponseStructure;

@FeignClient(value = "category-services")
public interface CategoryServicesFeign {
	
	@GetMapping("/api/v1/subServices/getAllSubServices")
	public ResponseEntity<ResponseStructure<List<SubServicesDto>>> getAllSubServices();

	@GetMapping("/api/v1/subServices/getSubService/{hospitalId}/{subServiceId}")
	public ResponseEntity<ResponseStructure<SubServicesDto>> getSubServiceBySubServiceId(@PathVariable String hospitalId, @PathVariable String subServiceId);
}
