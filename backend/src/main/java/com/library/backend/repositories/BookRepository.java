package com.library.backend.repositories;

import com.library.backend.entities.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    Page<Book> findAllByOrderByRatingDesc(Pageable pageable);

    Optional<Book> findByTitleAndAuthorAndCategoryAndPublisherAndYearPublishedAndAvailableCopies(
            String title,
            String author,
            String category,
            String publisher,
            Integer yearPublished,
            Integer availableCopies
    );

}
