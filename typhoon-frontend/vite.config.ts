import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import cesium from 'vite-plugin-cesium'
import path from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue(), cesium()],
  resolve: {
    alias: {
      cesium: path.resolve(__dirname, 'node_modules/cesium'),
    },
  },
  build: {
    target: 'esnext',
  },
  server: {
    proxy: {
      '/user': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/typhoon': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      }
    }
  }
})
