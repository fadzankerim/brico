import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import { bookingService } from '../services/booking.service'
import { useBookingStore } from '../store/bookingStore'
import type { CreateAppointmentRequest } from '../types/booking.types'


export const appointmentKeys = {
  all: ['appointments'] as const,
  mine: () => ['appointments', 'mine'] as const,
  detail: (id: number) => ['appointments', id] as const,
  availability: (hairdresserId: number, date: string) =>
    ['availability', hairdresserId, date] as const,
  salonAll: (salonId: number) => ['appointments', 'salon', salonId] as const,
}

export function useMyAppointments() {
  return useQuery({
    queryKey: appointmentKeys.mine(),
    queryFn: () => bookingService.getMyAppointments(),
    staleTime: 1000 * 60,
  })
}

export function useAvailability(hairdresserId: number | undefined, date: string | undefined) {
  return useQuery({
    queryKey: appointmentKeys.availability(hairdresserId!, date!),
    queryFn: () => bookingService.getAvailability(hairdresserId!, date!),
    enabled: !!hairdresserId && !!date,
    staleTime: 30_000, // 30s — availability changes fast
  })
}

export function useCreateAppointment() {
  const queryClient = useQueryClient()
  const reset = useBookingStore((s) => s.reset)

  return useMutation({
    mutationFn: (data: CreateAppointmentRequest) => bookingService.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: appointmentKeys.mine() })
      toast.success('Termin uspješno rezervisan!')
      reset()
    },
    onError: () => {
      toast.error('Greška pri rezervaciji. Pokušajte ponovo.')
    },
  })
}

export function useCancelAppointment() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => bookingService.cancel(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: appointmentKeys.mine() })
      toast.success('Termin je otkazan')
    },
    onError: () => {
      toast.error('Greška pri otkazivanju termina')
    },
  })
}

export function useSalonAppointments(salonId: number | undefined) {
  return useQuery({
    queryKey: appointmentKeys.salonAll(salonId!),
    queryFn: () => bookingService.getSalonAppointments(salonId!),
    enabled: !!salonId,
    staleTime: 30_000,
  })
}