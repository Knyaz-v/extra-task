package com.extra.task.controller;

import com.extra.task.dto.GenreRequest;
import com.extra.task.model.Genre;
import com.extra.task.service.LibraryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    private final LibraryService service;

    public GenreController(LibraryService service) {
        this.service = service;
    }

    @GetMapping
    public List<Genre> all() {
        return service.getAllGenres();
    }

    @PostMapping
    public Genre add(@RequestBody GenreRequest req) {
        return service.addGenre(req.getName());
    }

    @PutMapping("/{id}")
    public Genre update(@PathVariable Long id, @RequestBody GenreRequest req) {
        return service.updateGenre(id, req.getName());
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        service.deleteGenre(id);
        return Map.of("message", "Deleted successfully");
    }
}