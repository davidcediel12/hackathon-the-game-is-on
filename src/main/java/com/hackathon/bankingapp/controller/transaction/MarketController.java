package com.hackathon.bankingapp.controller.transaction;


import com.hackathon.bankingapp.services.transaction.MarketPricesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/market/prices")
@RequiredArgsConstructor
public class MarketController {

    private final MarketPricesService marketPricesService;

    @GetMapping
    public ResponseEntity<Map<String, BigDecimal>> getMarketPrices(){
        return ResponseEntity.ok(marketPricesService.getAssetsPrice());
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<BigDecimal> getAssetPrice(@PathVariable String symbol){
        return ResponseEntity.ok(marketPricesService.getAssetPrice(symbol));
    }
}
