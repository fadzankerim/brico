import { useState } from "react"
import type { Appointment } from "../types/booking.types"
import AppointmentStatusBadge from "./AppointmentStatusBadge"
import ReviewModal from "./ReviewModal"
import { Calendar, Clock, MapPin, X } from "lucide-react"
import { formatDate, formatPrice, formatTime } from "../utils/dateUtils"



export default function AppointmentCard({
  appointment: a,
  onCancel,
  isPast,
  alreadyReviewed = false,
}: {
  appointment: Appointment
  onCancel: () => void
  isPast: boolean
  alreadyReviewed?: boolean
}) {
  const [confirmCancel, setConfirmCancel] = useState(false)
  const [reviewOpen, setReviewOpen] = useState(false)
 
  return (
    <div className="p-4 rounded-2xl bg-[#0F1623] border border-white/5 flex gap-4">
      {/* Left accent bar */}
      <div className={`w-1 rounded-full shrink-0 ${
        a.status === 'CONFIRMED' ? 'bg-emerald-500' :
        a.status === 'CANCELLED' ? 'bg-slate-600' :
        a.status === 'COMPLETED' ? 'bg-blue-500' :
        'bg-amber-500'
      }`} />
 
      {/* Avatar */}
      <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-slate-700 to-slate-600 flex items-center justify-center shrink-0 text-sm font-bold text-white">
        {a.hairdresserName.charAt(0)}
      </div>
 
      {/* Info */}
      <div className="flex-1 min-w-0">
        <div className="flex items-start justify-between gap-2">
          <div>
            <p className="font-medium text-white text-sm truncate">{a.salonName}</p>
            <p className="text-xs text-slate-400 mt-0.5">{a.hairdresserName} · {a.serviceName}</p>
          </div>
          <AppointmentStatusBadge status={a.status} />
        </div>
 
        <div className="flex flex-wrap items-center gap-3 mt-2 text-xs text-slate-500">
          <span className="flex items-center gap-1"><Calendar className="w-3 h-3" />{formatDate(a.startTime)}</span>
          <span className="flex items-center gap-1"><Clock className="w-3 h-3" />{formatTime(a.startTime)}</span>
          <span className="flex items-center gap-1"><MapPin className="w-3 h-3" />{a.salonAddress}</span>
          <span className="font-semibold text-white">{formatPrice(a.totalPrice ?? a.price ?? 0)}</span>
        </div>
      </div>
 
      {/* Actions */}
      {!isPast && a.status !== 'CANCELLED' && (
        <div className="shrink-0">
          {confirmCancel ? (
            <div className="flex gap-1">
              <button
                onClick={onCancel}
                className="px-2.5 py-1.5 rounded-lg bg-rose-500/10 text-rose-400 text-xs hover:bg-rose-500 hover:text-white transition-colors"
              >
                Potvrdi
              </button>
              <button
                onClick={() => setConfirmCancel(false)}
                className="px-2.5 py-1.5 rounded-lg bg-white/5 text-slate-400 text-xs hover:text-white transition-colors"
              >
                Odustani
              </button>
            </div>
          ) : (
            <button
              onClick={() => setConfirmCancel(true)}
              className="w-8 h-8 rounded-xl flex items-center justify-center text-slate-500 hover:text-rose-400 hover:bg-rose-500/10 transition-colors"
              title="Otkaži termin"
            >
              <X className="w-4 h-4" />
            </button>
          )}
        </div>
      )}
 
      {isPast && a.status === 'COMPLETED' && (
        <div className="shrink-0">
          {alreadyReviewed ? (
            <span className="px-3 py-1.5 rounded-lg bg-emerald-500/10 text-emerald-400 text-xs whitespace-nowrap">
              ✓ Ocijenjeno
            </span>
          ) : (
            <button
              onClick={() => setReviewOpen(true)}
              className="px-3 py-1.5 rounded-lg bg-amber-500/10 text-amber-400 text-xs hover:bg-amber-500/20 transition-colors whitespace-nowrap"
            >
              ★ Ocijeni
            </button>
          )}
        </div>
      )}

      <ReviewModal
        appointment={reviewOpen ? a : null}
        onClose={() => setReviewOpen(false)}
      />
    </div>
  )
}
