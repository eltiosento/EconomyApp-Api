package com.eltiosento.economyapp.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private Long id;
    private String username;
    @Email(message = "Must be a valid email")
    private String email;
    private String firstName;
    private String lastName;
    private String profileImage;
    private String roleName;

}
