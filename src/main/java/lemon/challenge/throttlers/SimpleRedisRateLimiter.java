package lemon.challenge.throttlers;

import lemon.challenge.filters.RateLimiterHandlerFilterFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class SimpleRedisRateLimiter implements RateLimiter{

    Logger logger = LoggerFactory.getLogger(RateLimiterHandlerFilterFunction.class);

    @Value("${MAX_REQUESTS_PER_EXPIRY}")
    private Long MAX_REQUESTS_PER_EXPIRY;

    @Value("${EXPIRY_SECONDS}")
    private Long EXPIRY_SECONDS;

    private final ReactiveRedisTemplate<String, Long> redisTemplate;
    private final RedisScript<Boolean> script;

    public SimpleRedisRateLimiter(
            ReactiveRedisTemplate<String, Long> redisTemplate,
            RedisScript<Boolean> script
    ) {
        this.redisTemplate = redisTemplate;
        this.script = script;
    }

    public Long getMAX_REQUESTS_PER_EXPIRY() {
        return MAX_REQUESTS_PER_EXPIRY;
    }

    public void setMAX_REQUESTS_PER_EXPIRY(Long MAX_REQUESTS_PER_EXPIRY) {
        this.MAX_REQUESTS_PER_EXPIRY = MAX_REQUESTS_PER_EXPIRY;
    }

    public Long getEXPIRY_SECONDS() {
        return EXPIRY_SECONDS;
    }

    public void setEXPIRY_SECONDS(Long EXPIRY_SECONDS) {
        this.EXPIRY_SECONDS = EXPIRY_SECONDS;
    }

    @Override
    public Mono<Boolean> isOverTheLimit(String key) {
       return  redisTemplate
                .execute(
                        this.script,
                        List.of(key),
                        List.of(MAX_REQUESTS_PER_EXPIRY, EXPIRY_SECONDS)
                )
                .single(false);
    }
}
