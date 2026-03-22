import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'


import { initMockHandlers } from './db/handlers.ts'
import { api } from './services/api.ts'
initMockHandlers(api)

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
