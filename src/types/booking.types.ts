
export type AppointmentStatus =
  | 'PENDING'
  | 'CONFIRMED'
  | 'COMPLETED'
  | 'CANCELLED'
  | 'NO_SHOW'

  export interface AppointmentService {
  serviceId:       number
  serviceName:     string
  price:           number
  durationMinutes: number
}

export interface InvoiceItem {
  name:     string
  price:    number
  duration: number   // minutes
}

export interface Invoice {
  invoiceNumber:   string   // "HB-20260330-0001"
  appointmentId:   number
  clientName:      string
  clientEmail:     string
  salonName:       string
  salonAddress:    string
  hairdresserName: string
  date:            string   // ISO
  items:           InvoiceItem[]
  totalPrice:      number
  totalDuration:   number   // minutes — suma svih usluga
  status:          'SENT' | 'PENDING'
  createdAt:       string
}

export interface Appointment {
  id:              number
  clientId:        number
  clientName:      string
  clientPhone?:    string
  hairdresserId:   number
  hairdresserName: string
  hairdresserPhoto?: string
  services:        AppointmentService[]
  items?:          AppointmentService[]  // alias za services (backend vraća items)
  serviceId:       number   // primary service (first) — legacy compat
  serviceName:     string   // primary service name  — legacy compat
  salonId:         number
  salonName:       string
  salonAddress:    string
  startTime:       string   // ISO datetime
  endTime:         string   // startTime + sum(durations)
  status:          AppointmentStatus
  price:           number   // alias za totalPrice (legacy compat)
  totalPrice?:     number   // backend polje (opcionalno za backward compat)
  notes?:          string
  cancelReason?:   string
  invoice?:        Invoice
  createdAt:       string
}


 
export interface AppointmentItemRequest {
  serviceId:       number
  serviceName:     string
  price:           number
  durationMinutes: number
}

export interface CreateAppointmentRequest {
  clientId?:       number
  clientName:      string
  clientPhone?:    string
  hairdresserId:   number
  hairdresserName: string
  salonId:         number
  salonName:       string
  salonAddress?:   string
  startTime:       string
  notes?:          string
  items:           AppointmentItemRequest[]
}


export interface TimeSlot {
  time: string   // "09:00"
  available: boolean
}

export interface AvailabilityResponse {
  date: string
  hairdresserId: number
  totalDuration: number
  slots: TimeSlot[]
}

export interface SelectedService{
  id:       number
  name:     string
  price:    number
  duration: number 
}


// Booking wizard state
export interface BookingWizardState {
  salonId?: number
  salonName?: string
  selectedHairdresser?: { id: number; name: string; photo?: string }
  selectedServices: SelectedService[]
  selectedDate?: string   // "2026-03-20"
  selectedTime?: string   // "14:30"
  notes?: string
}
 

export function totalPrice(services: SelectedService[]): number {
  return services.reduce((sum, s) => sum + s.price, 0)
}
 
export function totalDuration(services: SelectedService[]): number {
  return services.reduce((sum, s) => sum + s.duration, 0)
}