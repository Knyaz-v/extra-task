package com.extra.task.controller;

import com.extra.task.dto.BookRequest;
import com.extra.task.model.Book;
import com.extra.task.service.LibraryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final LibraryService service;

    public BookController(LibraryService service) {
        this.service = service;
    }

    @GetMapping
    public List<Book> list(@RequestParam(required = false) Long genreId) {
        return genreId == null ? service.getAllBooks() : service.getBooksByGenre(genreId);
    }

    @GetMapping("/{id}")
    public Book byId(@PathVariable Long id) {
        return service.getBook(id);
    }

    @PostMapping
    public Book add(@RequestBody BookRequest req) {
        return service.addBook(req.getTitle(), req.getAuthor(), req.getGenreId());
    }

    @PutMapping("/{id}")
    public Book update(@PathVariable Long id, @RequestBody BookRequest req) {
        return service.updateBook(id, req.getTitle(), req.getAuthor(), req.getGenreId());
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        service.deleteBook(id);
        return Map.of("message", "Deleted successfully");
    }
}