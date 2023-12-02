package com.huddle.huddlebookstore.service.BookTypeStrategy;

import java.math.BigDecimal;

public class RegularBookDiscountStrategy extends BookTypeDiscountStrategy {

    @Override
    public BigDecimal getBundleDiscount() {
        return BigDecimal.valueOf(0.90);
    }
}
