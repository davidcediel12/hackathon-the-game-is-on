package com.hackathon.bankingapp.controller.account;


import com.hackathon.bankingapp.dto.request.account.TransactionRequest;
import com.hackathon.bankingapp.dto.request.account.TransferRequest;
import com.hackathon.bankingapp.dto.response.ReducedGenericResponse;
import com.hackathon.bankingapp.dto.response.transaction.TransactionDetail;
import com.hackathon.bankingapp.services.transaction.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final TransactionService transactionService;


    @PostMapping("/deposit")
    public ResponseEntity<ReducedGenericResponse> depositMoney(@Valid @RequestBody
                                                               TransactionRequest transactionRequest) {

        transactionService.depositMoney(transactionRequest);
        return ResponseEntity.ok(getGenericResponse("Cash deposited successfully"));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ReducedGenericResponse> withdrawMoney(@Valid @RequestBody
                                                                TransactionRequest transactionRequest) {
        transactionService.withdrawMoney(transactionRequest);
        return ResponseEntity.ok(getGenericResponse("Cash withdrawn successfully"));
    }

    @PostMapping("/fund-transfer")
    public ResponseEntity<ReducedGenericResponse> transfer(@Valid @RequestBody TransferRequest transferRequest) {

        transactionService.transferMoney(transferRequest);

        return ResponseEntity.ok(getGenericResponse("Fund transferred successfully"));
    }


    private ReducedGenericResponse getGenericResponse(String message) {
        return new ReducedGenericResponse(message);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDetail>> getTransactions(){
        return ResponseEntity.ok(transactionService.getTransactions());
    }
}
