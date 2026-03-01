<template>
  <div class="reservations-page">
    <div class="container">

      <!-- Header -->
      <div class="page-header">
        <p class="page-eyebrow"><span class="eye-prefix">//</span> MON ESPACE</p>
        <h1 class="page-title">MES<span class="title-neon"> LOCATIONS</span></h1>

        <!-- Slots indicator -->
        <div class="slots-indicator">
          <div class="slots-bar">
            <div
              v-for="i in 3"
              :key="i"
              class="slot"
              :class="{ used: i <= reservations.length }"
            ></div>
          </div>
          <span class="slots-label">
            <span class="neon-text">{{ reservations.length }}</span> / 3 slots utilisés
          </span>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <p class="loading-text">RÉCUPÉRATION DES DONNÉES...</p>
      </div>

      <!-- Empty -->
      <div v-else-if="reservations.length === 0" class="empty-state">
        <div class="empty-icon-wrap">
          <span class="empty-icon">◈</span>
        </div>
        <h3 class="empty-title">Aucune location en cours</h3>
        <p class="empty-sub">Parcourez le catalogue et louez vos films préférés.</p>
        <RouterLink to="/films" class="btn-cyber"><span>Explorer le catalogue</span></RouterLink>
      </div>

      <!-- Reservations grid -->
      <div v-else class="reservations-grid">
        <div
          v-for="res in reservations"
          :key="res.id"
          class="reservation-card cyber-card"
        >
          <!-- Poster -->
          <div class="res-poster">
            <img v-if="res.film?.affiche" :src="res.film.affiche" :alt="res.film.titre" class="res-poster-img" />
            <div v-else class="res-poster-placeholder">
              <span class="poster-letters">{{ res.film?.titre?.split(' ').map(w => w[0]).join('').slice(0, 2) }}</span>
            </div>
            <div class="res-overlay">
              <span class="res-status active">● ACTIVE</span>
            </div>
          </div>

          <!-- Info -->
          <div class="res-info">
            <h3 class="res-title">{{ res.film?.titre }}</h3>
            <p class="res-director">
              <span class="meta-label">DIR.</span>
              {{ res.film?.realisateur || '—' }}
            </p>

            <div class="res-meta">
              <div class="meta-item">
                <span class="meta-key">LOUÉ LE</span>
                <span class="meta-val">{{ formatDate(res.dateDebut) }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-key">PRIX PAYÉ</span>
                <span class="meta-val neon-text">{{ res.prix?.toFixed(2) }} €</span>
              </div>
            </div>

            <!-- Genres -->
            <div class="res-genres">
              <span v-for="g in (res.film?.genres || []).slice(0, 3)" :key="g" class="tag tag-purple">{{ g }}</span>
            </div>

            <!-- Actions -->
            <div class="res-actions">
              <RouterLink :to="`/films/${res.film?.id}`" class="btn-cyber btn-sm">
                <span>Voir détails</span>
              </RouterLink>
              <button class="btn-cyber btn-cyber-pink btn-sm" @click="confirmTerminer(res)">
                <span>Terminer la location</span>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- History section -->
      <div v-if="history.length" class="history-section">
        <h2 class="section-title">Historique</h2>
        <div class="history-list">
          <div v-for="h in history" :key="h.id" class="history-item cyber-card">
            <div class="history-main">
              <span class="history-title">{{ h.film?.titre }}</span>
              <span class="history-date">{{ formatDate(h.dateFin) }}</span>
            </div>
            <div class="history-meta">
              <span class="tag tag-cyan">{{ h.film?.annee }}</span>
              <span class="history-price">{{ h.prix?.toFixed(2) }} €</span>
              <span class="history-status closed">● Terminée</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Confirm modal -->
    <Teleport to="body">
      <Transition name="modal">
        <div v-if="confirmModal" class="modal-overlay" @click.self="confirmModal = false">
          <div class="confirm-modal">
            <div class="modal-header">
              <span class="modal-title">// CONFIRMER LA FIN DE LOCATION</span>
            </div>
            <div class="modal-body">
              <p class="confirm-text">
                Voulez-vous vraiment terminer la location de
                <span class="confirm-film-name">{{ selectedRes?.film?.titre }}</span> ?
              </p>
              <p class="confirm-warn">⚠ Cette action est irréversible.</p>
              <div class="confirm-actions">
                <button class="btn-cyber" @click="confirmModal = false"><span>Annuler</span></button>
                <button class="btn-cyber btn-cyber-pink" @click="terminerLocation" :disabled="terminating">
                  <span>{{ terminating ? 'TRAITEMENT...' : 'CONFIRMER' }}</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import reservationService from '../services/reservationService'

const reservations = ref([])
const history = ref([])
const loading = ref(true)
const confirmModal = ref(false)
const selectedRes = ref(null)
const terminating = ref(false)

onMounted(async () => {
  try {
    const res = await reservationService.getMesReservations()
    const all = res.data || []
    reservations.value = all.filter(r => r.active)
    history.value = all.filter(r => !r.active)
  } catch {
    // Mock data
    reservations.value = [
      { id: 1, dateDebut: '2025-03-01', prix: 3.99, film: { id: 1, titre: 'Blade Runner 2049', realisateur: 'Denis Villeneuve', genres: ['Sci-Fi', 'Thriller'], annee: 2017 } },
      { id: 2, dateDebut: '2025-03-03', prix: 4.49, film: { id: 2, titre: 'Dune', realisateur: 'Denis Villeneuve', genres: ['Sci-Fi', 'Aventure'], annee: 2021 } },
    ]
    history.value = [
      { id: 10, dateFin: '2025-02-20', prix: 2.99, film: { id: 3, titre: 'The Matrix', annee: 1999 } },
    ]
  } finally {
    loading.value = false
  }
})

const formatDate = (dateStr) => {
  if (!dateStr) return '—'
  return new Date(dateStr).toLocaleDateString('fr-FR', { day: '2-digit', month: 'short', year: 'numeric' })
}

const confirmTerminer = (res) => {
  selectedRes.value = res
  confirmModal.value = true
}

const terminerLocation = async () => {
  terminating.value = true
  try {
    await reservationService.terminerLocation(selectedRes.value.id)
    const idx = reservations.value.findIndex(r => r.id === selectedRes.value.id)
    if (idx !== -1) {
      const done = { ...reservations.value[idx], active: false, dateFin: new Date().toISOString() }
      reservations.value.splice(idx, 1)
      history.value.unshift(done)
    }
    confirmModal.value = false
  } catch (e) {
    console.error(e)
  } finally {
    terminating.value = false
  }
}
</script>

<style scoped>
.reservations-page { padding: 3rem 0 6rem; }

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

/* Header */
.page-header {
  margin-bottom: 3rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.page-eyebrow {
  font-family: var(--font-mono);
  font-size: 0.7rem;
  letter-spacing: 0.2em;
  color: var(--text-muted);
}

.eye-prefix { color: var(--neon-cyan); margin-right: 6px; }

.page-title {
  font-family: var(--font-display);
  font-size: clamp(2rem, 5vw, 3.5rem);
  font-weight: 900;
  letter-spacing: 0.05em;
  color: var(--text-primary);
  line-height: 1;
}

.title-neon {
  color: var(--neon-cyan);
  text-shadow: 0 0 20px var(--neon-cyan);
}

/* Slots */
.slots-indicator {
  display: flex;
  align-items: center;
  gap: 16px;
}

.slots-bar { display: flex; gap: 8px; }

.slot {
  width: 40px;
  height: 6px;
  border-radius: 3px;
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  transition: all 0.3s ease;
}

.slot.used {
  background: var(--neon-cyan);
  border-color: var(--neon-cyan);
  box-shadow: 0 0 12px var(--neon-cyan);
}

.slots-label {
  font-family: var(--font-mono);
  font-size: 0.75rem;
  color: var(--text-muted);
}

/* Loading */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 40vh;
  gap: 1.5rem;
}

.loading-spinner {
  width: 48px; height: 48px;
  border: 2px solid var(--border-subtle);
  border-top-color: var(--neon-cyan);
  border-radius: 50%;
  animation: spin-slow 0.8s linear infinite;
}

.loading-text {
  font-family: var(--font-mono);
  font-size: 0.7rem;
  letter-spacing: 0.2em;
  color: var(--neon-cyan);
  animation: pulse-neon 1.5s infinite;
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

.empty-icon-wrap {
  width: 80px; height: 80px;
  border: 1px solid var(--border-glow);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 0.5rem;
}

.empty-icon {
  font-size: 2rem;
  color: var(--neon-cyan);
  opacity: 0.4;
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

/* Grid */
.reservations-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 1.5rem;
  margin-bottom: 4rem;
}

.reservation-card {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* Poster */
.res-poster {
  position: relative;
  aspect-ratio: 16/7;
  overflow: hidden;
  background: var(--bg-void);
}

.res-poster-img {
  width: 100%; height: 100%;
  object-fit: cover;
  filter: brightness(0.7);
  transition: filter 0.3s;
}

.reservation-card:hover .res-poster-img { filter: brightness(0.9); }

.res-poster-placeholder {
  width: 100%; height: 100%;
  background: linear-gradient(135deg, var(--bg-card), var(--bg-void));
  display: flex;
  align-items: center;
  justify-content: center;
}

.poster-letters {
  font-family: var(--font-display);
  font-size: 3rem;
  font-weight: 900;
  color: rgba(0, 245, 255, 0.15);
  letter-spacing: 0.2em;
}

.res-overlay {
  position: absolute;
  top: 12px; left: 12px;
}

.res-status {
  font-family: var(--font-mono);
  font-size: 0.65rem;
  letter-spacing: 0.1em;
  padding: 4px 12px;
  border-radius: 2px;
}

.res-status.active {
  background: rgba(0, 255, 136, 0.1);
  color: var(--neon-green);
  border: 1px solid rgba(0, 255, 136, 0.3);
}

/* Info */
.res-info { padding: 1.5rem; display: flex; flex-direction: column; gap: 0.75rem; }

.res-title {
  font-family: var(--font-display);
  font-size: 0.9rem;
  font-weight: 700;
  letter-spacing: 0.05em;
  color: var(--text-primary);
  text-transform: uppercase;
}

.res-director {
  font-family: var(--font-mono);
  font-size: 0.7rem;
  color: var(--text-muted);
  display: flex;
  gap: 8px;
}

.meta-label { color: var(--neon-cyan); opacity: 0.7; }

.res-meta {
  display: flex;
  gap: 1.5rem;
}

.meta-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.meta-key {
  font-family: var(--font-mono);
  font-size: 0.6rem;
  letter-spacing: 0.15em;
  color: var(--text-muted);
}

.meta-val {
  font-family: var(--font-mono);
  font-size: 0.8rem;
  color: var(--text-secondary);
}

.res-genres { display: flex; flex-wrap: wrap; gap: 4px; }

.res-actions {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
  margin-top: 0.25rem;
}

.btn-sm { padding: 8px 16px !important; font-size: 0.65rem !important; }

/* History */
.history-section { margin-top: 2rem; }

.history-list { display: flex; flex-direction: column; gap: 0.75rem; }

.history-item {
  padding: 1rem 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 0.75rem;
  opacity: 0.7;
  transition: opacity 0.2s;
}

.history-item:hover { opacity: 1; }

.history-main {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.history-title {
  font-family: var(--font-display);
  font-size: 0.8rem;
  letter-spacing: 0.05em;
  color: var(--text-secondary);
}

.history-date {
  font-family: var(--font-mono);
  font-size: 0.65rem;
  color: var(--text-muted);
}

.history-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.history-price {
  font-family: var(--font-mono);
  font-size: 0.8rem;
  color: var(--text-secondary);
}

.history-status.closed {
  font-family: var(--font-mono);
  font-size: 0.65rem;
  letter-spacing: 0.08em;
  color: var(--text-muted);
}

/* Modal */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(2, 4, 8, 0.85);
  backdrop-filter: blur(10px);
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

.confirm-modal {
  background: var(--bg-card);
  border: 1px solid rgba(255, 0, 110, 0.3);
  border-radius: 8px;
  width: 100%;
  max-width: 420px;
  box-shadow: var(--shadow-pink), var(--shadow-card);
}

.modal-header {
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid var(--border-subtle);
}

.modal-title {
  font-family: var(--font-mono);
  font-size: 0.7rem;
  letter-spacing: 0.15em;
  color: var(--neon-pink);
}

.modal-body {
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.confirm-text {
  font-size: 0.95rem;
  color: var(--text-secondary);
  line-height: 1.6;
}

.confirm-film-name {
  font-family: var(--font-display);
  font-size: 0.85rem;
  color: var(--text-primary);
  letter-spacing: 0.05em;
}

.confirm-warn {
  font-family: var(--font-mono);
  font-size: 0.72rem;
  color: var(--neon-pink);
  letter-spacing: 0.05em;
}

.confirm-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 0.5rem;
}

.modal-enter-active, .modal-leave-active { transition: all 0.3s ease; }
.modal-enter-from, .modal-leave-to { opacity: 0; transform: scale(0.95); }
</style>