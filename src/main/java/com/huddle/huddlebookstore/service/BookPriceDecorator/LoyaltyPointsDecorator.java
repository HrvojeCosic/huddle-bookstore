package com.huddle.huddlebookstore.service.BookPriceDecorator;

import com.huddle.huddlebookstore.model.BookType;
import com.huddle.huddlebookstore.repository.CustomerRepository;
import com.huddle.huddlebookstore.service.BookTypeStrategy.BookTypeDiscountStrategy;
import com.huddle.huddlebookstore.service.BookTypeStrategy.BookTypeDiscountStrategyFactory;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public class LoyaltyPointsDecorator extends BaseBookPriceDecorator {

    private final CustomerRepository customerRepository;
    private final Integer customerId;

    public LoyaltyPointsDecorator(IBookPurchaseInfo wrapped, CustomerRepository customerRepository, Integer customerId) {
        super(wrapped);
        this.customerRepository = customerRepository;
        this.customerId = customerId;
    }

    @Override
    public BigDecimal getPrice() {
        BigDecimal initialPrice = super.getPrice();
        BookType bookType = super.getBookType();

        return customerRepository.findLoyaltyPointsForCustomerId(customerId)
                .flatMap(points -> {
                    final int pointDiscountThreshold = 10;

                    if (points < pointDiscountThreshold) {
                        return Mono.just(initialPrice);
                    }

                    BookTypeDiscountStrategy discountStrategy = BookTypeDiscountStrategyFactory.create(bookType);
                    return customerRepository
                            .updateLoyaltyPoints(customerId, 0)
                            .thenReturn(initialPrice.multiply(discountStrategy.getLoyaltyPointsDiscount()));
                }).block();
    }
}
