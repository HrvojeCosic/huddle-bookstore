package com.huddle.huddlebookstore.service.BookPriceDecorator;

import com.huddle.huddlebookstore.service.BookTypeStrategy.BookTypeDiscountStrategy;
import com.huddle.huddlebookstore.service.BookTypeStrategy.BookTypeDiscountStrategyFactory;

import java.math.BigDecimal;

public class LoyaltyPointsDecorator extends BaseBookPriceDecorator {

    private final int customerPoints;
    private final int pointDiscountThreshold = 10;

    public LoyaltyPointsDecorator(IBookPurchaseInfo wrapped, int customerPoints) {
        super(wrapped);
        this.customerPoints = customerPoints;
    }

    @Override
    public BigDecimal getPrice() {
        if (customerPoints < pointDiscountThreshold) {
            return super.getPrice();
        } else {
            BookTypeDiscountStrategy discountStrategy = BookTypeDiscountStrategyFactory.create(super.getBookType());
            return super.getPrice().multiply(discountStrategy.getLoyaltyPointsDiscount());
        }
    }

    public boolean loyaltyPointsUsed() {
        return customerPoints >= pointDiscountThreshold;
    }
}
