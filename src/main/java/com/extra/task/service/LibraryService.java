package com.extra.task.service;

import com.extra.task.model.*;
import com.extra.task.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LibraryService {

    private final BookRepository bookRepo;
    private final GenreRepository genreRepo;
    private final LoanRepository loanRepo;

    public LibraryService(BookRepository bookRepo,
                          GenreRepository genreRepo,
                          LoanRepository loanRepo) {
        this.bookRepo = bookRepo;
        this.genreRepo = genreRepo;
        this.loanRepo = loanRepo;
    }

    @Transactional(readOnly = true)
    public List<Book> getBooksByGenre(Long genreId) {
        return bookRepo.findByGenreId(genreId);
    }

    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepo.findAll();
    }

    @Transactional(readOnly = true)
    public Book getBook(Long bookId) {
        return bookRepo.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Book not found"));
    }

    @Transactional(readOnly = true)
    public List<Genre> getAllGenres() {
        return genreRepo.findAll();
    }

    @Transactional(readOnly = true)
    public List<Loan> getAllLoans() {
        return loanRepo.findAll();
    }

    @Transactional(readOnly = true)
    public List<Loan> getActiveLoans() {
        return loanRepo.findByReturnedAtIsNull();
    }

    @Transactional
    public Genre addGenre(String name) {
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Genre name is required");
        }
        String trimmed = name.trim();
        if (genreRepo.existsByName(trimmed)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Genre already exists");
        }
        Genre g = new Genre();
        g.setName(trimmed);
        return genreRepo.save(g);
    }

    @Transactional
    public Genre updateGenre(Long genreId, String name) {
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Genre name is required");
        }
        Genre genre = genreRepo.findById(genreId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Genre not found"));

        String trimmed = name.trim();
        if (!trimmed.equals(genre.getName()) && genreRepo.existsByName(trimmed)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Genre already exists");
        }
        genre.setName(trimmed);
        return genreRepo.save(genre);
    }

    @Transactional
    public void deleteGenre(Long genreId) {
        Genre genre = genreRepo.findById(genreId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Genre not found"));

        if (bookRepo.existsByGenreId(genreId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Cannot delete: genre has books");
        }
        genreRepo.delete(genre);
    }

    @Transactional
    public Book addBook(String title, String author, Long genreId) {
        if (title == null || title.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Book title is required");
        }
        if (genreId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Genre is required");
        }
        Genre genre = genreRepo.findById(genreId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Genre not found"));

        Book book = new Book();
        book.setTitle(title.trim());
        book.setAuthor(author);
        book.setGenre(genre);
        book.setAvailable(true);
        return bookRepo.save(book);
    }

    @Transactional
    public Book updateBook(Long bookId, String title, String author, Long genreId) {
        if (title == null || title.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Book title is required");
        }
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Book not found"));

        book.setTitle(title.trim());
        book.setAuthor(author);
        if (genreId != null) {
            Genre genre = genreRepo.findById(genreId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Genre not found"));
            book.setGenre(genre);
        }
        return bookRepo.save(book);
    }

    @Transactional
    public void deleteBook(Long bookId) {
        bookRepo.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Book not found"));

        if (!loanRepo.findByBookIdAndReturnedAtIsNull(bookId).isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Cannot delete: book is currently borrowed");
        }
        bookRepo.deleteById(bookId);
    }

    @Transactional
    public Loan borrowBook(Long bookId, String borrowerName) {
        if (borrowerName == null || borrowerName.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Borrower name is required");
        }
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Book not found"));

        if (!book.isAvailable()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Book is already borrowed");
        }
        if (!loanRepo.findByBookIdAndReturnedAtIsNull(bookId).isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Book is already borrowed");
        }

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setBorrowerName(borrowerName.trim());
        loan.setBorrowedAt(LocalDateTime.now());

        book.setAvailable(false);
        bookRepo.save(book);

        return loanRepo.save(loan);
    }

    @Transactional
    public Loan returnBook(Long loanId) {
        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Loan not found"));
        if (loan.getReturnedAt() != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Book already returned");
        }

        loan.setReturnedAt(LocalDateTime.now());

        Book book = loan.getBook();
        if (book != null) {
            book.setAvailable(true);
            bookRepo.save(book);
        }

        return loanRepo.save(loan);
    }
}