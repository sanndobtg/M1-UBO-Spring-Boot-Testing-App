import api from './api'

export default {
  getByFilm: (titreFilm) =>
    api.get(`/posters/${encodeURIComponent(titreFilm)}`),

  getById: (posterId) =>
    api.get(`/posters/${posterId}`),

  addPoster: (titreFilm, imageUrl) =>
    api.post(`/posters/${encodeURIComponent(titreFilm)}`, { nom: imageUrl }),

  updatePoster: (posterId, imageUrl) =>
    api.put(`/posters/${posterId}`, { nom: imageUrl }),

  deletePoster: (posterId) =>
    api.delete(`/posters/${posterId}`),
}