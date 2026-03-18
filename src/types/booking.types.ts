
export type AppointmentStatus =
  | 'PENDING'
  | 'CONFIRMED'
  | 'COMPLETED'
  | 'CANCELLED'
  | 'NO_SHOW'



export interface Appointment {
  id: number
  clientId: number
  clientName: string
  clientPhone?: string
  hairdresserId: number
  hairdresserName: string
  hairdresserPhoto?: string
  serviceId: number
  serviceName: string
  salonId: number
  salonName: string
  salonAddress: string
  startTime: string  // ISO datetime
  endTime: string
  status: AppointmentStatus
  price: number
  notes?: string
  createdAt: string
}

 
export interface CreateAppointmentRequest {
  hairdresserId: number
  serviceId: number
  startTime: string
  notes?: string
}


export interface TimeSlot {
  time: string   // "09:00"
  available: boolean
}

export interface AvailabilityResponse {
  date: string
  hairdresserId: number
  slots: TimeSlot[]
}


// Booking wizard state
export interface BookingWizardState {
  salonId?: number
  salonName?: string
  selectedHairdresser?: { id: number; name: string; photo?: string }
  selectedService?: { id: number; name: string; price: number; duration: number }
  selectedDate?: string   // "2026-03-20"
  selectedTime?: string   // "14:30"
  notes?: string
}
 