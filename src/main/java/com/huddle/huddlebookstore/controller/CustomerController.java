package com.huddle.huddlebookstore.controller;

import com.huddle.huddlebookstore.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @GetMapping("/{customerId}")
    Mono<Integer> getLoyaltyPoints(@PathVariable Integer customerId) {
        return service.getLoyaltyPoints(customerId);
    }
}
