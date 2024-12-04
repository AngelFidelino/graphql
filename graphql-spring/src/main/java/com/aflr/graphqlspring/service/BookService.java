package com.aflr.graphqlspring.service;


import com.aflr.graphqlspring.dao.BookRepository;
import com.aflr.graphqlspring.dao.BookRepositoryPagination;
import com.aflr.graphqlspring.entity.Author;
import com.aflr.graphqlspring.entity.Book;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Service
public class BookService {
    private BookRepository bookRepository;
    private BookRepositoryPagination bookRepositoryPagination;
    private AuthorService authorService;

    public BookService(BookRepository bookRepository, AuthorService authorService,
            BookRepositoryPagination bookRepositoryPagination) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.bookRepositoryPagination = bookRepositoryPagination;
    }

    public Mono<Book> getBook(int id) {
        return bookRepository.findById(id);
    }

    public Flux<Book> getAllBooks() {
        final Flux<Book> all = bookRepository.findAll();
        return all;
    }

    public Flux<Book> getAllBooks(PageRequest pageRequest) {
        return bookRepositoryPagination.findAllBy(pageRequest);
    }

    public Mono<Book> createBook(Author author, Book book) {

        return authorService.addAuthor(author)
                .flatMap(savedAuthor -> {

                    book.setAuthorId(savedAuthor.getId());
                    return bookRepository.save(book).map(Function.identity());
                });
    }

    private JsonNode createJsonResponse(int bookId, int authorId) {
        JsonNode json = JsonNodeFactory.instance.objectNode();
        ((ObjectNode) json).put("bookId", bookId);
        ((ObjectNode) json).put("authorId", authorId);
        return json;
    }
}
