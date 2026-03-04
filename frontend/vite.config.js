import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

const FAC = 'http://info-tpsi.univ-brest.fr:11080'

export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/api': {
        target: FAC,
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')  // /api/comptes → /comptes
      }
    }
  }
})