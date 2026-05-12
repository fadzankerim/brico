import { Bell, Clock, Scissors } from "lucide-react";
import type { Appointment } from "../../types/booking.types"
import { motion } from "motion/react"
import { formatPrice } from "../../utils/dateUtils";

function safeDate(val: unknown): Date | null {
  if (!val) return null
  if (Array.isArray(val)) {
    const [y, mo, d, h = 0, m = 0] = val as number[]
    return new Date(y, mo - 1, d, h, m)
  }
  try {
    const dt = new Date(String(val))
    return isNaN(dt.getTime()) ? null : dt
  } catch { return null }
}

function fmtDateTime(val: unknown) {
  const d = safeDate(val)
  if (!d) return '—'
  return d.toLocaleDateString('bs-BA', { day: '2-digit', month: '2-digit', year: 'numeric' }) +
    ' u ' + d.toLocaleTimeString('bs-BA', { hour: '2-digit', minute: '2-digit' })
}

export default function BookingSuccessScreen({ appointment: appt, onDone }: { appointment: Appointment; onDone: () => void }) {
  const items: any[] = appt.items ?? appt.services ?? []
  const total = appt.totalPrice ?? appt.price ?? 0

  return (
    <div className="min-h-screen bg-[#080C14] text-white flex items-center justify-center px-4 py-12">
      <div className="w-full max-w-md">

        {/* Header */}
        <motion.div
          initial={{ scale: 0.7, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          transition={{ type: 'spring', damping: 15, stiffness: 200 }}
          className="text-center mb-6"
        >
          <div className="w-20 h-20 rounded-full bg-amber-500/15 border-2 border-amber-500/30 flex items-center justify-center mx-auto mb-4">
            <Clock className="w-10 h-10 text-amber-400" />
          </div>
          <h1 className="font-display text-2xl font-bold text-white mb-1">Zahtjev Poslan!</h1>
          <p className="text-slate-400 text-sm">Vaša rezervacija je primljena i čeka potvrdu</p>
        </motion.div>

        {/* Booking summary */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.15 }}
          className="bg-[#0F1623] rounded-2xl border border-white/8 overflow-hidden mb-4"
        >
          {/* Salon + hairdresser */}
          <div className="px-5 py-4 border-b border-white/5">
            <p className="text-xs text-slate-500 uppercase tracking-wider mb-1">Salon</p>
            <p className="font-semibold text-white">{appt.salonName || '—'}</p>
            <p className="text-xs text-slate-400 mt-0.5">{appt.salonAddress || ''}</p>
          </div>

          {/* Details grid */}
          <div className="grid grid-cols-2 gap-0 divide-x divide-white/5">
            <div className="px-5 py-3 border-b border-white/5">
              <p className="text-xs text-slate-500 mb-0.5">Frizer</p>
              <p className="text-sm font-medium text-white">{appt.hairdresserName || '—'}</p>
            </div>
            <div className="px-5 py-3 border-b border-white/5">
              <p className="text-xs text-slate-500 mb-0.5">Datum i termin</p>
              <p className="text-sm font-medium text-white">{fmtDateTime(appt.startTime)}</p>
            </div>
          </div>

          {/* Services */}
          {items.length > 0 && (
            <div className="divide-y divide-white/5">
              {items.map((s: any, i: number) => (
                <div key={i} className="flex items-center justify-between px-5 py-2.5">
                  <div className="flex items-center gap-2 min-w-0">
                    <Scissors className="w-3.5 h-3.5 text-slate-500 shrink-0" />
                    <span className="text-sm text-slate-200 truncate">{s.serviceName ?? s.name}</span>
                  </div>
                  <span className="text-sm font-medium text-white ml-3 shrink-0">{formatPrice(s.price)}</span>
                </div>
              ))}
            </div>
          )}

          {/* Total */}
          <div className="px-5 py-3.5 bg-white/3 flex items-center justify-between border-t border-white/5">
            <span className="text-sm font-semibold text-slate-200">Ukupno</span>
            <span className="text-xl font-bold text-white">{formatPrice(total)}</span>
          </div>
        </motion.div>

        {/* Notification info */}
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.3 }}
          className="flex items-start gap-3 p-3.5 rounded-xl bg-amber-500/8 border border-amber-500/20 mb-5"
        >
          <div className="w-8 h-8 rounded-lg bg-amber-500/15 flex items-center justify-center shrink-0 mt-0.5">
            <Bell className="w-4 h-4 text-amber-400" />
          </div>
          <div>
            <p className="text-xs font-semibold text-amber-300">Čeka se potvrda salona</p>
            <p className="text-xs text-slate-400 mt-0.5 leading-relaxed">
              Dobit ćete notifikaciju čim salon potvrdi termin. Obično traje nekoliko sekundi.
            </p>
          </div>
        </motion.div>

        <motion.button
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.4 }}
          onClick={onDone}
          className="w-full py-3.5 rounded-xl bg-rose-500 hover:bg-rose-600 text-white font-semibold transition-colors shadow-lg shadow-rose-500/20"
        >
          Prikaži moje termine
        </motion.button>
      </div>
    </div>
  )
}
