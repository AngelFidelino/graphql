package com.aflr.graphqlspring.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.ConstraintViolationException;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class CustomExceptionResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof ConstraintViolationException) {
            return graphQLError(ErrorType.BAD_REQUEST, ex, env);
        } else if (ex instanceof NotFoundException) {
            return graphQLError(ErrorType.NOT_FOUND, ex, env);
        } else if (ex instanceof AccessDeniedException || ex instanceof AuthenticationException) {
            return graphQLError(ErrorType.FORBIDDEN, ex, env);
        } else {
            return graphQLError(ErrorType.INTERNAL_ERROR, "INTERNAL SERVER ERROR", env);
        }
    }

    private GraphQLError graphQLError(ErrorType errorType, Throwable ex, DataFetchingEnvironment env) {
        return graphQLError(ErrorType.NOT_FOUND, ex.getMessage(), env);
    }

    private GraphQLError graphQLError(ErrorType errorType, String message, DataFetchingEnvironment env) {
        return GraphqlErrorBuilder.newError()
                .errorType(errorType)
                .message(message)
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .build();
    }

}
