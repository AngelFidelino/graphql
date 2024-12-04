package com.aflr.graphql.controller;

import com.aflr.graphql.dto.GraphQLRequest;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class GraphQLController {

    public GraphQLController(GraphQL graphQL) {
        this.graphQL = graphQL;
    }

    private GraphQL graphQL;

    @PostMapping(value = "/graphql", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Mono<Map<String, Object>> execute(@RequestBody GraphQLRequest request)
            throws ExecutionException, InterruptedException {
        CompletableFuture<ExecutionResult> executionResult = graphQL
                .executeAsync(ExecutionInput.newExecutionInput()
                        .query(request.getQuery())
                        .operationName(request.getOperationName())
                        .variables(request.getVariables()).build()
                );
        System.out.printf("Data: " + executionResult.get().getData());
        return Mono.fromCompletionStage(executionResult).map(ExecutionResult::toSpecification);
    }
}
