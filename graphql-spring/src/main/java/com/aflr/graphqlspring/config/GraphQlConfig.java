package com.aflr.graphqlspring.config;

import graphql.scalars.ExtendedScalars;
import graphql.validation.rules.OnValidationErrorStrategy;
import graphql.validation.rules.ValidationRules;
import graphql.validation.schemawiring.ValidationSchemaWiring;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQlConfig {
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder.scalar(ExtendedScalars.NonNegativeInt)
                .scalar(ExtendedScalars.Date)
                .scalar(ExtendedScalars.DateTime)
                .directiveWiring(createValidationSchemaWiring())
                .build();
    }

    private ValidationSchemaWiring createValidationSchemaWiring() {

        ValidationRules validationRules = ValidationRules.newValidationRules()
                .onValidationErrorStrategy(OnValidationErrorStrategy.RETURN_NULL)
                .build();

        return new ValidationSchemaWiring(validationRules);
    }
}
