package com.huddle.huddlebookstore.service;

import com.huddle.huddlebookstore.exception.ExceptionMessage;
import com.huddle.huddlebookstore.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;

    public Mono<Integer> getLoyaltyPoints(Integer customerId) {
        return repository.findLoyaltyPointsForCustomerId(customerId)
                .switchIfEmpty(ExceptionMessage.getMonoResponseStatusNotFoundException("Customer"));
    }
}
