import axios from 'axios';

// Create configured Axios instance
const api = axios.create({
  baseURL: '/',
  timeout: 5000,
});

const mockMovies = [
  {
    id: 1,
    title: 'Interstellar',
    rating: 8.9,
    genre: 'Sci-Fi',
    duration: '2h 49m',
    year: 2014,
    description: 'A team of explorers travel through a wormhole in space in an attempt to ensure humanity\'s survival.',
    gradient: 'radial-gradient(circle at top left, #1e1b4b, #311042)'
  },
  {
    id: 2,
    title: 'Avengers: Endgame',
    rating: 8.4,
    genre: 'Action',
    duration: '3h 02m',
    year: 2019,
    description: 'After the devastating events of Infinity War, the universe is in ruins. With the help of remaining allies, the Avengers assemble once more.',
    gradient: 'radial-gradient(circle at top left, #450a0a, #1e1b4b)'
  },
  {
    id: 3,
    title: 'Avatar: The Way of Water',
    rating: 7.6,
    genre: 'Adventure',
    duration: '3h 12m',
    year: 2022,
    description: 'Jake Sully lives with his newfound family formed on the extrasolar moon Pandora. Once a familiar threat returns, Jake must work with Neytiri.',
    gradient: 'radial-gradient(circle at top left, #064e3b, #0c4a6e)'
  },
  {
    id: 4,
    title: 'Inception',
    rating: 8.8,
    genre: 'Thriller',
    duration: '2h 28m',
    year: 2010,
    description: 'A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea.',
    gradient: 'radial-gradient(circle at top left, #172554, #1e1e24)'
  },
  {
    id: 5,
    title: 'Spider-Man: Into the Spider-Verse',
    rating: 8.7,
    genre: 'Animation',
    duration: '1h 57m',
    year: 2018,
    description: 'Teen Miles Morales becomes the Spider-Man of his universe, and must join with five spider-powered individuals from other dimensions to stop a threat.',
    gradient: 'radial-gradient(circle at top left, #4c1d95, #831843)'
  },
  {
    id: 6,
    title: 'Dune: Part Two',
    rating: 8.9,
    genre: 'Sci-Fi',
    duration: '2h 46m',
    year: 2024,
    description: 'Paul Atreides unites with Chani and the Fremen while seeking revenge against the conspirators who destroyed his family.',
    gradient: 'radial-gradient(circle at top left, #78350f, #450a0a)'
  }
];

export const movieService = {
  getMovies: async () => {
    try {
      // In production, this will invoke the backend API
      const response = await api.get('/api/movies');
      return response.data;
    } catch (error) {
      // Fallback to static mock data when backend doesn't exist yet
      console.warn('Backend API /api/movies not active yet. Falling back to simulated static data.', error.message);
      return mockMovies;
    }
  },

  bookSeats: async (bookingData) => {
    try {
      // In production, this will post the booking to the backend API
      const response = await api.post('/api/booking', bookingData);
      return response.data;
    } catch (error) {
      console.warn('Backend API /api/booking not active yet. Simulating successful booking.', error.message);
      return { success: true, transactionId: `TX-${Math.floor(1000 + Math.random() * 9000)}`, ...bookingData };
    }
  }
};
