/**
 * HAIRBOOK MOCK HANDLERS
 * ──────────────────────
 * Interceptira sve API pozive i preusmjerava na mock bazu (db.ts).
 * Simulira realne API response-e uključujući:
 *  - Network delay (150–400ms)
 *  - Paginaciju, filtriranje, sortiranje
 *  - Mutable state (write operacije mijenjaju in-memory store)
 *  - JWT "auth" (samo provjera da li je token prisutan)
 *
 * UPOTREBA:
 *   Import i pozovi `initMockHandlers()` u main.tsx PRIJE svega.
 *   Komentiraj poziv kada backend bude spreman.
 *
 * ZAMJENA SA PRAVIM API-JEM:
 *   1. Zakomentariši `initMockHandlers()` u main.tsx
 *   2. Svi servisi automatski počinju koristiti pravi Axios -> backend
 */

import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig } from 'axios'
import { format, addMinutes } from 'date-fns'
import {
  MOCK_USERS,
  MOCK_SALONS,
  MOCK_SERVICES,
  MOCK_HAIRDRESSERS,
  MOCK_APPOINTMENTS,
  MOCK_REVIEWS,
  MOCK_FAVORITES,
  searchSalons,
  generateAvailability,
} from './db'
import type { Hairdresser, Salon, Service } from '../types/salon.typs'
import type { AuthResponse, User } from '../types/user.types'
import type { Appointment } from '../types/booking.types'
import type { Favorite, Review } from '../types/review.types'


// ─── In-memory mutable state ─────────────────────────────────────────────────
// Kopije iz db.ts — mutiramo ove lokalne kopije, original ostaje kao seed
let users        = [...MOCK_USERS]
let salons       = MOCK_SALONS.map(s => ({ ...s }))
let services     = [...MOCK_SERVICES]
let hairdressers = [...MOCK_HAIRDRESSERS]
let appointments = [...MOCK_APPOINTMENTS]
let reviews      = [...MOCK_REVIEWS]
let favorites    = [...MOCK_FAVORITES]

// ID counters
let nextUserId        = Math.max(...users.map(u => u.id)) + 1
let nextSalonId       = Math.max(...salons.map(s => s.id)) + 1
let nextServiceId     = Math.max(...services.map(s => s.id)) + 1
let nextHairdresserId = Math.max(...hairdressers.map(h => h.id)) + 1
let nextAppointmentId = Math.max(...appointments.map(a => a.id)) + 1
let nextReviewId      = Math.max(...reviews.map(r => r.id)) + 1
let nextFavoriteId    = Math.max(...favorites.map(f => f.id)) + 1

// ─── Utility ─────────────────────────────────────────────────────────────────
function delay(min = 150, max = 350) {
  return new Promise(res => setTimeout(res, min + Math.random() * (max - min)))
}

function ok(data: unknown, status = 200) {
  return Promise.resolve({ data, status, headers: {}, config: {} as AxiosRequestConfig, statusText: 'OK' })
}

function err(message: string, status = 400) {
  const error: any = new Error(message)
  error.response = { data: { message }, status }
  return Promise.reject(error)
}

function currentUserId(): number | null {
  // Čitaj iz localStorage (Zustand persist)
  try {
    const raw = localStorage.getItem('hairbook-auth')
    if (!raw) return null
    const parsed = JSON.parse(raw)
    return parsed?.state?.user?.id ?? null
  } catch {
    return null
  }
}

function salonWithRelations(salonId: number): Salon | undefined {
  const s = salons.find(s => s.id === salonId)
  if (!s) return undefined
  return {
    ...s,
    services:     services.filter(sv => sv.salonId === salonId && sv.isActive),
    hairdressers: hairdressers.filter(h => h.salonId === salonId),
  }
}

function makeJwt(userId: number) {
  // Dummy token — u produkciji dolazi od Spring Security
  return `mock-jwt-${userId}-${Date.now()}`
}

// ─── Route matching ───────────────────────────────────────────────────────────
type Method = 'get' | 'post' | 'put' | 'patch' | 'delete'

interface Route {
  method: Method
  pattern: RegExp
  handler: (url: string, config: AxiosRequestConfig, match: RegExpMatchArray) => Promise<any>
}

const routes: Route[] = []

function route(method: Method, pattern: RegExp, handler: Route['handler']) {
  routes.push({ method, pattern, handler })
}

// ─── AUTH ROUTES ─────────────────────────────────────────────────────────────

route('post', /^\/auth\/register$/, async (_, config) => {
  await delay()
  const body = JSON.parse(config.data ?? '{}')
  if (users.find(u => u.email === body.email)) return err('Email već postoji', 409)

  const newUser: User = {
    id: nextUserId++,
    email: body.email,
    fullName: body.fullName,
    phone: body.phone,
    role: body.role ?? 'CLIENT',
    emailVerified: false,
    createdAt: new Date().toISOString(),
  }
  users.push(newUser)

  const res: AuthResponse = { accessToken: makeJwt(newUser.id), refreshToken: `refresh-${newUser.id}`, user: newUser }
  return ok(res, 201)
})

route('post', /^\/auth\/login$/, async (_, config) => {
  await delay()
  const { email, password } = JSON.parse(config.data ?? '{}')

  // Demo: any password works for existing users
  const user = users.find(u => u.email === email)
  if (!user) return err('Pogrešan email ili lozinka', 401)

  // Demo credentials: password mora biti "password" ili duža od 0 znakova
  if (!password || password.length < 1) return err('Pogrešan email ili lozinka', 401)

  const res: AuthResponse = { accessToken: makeJwt(user.id), refreshToken: `refresh-${user.id}`, user }
  return ok(res)
})

route('post', /^\/auth\/logout$/, async () => {
  await delay(50, 100)
  return ok({})
})

route('get', /^\/auth\/me$/, async () => {
  await delay(50, 150)
  const uid = currentUserId()
  const user = users.find(u => u.id === uid)
  if (!user) return err('Unauthorized', 401)
  return ok(user)
})

route('put', /^\/auth\/me$/, async (_, config) => {
  await delay()
  const uid = currentUserId()
  const idx = users.findIndex(u => u.id === uid)
  if (idx === -1) return err('Unauthorized', 401)
  const body = JSON.parse(config.data ?? '{}')
  users[idx] = { ...users[idx], ...body }
  return ok(users[idx])
})

// ─── SALON ROUTES ─────────────────────────────────────────────────────────────

route('get', /^\/salons$/, async (_, config) => {
  await delay()
  const p = (config.params ?? {}) as Record<string, any>
  const result = searchSalons({
    city:      p.city,
    minRating: p.minRating ? Number(p.minRating) : undefined,
    maxPrice:  p.maxPrice  ? Number(p.maxPrice)  : undefined,
    verified:  p.verified  ? Boolean(p.verified) : undefined,
    sortBy:    p.sortBy,
    page:      p.page  ? Number(p.page)  : 0,
    size:      p.size  ? Number(p.size)  : 12,
  })
  return ok(result)
})

route('get', /^\/salons\/slug\/(.+)$/, async (_, __, match) => {
  await delay()
  const slug = match[1]
  const salon = salons.find(s => s.slug === slug)
  if (!salon) return err('Salon nije pronađen', 404)
  return ok(salonWithRelations(salon.id))
})

route('get', /^\/salons\/(\d+)$/, async (_, __, match) => {
  await delay()
  const id = Number(match[1])
  const salon = salonWithRelations(id)
  if (!salon) return err('Salon nije pronađen', 404)
  return ok(salon)
})

route('post', /^\/salons$/, async (_, config) => {
  await delay()
  const body = JSON.parse(config.data ?? '{}')
  const uid = currentUserId()
  const newSalon: Salon = {
    id: nextSalonId++,
    slug: body.name?.toLowerCase().replace(/\s+/g, '-').replace(/[^a-z0-9-]/g, '') + '-' + Date.now(),
    ...body,
    ownerId: uid,
    avgRating: 0,
    reviewCount: 0,
    verified: false,
    isActive: true,
    photos: [],
    createdAt: new Date().toISOString(),
  }
  salons.push(newSalon)
  return ok(newSalon, 201)
})

route('put', /^\/salons\/(\d+)$/, async (_, config, match) => {
  await delay()
  const id  = Number(match[1])
  const idx = salons.findIndex(s => s.id === id)
  if (idx === -1) return err('Salon nije pronađen', 404)
  const body = JSON.parse(config.data ?? '{}')
  salons[idx] = { ...salons[idx], ...body }
  return ok(salons[idx])
})

route('delete', /^\/salons\/(\d+)$/, async (_, __, match) => {
  await delay()
  const id = Number(match[1])
  salons = salons.map(s => s.id === id ? { ...s, isActive: false } : s)
  return ok({})
})

// ─── SALON APPOINTMENTS (for Owner) ──────────────────────────────────────────

route('get', /^\/salons\/(\d+)\/appointments$/, async (_, config, match) => {
  await delay()
  const salonId = Number(match[1])
  let list = appointments.filter(a => a.salonId === salonId)
  const { from, to } = (config.params ?? {}) as { from?: string; to?: string }
  if (from) list = list.filter(a => a.startTime >= from)
  if (to)   list = list.filter(a => a.startTime <= to)
  return ok(list)
})

// ─── HAIRDRESSER ROUTES ───────────────────────────────────────────────────────

route('get', /^\/salons\/(\d+)\/hairdressers$/, async (_, __, match) => {
  await delay()
  const salonId = Number(match[1])
  return ok(hairdressers.filter(h => h.salonId === salonId))
})

route('post', /^\/salons\/(\d+)\/hairdressers$/, async (_, config, match) => {
  await delay()
  const salonId = Number(match[1])
  const body    = JSON.parse(config.data ?? '{}')
  const newH: Hairdresser = {
    id:           nextHairdresserId++,
    userId:       0,
    salonId,
    fullName:     body.fullName,
    bio:          body.bio,
    specialties:  body.specialties,
    profilePhoto: undefined,
    isActive:     true,
  }
  hairdressers.push(newH)
  return ok(newH, 201)
})

route('put', /^\/salons\/(\d+)\/hairdressers\/(\d+)$/, async (_, config, match) => {
  await delay()
  const hid = Number(match[2])
  const idx = hairdressers.findIndex(h => h.id === hid)
  if (idx === -1) return err('Frizer nije pronađen', 404)
  const body = JSON.parse(config.data ?? '{}')
  hairdressers[idx] = { ...hairdressers[idx], ...body }
  return ok(hairdressers[idx])
})

route('delete', /^\/salons\/(\d+)\/hairdressers\/(\d+)$/, async (_, __, match) => {
  await delay()
  const hid = Number(match[2])
  hairdressers = hairdressers.filter(h => h.id !== hid)
  return ok({})
})

// ─── AVAILABILITY ─────────────────────────────────────────────────────────────

route('get', /^\/hairdressers\/(\d+)\/availability$/, async (_, config, match) => {
  await delay(80, 200)
  const hairdresserId = Number(match[1])
  const date = (config.params as any)?.date ?? format(new Date(), 'yyyy-MM-dd')
  const slots = generateAvailability(hairdresserId, date)
  return ok({ date, hairdresserId, slots })
})

// ─── SERVICE ROUTES ───────────────────────────────────────────────────────────

route('get', /^\/salons\/(\d+)\/services$/, async (_, __, match) => {
  await delay()
  const salonId = Number(match[1])
  return ok(services.filter(s => s.salonId === salonId && s.isActive))
})

route('post', /^\/salons\/(\d+)\/services$/, async (_, config, match) => {
  await delay()
  const salonId = Number(match[1])
  const body    = JSON.parse(config.data ?? '{}')
  const newS: Service = {
    id:              nextServiceId++,
    salonId,
    name:            body.name,
    description:     body.description,
    price:           Number(body.price),
    durationMinutes: Number(body.durationMinutes),
    isActive:        true,
  }
  services.push(newS)
  return ok(newS, 201)
})

route('put', /^\/salons\/(\d+)\/services\/(\d+)$/, async (_, config, match) => {
  await delay()
  const sid = Number(match[2])
  const idx = services.findIndex(s => s.id === sid)
  if (idx === -1) return err('Usluga nije pronađena', 404)
  const body = JSON.parse(config.data ?? '{}')
  services[idx] = { ...services[idx], ...body }
  return ok(services[idx])
})

route('delete', /^\/salons\/(\d+)\/services\/(\d+)$/, async (_, __, match) => {
  await delay()
  const sid = Number(match[2])
  services = services.map(s => s.id === sid ? { ...s, isActive: false } : s)
  return ok({})
})

// ─── APPOINTMENT ROUTES ───────────────────────────────────────────────────────

route('get', /^\/appointments$/, async () => {
  await delay()
  const uid = currentUserId()
  if (!uid) return err('Unauthorized', 401)
  const user = users.find(u => u.id === uid)
  let list: Appointment[]
  if (user?.role === 'CLIENT') {
    list = appointments.filter(a => a.clientId === uid)
  } else if (user?.role === 'HAIRDRESSER') {
    const h = hairdressers.find(h => h.userId === uid)
    list = appointments.filter(a => a.hairdresserId === h?.id)
  } else {
    list = appointments
  }
  return ok(list.sort((a, b) => a.startTime.localeCompare(b.startTime)))
})

route('get', /^\/appointments\/(\d+)$/, async (_, __, match) => {
  await delay(80)
  const id = Number(match[1])
  const a = appointments.find(a => a.id === id)
  if (!a) return err('Termin nije pronađen', 404)
  return ok(a)
})

route('post', /^\/appointments$/, async (_, config) => {
  await delay()
  const uid  = currentUserId()
  if (!uid) return err('Unauthorized', 401)
  const body = JSON.parse(config.data ?? '{}')

  const hairdresser = hairdressers.find(h => h.id === body.hairdresserId)
  const service     = services.find(s => s.id === body.serviceId)
  const client      = users.find(u => u.id === uid)
  const salon       = salons.find(s => s.id === hairdresser?.salonId)

  if (!hairdresser || !service || !client || !salon) return err('Neispravni podaci', 400)

  const endTime = new Date(body.startTime)
  endTime.setMinutes(endTime.getMinutes() + service.durationMinutes)

  const newA: Appointment = {
    id:              nextAppointmentId++,
    clientId:        uid,
    clientName:      client.fullName,
    clientPhone:     client.phone,
    hairdresserId:   hairdresser.id,
    hairdresserName: hairdresser.fullName,
    serviceId:       service.id,
    serviceName:     service.name,
    salonId:         salon.id,
    salonName:       salon.name,
    salonAddress:    salon.address,
    startTime:       body.startTime,
    endTime:         endTime.toISOString(),
    status:          'PENDING',
    price:           service.price,
    notes:           body.notes,
    createdAt:       new Date().toISOString(),
  }
  appointments.push(newA)
  return ok(newA, 201)
})

route('patch', /^\/appointments\/(\d+)\/status$/, async (_, config, match) => {
  await delay()
  const id  = Number(match[1])
  const idx = appointments.findIndex(a => a.id === id)
  if (idx === -1) return err('Termin nije pronađen', 404)
  const { status } = JSON.parse(config.data ?? '{}')
  appointments[idx] = { ...appointments[idx], status }
  return ok(appointments[idx])
})

route('delete', /^\/appointments\/(\d+)$/, async (_, __, match) => {
  await delay()
  const id  = Number(match[1])
  const idx = appointments.findIndex(a => a.id === id)
  if (idx === -1) return err('Termin nije pronađen', 404)
  appointments[idx] = { ...appointments[idx], status: 'CANCELLED' }
  return ok({})
})

// ─── REVIEW ROUTES ────────────────────────────────────────────────────────────

route('get', /^\/salons\/(\d+)\/reviews$/, async (_, __, match) => {
  await delay()
  const salonId = Number(match[1])
  return ok(reviews.filter(r => r.salonId === salonId).sort((a, b) => b.createdAt.localeCompare(a.createdAt)))
})

route('post', /^\/reviews$/, async (_, config) => {
  await delay()
  const uid  = currentUserId()
  if (!uid) return err('Unauthorized', 401)
  const body = JSON.parse(config.data ?? '{}')
  const client = users.find(u => u.id === uid)

  const newR: Review = {
    id:              nextReviewId++,
    clientId:        uid,
    clientName:      client?.fullName ?? 'Klijent',
    salonId:         body.salonId,
    hairdresserId:   body.hairdresserId,
    hairdresserName: hairdressers.find(h => h.id === body.hairdresserId)?.fullName,
    appointmentId:   body.appointmentId,
    rating:          body.rating,
    comment:         body.comment,
    createdAt:       new Date().toISOString(),
  }
  reviews.push(newR)

  // Recalculate salon avgRating
  const salonReviews = reviews.filter(r => r.salonId === body.salonId)
  const avg = salonReviews.reduce((s, r) => s + r.rating, 0) / salonReviews.length
  const sidx = salons.findIndex(s => s.id === body.salonId)
  if (sidx !== -1) {
    salons[sidx] = { ...salons[sidx], avgRating: Math.round(avg * 10) / 10, reviewCount: salonReviews.length }
  }

  return ok(newR, 201)
})

route('delete', /^\/reviews\/(\d+)$/, async (_, __, match) => {
  await delay()
  const id = Number(match[1])
  reviews = reviews.filter(r => r.id !== id)
  return ok({})
})

// ─── FAVORITES ROUTES ─────────────────────────────────────────────────────────

route('get', /^\/favorites$/, async () => {
  await delay()
  const uid = currentUserId()
  if (!uid) return err('Unauthorized', 401)
  return ok(favorites.filter(f => f.userId === uid))
})

route('post', /^\/favorites$/, async (_, config) => {
  await delay()
  const uid  = currentUserId()
  if (!uid) return err('Unauthorized', 401)
  const { salonId } = JSON.parse(config.data ?? '{}')
  if (favorites.find(f => f.userId === uid && f.salonId === salonId)) return err('Već dodano', 409)
  const salon = salons.find(s => s.id === salonId)
  if (!salon) return err('Salon nije pronađen', 404)

  const newF: Favorite = {
    id: nextFavoriteId++,
    userId: uid,
    salonId,
    salon: { id: salon.id, name: salon.name, city: salon.city, avgRating: salon.avgRating, verified: salon.verified },
    createdAt: new Date().toISOString(),
  }
  favorites.push(newF)
  return ok(newF, 201)
})

route('delete', /^\/favorites\/(\d+)$/, async (_, __, match) => {
  await delay()
  const uid     = currentUserId()
  const salonId = Number(match[1])
  favorites = favorites.filter(f => !(f.userId === uid && f.salonId === salonId))
  return ok({})
})

// ─── INTERCEPTOR INIT ─────────────────────────────────────────────────────────

let initialized = false

export function initMockHandlers(axiosInstance: AxiosInstance) {
  if (initialized) return
  initialized = true

  axiosInstance.interceptors.request.use(async (config) => {
    const url    = config.url?.replace(config.baseURL ?? '', '') ?? ''
    const method = (config.method?.toLowerCase() ?? 'get') as Method

    for (const r of routes) {
      if (r.method !== method) continue
      const match = url.match(r.pattern)
      if (!match) continue

      // Mark as mock so response interceptor skips it
      ;(config as any).__mock = true

      try {
        const result = await r.handler(url, config, match)
        config.adapter = () => Promise.resolve({ ...result, config })
      } catch (e: any) {
        config.adapter = () => Promise.reject(e)
      }

      return config
    }

    // No mock matched — let real request through (useful when partial backend is ready)
    console.warn(`[MOCK] No handler for ${method.toUpperCase()} ${url} — forwarding to real API`)
    return config
  })

  console.info(
    '%c[HairBook Mock] ✓ Mock handlers aktivan — svi API pozivi koriste mock bazu',
    'color: #e94560; font-weight: bold'
  )
}

// ─── DEV HELPERS (dostupne u browser konzoli) ─────────────────────────────────
if (typeof window !== 'undefined') {
  ;(window as any).__hairbook_mock = {
    getState: () => ({ users, salons, services, hairdressers, appointments, reviews, favorites }),
    reset:    () => {
      users        = [...MOCK_USERS]
      salons       = MOCK_SALONS.map(s => ({ ...s }))
      services     = [...MOCK_SERVICES]
      hairdressers = [...MOCK_HAIRDRESSERS]
      appointments = [...MOCK_APPOINTMENTS]
      reviews      = [...MOCK_REVIEWS]
      favorites    = [...MOCK_FAVORITES]
      console.info('[HairBook Mock] State resetovan')
    },
  }
}