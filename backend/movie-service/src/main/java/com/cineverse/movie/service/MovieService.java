package com.cineverse.movie.service;

import com.cineverse.movie.dto.AverageRatingResponse;
import com.cineverse.movie.dto.MovieDetailResponse;
import com.cineverse.movie.dto.ReviewDto;
import com.cineverse.movie.dto.ReviewListResponse;
import com.cineverse.movie.model.Movie;
import com.cineverse.movie.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final WebClient reviewWebClient;
    private final Path fileStorageLocation;

    public MovieService(MovieRepository movieRepository, 
                        WebClient reviewWebClient,
                        @Value("${file.upload-dir}") String uploadDir) {
        this.movieRepository = movieRepository;
        this.reviewWebClient = reviewWebClient;
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public Movie createMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    public Page<Movie> getAllMovies(int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by(sortBy);
        if ("DESC".equalsIgnoreCase(direction)) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        return movieRepository.findAll(pageable);
    }

    public MovieDetailResponse getMovieById(String id) {
        Optional<Movie> movieOpt = movieRepository.findById(id);
        if (movieOpt.isEmpty()) {
            throw new RuntimeException("Movie not found with ID: " + id);
        }
        Movie movie = movieOpt.get();

        // Asynchronously call review-service for reviews and average rating using WebClient
        Mono<List<ReviewDto>> reviewsMono = reviewWebClient.get()
                .uri("/reviews/" + id)
                .retrieve()
                .bodyToMono(ReviewListResponse.class)
                .map(ReviewListResponse::getData)
                .onErrorReturn(Collections.emptyList());

        Mono<Double> avgRatingMono = reviewWebClient.get()
                .uri("/reviews/average/" + id)
                .retrieve()
                .bodyToMono(AverageRatingResponse.class)
                .map(AverageRatingResponse::getData)
                .onErrorReturn(0.0);

        // Wait for both reactive calls to complete
        List<ReviewDto> reviews = reviewsMono.block();
        Double averageRating = avgRatingMono.block();

        // Enforce update in movie rating cache
        if (averageRating != null && averageRating > 0.0) {
            movie.setRating(averageRating);
            movieRepository.save(movie);
        }

        return new MovieDetailResponse(movie, reviews, averageRating);
    }

    public Movie updateMovie(String id, Movie updatedMovie) {
        Optional<Movie> movieOpt = movieRepository.findById(id);
        if (movieOpt.isEmpty()) {
            throw new RuntimeException("Movie not found with ID: " + id);
        }
        Movie movie = movieOpt.get();
        movie.setTitle(updatedMovie.getTitle());
        movie.setGenre(updatedMovie.getGenre());
        movie.setLanguage(updatedMovie.getLanguage());
        movie.setDuration(updatedMovie.getDuration());
        movie.setReleaseDate(updatedMovie.getReleaseDate());
        movie.setPosterUrl(updatedMovie.getPosterUrl());
        return movieRepository.save(movie);
    }

    public void deleteMovie(String id) {
        movieRepository.deleteById(id);
    }

    public List<Movie> searchMoviesByTitle(String title) {
        return movieRepository.findByTitleContainingIgnoreCase(title);
    }

    public String storeFile(MultipartFile file) {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        try {
            if (fileName.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation);
            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }
}
