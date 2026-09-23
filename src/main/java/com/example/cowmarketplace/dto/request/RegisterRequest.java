package com.example.cowmarketplace.dto.request;

import com.example.cowmarketplace.entity.Role;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "{validation.email.invalid}")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "{validation.mobile.invalid}")
    private String mobileNumber;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "{validation.mobile.invalid}")
    private String whatsappNumber;

    private String address;

    private String city;

    private String state;

    @NotNull(message = "Role is required")
    private Role role;
}