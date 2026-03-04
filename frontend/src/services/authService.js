import api from './api'
export default {
  register: (data) => api.post('/comptes', data),
  login: (pseudo, motDePasse) => api.post('/comptes/login', { pseudo, motDePasse }),
  getAll: () => api.get('/comptes'),
  getById: (id) => api.get(`/comptes/${id}`),
  delete: (id) => api.delete(`/comptes/${id}`),
    update:   (id, data)    => api.put(`/comptes/${id}`, data),
}