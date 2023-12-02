package com.huddle.huddlebookstore.service.BookPriceDecorator;

import com.huddle.huddlebookstore.model.BookType;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class BaseBookPriceDecorator implements IBookPurchaseInfo {

    private final IBookPurchaseInfo wrapped;

    @Override
    public BigDecimal getPrice() {
        return wrapped.getPrice();
    }

    @Override
    public BookType getBookType() {
        return wrapped.getBookType();
    }
}
