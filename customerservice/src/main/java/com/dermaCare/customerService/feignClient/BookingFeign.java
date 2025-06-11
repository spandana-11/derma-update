package com.dermaCare.customerService.feignClient;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.dermaCare.customerService.dto.BookingRequset;
import com.dermaCare.customerService.dto.BookingResponse;
import com.dermaCare.customerService.util.ResponseStructure;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;


@FeignClient(value = "bookingservice")
public interface BookingFeign {
@CircuitBreaker(name = "BookingServiceCircuitBreaker", fallbackMethod = "bookServiceFallBack")
	@PostMapping("/api/v1/bookService")
	public ResponseEntity<ResponseStructure<BookingResponse>> bookService(@RequestBody BookingRequset req);

	@DeleteMapping("/api/v1/deleteService/{id}")
	public ResponseEntity<ResponseStructure<BookingResponse>> deleteBookedService(@PathVariable String id);

	@GetMapping("/api/v1/getBookedService/{id}")
	public ResponseEntity<ResponseStructure<BookingResponse>> getBookedService(@PathVariable String id);

	@GetMapping("/api/v1/getBookedServicesByMobileNumber/{mobileNumber}")
	public ResponseEntity<ResponseStructure<List<BookingResponse>>> getCustomerBookedServices(
			@PathVariable String mobileNumber);
	@GetMapping("/api/v1/getAllBookedServices")
	public ResponseEntity<ResponseStructure<List<BookingResponse>>> getAllBookedService();

	@GetMapping("/api/v1/getAllBookedServices/{doctorId}")
	public ResponseEntity<ResponseStructure<List<BookingResponse>>> getBookingByDoctorId(@PathVariable String doctorId);

	@GetMapping("/api/v1/getBookedServicesByServiceId/{serviceId}")
	public ResponseEntity<ResponseStructure<List<BookingResponse>>> getBookingByServiceId(@PathVariable String serviceId);
	
	@GetMapping("/api/v1/getBookedServicesByClinicId/{clinicId}")
	public ResponseEntity<ResponseStructure<List<BookingResponse>>> getBookingByClinicId(@PathVariable String clinicId);
	
	
	//FALLBACK METHODS
	
	default ResponseEntity<?> bookServiceFallBack(Exception e){
		return ResponseEntity.status(503).body(e.getMessage());
		}
	
	
}
