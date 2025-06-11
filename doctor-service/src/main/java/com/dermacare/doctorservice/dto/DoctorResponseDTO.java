package com.dermacare.doctorservice.dto;



import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DoctorResponseDTO {
    private String id;
    private String doctorMobileNumber;
}

