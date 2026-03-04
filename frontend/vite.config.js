import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
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