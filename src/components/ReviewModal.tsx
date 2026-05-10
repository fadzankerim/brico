import { useState } from "react";
import type { Appointment } from "../types/booking.types";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { reviewService } from "../services/review.service";
import { appointmentKeys } from "../hooks/useBooking";
import { salonKeys } from "../hooks/useSalons";
import { toast } from "sonner";
import { AnimatePresence, motion } from "motion/react";
import { Calendar, CheckCircle, Scissors, X } from "lucide-react";
import { formatDate, formatTime } from "../utils/dateUtils";
import StarRating from "./StarRating";



interface ReviewModalProps {
    appointment: Appointment | null
    onClose: () => void
    
}


const RATING_LABELS = ['', 'Loše', 'Ispod prosjeka', 'Dobro', 'Odlično', 'Izvrsno!']
const RATING_COLORS = ['', 'text-rose-400', 'text-orange-400', 'text-amber-400', 'text-lime-400', 'text-emerald-400']

export default function ReviewModal({appointment, onClose}: ReviewModalProps){

    const [rating,    setRating]    = useState(0)
  const [comment,   setComment]   = useState('')
  const [submitted, setSubmitted] = useState(false)
  const queryClient = useQueryClient()

  const { mutate: submit, isPending } = useMutation({
    mutationFn: () =>
      reviewService.create({
        clientId:      appointment!.clientId,
        clientName:    appointment!.clientName,
        salonId:       appointment!.salonId,
        hairdresserId: appointment!.hairdresserId,
        appointmentId: appointment!.id,
        rating,
        comment: comment || undefined,
      }),
    onSuccess: () => {
      setSubmitted(true)
      queryClient.invalidateQueries({ queryKey: appointmentKeys.mine() })
      queryClient.invalidateQueries({ queryKey: salonKeys.all })
    },
    onError: (err: any) => {
      if (err?.response?.status === 409) {
        toast.error('Recenzija za ovaj termin već postoji.')
        onClose()
      } else {
        toast.error('Greška pri slanju recenzije')
      }
    },
  })

  const isOpen = !!appointment
  const canSubmit = rating > 0 && (comment.length === 0 || comment.length >= 10)

  function handleClose() {
    setRating(0)
    setComment('')
    setSubmitted(false)
    onClose()
  }

  return (
    <AnimatePresence>
      {isOpen && (
        <>
          <motion.div
            initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
            onClick={handleClose}
            className="fixed inset-0 bg-black/60 backdrop-blur-sm z-40"
          />

          <motion.div
            initial={{ opacity: 0, y: 40, scale: 0.97 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: 20, scale: 0.97 }}
            transition={{ type: 'spring', damping: 28, stiffness: 280 }}
            className="fixed z-50 inset-x-4 sm:inset-x-auto sm:left-1/2 sm:-translate-x-1/2 top-1/2 sm:top-[12vh] -translate-y-1/2 sm:translate-y-0 sm:w-[440px] bg-[#0F1623] rounded-2xl border border-white/10 shadow-2xl shadow-black/60 overflow-hidden"
          >
            {submitted ? (
              <SuccessState onClose={handleClose} />
            ) : (
              <>
                {/* Header */}
                <div className="flex items-center justify-between px-5 py-4 border-b border-white/5">
                  <h2 className="font-display font-semibold text-white">Ocijeni posjetu</h2>
                  <button onClick={handleClose} className="w-8 h-8 rounded-xl flex items-center justify-center text-slate-500 hover:text-white hover:bg-white/8 transition-colors">
                    <X className="w-4 h-4" />
                  </button>
                </div>

                <div className="p-5 space-y-5">
                  {/* Appointment summary */}
                  {appointment && (
                    <div className="flex gap-3 p-3.5 rounded-xl bg-white/4 border border-white/6">
                      <div className="w-9 h-9 rounded-lg bg-gradient-to-br from-rose-500 to-rose-700 flex items-center justify-center text-white text-sm font-bold shrink-0">
                        {appointment.hairdresserName.charAt(0)}
                      </div>
                      <div className="min-w-0">
                        <p className="text-sm font-medium text-white truncate">{appointment.salonName}</p>
                        <div className="flex items-center gap-2 mt-0.5 text-xs text-slate-400">
                          <span className="flex items-center gap-1"><Scissors className="w-3 h-3" />{appointment.serviceName}</span>
                          <span className="text-slate-600">·</span>
                          <span className="flex items-center gap-1"><Calendar className="w-3 h-3" />{formatDate(appointment.startTime)} {formatTime(appointment.startTime)}</span>
                        </div>
                      </div>
                    </div>
                  )}

                  {/* Star rating */}
                  <div className="text-center py-2">
                    <p className="text-sm text-slate-400 mb-4">Kako biste ocjenili vaše iskustvo?</p>
                    <div className="flex justify-center mb-3">
                      <StarRating value={rating} onChange={setRating} size="lg" />
                    </div>
                    <AnimatePresence mode="wait">
                      {rating > 0 && (
                        <motion.p
                          key={rating}
                          initial={{ opacity: 0, y: 6 }}
                          animate={{ opacity: 1, y: 0 }}
                          exit={{ opacity: 0, y: -6 }}
                          transition={{ duration: 0.15 }}
                          className={`text-base font-semibold ${RATING_COLORS[rating]}`}
                        >
                          {RATING_LABELS[rating]}
                        </motion.p>
                      )}
                    </AnimatePresence>
                  </div>

                  {/* Comment */}
                  <div>
                    <div className="flex items-center justify-between mb-2">
                      <label className="text-xs font-medium text-slate-400 uppercase tracking-wider">Komentar</label>
                      <span className={`text-xs ${comment.length > 450 ? 'text-rose-400' : 'text-slate-600'}`}>
                        {comment.length}/500
                      </span>
                    </div>
                    <textarea
                      value={comment}
                      onChange={(e) => setComment(e.target.value.slice(0, 500))}
                      placeholder="Opišite vaše iskustvo... (opciono, min. 10 karaktera)"
                      rows={4}
                      className="w-full px-3.5 py-3 rounded-xl bg-white/5 border border-white/8 text-white placeholder:text-slate-600 text-sm focus:outline-none focus:border-rose-500/50 focus:ring-2 focus:ring-rose-500/10 transition-all resize-none"
                    />
                    {comment.length > 0 && comment.length < 10 && (
                      <p className="text-xs text-amber-400 mt-1">Minimum 10 karaktera</p>
                    )}
                  </div>
                </div>

                {/* Footer */}
                <div className="px-5 pb-5 flex gap-3">
                  <button onClick={handleClose} className="px-4 py-2.5 rounded-xl bg-white/5 text-slate-300 text-sm hover:text-white transition-colors">
                    Preskoci
                  </button>
                  <button
                    onClick={() => submit()}
                    disabled={!canSubmit || isPending}
                    className="flex-1 py-2.5 rounded-xl bg-rose-500 hover:bg-rose-600 disabled:opacity-40 disabled:cursor-not-allowed text-white text-sm font-semibold transition-colors flex items-center justify-center gap-2"
                  >
                    {isPending
                      ? <><span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />Šaljem...</>
                      : 'Pošalji Recenziju'}
                  </button>
                </div>
              </>
            )}
          </motion.div>
        </>
      )}
    </AnimatePresence>
  )
}


function SuccessState({ onClose }: { onClose: () => void }) {
  return (
    <motion.div
      initial={{ opacity: 0, scale: 0.9 }}
      animate={{ opacity: 1, scale: 1 }}
      className="p-8 text-center"
    >
      <motion.div
        initial={{ scale: 0 }}
        animate={{ scale: 1 }}
        transition={{ delay: 0.1, type: 'spring', damping: 15 }}
        className="w-16 h-16 rounded-full bg-emerald-500/15 border-2 border-emerald-500/30 flex items-center justify-center mx-auto mb-5"
      >
        <CheckCircle className="w-8 h-8 text-emerald-400" />
      </motion.div>
      <h3 className="font-display text-xl font-bold text-white mb-2">Hvala na recenziji!</h3>
      <p className="text-slate-400 text-sm mb-6 leading-relaxed">
        Vaša recenzija pomaže drugim klijentima da pronađu odličan salon.
      </p>
      <button
        onClick={onClose}
        className="px-8 py-2.5 rounded-xl bg-rose-500 hover:bg-rose-600 text-white font-semibold text-sm transition-colors"
      >
        Zatvori
      </button>
    </motion.div>
  )
}