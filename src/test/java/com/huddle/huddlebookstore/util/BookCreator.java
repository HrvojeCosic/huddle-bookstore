package com.huddle.huddlebookstore.util;

import com.huddle.huddlebookstore.model.Book;
import com.huddle.huddlebookstore.model.BookType;

import java.math.BigDecimal;

public class BookCreator {

    public static Book createRegularBook() {
        return Book.builder()
                .id(1)
                .count(10)
                .title("Homo deus")
                .type(BookType.REGULAR)
                .basePrice(BigDecimal.valueOf(99.99))
                .build();
    }

    public static Book createNewReleaseBook() {
        return Book.builder()
                .id(2)
                .count(4)
                .title("Sapiens")
                .type(BookType.NEW_RELEASE)
                .basePrice(BigDecimal.valueOf(199.99))
                .build();
    }
}
