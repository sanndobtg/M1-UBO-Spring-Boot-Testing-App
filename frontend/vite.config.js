import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

const GATEWAY = 'http://info-tpsi.univ-brest.fr:11080'

export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/comptes': { target: GATEWAY, changeOrigin: true },
      '/reservations': { target: GATEWAY, changeOrigin: true },
      '/reviews': { target: GATEWAY, changeOrigin: true },
      '/posters': { target: GATEWAY, changeOrigin: true },
      '/movies': { target: 'http://info-tpsi.univ-brest.fr:11041', changeOrigin: true },
    }
  }
})