import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  {
    path: '/',
    component: () => import('../views/HomeView.vue')
  },
  {
    path: '/login',
    component: () => import('../views/LoginView.vue')
  },
  {
    path: '/register',
    component: () => import('../views/RegisterView.vue')
  },
  {
    path: '/films',
    component: () => import('../views/FilmsView.vue')
  },
  {
    path: '/films/:id',
    component: () => import('../views/FilmDetailView.vue')
  },
  {
    // Liste des comptes — public (visible sans connexion)
    path: '/comptes',
    component: () => import('../views/ComptesView.vue')
  },
  {
    path: '/reservations',
    component: () => import('../views/ReservationsView.vue'),
    meta: { requiresAuth: true }
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Nouvelle syntaxe sans next()
router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.isLoggedIn) return '/login'

})

export default router