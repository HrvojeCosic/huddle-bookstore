package com.huddle.huddlebookstore.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "books")
public class Book {

    @Id
    private Integer id;

    @Setter
    private BigDecimal basePrice;

    @Setter
    private Integer count;

    private String title;

    private BookType type;
}
