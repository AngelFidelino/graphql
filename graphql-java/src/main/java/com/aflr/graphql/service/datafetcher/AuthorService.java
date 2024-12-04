package com.aflr.graphql.service.datafetcher;

import com.aflr.graphql.dao.AuthorRepository;
import com.aflr.graphql.entity.Author;
import com.aflr.graphql.entity.Book;
import graphql.schema.DataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@Service
public class AuthorService {
    @Autowired
    private AuthorRepository authorRepository;

    public Mono<Integer> addAuthor(String firstName, String lastName, int age, int bookId) {
        Author author = new Author();
        author.setFirstName(firstName);
        author.setLastName(lastName);
        author.setAge(age);
        author.setBookId(bookId);
        return authorRepository.addAuthor(author);
    }

    public DataFetcher<CompletableFuture<Author>> authorDataFetcher() {
        return env -> {
            Book book = env.getSource();
            return authorRepository.getAuthorByBookId(book.getId()).toFuture();
        };
    }
}
