package com.aflr.graphqlspring.config;

import com.aflr.graphqlspring.entity.Book;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;

@Configuration
public class SubscriptionConfig {
    @Bean
    public Sinks.Many<Book> publisher() {
        return Sinks.many().multicast().directBestEffort();
    }
}
