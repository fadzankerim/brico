import { describe, it, expect, vi } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import StarRating from '../components/StarRating'

describe('StarRating', () => {
  it('renders 5 star buttons', () => {
    render(<StarRating value={0} />)
    expect(screen.getAllByRole('button')).toHaveLength(5)
  })

  it('calls onChange with correct star value on click', () => {
    const onChange = vi.fn()
    render(<StarRating value={0} onChange={onChange} />)
    const buttons = screen.getAllByRole('button')
    fireEvent.click(buttons[2]) // 3rd star = value 3
    expect(onChange).toHaveBeenCalledWith(3)
  })

  it('calls onChange with 1 when first star clicked', () => {
    const onChange = vi.fn()
    render(<StarRating value={0} onChange={onChange} />)
    fireEvent.click(screen.getAllByRole('button')[0])
    expect(onChange).toHaveBeenCalledWith(1)
  })

  it('calls onChange with 5 when last star clicked', () => {
    const onChange = vi.fn()
    render(<StarRating value={0} onChange={onChange} />)
    fireEvent.click(screen.getAllByRole('button')[4])
    expect(onChange).toHaveBeenCalledWith(5)
  })

  it('disables all buttons in readonly mode', () => {
    render(<StarRating value={3} readonly />)
    screen.getAllByRole('button').forEach((btn: HTMLElement) => {
      expect(btn).toBeDisabled()
    })
  })

  it('shows label when showLabel is true and value > 0', () => {
    render(<StarRating value={5} showLabel />)
    expect(screen.getByText('Izvrsno')).toBeInTheDocument()
  })

  it('does not show label when value is 0', () => {
    render(<StarRating value={0} showLabel />)
    expect(screen.queryByText('Izvrsno')).not.toBeInTheDocument()
  })

  it('does not call onChange in readonly mode', () => {
    const onChange = vi.fn()
    render(<StarRating value={3} readonly onChange={onChange} />)
    fireEvent.click(screen.getAllByRole('button')[0])
    expect(onChange).not.toHaveBeenCalled()
  })
})
