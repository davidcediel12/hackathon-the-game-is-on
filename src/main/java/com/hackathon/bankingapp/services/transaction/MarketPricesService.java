package com.hackathon.bankingapp.services.transaction;

import java.math.BigDecimal;
import java.util.Map;

public interface MarketPricesService {


    Map<String, BigDecimal> getAssetsPrice();

    BigDecimal getAssetPrice(String symbol);
}
