<template>
  <div class="home">
    <section class="hero">
      <div class="hero-bg">
        <div class="hero-orb orb-1"></div>
        <div class="hero-orb orb-2"></div>
        <div class="hero-orb orb-3"></div>
        <div class="hero-grid"></div>
      </div>

      <div class="hero-content">
        <!-- Session banner si connecté -->
        <Transition name="slide-down">
          <div v-if="auth.isLoggedIn" class="session-banner">
            <span class="session-dot"></span>
            <span class="session-text">SESSION ACTIVE</span>
            <span class="session-divider">|</span>
            <span class="session-user">{{ auth.user?.pseudo }}</span>
          </div>
        </Transition>

        <div class="hero-eyebrow">
          <span class="eyebrow-line"></span>
          <span class="eyebrow-text">Système de vidéo à la demande</span>
          <span class="eyebrow-line"></span>
        </div>

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
          Accédez à des milliers de films en location. Une expérience cinématographique
          réinventée pour l'ère numérique.
        </p>

        <div class="hero-actions">
          <RouterLink to="/films" class="btn-cyber btn-large">
            <span>Explorer les films</span>
          </RouterLink>
          <RouterLink v-if="!auth.isLoggedIn" to="/register" class="btn-cyber btn-cyber-pink btn-large">
            <span>Créer un compte</span>
          </RouterLink>
          <RouterLink v-else to="/reservations" class="btn-cyber btn-cyber-pink btn-large">
            <span>Mes Locations</span>
          </RouterLink>
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

    <!-- Films -->
    <section class="section-films">
      <div class="container">
        <h2 class="section-title">Films récents</h2>

        <div v-if="loading" class="loading-grid">
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

    <!-- Features -->
    <section class="section-features">
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import FilmCard from '../components/FilmCard.vue'
import filmService from '../services/filmService'

const auth = useAuthStore()
const films = ref([])
const loading = ref(true)

// ✅ URL correcte avec .univ-brest.fr
const POSTER_API = 'http://info-tpsi:11084/posters'

const fetchPosterUrl = async (titre) => {
  if (!titre) return null
  try {
    const res = await fetch(`${POSTER_API}/${encodeURIComponent(titre)}`)
    const list = await res.json()
    return list.length ? (list[0].nom || null) : null
  } catch {
    return null
  }
}

const attachPosters = async (rawFilms) => {
  const posterUrls = await Promise.all(
    rawFilms.map(f => fetchPosterUrl(f.title || f.titre))
  )
  return rawFilms.map((f, i) => ({ ...f, affiche: posterUrls[i] || null }))
}

onMounted(async () => {
  try {
    const res = await filmService.getAll()
    const raw = (res.data || []).slice(0, 6)
    films.value = await attachPosters(raw)
  } catch {
    films.value = await attachPosters(mockFilms)
  } finally {
    loading.value = false
  }
})

const features = [
  { icon: '◈', title: 'Location instantanée', desc: 'Réservez jusqu\'à 3 films simultanément.', color: 'var(--neon-cyan)' },
  { icon: '◉', title: 'Évaluations vérifiées', desc: 'Partagez votre avis sur les films loués.', color: 'var(--neon-pink)' },
  { icon: '◆', title: 'Catalogue enrichi', desc: 'Films classés par réalisateur, acteur ou genre.', color: 'var(--neon-purple)' },
  { icon: '◇', title: 'Paiement sécurisé', desc: 'Système de paiement simulé intégré.', color: 'var(--neon-green)' },
]

const mockFilms = [
  { id: 1, title: 'Blade Runner 2049', yearCompletion: '2017', genres: ['Sci-Fi'], openForRent: true },
  { id: 2, title: 'Dune', yearCompletion: '2021', genres: ['Sci-Fi'], openForRent: true },
  { id: 3, title: 'The Matrix', yearCompletion: '1999', genres: ['Action'], openForRent: true },
  { id: 4, title: 'Ghost in the Shell', yearCompletion: '1995', genres: ['Anime'], openForRent: false },
  { id: 5, title: 'Akira', yearCompletion: '1988', genres: ['Anime'], openForRent: true },
  { id: 6, title: 'Ex Machina', yearCompletion: '2014', genres: ['Drama'], openForRent: true },
]
</script>

<style scoped>
.hero {
  min-height: 100vh; display: flex; flex-direction: column;
  justify-content: center; align-items: center;
  position: relative; overflow: hidden; padding: 4rem 2rem;
}

.hero-bg { position: absolute; inset: 0; pointer-events: none; }

.hero-orb { position: absolute; border-radius: 50%; filter: blur(80px); animation: float 8s ease-in-out infinite; }
.orb-1 { width: 500px; height: 500px; background: radial-gradient(circle, rgba(0,245,255,0.12), transparent); top: -100px; left: -100px; }
.orb-2 { width: 400px; height: 400px; background: radial-gradient(circle, rgba(255,0,110,0.1), transparent); bottom: -50px; right: -50px; animation-delay: 3s; }
.orb-3 { width: 300px; height: 300px; background: radial-gradient(circle, rgba(157,0,255,0.08), transparent); top: 50%; left: 50%; transform: translate(-50%,-50%); animation-delay: 1.5s; }

.hero-grid {
  position: absolute; inset: 0;
  background: linear-gradient(rgba(0,245,255,0.03) 1px, transparent 1px), linear-gradient(90deg, rgba(0,245,255,0.03) 1px, transparent 1px);
  background-size: 40px 40px;
  mask-image: radial-gradient(ellipse at center, black 30%, transparent 80%);
}

/* Session banner */
.session-banner {
  display: inline-flex; align-items: center; gap: 10px;
  padding: 6px 20px; border: 1px solid rgba(0,255,136,0.3);
  border-radius: 20px; background: rgba(0,255,136,0.05);
  margin-bottom: 2rem; font-family: var(--font-mono);
  font-size: 0.65rem; letter-spacing: 0.15em;
}
.session-dot { width: 6px; height: 6px; border-radius: 50%; background: var(--neon-green); box-shadow: 0 0 8px var(--neon-green); animation: pulse-neon 1.5s infinite; }
.session-text { color: var(--neon-green); }
.session-divider { color: var(--text-muted); }
.session-user { color: var(--neon-cyan); }

.hero-content { position: relative; z-index: 1; text-align: center; max-width: 800px; display: flex; flex-direction: column; align-items: center; }

.hero-eyebrow { display: flex; align-items: center; justify-content: center; gap: 16px; margin-bottom: 2rem; }
.eyebrow-line { flex: 1; max-width: 60px; height: 1px; background: linear-gradient(90deg, transparent, var(--neon-cyan)); }
.eyebrow-line:last-child { background: linear-gradient(90deg, var(--neon-cyan), transparent); }
.eyebrow-text { font-family: var(--font-mono); font-size: 0.7rem; letter-spacing: 0.3em; text-transform: uppercase; color: var(--neon-cyan); animation: flicker 5s infinite; }

.hero-title { font-family: var(--font-display); font-weight: 900; line-height: 0.9; margin-bottom: 2rem; }
.title-line-1 { display: block; font-size: clamp(4rem, 12vw, 9rem); color: transparent; -webkit-text-stroke: 1px rgba(0,245,255,0.4); letter-spacing: 0.1em; }
.title-line-2 { display: block; font-size: clamp(3rem, 10vw, 7rem); color: var(--text-primary); letter-spacing: 0.05em; }
.title-accent { color: var(--neon-pink); text-shadow: 0 0 20px var(--neon-pink), 0 0 60px rgba(255,0,110,0.3); }

.hero-description { font-size: 1.05rem; color: var(--text-secondary); max-width: 520px; margin: 0 auto 2.5rem; line-height: 1.7; }

.hero-actions { display: flex; gap: 1rem; justify-content: center; flex-wrap: wrap; margin-bottom: 3rem; }
.btn-large { padding: 14px 36px !important; font-size: 0.8rem !important; }

.hero-stats { display: inline-flex; align-items: center; gap: 2rem; padding: 1.5rem 2.5rem; border: 1px solid var(--border-subtle); border-radius: 8px; background: rgba(0,245,255,0.02); }
.stat { text-align: center; }
.stat-value { display: block; font-family: var(--font-display); font-size: 1.8rem; font-weight: 700; line-height: 1; margin-bottom: 4px; }
.stat-label { font-family: var(--font-mono); font-size: 0.65rem; letter-spacing: 0.1em; color: var(--text-muted); text-transform: uppercase; }
.stat-divider { width: 1px; height: 40px; background: var(--border-subtle); }

.scroll-indicator { position: absolute; bottom: 2rem; left: 50%; transform: translateX(-50%); display: flex; flex-direction: column; align-items: center; gap: 8px; animation: float 2s ease-in-out infinite; }
.scroll-text { font-family: var(--font-mono); font-size: 0.6rem; letter-spacing: 0.3em; color: var(--text-muted); }
.scroll-line { width: 1px; height: 40px; background: linear-gradient(to bottom, var(--neon-cyan), transparent); }

.section-films, .section-features { padding: 6rem 0; }
.container { max-width: 1400px; margin: 0 auto; padding: 0 2rem; }

.films-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 1.5rem; margin-bottom: 3rem; }
.loading-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 1.5rem; margin-bottom: 3rem; }
.skeleton-card { height: 300px; border-radius: 8px; background: linear-gradient(90deg, var(--bg-card) 25%, var(--bg-card-hover) 50%, var(--bg-card) 75%); background-size: 200% 100%; animation: skeleton-shimmer 1.5s infinite; }
@keyframes skeleton-shimmer { 0% { background-position: 200% 0; } 100% { background-position: -200% 0; } }
.see-all { text-align: center; }

.features-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 1.5rem; }
.feature-card { padding: 2rem; text-align: center; }
.feature-icon { font-size: 2.5rem; margin-bottom: 1.25rem; display: block; filter: drop-shadow(0 0 12px currentColor); }
.feature-title { font-family: var(--font-display); font-size: 0.8rem; letter-spacing: 0.1em; text-transform: uppercase; color: var(--text-primary); margin-bottom: 0.75rem; }
.feature-desc { font-size: 0.9rem; color: var(--text-secondary); line-height: 1.6; }

.slide-down-enter-active { transition: all 0.4s ease; }
.slide-down-enter-from { opacity: 0; transform: translateY(-10px); }

@media (max-width: 600px) {
  .hero-stats { flex-direction: column; gap: 1rem; }
  .stat-divider { width: 40px; height: 1px; }
}
</style>
