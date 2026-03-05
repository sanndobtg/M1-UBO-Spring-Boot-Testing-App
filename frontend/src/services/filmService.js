import api from './api'

export default {
  getAll: () => api.get('/movies'),

  getById: (id) => api.get(`/movies/${id}`),

  create: (film) => api.post('/movies', film),

  delete: (id) => api.delete(`/movies/${id}`)
}