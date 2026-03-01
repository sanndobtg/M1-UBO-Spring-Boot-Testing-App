<template>
  <div class="film-card cyber-card" @click="$emit('click')">
    <!-- Poster -->
    <div class="card-poster">
      <img v-if="film.affiche" :src="film.affiche" :alt="film.titre" class="poster-img" />
      <div v-else class="poster-placeholder">
        <span class="poster-icon">◈</span>
        <span class="poster-title-short">{{ initials }}</span>
      </div>
      <!-- Badges -->
      <div class="card-badges">
        <span class="badge badge-year">{{ film.annee }}</span>
        <span v-if="film.ouvert" class="badge badge-open">● DISPO</span>
        <span v-else class="badge badge-closed">● FERMÉ</span>
      </div>
      <!-- Price -->
      <div v-if="film.prix" class="card-price">
        {{ film.prix?.toFixed(2) }} €
      </div>
    </div>

    <!-- Info -->
    <div class="card-info">
      <h3 class="card-title">{{ film.titre }}</h3>
      <p v-if="film.realisateur" class="card-director">
        <span class="director-label">DIR.</span> {{ film.realisateur }}
      </p>
      <div class="card-genres">
        <span
          v-for="g in (film.genres || film.genre || []).slice(0, 2)"
          :key="g"
          class="tag tag-cyan"
        >{{ g }}</span>
      </div>

      <!-- Rating -->
      <div v-if="film.note !== undefined" class="card-rating">
        <div class="stars">
          <span v-for="i in 5" :key="i" class="star" :class="{ filled: i <= Math.round(film.note) }">★</span>
        </div>
        <span class="rating-value">{{ film.note?.toFixed(1) }}</span>
      </div>

      <!-- Age min -->
      <div v-if="film.ageMin" class="age-badge">
        <span>{{ film.ageMin }}+</span>
      </div>
    </div>

    <!-- Hover CTA -->
    <div class="card-cta">
      <span class="cta-text">Voir le film →</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  film: { type: Object, required: true }
})
defineEmits(['click'])

const initials = computed(() =>
  props.film.titre?.split(' ').slice(0, 2).map(w => w[0]).join('').toUpperCase()
)
</script>

<style scoped>
.film-card {
  cursor: pointer;
  display: flex;
  flex-direction: column;
  border-radius: 8px;
  overflow: hidden;
  position: relative;
}

/* Poster */
.card-poster {
  position: relative;
  aspect-ratio: 2/3;
  overflow: hidden;
  background: var(--bg-void);
}

.poster-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.film-card:hover .poster-img {
  transform: scale(1.05);
}

.poster-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--bg-card) 0%, var(--bg-void) 100%);
  gap: 0.5rem;
}

.poster-icon {
  font-size: 2.5rem;
  color: var(--neon-cyan);
  opacity: 0.3;
}

.poster-title-short {
  font-family: var(--font-display);
  font-size: 1.5rem;
  font-weight: 900;
  color: rgba(0, 245, 255, 0.2);
  letter-spacing: 0.2em;
}

/* Badges */
.card-badges {
  position: absolute;
  top: 10px;
  left: 10px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.badge {
  font-family: var(--font-mono);
  font-size: 0.6rem;
  padding: 2px 8px;
  border-radius: 2px;
  letter-spacing: 0.08em;
}

.badge-year {
  background: rgba(6, 13, 20, 0.9);
  color: var(--text-secondary);
  border: 1px solid var(--border-subtle);
}

.badge-open {
  background: rgba(0, 255, 136, 0.1);
  color: var(--neon-green);
  border: 1px solid rgba(0, 255, 136, 0.3);
}

.badge-closed {
  background: rgba(255, 0, 110, 0.1);
  color: var(--neon-pink);
  border: 1px solid rgba(255, 0, 110, 0.3);
}

.card-price {
  position: absolute;
  bottom: 10px;
  right: 10px;
  font-family: var(--font-display);
  font-size: 0.85rem;
  font-weight: 700;
  color: var(--neon-cyan);
  background: rgba(2, 4, 8, 0.9);
  padding: 4px 10px;
  border-radius: 4px;
  border: 1px solid rgba(0, 245, 255, 0.3);
  text-shadow: 0 0 8px var(--neon-cyan);
}

/* Info */
.card-info {
  padding: 1rem;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.card-title {
  font-family: var(--font-display);
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0.05em;
  color: var(--text-primary);
  line-height: 1.3;
  text-transform: uppercase;
}

.card-director {
  font-family: var(--font-mono);
  font-size: 0.65rem;
  color: var(--text-muted);
}

.director-label {
  color: var(--neon-cyan);
  opacity: 0.7;
}

.card-genres {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.card-rating {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: auto;
}

.stars { display: flex; gap: 2px; }

.star {
  font-size: 0.75rem;
  color: var(--text-muted);
  transition: color 0.2s;
}

.star.filled {
  color: var(--neon-cyan);
  text-shadow: 0 0 6px var(--neon-cyan);
}

.rating-value {
  font-family: var(--font-mono);
  font-size: 0.7rem;
  color: var(--neon-cyan);
}

.age-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  font-family: var(--font-mono);
  font-size: 0.6rem;
  background: rgba(255, 107, 0, 0.15);
  color: var(--neon-orange);
  border: 1px solid rgba(255, 107, 0, 0.4);
  border-radius: 2px;
  padding: 2px 6px;
}

/* Hover CTA */
.card-cta {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgba(0, 245, 255, 0.15), transparent 60%);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  padding-bottom: 1rem;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.film-card:hover .card-cta {
  opacity: 1;
}

.cta-text {
  font-family: var(--font-display);
  font-size: 0.65rem;
  letter-spacing: 0.15em;
  text-transform: uppercase;
  color: var(--neon-cyan);
  text-shadow: 0 0 10px var(--neon-cyan);
}
</style>