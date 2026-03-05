<template>
  <div class="app-shell">
    <NavBar />
    <main class="main-content">
      <RouterView v-slot="{ Component }">
        <Transition name="page" mode="out-in">
          <component :is="Component" />
        </Transition>
      </RouterView>
    </main>
  </div>
</template>

<script setup>
import NavBar from './components/NavBar.vue'
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Orbitron:wght@400;600;700;900&family=Rajdhani:wght@300;400;500;600;700&family=Share+Tech+Mono&display=swap');

:root {
  --bg-void: #020408;
  --bg-dark: #060d14;
  --bg-card: #0a1520;
  --bg-card-hover: #0f1e2e;
  --neon-cyan: #00f5ff;
  --neon-pink: #ff006e;
  --neon-purple: #9d00ff;
  --neon-green: #00ff88;
  --neon-orange: #ff6b00;
  --text-primary: #e8f4f8;
  --text-secondary: #7ba3b8;
  --text-muted: #3a5568;
  --border-glow: rgba(0, 245, 255, 0.2);
  --border-subtle: rgba(0, 245, 255, 0.08);
  --font-display: 'Orbitron', monospace;
  --font-body: 'Rajdhani', sans-serif;
  --font-mono: 'Share Tech Mono', monospace;
  --shadow-neon: 0 0 20px rgba(0, 245, 255, 0.3), 0 0 60px rgba(0, 245, 255, 0.1);
  --shadow-pink: 0 0 20px rgba(255, 0, 110, 0.3), 0 0 60px rgba(255, 0, 110, 0.1);
  --shadow-card: 0 8px 32px rgba(0, 0, 0, 0.6);
}

*, *::before, *::after {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

html {
  scroll-behavior: smooth;
}

body {
  background-color: var(--bg-void);
  color: var(--text-primary);
  font-family: var(--font-body);
  font-size: 16px;
  line-height: 1.6;
  overflow-x: hidden;
}

/* Scanline overlay */
body::before {
  content: '';
  position: fixed;
  inset: 0;
  background: repeating-linear-gradient(
    0deg,
    transparent,
    transparent 2px,
    rgba(0, 245, 255, 0.015) 2px,
    rgba(0, 245, 255, 0.015) 4px
  );
  pointer-events: none;
  z-index: 9999;
}

/* Grid background */
body::after {
  content: '';
  position: fixed;
  inset: 0;
  background-image:
    linear-gradient(rgba(0, 245, 255, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 245, 255, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
  pointer-events: none;
  z-index: 0;
}

.app-shell {
  min-height: 100vh;
  position: relative;
  z-index: 1;
}

.main-content {
  padding-top: 70px;
  min-height: calc(100vh - 70px);
}

/* Page transitions */
.page-enter-active,
.page-leave-active {
  transition: all 0.3s ease;
}
.page-enter-from {
  opacity: 0;
  transform: translateY(10px);
  filter: blur(4px);
}
.page-leave-to {
  opacity: 0;
  transform: translateY(-10px);
  filter: blur(4px);
}

/* Scrollbar */
::-webkit-scrollbar { width: 6px; }
::-webkit-scrollbar-track { background: var(--bg-void); }
::-webkit-scrollbar-thumb {
  background: var(--neon-cyan);
  border-radius: 3px;
  box-shadow: 0 0 8px var(--neon-cyan);
}

/* PrimeVue overrides */
.p-button {
  font-family: var(--font-display) !important;
  font-size: 0.75rem !important;
  letter-spacing: 0.1em !important;
  text-transform: uppercase !important;
}

.p-inputtext {
  background: var(--bg-card) !important;
  border: 1px solid var(--border-glow) !important;
  color: var(--text-primary) !important;
  font-family: var(--font-body) !important;
  border-radius: 4px !important;
}

.p-inputtext:focus {
  border-color: var(--neon-cyan) !important;
  box-shadow: 0 0 0 1px var(--neon-cyan), var(--shadow-neon) !important;
}

/* Global utility classes */
.neon-text {
  color: var(--neon-cyan);
  text-shadow: 0 0 10px var(--neon-cyan), 0 0 30px rgba(0, 245, 255, 0.5);
}

.neon-text-pink {
  color: var(--neon-pink);
  text-shadow: 0 0 10px var(--neon-pink), 0 0 30px rgba(255, 0, 110, 0.5);
}

.glitch-text {
  position: relative;
  animation: glitch 4s infinite;
}

@keyframes glitch {
  0%, 90%, 100% { text-shadow: none; transform: none; }
  91% { text-shadow: 2px 0 var(--neon-pink), -2px 0 var(--neon-cyan); transform: skewX(1deg); }
  92% { text-shadow: -2px 0 var(--neon-pink), 2px 0 var(--neon-cyan); transform: skewX(-1deg); }
  93% { text-shadow: none; transform: none; }
}

.cyber-card {
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: 8px;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.cyber-card::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--neon-cyan), transparent);
  opacity: 0;
  transition: opacity 0.3s;
}

.cyber-card:hover {
  border-color: var(--border-glow);
  background: var(--bg-card-hover);
  box-shadow: var(--shadow-card), 0 0 20px rgba(0, 245, 255, 0.05);
  transform: translateY(-2px);
}

.cyber-card:hover::before {
  opacity: 1;
}

.btn-cyber {
  background: transparent;
  border: 1px solid var(--neon-cyan);
  color: var(--neon-cyan);
  font-family: var(--font-display);
  font-size: 0.7rem;
  letter-spacing: 0.15em;
  text-transform: uppercase;
  padding: 10px 24px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  clip-path: polygon(8px 0%, 100% 0%, calc(100% - 8px) 100%, 0% 100%);
}

.btn-cyber::before {
  content: '';
  position: absolute;
  inset: 0;
  background: var(--neon-cyan);
  transform: translateX(-101%);
  transition: transform 0.3s ease;
}

.btn-cyber:hover {
  color: var(--bg-void);
  box-shadow: var(--shadow-neon);
}

.btn-cyber:hover::before {
  transform: translateX(0);
}

.btn-cyber span {
  position: relative;
  z-index: 1;
}

.btn-cyber-pink {
  border-color: var(--neon-pink);
  color: var(--neon-pink);
}

.btn-cyber-pink::before {
  background: var(--neon-pink);
}

.btn-cyber-pink:hover {
  box-shadow: var(--shadow-pink);
}

.section-title {
  font-family: var(--font-display);
  font-size: 0.7rem;
  letter-spacing: 0.3em;
  text-transform: uppercase;
  color: var(--neon-cyan);
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 2rem;
}

.section-title::after {
  content: '';
  flex: 1;
  height: 1px;
  background: linear-gradient(90deg, var(--neon-cyan), transparent);
  opacity: 0.4;
}

.tag {
  font-family: var(--font-mono);
  font-size: 0.7rem;
  padding: 2px 10px;
  border-radius: 2px;
  border: 1px solid;
  letter-spacing: 0.05em;
}

.tag-cyan {
  border-color: rgba(0, 245, 255, 0.4);
  color: var(--neon-cyan);
  background: rgba(0, 245, 255, 0.05);
}

.tag-pink {
  border-color: rgba(255, 0, 110, 0.4);
  color: var(--neon-pink);
  background: rgba(255, 0, 110, 0.05);
}

.tag-green {
  border-color: rgba(0, 255, 136, 0.4);
  color: var(--neon-green);
  background: rgba(0, 255, 136, 0.05);
}

.tag-purple {
  border-color: rgba(157, 0, 255, 0.4);
  color: var(--neon-purple);
  background: rgba(157, 0, 255, 0.05);
}

/* Pulse animation */
@keyframes pulse-neon {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

@keyframes spin-slow {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes flicker {
  0%, 95%, 100% { opacity: 1; }
  96% { opacity: 0.8; }
  97% { opacity: 1; }
  98% { opacity: 0.6; }
  99% { opacity: 1; }
}
</style>