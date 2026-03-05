import api from './api'
export default {
  // GET /reservations/me
  getMesReservations: () =>
    api.get('/reservations/me'),

  // POST /reservations/{titreFilm} — pas de body
  reserver: (titreFilm) =>
    api.post(`/reservations/${encodeURIComponent(titreFilm)}`),

  // DELETE /reservations/{titreFilm}
  terminerLocation: (titreFilm) =>
    api.delete(`/reservations/${encodeURIComponent(titreFilm)}`),
}