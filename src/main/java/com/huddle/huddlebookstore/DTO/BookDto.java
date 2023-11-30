package com.huddle.huddlebookstore.DTO;

import com.huddle.huddlebookstore.model.BookType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookDto {

    private Integer count;
    private String title;
    private BookType type;
    private BigDecimal basePrice;
}
