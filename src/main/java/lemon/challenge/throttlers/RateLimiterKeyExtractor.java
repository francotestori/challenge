package lemon.challenge.throttlers;

import org.springframework.web.reactive.function.server.ServerRequest;

/**
 * RateLimiterKeyExtractor interface used to process a request
 * and extract a key value to be used for API rate limiting.
 */
public interface RateLimiterKeyExtractor {
    String extract(ServerRequest request);
}
