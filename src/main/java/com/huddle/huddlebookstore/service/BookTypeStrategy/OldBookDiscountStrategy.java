package com.huddle.huddlebookstore.service.BookTypeStrategy;

import java.math.BigDecimal;

public class OldBookDiscountStrategy extends BookTypeDiscountStrategy {

    @Override
    public BigDecimal getBaseDiscount() {
        return BigDecimal.valueOf(0.80);
    }

    @Override
    public BigDecimal getBundleDiscount() {
        return BigDecimal.valueOf(0.95);
    }
}
