<template>
  <div class="auth-page">
    <div class="auth-bg">
      <div class="auth-orb orb-pink"></div>
    </div>

    <div class="auth-container wide">
      <div class="terminal-header">
        <span class="terminal-dot red"></span>
        <span class="terminal-dot yellow"></span>
        <span class="terminal-dot green"></span>
        <span class="terminal-title">AUTH_SYSTEM :: NEW_USER_REGISTRATION</span>
      </div>

      <div class="auth-card">
        <div class="auth-logo">
          <span class="logo-symbol pink">◉</span>
          <h1 class="auth-title">INSCRIPTION</h1>
          <p class="auth-subtitle">Créez votre accès au système VOD</p>
        </div>

        <form class="auth-form" @submit.prevent="handleRegister">
          <div class="form-grid">
            <!-- Pseudo -->
            <div class="field-group full">
              <label class="field-label"><span class="label-prefix">&gt;</span> IDENTIFIANT (PSEUDO)</label>
              <div class="input-wrapper">
                <span class="input-icon">◈</span>
                <input v-model="form.pseudo" type="text" class="cyber-input" placeholder="mon_pseudo_unique" required />
              </div>
            </div>

            <!-- Nom / Prénom -->
            <div class="field-group">
              <label class="field-label"><span class="label-prefix">&gt;</span> NOM</label>
              <div class="input-wrapper">
                <input v-model="form.nom" type="text" class="cyber-input no-icon" placeholder="Dupont" required />
              </div>
            </div>

            <div class="field-group">
              <label class="field-label"><span class="label-prefix">&gt;</span> PRÉNOM</label>
              <div class="input-wrapper">
                <input v-model="form.prenom" type="text" class="cyber-input no-icon" placeholder="Jean" required />
              </div>
            </div>

            <!-- Age -->
            <div class="field-group">
              <label class="field-label"><span class="label-prefix">&gt;</span> ÂGE</label>
              <div class="input-wrapper">
               <div class="field-group">
                 <label class="field-label"><span class="label-prefix">&gt;</span> DATE DE NAISSANCE</label>
                 <div class="input-wrapper">
                   <input
                     v-model="form.dateDeNaissance"
                     type="date"
                     class="cyber-input no-icon"
                     required
                   />
                 </div>
               </div>
              </div>
            </div>

            <!-- Adresse -->
            <div class="field-group">
              <label class="field-label"><span class="label-prefix">&gt;</span> ADRESSE</label>
              <div class="input-wrapper">
                <input v-model="form.adresse" type="text" class="cyber-input no-icon" placeholder="1 rue de la Paix, Paris" required />
              </div>
            </div>

            <!-- Password -->
            <div class="field-group">
              <label class="field-label"><span class="label-prefix">&gt;</span> MOT DE PASSE</label>
              <div class="input-wrapper">
                <span class="input-icon">◉</span>
                <input v-model="form.motDePasse" :type="showPwd ? 'text' : 'password'" class="cyber-input" placeholder="••••••••" required />
                <button type="button" class="toggle-pwd" @click="showPwd = !showPwd">{{ showPwd ? '◎' : '◉' }}</button>
              </div>
            </div>

            <!-- Confirm password -->
            <div class="field-group">
              <label class="field-label"><span class="label-prefix">&gt;</span> CONFIRMATION</label>
              <div class="input-wrapper">
                <span class="input-icon pink-icon">◉</span>
                <input v-model="form.confirmPassword" :type="showPwd2 ? 'text' : 'password'" class="cyber-input" placeholder="••••••••" required />
                <button type="button" class="toggle-pwd" @click="showPwd2 = !showPwd2">{{ showPwd2 ? '◎' : '◉' }}</button>
              </div>
            </div>
          </div>

          <!-- Password strength -->
          <div v-if="form.password" class="pwd-strength">
            <div class="strength-bar">
              <div class="strength-fill" :style="{ width: strengthPct + '%', background: strengthColor }"></div>
            </div>
            <span class="strength-label" :style="{ color: strengthColor }">{{ strengthLabel }}</span>
          </div>

          <!-- Error / Success -->
          <Transition name="fade">
            <div v-if="error" class="error-box">
              <span class="error-icon">⚠</span> {{ error }}
            </div>
          </Transition>

          <button type="submit" class="btn-submit pink" :disabled="loading">
            <span v-if="!loading">CRÉER LE COMPTE</span>
            <span v-else class="loading-dots">TRAITEMENT<span>.</span><span>.</span><span>.</span></span>
          </button>
        </form>

        <div class="auth-footer">
          <span class="footer-text">Déjà un compte ?</span>
          <RouterLink to="/login" class="footer-link">Connexion →</RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import authService from '../services/authService'

const router = useRouter()
const auth = useAuthStore()
const showPwd = ref(false)
const showPwd2 = ref(false)
const error = ref('')
const loading = ref(false)

const form = ref({
  pseudo: '', nom: '', prenom: '', dateDeNaissance: '',
  adresse: '', motDePasse: '', confirmPassword: ''
})

// Password strength
const strengthPct = computed(() => {
  const pwd = form.value.motDePasse
  if (!pwd) return 0
  let score = 0
  if (pwd.length >= 8) score += 25
  if (/[A-Z]/.test(pwd)) score += 25
  if (/[0-9]/.test(pwd)) score += 25
  if (/[^A-Za-z0-9]/.test(pwd)) score += 25
  return score
})

const strengthColor = computed(() => {
  if (strengthPct.value <= 25) return 'var(--neon-pink)'
  if (strengthPct.value <= 50) return 'var(--neon-orange)'
  if (strengthPct.value <= 75) return 'var(--neon-cyan)'
  return 'var(--neon-green)'
})

const strengthLabel = computed(() => {
  if (strengthPct.value <= 25) return 'FAIBLE'
  if (strengthPct.value <= 50) return 'MOYEN'
  if (strengthPct.value <= 75) return 'BON'
  return 'FORT'
})

const handleRegister = async () => {
  error.value = ''

  // ICI : Utilise motDePasse au lieu de password
  if (form.value.motDePasse !== form.value.confirmPassword) {
    error.value = 'Les mots de passe ne correspondent pas.'
    return
  }

  loading.value = true
  try {
    await authService.register({
      pseudo: form.value.pseudo,
      nom: form.value.nom,
      prenom: form.value.prenom,
      dateDeNaissance: form.value.dateDeNaissance,
      adresse: form.value.adresse,
      motDePasse: form.value.motDePasse, // <--- ICI
    })

    // ICI AUSSI pour le login automatique après inscription
    await auth.login(form.value.pseudo, form.value.motDePasse)
    router.push('/')
  } catch (e) {
    error.value = e.response?.data?.message || 'Erreur lors de l\'inscription.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  position: relative;
  overflow: hidden;
}

.auth-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.auth-orb {
  position: absolute;
  width: 600px; height: 600px;
  top: 50%; left: 50%;
  transform: translate(-50%, -50%);
  border-radius: 50%;
  filter: blur(60px);
}

.orb-pink {
  background: radial-gradient(circle, rgba(255, 0, 110, 0.08), transparent 70%);
}

.auth-container {
  position: relative;
  width: 100%;
  max-width: 440px;
}

.auth-container.wide {
  max-width: 640px;
}

.terminal-header {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 0, 110, 0.05);
  border: 1px solid rgba(255, 0, 110, 0.15);
  border-bottom: none;
  padding: 10px 16px;
  border-radius: 8px 8px 0 0;
}

.terminal-dot { width: 10px; height: 10px; border-radius: 50%; }
.terminal-dot.red { background: #ff5f57; }
.terminal-dot.yellow { background: #ffbd2e; }
.terminal-dot.green { background: #28ca41; }

.terminal-title {
  font-family: var(--font-mono);
  font-size: 0.6rem;
  letter-spacing: 0.12em;
  color: var(--text-muted);
  margin-left: 8px;
}

.auth-card {
  background: var(--bg-card);
  border: 1px solid rgba(255, 0, 110, 0.25);
  border-radius: 0 0 8px 8px;
  padding: 2.5rem;
  box-shadow: var(--shadow-pink), var(--shadow-card);
}

.auth-logo { text-align: center; margin-bottom: 2rem; }

.logo-symbol {
  font-size: 2rem;
  display: block;
  margin-bottom: 1rem;
  animation: pulse-neon 2s ease-in-out infinite;
}

.logo-symbol.pink {
  color: var(--neon-pink);
  text-shadow: 0 0 20px var(--neon-pink);
}

.auth-title {
  font-family: var(--font-display);
  font-size: 1.5rem;
  font-weight: 900;
  letter-spacing: 0.2em;
  color: var(--text-primary);
  margin-bottom: 0.5rem;
}

.auth-subtitle {
  font-family: var(--font-mono);
  font-size: 0.7rem;
  color: var(--text-muted);
  letter-spacing: 0.1em;
}

.auth-form { display: flex; flex-direction: column; gap: 1.25rem; }

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.25rem;
}

.field-group { display: flex; flex-direction: column; gap: 0.4rem; }
.field-group.full { grid-column: 1 / -1; }

.field-label {
  font-family: var(--font-mono);
  font-size: 0.6rem;
  letter-spacing: 0.2em;
  color: var(--neon-pink);
}

.label-prefix { opacity: 0.6; margin-right: 4px; }

.input-wrapper { position: relative; display: flex; align-items: center; }

.input-icon {
  position: absolute;
  left: 14px;
  color: var(--neon-cyan);
  opacity: 0.5;
  font-size: 0.8rem;
  pointer-events: none;
}

.pink-icon { color: var(--neon-pink) !important; }

.cyber-input {
  width: 100%;
  padding: 10px 14px 10px 38px;
  background: rgba(255, 0, 110, 0.02);
  border: 1px solid rgba(255, 0, 110, 0.15);
  border-radius: 4px;
  color: var(--text-primary);
  font-family: var(--font-mono);
  font-size: 0.85rem;
  transition: all 0.3s ease;
  outline: none;
}

.cyber-input.no-icon { padding-left: 14px; }
.cyber-input::placeholder { color: var(--text-muted); }

.cyber-input:focus {
  border-color: var(--neon-pink);
  background: rgba(255, 0, 110, 0.04);
  box-shadow: 0 0 0 1px rgba(255, 0, 110, 0.2), 0 0 15px rgba(255, 0, 110, 0.08);
}

.toggle-pwd {
  position: absolute;
  right: 12px;
  background: none;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
  transition: color 0.2s;
}

.toggle-pwd:hover { color: var(--neon-pink); }

/* Password strength */
.pwd-strength {
  display: flex;
  align-items: center;
  gap: 12px;
}

.strength-bar {
  flex: 1;
  height: 3px;
  background: var(--bg-void);
  border-radius: 2px;
  overflow: hidden;
}

.strength-fill {
  height: 100%;
  border-radius: 2px;
  transition: all 0.4s ease;
  box-shadow: 0 0 8px currentColor;
}

.strength-label {
  font-family: var(--font-mono);
  font-size: 0.6rem;
  letter-spacing: 0.15em;
  width: 40px;
}

/* Error */
.error-box {
  background: rgba(255, 0, 110, 0.08);
  border: 1px solid rgba(255, 0, 110, 0.3);
  border-radius: 4px;
  padding: 10px 16px;
  font-family: var(--font-mono);
  font-size: 0.75rem;
  color: var(--neon-pink);
  display: flex;
  align-items: center;
  gap: 8px;
}

/* Submit */
.btn-submit {
  width: 100%;
  padding: 14px;
  background: transparent;
  border: 1px solid var(--neon-cyan);
  color: var(--neon-cyan);
  font-family: var(--font-display);
  font-size: 0.75rem;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  clip-path: polygon(12px 0%, 100% 0%, calc(100% - 12px) 100%, 0% 100%);
}

.btn-submit::before {
  content: '';
  position: absolute;
  inset: 0;
  background: var(--neon-cyan);
  transform: translateX(-101%);
  transition: transform 0.3s ease;
}

.btn-submit.pink {
  border-color: var(--neon-pink);
  color: var(--neon-pink);
}

.btn-submit.pink::before { background: var(--neon-pink); }

.btn-submit:not(:disabled):hover { color: var(--bg-void); }
.btn-submit:not(:disabled):hover::before { transform: translateX(0); }
.btn-submit span { position: relative; z-index: 1; }
.btn-submit:disabled { opacity: 0.6; cursor: not-allowed; }

.loading-dots span {
  animation: blink 1.4s infinite;
  font-family: var(--font-mono);
}
.loading-dots span:nth-child(2) { animation-delay: 0.2s; }
.loading-dots span:nth-child(3) { animation-delay: 0.4s; }

@keyframes blink {
  0%, 80%, 100% { opacity: 0; }
  40% { opacity: 1; }
}

.auth-footer {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin-top: 2rem;
  padding-top: 1.5rem;
  border-top: 1px solid rgba(255, 0, 110, 0.1);
}

.footer-text {
  font-family: var(--font-mono);
  font-size: 0.75rem;
  color: var(--text-muted);
}

.footer-link {
  font-family: var(--font-mono);
  font-size: 0.75rem;
  color: var(--neon-pink);
  text-decoration: none;
  transition: all 0.2s;
}

.footer-link:hover { text-shadow: 0 0 8px var(--neon-pink); }

.fade-enter-active, .fade-leave-active { transition: all 0.3s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(-6px); }

@media (max-width: 500px) {
  .form-grid { grid-template-columns: 1fr; }
}
</style>