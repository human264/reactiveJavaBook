package com.example.chapter2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

@RestController
public class ApiController {
    private static final Logger log = LoggerFactory.getLogger(ApiController.class);

    private static final String API_BASE_PATH = "/api";

    // tag::get[]


    @PostMapping(API_BASE_PATH + "/images")
    Mono<Void> create(@RequestPart Flux<FilePart> images) {
        Hooks.onOperatorDebug();

        return images
                .map(image -> {
                    log.info("We will save " + image + " to a Reactive database soon!");
                    return image;
                })
                .then();
    }


}
