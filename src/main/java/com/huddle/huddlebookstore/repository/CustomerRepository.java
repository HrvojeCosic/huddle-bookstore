package com.huddle.huddlebookstore.repository;

import com.huddle.huddlebookstore.model.Customer;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {

    @Query("SELECT e.loyalty_points FROM customers e WHERE e.id = :customerId")
    Mono<Integer> findLoyaltyPointsForCustomerId(@Param("customerId") Integer customerId);
}
