package com.aflr.graphqlspring.entity;

import com.aflr.graphqlspring.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
@Table("books")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    @Id
    private Integer id;
    private String name;
    private int pages;
    private LocalDate publishedAt;
    private Category category;
    private Integer authorId;
}
