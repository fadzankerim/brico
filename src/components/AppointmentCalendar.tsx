import { useMemo, useState } from "react"
import type { Appointment } from "../types/booking.types"
import { addDays, addWeeks, differenceInMinutes, format, formatDuration, isToday, parseISO, startOfWeek, subWeeks } from "date-fns"
import { Calendar, ChevronLeft, ChevronRight, Clock, DollarSign, FileText, Pencil, Plus, Scissors, Trash2, User, X } from "lucide-react"
import { cn } from "../lib/utils"
import { AnimatePresence } from "motion/react"
import AppointmentStatusBadge from "./AppointmentStatusBadge"
import { formatPrice, formatTime } from "../utils/dateUtils"
import {motion} from "motion/react"


//CONSTANTS
const HOUR_HEIGHT = 64 // px per hour
const START_HOUR  = 8  // calendar starts at 08:00
const END_HOUR    = 20 // calendar ends at 20:00
const HOURS       = Array.from({ length: END_HOUR - START_HOUR }, (_, i) => i + START_HOUR)

const STATUS_COLORS = {
  PENDING:   { bg: 'bg-amber-500/20',   border: 'border-amber-500/40',   text: 'text-amber-300',   dot: 'bg-amber-500',   label: 'Čeka potvrdu' },
  CONFIRMED: { bg: 'bg-emerald-500/20', border: 'border-emerald-500/40', text: 'text-emerald-300', dot: 'bg-emerald-500', label: 'Potvrđen' },
  COMPLETED: { bg: 'bg-blue-500/20',    border: 'border-blue-500/40',    text: 'text-blue-300',    dot: 'bg-blue-500',    label: 'Odrađen' },
  CANCELLED: { bg: 'bg-slate-500/15',   border: 'border-slate-500/30',   text: 'text-slate-500',   dot: 'bg-slate-500',   label: 'Otkazan' },
  NO_SHOW:   { bg: 'bg-rose-700/20',    border: 'border-rose-700/40',    text: 'text-rose-400',    dot: 'bg-rose-700',    label: 'Nije se pojavio' },
} as const


interface CalendarProps {
  appointments: Appointment[]
  hairdressers?: { id: number; fullName: string }[] 
  isLoading?: boolean
  onAddAppointment?: () => void
  onEditAppointment?: (a: Appointment) => void
  onDeleteAppointment?: (id: number) => void
}

function getBlockStyle(startTime: string, endTime: string) {
  const start = parseISO(startTime)
  const end   = parseISO(endTime)
  const startMinutes = (start.getHours() - START_HOUR) * 60 + start.getMinutes()
  const duration     = differenceInMinutes(end, start)
  const top          = (startMinutes / 60) * HOUR_HEIGHT
  const height       = Math.max((duration / 60) * HOUR_HEIGHT, 28) // min 28px
  return { top, height }
}

export default function AppointmentCalendar({ appointments, hairdressers, isLoading, onAddAppointment, onEditAppointment, onDeleteAppointment }: CalendarProps) {


    const [weekStart, setWeekStart] = useState(() => startOfWeek(new Date(), {weekStartsOn: 1}))
    const [selectedAppointment, setSelectedAppointment] = useState<Appointment | null>(null)
    const [filterHairdresser, setFilter] = useState<number | 'all'>('all')

    const days = Array.from({ length: 7}, (_, i) => addDays(weekStart, i))

    const getStatusColor = (status: string) =>
    STATUS_COLORS[status as keyof typeof STATUS_COLORS] ?? STATUS_COLORS.PENDING

    const filtered = appointments.filter(
    (a) => filterHairdresser === 'all' || a.hairdresserId === filterHairdresser
  )

  

  // Group by day
  const byDay = useMemo(() => {
    const map = new Map<string, Appointment[]>()
    days.forEach((d) => map.set(format(d, 'yyyy-MM-dd'), []))
    filtered.forEach((a) => {
      const key = format(parseISO(a.startTime), 'yyyy-MM-dd')
      if (map.has(key)) map.get(key)!.push(a)
    })
    return map
  }, [filtered, days])

  return(
    <div className="flex flex-col h-full bg-[#080C14] rounded-2xl border border-white/5 overflow-hidden">

      {/* ── Toolbar ── */}
      <div className="flex items-center justify-between gap-4 px-5 py-3.5 border-b border-white/5 shrink-0 flex-wrap gap-y-2">
        {/* Week navigation */}
        <div className="flex items-center gap-2">
          <button onClick={() => setWeekStart(subWeeks(weekStart, 1))} className="w-8 h-8 rounded-lg flex items-center justify-center text-slate-400 hover:text-white hover:bg-white/8 transition-colors">
            <ChevronLeft className="w-4 h-4" />
          </button>
          <span className="text-sm font-medium text-white min-w-[160px] text-center">
            {format(weekStart, 'd. MMM')} – {format(addDays(weekStart, 6), 'd. MMM yyyy')}
          </span>
          <button onClick={() => setWeekStart(addWeeks(weekStart, 1))} className="w-8 h-8 rounded-lg flex items-center justify-center text-slate-400 hover:text-white hover:bg-white/8 transition-colors">
            <ChevronRight className="w-4 h-4" />
          </button>
          <button
            onClick={() => setWeekStart(startOfWeek(new Date(), { weekStartsOn: 1 }))}
            className="ml-1 px-3 py-1.5 rounded-lg bg-white/5 text-xs text-slate-400 hover:text-white hover:bg-white/8 transition-colors"
          >
            Danas
          </button>
        </div>

        <div className="flex items-center gap-2">
          {/* Hairdresser filter */}
          {hairdressers?.length && hairdressers.length > 1 && (
            <select
              value={filterHairdresser}
              onChange={(e) => setFilter(e.target.value === 'all' ? 'all' : Number(e.target.value))}
              className="px-3 py-1.5 rounded-lg bg-white/5 border border-white/8 text-sm text-slate-300 focus:outline-none cursor-pointer"
            >
              <option value="all" className="bg-[#0F1623]">Svi Frizeri</option>
              {hairdressers.map((h) => (
                <option key={h.id} value={h.id} className="bg-[#0F1623]">{h.fullName}</option>
              ))}
            </select>
          )}

          {/* Add button */}
          {onAddAppointment && (
            <button
              onClick={onAddAppointment}
              className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-rose-500 hover:bg-rose-600 text-white text-sm font-medium transition-colors"
            >
              <Plus className="w-4 h-4" /> Novi Termin
            </button>
          )}
        </div>
      </div>

      {/* ── Status color legend ── */}
      <div className="flex items-center gap-4 px-5 py-2 border-b border-white/5 overflow-x-auto scrollbar-none">
        {Object.entries(STATUS_COLORS).map(([status, c]) => (
          <div key={status} className="flex items-center gap-1.5 shrink-0">
            <div className={`w-2 h-2 rounded-full ${c.dot}`} />
            <span className="text-xs text-slate-400">{c.label}</span>
          </div>
        ))}
      </div>

      {/* ── Calendar grid ── */}
      <div className="flex-1 overflow-auto">
        {/* Day header row */}
        <div className="sticky top-0 z-10 grid bg-[#080C14] border-b border-white/5" style={{ gridTemplateColumns: '52px repeat(7, 1fr)' }}>
          <div className="border-r border-white/5" /> {/* time gutter */}
          {days.map((day) => {
            const todayDay = isToday(day)
            return (
              <div key={day.toISOString()} className="text-center py-3 border-r border-white/5 last:border-r-0">
                <p className={cn('text-xs uppercase tracking-wider', todayDay ? 'text-rose-400' : 'text-slate-500')}>
                  {format(day, 'EEE')}
                </p>
                <div className={cn(
                  'inline-flex items-center justify-center w-8 h-8 rounded-full mt-1 text-sm font-semibold mx-auto',
                  todayDay ? 'bg-rose-500 text-white' : 'text-white'
                )}>
                  {format(day, 'd')}
                </div>
              </div>
            )
          })}
        </div>

        {/* Time + event rows */}
        <div className="grid relative" style={{ gridTemplateColumns: '52px repeat(7, 1fr)' }}>
          {/* Time gutter */}
          <div className="border-r border-white/5">
            {HOURS.map((h) => (
              <div key={h} style={{ height: HOUR_HEIGHT }} className="relative">
                <span className="absolute -top-2.5 right-2 text-[11px] text-slate-600 select-none">
                  {h.toString().padStart(2, '0')}:00
                </span>
              </div>
            ))}
          </div>

          {/* Day columns */}
          {days.map((day) => {
            const key     = format(day, 'yyyy-MM-dd')
            const dayAppts = byDay.get(key) ?? []
            const todayDay = isToday(day)

            return (
              <div
                key={key}
                className={cn('relative border-r border-white/5 last:border-r-0', todayDay && 'bg-rose-500/[0.02]')}
                style={{ height: HOURS.length * HOUR_HEIGHT }}
              >
                {/* Hour lines */}
                {HOURS.map((h) => (
                  <div
                    key={h}
                    className="absolute inset-x-0 border-t border-white/[0.04]"
                    style={{ top: (h - START_HOUR) * HOUR_HEIGHT }}
                  />
                ))}

                {/* Current time line */}
                {todayDay && (() => {
                  const now = new Date()
                  const mins = (now.getHours() - START_HOUR) * 60 + now.getMinutes()
                  if (mins < 0 || mins > (END_HOUR - START_HOUR) * 60) return null
                  return (
                    <div
                      className="absolute inset-x-0 z-10 flex items-center pointer-events-none"
                      style={{ top: (mins / 60) * HOUR_HEIGHT }}
                    >
                      <div className="w-2 h-2 rounded-full bg-rose-500 -ml-1 shrink-0" />
                      <div className="flex-1 h-px bg-rose-500/60" />
                    </div>
                  )
                })()}

                {/* Appointment blocks */}
                {dayAppts.map((appt) => {
                  const { top, height } = getBlockStyle(appt.startTime, appt.endTime)
                  const c = getStatusColor(appt.status)
                  const isShort = height < 48

                  return (
                    <button
                      key={appt.id}
                      onClick={() => setSelectedAppointment(appt)}
                      className={cn(
                        'absolute inset-x-1 rounded-lg border text-left overflow-hidden transition-all hover:brightness-110 hover:z-10 focus:outline-none focus:ring-1 focus:ring-rose-500',
                        c.bg, c.border
                      )}
                      style={{ top, height }}
                    >
                      <div className="px-1.5 py-1 h-full flex flex-col justify-start">
                        <p className={cn('text-[11px] font-semibold truncate leading-tight', c.text)}>
                          {appt.clientName}
                        </p>
                        {!isShort && (
                          <p className="text-[10px] text-slate-400 truncate leading-tight mt-0.5">
                            {appt.serviceName}
                          </p>
                        )}
                      </div>
                    </button>
                  )
                })}
              </div>
            )
          })}
        </div>
      </div>

      {/* ── Appointment detail modal ── */}
      <AnimatePresence>
        {selectedAppointment && (
          <AppointmentDetailModal
            appointment={selectedAppointment}
            onClose={() => setSelectedAppointment(null)}
            onEdit={onEditAppointment ? () => { onEditAppointment(selectedAppointment); setSelectedAppointment(null) } : undefined}
            onDelete={onDeleteAppointment ? () => { onDeleteAppointment(selectedAppointment.id); setSelectedAppointment(null) } : undefined}
          />
        )}
      </AnimatePresence>
    </div>
  )
}

// ── Appointment detail modal ─────────────────────────────────────────────────
function AppointmentDetailModal({
  appointment: a,
  onClose,
  onEdit,
  onDelete,
}: {
  appointment: Appointment
  onClose: () => void
  onEdit?: () => void
  onDelete?: () => void
}) {
  const c = STATUS_COLORS[a.status as keyof typeof STATUS_COLORS] ?? STATUS_COLORS.PENDING
  const [confirmDelete, setConfirmDelete] = useState(false)

  const duration = differenceInMinutes(parseISO(a.endTime), parseISO(a.startTime))

  return (
    <>
      {/* Backdrop */}
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        exit={{ opacity: 0 }}
        onClick={onClose}
        className="fixed inset-0 bg-black/60 backdrop-blur-sm z-40"
      />

      {/* Panel */}
      <motion.div
        initial={{ opacity: 0, scale: 0.95, y: 10 }}
        animate={{ opacity: 1, scale: 1, y: 0 }}
        exit={{ opacity: 0, scale: 0.95, y: 10 }}
        transition={{ type: 'spring', damping: 25, stiffness: 300 }}
        className="fixed z-50 inset-x-4 sm:inset-x-auto sm:right-6 sm:w-80 top-1/2 -translate-y-1/2 sm:translate-y-0 sm:top-20 bg-[#0F1623] rounded-2xl border border-white/10 shadow-2xl shadow-black/60 overflow-hidden"
      >
        {/* Color accent header */}
        <div className={cn('h-1.5 w-full', c.dot)} />

        <div className="p-5">
          {/* Title row */}
          <div className="flex items-start justify-between mb-4">
            <div>
              <h3 className="font-display font-semibold text-white text-base">{a.clientName}</h3>
              <AppointmentStatusBadge status={a.status} />
            </div>
            <button onClick={onClose} className="w-7 h-7 flex items-center justify-center rounded-lg text-slate-500 hover:text-white hover:bg-white/8 transition-colors">
              <X className="w-4 h-4" />
            </button>
          </div>

          {/* Details */}
          <div className="space-y-3">
            {[
              { icon: User,       label: 'Frizer',   value: a.hairdresserName },
              { icon: Scissors,   label: 'Usluga',   value: a.services?.length ? a.services.map(s => s.serviceName).join(', ') : (a.serviceName || '—') },
              { icon: Calendar,   label: 'Datum',    value: format(parseISO(a.startTime), 'dd.MM.yyyy') },
              { icon: Clock,      label: 'Termin',   value: `${formatTime(a.startTime)} – ${formatTime(a.endTime)} (${formatDuration({ hours: Math.floor(duration / 60), minutes: duration % 60 })})` },
              { icon: DollarSign, label: 'Cijena',   value: formatPrice(a.totalPrice ?? a.price) },
            ].map(({ icon: Icon, label, value }) => (
              <div key={label} className="flex items-center gap-3">
                <div className="w-7 h-7 rounded-lg bg-white/5 flex items-center justify-center shrink-0">
                  <Icon className="w-3.5 h-3.5 text-slate-400" />
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-[11px] text-slate-500 uppercase tracking-wide">{label}</p>
                  <p className="text-sm text-white truncate">{value}</p>
                </div>
              </div>
            ))}

            {a.notes && (
              <div className="flex items-start gap-3">
                <div className="w-7 h-7 rounded-lg bg-white/5 flex items-center justify-center shrink-0 mt-0.5">
                  <FileText className="w-3.5 h-3.5 text-slate-400" />
                </div>
                <div>
                  <p className="text-[11px] text-slate-500 uppercase tracking-wide">Napomena</p>
                  <p className="text-sm text-slate-300 leading-relaxed">{a.notes}</p>
                </div>
              </div>
            )}
          </div>

          {/* Actions */}
          {(onEdit || onDelete) && (
            <div className="flex gap-2 mt-5 pt-4 border-t border-white/5">
              {onEdit && (
                <button
                  onClick={onEdit}
                  className="flex-1 flex items-center justify-center gap-2 py-2 rounded-xl bg-white/5 hover:bg-white/8 text-slate-300 text-sm transition-colors"
                >
                  <Pencil className="w-3.5 h-3.5" /> Uredi
                </button>
              )}
              {onDelete && (
                confirmDelete ? (
                  <div className="flex gap-2 flex-1">
                    <button onClick={onDelete} className="flex-1 py-2 rounded-xl bg-rose-500/20 text-rose-400 text-xs hover:bg-rose-500 hover:text-white transition-colors">
                      Potvrdi brisanje
                    </button>
                    <button onClick={() => setConfirmDelete(false)} className="px-3 py-2 rounded-xl bg-white/5 text-slate-400 text-xs hover:text-white transition-colors">
                      Odustani
                    </button>
                  </div>
                ) : (
                  <button
                    onClick={() => setConfirmDelete(true)}
                    className="flex items-center justify-center gap-2 px-4 py-2 rounded-xl bg-white/5 hover:bg-rose-500/10 text-slate-400 hover:text-rose-400 text-sm transition-colors"
                  >
                    <Trash2 className="w-3.5 h-3.5" />
                  </button>
                )
              )}
            </div>
          )}
        </div>
      </motion.div>
    </>
  )
  
  
    
}
  