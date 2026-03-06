import api from './api'

const adapt = (f) => ({
  id: f.id,
  titre: f.title,
  annee: f.yearCompletion,
  realisateur: f.director,
  acteurs: f.actors || [],
  genres: f.genres || [],
  ageMin: f.minimumAge,
  ouvert: f.openForRent,
  posters: f.posters || [],
  prix: 3.99,
})

export default {
  getAll: async () => {
    const res = await api.get('/movies')   // ← /movies directement
    return { data: (res.data || []).map(adapt) }
  },
  getById: async (id) => {
    const res = await api.get(`/movies/${id}`)
    return { data: adapt(res.data) }
  },
}