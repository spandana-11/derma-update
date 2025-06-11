package com.dermacare.notificationservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.dermacare.notificationservice.dto.BookingResponse;
import com.dermacare.notificationservice.util.ResponseStructure;


@FeignClient(value = "bookingservice")
public interface BookServiceFeign {
	
	@GetMapping("/api/v1/getBookedService/{id}")
	public ResponseEntity<ResponseStructure<BookingResponse>> getBookedService(@PathVariable String id);
	
	@PutMapping("/api/v1/updateAppointment")
	public ResponseEntity<?> updateAppointment(@RequestBody BookingResponse bookingResponse );

}
