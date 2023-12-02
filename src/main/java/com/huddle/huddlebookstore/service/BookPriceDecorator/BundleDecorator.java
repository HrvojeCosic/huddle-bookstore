package com.huddle.huddlebookstore.service.BookPriceDecorator;

import com.huddle.huddlebookstore.model.BookType;
import com.huddle.huddlebookstore.service.BookTypeStrategy.BookTypeDiscountStrategy;
import com.huddle.huddlebookstore.service.BookTypeStrategy.BookTypeDiscountStrategyFactory;

import java.math.BigDecimal;

public class BundleDecorator extends BaseBookPriceDecorator {

    private final int bookCount;

    public BundleDecorator(IBookPurchaseInfo wrapped, int bookCount) {
        super(wrapped);
        this.bookCount = bookCount;
    }

    @Override
    public BigDecimal getPrice() {
        final int deductionThreshold = 3;

        BigDecimal initialPrice = super.getPrice();
        BookType bookType = super.getBookType();

        if (bookCount >= deductionThreshold) {
            BookTypeDiscountStrategy discountStrategy = BookTypeDiscountStrategyFactory.create(bookType);
            return initialPrice.multiply(discountStrategy.getBundleDiscount());
        }

        return initialPrice;
    }
}
