package com.hackathon.bankingapp.controller.account;


import com.hackathon.bankingapp.dto.request.account.DepositRequest;
import com.hackathon.bankingapp.dto.response.ReducedGenericResponse;
import com.hackathon.bankingapp.services.transaction.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final TransactionService transactionService;


    @PostMapping("/deposit")
    public ResponseEntity<ReducedGenericResponse> depositMoney(@Valid @RequestBody
                                                               DepositRequest depositRequest) {

        transactionService.depositMoney(depositRequest);
        return ResponseEntity.ok(new ReducedGenericResponse("Cash deposited successfully"));
    }
}
