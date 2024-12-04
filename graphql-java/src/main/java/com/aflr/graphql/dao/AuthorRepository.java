package com.aflr.graphql.dao;

import com.aflr.graphql.entity.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class AuthorRepository {
    @Autowired
    private R2dbcEntityTemplate entityTemplate;

    public Mono<Integer> addAuthor(Author author) {
        return entityTemplate.insert(Author.class).using(author).map(Author::getId);
    }

    public Mono<Author> getAuthorByBookId(int bookId) {
        return entityTemplate.select(Author.class)
                .matching(Query.query(Criteria.where("book_id").is(bookId)))
                .one();
    }

}
