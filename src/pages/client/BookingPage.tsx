import { useEffect, useState } from 'react'
import { useParams, useSearchParams, useNavigate, Link } from 'react-router-dom'
import { motion, AnimatePresence } from 'motion/react'
import {
  ArrowLeft, ArrowRight, CheckCircle,
  Scissors, Calendar, Clock, User,
  ShoppingCart,
  Minus,
  Receipt,
  Mail,
  Plus,
} from 'lucide-react'

import { addDays, format, formatDuration, isSameDay, parseISO } from 'date-fns'
import { useBookingStore } from '../../store/bookingStore'
import { useAuthStore } from '../../store/authStore'
import { useSalonById } from '../../hooks/useSalons'
import { useAvailability, useCreateAppointment } from '../../hooks/useBooking'
import { cn } from '../../lib/utils'
import { formatDate, formatPrice } from '../../utils/dateUtils'
import type { Appointment } from '../../types/booking.types'
import BookingSuccessScreen from './BookingSuccessScreen'

const STEPS = ['Frizer', 'Usluga', 'Termin', 'Potvrda']

export default function BookingPage() {
  const { salonId }        = useParams<{ salonId: string }>()
  const [searchParams]     = useSearchParams()
  const navigate           = useNavigate()

  const {
    step, data, nextStep, prevStep,
    setData, reset, getTotalPrice, getTotalDuration,
  } = useBookingStore()

  const { user } = useAuthStore()
  const { data: salon, isLoading } = useSalonById(salonId)
  const createAppointment = useCreateAppointment()

  const [completedAppt, setCompleted] = useState<Appointment | null>(null)

  // Pre select hairdresser 
  useEffect(() => {
    if (!salon) return
    setData({ salonId: salon.id, salonName: salon.name })

    const hid = searchParams.get('hairdresser')
    if (hid && salon.hairdressers) {
      const h = salon.hairdressers.find(h => h.id === Number(hid))
      if (h) setData({ selectedHairdresser: { id: h.id, name: h.fullName, photo: h.profilePhoto } })
    }
  }, [salon])

  useEffect(() => () => reset(), [])

  function handleConfirm() {
    if (!data.selectedHairdresser || !data.selectedServices.length || !data.selectedDate || !data.selectedTime || !user || !salon) return

    const totalDur = getTotalDuration()
    const startTime = `${data.selectedDate}T${data.selectedTime}:00`

    createAppointment.mutate(
      {
        clientId:        user.id,
        clientName:      user.fullName,
        clientPhone:     user.phone,
        hairdresserId:   data.selectedHairdresser.id,
        hairdresserName: data.selectedHairdresser.name,
        salonId:         salon.id,
        salonName:       salon.name,
        salonAddress:    salon.address,
        startTime,
        notes:           data.notes,
        items: data.selectedServices.map(s => ({
          serviceId:       s.id,
          serviceName:     s.name,
          price:           s.price,
          durationMinutes: s.duration,
        })),
      } as any,
      { onSuccess: (appt) => setCompleted(appt) }
    )
  }

  const canAdvance =
    (step === 1 && !!data.selectedHairdresser) ||
    (step === 2 && data.selectedServices.length > 0)     ||
    (step === 3 && !!data.selectedDate && !!data.selectedTime)

  if (completedAppt) {
    return <BookingSuccessScreen appointment={completedAppt} onDone={() => navigate('/dashboard')} />
  }

  if (isLoading || !salon) {
    return (
      <div className="min-h-screen bg-[#080C14] flex items-center justify-center">
        <div className="w-8 h-8 border-2 border-rose-500 border-t-transparent rounded-full animate-spin" />
      </div>
    )
  }


  return (
    <div className="min-h-screen bg-[#080C14] text-white">
      {/* Header */}
      <div className="sticky top-0 z-20 bg-[#080C14]/95 backdrop-blur-xl border-b border-white/5">
        <div className="max-w-2xl mx-auto px-4 h-14 flex items-center gap-4">
          <button
            onClick={() => step === 1 ? navigate(-1) : prevStep()}
            className="w-8 h-8 flex items-center justify-center rounded-xl text-slate-400 hover:text-white hover:bg-white/8 transition-colors"
          >
            <ArrowLeft className="w-4 h-4" />
          </button>
          <div className="flex-1 min-w-0">
            <p className="text-xs text-slate-500 truncate">
              Rezervacija · <span className="text-slate-300">{salon.name}</span>
            </p>
          </div>
          {/* Cart chip */}
          {data.selectedServices.length > 0 && (
            <div className="flex items-center gap-1.5 px-2.5 py-1 rounded-xl bg-rose-500/10 border border-rose-500/20">
              <ShoppingCart className="w-3.5 h-3.5 text-rose-400" />
              <span className="text-xs font-semibold text-rose-400">
                {data.selectedServices.length} · {formatPrice(getTotalPrice())}
              </span>
            </div>
          )}
          <span className="text-xs text-slate-500 shrink-0">{step}/4</span>
        </div>
 
        {/* Progress bar */}
        <div className="max-w-2xl mx-auto px-4 pb-3">
          <div className="flex items-center gap-2">
            {STEPS.map((label, i) => (
              <div key={label} className="flex-1 flex flex-col items-center gap-1">
                <div className={cn(
                  'h-1 w-full rounded-full transition-all duration-300',
                  i + 1 < step   ? 'bg-rose-500'    :
                  i + 1 === step ? 'bg-rose-500/60' : 'bg-white/10'
                )} />
                <span className={cn('text-[10px] transition-colors',
                  i + 1 === step ? 'text-rose-400' : 'text-slate-600'
                )}>{label}</span>
              </div>
            ))}
          </div>
        </div>
      </div>
 
      {/* Step content */}
      <div className="max-w-2xl mx-auto px-4 py-6">
        <AnimatePresence mode="wait">
          <motion.div
            key={step}
            initial={{ opacity: 0, x: 16 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -16 }}
            transition={{ duration: 0.18 }}
          >
            {step === 1 && <StepHairdresser salon={salon} />}
            {step === 2 && <StepServices    salon={salon} />}
            {step === 3 && <StepDateTime />}
            {step === 4 && <StepConfirm salon={salon} onConfirm={handleConfirm} isPending={createAppointment.isPending} />}
          </motion.div>
        </AnimatePresence>
      </div>
 
      {/* Bottom nav */}
      {step < 4 && (
        <div className="fixed bottom-0 inset-x-0 z-20 bg-[#080C14]/95 backdrop-blur-xl border-t border-white/5">
          <div className="max-w-2xl mx-auto p-4">
            {step >= 2 && data.selectedServices.length > 0 && (
              <div className="flex items-center justify-between mb-3 px-1">
                <span className="text-xs text-slate-400">
                  {data.selectedServices.length} {data.selectedServices.length === 1 ? 'usluga' : 'usluge'} · {formatDuration({hours: Math.floor(getTotalDuration() / 60), minutes: getTotalDuration() % 60})}
                </span>
                <span className="text-sm font-bold text-white">{formatPrice(getTotalPrice())}</span>
              </div>
            )}
            <button
              onClick={nextStep}
              disabled={!canAdvance}
              className="w-full flex items-center justify-center gap-2 py-3.5 rounded-xl bg-rose-500 hover:bg-rose-600 disabled:opacity-30 disabled:cursor-not-allowed text-white font-semibold transition-colors shadow-lg shadow-rose-500/20"
            >
              Nastavi <ArrowRight className="w-4 h-4" />
            </button>
          </div>
        </div>
      )}
      <div className="h-28" />
    </div>
  )
}
 
// ─── Step 1: Hairdresser ──────────────────────────────────────────────────────
function StepHairdresser({ salon }: { salon: any }) {
  const { data, setData } = useBookingStore()
  return (
    <div>
      <h2 className="font-display text-xl font-bold text-white mb-1">Odaberi Frizera</h2>
      <p className="text-sm text-slate-400 mb-5">Ko će raditi tvoju frizuru?</p>
      <div className="space-y-3">
        {(salon.hairdressers ?? []).map((h: any) => {
          const sel = data.selectedHairdresser?.id === h.id
          return (
            <button key={h.id}
              onClick={() => setData({ selectedHairdresser: { id: h.id, name: h.fullName, photo: h.profilePhoto } })}
              className={cn('w-full flex items-center gap-4 p-4 rounded-2xl border transition-all text-left',
                sel ? 'bg-rose-500/10 border-rose-500/40 ring-1 ring-rose-500/30'
                    : 'bg-[#0F1623] border-white/5 hover:border-white/10'
              )}
            >
              <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-rose-500 to-rose-700 flex items-center justify-center text-white font-bold text-base shrink-0">
                {h.fullName.split(' ').map((n: string) => n[0]).join('').slice(0, 2)}
              </div>
              <div className="flex-1 min-w-0">
                <p className="font-medium text-white">{h.fullName}</p>
                {h.specialties && <p className="text-xs text-slate-400 mt-0.5 truncate">{h.specialties}</p>}
              </div>
              {sel && <CheckCircle className="w-5 h-5 text-rose-400 shrink-0" />}
            </button>
          )
        })}
      </div>
    </div>
  )
}
 
// ─── Step 2: Multi-Service Selection ─────────────────────────────────────────
function StepServices({ salon }: { salon: any }) {
  const { toggleService, isServiceSelected, data, getTotalPrice, getTotalDuration } = useBookingStore()
  const services: any[] = salon.services ?? []
 
  // Group services by inferred category
  const grouped = services.reduce((acc: Record<string, any[]>, s: any) => {
    const cat = inferCategory(s.name)
    if (!acc[cat]) acc[cat] = []
    acc[cat].push(s)
    return acc
  }, {} as Record<string, any[]>)
 
  return (
    <div>
      <h2 className="font-display text-xl font-bold text-white mb-1">Odaberi Usluge</h2>
      <p className="text-sm text-slate-400 mb-5">
        Možeš odabrati više usluga — trajanje i cijena se sabiraju automatski.
      </p>
 
      {/* Selected summary pill bar */}
      <AnimatePresence>
        {data.selectedServices.length > 0 && (
          <motion.div
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: 'auto' }}
            exit={{ opacity: 0, height: 0 }}
            className="mb-4 p-3.5 rounded-xl bg-rose-500/10 border border-rose-500/20 overflow-hidden"
          >
            <div className="flex items-center justify-between mb-2">
              <span className="text-xs font-semibold text-rose-400 uppercase tracking-wider">
                Odabrano ({data.selectedServices.length})
              </span>
              <div className="flex items-center gap-3">
                <span className="text-xs text-slate-400 flex items-center gap-1">
                  <Clock className="w-3 h-3" />{formatDuration({hours: Math.floor(getTotalDuration() / 60), minutes: getTotalDuration() % 60})}
                </span>
                <span className="text-sm font-bold text-white">{formatPrice(getTotalPrice())}</span>
              </div>
            </div>
            <div className="flex flex-wrap gap-1.5">
              {data.selectedServices.map(s => (
                <button key={s.id}
                  onClick={() => toggleService(s)}
                  className="flex items-center gap-1.5 px-2.5 py-1 rounded-lg bg-rose-500/20 text-rose-300 text-xs hover:bg-red-500/20 hover:text-red-400 transition-colors group"
                >
                  {s.name}
                  <Minus className="w-3 h-3 opacity-60 group-hover:opacity-100" />
                </button>
              ))}
            </div>
          </motion.div>
        )}
      </AnimatePresence>
 
      {/* Service groups */}
      <div className="space-y-5">
        {Object.entries(grouped).map(([category, items]) => (
          <div key={category}>
            <p className="text-xs text-slate-500 uppercase tracking-wider mb-2 flex items-center gap-1.5">
              <Scissors className="w-3 h-3" />{category}
            </p>
            <div className="space-y-2">
              {(items as any[]).map((s: any) => {
                const sel = isServiceSelected(s.id)
                return (
                  <button key={s.id}
                    onClick={() => toggleService({ id: s.id, name: s.name, price: s.price, duration: s.durationMinutes })}
                    className={cn('w-full flex items-center gap-3 p-4 rounded-xl border transition-all text-left',
                      sel ? 'bg-rose-500/10 border-rose-500/40'
                          : 'bg-[#0F1623] border-white/5 hover:border-white/10'
                    )}
                  >
                    <div className={cn('w-7 h-7 rounded-lg flex items-center justify-center shrink-0 transition-all',
                      sel ? 'bg-rose-500 text-white' : 'bg-white/5 text-slate-500'
                    )}>
                      {sel ? <CheckCircle className="w-4 h-4" /> : <Plus className="w-4 h-4" />}
                    </div>
                    <div className="flex-1 min-w-0">
                      <p className={cn('text-sm font-medium', sel ? 'text-white' : 'text-slate-200')}>{s.name}</p>
                      {s.description && <p className="text-xs text-slate-500 mt-0.5 line-clamp-1">{s.description}</p>}
                      <span className="text-xs text-slate-400 flex items-center gap-1 mt-1">
                        <Clock className="w-3 h-3" />{formatDuration({hours: Math.floor(s.durationMinutes / 60), minutes: s.durationMinutes % 60})}
                      </span>
                    </div>
                    <p className={cn('font-semibold text-sm shrink-0', sel ? 'text-white' : 'text-slate-300')}>
                      {formatPrice(s.price)}
                    </p>
                  </button>
                )
              })}
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
 
// ─── Step 3: Date & Time ──────────────────────────────────────────────────────
function StepDateTime() {
  const { data, setData, getTotalDuration } = useBookingStore()
  const totalDur = getTotalDuration()
 
  const days         = Array.from({ length: 14 }, (_, i) => addDays(new Date(), i))
  const selectedDate = data.selectedDate ? parseISO(data.selectedDate) : null
 
  const { data: availability, isLoading } = useAvailability(
    data.salonId,
    data.selectedHairdresser?.id,
    data.selectedDate,
    totalDur,
  )
  const slots = availability?.slots ?? []
 
  return (
    <div>
      <h2 className="font-display text-xl font-bold text-white mb-1">Odaberi Datum i Termin</h2>
      {totalDur > 0 && (
        <p className="text-xs text-rose-400 mb-5 flex items-center gap-1">
          <Clock className="w-3 h-3" />
          Trajanje termina: <span className="font-semibold ml-1">{formatDuration({hours: Math.floor(totalDur / 60), minutes: totalDur % 60})}</span>
          &nbsp;— prikazani su termini s dovoljno slobodnog vremena
        </p>
      )}
 
      {/* Date scroll */}
      <div className="mb-6">
        <p className="text-xs text-slate-500 uppercase tracking-wider mb-3">Datum</p>
        <div className="flex gap-2 overflow-x-auto pb-2 scrollbar-none">
          {days.map(day => {
            const val   = format(day, 'yyyy-MM-dd')
            const isSel = selectedDate && isSameDay(day, selectedDate)
            return (
              <button key={val}
                onClick={() => setData({ selectedDate: val, selectedTime: undefined })}
                className={cn('flex flex-col items-center gap-0.5 px-3 py-2.5 rounded-xl border min-w-[52px] shrink-0 transition-all',
                  isSel ? 'bg-rose-500 border-rose-500 text-white'
                        : 'bg-[#0F1623] border-white/5 text-slate-300 hover:border-white/15'
                )}
              >
                <span className="text-[10px] opacity-70">{format(day, 'EEE').slice(0, 3)}</span>
                <span className="text-base font-bold leading-none">{format(day, 'd')}</span>
                {isSameDay(day, new Date()) && <span className="text-[9px] opacity-60">Danas</span>}
              </button>
            )
          })}
        </div>
      </div>
 
      {/* Time slots */}
      {data.selectedDate && (
        <div>
          <p className="text-xs text-slate-500 uppercase tracking-wider mb-3">Slobodni Termini</p>
          {isLoading ? (
            <div className="grid grid-cols-5 sm:grid-cols-6 gap-2">
              {Array.from({ length: 12 }).map((_, i) => <div key={i} className="h-10 rounded-xl bg-slate-800 animate-pulse" />)}
            </div>
          ) : slots.length === 0 ? (
            <p className="text-sm text-slate-500 py-6 text-center">Nema slobodnih termina za odabrani datum</p>
          ) : (
            <div className="grid grid-cols-5 sm:grid-cols-6 gap-2">
              {slots.map(slot => (
                <button key={slot.time}
                  disabled={!slot.available}
                  onClick={() => setData({ selectedTime: slot.time })}
                  className={cn('py-2.5 rounded-xl text-sm font-medium transition-all',
                    !slot.available
                      ? 'opacity-25 cursor-not-allowed bg-slate-800 text-slate-600 line-through'
                      : data.selectedTime === slot.time
                        ? 'bg-rose-500 text-white shadow-lg shadow-rose-500/20'
                        : 'bg-[#0F1623] border border-white/8 text-slate-300 hover:border-rose-500/30 hover:text-white'
                  )}
                >
                  {slot.time}
                </button>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  )
}
 
// ─── Step 4: Confirm ──────────────────────────────────────────────────────────
function StepConfirm({ salon, onConfirm, isPending }: { salon: any; onConfirm: () => void; isPending: boolean }) {
  const { data, setData, getTotalPrice, getTotalDuration } = useBookingStore()
  const totalPrice = getTotalPrice()
  const totalDur   = getTotalDuration()
 
  const endTimeStr = data.selectedDate && data.selectedTime ? (() => {
    const d = new Date(`${data.selectedDate}T${data.selectedTime}:00`)
    d.setMinutes(d.getMinutes() + totalDur)
    return format(d, 'HH:mm')
  })() : null
 
  return (
    <div>
      <h2 className="font-display text-xl font-bold text-white mb-1">Potvrdi Rezervaciju</h2>
      <p className="text-sm text-slate-400 mb-5">Provjeri detalje i potvrdi</p>
 
      {/* Summary cards */}
      <div className="space-y-2.5 mb-5">
        <div className="flex items-center gap-3 p-3.5 rounded-xl bg-[#0F1623] border border-white/5">
          <div className="w-9 h-9 rounded-lg bg-rose-500/10 flex items-center justify-center shrink-0">
            <User className="w-4 h-4 text-rose-400" />
          </div>
          <div>
            <p className="text-[10px] text-slate-500 uppercase tracking-wider">Frizer</p>
            <p className="text-sm font-medium text-white">{data.selectedHairdresser?.name}</p>
          </div>
        </div>
        <div className="grid grid-cols-2 gap-2.5">
          {[
            { icon: Calendar, label: 'Datum', value: data.selectedDate ? formatDate(data.selectedDate) : '—' },
            { icon: Clock,    label: 'Termin', value: `${data.selectedTime ?? '—'} → ${endTimeStr ?? '—'}` },
          ].map(({ icon: Icon, label, value }) => (
            <div key={label} className="flex items-center gap-3 p-3.5 rounded-xl bg-[#0F1623] border border-white/5">
              <Icon className="w-4 h-4 text-rose-400 shrink-0" />
              <div>
                <p className="text-[10px] text-slate-500 uppercase tracking-wider">{label}</p>
                <p className="text-sm font-medium text-white">{value}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
 
      {/* Invoice preview */}
      <div className="rounded-xl bg-[#0F1623] border border-white/5 overflow-hidden mb-5">
        <div className="px-4 py-2.5 border-b border-white/5 flex items-center gap-2">
          <Receipt className="w-3.5 h-3.5 text-slate-400" />
          <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Pregled Računa</span>
        </div>
        <div className="divide-y divide-white/5">
          {data.selectedServices.map(s => (
            <div key={s.id} className="flex items-center justify-between px-4 py-2.5">
              <div className="flex items-center gap-2 min-w-0">
                <Scissors className="w-3.5 h-3.5 text-slate-500 shrink-0" />
                <span className="text-sm text-slate-200 truncate">{s.name}</span>
                <span className="text-xs text-slate-500 shrink-0">{formatDuration({hours: Math.floor(s.duration / 60), minutes: s.duration % 60})}</span>
              </div>
              <span className="text-sm font-medium text-white ml-3 shrink-0">{formatPrice(s.price)}</span>
            </div>
          ))}
        </div>
        <div className="px-4 py-3 bg-white/3 flex items-center justify-between">
          <span className="text-xs text-slate-400">Ukupno trajanje: {formatDuration({hours: totalDur / 60, minutes: totalDur % 60})}</span>
          <div className="text-right">
            <p className="text-xs text-slate-400 mb-0.5">Za platiti</p>
            <p className="text-lg font-bold text-white">{formatPrice(totalPrice)}</p>
          </div>
        </div>
      </div>
 
      {/* Email notice */}
      <div className="flex items-start gap-2.5 p-3.5 rounded-xl bg-rose-500/8 border border-rose-500/15 mb-5">
        <Mail className="w-4 h-4 text-rose-400 shrink-0 mt-0.5" />
        <p className="text-xs text-slate-400 leading-relaxed">
          <span className="font-semibold text-rose-300 block mb-0.5">Račun na email</span>
          Nakon potvrde, detaljan račun bit će automatski poslan na vašu email adresu.
        </p>
      </div>
 
      {/* Notes */}
      <div className="mb-5">
        <label className="block text-xs text-slate-500 uppercase tracking-wider mb-2">Napomena (opciono)</label>
        <textarea value={data.notes ?? ''} onChange={e => setData({ notes: e.target.value })}
          placeholder="Posebni zahtjevi ili napomene..." rows={3}
          className="w-full px-4 py-3 rounded-xl bg-[#0F1623] border border-white/8 text-white placeholder:text-slate-600 text-sm focus:outline-none focus:border-rose-500/50 transition-all resize-none"
        />
      </div>
 
      <button onClick={onConfirm} disabled={isPending}
        className="w-full flex items-center justify-center gap-2 py-4 rounded-xl bg-rose-500 hover:bg-rose-600 disabled:opacity-50 text-white font-semibold transition-colors shadow-lg shadow-rose-500/20"
      >
        {isPending
          ? <><span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />Rezerviram...</>
          : <><CheckCircle className="w-5 h-5" />Potvrdi · {formatPrice(totalPrice)}</>
        }
      </button>
    </div>
  )
}


function inferCategory(name: string): string {
  const n = name.toLowerCase()
  if (n.includes('bojenje') || n.includes('balayage') || n.includes('ombre') || n.includes('keratin')) return 'Bojenje i Tretmani'
  if (n.includes('brada') || n.includes('brijanje') || n.includes('muško') || n.includes('muski') || n.includes('klasični')) return 'Muške Usluge'
  if (n.includes('dječ') || n.includes('djecij')) return 'Dječije Usluge'
  if (n.includes('pranje') || n.includes('fen') || n.includes('event') || n.includes('frizura')) return 'Styling'
  return 'Šišanje i Rezovi'
}