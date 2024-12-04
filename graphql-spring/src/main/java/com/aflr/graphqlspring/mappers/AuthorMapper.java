package com.aflr.graphqlspring.mappers;

import com.aflr.graphqlspring.entity.Author;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AuthorMapper {
    AuthorMapper INSTANCE = Mappers.getMapper(AuthorMapper.class);

    Author toEntity(String firstName, String lastName, int age);
}
