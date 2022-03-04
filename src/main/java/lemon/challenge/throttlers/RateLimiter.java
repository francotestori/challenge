package lemon.challenge.throttlers;

import reactor.core.publisher.Mono;

public interface RateLimiter {
    Mono<Boolean> isOverTheLimit(String key);
}
