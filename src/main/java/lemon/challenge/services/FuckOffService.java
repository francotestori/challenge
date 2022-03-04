package lemon.challenge.services;

import lemon.challenge.exceptions.ExternalServiceException;
import lemon.challenge.exceptions.FuckOffServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerErrorException;
import reactor.core.publisher.Mono;

import java.util.function.Predicate;

@Service
public class FuckOffService {

    private Logger logger = LoggerFactory.getLogger(FuckOffService.class);

    @Value("${foaas.web_client.path}")
    private String path;

    private final WebClient client;

    public FuckOffService(WebClient client) {
        this.client = client;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Mono<String> getFuckOffMessage(String name){
        String uri = String.format("/%s/%s", this.path, name);

        return this.client
                .get()
                .uri(uri)
                .accept(MediaType.TEXT_PLAIN)
                .retrieve()
                .onStatus(
                        HttpStatus::isError,
                        response -> response
                                    .bodyToMono(String.class)
                                    .flatMap(body -> {
                                        if(response.statusCode().is4xxClientError()){
                                            logger.error(String.format("Response from FOAAS is 4xx: %s", body));
                                        } else {
                                            logger.error(String.format("Response from FOAAS is 5xx: %s", body));
                                        }

                                        return Mono.error(
                                                new ExternalServiceException(
                                                        "Response from FOAAS was not 2xx",
                                                        response.statusCode()
                                                )
                                        );
                                    })
                )
                .bodyToMono(String.class)
                .onErrorMap(
                        Predicate.not(ExternalServiceException.class::isInstance),
                        throwable -> {
                            logger.error("Failed to send request to service", throwable);
                            return new FuckOffServiceException("Service request failed.");
                        }

                );
    }
}
