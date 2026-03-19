import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { toast } from 'sonner'
import { authService } from '../services/auth.service'
import { useAuthStore } from '../store/authStore'
import type { LoginRequest, RegisterRequest } from '../types/user.types'

export function useLogin() {
  const { setAuth } = useAuthStore()
  const navigate = useNavigate()

  return useMutation({
    mutationFn: (data: LoginRequest) => authService.login(data),
    onSuccess: (res) => {
      setAuth(res.user, res.accessToken)
      toast.success(`Dobrodošli, ${res.user.fullName}!`)

      // Role-based redirect
      switch (res.user.role) {
        case 'SALON_OWNER':  navigate('/owner/dashboard'); break
        case 'HAIRDRESSER':  navigate('/hairdresser/dashboard'); break
        default:             navigate('/')
      }
    },
    onError: () => {
      toast.error('Pogrešan email ili lozinka')
    },
  })
}

export function useRegister() {
  const { setAuth } = useAuthStore()
  const navigate = useNavigate()

  return useMutation({
    mutationFn: (data: RegisterRequest) => authService.register(data),
    onSuccess: (res) => {
      setAuth(res.user, res.accessToken)
      toast.success('Račun uspješno kreiran!')
      navigate('/')
    },
    onError: () => {
      toast.error('Greška pri registraciji. Provjerite podatke.')
    },
  })
}

export function useLogout() {
  const { logout } = useAuthStore()
  const navigate = useNavigate()
  const queryClient = useQueryClient()

  return () => {
    authService.logout()
    logout()
    queryClient.clear()
    navigate('/')
    toast.success('Uspješno ste se odjavili')
  }
}