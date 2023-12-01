package com.huddle.huddlebookstore.Controller;

import com.huddle.huddlebookstore.controller.BookController;
import com.huddle.huddlebookstore.model.Book;
import com.huddle.huddlebookstore.model.BookType;
import com.huddle.huddlebookstore.service.BookService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;

@WebFluxTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BookService bookService;

    @Test
    public void testGetAvailable() {
        Book book1 = new Book(1, BigDecimal.valueOf(100), 10, "Sapiens", BookType.OLD_EDITION);
        Book book2 = new Book(2, BigDecimal.valueOf(250), 4, "Homo deus", BookType.REGULAR);
        Flux<Book> bookFlux = Flux.just(book1, book2);
        Mockito.when(bookService.findAvailable()).thenReturn(bookFlux);

        webTestClient.get()
                .uri("/api/v1/books")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$[0].id").isEqualTo(book1.getId())
                .jsonPath("$[0].basePrice").isEqualTo(book1.getBasePrice())
                .jsonPath("$[0].count").isEqualTo(book1.getCount())
                .jsonPath("$[0].title").isEqualTo(book1.getTitle())
                .jsonPath("$[0].type").isEqualTo(book1.getType().toString())
                .jsonPath("$[1].id").isEqualTo(book2.getId())
                .jsonPath("$[1].basePrice").isEqualTo(book2.getBasePrice())
                .jsonPath("$[1].count").isEqualTo(book2.getCount())
                .jsonPath("$[1].title").isEqualTo(book2.getTitle())
                .jsonPath("$[1].type").isEqualTo(book2.getType().toString());
    }
}
