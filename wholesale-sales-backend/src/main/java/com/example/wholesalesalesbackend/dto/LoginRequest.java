package com.example.wholesalesalesbackend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class LoginRequest {

    private String username;

    private String mail;

    @Min(value = 0)
    @Max(value = 8)
    private String password;

    @Min(value = 10)
    @Max(value = 10)
    private Long mobileNumber;

}
