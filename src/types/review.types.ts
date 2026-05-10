
export interface Review {
  id: number
  clientId: number
  clientName: string
  clientPhoto?: string
  salonId: number
  hairdresserId?: number
  hairdresserName?: string
  appointmentId?: number
  rating: number
  comment?: string
  ownerReply?: string
  createdAt: string
}
 
export interface CreateReviewRequest {
  clientId?: number
  clientName?: string
  salonId: number
  hairdresserId?: number
  appointmentId?: number
  rating: number
  comment?: string
}
 
export interface Favorite {
  id: number
  userId: number
  salonId: number
  salon: {
    id: number
    name: string
    slug: string
    city: string
    avgRating: number
    primaryPhoto?: string
    verified: boolean
  }
  createdAt: string
}
 
// STATISTIKE ZA DASHBOARD

export interface RevenueStats {
  totalRevenue: number
  appointmentsCount: number
  avgPerAppointment: number
  periodLabel: string
  chartData: { label: string; revenue: number; count: number }[]
}
 
export interface ServiceStats {
  serviceName: string
  count: number
  revenue: number
  percentage: number
}
 
export interface DashboardStats {
  todayAppointments: number
  weekRevenue: number
  monthRevenue: number
  totalClients: number
  avgRating: number
  completionRate: number
}