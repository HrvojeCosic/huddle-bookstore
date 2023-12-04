package com.huddle.huddlebookstore.integration;

import com.huddle.huddlebookstore.model.Book;
import com.huddle.huddlebookstore.model.Customer;
import com.huddle.huddlebookstore.repository.BookRepository;
import com.huddle.huddlebookstore.repository.CustomerRepository;
import com.huddle.huddlebookstore.service.CustomerService;
import com.huddle.huddlebookstore.util.BookCreator;
import com.huddle.huddlebookstore.util.CustomerCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class BookControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BookRepository bookRepositoryMock;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private CustomerService customerServiceMock;

    private final Flux<Book> bookFlux = Flux.just(BookCreator.createRegularBook(), BookCreator.createRegularBook());
    private final List<Integer> bookIds = Arrays.asList(1, 2);
    private final List<Integer> invalidBookIds = Arrays.asList(-1, -2);
    private final int customerId = 1;
    private final int invalidCustomerId = -1;

    @BeforeAll
    public static void blockHoundSetup() {
        BlockHound.install();
    }

    @BeforeEach
    public void setUp() {
        BDDMockito.when(bookRepositoryMock.findByCountGreaterThan(0))
                .thenReturn(bookFlux);

        BDDMockito.when(bookRepositoryMock.findAllById(bookIds))
                .thenReturn(Flux.just(BookCreator.createRegularBook()));

        BDDMockito.when(bookRepositoryMock.findAllById(invalidBookIds))
                .thenReturn(Flux.empty());

        BDDMockito.when(customerRepository.findById(customerId))
                .thenReturn(Mono.just(CustomerCreator.createCustomerWithNoPoints()));

        BDDMockito.when(customerRepository.findLoyaltyPointsById(customerId))
                .thenReturn(Mono.just(0));

        BDDMockito.when(customerRepository.findLoyaltyPointsById(invalidCustomerId))
                .thenReturn(Mono.empty());

        BDDMockito.when(customerServiceMock.update(Mockito.any(Customer.class)))
                .thenReturn(Mono.empty());

        BDDMockito.when(customerServiceMock.update(Mockito.any(Customer.class)))
                .thenReturn(Mono.empty());

        BDDMockito.when(customerServiceMock.getLoyaltyPoints(customerId))
                .thenReturn(Mono.just(10));

        BDDMockito.when(customerServiceMock.getLoyaltyPoints(invalidCustomerId))
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
    public void getAvailable_ReturnsFluxBook_WhenSuccessful() {
        Book bookExpected1 = BookCreator.createRegularBook();
        Book bookExpected2 = BookCreator.createRegularBook();

        webTestClient.get()
                .uri("/api/v1/books")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$[0].id").isEqualTo(bookExpected1.getId())
                .jsonPath("$[0].basePrice").isEqualTo(bookExpected1.getBasePrice())
                .jsonPath("$[0].count").isEqualTo(bookExpected1.getCount())
                .jsonPath("$[0].title").isEqualTo(bookExpected1.getTitle())
                .jsonPath("$[0].type").isEqualTo(bookExpected1.getType().toString())
                .jsonPath("$[1].id").isEqualTo(bookExpected2.getId())
                .jsonPath("$[1].basePrice").isEqualTo(bookExpected2.getBasePrice())
                .jsonPath("$[1].count").isEqualTo(bookExpected2.getCount())
                .jsonPath("$[1].title").isEqualTo(bookExpected2.getTitle())
                .jsonPath("$[1].type").isEqualTo(bookExpected2.getType().toString());
    }

    @Test
    public void getAvailable_ReturnsEmptyFlux_WhenNoBooksAvailable() {
        BDDMockito.when(bookRepositoryMock.findByCountGreaterThan(0)).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/api/v1/books")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$").isEmpty();
    }

    @Test
    public void getAvailable_ReturnsJsonContentType() {
        webTestClient.get()
                .uri("/api/v1/books")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    public void buy_ReturnsTotalPrice_WhenSuccessful() {
        webTestClient.post()
                .uri(String.format("/api/v1/books/buy/%s", customerId))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bookIds)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BigDecimal.class);
    }

    @Test
    public void buy_ReturnsNotFound_WhenInvalidCustomerId() {
        webTestClient.post()
                .uri(String.format("/api/v1/books/buy/%s", invalidCustomerId))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bookIds)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void buy_ReturnsNotFound_WhenInvalidBookIds() {
        webTestClient.post()
                .uri(String.format("/api/v1/books/buy/%s", invalidCustomerId))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidBookIds)
                .exchange()
                .expectStatus().isNotFound();
    }
}
