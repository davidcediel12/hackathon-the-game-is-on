package com.hackathon.bankingapp.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserRegisterRequest(@NotBlank String name,
                                  @NotBlank String password,
                                  @Email String email,
                                  @NotBlank String address,
                                  @NotBlank @Pattern(regexp = "\\d+") String phoneNumber) {
}
