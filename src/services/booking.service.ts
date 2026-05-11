import { api } from './api'
import { useAuthStore } from '../store/authStore'
import type { Appointment, CreateAppointmentRequest, AvailabilityResponse } from '../types/booking.types'

export const bookingService = {
  create: (data: CreateAppointmentRequest) =>
    api.post<Appointment>('/appointments', data).then((r) => r.data),

  getMyAppointments: () => {
    const user = useAuthStore.getState().user
    return api.get<Appointment[]>('/appointments', {
      params: user ? { clientId: user.id } : {}
    }).then((r) => r.data)
  },

  getById: (id: number) =>
    api.get<Appointment>(`/appointments/${id}`).then((r) => r.data),

  updateStatus: (id: number, status: string) =>
    api.patch<Appointment>(`/appointments/${id}/status`, null, { params: { status } }).then((r) => r.data),

  cancel: (id: number) =>
    api.patch(`/appointments/${id}/cancel`),

  getHairdresserAppointments: (hairdresserId: number) =>
    api.get<Appointment[]>('/appointments', { params: { hairdresserId } }).then(r => r.data),

  getSalonAppointments: (salonId: number, params?: { from?: string; to?: string }) =>
    api.get<Appointment[]>('/appointments', { params: { salonId, ...params } }).then((r) => r.data),

  getAvailability: (salonId: number, hairdresserId: number, date: string, totalDuration: number) =>
    api
      .get<AvailabilityResponse>(`/salons/${salonId}/hairdressers/${hairdresserId}/availability`, {
        params: { date, duration: totalDuration },
      })
      .then((r) => r.data),
}