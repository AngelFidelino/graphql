package com.aflr.graphqlspring.controller;

import com.aflr.graphqlspring.entity.Author;
import com.aflr.graphqlspring.entity.Book;
import com.aflr.graphqlspring.enums.Category;
import com.aflr.graphqlspring.mappers.AuthorMapper;
import com.aflr.graphqlspring.mappers.BookMapper;
import com.aflr.graphqlspring.service.AuthorService;
import com.aflr.graphqlspring.service.BookService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.graphql.data.query.ScrollSubrange;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.security.Principal;
import java.time.LocalDate;

@Slf4j
@RestController
public class BookController {

    private Sinks.Many<Book> publisher;
    private BookService bookService;
    private AuthorService authorService;

    public BookController(AuthorService authorService, BookService bookService, Sinks.Many<Book> publisher) {
        this.authorService = authorService;
        this.bookService = bookService;
        this.publisher = publisher;
    }


    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN') ")
    @QueryMapping(name = "getBookById")
    public Mono<Book> bookById(@Argument int id, Principal principal) {
        return bookService.getBook(id);
    }

    @SchemaMapping(typeName = "Book", field = "author")
    public Mono<Author> getAuthor(Book book) {
        if (book.getAuthorId() == null)
            return Mono.empty();
        return authorService.getAuthorByBookId(book.getAuthorId());
    }

    @QueryMapping
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public Flux<Book> getBooks(Authentication authentication) {
        return bookService.getAllBooks();
    }

    @QueryMapping(name = "getBooksPageable")
    @Deprecated
    public Flux<Book> getBooks(ScrollSubrange subrange) {
        int defaultLimit = 10;
        ScrollPosition scrollPosition = subrange.position().orElse(ScrollPosition.offset());
        Limit limit = Limit.of(subrange.count().orElse(defaultLimit));
        Sort sort = Sort.by("id").ascending();
        int pageNumber = 0;//(int)((OffsetScrollPosition)subrange.position().get()).getOffset();
        PageRequest pageRequest = PageRequest.of(0, limit.max());
        /*

        Flux<DefaultEdge<Book>> edges = bookService.getAllBooks(pageRequest).map(book ->
                new DefaultEdge<>(book, new DefaultConnectionCursor(String.valueOf(book.getId())))
        );

        int edgeSize = edges.size();
        DefaultPageInfo pageInfo = new DefaultPageInfo(() -> edges.get(0).getCursor().getValue(),
                () -> edges.get(edgeSize - 1).getCursor().getValue(), pageNumber > 0, edgeSize > limit.max());*/
        //return new DefaultConnection(edges, pageInfo);
        final Flux<Book> allBooks = bookService.getAllBooks(pageRequest);
        return allBooks;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @MutationMapping
    public Mono<Book> addBook(@Argument String bookName, @Argument Integer pages,
            @Argument Category category, @Argument LocalDate publishedAt,
            @Argument("author") @Valid AuthorController.AuthorInput authorInput) {

        Book book = BookMapper.INSTANCE.toEntity(bookName, pages, publishedAt, category);
        Author author =
                AuthorMapper.INSTANCE.toEntity(authorInput.firstNameAuthor(), authorInput.lastNameAuthor(),
                        authorInput.ageAuthor());

        return bookService.createBook(author, book).map(book1 -> {
            publisher.tryEmitNext(book1);
            return book1;
        });
    }

    @SubscriptionMapping
    public Flux<Book> notifyNewBooks() {
        return publisher.asFlux();
    }
}
