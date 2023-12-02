package com.huddle.huddlebookstore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

public class ExceptionMessage {

    public static <T> Mono<T> getMonoResponseStatusNotFoundException(String object) {
        return Mono.error(
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("%s not found", object))
        );
    }
}
