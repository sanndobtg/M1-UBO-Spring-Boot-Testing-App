import api from './api'
export default {
  getMesReservations: () => api.get('/reservations/me'),
  reserver: (titreFilm) => api.post(`/reservations/${titreFilm}`),
  terminerLocation: (titreFilm) => api.delete(`/reservations/${titreFilm}`),
}