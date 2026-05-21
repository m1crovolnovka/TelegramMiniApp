import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/vite';

export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: {
    port: 5174,
    proxy: {
      '/api': {
        target: 'https://p01--telegramminiapp--jrk5phf8xmp4.code.run',
        changeOrigin: true,
      },
    },
  },
});
