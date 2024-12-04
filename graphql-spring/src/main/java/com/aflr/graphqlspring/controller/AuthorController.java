package com.aflr.graphqlspring.controller;

import com.aflr.graphqlspring.entity.Author;
import com.aflr.graphqlspring.service.AuthorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class AuthorController {
    @Autowired
    private AuthorService authorService;

    @QueryMapping
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN') ")
    public Flux<Author> getAuthors() {
        return authorService.getAll();
    }

    @QueryMapping
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN') ")
    public Mono<Author> getAuthorById(@Argument("id") int id) {
        return authorService.getAuthorByBookId(id);
    }

    @MutationMapping(name = "addAuthor")
    @PreAuthorize("hasRole('ROLE_ADMIN') ")
    public Mono<Author> save(@Argument("author") @Valid AuthorInput author) {
        Author authorEntity = new Author();
        authorEntity.setFirstName(author.firstNameAuthor());
        authorEntity.setLastName(author.lastNameAuthor());
        authorEntity.setAge(author.ageAuthor());
        return authorService.addAuthor(authorEntity);
    }

    public record AuthorInput(@Pattern(regexp = "^[a-zA-Z\\s]*$") String firstNameAuthor,
                              @Pattern(regexp = "^[a-zA-Z\\s]*$") String lastNameAuthor,
                              Integer ageAuthor) {
    }
}
