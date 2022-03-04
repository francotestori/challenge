package lemon.challenge;

import lemon.challenge.throttlers.InetSocketKeyExtractor;
import lemon.challenge.throttlers.RateLimiterKeyExtractor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@SpringBootApplication
public class ChallengeApplication {

	@Bean
	public RateLimiterKeyExtractor keyExtractor(){
		return new InetSocketKeyExtractor();
	}

	@Bean
	public RedisScript<Boolean> script() {
		return RedisScript.of(
				new ClassPathResource("scripts/rate_limiter.lua"),
				Boolean.class
		);
	}

	@Bean
	ReactiveRedisTemplate<String, Long> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
		JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();
		StringRedisSerializer stringRedisSerializer = StringRedisSerializer.UTF_8;
		GenericToStringSerializer<Long> longToStringSerializer = new GenericToStringSerializer<>(Long.class);

		return new ReactiveRedisTemplate<>(
				factory,
				RedisSerializationContext
						.<String, Long>newSerializationContext(jdkSerializationRedisSerializer)
						.key(stringRedisSerializer)
						.value(longToStringSerializer)
						.build()
		);
	}

	public static void main(String[] args) {
		SpringApplication.run(ChallengeApplication.class, args);
	}


}
