import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { movieService } from '../services/movieService';
import Button from '../components/Button';
import { CreditCard, Calendar, Clock, Monitor, CheckCircle } from 'lucide-react';
import './Booking.css';

const Booking = () => {
  const location = useLocation();
  const navigate = useNavigate();

  // If redirected from Movies page, retrieve selectedMovie from state. Else default to Interstellar.
  const defaultMovie = {
    id: 1,
    title: 'Interstellar',
    rating: 8.9,
    genre: 'Sci-Fi',
    duration: '2h 49m',
    year: 2014,
    gradient: 'radial-gradient(circle at top left, #1e1b4b, #311042)'
  };
  
  const movie = location.state?.selectedMovie || defaultMovie;
  const ticketPrice = 14.50;

  // Seating grid configuration
  const rows = ['A', 'B', 'C', 'D', 'E', 'F', 'G'];
  const seatsPerRow = 10;
  
  // Simulated occupied seats (indices start at 1)
  const [occupiedSeats] = useState(() => {
    return new Set(['A3', 'A4', 'C6', 'C7', 'E1', 'E2', 'F9', 'F10']);
  });

  const [selectedSeats, setSelectedSeats] = useState([]);
  const [bookingStatus, setBookingStatus] = useState(null); // null, 'booking', 'success'
  const [transactionId, setTransactionId] = useState('');

  const handleSeatClick = (seatId) => {
    if (occupiedSeats.has(seatId)) return; // Can't book occupied seats
    
    setSelectedSeats(prev => {
      if (prev.includes(seatId)) {
        return prev.filter(s => s !== seatId); // Remove
      } else {
        return [...prev, seatId]; // Add
      }
    });
  };

  const handleProceed = async () => {
    if (selectedSeats.length === 0) return;
    
    setBookingStatus('booking');
    
    const bookingData = {
      movieId: movie.id,
      movieTitle: movie.title,
      seats: selectedSeats.join(', '),
      amount: `$${(selectedSeats.length * ticketPrice).toFixed(2)}`,
      date: '2026-06-12',
      time: '19:30'
    };

    try {
      const response = await movieService.bookSeats(bookingData);
      setTimeout(() => {
        setTransactionId(response.transactionId);
        setBookingStatus('success');
      }, 1500); // Simulated delay for premium user experience
    } catch (err) {
      console.error(err);
      setBookingStatus(null);
    }
  };

  const handleDone = () => {
    // Navigate back to dashboard overview tab
    navigate('/dashboard');
  };

  const totalAmount = selectedSeats.length * ticketPrice;

  if (bookingStatus === 'success') {
    return (
      <div className="booking-container container">
        <div className="success-card glass-panel animate-fade-in text-center">
          <div className="success-icon-container">
            <CheckCircle size={64} className="success-icon" />
          </div>
          <h2>Booking Confirmed!</h2>
          <p className="success-msg">Your seats have been reserved successfully.</p>
          
          <div className="ticket-details-summary">
            <div className="ticket-field">
              <span className="tf-label">Movie Name</span>
              <span className="tf-value">{movie.title}</span>
            </div>
            <div className="ticket-field">
              <span className="tf-label">Seats Reserved</span>
              <span className="tf-value text-accent">{selectedSeats.join(', ')}</span>
            </div>
            <div className="ticket-field">
              <span className="tf-label">Total Amount Paid</span>
              <span className="tf-value">${totalAmount.toFixed(2)}</span>
            </div>
            <div className="ticket-field">
              <span className="tf-label">Transaction ID</span>
              <span className="tf-value text-secondary">{transactionId}</span>
            </div>
          </div>
          
          <Button variant="primary" onClick={handleDone} className="success-btn">
            Go to Dashboard
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="booking-container container">
      {/* Booking Layout */}
      <div className="booking-layout">
        
        {/* Left Column: Seating Grid */}
        <section className="booking-seating-section glass-panel">
          <header className="seating-header">
            <h3>Select Seats</h3>
            <div className="seating-legend">
              <div className="legend-item"><span className="legend-box seat-available"></span> Available</div>
              <div className="legend-item"><span className="legend-box seat-selected"></span> Selected</div>
              <div className="legend-item"><span className="legend-box seat-occupied"></span> Occupied</div>
            </div>
          </header>

          {/* Screen projection line */}
          <div className="screen-projection">
            <Monitor className="screen-icon" />
            <div className="screen-glow-line"></div>
            <span>SCREEN THIS WAY</span>
          </div>

          {/* Grid Layout */}
          <div className="seating-grid">
            {rows.map(row => (
              <div className="grid-row" key={row}>
                <span className="row-letter">{row}</span>
                <div className="row-seats">
                  {[...Array(seatsPerRow)].map((_, i) => {
                    const seatNum = i + 1;
                    const seatId = `${row}${seatNum}`;
                    const isOccupied = occupiedSeats.has(seatId);
                    const isSelected = selectedSeats.includes(seatId);

                    return (
                      <button
                        key={seatId}
                        className={`seat-btn ${isOccupied ? 'occupied' : ''} ${isSelected ? 'selected' : ''}`}
                        onClick={() => handleSeatClick(seatId)}
                        disabled={isOccupied}
                        title={`Seat ${seatId}`}
                      >
                        {seatNum}
                      </button>
                    );
                  })}
                </div>
                <span className="row-letter">{row}</span>
              </div>
            ))}
          </div>
        </section>

        {/* Right Column: Checkout Summary Panel */}
        <aside className="booking-checkout-panel glass-panel">
          <div 
            className="checkout-movie-banner"
            style={{ background: movie.gradient }}
          >
            <div className="banner-overlay">
              <h3>{movie.title}</h3>
              <p>{movie.genre} | {movie.duration}</p>
            </div>
          </div>

          <div className="checkout-details">
            <div className="info-row">
              <Calendar size={16} /> <span>Friday, June 12, 2026</span>
            </div>
            <div className="info-row">
              <Clock size={16} /> <span>07:30 PM (Evening Show)</span>
            </div>

            <hr className="divider" />

            <div className="selection-stats">
              <div className="stats-row">
                <span className="stats-label">Selected Seats</span>
                <span className="stats-value">
                  {selectedSeats.length > 0 ? selectedSeats.join(', ') : 'None'}
                </span>
              </div>
              <div className="stats-row">
                <span className="stats-label">Tickets Count</span>
                <span className="stats-value">{selectedSeats.length}</span>
              </div>
              <div className="stats-row">
                <span className="stats-label">Price per Ticket</span>
                <span className="stats-value">${ticketPrice.toFixed(2)}</span>
              </div>
            </div>

            <hr className="divider" />

            <div className="total-calculation">
              <span>Total Price</span>
              <span className="total-val">${totalAmount.toFixed(2)}</span>
            </div>

            <Button
              variant="primary"
              className="proceed-btn"
              disabled={selectedSeats.length === 0}
              loading={bookingStatus === 'booking'}
              onClick={handleProceed}
            >
              <CreditCard size={18} /> Proceed
            </Button>
          </div>
        </aside>

      </div>
    </div>
  );
};

export default Booking;
