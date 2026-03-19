import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import { salonService } from '../services/salon.service'
import { useAuthStore } from '../store/authStore'
import type { SalonSearchParams } from '../types/salon.typs'
import { favoritesService } from '../services/review.service'

export const salonKeys = {
  all: ['salons'] as const,
  search: (params: SalonSearchParams) => ['salons', 'search', params] as const,
  detail: (id: number | string) => ['salons', 'detail', id] as const,
  favorites: () => ['salons', 'favorites'] as const,
}

export function useSalonSearch(params: SalonSearchParams) {
  return useQuery({
    queryKey: salonKeys.search(params),
    queryFn: () => salonService.search(params),
    staleTime: 1000 * 60 * 3, // 3 min
  })
}

export function useSalon(slug: string) {
  return useQuery({
    queryKey: salonKeys.detail(slug),
    queryFn: () => salonService.getBySlug(slug),
    staleTime: 1000 * 60 * 5,
    enabled: !!slug,
  })
}

export function useFavorites() {
  const { isAuthenticated } = useAuthStore()
  return useQuery({
    queryKey: salonKeys.favorites(),
    queryFn: () => favoritesService.getAll(),
    enabled: isAuthenticated(),
    staleTime: 1000 * 60 * 5,
  })
}

export function useToggleFavorite() {
  const queryClient = useQueryClient()
  const { isAuthenticated } = useAuthStore()

  return useMutation({
    mutationFn: async ({ salonId, isFavorited }: { salonId: number; isFavorited: boolean }) => {
      if (!isAuthenticated()) throw new Error('AUTH_REQUIRED')
      return isFavorited
        ? favoritesService.remove(salonId)
        : favoritesService.add(salonId)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: salonKeys.favorites() })
    },
    onError: (error: Error) => {
      if (error.message === 'AUTH_REQUIRED') {
        toast.error('Prijavite se da biste dodali u favourite')
      }
    },
  })
}