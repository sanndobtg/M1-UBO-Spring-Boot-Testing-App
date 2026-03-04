<template>
  <div class="films-page">
    <div class="container">

      <!-- Header -->
      <div class="page-header">
        <div>
          <h1>TOUS LES FILMS</h1>
          <p v-if="!loading">{{ filtered.length }} films trouvés</p>
        </div>

        <!--  Bouton Ajouter -->
        <button class="add-btn" @click="showForm = !showForm">
          Ajouter un film
        </button>
      </div>

      <!--  Formulaire Ajout -->
      <div v-if="showForm" class="form-box">
        <input v-model="newFilm.title" placeholder="Titre" />
        <input v-model="newFilm.realisateur" placeholder="Réalisateur" />
        <input v-model.number="newFilm.annee_realisation" placeholder="Année" type="number" />
        <label>
          Disponible :
          <input type="checkbox" v-model="newFilm.est_ouvertala_location" />
        </label>

        <button @click="addFilm">Valider</button>
      </div>

      <!-- Search -->
      <input
        v-model="search"
        placeholder="Rechercher..."
      />

      <!-- Loading -->
      <div v-if="loading">Chargement...</div>

      <!-- Grid -->
      <div v-else class="films-grid">
        <FilmCard
          v-for="film in filtered"
          :key="film.id"
          :film="film"
          @click="goToFilm(film.id)"
          @delete="deleteFilm"
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
const showForm = ref(false)

const newFilm = ref({
  title: '',
  realisateur: '',
  annee_realisation: null,
  est_ouvertala_location: false
})

onMounted(loadFilms)

async function loadFilms() {
  try {
    const res = await filmService.getAll()
    films.value = res.data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}




async function addFilm() {
  try {
    const res = await filmService.create(newFilm.value)
    films.value.push(res.data)

    // reset form
    newFilm.value = {
      title: '',
      realisateur: '',
      annee_realisation: null,
      est_ouvertala_location: false
    }

    showForm.value = false

  } catch (e) {
    console.error(e)
  }
}

async function deleteFilm(id) {
  try {
    await filmService.delete(id)
    films.value = films.value.filter(f => f.id !== id)
  } catch (e) {
    console.error(e)
  }
}

const filtered = computed(() => {
  return films.value.filter(f =>
    f.title.toLowerCase().includes(search.value.toLowerCase())
  )
})

const goToFilm = (id) => {
  router.push(`/films/${id}`)
}
</script>

<style scoped>
.container {
  max-width: 1000px;
  margin: auto;
  padding: 2rem;
}

.add-btn {
  padding: 8px 15px;
  background: #00f5ff;
  border: none;
  cursor: pointer;
}

.form-box {
  margin: 20px 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.films-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 1rem;
}
</style>