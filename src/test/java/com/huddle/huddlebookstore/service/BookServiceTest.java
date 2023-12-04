package com.huddle.huddlebookstore.service;

import com.huddle.huddlebookstore.model.Book;
import com.huddle.huddlebookstore.model.BookType;
import com.huddle.huddlebookstore.model.Customer;
import com.huddle.huddlebookstore.repository.BookRepository;
import com.huddle.huddlebookstore.repository.CustomerRepository;
import com.huddle.huddlebookstore.service.BookTypeStrategy.BookTypeDiscountStrategy;
import com.huddle.huddlebookstore.service.BookTypeStrategy.BookTypeDiscountStrategyFactory;
import com.huddle.huddlebookstore.util.BookCreator;
import com.huddle.huddlebookstore.util.CustomerCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ExtendWith(SpringExtension.class)
public class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private CustomerService customerServiceMock;

    @Mock
    private BookRepository bookRepositoryMock;

    @Mock
    private CustomerRepository customerRepository;

    private final Book book = BookCreator.createRegularBook();
    private final Customer customer = CustomerCreator.createCustomerWithNoPoints();
    private final List<Integer> bookIds = Arrays.asList(1, 2);

    @BeforeAll
    public static void blockHoundSetup() {
        BlockHound.install();
    }

    @BeforeEach
    public void setUp() {
        BDDMockito.when(customerRepository.findLoyaltyPointsById(customer.getId()))
                .thenReturn(Mono.just(0));

        BDDMockito.when(bookRepositoryMock.findByCountGreaterThan(0))
                .thenReturn(Flux.just(book));

        BDDMockito.when(bookRepositoryMock.findAllById(bookIds))
                .thenReturn(Flux.just(book));

        BDDMockito.when(customerServiceMock.update(Mockito.any(Customer.class)))
                .thenReturn(Mono.empty());
    }

    @Test
    public void blockHoundWorks() throws TimeoutException, InterruptedException {
        try {
            FutureTask<?> task = new FutureTask<Object>(() -> {
                Thread.sleep(0);
                return "";
            });
            Schedulers.parallel().schedule(task);

            task.get(10, TimeUnit.SECONDS);
            Assertions.fail("should fail");
        } catch (ExecutionException e) {
            Assertions.assertTrue(e.getCause() instanceof BlockingOperationError);
        }
    }

    @Test
    public void findAvailable_ReturnsFluxBook_WhenSuccessful() {
        StepVerifier.create(bookService.findAvailable())
                .expectSubscription()
                .expectNext(book)
                .verifyComplete();
    }

    @Test
    public void buy_ReturnsZeroAndDeductsLoyaltyPoints_WhenCustomerHasEnoughPointsForFreeBook() {
        Customer customer = CustomerCreator.createCustomerWith10Points();

        StepVerifier.create(bookService.buy(bookIds, customer))
                .expectSubscription()
                .expectNextMatches(value -> value.compareTo(BigDecimal.ZERO) == 0)
                .verifyComplete();

        BDDMockito.verify(customerServiceMock, Mockito.times(1))
                .update(customer);
    }

    @Test
    public void buy_ReturnsFullPrice_WhenCustomerHasEnoughPointsForFreeBookButBookIsNewRelease() {
        Book newReleaseBook = BookCreator.createNewReleaseBook();

        BDDMockito.when(customerRepository.findLoyaltyPointsById(customer.getId()))
                .thenReturn(Mono.just(10));

        BDDMockito.when(bookRepositoryMock.findAllById(bookIds))
                .thenReturn(Flux.just(newReleaseBook));

        StepVerifier.create(bookService.buy(bookIds, customer))
                .expectSubscription()
                .expectNext(newReleaseBook.getBasePrice())
                .verifyComplete();
    }

    @Test
    public void buy_ReturnsDiscountedPrice_WhenCustomerBuysOldEditionBook() {
        Book oldEditionBook = BookCreator.createOldEditionBook();

        BDDMockito.when(bookRepositoryMock.findAllById(bookIds))
                .thenReturn(Flux.just(oldEditionBook));

        BigDecimal oldBookDiscount = BookTypeDiscountStrategyFactory.create(BookType.OLD_EDITION).getBaseDiscount();
        assert oldBookDiscount.compareTo(BigDecimal.ZERO) > 0;
        BigDecimal expectedPrice = oldEditionBook
                .getBasePrice()
                .multiply(oldBookDiscount);

        StepVerifier.create(bookService.buy(bookIds, customer))
                .expectSubscription()
                .expectNext(expectedPrice)
                .verifyComplete();
    }

    @Test
    public void buy_ReturnsDiscountedPrice_WhenBundleDiscountApplies() {
        List<Integer> bookIds = Arrays.asList(1, 2, 3);

        List<Book> books = Arrays.asList(
                BookCreator.createOldEditionBook(),
                BookCreator.createNewReleaseBook(),
                BookCreator.createRegularBook());

        Flux<Book> bookFlux = Flux.fromIterable(books);

        BigDecimal expectedPrice = books.stream()
                .map(book -> {
                    BookTypeDiscountStrategy strategy = BookTypeDiscountStrategyFactory.create(book.getType());
                    return book.getBasePrice()
                            .multiply(strategy.getBundleDiscount())
                            .multiply(strategy.getBaseDiscount());
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BDDMockito.when(bookRepositoryMock.findAllById(bookIds))
                .thenReturn(bookFlux);

        StepVerifier.create(bookService.buy(bookIds, customer))
                .expectSubscription()
                .expectNext(expectedPrice)
                .verifyComplete();
    }

    @Test
    public void buy_AddsLoyaltyPointAndReturnsFullPrice_WhenCustomerDoesntMeetAnyDiscountConditions() {
        StepVerifier.create(bookService.buy(bookIds, customer))
                .expectSubscription()
                .expectNext(book.getBasePrice())
                .verifyComplete();

        BDDMockito.verify(customerServiceMock, Mockito.times(1))
                .update(customer);
    }
}
