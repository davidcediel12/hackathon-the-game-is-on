package com.hackathon.bankingapp.dto.request.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ResetTokenRequest (@NotBlank @Email String identifier,
                                 @Pattern(regexp = "\\d{6}") String otp){
}
