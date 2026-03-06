<template>
  <div class="film-detail-page">

    <div v-if="loading" class="loading-state">
      <div class="loading-spinner"></div>
      <p class="loading-text">CHARGEMENT DES DONNÉES...</p>
    </div>

    <template v-else-if="film">

      <!-- Hero -->
      <div class="film-hero">
        <div class="hero-backdrop" :style="poster ? `background-image: url(${poster.nom})` : ''"></div>
        <div class="hero-overlay"></div>

        <div class="container hero-content">

          <!-- Poster + actions CRUD -->
          <div class="poster-wrap">
            <div class="poster-frame">
              <img v-if="poster" :src="poster.nom" :alt="film.title" class="poster" @error="poster = null" />
              <div v-else class="poster-placeholder">
                <span class="placeholder-icon">◈</span>
                <span class="placeholder-text">{{ film.title?.split(' ').map(w => w[0]).join('').slice(0,3) }}</span>
              </div>
            </div>

            <!-- Actions poster -->
            <div class="poster-actions">
              <template v-if="poster">
                <button class="btn-poster-action" @click="openEditPoster" title="Modifier">✎ Modifier</button>
                <button class="btn-poster-action btn-poster-delete" @click="deletePoster" title="Supprimer">✕ Supprimer</button>
              </template>
              <button v-else class="btn-poster-action btn-poster-add" @click="openAddPoster">＋ Ajouter un poster</button>
            </div>

            <!-- Formulaire inline poster -->
            <div v-if="showPosterForm" class="poster-form-inline">
              <input
                v-model="posterUrl"
                class="cyber-input-modal"
                placeholder="https://image.tmdb.org/..."
                @keyup.enter="savePoster"
                @keyup.escape="showPosterForm = false"
              />
              <div class="poster-form-btns">
                <button class="btn-cyber" @click="savePoster" :disabled="!posterUrl.trim() || savingPoster">
                  <span>{{ savingPoster ? '...' : 'OK' }}</span>
                </button>
                <button class="btn-poster-action" @click="showPosterForm = false">Annuler</button>
              </div>
              <div v-if="posterError" class="pay-error">⚠ {{ posterError }}</div>
            </div>
          </div>

          <!-- Infos film -->
          <div class="film-info">
            <div class="info-tags">
              <span class="tag tag-cyan">{{ film.yearCompletion || film.annee }}</span>
              <span v-for="g in (film.genres || film.genre || [])" :key="g" class="tag tag-purple">{{ g }}</span>
              <span v-if="film.minimumAge || film.ageMin" class="tag tag-pink">{{ film.minimumAge || film.ageMin }}+</span>
            </div>

            <h1 class="film-title">{{ film.title || film.titre }}</h1>

            <p v-if="film.director || film.realisateur" class="film-director">
              <span class="meta-label">RÉALISÉ PAR</span>
              <span class="meta-value">{{ film.director || film.realisateur }}</span>
            </p>

            <div v-if="(film.actors || film.acteurs)?.length" class="film-cast">
              <span class="meta-label">AVEC</span>
              <span class="cast-list">{{ (film.actors || film.acteurs).slice(0, 5).join(', ') }}</span>
            </div>

            <!-- Rating depuis api-review -->
            <div v-if="avgRating !== null" class="film-rating">
              <div class="rating-stars">
                <span v-for="i in 5" :key="i" class="star" :class="{ filled: i <= Math.round(avgRating) }">★</span>
              </div>
              <span class="rating-num neon-text">{{ avgRating.toFixed(1) }}</span>
              <span class="rating-count">/ 5 — {{ reviews.length }} avis</span>
            </div>

            <!-- Actions réservation -->
            <div class="film-actions">
              <template v-if="film.openForRent || film.ouvert">
                <div class="price-display">
                  <span class="price-amount">3.99 €</span>
                  <span class="price-label">par location</span>
                </div>
                <template v-if="auth.isLoggedIn">
                  <button
                    v-if="!alreadyRented"
                    class="btn-cyber btn-reserve"
                    @click="openReservation"
                  >
                    <span>LOUER CE FILM</span>
                  </button>
                  <button
                    v-else
                    class="btn-cyber btn-rented"
                    @click="terminer"
                    :disabled="terminating"
                  >
                    <span>{{ terminating ? '...' : '✓ EN LOCATION — TERMINER' }}</span>
                  </button>
                </template>
                <RouterLink v-else to="/login" class="btn-cyber btn-reserve">
                  <span>CONNEXION REQUISE</span>
                </RouterLink>
              </template>
              <div v-else class="closed-badge">
                <span>⊘</span> Non disponible à la location
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Reviews -->
      <div class="reviews-section">
        <div class="container">
          <h2 class="section-title">Évaluations</h2>

          <!-- Formulaire avis -->
          <div v-if="auth.isLoggedIn" class="review-form-card cyber-card">
            <h3 class="form-title">Votre évaluation</h3>
            <div class="star-picker">
              <span
                v-for="i in 5" :key="i"
                class="star-pick"
                :class="{ active: i <= newReview.note }"
                @click="newReview.note = i"
              >★</span>
              <span class="star-label">{{ newReview.note > 0 ? newReview.note + ' / 5' : 'Sélectionnez une note' }}</span>
            </div>
            <textarea
              v-model="newReview.commentaire"
              class="review-textarea"
              placeholder="Votre commentaire (facultatif)..."
              rows="3"
            ></textarea>
            <div class="form-actions">
              <button class="btn-cyber" @click="submitReview" :disabled="newReview.note === 0 || submittingReview">
                <span>{{ submittingReview ? 'ENVOI...' : 'PUBLIER' }}</span>
              </button>
            </div>
          </div>

          <!-- Liste avis -->
          <div v-if="loadingReviews" class="reviews-loading">
            <div class="loading-spinner small"></div>
          </div>

          <div v-else-if="reviews.length" class="reviews-list">
            <div v-for="(r, i) in reviews" :key="i" class="review-item cyber-card">
              <div class="review-header">
                <span class="reviewer-name">◈ {{ r.pseudo }}</span>
                <div class="review-stars">
                  <span v-for="j in 5" :key="j" class="star-sm" :class="{ filled: j <= r.note }">★</span>
                </div>
                <span class="review-note">{{ r.note }}/5</span>
              </div>
              <p v-if="r.commentaire" class="review-comment">{{ r.commentaire }}</p>
            </div>
          </div>

          <div v-else class="no-reviews">
            <span class="no-reviews-icon">◎</span>
            <p>Aucune évaluation pour ce film.</p>
          </div>
        </div>
      </div>

    </template>

    <!-- Modal paiement -->
    <Teleport to="body">
      <Transition name="modal">
        <div v-if="showPayModal" class="modal-overlay" @click.self="showPayModal = false">
          <div class="pay-modal">
            <div class="modal-header">
              <span class="modal-title">// PAIEMENT SÉCURISÉ</span>
              <button class="modal-close" @click="showPayModal = false">✕</button>
            </div>
            <div class="modal-body">
              <div class="pay-summary">
                <span class="pay-film">{{ film?.title || film?.titre }}</span>
                <span class="pay-price neon-text">3.99 €</span>
              </div>
              <div class="field-group">
                <label class="field-label">NUMÉRO DE CARTE</label>
                <input v-model="card.numero" class="cyber-input-modal" placeholder="1234 5678 9012 3456" />
              </div>
              <div class="card-row">
                <div class="field-group">
                  <label class="field-label">EXPIRATION</label>
                  <input v-model="card.expiration" class="cyber-input-modal" placeholder="MM/AA" />
                </div>
                <div class="field-group">
                  <label class="field-label">CVV</label>
                  <input v-model="card.cvv" class="cyber-input-modal" placeholder="•••" type="password" />
                </div>
              </div>
              <Transition name="fade">
                <div v-if="payError" class="pay-error">⚠ {{ payError }}</div>
              </Transition>
              <button class="btn-cyber btn-pay" @click="confirmPayment" :disabled="paying">
                <span>{{ paying ? 'TRAITEMENT...' : 'CONFIRMER LE PAIEMENT' }}</span>
              </button>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import filmService from '../services/filmService'
import reviewService from '../services/reviewService'
import reservationService from '../services/reservationService'

const route = useRoute()
const auth = useAuthStore()

const film = ref(null)
const reviews = ref([])
const loading = ref(true)
const loadingReviews = ref(true)
const showPayModal = ref(false)
const paying = ref(false)
const payError = ref('')
const alreadyRented = ref(false)
const terminating = ref(false)
const submittingReview = ref(false)
const newReview = ref({ note: 0, commentaire: '' })
const card = ref({ numero: '', expiration: '', cvv: '' })

// Poster state
const poster = ref(null)
const showPosterForm = ref(false)
const posterUrl = ref('')
const savingPoster = ref(false)
const posterError = ref('')
const isEditingPoster = ref(false)

// ✅ URL correcte avec .univ-brest.fr
const POSTER_API = 'http://info-tpsi:11084/posters'

const avgRating = computed(() => {
  if (!reviews.value.length) return null
  return reviews.value.reduce((s, r) => s + r.note, 0) / reviews.value.length
})

// ── Poster functions ──
const fetchPoster = async (titre) => {
  try {
    const res = await fetch(`${POSTER_API}/${encodeURIComponent(titre)}`)
    const list = await res.json()
    poster.value = list.length ? list[0] : null
  } catch {
    poster.value = null
  }
}

const openAddPoster = () => {
  isEditingPoster.value = false
  posterUrl.value = ''
  posterError.value = ''
  showPosterForm.value = true
}

const openEditPoster = () => {
  isEditingPoster.value = true
  posterUrl.value = poster.value.nom
  posterError.value = ''
  showPosterForm.value = true
}

const savePoster = async () => {
  if (!posterUrl.value.trim()) return
  savingPoster.value = true
  posterError.value = ''
  const titre = film.value.title || film.value.titre
  try {
    if (isEditingPoster.value) {
      const res = await fetch(`${POSTER_API}/${poster.value._id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nom: posterUrl.value.trim() })
      })
      if (!res.ok) throw new Error()
      poster.value = { ...poster.value, nom: posterUrl.value.trim() }
    } else {
      const res = await fetch(`${POSTER_API}/${encodeURIComponent(titre)}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nom: posterUrl.value.trim() })
      })
      if (!res.ok) throw new Error()
      const data = await res.json()
      poster.value = { _id: data.id, titreFilm: titre, nom: posterUrl.value.trim() }
    }
    showPosterForm.value = false
  } catch {
    posterError.value = 'Erreur lors de la sauvegarde.'
  } finally {
    savingPoster.value = false
  }
}

const deletePoster = async () => {
  if (!confirm('Supprimer ce poster ?')) return
  try {
    await fetch(`${POSTER_API}/${poster.value._id}`, { method: 'DELETE' })
    poster.value = null
  } catch { /* silencieux */ }
}

// ── Chargement principal ──
onMounted(async () => {
  const id = route.params.id
  try {
    const filmRes = await filmService.getById(id)
    film.value = filmRes.data
  } catch {
    film.value = { id, title: `Film #${id}`, genres: [], openForRent: false }
  } finally {
    loading.value = false
  }

  if (!film.value) return
  const titre = film.value.title || film.value.titre

  // Charger poster, reviews, réservations en parallèle
  await Promise.allSettled([
    fetchPoster(titre),
    loadReviews(titre),
    auth.isLoggedIn ? checkReservation(titre) : Promise.resolve(),
  ])
})

async function loadReviews(titre) {
  loadingReviews.value = true
  try {
    const res = await reviewService.getByFilm(titre)
    reviews.value = res.data || []
  } catch {
    reviews.value = []
  } finally {
    loadingReviews.value = false
  }
}

async function checkReservation(titre) {
  try {
    const res = await reservationService.getMesReservations()
    alreadyRented.value = (res.data || []).some(
      r => r.titreFilm === titre || r.film?.titre === titre
    )
  } catch {
    alreadyRented.value = false
  }
}

// ── Réservation ──
const openReservation = () => {
  showPayModal.value = true
  payError.value = ''
  card.value = { numero: '', expiration: '', cvv: '' }
}

const confirmPayment = async () => {
  paying.value = true
  payError.value = ''
  try {
    // ✅ Utilise film.title (titre exact) pour la réservation
    const titre = film.value.title || film.value.titre
    await reservationService.reserver(titre)
    showPayModal.value = false
    alreadyRented.value = true
  } catch (e) {
    payError.value = e.response?.data?.message || e.response?.data?.error || 'Paiement refusé.'
  } finally {
    paying.value = false
  }
}

const terminer = async () => {
  terminating.value = true
  try {
    const titre = film.value.title || film.value.titre
    await reservationService.terminerLocation(titre)
    alreadyRented.value = false
  } catch (e) {
    console.error(e)
  } finally {
    terminating.value = false
  }
}

// ── Review ──
const submitReview = async () => {
  if (newReview.value.note === 0) return
  submittingReview.value = true
  try {
    const titre = film.value.title || film.value.titre
    await reviewService.addReview(titre, newReview.value)
    reviews.value.unshift({ ...newReview.value, pseudo: auth.user?.pseudo })
    newReview.value = { note: 0, commentaire: '' }
  } catch (e) {
    console.error(e)
  } finally {
    submittingReview.value = false
  }
}
</script>

<style scoped>
.film-detail-page { min-height: 100vh; }

.loading-state { display: flex; flex-direction: column; align-items: center; justify-content: center; min-height: 60vh; gap: 1.5rem; }
.loading-spinner { width: 48px; height: 48px; border: 2px solid var(--border-subtle); border-top-color: var(--neon-cyan); border-radius: 50%; animation: spin-slow 0.8s linear infinite; }
.loading-spinner.small { width: 28px; height: 28px; }
.loading-text { font-family: var(--font-mono); font-size: 0.7rem; letter-spacing: 0.2em; color: var(--neon-cyan); animation: pulse-neon 1.5s infinite; }

.film-hero { position: relative; min-height: 70vh; display: flex; align-items: flex-end; padding-bottom: 4rem; overflow: hidden; }
.hero-backdrop { position: absolute; inset: 0; background-color: var(--bg-void); background-size: cover; background-position: center; filter: blur(20px) brightness(0.3); transform: scale(1.1); }
.hero-overlay { position: absolute; inset: 0; background: linear-gradient(to top, var(--bg-void) 30%, transparent 70%), linear-gradient(to right, var(--bg-void) 10%, transparent 60%); }

.container { max-width: 1400px; margin: 0 auto; padding: 0 2rem; }
.hero-content { position: relative; z-index: 1; display: flex; gap: 3rem; align-items: flex-end; width: 100%; }

.poster-wrap { flex-shrink: 0; display: flex; flex-direction: column; gap: 0.5rem; }
.poster-frame { width: 220px; aspect-ratio: 2/3; border: 1px solid var(--border-glow); border-radius: 8px; overflow: hidden; box-shadow: var(--shadow-neon), var(--shadow-card); }
.poster { width: 100%; height: 100%; object-fit: cover; }
.poster-placeholder { width: 100%; height: 100%; background: linear-gradient(135deg, var(--bg-card), var(--bg-void)); display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 1rem; }
.placeholder-icon { font-size: 3rem; color: rgba(0,245,255,0.2); }
.placeholder-text { font-family: var(--font-display); font-size: 2rem; font-weight: 900; color: rgba(0,245,255,0.15); letter-spacing: 0.2em; }

.poster-actions { display: flex; gap: 6px; justify-content: center; flex-wrap: wrap; }
.btn-poster-action { background: rgba(0,245,255,0.05); border: 1px solid var(--border-subtle); color: var(--text-muted); border-radius: 4px; padding: 4px 10px; font-size: 0.72rem; cursor: pointer; transition: all 0.2s; font-family: var(--font-mono); }
.btn-poster-action:hover { border-color: var(--neon-cyan); color: var(--neon-cyan); }
.btn-poster-delete:hover { border-color: var(--neon-pink); color: var(--neon-pink); }
.btn-poster-add:hover { border-color: var(--neon-cyan); color: var(--neon-cyan); box-shadow: 0 0 8px rgba(0,245,255,0.2); }

.poster-form-inline { display: flex; flex-direction: column; gap: 6px; width: 220px; }
.poster-form-btns { display: flex; gap: 6px; align-items: center; }

.film-info { flex: 1; display: flex; flex-direction: column; gap: 1rem; }
.info-tags { display: flex; flex-wrap: wrap; gap: 8px; }
.film-title { font-family: var(--font-display); font-size: clamp(1.8rem, 4vw, 3rem); font-weight: 900; color: var(--text-primary); line-height: 1.1; }
.film-director, .film-cast { display: flex; align-items: baseline; gap: 12px; flex-wrap: wrap; }
.meta-label { font-family: var(--font-mono); font-size: 0.6rem; letter-spacing: 0.2em; color: var(--neon-cyan); flex-shrink: 0; }
.meta-value, .cast-list { font-size: 0.95rem; color: var(--text-secondary); }

.film-rating { display: flex; align-items: center; gap: 12px; }
.rating-stars { display: flex; gap: 2px; }
.star { font-size: 1.1rem; color: var(--text-muted); }
.star.filled { color: var(--neon-cyan); text-shadow: 0 0 8px var(--neon-cyan); }
.rating-num { font-family: var(--font-display); font-size: 1.4rem; font-weight: 700; }
.rating-count { font-family: var(--font-mono); font-size: 0.7rem; color: var(--text-muted); }

.film-actions { display: flex; align-items: center; gap: 1.5rem; flex-wrap: wrap; margin-top: 0.5rem; }
.price-display { display: flex; flex-direction: column; }
.price-amount { font-family: var(--font-display); font-size: 2rem; font-weight: 700; color: var(--neon-cyan); text-shadow: 0 0 15px var(--neon-cyan); line-height: 1; }
.price-label { font-family: var(--font-mono); font-size: 0.65rem; color: var(--text-muted); }
.btn-reserve { padding: 14px 32px !important; }
.btn-rented { padding: 14px 32px !important; border-color: var(--neon-green) !important; color: var(--neon-green) !important; }
.closed-badge { display: flex; align-items: center; gap: 10px; font-family: var(--font-mono); font-size: 0.75rem; color: var(--neon-pink); padding: 12px 20px; border: 1px solid rgba(255,0,110,0.3); border-radius: 4px; background: rgba(255,0,110,0.05); }

.reviews-section { padding: 4rem 0 6rem; }
.review-form-card { padding: 2rem; margin-bottom: 2rem; }
.form-title { font-family: var(--font-display); font-size: 0.75rem; letter-spacing: 0.15em; color: var(--text-secondary); margin-bottom: 1.25rem; text-transform: uppercase; }
.star-picker { display: flex; align-items: center; gap: 4px; margin-bottom: 1rem; }
.star-pick { font-size: 1.8rem; color: var(--text-muted); cursor: pointer; transition: all 0.2s; }
.star-pick.active, .star-pick:hover { color: var(--neon-cyan); text-shadow: 0 0 10px var(--neon-cyan); }
.star-label { font-family: var(--font-mono); font-size: 0.7rem; color: var(--text-muted); margin-left: 8px; }
.review-textarea { width: 100%; padding: 12px 16px; background: rgba(0,245,255,0.02); border: 1px solid var(--border-subtle); border-radius: 4px; color: var(--text-primary); font-family: var(--font-body); font-size: 0.9rem; resize: vertical; outline: none; transition: border 0.3s; margin-bottom: 1rem; }
.review-textarea:focus { border-color: var(--neon-cyan); }
.form-actions { display: flex; justify-content: flex-end; }
.reviews-loading { display: flex; justify-content: center; padding: 2rem; }
.reviews-list { display: flex; flex-direction: column; gap: 1rem; }
.review-item { padding: 1.25rem 1.5rem; }
.review-header { display: flex; align-items: center; gap: 12px; margin-bottom: 0.75rem; }
.reviewer-name { font-family: var(--font-mono); font-size: 0.8rem; color: var(--neon-cyan); flex: 1; }
.review-stars { display: flex; gap: 2px; }
.star-sm { font-size: 0.75rem; color: var(--text-muted); }
.star-sm.filled { color: var(--neon-cyan); text-shadow: 0 0 5px var(--neon-cyan); }
.review-note { font-family: var(--font-mono); font-size: 0.7rem; color: var(--text-muted); }
.review-comment { font-size: 0.9rem; color: var(--text-secondary); line-height: 1.6; padding-left: 1rem; border-left: 2px solid var(--border-subtle); }
.no-reviews { text-align: center; padding: 4rem 2rem; display: flex; flex-direction: column; align-items: center; gap: 1rem; }
.no-reviews-icon { font-size: 2rem; color: var(--text-muted); animation: pulse-neon 2s infinite; }
.no-reviews p { font-family: var(--font-mono); font-size: 0.8rem; color: var(--text-muted); }

.modal-overlay { position: fixed; inset: 0; background: rgba(2,4,8,0.85); backdrop-filter: blur(10px); z-index: 2000; display: flex; align-items: center; justify-content: center; padding: 2rem; }
.pay-modal { background: var(--bg-card); border: 1px solid var(--border-glow); border-radius: 8px; width: 100%; max-width: 440px; box-shadow: var(--shadow-neon), var(--shadow-card); }
.modal-header { display: flex; justify-content: space-between; align-items: center; padding: 1.25rem 1.5rem; border-bottom: 1px solid var(--border-subtle); }
.modal-title { font-family: var(--font-mono); font-size: 0.75rem; letter-spacing: 0.2em; color: var(--neon-cyan); }
.modal-close { background: none; border: none; color: var(--text-muted); cursor: pointer; font-size: 1rem; transition: color 0.2s; }
.modal-close:hover { color: var(--neon-pink); }
.modal-body { padding: 1.5rem; display: flex; flex-direction: column; gap: 1.25rem; }
.pay-summary { display: flex; justify-content: space-between; align-items: center; padding: 1rem; background: rgba(0,245,255,0.03); border: 1px solid var(--border-subtle); border-radius: 4px; }
.pay-film { font-family: var(--font-display); font-size: 0.75rem; color: var(--text-secondary); }
.pay-price { font-family: var(--font-display); font-size: 1.2rem; font-weight: 700; }
.field-group { display: flex; flex-direction: column; gap: 0.4rem; flex: 1; }
.field-label { font-family: var(--font-mono); font-size: 0.6rem; letter-spacing: 0.2em; color: var(--neon-cyan); }
.card-row { display: flex; gap: 1rem; }
.cyber-input-modal { padding: 10px 14px; background: rgba(0,245,255,0.03); border: 1px solid var(--border-subtle); border-radius: 4px; color: var(--text-primary); font-family: var(--font-mono); font-size: 0.9rem; outline: none; transition: border 0.3s; width: 100%; }
.cyber-input-modal:focus { border-color: var(--neon-cyan); }
.pay-error { font-family: var(--font-mono); font-size: 0.75rem; color: var(--neon-pink); padding: 10px 16px; background: rgba(255,0,110,0.08); border: 1px solid rgba(255,0,110,0.3); border-radius: 4px; }
.btn-pay { width: 100%; padding: 14px; background: transparent; border: 1px solid var(--neon-cyan); color: var(--neon-cyan); font-family: var(--font-display); font-size: 0.75rem; letter-spacing: 0.15em; text-transform: uppercase; cursor: pointer; position: relative; overflow: hidden; transition: all 0.3s; clip-path: polygon(12px 0%, 100% 0%, calc(100% - 12px) 100%, 0% 100%); }
.btn-pay::before { content: ''; position: absolute; inset: 0; background: var(--neon-cyan); transform: translateX(-101%); transition: transform 0.3s; }
.btn-pay:not(:disabled):hover { color: var(--bg-void); }
.btn-pay:not(:disabled):hover::before { transform: translateX(0); }
.btn-pay span { position: relative; z-index: 1; }
.btn-pay:disabled { opacity: 0.6; cursor: not-allowed; }

.modal-enter-active, .modal-leave-active { transition: all 0.3s ease; }
.modal-enter-from, .modal-leave-to { opacity: 0; transform: scale(0.95); }
.fade-enter-active, .fade-leave-active { transition: all 0.3s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

@media (max-width: 768px) {
  .hero-content { flex-direction: column; align-items: flex-start; }
  .poster-frame { width: 150px; }
  .poster-form-inline { width: 150px; }
}
</style>
