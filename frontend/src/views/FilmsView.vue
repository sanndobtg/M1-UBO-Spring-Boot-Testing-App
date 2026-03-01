<template>
  <div class="films-page">
    <div class="container">

      <!-- Header -->
      <div class="page-header">
        <div class="header-left">
          <p class="page-eyebrow"><span class="eye-prefix">//</span> CATALOGUE</p>
          <h1 class="page-title">TOUS LES<span class="title-neon"> FILMS</span></h1>
          <p class="page-count" v-if="!loading">
            <span class="count-val">{{ filtered.length }}</span>
            <span class="count-label"> films trouvés</span>
          </p>
        </div>
        <div class="header-decoration">
          <div class="deco-lines">
            <span v-for="i in 5" :key="i" class="deco-line" :style="{ opacity: 1 - i * 0.15 }"></span>
          </div>
        </div>
      </div>

      <!-- Search & Filters -->
      <div class="filters-bar">
        <div class="search-wrapper">
          <span class="search-icon">⌕</span>
          <input
            v-model="search"
            type="text"
            class="search-input"
            placeholder="Rechercher un film, réalisateur..."
          />
          <button v-if="search" class="clear-btn" @click="search = ''">✕</button>
        </div>

        <div class="filter-chips">
          <button
            v-for="genre in genres"
            :key="genre"
            class="chip"
            :class="{ active: activeGenre === genre }"
            @click="activeGenre = activeGenre === genre ? null : genre"
          >
            {{ genre }}
          </button>
        </div>

        <div class="filter-toggles">
          <button class="toggle-btn" :class="{ active: onlyOpen }" @click="onlyOpen = !onlyOpen">
            <span class="dot green-dot">●</span> Disponibles seulement
          </button>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="loading-grid">
        <div v-for="i in 12" :key="i" class="skeleton-card"></div>
      </div>

      <!-- Empty -->
      <div v-else-if="filtered.length === 0" class="empty-state">
        <span class="empty-icon">◈</span>
        <p class="empty-title">Aucun film trouvé</p>
        <p class="empty-sub">Modifiez vos filtres ou votre recherche</p>
        <button class="btn-cyber" @click="resetFilters"><span>Réinitialiser</span></button>
      </div>

      <!-- Grid -->
      <div v-else class="films-grid">
        <FilmCard
          v-for="film in filtered"
          :key="film.id"
          :film="film"
          @click="goToFilm(film.id)"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import FilmCard from '../components/FilmCard.vue'
import filmService from '../services/filmService'

const router = useRouter()
const films = ref([])
const loading = ref(true)
const search = ref('')
const activeGenre = ref(null)
const onlyOpen = ref(false)

onMounted(async () => {
  try {
    const res = await filmService.getAll()
    films.value = res.data || []
  } catch {
    films.value = mockFilms
  } finally {
    loading.value = false
  }
})

const genres = computed(() => {
  const all = films.value.flatMap(f => f.genres || f.genre || [])
  return [...new Set(all)].sort()
})

const filtered = computed(() => {
  return films.value.filter(f => {
    const q = search.value.toLowerCase()
    const matchSearch = !q ||
      f.titre?.toLowerCase().includes(q) ||
      f.realisateur?.toLowerCase().includes(q)
    const matchGenre = !activeGenre.value ||
      (f.genres || f.genre || []).includes(activeGenre.value)
    const matchOpen = !onlyOpen.value || f.ouvert
    return matchSearch && matchGenre && matchOpen
  })
})

const goToFilm = (id) => router.push(`/films/${id}`)
const resetFilters = () => {
  search.value = ''
  activeGenre.value = null
  onlyOpen.value = false
}

const mockFilms = [
  { id: 1, titre: 'Blade Runner 2049', annee: 2017, realisateur: 'Denis Villeneuve', genres: ['Sci-Fi', 'Thriller'], prix: 3.99, ouvert: true, note: 4.2 },
  { id: 2, titre: 'Dune', annee: 2021, realisateur: 'Denis Villeneuve', genres: ['Sci-Fi', 'Aventure'], prix: 4.49, ouvert: true, note: 4.5 },
  { id: 3, titre: 'The Matrix', annee: 1999, realisateur: 'Wachowski', genres: ['Sci-Fi', 'Action'], prix: 2.99, ouvert: true, note: 4.8 },
  { id: 4, titre: 'Ghost in the Shell', annee: 1995, realisateur: 'Mamoru Oshii', genres: ['Anime', 'Sci-Fi'], prix: 2.49, ouvert: false, note: 4.6 },
  { id: 5, titre: 'Akira', annee: 1988, realisateur: 'Katsuhiro Otomo', genres: ['Anime', 'Cyberpunk'], prix: 1.99, ouvert: true, note: 4.7 },
  { id: 6, titre: 'Ex Machina', annee: 2014, realisateur: 'Alex Garland', genres: ['Sci-Fi', 'Drama'], prix: 3.49, ouvert: true, note: 4.1 },
  { id: 7, titre: 'Interstellar', annee: 2014, realisateur: 'Christopher Nolan', genres: ['Sci-Fi', 'Drama'], prix: 3.99, ouvert: true, note: 4.9 },
  { id: 8, titre: 'Arrival', annee: 2016, realisateur: 'Denis Villeneuve', genres: ['Sci-Fi', 'Drama'], prix: 3.49, ouvert: false, note: 4.4 },
]
</script>

<style scoped>
.films-page {
  padding: 3rem 0 6rem;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 2rem;
}

/* Header */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 3rem;
}

.page-eyebrow {
  font-family: var(--font-mono);
  font-size: 0.7rem;
  letter-spacing: 0.2em;
  color: var(--text-muted);
  margin-bottom: 0.5rem;
}

.eye-prefix {
  color: var(--neon-cyan);
  margin-right: 6px;
}

.page-title {
  font-family: var(--font-display);
  font-size: clamp(2rem, 5vw, 3.5rem);
  font-weight: 900;
  letter-spacing: 0.05em;
  color: var(--text-primary);
  line-height: 1;
  margin-bottom: 0.75rem;
}

.title-neon {
  color: var(--neon-cyan);
  text-shadow: 0 0 20px var(--neon-cyan);
}

.page-count {
  font-family: var(--font-mono);
  font-size: 0.8rem;
}

.count-val {
  color: var(--neon-cyan);
  font-size: 1.1rem;
  font-weight: 700;
}

.count-label { color: var(--text-muted); }

.header-decoration {
  display: flex;
  align-items: center;
}

.deco-lines {
  display: flex;
  flex-direction: column;
  gap: 6px;
  align-items: flex-end;
}

.deco-line {
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--neon-cyan));
  border-radius: 1px;
}

.deco-line:nth-child(1) { width: 80px; }
.deco-line:nth-child(2) { width: 60px; }
.deco-line:nth-child(3) { width: 40px; }
.deco-line:nth-child(4) { width: 25px; }
.deco-line:nth-child(5) { width: 12px; }

/* Filters */
.filters-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  align-items: center;
  margin-bottom: 2.5rem;
  padding: 1.25rem 1.5rem;
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: 8px;
}

.search-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  min-width: 240px;
}

.search-icon {
  position: absolute;
  left: 12px;
  color: var(--text-muted);
  font-size: 1.1rem;
  pointer-events: none;
}

.search-input {
  padding: 9px 36px 9px 36px;
  background: rgba(0, 245, 255, 0.03);
  border: 1px solid var(--border-subtle);
  border-radius: 4px;
  color: var(--text-primary);
  font-family: var(--font-mono);
  font-size: 0.85rem;
  outline: none;
  transition: all 0.3s;
  width: 100%;
}

.search-input::placeholder { color: var(--text-muted); }

.search-input:focus {
  border-color: var(--neon-cyan);
  box-shadow: 0 0 0 1px rgba(0, 245, 255, 0.2);
}

.clear-btn {
  position: absolute;
  right: 12px;
  background: none;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
  font-size: 0.7rem;
  transition: color 0.2s;
}

.clear-btn:hover { color: var(--neon-pink); }

/* Chips */
.filter-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  flex: 1;
}

.chip {
  font-family: var(--font-mono);
  font-size: 0.65rem;
  letter-spacing: 0.08em;
  padding: 5px 14px;
  border-radius: 20px;
  border: 1px solid var(--border-subtle);
  background: transparent;
  color: var(--text-muted);
  cursor: pointer;
  transition: all 0.2s;
  text-transform: uppercase;
}

.chip:hover {
  border-color: var(--border-glow);
  color: var(--text-secondary);
}

.chip.active {
  border-color: var(--neon-cyan);
  color: var(--neon-cyan);
  background: rgba(0, 245, 255, 0.05);
  box-shadow: 0 0 10px rgba(0, 245, 255, 0.1);
}

/* Toggles */
.toggle-btn {
  font-family: var(--font-mono);
  font-size: 0.7rem;
  letter-spacing: 0.08em;
  padding: 7px 16px;
  border-radius: 4px;
  border: 1px solid var(--border-subtle);
  background: transparent;
  color: var(--text-muted);
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.toggle-btn:hover, .toggle-btn.active {
  border-color: rgba(0, 255, 136, 0.4);
  color: var(--neon-green);
  background: rgba(0, 255, 136, 0.05);
}

.green-dot { color: var(--neon-green); font-size: 0.6rem; }

/* Grid */
.films-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 1.25rem;
}

/* Skeleton */
.loading-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 1.25rem;
}

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

/* Empty */
.empty-state {
  text-align: center;
  padding: 6rem 2rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.empty-icon {
  font-size: 3rem;
  color: var(--text-muted);
  animation: pulse-neon 2s infinite;
}

.empty-title {
  font-family: var(--font-display);
  font-size: 1rem;
  letter-spacing: 0.1em;
  color: var(--text-secondary);
}

.empty-sub {
  font-size: 0.85rem;
  color: var(--text-muted);
  margin-bottom: 0.5rem;
}
</style>