package com.clinicadmin.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "doctor_login_credentials")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorLoginCredentials {

    @Id
    private String id;
    private String doctorId;       // Reference to the Doctor
    private String username;       // Usually mobile number
    private String password;       // Encrypted password
    private String role = "DOCTOR"; // Default role
}

