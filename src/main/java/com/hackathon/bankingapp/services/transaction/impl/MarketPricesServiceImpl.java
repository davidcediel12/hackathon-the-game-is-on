package com.hackathon.bankingapp.services.transaction.impl;

import com.hackathon.bankingapp.exceptions.ApiException;
import com.hackathon.bankingapp.services.transaction.MarketPricesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MarketPricesServiceImpl implements MarketPricesService {


    private final RestTemplate restTemplate;

    @Value("${api.market.prices}")
    String apiMarketPrices;

    @Override
    public Map<String, BigDecimal> getAssetsPrice() {

        ParameterizedTypeReference<Map<String, BigDecimal>> responseType =
                new ParameterizedTypeReference<>() {};

        RequestEntity<Void> request = RequestEntity.get(apiMarketPrices)
                .accept(MediaType.APPLICATION_JSON).build();

        return restTemplate.exchange(request, responseType)
                .getBody();
    }

    @Override
    public BigDecimal getAssetPrice(String symbol) {
        Map<String, BigDecimal> assetsPrice = getAssetsPrice();

        if(assetsPrice.containsKey(symbol)) {
            return assetsPrice.get(symbol);
        }

        throw new ApiException("Asset not found", HttpStatus.NOT_FOUND);
    }
}
