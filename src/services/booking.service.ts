import { api } from './api'
import type { Appointment, CreateAppointmentRequest, AvailabilityResponse } from '../types/booking.types'

export const bookingService = {
  create: (data: CreateAppointmentRequest) =>
    api.post<Appointment>('/appointments', data).then((r) => r.data),

  getMyAppointments: () =>
    api.get<Appointment[]>('/appointments').then((r) => r.data),

  getById: (id: number) =>
    api.get<Appointment>(`/appointments/${id}`).then((r) => r.data),

  updateStatus: (id: number, status: string) =>
    api.patch<Appointment>(`/appointments/${id}/status`, { status }).then((r) => r.data),

  cancel: (id: number) =>
    api.delete(`/appointments/${id}`),

  getSalonAppointments: (salonId: number, params?: { from?: string; to?: string }) =>
    api.get<Appointment[]>(`/salons/${salonId}/appointments`, { params }).then((r) => r.data),

  getAvailability: (hairdresserId: number, date: string, totalDuration: number) =>
    api
      .get<AvailabilityResponse>(`/hairdressers/${hairdresserId}/availability`, {
        params: { date, duration: totalDuration },
      })
      .then((r) => r.data),
}