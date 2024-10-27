package com.hackathon.bankingapp.controller.authentication;


import com.hackathon.bankingapp.dto.request.authentication.AssignPinRequest;
import com.hackathon.bankingapp.dto.request.authentication.UpdatePinRequest;
import com.hackathon.bankingapp.dto.response.ReducedGenericResponse;
import com.hackathon.bankingapp.services.customer.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/account/pin")
@RequiredArgsConstructor
public class PinController {

    private final AccountService accountService;


    @PostMapping("/create")
    public ResponseEntity<ReducedGenericResponse> createPin(@Valid @RequestBody AssignPinRequest pinRequest) {
        accountService.assignPin(pinRequest);

        return ResponseEntity.ok(new ReducedGenericResponse("PIN created successfully"));
    }

    @PostMapping("/update")
    public ResponseEntity<ReducedGenericResponse> updatePin(@Valid @RequestBody UpdatePinRequest pinRequest) {
        accountService.updatePin(pinRequest);
        return ResponseEntity.ok(new ReducedGenericResponse("PIN updated successfully"));
    }
}
