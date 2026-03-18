import { create } from 'zustand'
import type { BookingWizardState } from '../types/booking.types'

interface BookingStore {
  step: number
  data: BookingWizardState
  setStep: (step: number) => void
  nextStep: () => void
  prevStep: () => void
  setData: (data: Partial<BookingWizardState>) => void
  reset: () => void
}

const INITIAL: BookingWizardState = {}

export const useBookingStore = create<BookingStore>((set) => ({
  step: 1,
  data: INITIAL,

  setStep: (step) => set({ step }),
  nextStep: () => set((s) => ({ step: Math.min(s.step + 1, 4) })),
  prevStep: () => set((s) => ({ step: Math.max(s.step - 1, 1) })),
  setData: (data) => set((s) => ({ data: { ...s.data, ...data } })),
  reset: () => set({ step: 1, data: INITIAL }),
}))