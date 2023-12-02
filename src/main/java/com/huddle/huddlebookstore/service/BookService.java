package com.huddle.huddlebookstore.service;

import com.huddle.huddlebookstore.model.Book;
import com.huddle.huddlebookstore.repository.BookRepository;
import com.huddle.huddlebookstore.repository.CustomerRepository;
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

    public Mono<BigDecimal> buy(List<Integer> bookIds, Integer customerId) {
        Flux<Book> books = bookRepository.findAllById(bookIds);

        return books.flatMap(book -> {
            BookPurchaseInfo basePrice = new BookPurchaseInfo(book.getBasePrice(), book.getType());

            LoyaltyPointsDecorator decoratedPrice = new LoyaltyPointsDecorator(
                    new TypeDecorator(
                            new BundleDecorator(basePrice, bookIds.size())
                    )
                    , customerRepository, customerId);

            return Mono.just(decoratedPrice.getPrice());
        }).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
