package com.hackathon.bankingapp.dto.response.dashboard;

public record UserDetailsResponse(String name,
                                  String email,
                                  String address,
                                  String phoneNumber,
                                  String accountNumber,
                                  String hashedPassword) {
}
