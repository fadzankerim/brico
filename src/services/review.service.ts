import { api } from './api'
import type { Review, CreateReviewRequest, Favorite } from '../types/review.types'

export const reviewService = {
  getSalonReviews: (salonId: number) =>
    api.get<Review[]>(`/salons/${salonId}/reviews`).then((r) => r.data),

  getClientReviews: (clientId: number) =>
    api.get<Review[]>(`/clients/${clientId}/reviews`).then((r) => r.data),

  create: (data: CreateReviewRequest) =>
    api.post<Review>('/reviews', data).then((r) => r.data),

  delete: (id: number) =>
    api.delete(`/reviews/${id}`),
}

export const favoritesService = {
  getAll: () =>
    api.get<Favorite[]>('/favorites').then((r) => r.data),

  add: (salon: { id: number; name: string; slug: string; city: string; avgRating?: number; verified?: boolean }, userId: number) =>
    api.post<Favorite>('/favorites', {
      userId,
      salonId:        salon.id,
      salonName:      salon.name,
      salonSlug:      salon.slug,
      salonCity:      salon.city,
      salonAvgRating: salon.avgRating ?? 0,
      salonVerified:  salon.verified ?? false,
    }).then((r) => r.data),

  remove: (salonId: number) =>
    api.delete(`/favorites/${salonId}`),
}