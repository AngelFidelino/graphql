package com.aflr.graphqlspring.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ServerBodyAuthenticationConverter implements Function<ServerWebExchange, Mono<Authentication>> {
    @Override
    public Mono<Authentication> apply(ServerWebExchange exchange) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final ServerHttpRequest request = exchange.getRequest();

        final HttpHeaders headers = request.getHeaders();
        if (isSupported(headers)) {

            final String userId = (String) getHeaderValueOrDefaultByIndex(headers, "user_id", 0).orElse("");
            final String password = (String) getHeaderValueOrDefaultByIndex(headers, "password", 0).orElse("");
            final Optional<String> userRoles = getHeaderValueOrDefaultByIndex(headers, "user_roles", 0);

            Authentication auth = new UsernamePasswordAuthenticationToken(userId, password, getAuthorities(userRoles));
            return Mono.just(auth);
        } else {
            return Mono.empty();
        }
    }

    private boolean isSupported(HttpHeaders headers) {
        MediaType contentType = headers.getContentType();
        return contentType != null && contentType.isCompatibleWith(
                MediaType.APPLICATION_JSON) && headers != null && headers.containsKey("user_id") && headers.containsKey(
                "password");
    }

    public static <T> Optional<T> getHeaderValueOrDefaultByIndex(HttpHeaders headers, String key, int index) {
        return headers.containsKey(key) ? Optional.ofNullable((T) headers.get(key).get(index)) : Optional.empty();
    }

    public static List<GrantedAuthority> getAuthorities(Optional<String> userRoles) {
        if (userRoles.isEmpty())
            return Collections.emptyList();
        return Set.of(userRoles.get().split(",")).stream().map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
