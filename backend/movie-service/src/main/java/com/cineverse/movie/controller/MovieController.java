package com.cineverse.movie.controller;

import com.cineverse.movie.dto.MovieDetailResponse;
import com.cineverse.movie.dto.StandardResponse;
import com.cineverse.movie.model.Movie;
import com.cineverse.movie.service.MovieService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping
    public ResponseEntity<StandardResponse<Movie>> createMovie(@RequestBody Movie movie) {
        Movie savedMovie = movieService.createMovie(movie);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponse.success("Movie created successfully", savedMovie));
    }

    @GetMapping
    public ResponseEntity<StandardResponse<Page<Movie>>> getAllMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        Page<Movie> movies = movieService.getAllMovies(page, size, sortBy, direction);
        return ResponseEntity.ok(StandardResponse.success("Movies fetched successfully", movies));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<MovieDetailResponse>> getMovieById(@PathVariable String id) {
        MovieDetailResponse details = movieService.getMovieById(id);
        return ResponseEntity.ok(StandardResponse.success("Movie details fetched successfully", details));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse<Movie>> updateMovie(@PathVariable String id, @RequestBody Movie movie) {
        Movie updated = movieService.updateMovie(id, movie);
        return ResponseEntity.ok(StandardResponse.success("Movie updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse<Void>> deleteMovie(@PathVariable String id) {
        movieService.deleteMovie(id);
        return ResponseEntity.ok(StandardResponse.success("Movie deleted successfully", null));
    }

    @GetMapping("/search")
    public ResponseEntity<StandardResponse<List<Movie>>> searchMovies(@RequestParam String title) {
        List<Movie> results = movieService.searchMoviesByTitle(title);
        return ResponseEntity.ok(StandardResponse.success("Movies search completed", results));
    }

    @PostMapping("/upload")
    public ResponseEntity<StandardResponse<String>> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = movieService.storeFile(file);
        return ResponseEntity.ok(StandardResponse.success("File uploaded successfully", fileName));
    }
}
