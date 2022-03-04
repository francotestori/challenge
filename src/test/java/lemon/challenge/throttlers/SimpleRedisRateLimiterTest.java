package lemon.challenge.throttlers;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class SimpleRedisRateLimiterTest {

    private static final long MAX_REQUESTS_PER_EXPIRY = 5L;
    private static final long EXPIRY_SECONDS = 10L;

    private static GenericContainer genericContainer;
    private SimpleRedisRateLimiter redisRateLimiter;

    @BeforeAll
    public static void setupRedisServer() {
        genericContainer = new GenericContainer(
                DockerImageName.parse("redis:6.2.6-alpine")
        ).withExposedPorts(6379);
        genericContainer.start();
    }

    @BeforeEach
    public void setupRedisClient() {
        LettuceConnectionFactory factory = new LettuceConnectionFactory(
                genericContainer.getHost(),
                genericContainer.getMappedPort(6379)
        );
        JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();
        StringRedisSerializer stringRedisSerializer = StringRedisSerializer.UTF_8;
        GenericToStringSerializer<Long> longToStringSerializer = new GenericToStringSerializer<>(Long.class);

        ReactiveRedisTemplate<String, Long> template = new ReactiveRedisTemplate<>(
                factory,
                RedisSerializationContext
                        .<String, Long>newSerializationContext(jdkSerializationRedisSerializer)
                        .key(stringRedisSerializer)
                        .value(longToStringSerializer)
                        .build()
        );

        RedisScript<Boolean> script = RedisScript.of(
                new ClassPathResource("scripts/rate_limiter.lua"),
                Boolean.class
        );

        redisRateLimiter = new SimpleRedisRateLimiter(template, script);
        ReflectionTestUtils.setField(redisRateLimiter, "MAX_REQUESTS_PER_EXPIRY", MAX_REQUESTS_PER_EXPIRY);
        ReflectionTestUtils.setField(redisRateLimiter, "EXPIRY_SECONDS", EXPIRY_SECONDS);

        factory.afterPropertiesSet();
    }

    public void checkRateLimiter(String key, long sleepMillis, boolean expected) throws InterruptedException{
        Mono<Boolean> isOverTheLimit = redisRateLimiter.isOverTheLimit(key);
        StepVerifier.create(isOverTheLimit)
                .expectNextMatches(
                        hasReachedLimit -> hasReachedLimit.equals(expected)
                )
                .verifyComplete();
        if (sleepMillis > 0)
            Thread.sleep(sleepMillis);
    }

    @Test
    public void testSingeLimit()throws InterruptedException{
        String key = "rl_test-key-single";

        checkRateLimiter(key,0,false);
    }

    @Test
    public void testMaxSuccessLimit() throws InterruptedException {
        String key = "rl_test-key-max-success";

        for(int i=0; i < 5; i++){
            checkRateLimiter(key,0,false);
        }
    }

    @Test
    public void testMaxFailLimit() throws InterruptedException {
        String key = "rl_test-key-max-failure";

        for(int i = 0; i < 6; i++){
            checkRateLimiter(key, 0, i == 5);
        }
    }

    @Test
    public void testMaxSuccessLimitAfterExpiry() throws InterruptedException {
        String key = "rl_test-key-max-success-after-expiry";

        for(int i=0; i < 6; i++){
            long sleepMillis = i == 4 ? 10001 : 0;
            checkRateLimiter(key,sleepMillis,false);
        }
    }

    @AfterAll
    public static void teardownRedisServer() {
        genericContainer.stop();
    }

}
