package com.aflr.graphql.dao;

import com.aflr.graphql.entity.Book;
import com.aflr.graphql.enums.Category;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;

@Repository
public class BookRepository {
    public BookRepository(DatabaseClient databaseClient, R2dbcEntityTemplate r2dbcEntityTemplate) {
        this.databaseClient = databaseClient;
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
    }

    private DatabaseClient databaseClient;
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    public static final BiFunction<Row, RowMetadata, Book> MAPPING_FUNCTION =
            (row, rowMetaData) -> Book.builder()
                    .id(row.get("id", Integer.class))
                    .name(row.get("name", String.class))
                    .pages(row.get("pages", Integer.class))
                    .category(Objects.nonNull(row.get("category")) ?
                            Category.valueOf(row.get("category", String.class)) :
                            null)
                    .build();

    public Mono<Book> getBook(int id) {
        return databaseClient.sql("SELECT * FROM books where id=:id")
                .bind("id", id)
                .map(MAPPING_FUNCTION)
                .one();
    }

    public Flux<Book> getBooks() {
        //return r2dbcEntityTemplate.select(Book.class).all();
        return databaseClient.sql("SELECT * FROM books")
                .map(MAPPING_FUNCTION)
                .all();
    }

    public Mono<Integer> createBook(Book book) {
        return r2dbcEntityTemplate.insert(Book.class).using(book).map(Book::getId);
    }

    public Mono<Integer> createBookWithQuery(Book book) {
        final int id = ThreadLocalRandom.current().nextInt();
        return databaseClient.sql("INSERT INTO books(id, name, pages) VALUES(:id, :name, :pages)")
                .bindProperties(book)
                .bind("id", id)
                .filter(statement -> statement.returnGeneratedValues("id"))
                .map(row -> row.get("id", Integer.class))
                .first();
    }
}
