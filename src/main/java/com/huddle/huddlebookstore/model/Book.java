package com.huddle.huddlebookstore.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
