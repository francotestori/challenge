package lemon.challenge.routes;

import lemon.challenge.filters.RateLimiterHandlerFilterFunction;
import lemon.challenge.handlers.FuckOffMessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration(proxyBeanMethods = false)
public class FuckOffMessageRouter {

    RateLimiterHandlerFilterFunction rateLimiterHandlerFilterFunction;

    public FuckOffMessageRouter(RateLimiterHandlerFilterFunction rateLimiterHandlerFilterFunction) {
        this.rateLimiterHandlerFilterFunction = rateLimiterHandlerFilterFunction;
    }

    @Bean
    public RouterFunction<ServerResponse> route(FuckOffMessageHandler fuckOffMessageHandler) {
        return RouterFunctions
                .route(
                        GET("/message"),
                        fuckOffMessageHandler::fuckOffMessage
                )
                .filter(rateLimiterHandlerFilterFunction);
    }
}
