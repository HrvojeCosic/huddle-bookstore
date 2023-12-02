package com.huddle.huddlebookstore.util;

import com.huddle.huddlebookstore.model.Customer;

public class CustomerCreator {

    public static Customer createCustomerWith10Points() {
        return Customer.builder()
                .id(1)
                .loyaltyPoints(10)
                .build();
    }

    public static Customer createCustomerWithNoPoints() {
        return Customer.builder()
                .id(2)
                .loyaltyPoints(0)
                .build();
    }
}
