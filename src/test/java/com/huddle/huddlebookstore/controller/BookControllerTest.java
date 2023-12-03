package com.huddle.huddlebookstore.controller;

import com.huddle.huddlebookstore.DTO.BookDto;
import com.huddle.huddlebookstore.model.Book;
import com.huddle.huddlebookstore.model.Customer;
import com.huddle.huddlebookstore.service.BookService;
import com.huddle.huddlebookstore.service.CustomerService;
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
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
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
public class BookControllerTest {

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @Mock
    private BookService bookServiceMock;

    @Mock
    private CustomerService customerServiceMock;

    @InjectMocks
    private BookController bookController;

    private final Book book = BookCreator.createRegularBook();
    private final Customer customer = CustomerCreator.createCustomerWithNoPoints();
    private final BigDecimal booksCost = BigDecimal.valueOf(150);
    private final List<Integer> bookIds = Arrays.asList(1, 2);

    @BeforeAll
    public static void blockHoundSetup() {
        BlockHound.install();
    }

    @BeforeEach
    public void setUp() {
        BDDMockito.when(bookServiceMock.findAvailable())
                .thenReturn(Flux.just(book));

        BDDMockito.when(bookServiceMock.buy(Mockito.anyList(), Mockito.any(Customer.class)))
                .thenReturn(Mono.just(booksCost));

        BDDMockito.when(customerServiceMock.getLoyaltyPoints(customer.getId()))
                .thenReturn(Mono.just(customer.getLoyaltyPoints()));
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
    public void getAvailable_ReturnFluxOfBook_WhenSuccessful() {
        StepVerifier.create(bookController.getAvailable())
                .expectSubscription()
                .expectNext(modelMapper.map(book, BookDto.class))
                .verifyComplete();
    }

    @Test
    public void buy_ReturnTotalPrice_WhenSuccessful() {
        StepVerifier.create(bookController.buy(bookIds, customer.getId()))
                .expectSubscription()
                .expectNext(booksCost)
                .verifyComplete();
    }
}
