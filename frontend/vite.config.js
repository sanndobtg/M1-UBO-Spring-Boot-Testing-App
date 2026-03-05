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
      },
      '/comptes': {
        target: 'http://info-tpsi:11080',
        changeOrigin: true,
      },
      '/reservations': {
        target: 'http://info-tpsi:11080',
        changeOrigin: true,
      },
      '/reviews': {
        target: 'http://info-tpsi:11080',
        changeOrigin: true,
      },
      '/movies': {
        target: 'http://info-tpsi:11040',
        changeOrigin: true,
      },
      '/posters': {
        target: 'http://info-tpsi:11080',
        changeOrigin: true,
      },
    }
  }
})