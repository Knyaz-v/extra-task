package com.extra.task.repository;

import com.extra.task.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByGenreId(Long genreId);
    boolean existsByGenreId(Long genreId);
}