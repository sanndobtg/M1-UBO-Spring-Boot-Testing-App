import { defineStore } from 'pinia'
import authService from '../services/authService'

export const useAuthStore = defineStore('auth', {
  state: () => {
    let user = null
    try {
      const stored = localStorage.getItem('user')
      if (stored && stored !== 'undefined') user = JSON.parse(stored)
    } catch { user = null }

    return {
      token: localStorage.getItem('token') || null,
      user,
    }
  },

  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.user?.role === 'ADMIN',
  },

  actions: {
    async login(pseudo, motDePasse) {
      const res = await authService.login(pseudo, motDePasse)
      // L'API retourne directement l'objet compte
      this.user = res.data
      this.token = res.data.pseudo  // pas de JWT → on utilise le pseudo comme token
      localStorage.setItem('token', this.token)
      localStorage.setItem('user', JSON.stringify(this.user))
    },

    logout() {
      this.token = null
      this.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }
})