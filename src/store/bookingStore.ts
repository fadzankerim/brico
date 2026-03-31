import { create } from 'zustand'
import { totalDuration, totalPrice, type BookingWizardState, type SelectedService } from '../types/booking.types'

interface BookingStore {
  step: number
  data: BookingWizardState
  setStep: (step: number) => void
  nextStep: () => void
  prevStep: () => void
  setData:  (data: Partial<Omit<BookingWizardState, 'selectedServices'>>) => void
  // Multi-service actions
  toggleService:    (service: SelectedService) => void
  isServiceSelected:(id: number) => boolean
  clearServices:    () => void
 
  // Computed
  getTotalPrice:    () => number
  getTotalDuration: () => number
  reset: () => void
}

const INITIAL: BookingWizardState = {
  selectedServices: [],
}

export const useBookingStore = create<BookingStore>((set, get) => ({
  step: 1,
  data: INITIAL,

  setStep: (step) => set({ step }),
  nextStep: () => set((s) => ({ step: Math.min(s.step + 1, 4) })),
  prevStep: () => set((s) => ({ step: Math.max(s.step - 1, 1) })),
  setData: (data) => set((s) => ({ data: { ...s.data, ...data } })),
  
  toggleService: (service) =>
    set((s) => {
      const exists = s.data.selectedServices.some(sv => sv.id === service.id)
      const selectedServices = exists
        ? s.data.selectedServices.filter(sv => sv.id !== service.id)
        : [...s.data.selectedServices, service]
      // Reset time when services change (duration changes → slots change)
      return { data: { ...s.data, selectedServices, selectedTime: undefined } }
    }),

  isServiceSelected: (id) =>
    get().data.selectedServices.some(s => s.id === id),
 
  clearServices: () =>
    set((s) => ({ data: { ...s.data, selectedServices: [], selectedTime: undefined } })),
 
  getTotalPrice:    () => totalPrice(get().data.selectedServices),
  getTotalDuration: () => totalDuration(get().data.selectedServices),
 
  reset: () => set({ step: 1, data: INITIAL }),
}))
