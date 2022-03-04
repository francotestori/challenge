package lemon.challenge.filters;

import lemon.challenge.throttlers.RateLimiter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lemon.challenge.throttlers.RateLimiterKeyExtractor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

@Component
public class RateLimiterHandlerFilterFunction implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    Logger logger = LoggerFactory.getLogger(RateLimiterHandlerFilterFunction.class);

    private final RateLimiter rateLimiter;
    private final RateLimiterKeyExtractor keyExtractor;


    public RateLimiterHandlerFilterFunction(
            RateLimiter rateLimiter,
            RateLimiterKeyExtractor keyExtractor
    ) {
        this.rateLimiter = rateLimiter;
        this.keyExtractor = keyExtractor;
    }

    @Override
    public Mono<ServerResponse> filter(
            ServerRequest request,
            HandlerFunction<ServerResponse> next
    ) {
        DateTime now = DateTime.now(DateTimeZone.UTC);
        int seconds = now.getSecondOfDay();

        String key = String.format("rl_%s", keyExtractor.extract(request));

        return rateLimiter
                .isOverTheLimit(key)
                .flatMap(
                        value -> {
                            logger.debug(String.format("%s retrieved at %s with value -> %s", key, seconds, value));
                            return value ?
                                    ServerResponse.status(TOO_MANY_REQUESTS).build() :
                                    next.handle(request);
                        }
                );
    }
}
