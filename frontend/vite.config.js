import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

const FAC_GATEWAY = 'http://info-tpsi.univ-brest.fr:11080'
const FAC_FILMS   = 'http://info-tpsi.univ-brest.fr:11041'

export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/api/movies': { target: FAC_FILMS,    changeOrigin: true, rewrite: p => p.replace(/^\/api/, '') },
      '/api':        { target: FAC_GATEWAY,  changeOrigin: true, rewrite: p => p.replace(/^\/api/, '') },
    }
  }
})