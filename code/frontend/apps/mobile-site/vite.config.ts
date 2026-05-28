import react from '@vitejs/plugin-react';
import { defineConfig } from 'vite';
import { fileURLToPath, URL } from 'node:url';

export default defineConfig({
  root: fileURLToPath(new URL('.', import.meta.url)),
  plugins: [react()],
  build: {
    outDir: '../../dist/mobile-site',
    emptyOutDir: true
  },
  server: {
    port: 5174,
    proxy: {
      '/api': {
        target: 'http://localhost:18080',
        changeOrigin: true
      },
      '/auth': {
        target: 'http://localhost:18080',
        changeOrigin: true
      }
    }
  },
  resolve: {
    alias: {
      '@psm/api-client': fileURLToPath(new URL('../../packages/api-client/src', import.meta.url)),
      '@psm/domain-types': fileURLToPath(new URL('../../packages/domain-types/src', import.meta.url)),
      '@psm/auth': fileURLToPath(new URL('../../packages/auth/src', import.meta.url))
    }
  }
});
