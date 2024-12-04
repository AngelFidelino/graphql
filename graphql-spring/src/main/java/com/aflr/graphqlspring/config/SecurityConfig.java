package com.aflr.graphqlspring.config;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.util.StringUtils;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Log
public class SecurityConfig {
    @Autowired
    private SecurityProperties properties;
    public static final String[] AUTH_WHITELIST = {
            "/graphiql"
            , "/graphql"
            , "/actuator/health"
            , "/playground"
            , "/subscriptions"
    };

    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity httpSec,
            ReactiveAuthenticationManager authenticationManager) {
        return httpSec
                //.securityMatcher(new NegatedServerWebExchangeMatcher(ServerWebExchangeMatchers.pathMatchers(AUTH_WHITELIST)))
                .addFilterBefore(new MyAuthWebFilter(authenticationManager), SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange((aut) -> {
                    aut.anyExchange().permitAll();
                })
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .anonymous(ServerHttpSecurity.AnonymousSpec::disable)
                .build();

    }

    @Bean
    public UserDetailsRepositoryReactiveAuthenticationManager authenticationManager() {
        UserDetailsRepositoryReactiveAuthenticationManager manager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService());
        return manager;
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        SecurityProperties.User user = properties.getUser();
        List<String> roles = user.getRoles();
        String password = user.getPassword();
        if (user.isPasswordGenerated()) {
            log.info(String.format("%n%nUsing default security password: %s%n", user.getPassword()));
        }
        final UserDetails userDetails = User
                //.withDefaultPasswordEncoder()
                //.username(user.getName())
                .withUsername(user.getName())
                //.password(passwordEncoder().encode(password))
                .password("{noop}" + password)
                .roles(StringUtils.toStringArray(roles))
                .build();
        return new MapReactiveUserDetailsService(userDetails);
    }

    /**
     * This method works as an alternative to the custom MyAuthWebFilter class
     */
    @Deprecated
    private AuthenticationWebFilter authenticationWebFilter() {
        var filter = new AuthenticationWebFilter(authenticationManager());
        filter.setServerAuthenticationConverter(new UsernamePasswordAuthenticationConverter());
        return filter;
    }
}
