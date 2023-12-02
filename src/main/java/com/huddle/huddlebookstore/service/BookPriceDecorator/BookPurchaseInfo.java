package com.huddle.huddlebookstore.service.BookPriceDecorator;

import com.huddle.huddlebookstore.model.BookType;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class BookPurchaseInfo implements IBookPurchaseInfo {

    private final BigDecimal basePrice;
    private final BookType bookType;

    @Override
    public BigDecimal getPrice() {
        return this.basePrice;
    }

    @Override
    public BookType getBookType() {
        return this.bookType;
    }
}
