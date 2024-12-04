package com.aflr.graphqlspring.service;


import com.aflr.graphqlspring.dao.AuthorRepository;
import com.aflr.graphqlspring.entity.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AuthorService {
    @Autowired
    private AuthorRepository authorRepository;

    public Mono<Author> addAuthor(Author author) {
        return authorRepository.save(author);
    }

    public Mono<Author> getAuthorByBookId(int bookId) {
        return authorRepository.findById(bookId);
    }

    public Flux<Author> getAll(){
        return authorRepository.findAll();
    }
}
