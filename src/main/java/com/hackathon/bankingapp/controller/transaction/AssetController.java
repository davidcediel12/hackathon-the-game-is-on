package com.hackathon.bankingapp.controller.transaction;


import com.hackathon.bankingapp.dto.request.transaction.AssetPurchaseRequest;
import com.hackathon.bankingapp.dto.request.transaction.AssetSaleRequest;
import com.hackathon.bankingapp.services.transaction.AssetService;
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
public class AssetController {

    private final AssetService assetService;

    @PostMapping("/buy-asset")
    public ResponseEntity<String> buyAsset(@Valid @RequestBody
                                           AssetPurchaseRequest assetPurchaseRequest) {

        assetService.buyAsset(assetPurchaseRequest);
        return ResponseEntity.ok("Asset purchase successful.");
    }

    @PostMapping("/sell-asset")
    public ResponseEntity<String> sellAsset(@Valid @RequestBody
                                            AssetSaleRequest assetSaleRequest) {

        assetService.sellAsset(assetSaleRequest);
        return ResponseEntity.ok("Asset sale successful.");
    }
}
