
package com.clinicadmin.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorLoginDTO {
    private String username;
    private String password;

    public void setDoctorMobileNumber(String doctorMobileNumber) {
        this.username = username != null ? username.trim() : null;
    }

    public void setPassword(String password) {
        this.password = password != null ? password.trim() : null;
    }
}
