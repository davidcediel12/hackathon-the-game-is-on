package com.hackathon.bankingapp.dto.request.authentication;

import com.hackathon.bankingapp.utils.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserRegisterRequest(@NotBlank String name,
                                  @Password String password,
                                  @NotBlank @Email String email,
                                  @NotBlank String address,
                                  @NotBlank @Pattern(regexp = "\\d+") String phoneNumber) {
}
