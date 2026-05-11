import { describe, it, expect, beforeEach } from 'vitest'
import { useAuthStore } from '../store/authStore'
import type { User } from '../types/user.types'

const mockUser: User = {
  id: 1,
  email: 'test@example.com',
  role: 'CLIENT',
  fullName: 'Alen Begić',
  phone: undefined,
  createdAt: '2024-01-01T00:00:00',
}

describe('authStore', () => {
  beforeEach(() => {
    useAuthStore.getState().logout()
  })

  it('starts unauthenticated', () => {
    const state = useAuthStore.getState()
    expect(state.token).toBeNull()
    expect(state.user).toBeNull()
    expect(state.isAuthenticated()).toBe(false)
  })

  it('sets user and token on setAuth', () => {
    useAuthStore.getState().setAuth(mockUser, 'token123')
    const state = useAuthStore.getState()
    expect(state.token).toBe('token123')
    expect(state.user).toEqual(mockUser)
    expect(state.isAuthenticated()).toBe(true)
  })

  it('clears state on logout', () => {
    useAuthStore.getState().setAuth(mockUser, 'token123')
    useAuthStore.getState().logout()
    const state = useAuthStore.getState()
    expect(state.token).toBeNull()
    expect(state.user).toBeNull()
    expect(state.isAuthenticated()).toBe(false)
  })

  it('isAuthenticated depends only on token', () => {
    useAuthStore.setState({ token: null, user: mockUser, salonId: null })
    expect(useAuthStore.getState().isAuthenticated()).toBe(false)
    useAuthStore.setState({ token: 'tok', user: null, salonId: null })
    expect(useAuthStore.getState().isAuthenticated()).toBe(true)
  })

  it('hasRole returns true for matching role', () => {
    useAuthStore.getState().setAuth(mockUser, 'tok')
    expect(useAuthStore.getState().hasRole('CLIENT')).toBe(true)
    expect(useAuthStore.getState().hasRole('SALON_OWNER')).toBe(false)
  })

  it('hasRole accepts array of roles', () => {
    useAuthStore.getState().setAuth(mockUser, 'tok')
    expect(useAuthStore.getState().hasRole(['CLIENT', 'SALON_OWNER'])).toBe(true)
    expect(useAuthStore.getState().hasRole(['SALON_OWNER', 'ADMIN'])).toBe(false)
  })

  it('updateUser merges partial updates', () => {
    useAuthStore.getState().setAuth(mockUser, 'tok')
    useAuthStore.getState().updateUser({ fullName: 'Mirza Begić' })
    expect(useAuthStore.getState().user?.fullName).toBe('Mirza Begić')
    expect(useAuthStore.getState().user?.email).toBe('test@example.com')
  })

  it('setSalonId stores salon id', () => {
    useAuthStore.getState().setSalonId(42)
    expect(useAuthStore.getState().salonId).toBe(42)
  })
})
