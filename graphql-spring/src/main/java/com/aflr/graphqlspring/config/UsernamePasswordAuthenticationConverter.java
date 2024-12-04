package com.aflr.graphqlspring.config;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static com.aflr.graphqlspring.config.ServerBodyAuthenticationConverter.getAuthorities;
import static com.aflr.graphqlspring.config.ServerBodyAuthenticationConverter.getHeaderValueOrDefaultByIndex;

/**
 * Use @{link ServerBodyAuthenticationConverter} instead
 */
@Component
@AllArgsConstructor
@Deprecated
public class UsernamePasswordAuthenticationConverter implements ServerAuthenticationConverter {
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        final HttpHeaders headers = exchange.getRequest().getHeaders();
        if (headers.isEmpty()) {
            return Mono.empty();
        }

        final String userId = (String) getHeaderValueOrDefaultByIndex(headers, "user_id", 0).orElse("");
        final String password = (String) getHeaderValueOrDefaultByIndex(headers, "password", 0).orElse("");
        final Optional<String> userRoles = getHeaderValueOrDefaultByIndex(headers, "user_roles", 0);

        Authentication auth = new UsernamePasswordAuthenticationToken(userId, password, getAuthorities(userRoles));
        return Mono.just(auth);
    }
}
