import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import type { User } from '../types/user.types'

interface AuthStore {
  user: User | null
  token: string | null
  setAuth: (user: User, token: string) => void
  updateUser: (user: Partial<User>) => void
  logout: () => void
  isAuthenticated: () => boolean
  hasRole: (role: User['role'] | User['role'][]) => boolean
}

export const useAuthStore = create<AuthStore>()(
  persist(
    (set, get) => ({
      user: null,
      token: null,

      setAuth: (user, token) => set({ user, token }),

      updateUser: (partial) =>
        set((s) => ({ user: s.user ? { ...s.user, ...partial } : null })),

      logout: () => set({ user: null, token: null }),

      isAuthenticated: () => !!get().token,

      hasRole: (role) => {
        const userRole = get().user?.role
        if (!userRole) return false
        return Array.isArray(role) ? role.includes(userRole) : userRole === role
      },
    }),
    { name: 'brico-auth' }
  )
)