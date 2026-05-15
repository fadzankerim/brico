import { api } from './api'
import type { SalonSearchParams, SalonSearchResponse, Salon, Hairdresser, Service, SalonPhoto } from '../types/salon.typs'

export interface PlanResponse {
  id: number
  planType: 'BASIC' | 'PRO'
  monthlyPrice: number
  maxHairdressers: number | null
}

export interface SubscriptionResponse {
  id: number
  salonId: number
  plan: PlanResponse
  status: 'ACTIVE' | 'INACTIVE' | 'TRIAL' | 'CANCELLED' | 'PAST_DUE'
  startDate: string
  endDate: string | null
  stripeSubscriptionId?: string
  createdAt: string
}

export interface HairdresserProfile {
  id:         number
  userId:     number
  salonId:    number
  fullName:   string
  bio?:       string
  specialties?: string
  profilePhoto?: string
  isActive:   boolean
  avgRating:  number
}

export const salonService = {
  search: (params: SalonSearchParams) =>
    api.get<SalonSearchResponse>('/salons/paged', { params }).then((r) => r.data),

  getById: (id: number) =>
    api.get<Salon>(`/salons/${id}`).then((r) => r.data),

  getBySlug: (slug: string) =>
    api.get<Salon>(`/salons/slug/${slug}`).then((r) => r.data),

  getByOwner: (ownerId: number) =>
    api.get<Salon[]>(`/salons/owner/${ownerId}`).then((r) => r.data),

  getMyHairdresserProfile: () =>
    api.get<HairdresserProfile>('/salons/hairdresser-profile').then((r) => r.data),

  create: (data: Partial<Salon> & { ownerId?: number }) =>
    api.post<Salon>('/salons', data).then((r) => r.data),

  update: (id: number, data: Partial<Salon>) =>
    api.put<Salon>(`/salons/${id}`, data).then((r) => r.data),

  delete: (id: number) =>
    api.delete(`/salons/${id}`),

  // Photos
  addPhoto: (salonId: number, url: string, isPrimary = false) =>
    api.post<SalonPhoto>(`/salons/${salonId}/photos`, { url, isPrimary }).then((r) => r.data),

  setPrimaryPhoto: (salonId: number, photoId: number) =>
    api.patch(`/salons/${salonId}/photos/${photoId}/primary`),

  deletePhoto: (salonId: number, photoId: number) =>
    api.delete(`/salons/${salonId}/photos/${photoId}`),

  // Hairdressers
  getHairdressers: (salonId: number) =>
    api.get<Hairdresser[]>(`/salons/${salonId}/hairdressers`).then((r) => r.data),

  addHairdresser: (salonId: number, data: Partial<Hairdresser>) =>
    api.post<Hairdresser>(`/salons/${salonId}/hairdressers`, data).then((r) => r.data),

  updateHairdresser: (salonId: number, hairdresserId: number, data: Partial<Hairdresser>) =>
    api.put<Hairdresser>(`/salons/${salonId}/hairdressers/${hairdresserId}`, data).then((r) => r.data),

  removeHairdresser: (salonId: number, hairdresserId: number) =>
    api.delete(`/salons/${salonId}/hairdressers/${hairdresserId}`),

  // Unavailability
  getUnavailability: (salonId: number, hairdresserId: number) =>
    api.get<any[]>(`/salons/${salonId}/hairdressers/${hairdresserId}/unavailability`).then(r => r.data),

  addUnavailability: (salonId: number, hairdresserId: number, data: { startTime: string; endTime: string; reason?: string }) =>
    api.post<any>(`/salons/${salonId}/hairdressers/${hairdresserId}/unavailability`, data).then(r => r.data),

  deleteUnavailability: (salonId: number, hairdresserId: number, id: number) =>
    api.delete(`/salons/${salonId}/hairdressers/${hairdresserId}/unavailability/${id}`),

  // Subscription
  getSalonLimits: (salonId: number) =>
    api.get<{ planType: string; maxHairdressers: number | null }>(`/subscriptions/salon/${salonId}/limits`).then(r => r.data),

  getSubscription: (salonId: number) =>
    api.get<SubscriptionResponse>(`/subscriptions/salon/${salonId}`).then(r => r.data),

  createSubscription: (salonId: number, planType: 'BASIC' | 'PRO') =>
    api.post<SubscriptionResponse>('/subscriptions', { salonId, planType }).then(r => r.data),

  changePlan: (salonId: number, planType: 'BASIC' | 'PRO') =>
    api.patch<SubscriptionResponse>(`/subscriptions/salon/${salonId}/plan`, null, { params: { planType } }).then(r => r.data),

  cancelSubscription: (salonId: number) =>
    api.patch<SubscriptionResponse>(`/subscriptions/salon/${salonId}/cancel`).then(r => r.data),

  // Working Hours
  getWorkingHours: (salonId: number) =>
    api.get<any[]>(`/salons/${salonId}/working-hours`).then(r => r.data),

  updateWorkingHours: (salonId: number, dayOfWeek: number, data: { startTime?: string; endTime?: string; isDayOff: boolean }) =>
    api.put<any>(`/salons/${salonId}/working-hours/${dayOfWeek}`, data).then(r => r.data),

  // Services
  getServices: (salonId: number) =>
    api.get<Service[]>(`/salons/${salonId}/services`).then((r) => r.data),

  addService: (salonId: number, data: Partial<Service>) =>
    api.post<Service>(`/salons/${salonId}/services`, data).then((r) => r.data),

  updateService: (salonId: number, serviceId: number, data: Partial<Service>) =>
    api.put<Service>(`/salons/${salonId}/services/${serviceId}`, data).then((r) => r.data),

  deleteService: (salonId: number, serviceId: number) =>
    api.delete(`/salons/${salonId}/services/${serviceId}`),
}