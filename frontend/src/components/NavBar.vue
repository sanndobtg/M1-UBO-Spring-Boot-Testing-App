<template>
  <nav class="navbar" :class="{ scrolled: isScrolled }">
    <div class="navbar-inner">

      <RouterLink to="/" class="logo">
        <span class="logo-bracket">[</span>
        <span class="logo-text glitch-text">MPS<span class="logo-accent">_SYS</span></span>
        <span class="logo-bracket">]</span>
        <span class="logo-blink">▮</span>
      </RouterLink>

      <div class="nav-links">
        <RouterLink to="/" class="nav-link" exact-active-class="active">
          <span class="nav-prefix">01.</span> Accueil
        </RouterLink>
        <RouterLink to="/films" class="nav-link" active-class="active">
          <span class="nav-prefix">02.</span> Films
        </RouterLink>
        <RouterLink to="/comptes" class="nav-link" active-class="active">
          <span class="nav-prefix">03.</span> Comptes
        </RouterLink>
        <RouterLink v-if="auth.isLoggedIn" to="/reservations" class="nav-link" active-class="active">
          <span class="nav-prefix">04.</span> Mes Locations
        </RouterLink>
        <RouterLink v-if="auth.isAdmin" to="/admin" class="nav-link admin-link" active-class="active">
          <span class="nav-prefix">05.</span> Admin
        </RouterLink>
      </div>

      <div class="nav-auth">
        <template v-if="auth.isLoggedIn">
          <div class="user-badge">
            <span class="user-dot"></span>
            <span class="username">{{ auth.user?.pseudo }}</span>
          </div>
          <button class="btn-cyber btn-cyber-pink" @click="logout">
            <span>Déconnexion</span>
          </button>
        </template>
        <template v-else>
          <RouterLink to="/login" class="btn-cyber"><span>Connexion</span></RouterLink>
          <RouterLink to="/register" class="btn-cyber btn-cyber-pink"><span>S'inscrire</span></RouterLink>
        </template>
      </div>

      <button class="mobile-toggle" @click="mobileOpen = !mobileOpen">☰</button>
    </div>

    <Transition name="mobile-menu">
      <div v-if="mobileOpen" class="mobile-menu">
        <RouterLink to="/" class="mobile-link" @click="mobileOpen = false">Accueil</RouterLink>
        <RouterLink to="/films" class="mobile-link" @click="mobileOpen = false">Films</RouterLink>
        <RouterLink to="/comptes" class="mobile-link" @click="mobileOpen = false">Comptes</RouterLink>
        <RouterLink v-if="auth.isLoggedIn" to="/reservations" class="mobile-link" @click="mobileOpen = false">Mes Locations</RouterLink>
        <RouterLink v-if="auth.isAdmin" to="/admin" class="mobile-link" @click="mobileOpen = false">Admin</RouterLink>
        <div class="mobile-divider" />
        <template v-if="auth.isLoggedIn">
          <button class="mobile-link" @click="logout">Déconnexion</button>
        </template>
        <template v-else>
          <RouterLink to="/login" class="mobile-link" @click="mobileOpen = false">Connexion</RouterLink>
          <RouterLink to="/register" class="mobile-link" @click="mobileOpen = false">S'inscrire</RouterLink>
        </template>
      </div>
    </Transition>
  </nav>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
const router = useRouter()
const isScrolled = ref(false)
const mobileOpen = ref(false)

const onScroll = () => { isScrolled.value = window.scrollY > 20 }
onMounted(() => window.addEventListener('scroll', onScroll))
onUnmounted(() => window.removeEventListener('scroll', onScroll))

const logout = () => { auth.logout(); router.push('/'); mobileOpen.value = false }
</script>

<style scoped>
.navbar {
  position: fixed; top: 0; left: 0; right: 0; z-index: 1000; height: 70px;
  background: rgba(2, 4, 8, 0.7); backdrop-filter: blur(20px);
  border-bottom: 1px solid var(--border-subtle); transition: all 0.3s ease;
}

.navbar.scrolled { border-bottom-color: var(--border-glow); box-shadow: 0 0 30px rgba(0, 245, 255, 0.08); }

.navbar-inner {
  max-width: 1400px; margin: 0 auto; height: 100%;
  padding: 0 2rem; display: flex; align-items: center; gap: 2rem;
}

.logo { text-decoration: none; display: flex; align-items: center; gap: 2px; flex-shrink: 0; }
.logo-bracket { font-family: var(--font-mono); font-size: 1.4rem; color: var(--neon-cyan); opacity: 0.6; }
.logo-text { font-family: var(--font-display); font-size: 1.1rem; font-weight: 900; letter-spacing: 0.1em; color: var(--text-primary); }
.logo-accent { color: var(--neon-cyan); text-shadow: 0 0 10px var(--neon-cyan); }
.logo-blink { font-family: var(--font-mono); color: var(--neon-cyan); font-size: 1rem; margin-left: 2px; animation: pulse-neon 1s ease-in-out infinite; }

.nav-links { display: flex; gap: 0.25rem; flex: 1; }

.nav-link {
  font-family: var(--font-body); font-size: 0.85rem; font-weight: 500;
  letter-spacing: 0.05em; color: var(--text-secondary); text-decoration: none;
  padding: 6px 12px; border-radius: 4px; transition: all 0.2s ease;
}

.nav-prefix { font-family: var(--font-mono); font-size: 0.65rem; color: var(--neon-cyan); margin-right: 4px; opacity: 0.7; }
.nav-link:hover, .nav-link.active { color: var(--text-primary); background: rgba(0, 245, 255, 0.05); }
.nav-link.active { color: var(--neon-cyan); }
.admin-link { color: var(--neon-orange) !important; }
.admin-link:hover, .admin-link.active { color: var(--neon-orange) !important; background: rgba(255, 107, 0, 0.05) !important; }

.nav-auth { display: flex; align-items: center; gap: 0.75rem; flex-shrink: 0; }

.user-badge {
  display: flex; align-items: center; gap: 8px; padding: 6px 14px;
  border: 1px solid var(--border-subtle); border-radius: 4px; background: rgba(0, 245, 255, 0.03);
}

.user-dot {
  width: 6px; height: 6px; border-radius: 50%;
  background: var(--neon-green); box-shadow: 0 0 6px var(--neon-green);
  animation: pulse-neon 2s infinite;
}

.username { font-family: var(--font-mono); font-size: 0.8rem; color: var(--neon-cyan); letter-spacing: 0.05em; }

.mobile-toggle { display: none; background: none; border: none; color: var(--neon-cyan); font-size: 1.4rem; cursor: pointer; margin-left: auto; }

.mobile-menu {
  background: rgba(6, 13, 20, 0.98); border-top: 1px solid var(--border-glow);
  padding: 1rem; display: flex; flex-direction: column; gap: 0.25rem;
}

.mobile-link {
  display: block; font-family: var(--font-body); font-size: 1rem; color: var(--text-secondary);
  text-decoration: none; padding: 12px 16px; border-radius: 4px; transition: all 0.2s;
  background: none; border: none; cursor: pointer; text-align: left; width: 100%;
}

.mobile-link:hover { color: var(--neon-cyan); background: rgba(0, 245, 255, 0.05); }
.mobile-divider { height: 1px; background: var(--border-subtle); margin: 0.5rem 0; }
.mobile-menu-enter-active, .mobile-menu-leave-active { transition: all 0.2s ease; }
.mobile-menu-enter-from, .mobile-menu-leave-to { opacity: 0; transform: translateY(-10px); }

@media (max-width: 900px) {
  .nav-links, .nav-auth { display: none; }
  .mobile-toggle { display: block; }
}
</style>