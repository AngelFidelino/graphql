package com.aflr.graphql.conf;

import com.aflr.graphql.service.datafetcher.AuthorService;
import com.aflr.graphql.service.datafetcher.BookService;
import graphql.GraphQL;
import graphql.execution.instrumentation.tracing.TracingInstrumentation;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.TypeRuntimeWiring;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;

@Configuration
public class GraphQLConf {

    @Bean
    public GraphQL graphQL(BookService bookServiceDataFetcher, AuthorService authorService) throws IOException {

        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry registry = new TypeDefinitionRegistry();
        final Resource[] graphQLResources = new PathMatchingResourcePatternResolver()
                .getResources("classpath:/graphql/**/*.graphql");

        for (Resource resource : graphQLResources) {
            registry.merge(schemaParser.parse(resource.getInputStream()));
        }
        var getBookFetcher = TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("getBook", bookServiceDataFetcher.getBook());
        var getBooksFetcher = TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("getBooks", bookServiceDataFetcher.getBooks());
        var createBookFetcher = TypeRuntimeWiring.newTypeWiring("Mutation")
                .dataFetcher("createBook", bookServiceDataFetcher.createBook());
        var getAuthorFetcher = TypeRuntimeWiring.newTypeWiring("Book")
                .dataFetcher("author", authorService.authorDataFetcher());

        RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
                .type(getBookFetcher)
                .type(getBooksFetcher)
                .type(createBookFetcher)
                .type(getAuthorFetcher)
                .build();

        SchemaGenerator generator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = generator.makeExecutableSchema(registry, wiring);
        return GraphQL.newGraphQL(graphQLSchema).instrumentation(new TracingInstrumentation()).build();
    }
}
