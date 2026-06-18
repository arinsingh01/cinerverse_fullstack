package com.cineverse.movie.dto;

import com.cineverse.movie.model.Movie;
import java.util.List;

public class MovieDetailResponse {
    private Movie movie;
    private List<ReviewDto> reviews;
    private Double averageRating;

    public MovieDetailResponse() {}

    public MovieDetailResponse(Movie movie, List<ReviewDto> reviews, Double averageRating) {
        this.movie = movie;
        this.reviews = reviews;
        this.averageRating = averageRating;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public List<ReviewDto> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewDto> reviews) {
        this.reviews = reviews;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }
}
