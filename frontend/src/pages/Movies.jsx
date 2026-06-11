import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { movieService } from '../services/movieService';
import MovieCard from '../components/MovieCard';
import { Search, Film, SlidersHorizontal } from 'lucide-react';
import './Movies.css';

const Movies = () => {
  const [movies, setMovies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedGenre, setSelectedGenre] = useState('All');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchMovies = async () => {
      setLoading(true);
      try {
        const data = await movieService.getMovies();
        setMovies(data);
      } catch (err) {
        console.error('Failed to load movies:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchMovies();
  }, []);

  const handleBookNow = (movie) => {
    // Navigate to booking page and pass the selected movie object in route state
    navigate('/booking', { state: { selectedMovie: movie } });
  };

  // Extract unique genres
  const genres = ['All', ...new Set(movies.map(m => m.genre))];

  // Filter logic
  const filteredMovies = movies.filter(movie => {
    const matchesSearch = movie.title.toLowerCase().includes(searchTerm.toLowerCase()) || 
                          movie.description.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesGenre = selectedGenre === 'All' || movie.genre === selectedGenre;
    return matchesSearch && matchesGenre;
  });

  return (
    <div className="movies-catalog-container container">
      {/* Search and Filters Header */}
      <header className="catalog-header glass-panel">
        <div className="catalog-branding">
          <Film size={24} className="icon-glow" />
          <h2>Movie Catalog</h2>
        </div>
        
        <div className="catalog-controls">
          {/* Search bar */}
          <div className="search-bar">
            <Search className="search-icon" size={16} />
            <input
              type="text"
              placeholder="Search movies by title, details..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="form-input search-input"
            />
          </div>

          {/* Genre Filters dropdown on mobile or flex bar on desktop */}
          <div className="genre-filters">
            <SlidersHorizontal size={14} className="filter-icon" />
            <div className="filter-buttons">
              {genres.map(genre => (
                <button
                  key={genre}
                  onClick={() => setSelectedGenre(genre)}
                  className={`filter-btn ${selectedGenre === genre ? 'active' : ''}`}
                >
                  {genre}
                </button>
              ))}
            </div>
          </div>
        </div>
      </header>

      {/* Movie Grid */}
      {loading ? (
        <div className="loader-container">
          <div className="btn-spinner"></div>
          <p>Loading curated cinema selection...</p>
        </div>
      ) : filteredMovies.length > 0 ? (
        <div className="movies-grid">
          {filteredMovies.map(movie => (
            <MovieCard 
              key={movie.id} 
              movie={movie} 
              onBookClick={() => handleBookNow(movie)} 
            />
          ))}
        </div>
      ) : (
        <div className="empty-catalog glass-panel">
          <h3>No Movies Found</h3>
          <p>We couldn't find any movie matching "{searchTerm}" under "{selectedGenre}" genre.</p>
          <button onClick={() => { setSearchTerm(''); setSelectedGenre('All'); }} className="clear-filter-btn">
            Clear Filters
          </button>
        </div>
      )}
    </div>
  );
};

export default Movies;
