import { api } from './api'
import type { AuthResponse, LoginRequest, RegisterRequest, User } from '../types/user.types'

export const authService = {
  login: (data: LoginRequest) =>
    api.post<AuthResponse>('/auth/login', data).then((r) => r.data),

  register: (data: RegisterRequest) =>
    api.post<AuthResponse>('/auth/register', data).then((r) => r.data),

  logout: () =>
    api.post('/auth/logout').catch(() => {}), 

  me: () =>
    api.get<User>('/auth/me').then((r) => r.data),

  updateProfile: (data: Partial<User>) =>
    api.put<User>('/auth/me', data).then((r) => r.data),
}