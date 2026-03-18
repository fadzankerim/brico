import { api } from './api'
import type { SalonSearchParams, SalonSearchResponse, Salon, Hairdresser, Service } from '../types/salon.typs'

export const salonService = {
  search: (params: SalonSearchParams) =>
    api.get<SalonSearchResponse>('/salons', { params }).then((r) => r.data),

  getById: (id: number) =>
    api.get<Salon>(`/salons/${id}`).then((r) => r.data),

  getBySlug: (slug: string) =>
    api.get<Salon>(`/salons/slug/${slug}`).then((r) => r.data),

  create: (data: Partial<Salon>) =>
    api.post<Salon>('/salons', data).then((r) => r.data),

  update: (id: number, data: Partial<Salon>) =>
    api.put<Salon>(`/salons/${id}`, data).then((r) => r.data),

  delete: (id: number) =>
    api.delete(`/salons/${id}`),

  // Hairdressers
  getHairdressers: (salonId: number) =>
    api.get<Hairdresser[]>(`/salons/${salonId}/hairdressers`).then((r) => r.data),

  addHairdresser: (salonId: number, data: Partial<Hairdresser>) =>
    api.post<Hairdresser>(`/salons/${salonId}/hairdressers`, data).then((r) => r.data),

  updateHairdresser: (salonId: number, hairdresserId: number, data: Partial<Hairdresser>) =>
    api.put<Hairdresser>(`/salons/${salonId}/hairdressers/${hairdresserId}`, data).then((r) => r.data),

  removeHairdresser: (salonId: number, hairdresserId: number) =>
    api.delete(`/salons/${salonId}/hairdressers/${hairdresserId}`),

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