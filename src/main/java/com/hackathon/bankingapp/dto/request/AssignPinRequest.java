package com.hackathon.bankingapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AssignPinRequest(@NotBlank @Pattern(regexp = "\\d{4}") String pin,
                               @NotBlank String password){
}
