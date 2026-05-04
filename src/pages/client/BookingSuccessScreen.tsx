import { CheckCircle, Mail, Receipt, Sparkles } from "lucide-react";
import type { Appointment } from "../../types/booking.types"
import {motion} from "motion/react"
import { formatDate, formatDateTime, formatPrice } from "../../utils/dateUtils";
import { format, formatDuration, parseISO } from "date-fns";


export default function BookingSuccessScreen({ appointment: appt, onDone }: { appointment: Appointment; onDone: () => void }) {
  const inv = appt.invoice
 
  return (
    <div className="min-h-screen bg-[#080C14] text-white flex items-center justify-center px-4 py-12">
      <div className="w-full max-w-md">
        <motion.div initial={{ scale: 0.7, opacity: 0 }} animate={{ scale: 1, opacity: 1 }}
          transition={{ type: 'spring', damping: 15, stiffness: 200 }} className="text-center mb-6"
        >
          <div className="w-20 h-20 rounded-full bg-emerald-500/15 border-2 border-emerald-500/30 flex items-center justify-center mx-auto mb-4">
            <CheckCircle className="w-10 h-10 text-emerald-400" />
          </div>
          <h1 className="font-display text-2xl font-bold text-white mb-1">Rezervacija Potvrđena!</h1>
          <p className="text-slate-400 text-sm">Račun je poslan na vašu email adresu</p>
        </motion.div>
 
        {/* Invoice card */}
        <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.2 }}
          className="bg-[#0F1623] rounded-2xl border border-white/8 overflow-hidden mb-4"
        >
          <div className="px-5 py-4 bg-gradient-to-r from-rose-900/40 to-rose-800/20 border-b border-white/5">
            <div className="flex items-start justify-between">
              <div>
                <div className="flex items-center gap-2 mb-1">
                  <Receipt className="w-4 h-4 text-rose-400" />
                  <span className="text-xs font-semibold text-rose-400 uppercase tracking-wider">Račun</span>
                </div>
                <p className="font-display font-bold text-white text-base">{appt.salonName}</p>
                <p className="text-xs text-slate-400">{appt.salonAddress}</p>
              </div>
              {inv && (
                <div className="text-right">
                  <p className="text-xs text-slate-500"># {inv.invoiceNumber}</p>
                  <p className="text-xs text-slate-500 mt-0.5">{formatDate(appt.createdAt)}</p>
                </div>
              )}
            </div>
          </div>
 
          <div className="px-5 py-3 border-b border-white/5">
            <div className="grid grid-cols-2 gap-3 text-sm">
              <div><p className="text-xs text-slate-500 mb-0.5">Frizer</p><p className="text-white font-medium">{appt.hairdresserName}</p></div>
              <div><p className="text-xs text-slate-500 mb-0.5">Datum</p><p className="text-white font-medium">{formatDateTime(appt.startTime)}</p></div>
              <div><p className="text-xs text-slate-500 mb-0.5">Kraj termina</p><p className="text-white font-medium">{format(parseISO(appt.endTime), 'HH:mm')}</p></div>
              <div><p className="text-xs text-slate-500 mb-0.5">Klijent</p><p className="text-white font-medium">{appt.clientName}</p></div>
            </div>
          </div>
 
          <div className="divide-y divide-white/5">
            {(appt.items ?? appt.services ?? []).map((s: any, i: number) => (
              <div key={i} className="flex items-center justify-between px-5 py-2.5">
                <div className="min-w-0">
                  <p className="text-sm text-white truncate">{s.serviceName}</p>
                  {s.durationMinutes > 0 && <p className="text-xs text-slate-500">{formatDuration(s.durationMinutes)}</p>}
                </div>
                <span className="text-sm text-white ml-3 shrink-0">{formatPrice(s.price)}</span>
              </div>
            ))}
          </div>
 
          <div className="px-5 py-3.5 bg-white/3 flex items-center justify-between">
            <span className="text-sm font-semibold text-slate-200">Ukupno</span>
            <span className="text-xl font-bold text-white">{formatPrice(appt.totalPrice ?? appt.price)}</span>
          </div>
 
          <div className="px-5 py-3 bg-amber-500/5 border-t border-amber-500/15">
            <p className="text-xs text-amber-400 flex items-center gap-1.5">
              <Sparkles className="w-3.5 h-3.5" />
              Plaćanje se vrši direktno u salonu gotovinom ili karticom.
            </p>
          </div>
        </motion.div>
 
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: 0.35 }}
          className="flex items-center gap-3 p-3.5 rounded-xl bg-rose-500/8 border border-rose-500/15 mb-5"
        >
          <div className="w-8 h-8 rounded-lg bg-rose-500/15 flex items-center justify-center shrink-0">
            <Mail className="w-4 h-4 text-rose-400" />
          </div>
          <div>
            <p className="text-xs font-semibold text-rose-300">Račun poslan na email</p>
            <p className="text-xs text-slate-500 mt-0.5">Primit ćete i podsjetnik 24h i 1h ranije.</p>
          </div>
        </motion.div>
 
        <motion.button initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: 0.4 }}
          onClick={onDone}
          className="w-full py-3.5 rounded-xl bg-rose-500 hover:bg-rose-600 text-white font-semibold transition-colors shadow-lg shadow-rose-500/20"
        >
          Prikaži moje termine
        </motion.button>
      </div>
    </div>
  )
}