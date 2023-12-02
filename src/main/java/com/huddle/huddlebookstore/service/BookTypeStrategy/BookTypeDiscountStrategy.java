package com.huddle.huddlebookstore.service.BookTypeStrategy;

import java.math.BigDecimal;

public abstract class BookTypeDiscountStrategy {

    public BigDecimal getBaseDiscount() {
        return BigDecimal.ONE;
    }

    abstract public BigDecimal getBundleDiscount();

    public BigDecimal getLoyaltyPointsDiscount() {
        return BigDecimal.ZERO;
    }
}
