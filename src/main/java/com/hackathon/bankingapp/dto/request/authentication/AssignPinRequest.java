package com.hackathon.bankingapp.dto.request.authentication;

import com.hackathon.bankingapp.utils.Password;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AssignPinRequest(@NotBlank @Pattern(regexp = "\\d{4}") String pin,
                               @Password String password){
}
