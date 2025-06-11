package com.dermacare.doctorservice.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.dermacare.doctorservice.dto.BookingResponse;
import com.dermacare.doctorservice.dto.ResponseStructure;

@FeignClient(name = "bookingservice")

public interface  BookingFeignClient {
	
	@GetMapping("/api/v1/getBookedService/{id}")
	public ResponseEntity<ResponseStructure<BookingResponse>> getBookedService(@PathVariable String id);

}
