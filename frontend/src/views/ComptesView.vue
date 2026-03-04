<template>
  <div class="comptes-page">
    <div class="container">

      <!-- Header -->
      <div class="page-header">
        <div class="header-left">
          <p class="page-eyebrow"><span class="eye-prefix">//</span> GESTION</p>
          <h1 class="page-title">COMPTES<span class="title-neon"> UTILISATEURS</span></h1>
          <p class="page-count" v-if="!loading">
            <span class="count-val">{{ comptes.length }}</span>
            <span class="count-label"> utilisateurs enregistrés</span>
          </p>
        </div>
        <button class="btn-cyber btn-cyber-pink" @click="openCreate">
          <span>+ NOUVEAU COMPTE</span>
        </button>
      </div>



      <!-- Loading -->
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <p class="loading-text">CHARGEMENT DES COMPTES...</p>
      </div>

      <!-- Error -->
      <div v-else-if="error" class="error-state">
        <span class="error-icon">⚠</span>
        <p>{{ error }}</p>
        <button class="btn-cyber" @click="loadComptes"><span>Réessayer</span></button>
      </div>

      <!-- Empty -->
      <div v-else-if="filtered.length === 0" class="empty-state">
        <span class="empty-icon">◈</span>
        <p>Aucun compte trouvé</p>
      </div>

      <!-- Table -->
      <div v-else class="table-wrap">
        <table class="comptes-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>PSEUDO</th>
              <th>NOM</th>
              <th>PRÉNOM</th>
              <th>DATE NAISSANCE</th>
              <th>ADRESSE</th>
              <th>RÔLE</th>
              <th>ACTIONS</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="compte in filtered" :key="compte.id" class="compte-row">
              <td><span class="id-badge">{{ compte.id }}</span></td>
              <td>
                <span class="pseudo-wrap">
                  <span class="pseudo-icon">◈</span>
                  {{ compte.pseudo }}
                </span>
              </td>
              <td>{{ compte.nom || '—' }}</td>
              <td>{{ compte.prenom || '—' }}</td>
              <td class="td-mono">{{ compte.dateDeNaissance || '—' }}</td>
              <td class="td-adresse">{{ compte.adresse || '—' }}</td>
              <td>
                <span class="role-badge" :class="(compte.role || 'user').toLowerCase()">
                  {{ compte.role || 'USER' }}
                </span>
              </td>
              <td>
                <div class="actions-cell">
                  <button class="action-btn edit" @click="openEdit(compte)" title="Modifier">✎</button>
                  <button class="action-btn delete" @click="openDelete(compte)" title="Supprimer">✕</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

    </div>

    <!-- ── Modal Créer / Modifier ── -->
    <Teleport to="body">
      <Transition name="modal">
        <div v-if="showFormModal" class="modal-overlay" @click.self="showFormModal = false">
          <div class="form-modal">

            <div class="modal-header">
              <span class="modal-title">
                {{ editMode ? '// MODIFIER LE COMPTE' : '// NOUVEAU COMPTE' }}
              </span>
              <button class="modal-close" @click="showFormModal = false">✕</button>
            </div>

            <div class="modal-body">
              <div class="form-grid">

                <div class="field-group full">
                  <label class="field-label">PSEUDO</label>
                  <input v-model="form.pseudo" class="cyber-input" placeholder="pseudo_unique" />
                </div>

                <div class="field-group">
                  <label class="field-label">NOM</label>
                  <input v-model="form.nom" class="cyber-input" placeholder="Dupont" />
                </div>

                <div class="field-group">
                  <label class="field-label">PRÉNOM</label>
                  <input v-model="form.prenom" class="cyber-input" placeholder="Jean" />
                </div>

                <div class="field-group">
                  <label class="field-label">DATE DE NAISSANCE</label>
                  <input v-model="form.dateDeNaissance" type="date" class="cyber-input" />
                </div>

                <div class="field-group">
                  <label class="field-label">RÔLE</label>
                  <select v-model="form.role" class="cyber-input cyber-select">
                    <option value="">USER</option>
                    <option value="ADMIN">ADMIN</option>
                  </select>
                </div>

                <div class="field-group full">
                  <label class="field-label">ADRESSE</label>
                  <input v-model="form.adresse" class="cyber-input" placeholder="1 rue de la Paix, Paris" />
                </div>

                <div class="field-group">
                  <label class="field-label">MOT DE PASSE{{ editMode ? ' (laisser vide = inchangé)' : '' }}</label>
                  <input v-model="form.motDePasse" type="password" class="cyber-input" placeholder="••••••••" />
                </div>

              </div>

              <Transition name="fade">
                <div v-if="formError" class="form-error">⚠ {{ formError }}</div>
              </Transition>

              <div class="modal-actions">
                <button class="btn-cyber" @click="showFormModal = false">
                  <span>Annuler</span>
                </button>
                <button
                  class="btn-cyber btn-cyber-pink"
                  @click="submitForm"
                  :disabled="submitting"
                >
                  <span>{{ submitting ? 'TRAITEMENT...' : (editMode ? 'ENREGISTRER' : 'CRÉER') }}</span>
                </button>
              </div>
            </div>

          </div>
        </div>
      </Transition>
    </Teleport>

    <!-- ── Modal Confirmer Suppression ── -->
    <Teleport to="body">
      <Transition name="modal">
        <div v-if="showDeleteModal" class="modal-overlay" @click.self="showDeleteModal = false">
          <div class="delete-modal">

            <div class="modal-header">
              <span class="modal-title delete-title">// CONFIRMER LA SUPPRESSION</span>
              <button class="modal-close" @click="showDeleteModal = false">✕</button>
            </div>

            <div class="modal-body">
              <p class="delete-text">
                Supprimer le compte de
                <span class="delete-name">{{ selectedCompte?.pseudo }}</span> ?
              </p>
              <p class="delete-warn">⚠ Cette action est irréversible.</p>

              <Transition name="fade">
                <div v-if="deleteError" class="form-error">⚠ {{ deleteError }}</div>
              </Transition>

              <div class="modal-actions">
                <button class="btn-cyber" @click="showDeleteModal = false">
                  <span>Annuler</span>
                </button>
                <button
                  class="btn-cyber btn-cyber-pink"
                  @click="confirmDelete"
                  :disabled="deleting"
                >
                  <span>{{ deleting ? 'SUPPRESSION...' : 'CONFIRMER' }}</span>
                </button>
              </div>
            </div>

          </div>
        </div>
      </Transition>
    </Teleport>

    <!-- Toast succès -->
    <Teleport to="body">
      <Transition name="toast">
        <div v-if="toast" class="toast">
          <span class="toast-icon">✓</span> {{ toast }}
        </div>
      </Transition>
    </Teleport>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import authService from '../services/authService'

const comptes = ref([])
const loading = ref(true)
const error = ref('')
const search = ref('')

// Modals
const showFormModal = ref(false)
const showDeleteModal = ref(false)
const editMode = ref(false)
const selectedCompte = ref(null)
const submitting = ref(false)
const deleting = ref(false)
const formError = ref('')
const deleteError = ref('')
const toast = ref('')

const form = ref({
  pseudo: '', nom: '', prenom: '',
  dateDeNaissance: '', adresse: '',
  role: '', motDePasse: ''
})

// ── Chargement ──
const loadComptes = async () => {
  loading.value = true
  error.value = ''
  try {
    const res = await authService.getAll()
    comptes.value = res.data || []
  } catch {
    error.value = 'Impossible de charger la liste des comptes.'
  } finally {
    loading.value = false
  }
}

onMounted(loadComptes)

// ── Filtrage ──
const filtered = computed(() => {
  const q = search.value.toLowerCase()
  if (!q) return comptes.value
  return comptes.value.filter(c =>
    c.pseudo?.toLowerCase().includes(q) ||
    c.nom?.toLowerCase().includes(q) ||
    c.prenom?.toLowerCase().includes(q)
  )
})

// ── Ouvrir modal créer ──
const openCreate = () => {
  editMode.value = false
  formError.value = ''
  form.value = { pseudo: '', nom: '', prenom: '', dateDeNaissance: '', adresse: '', role: '', motDePasse: '' }
  showFormModal.value = true
}

// ── Ouvrir modal modifier ──
const openEdit = (compte) => {
  editMode.value = true
  formError.value = ''
  selectedCompte.value = compte
  form.value = {
    pseudo: compte.pseudo || '',
    nom: compte.nom || '',
    prenom: compte.prenom || '',
    dateDeNaissance: compte.dateDeNaissance || '',
    adresse: compte.adresse || '',
    role: compte.role || '',
    motDePasse: ''
  }
  showFormModal.value = true
}

// ── Ouvrir modal supprimer ──
const openDelete = (compte) => {
  selectedCompte.value = compte
  deleteError.value = ''
  showDeleteModal.value = true
}

// ── Soumettre formulaire (créer ou modifier) ──
const submitForm = async () => {
  formError.value = ''
  if (!form.value.pseudo) { formError.value = 'Le pseudo est obligatoire.'; return }

  submitting.value = true
  try {
    const payload = {
      pseudo: form.value.pseudo,
      nom: form.value.nom,
      prenom: form.value.prenom,
      dateDeNaissance: form.value.dateDeNaissance,
      adresse: form.value.adresse,
      role: form.value.role || null,
    }
    // Ajoute motDePasse seulement s'il est rempli
    if (form.value.motDePasse) payload.motDePasse = form.value.motDePasse

    if (editMode.value) {
      // PUT /comptes/{id}
      await authService.update(selectedCompte.value.id, payload)
      const idx = comptes.value.findIndex(c => c.id === selectedCompte.value.id)
      if (idx !== -1) comptes.value[idx] = { ...comptes.value[idx], ...payload }
      showToast('Compte modifié avec succès')
    } else {
      // POST /comptes
      const res = await authService.register(payload)
      comptes.value.push(res.data)
      showToast('Compte créé avec succès')
    }
    showFormModal.value = false
  } catch (e) {
    formError.value = e.response?.data?.message || 'Une erreur est survenue.'
  } finally {
    submitting.value = false
  }
}

// ── Confirmer suppression ──
const confirmDelete = async () => {
  deleteError.value = ''
  deleting.value = true
  try {
    await authService.delete(selectedCompte.value.id)
    comptes.value = comptes.value.filter(c => c.id !== selectedCompte.value.id)
    showDeleteModal.value = false
    showToast('Compte supprimé')
  } catch (e) {
    deleteError.value = e.response?.data?.message || 'Erreur lors de la suppression.'
  } finally {
    deleting.value = false
  }
}

// ── Toast ──
const showToast = (msg) => {
  toast.value = msg
  setTimeout(() => { toast.value = '' }, 3000)
}
</script>

<style scoped>
.comptes-page { padding: 3rem 0 6rem; }
.container { max-width: 1300px; margin: 0 auto; padding: 0 2rem; }

/* Header */
.page-header {
  display: flex; justify-content: space-between;
  align-items: flex-end; margin-bottom: 2rem;
}

.page-eyebrow { font-family: var(--font-mono); font-size: 0.7rem; letter-spacing: 0.2em; color: var(--text-muted); margin-bottom: 0.5rem; }
.eye-prefix { color: var(--neon-cyan); margin-right: 6px; }
.page-title { font-family: var(--font-display); font-size: clamp(1.8rem, 4vw, 2.8rem); font-weight: 900; letter-spacing: 0.05em; color: var(--text-primary); line-height: 1; margin-bottom: 0.5rem; }
.title-neon { color: var(--neon-cyan); text-shadow: 0 0 20px var(--neon-cyan); }
.page-count { font-family: var(--font-mono); font-size: 0.8rem; }
.count-val { color: var(--neon-cyan); font-size: 1.1rem; font-weight: 700; }
.count-label { color: var(--text-muted); }

/* Search */
.search-bar { position: relative; display: flex; align-items: center; margin-bottom: 1.5rem; max-width: 400px; }
.search-icon { position: absolute; left: 14px; color: var(--text-muted); font-size: 1.1rem; pointer-events: none; }
.search-input { width: 100%; padding: 10px 36px; background: var(--bg-card); border: 1px solid var(--border-subtle); border-radius: 4px; color: var(--text-primary); font-family: var(--font-mono); font-size: 0.85rem; outline: none; transition: border 0.3s; }
.search-input::placeholder { color: var(--text-muted); }
.search-input:focus { border-color: var(--neon-cyan); }
.clear-btn { position: absolute; right: 12px; background: none; border: none; color: var(--text-muted); cursor: pointer; font-size: 0.7rem; }
.clear-btn:hover { color: var(--neon-pink); }

/* States */
.loading-state { display: flex; flex-direction: column; align-items: center; padding: 6rem 2rem; gap: 1.5rem; }
.loading-spinner { width: 40px; height: 40px; border: 2px solid var(--border-subtle); border-top-color: var(--neon-cyan); border-radius: 50%; animation: spin-slow 0.8s linear infinite; }
.loading-text { font-family: var(--font-mono); font-size: 0.7rem; letter-spacing: 0.2em; color: var(--neon-cyan); animation: pulse-neon 1.5s infinite; }
.error-state, .empty-state { text-align: center; padding: 4rem 2rem; display: flex; flex-direction: column; align-items: center; gap: 1rem; font-family: var(--font-mono); font-size: 0.8rem; color: var(--text-muted); }
.error-icon { font-size: 2rem; color: var(--neon-pink); }
.empty-icon { font-size: 2rem; color: var(--text-muted); animation: pulse-neon 2s infinite; }

/* Table */
.table-wrap { overflow-x: auto; border: 1px solid var(--border-subtle); border-radius: 8px; }
.comptes-table { width: 100%; border-collapse: collapse; }
.comptes-table thead tr { background: rgba(0,245,255,0.03); border-bottom: 1px solid var(--border-glow); }
.comptes-table th { padding: 12px 16px; font-family: var(--font-mono); font-size: 0.6rem; letter-spacing: 0.2em; color: var(--neon-cyan); text-align: left; white-space: nowrap; }
.comptes-table td { padding: 12px 16px; font-family: var(--font-body); font-size: 0.88rem; color: var(--text-secondary); border-bottom: 1px solid var(--border-subtle); }
.compte-row:hover { background: rgba(0,245,255,0.02); }
.compte-row:last-child td { border-bottom: none; }

.id-badge { font-family: var(--font-mono); font-size: 0.72rem; color: var(--text-muted); background: var(--bg-void); padding: 2px 8px; border-radius: 2px; border: 1px solid var(--border-subtle); }
.pseudo-wrap { display: flex; align-items: center; gap: 6px; font-family: var(--font-mono); font-size: 0.85rem; color: var(--neon-cyan); }
.pseudo-icon { opacity: 0.5; font-size: 0.7rem; }
.td-mono { font-family: var(--font-mono); font-size: 0.8rem; }
.td-adresse { max-width: 180px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

.role-badge { font-family: var(--font-mono); font-size: 0.6rem; letter-spacing: 0.1em; padding: 3px 10px; border-radius: 2px; text-transform: uppercase; }
.role-badge.admin { background: rgba(255,107,0,0.1); border: 1px solid rgba(255,107,0,0.3); color: var(--neon-orange); }
.role-badge.user, .role-badge.null { background: rgba(0,245,255,0.05); border: 1px solid rgba(0,245,255,0.2); color: var(--text-muted); }

/* Actions */
.actions-cell { display: flex; gap: 6px; }

.action-btn {
  width: 30px; height: 30px; border-radius: 4px; border: 1px solid;
  background: transparent; cursor: pointer; font-size: 0.85rem;
  display: flex; align-items: center; justify-content: center; transition: all 0.2s;
}

.action-btn.edit { border-color: rgba(0,245,255,0.3); color: var(--neon-cyan); }
.action-btn.edit:hover { background: rgba(0,245,255,0.1); box-shadow: 0 0 10px rgba(0,245,255,0.2); }
.action-btn.delete { border-color: rgba(255,0,110,0.3); color: var(--neon-pink); }
.action-btn.delete:hover { background: rgba(255,0,110,0.1); box-shadow: 0 0 10px rgba(255,0,110,0.2); }

/* Modals */
.modal-overlay { position: fixed; inset: 0; background: rgba(2,4,8,0.88); backdrop-filter: blur(10px); z-index: 2000; display: flex; align-items: center; justify-content: center; padding: 2rem; }

.form-modal, .delete-modal { background: var(--bg-card); border-radius: 8px; width: 100%; box-shadow: var(--shadow-card); }
.form-modal { max-width: 560px; border: 1px solid var(--border-glow); box-shadow: var(--shadow-neon), var(--shadow-card); }
.delete-modal { max-width: 420px; border: 1px solid rgba(255,0,110,0.3); box-shadow: var(--shadow-pink), var(--shadow-card); }

.modal-header { display: flex; justify-content: space-between; align-items: center; padding: 1.25rem 1.5rem; border-bottom: 1px solid var(--border-subtle); }
.modal-title { font-family: var(--font-mono); font-size: 0.72rem; letter-spacing: 0.15em; color: var(--neon-cyan); }
.delete-title { color: var(--neon-pink); }
.modal-close { background: none; border: none; color: var(--text-muted); cursor: pointer; font-size: 1rem; transition: color 0.2s; }
.modal-close:hover { color: var(--neon-pink); }

.modal-body { padding: 1.5rem; display: flex; flex-direction: column; gap: 1.25rem; }

.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
.field-group { display: flex; flex-direction: column; gap: 0.4rem; }
.field-group.full { grid-column: 1 / -1; }
.field-label { font-family: var(--font-mono); font-size: 0.6rem; letter-spacing: 0.15em; color: var(--neon-cyan); }

.cyber-input {
  padding: 10px 14px; background: rgba(0,245,255,0.03);
  border: 1px solid var(--border-subtle); border-radius: 4px;
  color: var(--text-primary); font-family: var(--font-mono); font-size: 0.88rem;
  outline: none; transition: border 0.3s; width: 100%;
}

.cyber-input:focus { border-color: var(--neon-cyan); background: rgba(0,245,255,0.05); }
.cyber-input::placeholder { color: var(--text-muted); }

.cyber-select { appearance: none; cursor: pointer; }
.cyber-select option { background: var(--bg-card); color: var(--text-primary); }

.form-error { font-family: var(--font-mono); font-size: 0.75rem; color: var(--neon-pink); padding: 10px 16px; background: rgba(255,0,110,0.08); border: 1px solid rgba(255,0,110,0.3); border-radius: 4px; }

.modal-actions { display: flex; gap: 1rem; justify-content: flex-end; margin-top: 0.5rem; }

.delete-text { font-size: 0.95rem; color: var(--text-secondary); line-height: 1.6; }
.delete-name { font-family: var(--font-display); font-size: 0.9rem; color: var(--neon-cyan); }
.delete-warn { font-family: var(--font-mono); font-size: 0.72rem; color: var(--neon-pink); }

/* Toast */
.toast {
  position: fixed; bottom: 2rem; right: 2rem; z-index: 3000;
  background: var(--bg-card); border: 1px solid rgba(0,255,136,0.4);
  border-radius: 6px; padding: 12px 20px;
  font-family: var(--font-mono); font-size: 0.8rem; color: var(--neon-green);
  box-shadow: 0 0 20px rgba(0,255,136,0.15), var(--shadow-card);
  display: flex; align-items: center; gap: 10px;
}

.toast-icon { font-size: 1rem; }

/* Transitions */
.modal-enter-active, .modal-leave-active { transition: all 0.25s ease; }
.modal-enter-from, .modal-leave-to { opacity: 0; transform: scale(0.96); }
.toast-enter-active, .toast-leave-active { transition: all 0.3s ease; }
.toast-enter-from { opacity: 0; transform: translateY(10px); }
.toast-leave-to { opacity: 0; transform: translateY(10px); }
.fade-enter-active, .fade-leave-active { transition: all 0.2s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

@media (max-width: 500px) {
  .form-grid { grid-template-columns: 1fr; }
  .page-header { flex-direction: column; align-items: flex-start; gap: 1rem; }
}
</style>