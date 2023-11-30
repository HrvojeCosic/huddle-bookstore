package com.huddle.huddlebookstore.repository;

import com.huddle.huddlebookstore.model.Book;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface BookRepository extends ReactiveCrudRepository<Book, Integer> {

    Flux<Book> findByCountGreaterThan(int threshold);
}

