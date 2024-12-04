package com.aflr.graphqlspring.integration;

import com.aflr.graphqlspring.dao.AuthorRepository;
import com.aflr.graphqlspring.entity.Author;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("integration-test")
@AutoConfigureGraphQlTester
@WithMockUser(roles = {"USER", "ADMIN"})
public class AuthorControllerTests {
    @Autowired
    private GraphQlTester graphQlTester;

    @Autowired
    private AuthorRepository authorRepository;


    @Test
    void testAddNewAuthor() {
        Author newAuthor = new Author();
        newAuthor.setAge(89);
        newAuthor.setFirstName(UUID.randomUUID().toString());

        final Author saved = authorRepository.save(newAuthor).block();

        final List<Author> fetched = graphQlTester
                .documentName("getAllAuthors")
                .execute()
                .path("data.getAuthors[*]")
                .entityList(Author.class)
                .satisfies(System.out::println)
                .get();

        assertNotNull(fetched);
        assertThat(fetched).anyMatch(savedAuthor -> saved.getFirstName().equals(savedAuthor.getFirstName()));
    }

    @Test
    void testGetAuthorNameByID_with_documentName() {
        Author newAuthor = new Author();
        newAuthor.setAge(89);
        newAuthor.setFirstName(UUID.randomUUID().toString());

        final Author saved = authorRepository.save(newAuthor).block();

        String authorNameQuery = """
                {
                    getAuthorById(id: %d) { 
                        firstName
                    } 
                }
                """.formatted(saved.getId());

        graphQlTester
                .document(authorNameQuery)
                .execute()
                .path("getAuthorById.firstName")
                .entity(String.class)
                .satisfies(name -> assertNotNull(name));
    }

    @Test
    public void testAddBook() {
        final String addAuthor = """
                    mutation addAuthor($firstNameAuthor: String, $lastNameAuthor: String, $ageAuthor: NonNegativeInt) {
                    	addAuthor(author: {
                    			firstNameAuthor: $firstNameAuthor,
                    			lastNameAuthor: $lastNameAuthor,
                    			ageAuthor: $ageAuthor
                    			}
                    		)
                    	{
                    		firstName
                    		lastName
                    	}
                    }
                """;

        graphQlTester
                .document(addAuthor)
                .variable("firstNameAuthor", "Jane")
                .variable("lastNameAuthor", "Austen")
                .variable("ageAuthor", "46")
                .execute()
                .path("addAuthor")
                .matchesJson("""
                        {"firstName":"Jane","lastName":"Austen"}
                        """);
    }
}
