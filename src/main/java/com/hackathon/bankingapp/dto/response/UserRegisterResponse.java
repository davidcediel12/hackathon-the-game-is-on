package com.hackathon.bankingapp.dto.response;

public record UserRegisterResponse(String name,
                                   String password,
                                   String email,
                                   String address,
                                   String phoneNumber,
                                   String accountNumber,
                                   String hashedPassword) {
}
