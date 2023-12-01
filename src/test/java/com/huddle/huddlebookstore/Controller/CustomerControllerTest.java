package com.huddle.huddlebookstore.Controller;

import com.huddle.huddlebookstore.controller.CustomerController;
import com.huddle.huddlebookstore.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CustomerService customerService;

    @Test
    public void testGetLoyaltyPoints() {
        int mockPoints = 123;
        Mono<Integer> loyaltyPointsFlux = Mono.just(mockPoints);
        Mockito.when(customerService.getLoyaltyPoints(1)).thenReturn(loyaltyPointsFlux);

        webTestClient.get()
                .uri("/api/v1/customers/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .isEqualTo(mockPoints);
    }
}
