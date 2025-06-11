package com.AdminService.dto;

import java.util.List;

import org.hibernate.validator.constraints.URL;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClinicDTO {

	    private String hospitalId;

	    @NotBlank(message = "Hospital name is required")
	    private String name;

	    @NotBlank(message = "Address is required")
	    private String address;

	    @NotBlank(message = "City is required")
	    private String city;

	    @NotBlank(message = "Contact number is required")
	    @Pattern(regexp = "^[0-9]{10}$", message = "Contact number must be 10 digits")
	    private String contactNumber;

	    @NotBlank(message = "Hospital registration info is required")
	    private String hospitalRegistrations;

	    @NotBlank(message = "Opening time is required")
	    private String openingTime;

	    @NotBlank(message = "Closing time is required")
	    private String closingTime;

	    @NotBlank(message = "Hospital logo (Base64) is required")
	    private String hospitalLogo;

	    @NotBlank(message = "Email is required")
	    @Email(message = "Invalid email format")
	    private String emailAddress;

	    @URL(message = "Invalid website URL")
	    private String website;

	    @NotBlank(message = "License number is required")
	    private String licenseNumber;

	    @NotBlank(message = "Issuing authority is required")
	    private String issuingAuthority; 
	    
	    @NotNull(message = "Documents list must not be null")
	    private List<String> contractorDocuments;
	    
        @NotNull(message = "Documents list must not be null")
	    private List<String> hospitalDocuments;
        
        private boolean recommended;

	    // Getters and setters
	}

	   
	    
