package com.example.github_repos;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GithubIntegrationTest {

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void beforeAll() {
        wireMockServer = new WireMockServer(0); // 0 = random port
        wireMockServer.start();
    }

    @AfterAll
    static void afterAll() {
        wireMockServer.stop();
    }

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("github.api.url", () -> "http://localhost:" + wireMockServer.port());
    }

    @Test
    void happyPathTest() {
        wireMockServer.stubFor(get(urlEqualTo("/users/testuser/repos"))
                .willReturn(okJson("""
                        [
                          {
                            "name": "repo1",
                            "html_url": "https://github.com/testuser/repo1"
                          },
                          {
                            "name": "repo2",
                            "html_url": "https://github.com/testuser/repo2"
                          }
                        ]
                        """)));

        // when – wywołanie naszego API (np. RestTemplate lub TestRestTemplate)
        var restTemplate = new org.springframework.boot.test.web.client.TestRestTemplate();
        var response = restTemplate.getForEntity(
                "http://localhost:8080/repos/testuser", String.class);

        // then – weryfikacja treści
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).contains("repo1").contains("repo2");
    }
}
