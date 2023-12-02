package com.huddle.huddlebookstore.service;

import com.huddle.huddlebookstore.model.Book;
import com.huddle.huddlebookstore.model.Customer;
import com.huddle.huddlebookstore.repository.BookRepository;
import com.huddle.huddlebookstore.repository.CustomerRepository;
import com.huddle.huddlebookstore.util.BookCreator;
import com.huddle.huddlebookstore.util.CustomerCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@ExtendWith(SpringExtension.class)
public class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepositoryMock;

    @Mock
    private CustomerRepository customerRepository;

    private final Book book = BookCreator.createRegularBook();
    private final Customer customer = CustomerCreator.createCustomerWithNoPoints();
    private final List<Integer> bookIds = Arrays.asList(1, 2);

@BeforeEach
public void setUp() {
    BDDMockito.when(customerRepository.findLoyaltyPointsForCustomerId(customer.getId()))
            .thenReturn(Mono.just(0));

    BDDMockito.when(customerRepository.updateLoyaltyPoints(customer.getId(), 0))
            .thenReturn(Flux.just().then());

    BDDMockito.when(bookRepositoryMock.findByCountGreaterThan(0))
            .thenReturn(Flux.just(book));

    BDDMockito.when(bookRepositoryMock.findAllById(bookIds))
            .thenReturn(Flux.just(book));
}

    @Test
    public void findAvailable_ReturnsFluxBook_WhenSuccessful() {
        StepVerifier.create(bookService.findAvailable())
                .expectSubscription()
                .expectNext(book)
                .verifyComplete();
    }

    @Test
    public void buy_Returns0_WhenCustomerHasEnoughPointsForFreeBook() {
        StepVerifier.create(bookService.buy(bookIds, CustomerCreator.createCustomerWith10Points()))
                .expectSubscription()
                .expectNextMatches(value -> value.compareTo(BigDecimal.ZERO) == 0)
                .verifyComplete();
    }

    @Test
    public void buy_ReturnsFullPrice_WhenCustomerHasEnoughPointsForFreeBookButBookIsNewRelease() {
        Book newReleaseBook = BookCreator.createNewReleaseBook();

        BDDMockito.when(customerRepository.findLoyaltyPointsForCustomerId(customer.getId()))
                .thenReturn(Mono.just(10));

        BDDMockito.when(bookRepositoryMock.findAllById(bookIds))
                .thenReturn(Flux.just(newReleaseBook));

        StepVerifier.create(bookService.buy(bookIds, customer))
                .expectSubscription()
                .expectNext(newReleaseBook.getBasePrice())
                .verifyComplete();
    }

    @Test
    public void buy_ReturnsFullPrice_WhenCustomerDoesntHaveEnoughPointsForFreeBook() {
        StepVerifier.create(bookService.buy(bookIds, customer))
                .expectSubscription()
                .expectNext(book.getBasePrice())
                .verifyComplete();
    }
}
