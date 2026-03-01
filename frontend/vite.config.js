import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/comptes': {
        target: 'http://localhost:11080',
        changeOrigin: true,
      },
      '/reservations': {
        target: 'http://localhost:11080',
        changeOrigin: true,
      },
      '/reviews': {
        target: 'http://localhost:11080',
        changeOrigin: true,
      },
      '/films': {
        target: 'http://localhost:11080',
        changeOrigin: true,
      },
      '/posters': {
        target: 'http://localhost:11080',
        changeOrigin: true,
      },
    }
  }
})