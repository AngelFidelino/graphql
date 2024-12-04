package com.aflr.graphqlspring.dao;


import com.aflr.graphqlspring.entity.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public interface BookRepositoryPagination extends ReactiveSortingRepository<Book, Integer> {
    /*    @Autowired
        private R2dbcEntityTemplate entityTemplate;

        Window<Book> findBy(ScrollPosition position, Limit limit, Sort sort) {
            return null;
        }

        List<Book> findBy(Pageable pageable) {
            return null;
        }

        Window<Book> findByName(String name, ScrollPosition position, Limit limit, Sort sort) {
            return null;
        }*/
    //@Query("Select * from Book")
    Flux<Book> findAllBy(Pageable pageable);
}
