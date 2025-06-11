package com.dermacare.doctorservice.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoctorDTO {
    private String username;
    private String password;

    public void setDoctorMobileNumber(String username) {
        this.username = username != null ? username.trim() : null;
    }

    public void setPassword(String password) {
        this.password = password != null ? password.trim() : null;
    }
}

