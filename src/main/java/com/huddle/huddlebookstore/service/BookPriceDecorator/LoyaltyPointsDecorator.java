package com.huddle.huddlebookstore.service.BookPriceDecorator;

import com.huddle.huddlebookstore.service.BookTypeStrategy.BookTypeDiscountStrategyFactory;

import java.math.BigDecimal;
import java.util.Objects;

public class LoyaltyPointsDecorator extends BaseBookPriceDecorator {

    private final int customerPoints;
    private final int pointDiscountThreshold = 10;
    private boolean loyaltyPointsUsed = false;

    public LoyaltyPointsDecorator(IBookPurchaseInfo wrapped, int customerPoints) {
        super(wrapped);
        this.customerPoints = customerPoints;
    }

    @Override
    public BigDecimal getPrice() {
        BigDecimal discount = BookTypeDiscountStrategyFactory
                .create(super.getBookType())
                .getLoyaltyPointsDiscount();

        boolean discountAvailable = !Objects.equals(discount, BigDecimal.ONE);
        if (customerPoints < pointDiscountThreshold || !discountAvailable) {
            return super.getPrice();
        } else {
            loyaltyPointsUsed = true;
            return super.getPrice().multiply(discount);
        }
    }

    public boolean loyaltyPointsUsed() {
        return loyaltyPointsUsed;
    }
}
