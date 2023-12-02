package com.huddle.huddlebookstore.service.BookPriceDecorator;

import com.huddle.huddlebookstore.model.BookType;

import java.math.BigDecimal;

public interface IBookPurchaseInfo {

    BigDecimal getPrice();

    BookType getBookType();
}
