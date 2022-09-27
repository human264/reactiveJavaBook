package com.example.chapter2;



import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataMongoTest
public class EmbeddedImageRepositoryTest {
    @Autowired
    ImageRepository repository;

    @Autowired
    MongoOperations operations;
    // end::1[]
    @Before
    public void setUp() {
        operations.dropCollection(Image.class);

        operations.insert(new Image("1",
                "learning-spring-boot-cover.jpg"));
        operations.insert(new Image("2",
                "learning-spring-boot-2nd-edition-cover.jpg"));
        operations.insert(new Image("3",
                "bazinga.png"));

        operations.findAll(Image.class).forEach(image -> {
            System.out.println(image.toString());
        });
    }
    // end::2[]

    // tag::3[]
    @Test
    public void findAllShouldWork() {
        Flux<Image> images = repository.findAll();

        StepVerifier.create(images)
                .recordWith(ArrayList::new)
                .expectNextCount(3)
                .consumeRecordedWith(results -> {
                    assertThat(results).hasSize(3);
                    assertThat(results)
                            .extracting(Image::getName)
                            .contains(
                                    "learning-spring-boot-cover.jpg",
                                    "learning-spring-boot-2nd-edition-cover.jpg",
                                    "bazinga.png");
                })
                .expectComplete()
                .verify();
    }
}
