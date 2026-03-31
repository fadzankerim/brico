/**
 * HAIRBOOK MOCK HANDLERS
 * ──────────────────────
 * Interceptira sve API pozive i vraća podatke iz mock baze (db.ts).
 *
 * Kako radi:
 *  1. Registrira se kao REQUEST interceptor na api instancu
 *  2. Kada matchuje rutu, postavlja custom `adapter` na requestu
 *  3. Adapter odmah razriješava Promise s mock podacima — pravi HTTP
 *     request se nikada ne šalje
 *
 * VAŽNO: mora biti registriran PRIJE JWT interceptora (poziv u api.ts,
 * ne u main.tsx) jer Axios izvršava request interceptore u LIFO redoslijedu.
 *
 * Prebacivanje na backend:
 *   U api.ts zakomentariši: initMockHandlers(api)
 */

import type { AxiosInstance, AxiosRequestConfig, InternalAxiosRequestConfig } from 'axios'
import { format } from 'date-fns'
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
import type { User } from '../types/user.types'
import type { Favorite, Review } from '../types/review.types'
import type { Appointment } from '../types/booking.types'


// ─── In-memory mutable store ──────────────────────────────────────────────────
let users        = [...MOCK_USERS]
let salons       = MOCK_SALONS.map(s => ({ ...s }))
let services     = [...MOCK_SERVICES]
let hairdressers = [...MOCK_HAIRDRESSERS]
let appointments = [...MOCK_APPOINTMENTS]
let reviews      = [...MOCK_REVIEWS]
let favorites    = [...MOCK_FAVORITES]

let nextUserId        = Math.max(...users.map(u => u.id)) + 1
let nextSalonId       = Math.max(...salons.map(s => s.id)) + 1
let nextServiceId     = Math.max(...services.map(s => s.id)) + 1
let nextHairdresserId = Math.max(...hairdressers.map(h => h.id)) + 1
let nextAppointmentId = Math.max(...appointments.map(a => a.id)) + 1
let nextReviewId      = Math.max(...reviews.map(r => r.id)) + 1
let nextFavoriteId    = Math.max(...favorites.map(f => f.id)) + 1

// ─── Utilities ────────────────────────────────────────────────────────────────
const delay = (ms = 200) => new Promise(r => setTimeout(r, ms + Math.random() * 150))

/** Builds a mock AxiosResponse that the adapter resolves with */
function mockResponse(data: unknown, status = 200, config: InternalAxiosRequestConfig) {
  return {
    data,
    status,
    statusText: status === 201 ? 'Created' : 'OK',
    headers:    { 'content-type': 'application/json' },
    config,
  }
}

/** Builds a rejected error that mimics an Axios HTTP error */
function mockError(message: string, status: number, config: InternalAxiosRequestConfig) {
  const err: any = new Error(message)
  err.isAxiosError = true
  err.response = {
    data:       { message },
    status,
    statusText: 'Error',
    headers:    {},
    config,
  }
  return err
}

function salonWithRelations(id: number): Salon | undefined {
  const s = salons.find(s => s.id === id)
  if (!s) return undefined
  return {
    ...s,
    services:     services.filter(sv => sv.salonId === id && sv.isActive),
    hairdressers: hairdressers.filter(h => h.salonId === id),
  }
}

function currentUserId(config: InternalAxiosRequestConfig): number | null {
  // Read from Authorization header set by JWT interceptor
  // Format: "Bearer mock-jwt-{userId}-{timestamp}"
  const auth = config.headers?.Authorization as string | undefined
  if (!auth) return null
  const match = auth.match(/^Bearer mock-jwt-(\d+)-/)
  return match ? Number(match[1]) : null
}

function makeJwt(userId: number) {
  return `mock-jwt-${userId}-${Date.now()}`
}

function parseBody(config: InternalAxiosRequestConfig): Record<string, any> {
  if (!config.data) return {}
  if (typeof config.data === 'string') {
    try { return JSON.parse(config.data) } catch { return {} }
  }
  if (typeof config.data === 'object') return config.data
  return {}
}

// ─── Route table ──────────────────────────────────────────────────────────────
type Method = 'get' | 'post' | 'put' | 'patch' | 'delete'

type RouteHandler = (
  url:    string,
  config: InternalAxiosRequestConfig,
  match:  RegExpMatchArray
) => Promise<{ data: unknown; status: number }>

interface Route { method: Method; pattern: RegExp; handler: RouteHandler }

const routes: Route[] = []

function route(method: Method, pattern: RegExp, handler: RouteHandler) {
  routes.push({ method, pattern, handler })
}

// Shorthand returns
const ok  = async (data: unknown, status = 200) => ({ data, status })
const err = (message: string, status: number)   => {
  const e: any = { __mockError: true, message, status }
  throw e
}

// ─── AUTH ─────────────────────────────────────────────────────────────────────

route('post', /^\/auth\/register$/, async (_, config) => {
  await delay()
  const body = parseBody(config)
  if (users.find(u => u.email === body.email)) err('Email već postoji', 409)

  const newUser: User = {
    id:            nextUserId++,
    email:         body.email,
    fullName:      body.fullName,
    phone:         body.phone,
    role:          body.role ?? 'CLIENT',
    emailVerified: false,
    createdAt:     new Date().toISOString(),
  }
  users.push(newUser)
  return ok({ accessToken: makeJwt(newUser.id), refreshToken: `refresh-${newUser.id}`, user: newUser }, 201)
})

route('post', /^\/auth\/login$/, async (_, config) => {
  await delay()
  const { email, password } = parseBody(config)
  const user = users.find(u => u.email === email)

  if (!user)                         err('Pogrešan email ili lozinka', 401)
  if (!password || password.length < 1) err('Lozinka je obavezna', 401)

  return ok({ accessToken: makeJwt(user!.id), refreshToken: `refresh-${user!.id}`, user: user! })
})

route('post', /^\/auth\/logout$/, async () => {
  await delay(50)
  return ok({})
})

route('get', /^\/auth\/me$/, async (_, config) => {
  await delay(50)
  const uid  = currentUserId(config)
  const user = users.find(u => u.id === uid)
  if (!user) err('Unauthorized', 401)
  return ok(user!)
})

route('put', /^\/auth\/me$/, async (_, config) => {
  await delay()
  const uid  = currentUserId(config)
  const idx  = users.findIndex(u => u.id === uid)
  if (idx === -1) err('Unauthorized', 401)
  users[idx] = { ...users[idx], ...parseBody(config) }
  return ok(users[idx])
})

// ─── SALONS ───────────────────────────────────────────────────────────────────

route('get', /^\/salons$/, async (_, config) => {
  await delay()
  const p = config.params as Record<string, any> ?? {}
  return ok(searchSalons({
    city:      p.city,
    minRating: p.minRating ? Number(p.minRating) : undefined,
    maxPrice:  p.maxPrice  ? Number(p.maxPrice)  : undefined,
    verified:  p.verified  ? Boolean(p.verified) : undefined,
    sortBy:    p.sortBy,
    page:      p.page ? Number(p.page) : 0,
    size:      p.size ? Number(p.size) : 12,
  }))
})

route('get', /^\/salons\/slug\/(.+)$/, async (_, __, match) => {
  await delay()
  const s = salons.find(s => s.slug === match[1])
  if (!s) err('Salon nije pronađen', 404)
  return ok(salonWithRelations(s!.id)!)
})

route('get', /^\/salons\/(\d+)$/, async (_, __, match) => {
  await delay()
  const s = salonWithRelations(Number(match[1]))
  if (!s) err('Salon nije pronađen', 404)
  return ok(s!)
})

route('post', /^\/salons$/, async (_, config) => {
  await delay()
  const body = parseBody(config)
  const uid  = currentUserId(config)
  const newS: Salon = {
    id:          nextSalonId++,
    slug:        body.name?.toLowerCase().replace(/\s+/g, '-').replace(/[^a-z0-9-]/g, '') + '-' + Date.now(),
    ...body,
    name: body.name,
    city: body.city,
    address: body.address,
    avgRating:   0,
    reviewCount: 0,
    verified:    false,
    isActive:    true,
    photos:      [],
    createdAt:   new Date().toISOString(),
  }
  salons.push(newS)
  return ok(newS, 201)
})

route('put', /^\/salons\/(\d+)$/, async (_, config, match) => {
  await delay()
  const idx = salons.findIndex(s => s.id === Number(match[1]))
  if (idx === -1) err('Salon nije pronađen', 404)
  salons[idx] = { ...salons[idx], ...parseBody(config) }
  return ok(salons[idx])
})

route('delete', /^\/salons\/(\d+)$/, async (_, __, match) => {
  await delay()
  salons = salons.map(s => s.id === Number(match[1]) ? { ...s, isActive: false } : s)
  return ok({})
})

// ─── SALON APPOINTMENTS ───────────────────────────────────────────────────────

route('get', /^\/salons\/(\d+)\/appointments$/, async (_, config, match) => {
  await delay()
  const salonId = Number(match[1])
  let list = appointments.filter(a => a.salonId === salonId)
  const p = config.params as any ?? {}
  if (p.from) list = list.filter(a => a.startTime >= p.from)
  if (p.to)   list = list.filter(a => a.startTime <= p.to)
  return ok(list)
})

// ─── HAIRDRESSERS ─────────────────────────────────────────────────────────────

route('get', /^\/salons\/(\d+)\/hairdressers$/, async (_, __, match) => {
  await delay()
  return ok(hairdressers.filter(h => h.salonId === Number(match[1])))
})

route('post', /^\/salons\/(\d+)\/hairdressers$/, async (_, config, match) => {
  await delay()
  const body = parseBody(config)
  const newH: Hairdresser = {
    id:           nextHairdresserId++,
    userId:       0,
    salonId:      Number(match[1]),
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
  const idx = hairdressers.findIndex(h => h.id === Number(match[2]))
  if (idx === -1) err('Frizer nije pronađen', 404)
  hairdressers[idx] = { ...hairdressers[idx], ...parseBody(config) }
  return ok(hairdressers[idx])
})

route('delete', /^\/salons\/(\d+)\/hairdressers\/(\d+)$/, async (_, __, match) => {
  await delay()
  hairdressers = hairdressers.filter(h => h.id !== Number(match[2]))
  return ok({})
})

// ─── AVAILABILITY ─────────────────────────────────────────────────────────────

route('get', /^\/hairdressers\/(\d+)\/availability$/, async (_, config, match) => {
  await delay(80)
  const hairdresserId  = Number(match[1])
  const params         = (config.params as any) ?? {}
  const date           = params.date ?? format(new Date(), 'yyyy-MM-dd')
  const totalDuration  = Number(params.duration ?? 30)
  return ok({ date, hairdresserId, totalDuration, slots: generateAvailability(hairdresserId, date, totalDuration) })
})

// ─── SERVICES ─────────────────────────────────────────────────────────────────

route('get', /^\/salons\/(\d+)\/services$/, async (_, __, match) => {
  await delay()
  return ok(services.filter(s => s.salonId === Number(match[1]) && s.isActive))
})

route('post', /^\/salons\/(\d+)\/services$/, async (_, config, match) => {
  await delay()
  const body = parseBody(config)
  const newS: Service = {
    id:              nextServiceId++,
    salonId:         Number(match[1]),
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
  const idx = services.findIndex(s => s.id === Number(match[2]))
  if (idx === -1) err('Usluga nije pronađena', 404)
  services[idx] = { ...services[idx], ...parseBody(config) }
  return ok(services[idx])
})

route('delete', /^\/salons\/(\d+)\/services\/(\d+)$/, async (_, __, match) => {
  await delay()
  services = services.map(s => s.id === Number(match[2]) ? { ...s, isActive: false } : s)
  return ok({})
})

// ─── APPOINTMENTS ─────────────────────────────────────────────────────────────

route('get', /^\/appointments$/, async (_, config) => {
  await delay()
  const uid  = currentUserId(config)
  const user = users.find(u => u.id === uid)
  if (!user) err('Unauthorized', 401)

  let list: Appointment[]
  if (user!.role === 'CLIENT') {
    list = appointments.filter(a => a.clientId === uid)
  } else if (user!.role === 'HAIRDRESSER') {
    const h = hairdressers.find(h => h.userId === uid)
    list = h ? appointments.filter(a => a.hairdresserId === h.id) : []
  } else {
    list = appointments
  }
  return ok(list.sort((a, b) => a.startTime.localeCompare(b.startTime)))
})

route('get', /^\/appointments\/(\d+)$/, async (_, __, match) => {
  await delay(80)
  const a = appointments.find(a => a.id === Number(match[1]))
  if (!a) err('Termin nije pronađen', 404)
  return ok(a!)
})

route('post', /^\/appointments$/, async (_, config) => {
  await delay()
  const uid  = currentUserId(config)
  if (!uid) err('Unauthorized', 401)
  const body = parseBody(config)

  // Support both single serviceId (legacy) and serviceIds array (multi-service)
  const serviceIdList: number[] = body.serviceIds?.length
    ? body.serviceIds
    : body.serviceId ? [body.serviceId] : []

  if (!serviceIdList.length) err('Minimalno jedna usluga je obavezna', 400)

  const hairdresser   = hairdressers.find(h => h.id === body.hairdresserId)
  const selectedSvcs  = serviceIdList.map((id: number) => services.find(s => s.id === id)).filter(Boolean) as typeof services
  const client        = users.find(u => u.id === uid)
  const salon         = salons.find(s => s.id === hairdresser?.salonId)
  if (!hairdresser || !selectedSvcs.length || !client || !salon) err('Neispravni podaci', 400)

  // Total price and duration = sum of all services
  const totalPrice    = selectedSvcs.reduce((sum, s) => sum + s.price, 0)
  const totalDuration = selectedSvcs.reduce((sum, s) => sum + s.durationMinutes, 0)

  const endDate = new Date(body.startTime)
  endDate.setMinutes(endDate.getMinutes() + totalDuration)

  // Build AppointmentService list
  const apptServices = selectedSvcs.map(s => ({
    serviceId:       s.id,
    serviceName:     s.name,
    price:           s.price,
    durationMinutes: s.durationMinutes,
  }))

  // Generate invoice number
  const today     = format(new Date(), 'yyyyMMdd')
  const invoiceNo = `HB-${today}-${String(nextAppointmentId).padStart(4, '0')}`

  const invoice = {
    invoiceNumber:   invoiceNo,
    appointmentId:   nextAppointmentId,
    clientName:      client!.fullName,
    clientEmail:     client!.email,
    salonName:       salon!.name,
    salonAddress:    salon!.address,
    hairdresserName: hairdresser!.fullName,
    date:            body.startTime,
    items:           apptServices.map(s => ({ name: s.serviceName, price: s.price, duration: s.durationMinutes })),
    totalPrice,
    totalDuration,
    status:          'SENT',
    createdAt:       new Date().toISOString(),
  }

  const newA: Appointment = {
    id:              nextAppointmentId++,
    clientId:        uid!,
    clientName:      client!.fullName,
    clientPhone:     client!.phone,
    hairdresserId:   hairdresser!.id,
    hairdresserName: hairdresser!.fullName,
    // Primary service (first) for backwards compatibility
    serviceId:       selectedSvcs[0].id,
    serviceName:     selectedSvcs[0].name,
    // Full multi-service list
    services:        apptServices,
    salonId:         salon!.id,
    salonName:       salon!.name,
    salonAddress:    salon!.address,
    startTime:       body.startTime,
    endTime:         endDate.toISOString(),
    status:          'PENDING',
    price:           totalPrice,
    notes:           body.notes,
    invoice,
    createdAt:       new Date().toISOString(),
  } as Appointment
  appointments.push(newA)

  // Simulate email send (console log in mock)
  console.info(
    `%c[HairBook Mock] 📧 Račun ${invoiceNo} "poslan" na ${client!.email} — ${selectedSvcs.length} usluga, ukupno €${totalPrice}`,
    'color: #3B82F6; font-weight: bold'
  )

  return ok(newA, 201)
})

route('patch', /^\/appointments\/(\d+)\/status$/, async (_, config, match) => {
  await delay()
  const idx = appointments.findIndex(a => a.id === Number(match[1]))
  if (idx === -1) err('Termin nije pronađen', 404)
  appointments[idx] = { ...appointments[idx], ...parseBody(config) }
  return ok(appointments[idx])
})

route('delete', /^\/appointments\/(\d+)$/, async (_, __, match) => {
  await delay()
  const idx = appointments.findIndex(a => a.id === Number(match[1]))
  if (idx === -1) err('Termin nije pronađen', 404)
  appointments[idx] = { ...appointments[idx], status: 'CANCELLED' }
  return ok({})
})

// ─── REVIEWS ──────────────────────────────────────────────────────────────────

route('get', /^\/salons\/(\d+)\/reviews$/, async (_, __, match) => {
  await delay()
  return ok(
    reviews
      .filter(r => r.salonId === Number(match[1]))
      .sort((a, b) => b.createdAt.localeCompare(a.createdAt))
  )
})

route('post', /^\/reviews$/, async (_, config) => {
  await delay()
  const uid    = currentUserId(config)
  if (!uid) err('Unauthorized', 401)
  const body   = parseBody(config)
  const client = users.find(u => u.id === uid)

  const newR: Review = {
    id:              nextReviewId++,
    clientId:        uid!,
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
  const si  = salons.findIndex(s => s.id === body.salonId)
  if (si !== -1) salons[si] = { ...salons[si], avgRating: Math.round(avg * 10) / 10, reviewCount: salonReviews.length }

  return ok(newR, 201)
})

route('delete', /^\/reviews\/(\d+)$/, async (_, __, match) => {
  await delay()
  reviews = reviews.filter(r => r.id !== Number(match[1]))
  return ok({})
})

// ─── FAVORITES ────────────────────────────────────────────────────────────────

route('get', /^\/favorites$/, async (_, config) => {
  await delay()
  const uid = currentUserId(config)
  if (!uid) err('Unauthorized', 401)
  return ok(favorites.filter(f => f.userId === uid))
})

route('post', /^\/favorites$/, async (_, config) => {
  await delay()
  const uid     = currentUserId(config)
  if (!uid) err('Unauthorized', 401)
  const { salonId } = parseBody(config)
  if (favorites.find(f => f.userId === uid && f.salonId === salonId)) err('Već dodano', 409)
  const salon = salons.find(s => s.id === salonId)
  if (!salon) err('Salon nije pronađen', 404)

  const newF: Favorite = {
    id:        nextFavoriteId++,
    userId:    uid!,
    salonId,
    salon:     { id: salon!.id, name: salon!.name, slug: salon!.slug, city: salon!.city, avgRating: salon!.avgRating, verified: salon!.verified },
    createdAt: new Date().toISOString(),
  }
  favorites.push(newF)
  return ok(newF, 201)
})

route('delete', /^\/favorites\/(\d+)$/, async (_, config, match) => {
  await delay()
  const uid     = currentUserId(config)
  const salonId = Number(match[1])
  favorites = favorites.filter(f => !(f.userId === uid && f.salonId === salonId))
  return ok({})
})

// ─── INIT ─────────────────────────────────────────────────────────────────────

let initialized = false

export function initMockHandlers(axiosInstance: AxiosInstance) {
  if (initialized) return
  initialized = true

  axiosInstance.interceptors.request.use(
    async (config) => {
      const rawUrl = config.url ?? ''
      // Strip baseURL prefix if present (happens when full URL is passed)
      const baseURL = (config.baseURL ?? '').replace(/\/$/, '')
      const url = rawUrl.startsWith(baseURL)
        ? rawUrl.slice(baseURL.length)
        : rawUrl

      const method = (config.method?.toLowerCase() ?? 'get') as Method

      for (const r of routes) {
        if (r.method !== method) continue
        const match = url.match(r.pattern)
        if (!match) continue

        try {
          const { data, status } = await r.handler(url, config, match)
          // Bypass the real HTTP request by setting a custom adapter
          config.adapter = async () => mockResponse(data, status, config)
        } catch (e: any) {
          if (e.__mockError) {
            config.adapter = async () => { throw mockError(e.message, e.status, config) }
          } else {
            config.adapter = async () => { throw e }
          }
        }

        return config
      }

      // No route matched — warn and let real request through
      console.warn(`[MOCK] Unhandled: ${method.toUpperCase()} ${url}`)
      return config
    },
    (error) => Promise.reject(error)
  )

  console.info(
    '%c[HairBook Mock] ✓ Aktiviran — podaci se čitaju iz db.ts',
    'color: #e94560; font-weight: bold; font-size: 13px'
  )
}

// ─── Dev helpers ──────────────────────────────────────────────────────────────
if (typeof window !== 'undefined') {
  ;(window as any).__hairbook = {
    db:    () => ({ users, salons, services, hairdressers, appointments, reviews, favorites }),
    reset: () => {
      users        = [...MOCK_USERS]
      salons       = MOCK_SALONS.map(s => ({ ...s }))
      services     = [...MOCK_SERVICES]
      hairdressers = [...MOCK_HAIRDRESSERS]
      appointments = [...MOCK_APPOINTMENTS]
      reviews      = [...MOCK_REVIEWS]
      favorites    = [...MOCK_FAVORITES]
      console.info('[HairBook Mock] DB resetovan na početne podatke')
    },
  }
}