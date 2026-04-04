 
export interface SalonPhoto {
  id: number
  url: string
  isPrimary: boolean
}

export interface Hairdresser {
  id: number
  userId: number
  salonId: number
  fullName: string
  bio?: string
  specialties?: string
  profilePhoto?: string
  isActive: boolean
  services?: Service[]
  avgRating?: number
}
 
export interface Service {
  id: number
  salonId: number
  name: string
  description?: string
  price: number
  durationMinutes: number
  isActive: boolean
}


export interface WorkingHours {
  id: number
  dayOfWeek: number // 0=Pon, 6=Uto
  startTime: string // 09:00
  endTime: string   // 18:00
  isDayOff: boolean
}
 
export interface SalonSearchParams {
  city?: string
  lat?: number
  lng?: number
  minRating?: number
  maxPrice?: number
  minPrice?: number
  sortBy?: 'rating' | 'distance' | 'popularity' | 'price_asc' | 'price_desc'
  page?: number
  size?: number
  verified?: boolean
}
 
export interface SalonSearchResponse {
  content: Salon[]
  totalElements: number
  totalPages: number
  currentPage: number
  size: number
}

export interface Salon {
  id: number
  slug: string
  name: string
  description?: string
  city: string
  address: string
  latitude?: number
  longitude?: number
  phone?: string
  website?: string
  avgRating: number
  reviewCount: number
  verified: boolean
  isActive: boolean
  ownerId?: number
  photos: SalonPhoto[]
  hairdressers?: Hairdresser[]
  services?: Service[]
  workingHours?: WorkingHours[]
  createdAt: string
}

