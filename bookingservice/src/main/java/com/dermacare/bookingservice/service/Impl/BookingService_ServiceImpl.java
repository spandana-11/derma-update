package com.dermacare.bookingservice.service.Impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.dermacare.bookingservice.dto.BookingRequset;
import com.dermacare.bookingservice.dto.BookingResponse;
import com.dermacare.bookingservice.dto.DoctorNotesDTO;
import com.dermacare.bookingservice.dto.ReportsDTO;
import com.dermacare.bookingservice.entity.Booking;
import com.dermacare.bookingservice.entity.DoctorNotes;
import com.dermacare.bookingservice.entity.Reports;
import com.dermacare.bookingservice.feign.ClinicAdminFeign;
import com.dermacare.bookingservice.feign.DoctorFeign;
import com.dermacare.bookingservice.repository.BookingServiceRepository;
import com.dermacare.bookingservice.service.BookingService_Service;
import com.dermacare.bookingservice.util.ExtractFeignMessage;
import com.dermacare.bookingservice.util.Response;
import com.dermacare.bookingservice.util.ResponseStructure;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;


@Service
public class BookingService_ServiceImpl implements BookingService_Service {


	@Autowired
	private BookingServiceRepository repository;
	
	@Autowired
	private  DoctorFeign  doctorFeign;
	
	@Autowired
	private ClinicAdminFeign clinicAdminFeign;

	@Override
	public BookingResponse addService(BookingRequset request) {
		Booking entity = toEntity(request);
		try {
			entity.setStatus("Pending");			
		}catch (Exception e) {
			throw new RuntimeException("Unable to book service");
		}
		Booking response= repository.save(entity);
		return toResponse(response);
	}
	private static Booking toEntity(BookingRequset request) {
		Booking entity = new ObjectMapper().convertValue(request, Booking.class);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a", Locale.ENGLISH);
		String nowFormatted = LocalDateTime.now().format(formatter);
		entity.setBookedAt(nowFormatted);
		return entity;
	}

	private static BookingResponse toResponse(Booking entity) {
		BookingResponse response = new ObjectMapper().convertValue(entity,BookingResponse.class);
		if(entity.getConsultationType().equalsIgnoreCase("video consultation")) {
			response.setChannelId(randomNumber());
		}else {
			response.setChannelId(null) ;
		}		
		return response;
	}
	
	private static String randomNumber() {
        Random random = new Random();    
        int sixDigitNumber = 100000 + random.nextInt(900000); // Generates number from 100000 to 999999
        return String.valueOf(sixDigitNumber);
    }

	private List<BookingResponse> toResponses(List<Booking> bookings) {
		return bookings.stream().map(BookingService_ServiceImpl::toResponse).toList();
	}

	public BookingResponse getBookedService(String id) {
		Booking entity = repository.findByBookingId(new ObjectId(id))
				.orElseThrow(() -> new RuntimeException("Invalid Booking Id Please provide Valid Id"));
		return toResponse(entity);
	}

	@Override
	public BookingResponse deleteService(String id) {
		Booking entity = repository.findByBookingId(new ObjectId(id))
				.orElseThrow(() -> new RuntimeException("Invalid Booking Id Please provide Valid Id"));
		repository.deleteById(id);
		return toResponse(entity);
	}

	@Override
	public List<BookingResponse> getBookedServices(String mobileNumber) {
		List<Booking> bookings = repository.findByMobileNumber(mobileNumber);
		if (bookings == null) {
			return null;
		}
		return toResponses(bookings);
	}
	
	@Override
	public List<BookingResponse> getAllBookedServices() {
		List<Booking> bookings = repository.findAll();
		if (bookings == null) {
			return null;
		}
		return toResponses(bookings);
	}

	@Override
	public List<BookingResponse> bookingByDoctorId(String doctorId) {
		List<Booking> bookings = repository.findByDoctorId(doctorId);
		if (bookings == null) {
			return null;
		}
		return toResponses(bookings);
	}

	@Override
	public List<BookingResponse> bookingByServiceId(String serviceId) {
		List<Booking> bookings = repository.findBySubServiceId(serviceId);
		if (bookings.isEmpty()) {
			return null;
		}
		return toResponses(bookings);
	}

	@Override
	public List<BookingResponse> bookingByClinicId(String clinicId) {
		List<Booking> bookings = repository.findByClinicId(clinicId);
		if(bookings==null || bookings.isEmpty()) {
		 return null;
		}
		return toResponses(bookings);
	}
	
	
	public ResponseEntity<?> updateAppointment(BookingResponse bookingResponse){
		try {
		Booking entity = repository.findById(bookingResponse.getBookingId())
	.orElseThrow(() -> new RuntimeException("Invalid Booking Id Please provide Valid Id"));
		entity.setAge(bookingResponse.getAge());
		entity.setBookedAt( bookingResponse.getBookedAt());
		entity.setBookingFor( bookingResponse.getBookingFor());
		entity.setClinicId( bookingResponse.getClinicId());
		entity.setConsultationFee( bookingResponse.getConsultationFee());
		entity.setConsultationType( bookingResponse.getConsultationType());
		entity.setDoctorId( bookingResponse.getDoctorId());
		entity.setGender( bookingResponse.getGender());
		entity.setMobileNumber( bookingResponse.getMobileNumber());
		entity.setName( bookingResponse.getName());
		entity.setProblem( bookingResponse.getProblem());
		entity.setServiceDate(bookingResponse.getServiceDate());
		entity.setServicetime( bookingResponse.getServicetime());
		entity.setStatus( bookingResponse.getStatus());
		entity.setSubServiceId( bookingResponse.getSubServiceId());
		entity.setSubServiceName( bookingResponse.getSubServiceName());
		entity.setReasonForCancel( bookingResponse.getReasonForCancel());
		entity.setTotalFee(bookingResponse.getTotalFee());
		Booking e = repository.save(entity);
			ResponseEntity<Response> clinicRes = clinicAdminFeign.getAllReports();
			ResponseEntity<Response> doctorRes = doctorFeign.getAllNotes();
			List<Booking> bookings =  repository.findAll();
			Object doctorResponse = doctorRes.getBody().getData();
			Object clinicAdminResponse = clinicRes.getBody().getData();
			List<ReportsDTO> reports = new ObjectMapper().convertValue(clinicAdminResponse,new TypeReference<List<ReportsDTO>>() {});
			List<DoctorNotesDTO> notes = new ObjectMapper().convertValue(doctorResponse,new TypeReference<List<DoctorNotesDTO>>() {});
			List<Booking> completedBookings = bookings.stream().filter(n->n.getStatus().equalsIgnoreCase("completed")
			&& n.getReports() == null && n.getNotes()==null).toList();
			for(Booking b : completedBookings) {
			for(ReportsDTO r : reports) {
				if(b.getBookingId().equals(r.getBookingId())) {
					b.setReports(new ObjectMapper().convertValue(r,Reports.class));}}			
				for(DoctorNotesDTO n : notes) {
					if(b.getBookingId().equals(n.getBookingId())) {
						b.setNotes(new ObjectMapper().convertValue(n,DoctorNotes.class));
						 repository.save(b);
						}}}																
		if(e != null) {	
		return new ResponseEntity<>(ResponseStructure.buildResponse(null,
				"Booking updated sucessfully",HttpStatus.OK, HttpStatus.OK.value()),
				HttpStatus.OK);			
		}else {
			return new ResponseEntity<>(ResponseStructure.buildResponse(null,
					"Booking updated unsucessfully", HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value()),
					HttpStatus.NOT_FOUND);
		}}catch(FeignException e) {
			return new ResponseEntity<>(ResponseStructure.buildResponse(null,
					ExtractFeignMessage.clearMessage(e), HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}}
}
