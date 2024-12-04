package com.aflr.graphqlspring.mappers;

import com.aflr.graphqlspring.entity.Book;
import com.aflr.graphqlspring.enums.Category;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

@Mapper
public interface BookMapper {
    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    Book toEntity(String name, Integer pages, LocalDate publishedAt, Category category);
}
