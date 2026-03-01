import api from './api'
export default {
  getByFilm: (filmId) => api.get(`/reviews/film/${filmId}`),
  addReview: (filmId, data) => api.post(`/reviews/film/${filmId}`, data),
}