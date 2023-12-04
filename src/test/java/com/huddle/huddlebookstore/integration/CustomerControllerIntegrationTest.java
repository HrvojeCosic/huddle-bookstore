package com.huddle.huddlebookstore.integration;

import com.huddle.huddlebookstore.repository.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class CustomerControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CustomerRepository customerRepositoryMock;

    private final int mockCustomerPoints = 123;
    private final int customerId = 1;
    private final int invalidCustomerId = -1;

    @BeforeAll
    public static void blockHoundSetup() {
        BlockHound.install();
    }

    @BeforeEach
    public void setUp() {
        BDDMockito.when(customerRepositoryMock.findLoyaltyPointsById(invalidCustomerId))
                .thenReturn(Mono.empty());

        BDDMockito.when(customerRepositoryMock.findLoyaltyPointsById(customerId))
                .thenReturn(Mono.just(mockCustomerPoints));
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
    public void getLoyaltyPoints_ReturnsCustomerPoints_WhenSuccessful() {
        webTestClient.get()
                .uri("/api/v1/customers/" + customerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .isEqualTo(mockCustomerPoints);
    }
    @Test
    public void getLoyaltyPoints_ReturnsNotFound_WhenInvalidCustomerId() {
        webTestClient.get()
                .uri("/api/v1/customers/" + invalidCustomerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }
}
