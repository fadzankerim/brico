import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import AppointmentStatusBadge from '../components/AppointmentStatusBadge'

describe('AppointmentStatusBadge', () => {
  it('renders PENDING label', () => {
    render(<AppointmentStatusBadge status="PENDING" />)
    expect(screen.getByText('Čeka na potvrdu')).toBeInTheDocument()
  })

  it('renders CONFIRMED label', () => {
    render(<AppointmentStatusBadge status="CONFIRMED" />)
    expect(screen.getByText('Potvrđeno')).toBeInTheDocument()
  })

  it('renders COMPLETED label', () => {
    render(<AppointmentStatusBadge status="COMPLETED" />)
    expect(screen.getByText('Završeno')).toBeInTheDocument()
  })

  it('renders CANCELLED label', () => {
    render(<AppointmentStatusBadge status="CANCELLED" />)
    expect(screen.getByText('Otkazano')).toBeInTheDocument()
  })

  it('renders NO_SHOW label', () => {
    render(<AppointmentStatusBadge status="NO_SHOW" />)
    expect(screen.getByText('Nije se pojavio')).toBeInTheDocument()
  })

  it('applies correct class for CONFIRMED status', () => {
    const { container } = render(<AppointmentStatusBadge status="CONFIRMED" />)
    const badge = container.firstChild as HTMLElement
    expect(badge.className).toContain('emerald')
  })

  it('applies correct class for CANCELLED status', () => {
    const { container } = render(<AppointmentStatusBadge status="CANCELLED" />)
    const badge = container.firstChild as HTMLElement
    expect(badge.className).toContain('rose')
  })
})
