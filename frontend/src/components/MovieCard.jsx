import React from 'react';
import { Star, Clock, Calendar } from 'lucide-react';
import Button from './Button';
import './MovieCard.css';

const MovieCard = ({ movie, onBookClick }) => {
  const { title, rating, genre, duration, year, description, gradient } = movie;

  // Calculate full/half stars based on rating (out of 10)
  const starCount = Math.round(rating / 2);

  return (
    <div className="movie-card glass-panel glass-panel-glow animate-fade-in">
      {/* Dynamic Animated Gradient Poster Placeholder */}
      <div 
        className="movie-poster" 
        style={{ background: gradient || 'linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%)' }}
      >
        <div className="poster-overlay">
          <span className="movie-year-badge"><Calendar size={12} /> {year}</span>
          <span className="movie-genre-badge">{genre}</span>
        </div>
        <div className="poster-content">
          <h3 className="poster-title">{title}</h3>
        </div>
      </div>

      {/* Movie Details */}
      <div className="movie-info">
        <div className="movie-meta-row">
          <span className="movie-duration">
            <Clock size={14} /> {duration}
          </span>
          <div className="movie-rating" title={`Rating: ${rating}/10`}>
            <div className="stars">
              {[...Array(5)].map((_, i) => (
                <Star 
                  key={i} 
                  size={14} 
                  fill={i < starCount ? 'var(--color-warning)' : 'none'} 
                  color={i < starCount ? 'var(--color-warning)' : 'var(--text-muted)'} 
                />
              ))}
            </div>
            <span className="rating-val">{rating.toFixed(1)}</span>
          </div>
        </div>

        <p className="movie-description">{description}</p>

        <Button 
          variant="primary" 
          onClick={onBookClick} 
          className="movie-book-btn"
        >
          Book Now
        </Button>
      </div>
    </div>
  );
};

export default MovieCard;
