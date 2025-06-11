package com.dermacare.bookingservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import com.dermacare.bookingservice.util.Response;


@FeignClient(name = "doctor-service")
public interface DoctorFeign {
	
	@GetMapping("/api/doctor-notes/get-all-doctor-notes")
    public ResponseEntity<Response> getAllNotes();

}
