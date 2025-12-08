package com.eltiosento.economyapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDTO {
    @NotBlank(message = "New Password is required")
    @Size(min = 8, max = 20, message = "Password must be at least 8 characters and at most 20 characters")
    private String newPassword;
    @NotBlank(message = "Confirm Password is required")
    private String newPassword2;

}
