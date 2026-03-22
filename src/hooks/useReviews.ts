import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'

import { salonKeys } from './useSalons'
import { reviewService } from '../services/review.service'
import type { CreateReviewRequest } from '../types/review.types'
 
// ─── Query key factory ────────────────────────────────────────────────────────
export const reviewKeys = {
  all:     ['reviews'] as const,
  bySalon: (salonId: number) => ['reviews', 'salon', salonId] as const,
}
 
// ─── Salon reviews (SalonProfilePage reviews tab) ────────────────────────────
export function useSalonReviews(salonId: number | undefined) {
  return useQuery({
    queryKey: reviewKeys.bySalon(salonId!),
    queryFn:  () => reviewService.getSalonReviews(salonId!),
    enabled:  !!salonId,
    staleTime: 1000 * 60 * 5,
  })
}
 
// ─── Create review (ReviewModal) ──────────────────────────────────────────────
// Nakon uspješne recenzije invalidira: reviews lista + salon profil (avgRating se mijenja)
export function useCreateReview() {
  const queryClient = useQueryClient()
 
  return useMutation({
    mutationFn: (data: CreateReviewRequest) => reviewService.create(data),
    onSuccess: (_, vars) => {
      queryClient.invalidateQueries({ queryKey: reviewKeys.bySalon(vars.salonId) })
      queryClient.invalidateQueries({ queryKey: salonKeys.all }) // Refresh avgRating
      toast.success('Recenzija uspješno poslana!')
    },
    onError: () => {
      toast.error('Greška pri slanju recenzije.')
    },
  })
}
 
// ─── Delete review (Admin / owner moderation) ────────────────────────────────
export function useDeleteReview(salonId?: number) {
  const queryClient = useQueryClient()
 
  return useMutation({
    mutationFn: (id: number) => reviewService.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: reviewKeys.all })
      if (salonId) {
        queryClient.invalidateQueries({ queryKey: reviewKeys.bySalon(salonId) })
      }
      toast.success('Recenzija obrisana.')
    },
    onError: () => {
      toast.error('Greška pri brisanju recenzije.')
    },
  })
}