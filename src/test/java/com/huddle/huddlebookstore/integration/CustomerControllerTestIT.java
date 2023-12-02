package com.huddle.huddlebookstore.integration;

import com.huddle.huddlebookstore.repository.CustomerRepository;
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
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class CustomerControllerTestIT {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CustomerRepository customerRepositoryMock;

    private final int mockCustomerPoints = 123;
    private final int customerId = 1;
    private final int invalidCustomerId = -1;

    @BeforeEach
    public void setUp() {
        BDDMockito.when(customerRepositoryMock.findLoyaltyPointsForCustomerId(invalidCustomerId))
                .thenReturn(Mono.empty());

        BDDMockito.when(customerRepositoryMock.findLoyaltyPointsForCustomerId(customerId))
                .thenReturn(Mono.just(mockCustomerPoints));
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
