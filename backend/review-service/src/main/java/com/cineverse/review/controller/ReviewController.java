package com.cineverse.review.controller;

import com.cineverse.review.dto.StandardResponse;
import com.cineverse.review.model.Review;
import com.cineverse.review.repository.ReviewRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@Validated
public class ReviewController {

    private final ReviewRepository reviewRepository;

    public ReviewController(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @PostMapping
    public ResponseEntity<StandardResponse<Review>> addReview(@Valid @RequestBody Review review) {
        Review savedReview = reviewRepository.save(review);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponse.success("Review added successfully", savedReview));
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<StandardResponse<List<Review>>> getReviewsByMovieId(@PathVariable String movieId) {
        List<Review> reviews = reviewRepository.findByMovieId(movieId);
        return ResponseEntity.ok(StandardResponse.success("Reviews fetched successfully", reviews));
    }

    @GetMapping("/average/{movieId}")
    public ResponseEntity<StandardResponse<Double>> getAverageRating(@PathVariable String movieId) {
        List<Review> reviews = reviewRepository.findByMovieId(movieId);
        if (reviews.isEmpty()) {
            return ResponseEntity.ok(StandardResponse.success("No reviews found, average is 0.0", 0.0));
        }

        double sum = 0;
        for (Review r : reviews) {
            sum += r.getRating();
        }
        double average = sum / reviews.size();
        return ResponseEntity.ok(StandardResponse.success("Average rating fetched successfully", average));
    }
}
