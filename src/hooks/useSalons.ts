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
  mine: (ownerId: number | undefined) => ['salons', 'mine', ownerId] as const,
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

export function useSalonById(id: number | string | undefined) {
  const numId = Number(id)
  return useQuery({
    queryKey: salonKeys.detail(numId),
    queryFn:  () => salonService.getById(numId),
    staleTime: 1000 * 60 * 5,
    enabled:  !!id && !isNaN(numId),
  })
}

/** Returns the first salon owned by the currently logged-in SALON_OWNER. */
export function useMySalon() {
  const { user } = useAuthStore()
  return useQuery({
    queryKey: salonKeys.mine(user?.id),
    queryFn:  () => salonService.getByOwner(user!.id).then(salons => salons[0] ?? null),
    enabled:  !!user && user.role === 'SALON_OWNER',
    staleTime: 1000 * 60 * 5,
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
  const { isAuthenticated, user } = useAuthStore()

  return useMutation({
    mutationFn: async ({ salon, isFavorited }: {
      salon: { id: number; name: string; slug: string; city: string; avgRating?: number; verified?: boolean };
      isFavorited: boolean
    }) => {
      if (!isAuthenticated() || !user) throw new Error('AUTH_REQUIRED')
      return isFavorited
        ? favoritesService.remove(salon.id)
        : favoritesService.add(salon, user.id)
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