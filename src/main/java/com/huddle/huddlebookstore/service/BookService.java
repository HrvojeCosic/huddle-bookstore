package com.huddle.huddlebookstore.service;

import com.huddle.huddlebookstore.exception.ExceptionMessage;
import com.huddle.huddlebookstore.model.Book;
import com.huddle.huddlebookstore.model.Customer;
import com.huddle.huddlebookstore.repository.BookRepository;
import com.huddle.huddlebookstore.repository.CustomerRepository;
import com.huddle.huddlebookstore.service.BookPriceDecorator.BookDiscountInfo;
import com.huddle.huddlebookstore.service.BookPriceDecorator.BookPurchaseInfo;
import com.huddle.huddlebookstore.service.BookPriceDecorator.BundleDecorator;
import com.huddle.huddlebookstore.service.BookPriceDecorator.LoyaltyPointsDecorator;
import com.huddle.huddlebookstore.service.BookPriceDecorator.TypeDecorator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final CustomerRepository customerRepository;

    public Flux<Book> findAvailable() {
        return bookRepository.findByCountGreaterThan(0);
    }

    public Mono<BigDecimal> buy(List<Integer> bookIds, Customer customer) {
        final Flux<BigDecimal> priceFlux = bookRepository.findAllById(bookIds)
                .switchIfEmpty(ExceptionMessage.getMonoResponseStatusNotFoundException("Books"))
                .flatMap(book -> {
                    final BookPurchaseInfo basePurchaseInfo = new BookPurchaseInfo(book.getBasePrice(), book.getType());
                    final BookDiscountInfo finalPurchaseInfo = getFinalPrice(basePurchaseInfo, customer.getLoyaltyPoints(), bookIds.size());

                    if (finalPurchaseInfo.loyaltyPointsUsed()) {
                        customerRepository.updateLoyaltyPoints(customer.getId(), customer.getLoyaltyPoints() - 10);
                    }

                    return Flux.just(finalPurchaseInfo.discountedPrice());
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

        return new BookDiscountInfo(decoratedPrice.loyaltyPointsUsed(), decoratedPrice.getPrice());
    }
}
