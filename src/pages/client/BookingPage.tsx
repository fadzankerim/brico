import { useEffect } from 'react'
import { useParams, useSearchParams, useNavigate, Link } from 'react-router-dom'
import { motion, AnimatePresence } from 'motion/react'
import {
  ArrowLeft, ArrowRight, CheckCircle,
  Scissors, Calendar, Clock, User,
} from 'lucide-react'

import { addDays, format, formatDuration, isSameDay, parseISO } from 'date-fns'
import { useBookingStore } from '../../store/bookingStore'
import { useSalonById } from '../../hooks/useSalons'
import { useAvailability, useCreateAppointment } from '../../hooks/useBooking'
import { cn } from '../../lib/utils'
import { formatDate, formatPrice } from '../../utils/dateUtils'

const STEPS = ['Frizer', 'Usluga', 'Termin', 'Potvrda']

export default function BookingPage() {
  const { salonId }        = useParams<{ salonId: string }>()
  const [searchParams]     = useSearchParams()
  const navigate           = useNavigate()

  const { step, data, nextStep, prevStep, setData, reset } = useBookingStore()
  const { data: salon, isLoading } = useSalonById(salonId)   // ← fixed
  const createAppointment = useCreateAppointment()

  // Pre-select hairdresser / service from query params
  useEffect(() => {
    if (!salon) return
    setData({ salonId: salon.id, salonName: salon.name })

    const hid = searchParams.get('hairdresser')
    if (hid && salon.hairdressers) {
      const h = salon.hairdressers.find(h => h.id === Number(hid))
      if (h) setData({ selectedHairdresser: { id: h.id, name: h.fullName, photo: h.profilePhoto } })
    }

    const sid = searchParams.get('service')
    if (sid && salon.services) {
      const s = salon.services.find(s => s.id === Number(sid))
      if (s) setData({ selectedService: { id: s.id, name: s.name, price: s.price, duration: s.durationMinutes } })
    }
  }, [salon])

  useEffect(() => () => reset(), [])

  function handleConfirm() {
    if (!data.selectedHairdresser || !data.selectedService || !data.selectedDate || !data.selectedTime) return
    createAppointment.mutate(
      {
        hairdresserId: data.selectedHairdresser.id,
        serviceId:     data.selectedService.id,
        startTime:     `${data.selectedDate}T${data.selectedTime}:00`,
        notes:         data.notes,
      },
      { onSuccess: () => navigate('/dashboard') }
    )
  }

  if (isLoading || !salon) {
    return (
      <div className="min-h-screen bg-[#080C14] flex items-center justify-center">
        <div className="w-8 h-8 border-2 border-rose-500 border-t-transparent rounded-full animate-spin" />
      </div>
    )
  }

  const canAdvance =
    (step === 1 && !!data.selectedHairdresser) ||
    (step === 2 && !!data.selectedService)     ||
    (step === 3 && !!data.selectedDate && !!data.selectedTime)

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
          <span className="text-xs text-slate-500 shrink-0">Korak {step}/4</span>
        </div>

        {/* Progress */}
        <div className="max-w-2xl mx-auto px-4 pb-3">
          <div className="flex items-center gap-2">
            {STEPS.map((label, i) => (
              <div key={label} className="flex-1 flex flex-col items-center gap-1">
                <div className={cn(
                  'h-1 w-full rounded-full transition-all duration-300',
                  i + 1 < step  ? 'bg-rose-500'     :
                  i + 1 === step ? 'bg-rose-500/60' :
                  'bg-white/10'
                )} />
                <span className={cn(
                  'text-[10px] transition-colors',
                  i + 1 === step ? 'text-rose-400' : 'text-slate-600'
                )}>
                  {label}
                </span>
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
            {step === 2 && <StepService     salon={salon} />}
            {step === 3 && <StepDateTime />}
            {step === 4 && (
              <StepConfirm
                salon={salon}
                onConfirm={handleConfirm}
                isPending={createAppointment.isPending}
              />
            )}
          </motion.div>
        </AnimatePresence>
      </div>

      {/* Bottom nav */}
      {step < 4 && (
        <div className="fixed bottom-0 inset-x-0 p-4 bg-[#080C14]/95 backdrop-blur-xl border-t border-white/5 z-20">
          <div className="max-w-2xl mx-auto">
            <button
              onClick={nextStep}
              disabled={!canAdvance}
              className="w-full flex items-center justify-center gap-2 py-3.5 rounded-xl bg-rose-500 hover:bg-rose-600 disabled:opacity-30 disabled:cursor-not-allowed text-white font-semibold transition-colors"
            >
              Nastavi <ArrowRight className="w-4 h-4" />
            </button>
          </div>
        </div>
      )}
      <div className="h-24" />
    </div>
  )
}

// ─── Step 1 — Hairdresser ─────────────────────────────────────────────────────
function StepHairdresser({ salon }: { salon: any }) {
  const { data, setData } = useBookingStore()

  return (
    <div>
      <h2 className="font-display text-xl font-bold text-white mb-1">Odaberi Frizera</h2>
      <p className="text-sm text-slate-400 mb-5">Ko će raditi tvoju frizuru?</p>
      <div className="space-y-3">
        {(salon.hairdressers ?? []).map((h: any) => {
          const selected = data.selectedHairdresser?.id === h.id
          return (
            <button
              key={h.id}
              onClick={() => setData({ selectedHairdresser: { id: h.id, name: h.fullName, photo: h.profilePhoto } })}
              className={cn(
                'w-full flex items-center gap-4 p-4 rounded-2xl border transition-all text-left',
                selected
                  ? 'bg-rose-500/10 border-rose-500/40 ring-1 ring-rose-500/30'
                  : 'bg-[#0F1623] border-white/5 hover:border-white/10'
              )}
            >
              <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-rose-500 to-rose-700 flex items-center justify-center text-white font-bold text-base shrink-0">
                {h.fullName.split(' ').map((n: string) => n[0]).join('').slice(0, 2)}
              </div>
              <div className="flex-1 min-w-0">
                <p className="font-medium text-white">{h.fullName}</p>
                {h.specialties && (
                  <p className="text-xs text-slate-400 mt-0.5 truncate">{h.specialties}</p>
                )}
              </div>
              {selected && <CheckCircle className="w-5 h-5 text-rose-400 shrink-0" />}
            </button>
          )
        })}
      </div>
    </div>
  )
}

// ─── Step 2 — Service ─────────────────────────────────────────────────────────
function StepService({ salon }: { salon: any }) {
  const { data, setData } = useBookingStore()

  return (
    <div>
      <h2 className="font-display text-xl font-bold text-white mb-1">Odaberi Uslugu</h2>
      <p className="text-sm text-slate-400 mb-5">Koju uslugu želiš rezervisati?</p>
      <div className="space-y-3">
        {(salon.services ?? []).map((s: any) => {
          const selected = data.selectedService?.id === s.id
          return (
            <button
              key={s.id}
              onClick={() => setData({ selectedService: { id: s.id, name: s.name, price: s.price, duration: s.durationMinutes } })}
              className={cn(
                'w-full flex items-center gap-4 p-4 rounded-2xl border transition-all text-left',
                selected
                  ? 'bg-rose-500/10 border-rose-500/40 ring-1 ring-rose-500/30'
                  : 'bg-[#0F1623] border-white/5 hover:border-white/10'
              )}
            >
              <div className="w-10 h-10 rounded-xl bg-white/5 flex items-center justify-center shrink-0">
                <Scissors className="w-5 h-5 text-slate-400" />
              </div>
              <div className="flex-1 min-w-0">
                <p className="font-medium text-white">{s.name}</p>
                <p className="text-xs text-slate-400 mt-0.5">{formatDuration(s.durationMinutes)}</p>
              </div>
              <div className="text-right shrink-0">
                <p className="font-semibold text-white">{formatPrice(s.price)}</p>
              </div>
              {selected && <CheckCircle className="w-5 h-5 text-rose-400 shrink-0" />}
            </button>
          )
        })}
      </div>
    </div>
  )
}

// ─── Step 3 — Date & Time ─────────────────────────────────────────────────────
function StepDateTime() {
  const { data, setData } = useBookingStore()

  const days         = Array.from({ length: 14 }, (_, i) => addDays(new Date(), i))
  const selectedDate = data.selectedDate ? parseISO(data.selectedDate) : null

  const { data: availability, isLoading } = useAvailability(
    data.selectedHairdresser?.id,
    data.selectedDate
  )
  const slots = availability?.slots ?? []

  return (
    <div>
      <h2 className="font-display text-xl font-bold text-white mb-1">Odaberi Datum i Termin</h2>
      <p className="text-sm text-slate-400 mb-5">Kada ti odgovara?</p>

      {/* Date scroll */}
      <div className="mb-6">
        <p className="text-xs text-slate-500 uppercase tracking-wider mb-3">Datum</p>
        <div className="flex gap-2 overflow-x-auto pb-2 scrollbar-none">
          {days.map(day => {
            const val      = format(day, 'yyyy-MM-dd')
            const selected = selectedDate && isSameDay(day, selectedDate)
            const isToday  = isSameDay(day, new Date())
            return (
              <button
                key={val}
                onClick={() => setData({ selectedDate: val, selectedTime: undefined })}
                className={cn(
                  'flex flex-col items-center gap-0.5 px-3 py-2.5 rounded-xl border min-w-[52px] shrink-0 transition-all',
                  selected
                    ? 'bg-rose-500 border-rose-500 text-white'
                    : 'bg-[#0F1623] border-white/5 text-slate-300 hover:border-white/15'
                )}
              >
                <span className="text-[10px] opacity-70">{format(day, 'EEE').slice(0, 3)}</span>
                <span className="text-base font-bold leading-none">{format(day, 'd')}</span>
                {isToday && <span className="text-[9px] opacity-60">Danas</span>}
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
              {Array.from({ length: 12 }).map((_, i) => (
                <div key={i} className="h-10 rounded-xl bg-slate-800 animate-pulse" />
              ))}
            </div>
          ) : slots.length === 0 ? (
            <p className="text-sm text-slate-500 py-6 text-center">
              Nema slobodnih termina za odabrani datum
            </p>
          ) : (
            <div className="grid grid-cols-5 sm:grid-cols-6 gap-2">
              {slots.map(slot => (
                <button
                  key={slot.time}
                  disabled={!slot.available}
                  onClick={() => setData({ selectedTime: slot.time })}
                  className={cn(
                    'py-2.5 rounded-xl text-sm font-medium transition-all',
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

// ─── Step 4 — Confirm ─────────────────────────────────────────────────────────
function StepConfirm({
  salon,
  onConfirm,
  isPending,
}: { salon: any; onConfirm: () => void; isPending: boolean }) {
  const { data, setData } = useBookingStore()

  const rows = [
    { icon: User,     label: 'Frizer',  value: data.selectedHairdresser?.name },
    { icon: Scissors, label: 'Usluga',  value: `${data.selectedService?.name} · ${formatPrice(data.selectedService?.price ?? 0)}` },
    { icon: Calendar, label: 'Datum',   value: data.selectedDate ? formatDate(data.selectedDate) : '' },
    { icon: Clock,    label: 'Termin',  value: data.selectedTime },
  ]

  return (
    <div>
      <h2 className="font-display text-xl font-bold text-white mb-1">Potvrdi Rezervaciju</h2>
      <p className="text-sm text-slate-400 mb-5">Provjeri detalje prije potvrde</p>

      <div className="space-y-2.5 mb-6">
        {rows.map(({ icon: Icon, label, value }) => (
          <div key={label} className="flex items-center gap-3 p-3.5 rounded-xl bg-[#0F1623] border border-white/5">
            <div className="w-9 h-9 rounded-lg bg-rose-500/10 flex items-center justify-center shrink-0">
              <Icon className="w-4 h-4 text-rose-400" />
            </div>
            <div className="min-w-0">
              <p className="text-[10px] text-slate-500 uppercase tracking-wider">{label}</p>
              <p className="text-sm font-medium text-white truncate">{value}</p>
            </div>
          </div>
        ))}
      </div>

      {/* Notes */}
      <div className="mb-6">
        <label className="block text-xs text-slate-500 uppercase tracking-wider mb-2">
          Napomena (opciono)
        </label>
        <textarea
          value={data.notes ?? ''}
          onChange={e => setData({ notes: e.target.value })}
          placeholder="Posebni zahtjevi ili napomene frizerima..."
          rows={3}
          className="w-full px-4 py-3 rounded-xl bg-[#0F1623] border border-white/8 text-white placeholder:text-slate-600 text-sm focus:outline-none focus:border-rose-500/50 transition-all resize-none"
        />
      </div>

      <button
        onClick={onConfirm}
        disabled={isPending}
        className="w-full flex items-center justify-center gap-2 py-4 rounded-xl bg-rose-500 hover:bg-rose-600 disabled:opacity-50 text-white font-semibold transition-colors shadow-lg shadow-rose-500/20"
      >
        {isPending ? (
          <><span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />Rezerviram...</>
        ) : (
          <><CheckCircle className="w-5 h-5" />Potvrdi Rezervaciju</>
        )}
      </button>
    </div>
  )
}