import api from './api'
export default {
  // GET /reviews/{titreFilm} — public, pas d'auth
  getByFilm: (titreFilm) =>
    api.get(`/reviews/${encodeURIComponent(titreFilm)}`),

  // GET /reviews/{titreFilm}/average — public
  getAverage: (titreFilm) =>
    api.get(`/reviews/${encodeURIComponent(titreFilm)}/average`),

  // POST /reviews/{titreFilm} — nécessite X-User-Pseudo
  addReview: (titreFilm, data) =>
    api.post(`/reviews/${encodeURIComponent(titreFilm)}`, data),
}