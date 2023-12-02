package com.huddle.huddlebookstore.service.BookTypeStrategy;

import com.huddle.huddlebookstore.model.BookType;

public class BookTypeDiscountStrategyFactory {

    public static BookTypeDiscountStrategy create(BookType request) {
        return switch (request) {
            case REGULAR -> new RegularBookDiscountStrategy();
            case OLD_EDITION -> new OldBookDiscountStrategy();
            case NEW_RELEASE -> new NewBookDiscountStrategy();
        };
    }
}
