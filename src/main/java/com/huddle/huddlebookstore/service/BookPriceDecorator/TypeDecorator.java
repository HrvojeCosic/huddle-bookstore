package com.huddle.huddlebookstore.service.BookPriceDecorator;

import com.huddle.huddlebookstore.service.BookTypeStrategy.BookTypeDiscountStrategy;
import com.huddle.huddlebookstore.service.BookTypeStrategy.BookTypeDiscountStrategyFactory;

import java.math.BigDecimal;

public class TypeDecorator extends BaseBookPriceDecorator {

    public TypeDecorator(IBookPurchaseInfo wrapped) {
        super(wrapped);
    }

    @Override
    public BigDecimal getPrice() {
        BookTypeDiscountStrategy discountStrategy = BookTypeDiscountStrategyFactory.create(super.getBookType());
        return super.getPrice().multiply(discountStrategy.getBaseDiscount());
    }
}
