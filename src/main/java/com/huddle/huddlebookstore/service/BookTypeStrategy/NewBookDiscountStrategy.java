package com.huddle.huddlebookstore.service.BookTypeStrategy;

import java.math.BigDecimal;

public class NewBookDiscountStrategy extends BookTypeDiscountStrategy {

    @Override
    public BigDecimal getBundleDiscount() {
        return BigDecimal.ONE;
    }

    @Override
    public BigDecimal getLoyaltyPointsDiscount() {
        return BigDecimal.ONE;
    }
}
