package com.huddle.huddlebookstore.service;

import com.huddle.huddlebookstore.exception.ExceptionMessage;
import com.huddle.huddlebookstore.model.Book;
import com.huddle.huddlebookstore.model.Customer;
import com.huddle.huddlebookstore.repository.BookRepository;
import com.huddle.huddlebookstore.service.BookPriceDecorator.BookDiscountInfo;
import com.huddle.huddlebookstore.service.BookPriceDecorator.BookPurchaseInfo;
import com.huddle.huddlebookstore.service.BookPriceDecorator.BundleDecorator;
import com.huddle.huddlebookstore.service.BookPriceDecorator.LoyaltyPointsDecorator;
import com.huddle.huddlebookstore.service.BookPriceDecorator.TypeDecorator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final CustomerService customerService;

    public Flux<Book> findAvailable() {
        return bookRepository.findByCountGreaterThan(0);
    }

    @Transactional
    public Mono<BigDecimal> buy(List<Integer> bookIds, Customer customer) {
        Flux<BigDecimal> priceFlux = bookRepository.findAllById(bookIds)
                .switchIfEmpty(ExceptionMessage.getMonoResponseStatusNotFoundException("Books"))
                .flatMap(book -> {
                    BookPurchaseInfo basePurchaseInfo = new BookPurchaseInfo(book.getBasePrice(), book.getType());
                    BookDiscountInfo finalPurchaseInfo = getFinalPrice(basePurchaseInfo, customer.getLoyaltyPoints(), bookIds.size());
                    return updateCustomerLoyaltyPoints(finalPurchaseInfo, customer)
                            .thenMany(Flux.just(finalPurchaseInfo.discountedPrice()));
                });

        return priceFlux.reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BookDiscountInfo getFinalPrice(BookPurchaseInfo basePurchaseInfo, int loyaltyPoints, int bundleSize) {
        LoyaltyPointsDecorator decoratedPrice =
                new LoyaltyPointsDecorator(
                        new TypeDecorator(
                                new BundleDecorator(basePurchaseInfo, bundleSize)
                        ), loyaltyPoints
                );

        return new BookDiscountInfo(decoratedPrice.getPrice(), decoratedPrice.loyaltyPointsUsed());
    }

    private Mono<Void> updateCustomerLoyaltyPoints(BookDiscountInfo discountInfo, Customer customer) {
        if (discountInfo.loyaltyPointsUsed()) {
            customer.setLoyaltyPoints(customer.getLoyaltyPoints() - 10);
        }

        customer.setLoyaltyPoints(customer.getLoyaltyPoints() + 1);
        return customerService.update(customer);
    }
}
