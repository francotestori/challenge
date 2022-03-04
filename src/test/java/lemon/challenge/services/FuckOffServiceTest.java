package lemon.challenge.services;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class FuckOffServiceTest {

    final String TEST_PATH = "test-path";

    public static MockWebServer server;
    private FuckOffService service;

    @BeforeAll
    static void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        server.shutdown();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = server.url("").toString();
        service = new FuckOffService(WebClient.create(baseUrl));
        service.setPath(TEST_PATH);
    }

    @Test
    public void testServiceRequest() throws InterruptedException{
        String name = "lemon";
        MockResponse response = new MockResponse();
        server.enqueue(response);

        service.getFuckOffMessage(name).block();

        RecordedRequest request = server.takeRequest();
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath()).startsWith(String.format("/%s/%s", TEST_PATH, name));
    }

    @Test
    public void testServiceResponse() throws InterruptedException{
        String expected = "Happiness can be found, even in the darkest of times, if one only remembers to fuck off. - lemon";

        String name = "lemon";
        MockResponse response = new MockResponse()
                .addHeader("Content-Type", "text/plain")
                .setBody(expected);
        server.enqueue(response);

        StepVerifier
                .create(service.getFuckOffMessage(name))
                .expectNextMatches(message ->
                        message.equals(expected)
                )
                .verifyComplete();

        RecordedRequest request = server.takeRequest();
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath()).startsWith(String.format("/%s/%s", TEST_PATH, name));
    }

}
