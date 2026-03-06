<template>
  <div class="films-page">
    <div class="container">

      <!-- Header -->
      <div class="page-header">
        <div>
          <p class="page-eyebrow"><span class="eye-prefix">//</span> CATALOGUE</p>
          <h1 class="page-title">TOUS LES<span class="title-neon"> FILMS</span></h1>
          <p class="page-count" v-if="!loading">
            <span class="count-val">{{ filtered.length }}</span>
            <span class="count-label"> films trouvés</span>
          </p>
        </div>
      </div>

      <!-- Filtres -->
      <div class="filters-bar">
        <div class="search-bar">
          <span class="search-icon">⌕</span>
          <input v-model="search" type="text" class="search-input" placeholder="Rechercher un film..." />
          <button v-if="search" class="clear-btn" @click="search = ''">✕</button>
        </div>

        <label class="toggle-dispo">
          <input type="checkbox" v-model="onlyAvailable" />
          <span class="toggle-label">Disponibles seulement</span>
        </label>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="loading-grid">
        <div v-for="i in 8" :key="i" class="skeleton-card"></div>
      </div>

      <!-- Empty -->
      <div v-else-if="filtered.length === 0" class="empty-state">
        <span class="empty-icon">◈</span>
        <p>Aucun film trouvé</p>
      </div>

      <!-- Grid -->
      <div v-else class="films-grid">
        <FilmCard
          v-for="film in filtered"
          :key="film.id"
          :film="film"
          @click="$router.push(`/films/${film.id}`)"
        />
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import FilmCard from '../components/FilmCard.vue'
import filmService from '../services/filmService'

const films = ref([])
const loading = ref(true)
const search = ref('')
const onlyAvailable = ref(false)

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
    films.value = await attachPosters(res.data || [])
  } catch {
    films.value = await attachPosters(mockFilms)
  } finally {
    loading.value = false
  }
})

const filtered = computed(() => {
  return films.value.filter(f => {
    const titre = (f.title || f.titre || '').toLowerCase()
    const matchSearch = titre.includes(search.value.toLowerCase())
    const matchDispo = !onlyAvailable.value || f.openForRent || f.ouvert
    return matchSearch && matchDispo
  })
})

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
.films-page { padding: 3rem 0 6rem; }
.container { max-width: 1400px; margin: 0 auto; padding: 0 2rem; }

.page-header { margin-bottom: 2rem; }
.page-eyebrow { font-family: var(--font-mono); font-size: 0.7rem; letter-spacing: 0.2em; color: var(--text-muted); margin-bottom: 0.5rem; }
.eye-prefix { color: var(--neon-cyan); margin-right: 6px; }
.page-title { font-family: var(--font-display); font-size: clamp(1.8rem, 4vw, 3rem); font-weight: 900; letter-spacing: 0.05em; color: var(--text-primary); line-height: 1; margin-bottom: 0.5rem; }
.title-neon { color: var(--neon-cyan); text-shadow: 0 0 20px var(--neon-cyan); }
.page-count { font-family: var(--font-mono); font-size: 0.8rem; }
.count-val { color: var(--neon-cyan); font-size: 1.1rem; font-weight: 700; }
.count-label { color: var(--text-muted); }

.filters-bar { display: flex; align-items: center; gap: 1.5rem; margin-bottom: 2rem; flex-wrap: wrap; }

.search-bar { position: relative; display: flex; align-items: center; flex: 1; min-width: 250px; max-width: 400px; }
.search-icon { position: absolute; left: 14px; color: var(--text-muted); font-size: 1.1rem; pointer-events: none; }
.search-input { width: 100%; padding: 10px 36px; background: var(--bg-card); border: 1px solid var(--border-subtle); border-radius: 4px; color: var(--text-primary); font-family: var(--font-mono); font-size: 0.85rem; outline: none; transition: border 0.3s; }
.search-input::placeholder { color: var(--text-muted); }
.search-input:focus { border-color: var(--neon-cyan); }
.clear-btn { position: absolute; right: 12px; background: none; border: none; color: var(--text-muted); cursor: pointer; font-size: 0.7rem; }
.clear-btn:hover { color: var(--neon-pink); }

.toggle-dispo { display: flex; align-items: center; gap: 8px; cursor: pointer; }
.toggle-dispo input { accent-color: var(--neon-cyan); width: 16px; height: 16px; cursor: pointer; }
.toggle-label { font-family: var(--font-mono); font-size: 0.75rem; color: var(--text-secondary); letter-spacing: 0.05em; }

.loading-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(190px, 1fr)); gap: 1.5rem; }
.skeleton-card { aspect-ratio: 2/3; border-radius: 8px; background: linear-gradient(90deg, var(--bg-card) 25%, var(--bg-card-hover) 50%, var(--bg-card) 75%); background-size: 200% 100%; animation: skeleton-shimmer 1.5s infinite; }
@keyframes skeleton-shimmer { 0% { background-position: 200% 0; } 100% { background-position: -200% 0; } }

.empty-state { text-align: center; padding: 6rem 2rem; display: flex; flex-direction: column; align-items: center; gap: 1rem; font-family: var(--font-mono); font-size: 0.8rem; color: var(--text-muted); }
.empty-icon { font-size: 2rem; animation: pulse-neon 2s infinite; }

.films-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(190px, 1fr)); gap: 1.5rem; }
</style>
