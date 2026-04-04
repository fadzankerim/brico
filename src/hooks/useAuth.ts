import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { toast } from 'sonner'
import { authService } from '../services/auth.service'
import { salonService } from '../services/salon.service'
import { useAuthStore } from '../store/authStore'
import type { LoginRequest, RegisterRequest } from '../types/user.types'

export interface OwnerRegisterData extends RegisterRequest {
  salonData?: {
    name: string
    city: string
    address: string
    phone?: string
    description?: string
    website?: string
  }
}

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
        case 'ADMIN':        navigate('/admin/dashboard'); break
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
  const { setAuth, setSalonId } = useAuthStore()
  const navigate = useNavigate()

  return useMutation({
    mutationFn: async (data: OwnerRegisterData) => {
      const { salonData, ...userFields } = data
      const res = await authService.register(userFields)

      if (res.user.role === 'SALON_OWNER' && salonData) {
        const salon = await salonService.create({ ...salonData, ownerId: res.user.id })
        return { ...res, createdSalonId: salon.id }
      }
      return { ...res, createdSalonId: null }
    },
    onSuccess: (res) => {
      setAuth(res.user, res.accessToken)
      if (res.createdSalonId) setSalonId(res.createdSalonId)
      toast.success('Račun uspješno kreiran!')

      switch (res.user.role) {
        case 'ADMIN':       navigate('/admin/dashboard'); break
        case 'SALON_OWNER': navigate('/owner/dashboard'); break
        case 'HAIRDRESSER': navigate('/hairdresser/dashboard'); break
        default:            navigate('/')
      }
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