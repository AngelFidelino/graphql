package com.aflr.graphql.service.datafetcher;

import com.aflr.graphql.dao.BookRepository;
import com.aflr.graphql.entity.Book;
import com.aflr.graphql.enums.Category;
import graphql.schema.DataFetcher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class BookService {
    public BookService(BookRepository bookRepository, AuthorService authorService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
    }

    private BookRepository bookRepository;
    private AuthorService authorService;

    public DataFetcher<CompletableFuture<Book>> getBook() {
        return env -> {
            final int bookId = env.getArgument("id");
            return bookRepository.getBook(bookId).toFuture();
        };
    }

    public DataFetcher<CompletableFuture<List<Book>>> getBooks() {
        return env -> bookRepository.getBooks().collectList().toFuture();
    }

    public DataFetcher<CompletableFuture<Map<String, Integer>>> createBook() {
        return env -> {
            //------
            final String bookName = env.getArgument("bookName");
            final int pages = env.getArgument("pages");
            final Category category = Category.valueOf(env.getArgument("category"));
            //------
            final String firstName = env.getArgument("firstNameAuthor");
            final String lastName = env.getArgument("lastNameAuthor");
            final int age = env.getArgument("ageAuthor");

            Book book = Book.builder().name(bookName).pages(pages).category(category).build();

            return bookRepository.createBook(book).flatMap(bookId ->
                    authorService.addAuthor(firstName, lastName, age, bookId).map(authorId ->
                            Map.of("bookId", bookId, "authorId", authorId)
                    )
            ).toFuture();
        };
    }
}
