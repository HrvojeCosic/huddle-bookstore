package com.huddle.huddlebookstore.service;

import com.huddle.huddlebookstore.model.Book;
import com.huddle.huddlebookstore.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository repository;

    public Flux<Book> findAvailable() {
        return repository.findByCountGreaterThan(0);
    }
}
