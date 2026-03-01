<template>
  <div class="auth-page">
    <div class="auth-bg">
      <div class="auth-orb"></div>
    </div>

    <div class="auth-container">
      <!-- Terminal header -->
      <div class="terminal-header">
        <span class="terminal-dot red"></span>
        <span class="terminal-dot yellow"></span>
        <span class="terminal-dot green"></span>
        <span class="terminal-title">AUTH_SYSTEM :: LOGIN</span>
      </div>

      <div class="auth-card">
        <div class="auth-logo">
          <span class="logo-symbol">◈</span>
          <h1 class="auth-title">CONNEXION</h1>
          <p class="auth-subtitle">Accédez à votre espace VOD</p>
        </div>

        <form class="auth-form" @submit.prevent="handleLogin">
          <!-- Pseudo -->
          <div class="field-group">
            <label class="field-label">
              <span class="label-prefix">&gt;</span> IDENTIFIANT
            </label>
            <div class="input-wrapper">
              <span class="input-icon">◈</span>
              <input
                v-model="form.pseudo"
                type="text"
                class="cyber-input"
                placeholder="votre_pseudo"
                required
                autocomplete="username"
              />
            </div>
          </div>

          <!-- Password -->
          <div class="field-group">
            <label class="field-label">
              <span class="label-prefix">&gt;</span> MOT DE PASSE
            </label>
            <div class="input-wrapper">
              <span class="input-icon">◉</span>
              <input
                v-model="form.motDePasse"
                :type="showPwd ? 'text' : 'password'"
                class="cyber-input"
                placeholder="••••••••"
                required
                autocomplete="current-password"
              />
              <button type="button" class="toggle-pwd" @click="showPwd = !showPwd">
                {{ showPwd ? '◎' : '◉' }}
              </button>
            </div>
          </div>

          <!-- Error -->
          <Transition name="fade">
            <div v-if="error" class="error-box">
              <span class="error-icon">⚠</span> {{ error }}
            </div>
          </Transition>

          <!-- Submit -->
          <button type="submit" class="btn-submit" :class="{ loading }" :disabled="loading">
            <span v-if="!loading">INITIALISER SESSION</span>
            <span v-else class="loading-dots">CONNEXION<span>.</span><span>.</span><span>.</span></span>
          </button>
        </form>

        <div class="auth-footer">
          <span class="footer-text">Pas encore de compte ?</span>
          <RouterLink to="/register" class="footer-link">S'inscrire →</RouterLink>
        </div>
      </div>

      <!-- Decorative lines -->
      <div class="corner top-left"></div>
      <div class="corner top-right"></div>
      <div class="corner bottom-left"></div>
      <div class="corner bottom-right"></div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()
const form = ref({ pseudo: '', motDePasse: '' })
const error = ref('')
const loading = ref(false)
const showPwd = ref(false)

const handleLogin = async () => {
  error.value = ''
  loading.value = true
  try {
    // ICI : on utilise motDePasse au lieu de password
    await auth.login(form.value.pseudo, form.value.motDePasse)
    router.push('/')
  } catch (e) {
    error.value = e.response?.data?.message || 'Identifiants incorrects. Réessayez.'
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
  background: radial-gradient(circle, rgba(0, 245, 255, 0.08), transparent 70%);
  top: 50%; left: 50%;
  transform: translate(-50%, -50%);
  border-radius: 50%;
  filter: blur(40px);
}

.auth-container {
  position: relative;
  width: 100%;
  max-width: 440px;
}

/* Terminal header */
.terminal-header {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(0, 245, 255, 0.05);
  border: 1px solid var(--border-subtle);
  border-bottom: none;
  padding: 10px 16px;
  border-radius: 8px 8px 0 0;
}

.terminal-dot {
  width: 10px; height: 10px;
  border-radius: 50%;
}
.terminal-dot.red { background: #ff5f57; box-shadow: 0 0 6px #ff5f57; }
.terminal-dot.yellow { background: #ffbd2e; box-shadow: 0 0 6px #ffbd2e; }
.terminal-dot.green { background: #28ca41; box-shadow: 0 0 6px #28ca41; }

.terminal-title {
  font-family: var(--font-mono);
  font-size: 0.65rem;
  letter-spacing: 0.15em;
  color: var(--text-muted);
  margin-left: 8px;
}

/* Card */
.auth-card {
  background: var(--bg-card);
  border: 1px solid var(--border-glow);
  border-radius: 0 0 8px 8px;
  padding: 2.5rem;
  box-shadow: var(--shadow-neon), var(--shadow-card);
  position: relative;
}

.auth-logo {
  text-align: center;
  margin-bottom: 2.5rem;
}

.logo-symbol {
  font-size: 2rem;
  color: var(--neon-cyan);
  text-shadow: 0 0 20px var(--neon-cyan);
  display: block;
  margin-bottom: 1rem;
  animation: pulse-neon 2s ease-in-out infinite;
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

/* Form */
.auth-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.field-label {
  font-family: var(--font-mono);
  font-size: 0.65rem;
  letter-spacing: 0.2em;
  color: var(--neon-cyan);
  text-transform: uppercase;
}

.label-prefix {
  opacity: 0.6;
  margin-right: 4px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 14px;
  color: var(--neon-cyan);
  opacity: 0.5;
  font-size: 0.8rem;
  pointer-events: none;
}

.cyber-input {
  width: 100%;
  padding: 12px 14px 12px 38px;
  background: rgba(0, 245, 255, 0.03);
  border: 1px solid var(--border-subtle);
  border-radius: 4px;
  color: var(--text-primary);
  font-family: var(--font-mono);
  font-size: 0.9rem;
  letter-spacing: 0.05em;
  transition: all 0.3s ease;
  outline: none;
}

.cyber-input::placeholder { color: var(--text-muted); }

.cyber-input:focus {
  border-color: var(--neon-cyan);
  background: rgba(0, 245, 255, 0.05);
  box-shadow: 0 0 0 1px rgba(0, 245, 255, 0.3), 0 0 20px rgba(0, 245, 255, 0.1);
}

.toggle-pwd {
  position: absolute;
  right: 14px;
  background: none;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
  font-size: 0.9rem;
  transition: color 0.2s;
}

.toggle-pwd:hover { color: var(--neon-cyan); }

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

.error-icon { font-size: 1rem; }

/* Submit button */
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
  margin-top: 0.5rem;
}

.btn-submit::before {
  content: '';
  position: absolute;
  inset: 0;
  background: var(--neon-cyan);
  transform: translateX(-101%);
  transition: transform 0.3s ease;
}

.btn-submit:not(:disabled):hover {
  color: var(--bg-void);
  box-shadow: var(--shadow-neon);
}

.btn-submit:not(:disabled):hover::before {
  transform: translateX(0);
}

.btn-submit span {
  position: relative;
  z-index: 1;
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* Loading dots */
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

/* Footer */
.auth-footer {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin-top: 2rem;
  padding-top: 1.5rem;
  border-top: 1px solid var(--border-subtle);
}

.footer-text {
  font-family: var(--font-mono);
  font-size: 0.75rem;
  color: var(--text-muted);
}

.footer-link {
  font-family: var(--font-mono);
  font-size: 0.75rem;
  color: var(--neon-cyan);
  text-decoration: none;
  transition: all 0.2s;
}

.footer-link:hover {
  text-shadow: 0 0 8px var(--neon-cyan);
}

/* Corner decorations */
.corner {
  position: absolute;
  width: 16px; height: 16px;
  border-color: var(--neon-cyan);
  border-style: solid;
  opacity: 0.4;
}
.top-left { top: 36px; left: -6px; border-width: 2px 0 0 2px; }
.top-right { top: 36px; right: -6px; border-width: 2px 2px 0 0; }
.bottom-left { bottom: -6px; left: -6px; border-width: 0 0 2px 2px; }
.bottom-right { bottom: -6px; right: -6px; border-width: 0 2px 2px 0; }

.fade-enter-active, .fade-leave-active { transition: all 0.3s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(-6px); }
</style>