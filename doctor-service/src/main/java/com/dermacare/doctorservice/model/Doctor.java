package com.dermacare.doctorservice.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "doctors")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Doctor {

    private String doctorMobileNumber;
    private String password;
    private List<String> oldPasswords = new ArrayList<>();
}
