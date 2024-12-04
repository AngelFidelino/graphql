package com.aflr.graphqlspring.integration;

import com.aflr.graphqlspring.dao.BookRepository;
import com.aflr.graphqlspring.entity.Book;
import com.aflr.graphqlspring.enums.Category;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("integration-test")
@AutoConfigureHttpGraphQlTester
public class BookControllerTests {

    private Book firstBookSaved;

    @Autowired
    private HttpGraphQlTester httpGraphQlTester;
    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void beforeEach() {
        Book newBook = new Book();
        newBook.setName(UUID.randomUUID().toString());
        newBook.setAuthorId(1);
        newBook.setCategory(Category.NOVEL);
        newBook.setPages(823);
        newBook.setPublishedAt(LocalDate.now());
        firstBookSaved = bookRepository.save(newBook).block();
    }

    @AfterEach
    void afterAll() {
        bookRepository.deleteAll();
    }

    @Test
    void testGetAllBooksWithCredentials() {
        Map<String, String> authHeaders =
                Map.of("user_id", "angel", "password", "angel123", "user_roles", "book_by_id");
        final List<Book> books = httpGraphQlTester.mutate()
                .headers(h -> h.setAll(authHeaders))
                .build()
                .documentName("getAllBooks").execute()
                .path("data.getBooks[*]")
                .entityList(Book.class)
                .satisfies(list -> list.forEach(System.out::println))
                .get();

        assertThat(books.size()).isGreaterThan(0);
        assertThat(books).anyMatch(book -> firstBookSaved.getName().equals(book.getName()));
    }

    @Test
    @WithMockUser
    void testGetBookByID() {
        Book newBook = new Book();
        newBook.setName("My new newBook");
        newBook.setAuthorId(1);
        newBook.setCategory(Category.FANTASY);
        newBook.setPages(12);
        newBook.setPublishedAt(LocalDate.now());

        final Book saved = bookRepository.save(newBook).block();

        httpGraphQlTester.mutate()
                .build()
                .documentName("getOneBookById")
                .variable("id", saved.getId())
                .execute()
                .path("getBookById")
                .entity(Book.class)
                .satisfies(book -> {
                    Assertions.assertNotNull(book);
                    assertThat(saved.getId()).isEqualTo(book.getId());
                });
    }

    @Test
    @WithMockUser
    void testGetBookNameByID_with_documentName() {
        String bookNameQuery = """
                {
                    getBookById(id: 1) { 
                        name
                    } 
                }
                """;

        httpGraphQlTester.mutate()
                .build()
                .document(bookNameQuery)
                .execute()
                .path("getBookById.name")
                .entity(String.class)
                .satisfies(name -> firstBookSaved.getName().equals(name));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testAddBook_with_documentName() {
        final Integer bookId = httpGraphQlTester
                .mutate()
                .build()
                .documentName("addComedyBook")
                .variable("bookName", "Pride and Prejudice")
                .variable("firstNameAuthor", "Jane")
                .variable("lastNameAuthor", "Austen")
                .variable("ageAuthor", 45)
                .variable("pages", 435)
                .execute()
                .path("addBook.id")
                .entity(Integer.class).get();

        httpGraphQlTester
                .mutate()
                .header("user_id", "angel")
                .header("password", "angel123")
                .header("user_roles", "book_by_id")
                .build()
                .documentName("getOneBookById")
                .variable("id", bookId)
                .execute()
                .path("getBookById")
                .entity(Book.class)
                .satisfies(book -> assertThat(book.getName()).isEqualTo("Pride and Prejudice"))
                .get();

        assertThat(bookId).isNotNull();

    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testAddBook() {
        final String addBook = """
                    mutation addBook($bookName:String!, $firstNameAuthor:String, $lastNameAuthor:String) {
                        addBook(bookName:$bookName, pages:12, author: {firstNameAuthor:$firstNameAuthor, lastNameAuthor:$lastNameAuthor, ageAuthor: 23}){
                            id
                            name
                            author {
                                firstName
                                lastName
                            }
                        }
                    }
                """;

        httpGraphQlTester.mutate()
                .build()
                .document(addBook)
                .variable("bookName", "Pride and Prejudice")
                .variable("firstNameAuthor", "Jane")
                .variable("lastNameAuthor", "Austen")
                .execute()
                .path("addBook")
                .matchesJson("""
                        {"name":"Pride and Prejudice","author":{"firstName":"Jane","lastName":"Austen"}}
                        """);
    }
}
