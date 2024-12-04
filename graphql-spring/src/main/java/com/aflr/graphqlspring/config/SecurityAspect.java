package com.aflr.graphqlspring.config;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;


/**
 * //NOT IMPLEMENTED YET (Follow https://michalgebauer.github.io/spring-graphql-security/)
 Any method not marked an @Unsecured will be checked against this class and it no authenticated user is provided it will thrown an exception
* */
@Aspect
@Component
@Order(1)
public class SecurityAspect {


    /**
     * All graphQLResolver methods can be called only by authenticated user.
     *
     * @Unsecured annotated methods are excluded
     */
    @After("isDefinedInApplication() && !isMethodAnnotatedAsUnsecured()")
    public void doSecurityCheck() throws Throwable {
        ReactiveSecurityContextHolder.getContext()
                .doOnNext(sc -> {
                    if (sc.getAuthentication() == null || !sc.getAuthentication().isAuthenticated()) {
                        throw new AccessDeniedException("User not authenticated");
                    }
                });

    }

    /**
     * Any method annotated with @Unsecured
     */
    @Pointcut("@annotation(com.aflr.graphqlspring.config.Unsecured)")
    private void isMethodAnnotatedAsUnsecured() {
    }

    /**
     * Matches all beans in com.mi3o.springgraphqlsecurity package resolvers must be in this package (subpackages)
     */
    @Pointcut("within(com.aflr.graphqlspring.controller..*)")
    private void isDefinedInApplication() {
    }
}
