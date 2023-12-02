package com.huddle.huddlebookstore.controller;

import com.huddle.huddlebookstore.DTO.BookDto;
import com.huddle.huddlebookstore.exception.ExceptionMessage;
import com.huddle.huddlebookstore.model.Book;
import com.huddle.huddlebookstore.model.Customer;
import com.huddle.huddlebookstore.service.BookService;
import com.huddle.huddlebookstore.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final CustomerService customerService;
    private final ModelMapper modelMapper;

    @GetMapping
    public Flux<BookDto> getAvailable() {
        Flux<Book> books = bookService.findAvailable();
        return books.map(this::convertToDto);
    }

    @PostMapping("/buy/{customerId}")
    public Mono<BigDecimal> buy(@RequestBody List<Integer> bookIds,
                                @PathVariable Integer customerId) {
        return customerService.getLoyaltyPoints(customerId)
                .switchIfEmpty(ExceptionMessage.getMonoResponseStatusNotFoundException("Customer"))
                .flatMap(points -> bookService.buy(bookIds, new Customer(customerId, points)));
    }

    public BookDto convertToDto(Book book) {
        return modelMapper.map(book, BookDto.class);
    }
}
