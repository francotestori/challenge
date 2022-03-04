package lemon.challenge.handlers;

import lemon.challenge.services.FuckOffService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class FuckOffMessageHandler {

    private final FuckOffService service;

    public FuckOffMessageHandler(FuckOffService service) {
        this.service = service;
    }

    public Mono<ServerResponse> fuckOffMessage(ServerRequest request){
        String name = request.queryParam("name").orElse("Lemon");

        return service
                .getFuckOffMessage(name)
                .flatMap(message -> ok()
                                .contentType(TEXT_PLAIN)
                                .body(
                                        BodyInserters.fromValue(message)
                                )
                );
    }
}
