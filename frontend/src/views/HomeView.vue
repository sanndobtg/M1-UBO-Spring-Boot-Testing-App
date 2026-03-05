<template>
  <div class="home">

    <!-- Hero -->
    <section class="hero">
      <div class="hero-bg">
        <div class="hero-orb orb-1"></div>
        <div class="hero-orb orb-2"></div>
        <div class="hero-orb orb-3"></div>
      </div>

      <div class="hero-content">

        <!-- Session active banner -->
        <Transition name="slide-down">
          <div v-if="auth.isLoggedIn" class="session-banner">
            <span class="session-dot"></span>
            <span class="session-text">SESSION ACTIVE</span>
            <span class="session-divider">|</span>
            <span class="session-user">{{ auth.user?.pseudo }}</span>
            <span class="session-divider">|</span>
            <span class="session-role">{{ auth.user?.role || 'USER' }}</span>
          </div>
        </Transition>

        <div class="hero-eyebrow">
          <span class="eyebrow-line"></span>
          <span class="eyebrow-text">Système de vidéo à la demande</span>
          <span class="eyebrow-line right"></span>
        </div>

        <!-- Title changes based on auth -->
        <h1 class="hero-title">
          <template v-if="auth.isLoggedIn">
            <span class="title-line-1">BIENVENUE</span>
            <span class="title-line-2 glitch-text"><span class="title-accent">{{ auth.user?.pseudo?.toUpperCase() }}</span></span>
          </template>
          <template v-else>
            <span class="title-line-1">STREAM</span>
            <span class="title-line-2">THE<span class="title-accent"> FUTURE</span></span>
          </template>
        </h1>

        <p class="hero-description">
          <template v-if="auth.isLoggedIn">
            Vous avez <span class="highlight">{{ reservationCount }}/3</span> locations en cours.
            Explorez le catalogue et réservez vos prochains films.
          </template>
          <template v-else>
            Accédez à des milliers de films en location. Une expérience cinématographique
            réinventée pour l'ère numérique.
          </template>
        </p>

        <div class="hero-actions">
          <RouterLink to="/films" class="btn-cyber btn-large">
            <span>Explorer les films</span>
          </RouterLink>
          <template v-if="auth.isLoggedIn">
            <RouterLink to="/reservations" class="btn-cyber btn-cyber-pink btn-large">
              <span>Mes Locations</span>
            </RouterLink>
          </template>
          <template v-else>
            <RouterLink to="/register" class="btn-cyber btn-cyber-pink btn-large">
              <span>Créer un compte</span>
            </RouterLink>
          </template>
        </div>

        <div class="hero-stats">
          <div class="stat">
            <span class="stat-value neon-text">∞</span>
            <span class="stat-label">Films disponibles</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat">
            <span class="stat-value neon-text">4K</span>
            <span class="stat-label">Qualité maximale</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat">
            <span class="stat-value neon-text">3</span>
            <span class="stat-label">Locations simultanées</span>
          </div>
        </div>
      </div>

      <div class="scroll-indicator">
        <span class="scroll-text">SCROLL</span>
        <div class="scroll-line"></div>
      </div>
    </section>

    <!-- Dashboard utilisateur — visible seulement si connecté -->
    <Transition name="fade">
      <section v-if="auth.isLoggedIn" class="user-dashboard">
        <div class="container">
          <h2 class="section-title">Mon espace</h2>
          <div class="dashboard-grid">

            <!-- Profil -->
            <div class="dash-card cyber-card profile-card">
              <div class="dash-card-header">
                <span class="dash-icon cyan">◈</span>
                <span class="dash-label">PROFIL</span>
              </div>
              <div class="profile-info">
                <div class="profile-avatar">
                  {{ auth.user?.pseudo?.slice(0,2).toUpperCase() }}
                </div>
                <div class="profile-details">
                  <p class="profile-name">{{ auth.user?.prenom }} {{ auth.user?.nom }}</p>
                  <p class="profile-pseudo">@{{ auth.user?.pseudo }}</p>
                  <span class="profile-role-badge" :class="auth.isAdmin ? 'admin' : 'user'">
                    {{ auth.isAdmin ? '⬡ ADMIN' : '◈ USER' }}
                  </span>
                </div>
              </div>
            </div>

            <!-- Locations -->
            <div class="dash-card cyber-card clickable" @click="$router.push('/reservations')">
              <div class="dash-card-header">
                <span class="dash-icon pink">◉</span>
                <span class="dash-label">LOCATIONS EN COURS</span>
              </div>
              <div class="slots-display">
                <div class="slots-row">
                  <div v-for="i in 3" :key="i" class="slot-block" :class="{ active: i <= reservationCount }">
                    <span class="slot-num">{{ i <= reservationCount ? '▮' : '▯' }}</span>
                  </div>
                </div>
                <p class="slots-text">
                  <span class="slots-num" :class="reservationCount > 0 ? 'neon-text' : ''">{{ reservationCount }}</span>
                  / 3 slots utilisés
                </p>
              </div>
              <p class="dash-cta">Voir mes locations →</p>
            </div>

            <!-- Accès rapide -->
            <div class="dash-card cyber-card">
              <div class="dash-card-header">
                <span class="dash-icon purple">◆</span>
                <span class="dash-label">ACCÈS RAPIDE</span>
              </div>
              <div class="quick-links">
                <RouterLink to="/films" class="quick-link">
                  <span class="ql-icon">▶</span> Parcourir les films
                </RouterLink>
                <RouterLink to="/reservations" class="quick-link">
                  <span class="ql-icon">▶</span> Mes locations
                </RouterLink>
                <RouterLink v-if="auth.isAdmin" to="/admin" class="quick-link admin">
                  <span class="ql-icon">▶</span> Panneau admin
                </RouterLink>
              </div>
            </div>

          </div>
        </div>
      </section>
    </Transition>

    <!-- Films -->
    <section class="section-films">
      <div class="container">
        <h2 class="section-title">
          {{ auth.isLoggedIn ? 'Films à découvrir' : 'Films récents' }}
        </h2>
        <div v-if="loadingFilms" class="loading-grid">
          <div v-for="i in 6" :key="i" class="skeleton-card"></div>
        </div>
        <div v-else class="films-grid">
          <FilmCard
            v-for="film in films"
            :key="film.id"
            :film="film"
            @click="$router.push(`/films/${film.id}`)"
          />
        </div>
        <div class="see-all">
          <RouterLink to="/films" class="btn-cyber"><span>Voir tous les films</span></RouterLink>
        </div>
      </div>
    </section>

    <!-- Features (non connecté seulement) -->
    <Transition name="fade">
      <section v-if="!auth.isLoggedIn" class="section-features">
        <div class="container">
          <h2 class="section-title">Fonctionnalités</h2>
          <div class="features-grid">
            <div class="feature-card cyber-card" v-for="f in features" :key="f.icon">
              <div class="feature-icon" :style="{ color: f.color }">{{ f.icon }}</div>
              <h3 class="feature-title">{{ f.title }}</h3>
              <p class="feature-desc">{{ f.desc }}</p>
            </div>
          </div>
        </div>
      </section>
    </Transition>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import FilmCard from '../components/FilmCard.vue'
import filmService from '../services/filmService'
import reservationService from '../services/reservationService'

const auth = useAuthStore()
const films = ref([])
const loadingFilms = ref(true)
const reservationCount = ref(0)

const POSTER_API = 'http://info-tpsi:11084/posters'

// Récupère le poster d'un film et retourne l'URL ou null
const fetchPosterUrl = async (titre) => {
  if (!titre) return null
  try {
    const res = await fetch(`${POSTER_API}/${encodeURIComponent(titre)}`)
    const list = await res.json()
    if (list.length) {
      const p = list[0]
      return p.nom || null
    }
    return null
  } catch {
    return null
  }
}

onMounted(async () => {
  try {
    const res = await filmService.getAll()
    const rawFilms = (res.data || []).slice(0, 6)

    // Récupère tous les posters en parallèle
    const posterUrls = await Promise.all(
            rawFilms.map(f => fetchPosterUrl(f.title || f.titre))
        )

    // Injecte l'affiche dans chaque objet film
    films.value = rawFilms.map((f, i) => ({
          ...f,
          affiche: posterUrls[i] || null
        }))
  } catch(e) {
    films.value = mockFilms
  } finally {
    loadingFilms.value = false
  }

  if (auth.isLoggedIn) {
    try {
      const res = await reservationService.getMesReservations()
      reservationCount.value = (res.data || []).filter(r => r.active !== false).length
    } catch {
      reservationCount.value = 0
    }
  }
})

const features = [
  { icon: '◈', title: 'Location instantanée', desc: 'Réservez jusqu\'à 3 films simultanément.', color: 'var(--neon-cyan)' },
  { icon: '◉', title: 'Évaluations vérifiées', desc: 'Partagez votre avis sur les films loués.', color: 'var(--neon-pink)' },
  { icon: '◆', title: 'Catalogue enrichi', desc: 'Films classés par réalisateur, acteur ou genre.', color: 'var(--neon-purple)' },
  { icon: '◇', title: 'Paiement sécurisé', desc: 'Système de paiement par carte bancaire intégré.', color: 'var(--neon-green)' },
]

const mockFilms = [
  { id: 1, titre: 'Blade Runner 2049', annee: 2017, genres: ['Sci-Fi', 'Thriller'], prix: 3.99, ouvert: true, affiche: null },
  { id: 2, titre: 'Dune', annee: 2021, genres: ['Sci-Fi', 'Aventure'], prix: 4.49, ouvert: true, affiche: null },
  { id: 3, titre: 'The Matrix', annee: 1999, genres: ['Sci-Fi', 'Action'], prix: 2.99, ouvert: true, affiche: null },
  { id: 4, titre: 'Ghost in the Shell', annee: 1995, genres: ['Anime', 'Sci-Fi'], prix: 2.49, ouvert: false, affiche: null },
  { id: 5, titre: 'Akira', annee: 1988, genres: ['Anime', 'Cyberpunk'], prix: 1.99, ouvert: true, affiche: null },
  { id: 6, titre: 'Ex Machina', annee: 2014, genres: ['Sci-Fi', 'Drama'], prix: 3.49, ouvert: true, affiche: null },
]
</script>

<style scoped>
.hero {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
  padding: 6rem 2rem 4rem;
}

.hero-bg { position: absolute; inset: 0; pointer-events: none; }

.hero-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  animation: float 8s ease-in-out infinite;
}

.orb-1 { width: 500px; height: 500px; background: radial-gradient(circle, rgba(0,245,255,0.12), transparent); top: -100px; left: -100px; }
.orb-2 { width: 400px; height: 400px; background: radial-gradient(circle, rgba(255,0,110,0.1), transparent); bottom: -50px; right: -50px; animation-delay: 3s; }
.orb-3 { width: 300px; height: 300px; background: radial-gradient(circle, rgba(157,0,255,0.08), transparent); top: 50%; left: 50%; transform: translate(-50%,-50%); animation-delay: 1.5s; }

/* Session banner */
.session-banner {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 6px 20px;
  border: 1px solid rgba(0,255,136,0.3);
  border-radius: 20px;
  background: rgba(0,255,136,0.05);
  margin-bottom: 2rem;
  font-family: var(--font-mono);
  font-size: 0.65rem;
  letter-spacing: 0.15em;
}

.session-dot {
  width: 6px; height: 6px;
  border-radius: 50%;
  background: var(--neon-green);
  box-shadow: 0 0 8px var(--neon-green);
  animation: pulse-neon 1.5s infinite;
}

.session-text { color: var(--neon-green); }
.session-divider { color: var(--text-muted); }
.session-user { color: var(--neon-cyan); }
.session-role { color: var(--text-muted); }

.hero-content {
  position: relative;
  z-index: 1;
  text-align: center;
  max-width: 800px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.hero-eyebrow {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 2rem;
}

.eyebrow-line { width: 60px; height: 1px; background: linear-gradient(90deg, transparent, var(--neon-cyan)); }
.eyebrow-line.right { background: linear-gradient(90deg, var(--neon-cyan), transparent); }

.eyebrow-text {
  font-family: var(--font-mono);
  font-size: 0.7rem;
  letter-spacing: 0.3em;
  text-transform: uppercase;
  color: var(--neon-cyan);
  animation: flicker 5s infinite;
}

.hero-title {
  font-family: var(--font-display);
  font-weight: 900;
  line-height: 0.9;
  margin-bottom: 2rem;
}

.title-line-1 {
  display: block;
  font-size: clamp(4rem, 12vw, 9rem);
  color: transparent;
  -webkit-text-stroke: 1px rgba(0,245,255,0.4);
  letter-spacing: 0.1em;
}

.title-line-2 {
  display: block;
  font-size: clamp(3rem, 10vw, 7rem);
  color: var(--text-primary);
  letter-spacing: 0.05em;
}

.title-accent {
  color: var(--neon-pink);
  text-shadow: 0 0 20px var(--neon-pink), 0 0 60px rgba(255,0,110,0.3);
}

.hero-description {
  font-size: 1.05rem;
  color: var(--text-secondary);
  max-width: 520px;
  margin: 0 auto 2.5rem;
  line-height: 1.7;
}

.highlight {
  color: var(--neon-cyan);
  font-family: var(--font-mono);
  font-weight: 700;
}

.hero-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
  margin-bottom: 3rem;
}

.btn-large { padding: 14px 36px !important; font-size: 0.8rem !important; }

.hero-stats {
  display: inline-flex;
  align-items: center;
  gap: 2rem;
  padding: 1.5rem 2.5rem;
  border: 1px solid var(--border-subtle);
  border-radius: 8px;
  background: rgba(0,245,255,0.02);
}

.stat { text-align: center; }
.stat-value { display: block; font-family: var(--font-display); font-size: 1.8rem; font-weight: 700; line-height: 1; margin-bottom: 4px; }
.stat-label { font-family: var(--font-mono); font-size: 0.65rem; letter-spacing: 0.1em; color: var(--text-muted); text-transform: uppercase; }
.stat-divider { width: 1px; height: 40px; background: var(--border-subtle); }

.scroll-indicator {
  position: absolute;
  bottom: 2rem;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  animation: float 2s ease-in-out infinite;
}

.scroll-text { font-family: var(--font-mono); font-size: 0.6rem; letter-spacing: 0.3em; color: var(--text-muted); }
.scroll-line { width: 1px; height: 40px; background: linear-gradient(to bottom, var(--neon-cyan), transparent); }

/* Dashboard */
.user-dashboard {
  padding: 4rem 0;
  border-top: 1px solid var(--border-subtle);
  background: linear-gradient(to bottom, rgba(0,245,255,0.02), transparent);
}

.container { max-width: 1400px; margin: 0 auto; padding: 0 2rem; }

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1.5rem;
}

.dash-card { padding: 1.75rem; display: flex; flex-direction: column; gap: 1.25rem; }
.dash-card.clickable { cursor: pointer; }

.dash-card-header { display: flex; align-items: center; gap: 10px; }

.dash-icon { font-size: 1.2rem; }
.dash-icon.cyan { color: var(--neon-cyan); text-shadow: 0 0 10px var(--neon-cyan); }
.dash-icon.pink { color: var(--neon-pink); text-shadow: 0 0 10px var(--neon-pink); }
.dash-icon.purple { color: var(--neon-purple); text-shadow: 0 0 10px var(--neon-purple); }

.dash-label { font-family: var(--font-mono); font-size: 0.65rem; letter-spacing: 0.2em; color: var(--text-muted); text-transform: uppercase; }

/* Profile */
.profile-info { display: flex; align-items: center; gap: 1.25rem; }

.profile-avatar {
  width: 56px; height: 56px;
  border-radius: 8px;
  border: 1px solid var(--border-glow);
  background: rgba(0,245,255,0.05);
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: var(--font-display);
  font-size: 1.2rem;
  font-weight: 900;
  color: var(--neon-cyan);
  text-shadow: 0 0 10px var(--neon-cyan);
  flex-shrink: 0;
}

.profile-details { display: flex; flex-direction: column; gap: 4px; }
.profile-name { font-family: var(--font-display); font-size: 0.85rem; letter-spacing: 0.05em; color: var(--text-primary); }
.profile-pseudo { font-family: var(--font-mono); font-size: 0.75rem; color: var(--neon-cyan); }

.profile-role-badge {
  font-family: var(--font-mono);
  font-size: 0.6rem;
  letter-spacing: 0.1em;
  padding: 2px 10px;
  border-radius: 2px;
  display: inline-block;
  margin-top: 2px;
}

.profile-role-badge.user { border: 1px solid rgba(0,245,255,0.3); color: var(--neon-cyan); background: rgba(0,245,255,0.05); }
.profile-role-badge.admin { border: 1px solid rgba(255,107,0,0.3); color: var(--neon-orange); background: rgba(255,107,0,0.05); }

/* Slots */
.slots-display { display: flex; flex-direction: column; gap: 0.75rem; }
.slots-row { display: flex; gap: 8px; }

.slot-block {
  flex: 1;
  height: 48px;
  border-radius: 4px;
  border: 1px solid var(--border-subtle);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.slot-block.active {
  border-color: var(--neon-cyan);
  background: rgba(0,245,255,0.08);
  box-shadow: 0 0 12px rgba(0,245,255,0.1);
}

.slot-num { font-family: var(--font-mono); font-size: 1.2rem; color: var(--text-muted); }
.slot-block.active .slot-num { color: var(--neon-cyan); text-shadow: 0 0 8px var(--neon-cyan); }

.slots-text { font-family: var(--font-mono); font-size: 0.75rem; color: var(--text-muted); }
.slots-num { font-size: 1.1rem; font-weight: 700; }

.dash-cta { font-family: var(--font-mono); font-size: 0.7rem; color: var(--neon-pink); letter-spacing: 0.05em; margin-top: auto; }

/* Quick links */
.quick-links { display: flex; flex-direction: column; gap: 0.5rem; }

.quick-link {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  border-radius: 4px;
  border: 1px solid var(--border-subtle);
  color: var(--text-secondary);
  text-decoration: none;
  font-family: var(--font-body);
  font-size: 0.9rem;
  transition: all 0.2s ease;
}

.quick-link:hover { border-color: var(--border-glow); color: var(--neon-cyan); background: rgba(0,245,255,0.03); }
.quick-link.admin { border-color: rgba(255,107,0,0.2); color: var(--neon-orange); }
.quick-link.admin:hover { border-color: rgba(255,107,0,0.4); background: rgba(255,107,0,0.03); }
.ql-icon { font-size: 0.6rem; color: var(--neon-cyan); }
.quick-link.admin .ql-icon { color: var(--neon-orange); }

/* Films / Features */
.section-films, .section-features { padding: 5rem 0; }

.films-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(190px, 1fr)); gap: 1.5rem; margin-bottom: 3rem; }
.loading-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(190px, 1fr)); gap: 1.5rem; margin-bottom: 3rem; }

.skeleton-card {
  aspect-ratio: 2/3;
  border-radius: 8px;
  background: linear-gradient(90deg, var(--bg-card) 25%, var(--bg-card-hover) 50%, var(--bg-card) 75%);
  background-size: 200% 100%;
  animation: skeleton-shimmer 1.5s infinite;
}

@keyframes skeleton-shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

.see-all { text-align: center; }
.features-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 1.5rem; }
.feature-card { padding: 2rem; text-align: center; }
.feature-icon { font-size: 2.5rem; margin-bottom: 1.25rem; display: block; filter: drop-shadow(0 0 12px currentColor); }
.feature-title { font-family: var(--font-display); font-size: 0.8rem; letter-spacing: 0.1em; text-transform: uppercase; color: var(--text-primary); margin-bottom: 0.75rem; }
.feature-desc { font-size: 0.9rem; color: var(--text-secondary); line-height: 1.6; }

/* Transitions */
.slide-down-enter-active { transition: all 0.4s ease; }
.slide-down-enter-from { opacity: 0; transform: translateY(-10px); }
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

@media (max-width: 600px) {
  .hero-stats { flex-direction: column; gap: 1rem; }
  .stat-divider { width: 40px; height: 1px; }
}
</style>