package com.dermacare.doctorservice.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.dermacare.doctorservice.dto.BookingResponse;
import com.dermacare.doctorservice.dto.DoctorNotesDTO;
import com.dermacare.doctorservice.dto.Response;
import com.dermacare.doctorservice.dto.ResponseStructure;
import com.dermacare.doctorservice.feignclient.BookingFeignClient;
import com.dermacare.doctorservice.model.DoctorNotes;
import com.dermacare.doctorservice.repository.DoctorNotesRepository;

@Service
public class DoctorNotesServiceImpl implements DoctorNotesService {

    @Autowired
    private DoctorNotesRepository repository;

    @Autowired
    private BookingFeignClient bookingFeignClient;

    @Override
    public Response addDoctorNote(DoctorNotesDTO dto) {
       
        ResponseEntity<ResponseStructure<BookingResponse>> bookingResponseEntity =
            bookingFeignClient.getBookedService(dto.getBookingId());


        if (bookingResponseEntity.getStatusCode().is2xxSuccessful() &&
            bookingResponseEntity.getBody() != null &&
            bookingResponseEntity.getBody().getData() != null) {

            BookingResponse bookingData = bookingResponseEntity.getBody().getData();

            
            DoctorNotes note = new DoctorNotes();
            note.setBookingId(dto.getBookingId());
            note.setDoctorId(bookingData.getDoctorId());
            note.setPatientPhoneNumber(bookingData.getMobileNumber()); 
            note.setNotes(dto.getNotes());

            // 4. Save to MongoDB
            DoctorNotes saved = repository.save(note);

            return new Response(true, saved, "Doctor note added successfully", 201);
        } else {
            // Handle case when booking ID is invalid
            return new Response(false, null, "Invalid booking ID: Booking not found", 404);
        }
    }

    @Override
    public Response getAllDoctorNotes() {
        List<DoctorNotes> notes = repository.findAll();
        // Convert to DTOs
        List<DoctorNotesDTO> dtos = notes.stream().map(note -> {
            DoctorNotesDTO dto = new DoctorNotesDTO();
            dto.setBookingId(note.getBookingId());
            dto.setDoctorId(note.getDoctorId());
            dto.setPatientPhoneNumber(note.getPatientPhoneNumber());
            dto.setNotes(note.getNotes());
            return dto;
        }).collect(Collectors.toList());
        return new Response(true, dtos, "Fetched all doctor notes successfully", 200);
    }
}
