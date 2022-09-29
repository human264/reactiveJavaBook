/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.chapter2;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Flux;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.io.IOException;

import java.nio.charset.StandardCharsets;

/**
 * @author Greg Turnquist
 */
// tag::1[]
@RunWith(SpringRunner.class)
@WebFluxTest(controllers = HomeController.class)
@Import({ThymeleafAutoConfiguration.class})
public class HomeControllerTests {

	@Autowired
	WebTestClient webClient;

	@MockBean
	ImageService imageService;

	@Test
	public void baseRouteShouldListAllImages() {
		Image alphaImage = new Image("1", "alpha.png");
		Image bravoImage = new Image("2", "bravo.png");

		given(imageService.findAllImages())
				.willReturn(Flux.just(alphaImage, bravoImage));

		EntityExchangeResult<String> result = webClient
				.get().uri("/")
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class).returnResult();

		verify(imageService).findAllImages();
		verifyNoMoreInteractions(imageService);
		assertThat(result.getResponseBody())
				.contains(
						"<title>Learning Spring Boot: Spring-a-Gram</title>")
				.contains("<a href=\"/images/alpha.png/raw\">")
				.contains("<a href=\"/images/bravo.png/raw\">");
	}

	@Test
	public void fetchingImageShouldWork() {
		given(imageService.findOneImage(any()))
				.willReturn(Mono.just(new ByteArrayResource("data".getBytes())));

		webClient
				.get().uri("/images/alpha.png/raw")
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody(String.class)
				.isEqualTo("data");
		verify(imageService).findOneImage("alpha.png");
		verifyNoMoreInteractions(imageService);
	}

	@Test
	public void fetchingNullImageShouldFail() throws IOException {
		Resource resource = mock(Resource.class);
		given(resource.getInputStream())
				.willThrow(new IOException("Bad file"));
		given(imageService.findOneImage(any()))
				.willReturn(Mono.just(resource));
		webClient
				.get().uri("/images/alpha.png/raw")
				.exchange()
				.expectStatus()
				.isBadRequest()
				.expectBody(String.class)
				.isEqualTo("Couldn't find alpha.png => Bad file");

		verify(imageService).findOneImage("alpha.png");
		verifyNoMoreInteractions(imageService);
	}

	@Test
	public void deleteImageShouldWork() {
		Image alphaImage = new Image("1", "alpha.png");
		given(imageService.deleteImage(any())).willReturn(Mono.empty());

		webClient
				.delete().uri("/images/alpha.png")
				.exchange()
				.expectStatus().isSeeOther()
				.expectHeader().valueEquals(HttpHeaders.LOCATION, "/");

		verify(imageService).deleteImage("alpha.png");
		verifyNoMoreInteractions(imageService);
	}

}
