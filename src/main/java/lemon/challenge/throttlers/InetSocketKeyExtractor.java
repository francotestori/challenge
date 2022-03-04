package lemon.challenge.throttlers;

import org.springframework.web.reactive.function.server.ServerRequest;

import java.net.InetSocketAddress;
import java.util.Optional;

/**
 * Extract InetSocketAddress hostname as Rate Limiter Key to identify incoming requests.
 */
public class InetSocketKeyExtractor implements RateLimiterKeyExtractor {

    @Override
    public String extract(ServerRequest request) {
        Optional<InetSocketAddress> maybeAddress = request.remoteAddress();
        return maybeAddress.isPresent() ? maybeAddress.get().getHostName() : "";
    }
}
