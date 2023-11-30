package com.huddle.huddlebookstore.controller;

import com.huddle.huddlebookstore.model.Book;
import com.huddle.huddlebookstore.DTO.BookDto;
import com.huddle.huddlebookstore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService service;

    private final ModelMapper modelMapper;

    @GetMapping
    Flux<BookDto> getAvailable() {
        Flux<Book> books = service.findAvailable();
        return books.map(this::convertToDto);
    }

    private BookDto convertToDto(Book book) {
        return modelMapper.map(book, BookDto.class);
    }
}
