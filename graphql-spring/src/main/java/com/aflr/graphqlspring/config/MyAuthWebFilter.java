package com.aflr.graphqlspring.config;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

public class MyAuthWebFilter extends AuthenticationWebFilter {

    /**
     * Creates an instance
     *
     * @param authenticationManager the authentication manager to use
     */
    public MyAuthWebFilter(ReactiveAuthenticationManager authenticationManager) {
        super(authenticationManager);

        setAuthenticationConverter(new ServerBodyAuthenticationConverter());
    }

}
