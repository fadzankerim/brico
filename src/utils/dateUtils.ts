import {
  format,
  formatDistanceToNow,
  isToday,
  isTomorrow,
  isPast,
  parseISO,
  addMinutes,
} from 'date-fns'
import { bs } from 'date-fns/locale'

export function formatDate(date: string | Date, pattern = 'dd.MM.yyyy'): string {
  const d = typeof date === 'string' ? parseISO(date) : date
  return format(d, pattern)
}

export function formatDateTime(date: string | Date): string {
  const d = typeof date === 'string' ? parseISO(date) : date
  return format(d, 'dd.MM.yyyy HH:mm')
}

export function formatTime(date: string | Date): string {
  const d = typeof date === 'string' ? parseISO(date) : date
  return format(d, 'HH:mm')
}

export function formatRelative(date: string | Date): string {
  const d = typeof date === 'string' ? parseISO(date) : date
  if (isToday(d)) return `Danas u ${format(d, 'HH:mm')}`
  if (isTomorrow(d)) return `Sutra u ${format(d, 'HH:mm')}`
  return formatDistanceToNow(d, { addSuffix: true, locale: bs })
}

export function formatDuration(minutes: number): string {
  if (minutes < 60) return `${minutes} min`
  const h = Math.floor(minutes / 60)
  const m = minutes % 60
  return m > 0 ? `${h}h ${m}min` : `${h}h`
}

export function formatPrice(amount: number | undefined | null, currency = 'KM'): string {
  return `${(amount ?? 0).toFixed(2)} ${currency}`
}

export function isAppointmentPast(endTime: string): boolean {
  return isPast(parseISO(endTime))
}

export function calcEndTime(startTime: string, durationMinutes: number): string {
  return format(addMinutes(parseISO(startTime), durationMinutes), "yyyy-MM-dd'T'HH:mm:ss")
}

export const DAY_NAMES = ['Ponedjeljak', 'Utorak', 'Srijeda', 'Četvrtak', 'Petak', 'Subota', 'Nedjelja']
export const DAY_NAMES_SHORT = ['Pon', 'Uto', 'Sri', 'Čet', 'Pet', 'Sub', 'Ned']