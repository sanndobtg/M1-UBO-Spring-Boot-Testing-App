import api from './api'
export default {
  getAll: () => api.get('/films'),
  getById: (id) => api.get(`/films/${id}`),
}