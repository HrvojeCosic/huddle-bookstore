package com.huddle.huddlebookstore.repository;

import com.huddle.huddlebookstore.model.Customer;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {

    @Query("SELECT e.loyalty_points FROM customers e WHERE e.id = :customerId")
    Mono<Integer> findLoyaltyPointsForCustomerId(@Param("customerId") Integer customerId);

    @Modifying
    @Query("UPDATE customers SET loyalty_points = loyalty_points - :points WHERE id = :customerId")
    void deductLoyaltyPoints(@Param("customerId") Integer customerId, @Param("points") Integer points);

    @Modifying
    @Query("UPDATE customers SET loyalty_points = loyalty_points + :points WHERE id = :customerId")
    void addLoyaltyPoints(@Param("customerId") Integer customerId, @Param("points") Integer points);}
