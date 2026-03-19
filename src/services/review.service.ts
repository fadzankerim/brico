import { api } from './api'
import type { Review, CreateReviewRequest, Favorite } from '../types/review.types'

export const reviewService = {
  getSalonReviews: (salonId: number) =>
    api.get<Review[]>(`/salons/${salonId}/reviews`).then((r) => r.data),

  create: (data: CreateReviewRequest) =>
    api.post<Review>('/reviews', data).then((r) => r.data),

  delete: (id: number) =>
    api.delete(`/reviews/${id}`),
}

export const favoritesService = {
  getAll: () =>
    api.get<Favorite[]>('/favorites').then((r) => r.data),

  add: (salonId: number) =>
    api.post<Favorite>('/favorites', { salonId }).then((r) => r.data),

  remove: (salonId: number) =>
    api.delete(`/favorites/${salonId}`),
}