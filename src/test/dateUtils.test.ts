import { describe, it, expect } from 'vitest'
import {
  formatDate,
  formatTime,
  formatDateTime,
  formatDuration,
  formatPrice,
  calcEndTime,
} from '../utils/dateUtils'

describe('formatDate', () => {
  it('formats ISO string to dd.MM.yyyy', () => {
    expect(formatDate('2024-06-15T10:30:00')).toBe('15.06.2024')
  })

  it('accepts a Date object', () => {
    expect(formatDate(new Date(2024, 0, 5))).toBe('05.01.2024')
  })

  it('uses custom pattern', () => {
    expect(formatDate('2024-12-01T00:00:00', 'yyyy/MM/dd')).toBe('2024/12/01')
  })
})

describe('formatTime', () => {
  it('returns HH:mm from ISO string', () => {
    expect(formatTime('2024-06-15T09:05:00')).toBe('09:05')
  })

  it('handles midnight', () => {
    expect(formatTime('2024-01-01T00:00:00')).toBe('00:00')
  })
})

describe('formatDateTime', () => {
  it('combines date and time', () => {
    expect(formatDateTime('2024-06-15T14:30:00')).toBe('15.06.2024 14:30')
  })
})

describe('formatDuration', () => {
  it('shows minutes for less than 1 hour', () => {
    expect(formatDuration(45)).toBe('45 min')
  })

  it('shows hours only when no remainder', () => {
    expect(formatDuration(60)).toBe('1h')
    expect(formatDuration(120)).toBe('2h')
  })

  it('shows hours and minutes', () => {
    expect(formatDuration(90)).toBe('1h 30min')
    expect(formatDuration(75)).toBe('1h 15min')
  })
})

describe('formatPrice', () => {
  it('formats number with 2 decimals and currency', () => {
    expect(formatPrice(25)).toBe('25.00 KM')
    expect(formatPrice(12.5)).toBe('12.50 KM')
  })

  it('handles null and undefined as 0', () => {
    expect(formatPrice(null)).toBe('0.00 KM')
    expect(formatPrice(undefined)).toBe('0.00 KM')
  })

  it('uses custom currency', () => {
    expect(formatPrice(10, 'EUR')).toBe('10.00 EUR')
  })
})

describe('calcEndTime', () => {
  it('adds duration minutes to start time', () => {
    expect(calcEndTime('2024-06-15T10:00:00', 30)).toBe('2024-06-15T10:30:00')
    expect(calcEndTime('2024-06-15T10:00:00', 90)).toBe('2024-06-15T11:30:00')
  })

  it('handles overflow across hours', () => {
    expect(calcEndTime('2024-06-15T23:45:00', 30)).toBe('2024-06-16T00:15:00')
  })
})
