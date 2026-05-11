import axios from 'axios'
import { useAuthStore } from '../store/authStore'




export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' },
  timeout: 15_000,
})



// Mock je isključen — koristi se pravi backend na localhost:8080
// import { initMockHandlers } from '../db/handlers.ts'
// initMockHandlers(api)


// Attach JWT on every request
api.interceptors.request.use((config) => {
  const token = useAuthStore.getState().token
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// Handle 401 — redirect samo ako je korisnik bio prijavljen
api.interceptors.response.use(
  (res) => res,
  (error) => {
    if (error.response?.status === 401) {
      const wasLoggedIn = !!useAuthStore.getState().token
      useAuthStore.getState().logout()
      // Redirect samo ako je korisnik bio prijavljen — izbjegava flash pri logout-u
      if (wasLoggedIn) {
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)