import axios from 'axios'

const api = axios.create({
  baseURL: ''  // axios envoie /api/comptes → Vite rewrite → /comptes → gateway
})

api.interceptors.request.use(config => {
  const user = JSON.parse(localStorage.getItem('user') || 'null')
  if (user?.pseudo) {
    config.headers['X-User-Pseudo'] = user.pseudo
  }
  return config
})

export default api