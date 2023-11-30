package com.huddle.huddlebookstore.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Table(name = "customers")
public class Customer {

    @Id
    private Integer id;

    @Setter
    private Integer loyaltyPoints;
}
