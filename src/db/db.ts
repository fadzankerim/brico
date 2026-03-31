/**
 * HAIRBOOK MOCK DATABASE
 * ──────────────────────
 * Ovo je privremena in-memory baza koja simulira backend.
 * Svi servisi čitaju i pišu u ovaj fajl dok backend nije spreman.
 *
 * Struktura prati 1:1 baze podataka iz tehničke dokumentacije.
 * Zamijeni sa pravim API pozivima kada Spring Boot backend bude gotov.
 */


import { addDays, addMinutes, format, subDays } from 'date-fns'
import type { User } from '../types/user.types'
import type { Hairdresser, Salon, Service, WorkingHours } from '../types/salon.typs'
import type { Appointment } from '../types/booking.types'
import type { Favorite, Review } from '../types/review.types'

// ─── Helpers ────────────────────────────────────────────────────────────────
function iso(date: Date) {
  return date.toISOString()
}
function pastIso(daysAgo: number, hour: number, minute = 0) {
  const d = subDays(new Date(), daysAgo)
  d.setHours(hour, minute, 0, 0)
  return iso(d)
}
function futureIso(daysAhead: number, hour: number, minute = 0) {
  const d = addDays(new Date(), daysAhead)
  d.setHours(hour, minute, 0, 0)
  return iso(d)
}
function endIso(startIso: string, durationMinutes: number) {
  return iso(addMinutes(new Date(startIso), durationMinutes))
}

// ─── USERS ──────────────────────────────────────────────────────────────────
export const MOCK_USERS: User[] = [
  {
    id: 1,
    email: 'vlasnik@hairbook.ba',
    fullName: 'Edin Hadžić',
    phone: '+387 61 111 222',
    role: 'SALON_OWNER',
    emailVerified: true,
    createdAt: '2025-01-10T10:00:00Z',
  },
  {
    id: 2,
    email: 'amina@hairbook.ba',
    fullName: 'Amina Hodžić',
    phone: '+387 61 222 333',
    role: 'HAIRDRESSER',
    emailVerified: true,
    createdAt: '2025-01-15T10:00:00Z',
  },
  {
    id: 3,
    email: 'kenan@hairbook.ba',
    fullName: 'Kenan Begović',
    phone: '+387 61 333 444',
    role: 'HAIRDRESSER',
    emailVerified: true,
    createdAt: '2025-02-01T10:00:00Z',
  },
  {
    id: 4,
    email: 'sara@hairbook.ba',
    fullName: 'Sara Kovačević',
    phone: '+387 61 444 555',
    role: 'HAIRDRESSER',
    emailVerified: true,
    createdAt: '2025-03-01T10:00:00Z',
  },
  {
    id: 5,
    email: 'klijent@hairbook.ba',
    fullName: 'Maja Perić',
    phone: '+387 61 555 666',
    role: 'CLIENT',
    emailVerified: true,
    createdAt: '2025-06-01T10:00:00Z',
  },
  {
    id: 6,
    email: 'klijent2@hairbook.ba',
    fullName: 'Lejla Babić',
    phone: '+387 61 666 777',
    role: 'CLIENT',
    emailVerified: true,
    createdAt: '2025-07-15T10:00:00Z',
  },
  {
    id: 7,
    email: 'klijent3@hairbook.ba',
    fullName: 'Emir Softić',
    phone: '+387 61 777 888',
    role: 'CLIENT',
    emailVerified: true,
    createdAt: '2025-08-20T10:00:00Z',
  },
]

// ─── WORKING HOURS ──────────────────────────────────────────────────────────
const STD_HOURS: WorkingHours[] = [
  { id: 1, dayOfWeek: 0, startTime: '09:00', endTime: '18:00', isDayOff: false },
  { id: 2, dayOfWeek: 1, startTime: '09:00', endTime: '18:00', isDayOff: false },
  { id: 3, dayOfWeek: 2, startTime: '09:00', endTime: '18:00', isDayOff: false },
  { id: 4, dayOfWeek: 3, startTime: '09:00', endTime: '18:00', isDayOff: false },
  { id: 5, dayOfWeek: 4, startTime: '09:00', endTime: '17:00', isDayOff: false },
  { id: 6, dayOfWeek: 5, startTime: '09:00', endTime: '14:00', isDayOff: false },
  { id: 7, dayOfWeek: 6, startTime: '00:00', endTime: '00:00', isDayOff: true  },
]

// ─── SALONS ─────────────────────────────────────────────────────────────────
export const MOCK_SALONS: Salon[] = [
  {
    id: 1,
    slug: 'elite-cut-sarajevo',
    name: 'Elite Cut',
    description: 'Premium frizerski salon u centru Sarajeva. Specijalizovani za moderne muške i ženske frizure, balayage tehnike i tretmane njege kose. Tim od 3 iskusna frizera sa više od 10 godina iskustva.',
    city: 'Sarajevo',
    address: 'Ferhadija 12',
    latitude: 43.8563,
    longitude: 18.4131,
    phone: '+387 33 123 456',
    website: 'https://elitecut.ba',
    avgRating: 4.9,
    reviewCount: 213,
    verified: true,
    isActive: true,
    photos: [
      { id: 1, url: 'https://images.unsplash.com/photo-1560066984-138dadb4c035?w=800', isPrimary: true  },
      { id: 2, url: 'https://images.unsplash.com/photo-1521590832167-7bcbfaa6381f?w=400', isPrimary: false },
      { id: 3, url: 'https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?w=400', isPrimary: false },
    ],
    workingHours: STD_HOURS,
    createdAt: '2025-01-10T10:00:00Z',
  },
  {
    id: 2,
    slug: 'barber-king-mostar',
    name: 'Barber King',
    description: 'Autentični barbershop u Mostaru. Klasični muški rezovi, brijanje ravnom britvom, tretmani brade. Opuštena atmosfera uz vrhunsku uslugu.',
    city: 'Mostar',
    address: 'Španskih boraca 8',
    latitude: 43.3438,
    longitude: 17.8078,
    phone: '+387 36 456 789',
    website: undefined,
    avgRating: 4.7,
    reviewCount: 148,
    verified: true,
    isActive: true,
    photos: [
      { id: 4, url: 'https://images.unsplash.com/photo-1503951914875-452162b0f3f1?w=800', isPrimary: true  },
      { id: 5, url: 'https://images.unsplash.com/photo-1599351431202-1e0f0137899a?w=400', isPrimary: false },
    ],
    workingHours: STD_HOURS,
    createdAt: '2025-02-15T10:00:00Z',
  },
  {
    id: 3,
    slug: 'style-lab-banja-luka',
    name: 'Style Lab',
    description: 'Moderni salon za kosu u Banja Luci. Specijalizovani za koloristiku, keratinski tretmani i styling za posebne prilike.',
    city: 'Banja Luka',
    address: 'Kralja Petra I 45',
    latitude: 44.7722,
    longitude: 17.1910,
    phone: '+387 51 234 567',
    website: 'https://stylelab.ba',
    avgRating: 4.8,
    reviewCount: 97,
    verified: false,
    isActive: true,
    photos: [
      { id: 6, url: 'https://images.unsplash.com/photo-1562322140-8baeececf3df?w=800', isPrimary: true },
    ],
    workingHours: STD_HOURS,
    createdAt: '2025-03-01T10:00:00Z',
  },
  {
    id: 4,
    slug: 'hair-studio-tuzla',
    name: 'Hair Studio Tuzla',
    description: 'Kompletan salon za njegu i uljepšavanje kose u Tuzli.',
    city: 'Tuzla',
    address: 'Bosne Srebrene 22',
    latitude: 44.5384,
    longitude: 18.6730,
    phone: '+387 35 345 678',
    website: undefined,
    avgRating: 4.5,
    reviewCount: 64,
    verified: false,
    isActive: true,
    photos: [
      { id: 7, url: 'https://images.unsplash.com/photo-1595476108010-b4d1f102b1b1?w=800', isPrimary: true },
    ],
    workingHours: STD_HOURS,
    createdAt: '2025-04-01T10:00:00Z',
  },
  {
    id: 5,
    slug: 'zen-hair-sarajevo',
    name: 'Zen Hair',
    description: 'Mirno i opuštajuće okruženje za kompletnu njegu kose. Organski tretmani, bez štetnih hemikalija.',
    city: 'Sarajevo',
    address: 'Titova 28',
    latitude: 43.8480,
    longitude: 18.3990,
    phone: '+387 33 987 654',
    website: undefined,
    avgRating: 4.6,
    reviewCount: 81,
    verified: true,
    isActive: true,
    photos: [
      { id: 8, url: 'https://images.unsplash.com/photo-1470259078422-826894b933aa?w=800', isPrimary: true },
    ],
    workingHours: STD_HOURS,
    createdAt: '2025-05-01T10:00:00Z',
  },
]

// ─── SERVICES ────────────────────────────────────────────────────────────────
export const MOCK_SERVICES: Service[] = [
  // Salon 1 - Elite Cut
  { id: 1,  salonId: 1, name: 'Žensko šišanje',       description: 'Šišanje + pranje + fen styling',      price: 35,  durationMinutes: 60,  isActive: true  },
  { id: 2,  salonId: 1, name: 'Muško šišanje',         description: 'Klasično muško šišanje i styling',    price: 20,  durationMinutes: 30,  isActive: true  },
  { id: 3,  salonId: 1, name: 'Balayage',              description: 'Sunčani efekt, prirodan prijelaz',    price: 120, durationMinutes: 180, isActive: true  },
  { id: 4,  salonId: 1, name: 'Bojenje kose',          description: 'Jednobojno bojenje s maskom',        price: 60,  durationMinutes: 90,  isActive: true  },
  { id: 5,  salonId: 1, name: 'Keratinski tretman',    description: 'Zaglađivanje i njega oštećene kose', price: 90,  durationMinutes: 120, isActive: true  },
  { id: 6,  salonId: 1, name: 'Pranje i fen',          description: 'Pranje sa maskom + fen styling',     price: 18,  durationMinutes: 30,  isActive: true  },
  { id: 7,  salonId: 1, name: 'Dječije šišanje',       description: 'Za djecu do 12 godina',              price: 12,  durationMinutes: 25,  isActive: true  },
  // Salon 2 - Barber King
  { id: 8,  salonId: 2, name: 'Klasični rezov',        description: 'Šišanje + styling',                  price: 15,  durationMinutes: 30,  isActive: true  },
  { id: 9,  salonId: 2, name: 'Brijanje',              description: 'Brijanje ravnom britvom + tretman',  price: 18,  durationMinutes: 30,  isActive: true  },
  { id: 10, salonId: 2, name: 'Šišanje + brijanje',    description: 'Komplet paket',                      price: 28,  durationMinutes: 55,  isActive: true  },
  { id: 11, salonId: 2, name: 'Brada styling',         description: 'Oblikovanje i njegovanje brade',     price: 12,  durationMinutes: 20,  isActive: true  },
  { id: 12, salonId: 2, name: 'Dječije šišanje',       description: 'Za djecu do 12 godina',              price: 10,  durationMinutes: 20,  isActive: true  },
  // Salon 3 - Style Lab
  { id: 13, salonId: 3, name: 'Ombre/Sombre',          description: 'Gradient efekt boje',                price: 110, durationMinutes: 150, isActive: true  },
  { id: 14, salonId: 3, name: 'Keratin glatko',        description: 'Brazilski keratin tretman',          price: 100, durationMinutes: 120, isActive: true  },
  { id: 15, salonId: 3, name: 'Žensko šišanje',        description: 'Rez + pranje + styling',             price: 30,  durationMinutes: 50,  isActive: true  },
  { id: 16, salonId: 3, name: 'Frizura za event',      description: 'Styling za vjenčanja i proslave',    price: 75,  durationMinutes: 90,  isActive: true  },
]

// ─── HAIRDRESSERS ────────────────────────────────────────────────────────────
export const MOCK_HAIRDRESSERS: Hairdresser[] = [
  {
    id: 1, userId: 2, salonId: 1,
    fullName:   'Amina Hodžić',
    bio:        'Senior stilist s 8 godina iskustva. Specijalist za balayage i koloristiku. Pohađala Master Class u Milanu 2023.',
    specialties: 'Balayage, Koloristika, Keratinski tretmani',
    profilePhoto: undefined,
    isActive:   true,
    avgRating:  4.9,
    services:   MOCK_SERVICES.filter(s => s.salonId === 1),
  },
  {
    id: 2, userId: 3, salonId: 1,
    fullName:   'Kenan Begović',
    bio:        'Muški i ženski rezovi. Poseban fokus na moderne muške frizure i fade tehnike.',
    specialties: 'Fade, Muški rezovi, Brada',
    profilePhoto: undefined,
    isActive:   true,
    avgRating:  4.7,
    services:   MOCK_SERVICES.filter(s => s.salonId === 1),
  },
  {
    id: 3, userId: 4, salonId: 1,
    fullName:   'Sara Kovačević',
    bio:        'Junior stilist. Entuzijastična i kreativna, specijalizovana za boje i moderni styling.',
    specialties: 'Bojenje, Styling, Pletenje',
    profilePhoto: undefined,
    isActive:   false,
    avgRating:  4.5,
    services:   MOCK_SERVICES.filter(s => s.salonId === 1),
  },
  {
    id: 4, userId: 2, salonId: 2,
    fullName:   'Muamer Čović',
    bio:        'Barbershop veteran s 12 godina iskustva. Klasični i moderni muški rezovi.',
    specialties: 'Brijanje, Klasični rezovi, Brada oblikovanje',
    profilePhoto: undefined,
    isActive:   true,
    avgRating:  4.8,
    services:   MOCK_SERVICES.filter(s => s.salonId === 2),
  },
]

// ─── APPOINTMENTS ─────────────────────────────────────────────────────────────
const A = (
  id: number,
  clientId: number,
  clientName: string,
  hairdresserId: number,
  hairdresserName: string,
  serviceId: number,
  serviceName: string,
  salonId: number,
  startIso: string,
  durationMinutes: number,
  price: number,
  status: Appointment['status'],
  notes?: string,
): Appointment => ({
  id,
  clientId,
  clientName,
  hairdresserId,
  hairdresserName,
  services: [{ serviceId, serviceName, price, durationMinutes }],
  serviceId,
  serviceName,
  salonId,
  salonName: MOCK_SALONS.find(s => s.id === salonId)?.name ?? '',
  salonAddress: MOCK_SALONS.find(s => s.id === salonId)?.address ?? '',
  startTime: startIso,
  endTime:   endIso(startIso, durationMinutes),
  status,
  price,
  notes,
  createdAt: subDays(new Date(startIso), 3).toISOString(),
})

export const MOCK_APPOINTMENTS: Appointment[] = [
  // Prošli termini — klijent 5 (Maja)
  A(1,  5, 'Maja Perić',   1, 'Amina Hodžić',   1, 'Žensko šišanje',    1, pastIso(20, 10),       60, 35,  'COMPLETED'),
  A(2,  5, 'Maja Perić',   2, 'Kenan Begović',   4, 'Bojenje kose',      1, pastIso(12, 14),       90, 60,  'COMPLETED', 'Ton boje: 7.1 pepeljasto'),
  A(3,  5, 'Maja Perić',   1, 'Amina Hodžić',   3, 'Balayage',          1, pastIso(5, 11),       180, 120, 'COMPLETED'),
  A(4,  5, 'Maja Perić',   2, 'Kenan Begović',   2, 'Muško šišanje',     1, pastIso(2, 16),        30, 20,  'CANCELLED'),
  // Nadolazeći — klijent 5
  A(5,  5, 'Maja Perić',   1, 'Amina Hodžić',   5, 'Keratinski tretman',1, futureIso(3, 10),     120, 90,  'CONFIRMED'),
  A(6,  5, 'Maja Perić',   1, 'Amina Hodžić',   1, 'Žensko šišanje',    1, futureIso(14, 14),     60, 35,  'PENDING'),
  // Klijent 6 (Lejla)
  A(7,  6, 'Lejla Babić',  2, 'Kenan Begović',   1, 'Žensko šišanje',    1, pastIso(8, 13),        60, 35,  'COMPLETED'),
  A(8,  6, 'Lejla Babić',  1, 'Amina Hodžić',   3, 'Balayage',          1, futureIso(7, 11),     180, 120, 'CONFIRMED'),
  // Klijent 7 (Emir)
  A(9,  7, 'Emir Softić',  2, 'Kenan Begović',   2, 'Muško šišanje',     1, pastIso(3, 15),        30, 20,  'COMPLETED'),
  A(10, 7, 'Emir Softić',  2, 'Kenan Begović',   2, 'Muško šišanje',     1, futureIso(1, 15, 30),  30, 20,  'CONFIRMED'),
  // Danas — za Owner kalendar prikaz
  A(11, 5, 'Maja Perić',   1, 'Amina Hodžić',   6, 'Pranje i fen',      1, futureIso(0, 9),       30, 18,  'CONFIRMED'),
  A(12, 6, 'Lejla Babić',  2, 'Kenan Begović',   2, 'Muško šišanje',     1, futureIso(0, 10),      30, 20,  'CONFIRMED'),
  A(13, 7, 'Emir Softić',  1, 'Amina Hodžić',   4, 'Bojenje kose',      1, futureIso(0, 11),      90, 60,  'PENDING',   'Prva posjeta'),
  A(14, 5, 'Maja Perić',   3, 'Sara Kovačević',  1, 'Žensko šišanje',    1, futureIso(0, 13),      60, 35,  'CONFIRMED'),
  A(15, 6, 'Lejla Babić',  1, 'Amina Hodžić',   5, 'Keratinski tretman',1, futureIso(0, 14, 30), 120, 90,  'CONFIRMED'),
  // Sutra
  A(16, 7, 'Emir Softić',  2, 'Kenan Begović',   2, 'Muško šišanje',     1, futureIso(1, 9),       30, 20,  'CONFIRMED'),
  A(17, 5, 'Maja Perić',   1, 'Amina Hodžić',   3, 'Balayage',          1, futureIso(1, 10),     180, 120, 'PENDING'),
  A(18, 6, 'Lejla Babić',  3, 'Sara Kovačević',  6, 'Pranje i fen',      1, futureIso(1, 14),      30, 18,  'CONFIRMED'),
]

// ─── REVIEWS ─────────────────────────────────────────────────────────────────
export const MOCK_REVIEWS: Review[] = [
  { id: 1, clientId: 5, clientName: 'Maja P.',  salonId: 1, hairdresserId: 1, hairdresserName: 'Amina Hodžić',  appointmentId: 1, rating: 5, comment: 'Amina je nevjerovatna! Balayage je ispao savršeno, tačno onako kako sam zamišljala. Definitely ću se vratiti!', createdAt: pastIso(19, 12) },
  { id: 2, clientId: 6, clientName: 'Lejla B.', salonId: 1, hairdresserId: 2, hairdresserName: 'Kenan Begović', appointmentId: 7, rating: 5, comment: 'Profesionalan pristup, čist salon, brza usluga. Preporučujem svima!', createdAt: pastIso(7, 14) },
  { id: 3, clientId: 7, clientName: 'Emir S.',  salonId: 1, hairdresserId: 2, hairdresserName: 'Kenan Begović', appointmentId: 9, rating: 4, comment: 'Dobro šišanje, ugodna atmosfera. Malo duže čekanje nego što je planirano.', createdAt: pastIso(2, 16) },
  { id: 4, clientId: 5, clientName: 'Maja P.',  salonId: 1, hairdresserId: 1, hairdresserName: 'Amina Hodžić',  appointmentId: 3, rating: 5, comment: 'Treća posjeta za redom i svaki put oduševljena! Amina zna što radi.', createdAt: pastIso(4, 10) },
  { id: 5, clientId: 5, clientName: 'Maja P.',  salonId: 2, hairdresserId: 4, hairdresserName: 'Muamer Čović',  appointmentId: undefined, rating: 5, comment: 'Pravi barbershop doživljaj! Brijanje britvom je iskustvo za sebe.', createdAt: pastIso(15, 11) },
]

// ─── FAVORITES ───────────────────────────────────────────────────────────────
export const MOCK_FAVORITES: Favorite[] = [
  {
    id: 1, userId: 5, salonId: 1,
    salon: { id: 1, name: 'Elite Cut', slug: 'elite-cut-sarajevo', city: 'Sarajevo', avgRating: 4.9, verified: true },
    createdAt: pastIso(30, 10),
  },
  {
    id: 2, userId: 5, salonId: 2,
    salon: { id: 2, name: 'Barber King', slug: 'barber-king-mostar', city: 'Mostar', avgRating: 4.7, verified: true },
    createdAt: pastIso(25, 10),
  },
  {
    id: 3, userId: 6, salonId: 1,
    salon: { id: 1, name: 'Elite Cut', slug: 'elite-cut-sarajevo', city: 'Sarajevo', avgRating: 4.9, verified: true },
    createdAt: pastIso(10, 10),
  },
]

// ─── AVAILABILITY GENERATOR ──────────────────────────────────────────────────
/**
 * Dinamički generiše slobodne termine za frizera na određeni datum.
 * Uzima u obzir postojeće termine i vraća listu dostupnih slotova.
 */
export function generateAvailability(
  hairdresserId: number,
  date: string,
  totalDuration = 30,   // minutes — sum of all selected services
) {
  const allSlots = [
    '09:00','09:30','10:00','10:30','11:00','11:30',
    '12:00','12:30','13:00','13:30','14:00','14:30',
    '15:00','15:30','16:00','16:30','17:00','17:30',
  ]

  // Busy windows for this hairdresser on this date
  const busyWindows = MOCK_APPOINTMENTS
    .filter(a =>
      a.hairdresserId === hairdresserId &&
      a.startTime.startsWith(date) &&
      a.status !== 'CANCELLED'
    )
    .map(a => ({
      start: new Date(a.startTime).getTime(),
      end:   new Date(a.endTime).getTime(),
    }))

  return allSlots.map(time => {
    const [h, m]   = time.split(':').map(Number)
    const slotStart = new Date(`${date}T${time}:00`).getTime()
    const slotEnd   = slotStart + totalDuration * 60_000

    // Slot end must be before closing time (18:00)
    const closingTime = new Date(`${date}T18:00:00`).getTime()
    if (slotEnd > closingTime) return { time, available: false }

    // Check no busy window overlaps with [slotStart, slotEnd)
    const overlaps = busyWindows.some(w => slotStart < w.end && slotEnd > w.start)

    return { time, available: !overlaps }
  })
}

// ─── SALON SEARCH HELPER ─────────────────────────────────────────────────────
export function searchSalons(params: {
  city?: string
  minRating?: number
  maxPrice?: number
  sortBy?: string
  verified?: boolean
  page?: number
  size?: number
}) {
  let results = [...MOCK_SALONS]

  // Attach services and hairdressers
  results = results.map(s => ({
    ...s,
    services:     MOCK_SERVICES.filter(sv => sv.salonId === s.id),
    hairdressers: MOCK_HAIRDRESSERS.filter(h => h.salonId === s.id),
  }))

  // Filter
  if (params.city) {
    const q = params.city.toLowerCase()
    results = results.filter(s => s.city.toLowerCase().includes(q) || s.name.toLowerCase().includes(q))
  }
  if (params.minRating) {
    results = results.filter(s => s.avgRating >= params.minRating!)
  }
  if (params.verified) {
    results = results.filter(s => s.verified)
  }
  if (params.maxPrice) {
    results = results.filter(s => {
      const min = Math.min(...(MOCK_SERVICES.filter(sv => sv.salonId === s.id).map(sv => sv.price)))
      return min <= params.maxPrice!
    })
  }

  // Sort
  switch (params.sortBy) {
    case 'rating':     results.sort((a, b) => b.avgRating - a.avgRating);  break
    case 'popularity': results.sort((a, b) => b.reviewCount - a.reviewCount); break
    case 'price_asc':  results.sort((a, b) => {
      const aMin = Math.min(...MOCK_SERVICES.filter(s => s.salonId === a.id).map(s => s.price))
      const bMin = Math.min(...MOCK_SERVICES.filter(s => s.salonId === b.id).map(s => s.price))
      return aMin - bMin
    }); break
    case 'price_desc': results.sort((a, b) => {
      const aMin = Math.min(...MOCK_SERVICES.filter(s => s.salonId === a.id).map(s => s.price))
      const bMin = Math.min(...MOCK_SERVICES.filter(s => s.salonId === b.id).map(s => s.price))
      return bMin - aMin
    }); break
  }

  // Paginate
  const page = params.page ?? 0
  const size = params.size ?? 12
  const total = results.length
  const paged = results.slice(page * size, (page + 1) * size)

  return {
    content:       paged,
    totalElements: total,
    totalPages:    Math.ceil(total / size),
    currentPage:   page,
    size,
  }
}