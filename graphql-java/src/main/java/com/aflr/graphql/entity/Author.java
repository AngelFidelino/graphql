package com.aflr.graphql.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("authors")
@Data
public class Author {
    @Id
    private Integer id;
    private String firstName;
    private String lastName;
    private int age;
    private int bookId;
}
