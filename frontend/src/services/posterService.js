import axios from 'axios'

const API_URL = 'http://info-tpsi:11084/posters'

export default {

  getByFilm(titre) {
    return axios.get(`${API_URL}/${encodeURIComponent(titre)}`)
  },

  getById(id) {
    return axios.get(`${API_URL}/${id}`)
  },

  create(titre, nom) {
    return axios.post(`${API_URL}/${encodeURIComponent(titre)}`, {
      nom: nom
    })
  },

  update(id, nom) {
    return axios.put(`${API_URL}/${id}`, {
      nom: nom
    })
  },

  delete(id) {
    return axios.delete(`${API_URL}/${id}`)
  }

}