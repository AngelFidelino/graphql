package com.aflr.graphqlspring.dao;


import com.aflr.graphqlspring.entity.Book;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface BookRepository extends R2dbcRepository<Book, Integer> {
    Window<Book> findBy(ScrollPosition position, Limit limit, Sort sort);

    Flux<Book> findBy(Pageable pageable);

    Window<Book> findByName(String name, ScrollPosition position, Limit limit, Sort sort);
}
