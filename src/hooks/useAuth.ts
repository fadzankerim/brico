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
    idBroj: string
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
    onError: (error: any) => {
      const status = error?.response?.status
      if (status === 403) {
        toast.error('Nalog je deaktiviran.')
      } else {
        toast.error('Pogrešan email ili lozinka.')
      }
    },
  })
}

export function useRegister() {
  const { setAuth, setSalonId } = useAuthStore()
  const navigate = useNavigate()

  return useMutation({
    mutationFn: async (data: OwnerRegisterData) => {
      const { salonData, confirmPassword: _, ...userFields } = data as any
      if (userFields.phone === '') userFields.phone = undefined
      const res = await authService.register(userFields)

      if (res.user.role === 'SALON_OWNER' && salonData) {
        // Spremi token odmah da bi salonService.create mogao koristiti Authorization header
        useAuthStore.getState().setAuth(res.user, res.accessToken)
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
    onError: (error: any) => {
      const msg = error?.response?.data?.message
      const errType = error?.response?.data?.error
      if (errType === 'conflict') {
        toast.error('Korisnik sa ovim emailom već postoji.')
      } else if (errType === 'validation') {
        toast.error('Provjerite unesene podatke.')
      } else {
        toast.error(msg ?? 'Greška pri registraciji.')
      }
    },
  })
}

export function useLogout() {
  const { logout } = useAuthStore()
  const navigate = useNavigate()
  const queryClient = useQueryClient()

  return () => {
    queryClient.clear()   // otkaži sve pending upite PRIJE logout-a
    authService.logout()
    logout()
    navigate('/', { replace: true })
    toast.success('Uspješno ste se odjavili')
  }
}