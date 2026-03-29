import { useEffect, useState } from "react"
import type { Appointment } from "../types/booking.types"
import type { Hairdresser, Service } from "../types/salon.typs"
import { useAvailability } from "../hooks/useBooking"
import { addDays, format, formatDuration } from "date-fns"
import { AnimatePresence, motion } from "motion/react"
import { Calendar, ChevronDown, Clock, FileText, Scissors, User, X } from "lucide-react"
import { formatPrice } from "../utils/dateUtils"
import { cn } from "../lib/utils"


interface AddAppointmentModalProps {
    open: boolean
    onClose: () => void
    onSave: (data: {
        hairdresserId: number
        serviceId: number
        startTime: string
        notes?: string
        clientName?: string
        clientPhone?: string
    }) => void
    hairdressers: Hairdresser[]
    services: Service[]
    editAppointment?: Appointment | null
    isSaving?: boolean
}


export default function AddAppointmentModal({ open, onClose, onSave, hairdressers, services, editAppointment, isSaving }: AddAppointmentModalProps) {

    const [hairdresserId, setHairdresserId] = useState<number | ''>('')
    const [serviceId, setServiceId] = useState<number | ''>('')
    const [date, setDate] = useState(format(new Date(), 'yyyy-MM-dd'))
    const [time, setTime] = useState('')
    const [clientName, setClientName] = useState('')
    const [clientPhone, setClientPhone] = useState('')
    const [notes, setNotes] = useState('')

    const { data: availability } = useAvailability(
        hairdresserId || undefined,
        date || undefined
    )
    const slots = availability?.slots ?? []

    useEffect(() => {
        if (editAppointment) {
            setHairdresserId(editAppointment.hairdresserId)
            setServiceId(editAppointment.serviceId)
            setDate(format(new Date(editAppointment.startTime), 'yyyy-MM-dd'))
            setTime(format(new Date(editAppointment.startTime), 'HH:mm'))
            setClientName(editAppointment.clientName)
            setClientPhone(editAppointment.clientPhone ?? '')
            setNotes(editAppointment.notes ?? '')
        }
    }, [editAppointment])


    // Reset on close
    useEffect(() => {
        if (!open) {
            setHairdresserId('')
            setServiceId('')
            setDate(format(new Date(), 'yyyy-MM-dd'))
            setTime('')
            setClientName('')
            setClientPhone('')
            setNotes('')
        }
    }, [open])

    const isValid = hairdresserId && serviceId && date && time && clientName

    function handleSave() {
        if (!isValid) return
        onSave({
            hairdresserId: Number(hairdresserId),
            serviceId: Number(serviceId),
            startTime: `${date}T${time}:00`,
            notes: notes || undefined,
            clientName,
            clientPhone: clientPhone || undefined,
        })
    }

    const days = Array.from({ length: 30 }, (_, i) => addDays(new Date(), i))

    return (
        <AnimatePresence>
            {open && (
                <>
                    <motion.div
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        exit={{ opacity: 0 }}
                        onClick={onClose}
                        className="fixed inset-0 bg-black/60 backdrop-blur-sm z-40"
                    />

                    <motion.div
                        initial={{ opacity: 0, y: 40, scale: 0.97 }}
                        animate={{ opacity: 1, y: 0, scale: 1 }}
                        exit={{ opacity: 0, y: 20, scale: 0.97 }}
                        transition={{ type: 'spring', damping: 28, stiffness: 280 }}
                        className="fixed z-50 inset-x-4 sm:inset-x-auto sm:left-1/2 sm:-translate-x-1/2 top-[5vh] sm:w-[480px] max-h-[90vh] overflow-y-auto bg-[#0F1623] rounded-2xl border border-white/10 shadow-2xl shadow-black/60"
                    >
                        {/* Header */}
                        <div className="sticky top-0 bg-[#0F1623] flex items-center justify-between px-5 py-4 border-b border-white/5 z-10">
                            <h2 className="font-display font-semibold text-white">
                                {editAppointment ? 'Uredi Termin' : 'Novi Termin'}
                            </h2>
                            <button onClick={onClose} className="w-8 h-8 rounded-xl flex items-center justify-center text-slate-500 hover:text-white hover:bg-white/8 transition-colors">
                                <X className="w-4 h-4" />
                            </button>
                        </div>

                        <div className="p-5 space-y-5">

                            {/* Client name */}
                            <Field icon={User} label="Ime klijenta">
                                <input
                                    value={clientName}
                                    onChange={(e) => setClientName(e.target.value)}
                                    placeholder="Ime i prezime..."
                                    className="w-full px-3 py-2.5 rounded-xl bg-white/5 border border-white/8 text-white placeholder:text-slate-600 text-sm focus:outline-none focus:border-rose-500/50 transition-all"
                                />
                            </Field>

                            {/* Client phone */}
                            <Field icon={FileText} label="Telefon klijenta (opciono)">
                                <input
                                    value={clientPhone}
                                    onChange={(e) => setClientPhone(e.target.value)}
                                    placeholder="+387 61 ..."
                                    className="w-full px-3 py-2.5 rounded-xl bg-white/5 border border-white/8 text-white placeholder:text-slate-600 text-sm focus:outline-none focus:border-rose-500/50 transition-all"
                                />
                            </Field>

                            {/* Hairdresser */}
                            <Field icon={User} label="Frizer">
                                <div className="relative">
                                    <select
                                        value={hairdresserId}
                                        onChange={(e) => { setHairdresserId(Number(e.target.value)); setTime('') }}
                                        className="w-full px-3 py-2.5 rounded-xl bg-white/5 border border-white/8 text-white text-sm focus:outline-none focus:border-rose-500/50 transition-all appearance-none cursor-pointer"
                                    >
                                        <option value="" className="bg-[#0F1623]">Odaberi frizera...</option>
                                        {hairdressers.map((h) => (
                                            <option key={h.id} value={h.id} className="bg-[#0F1623]">{h.fullName}</option>
                                        ))}
                                    </select>
                                    <ChevronDown className="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500 pointer-events-none" />
                                </div>
                            </Field>

                            {/* Service */}
                            <Field icon={Scissors} label="Usluga">
                                <div className="relative">
                                    <select
                                        value={serviceId}
                                        onChange={(e) => setServiceId(Number(e.target.value))}
                                        className="w-full px-3 py-2.5 rounded-xl bg-white/5 border border-white/8 text-white text-sm focus:outline-none focus:border-rose-500/50 transition-all appearance-none cursor-pointer"
                                    >
                                        <option value="" className="bg-[#0F1623]">Odaberi uslugu...</option>
                                        {services.map((s) => (
                                            <option key={s.id} value={s.id} className="bg-[#0F1623]">
                                                {s.name} · {formatDuration({minutes: s.durationMinutes % 60 })} · {formatPrice(s.price)}
                                            </option>
                                        ))}
                                    </select>
                                    <ChevronDown className="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500 pointer-events-none" />
                                </div>
                            </Field>

                            {/* Date */}
                            <Field icon={Calendar} label="Datum">
                                <div className="flex gap-2 overflow-x-auto pb-1 scrollbar-none">
                                    {days.map((d) => {
                                        const val = format(d, 'yyyy-MM-dd')
                                        const sel = date === val
                                        return (
                                            <button
                                                key={val}
                                                onClick={() => { setDate(val); setTime('') }}
                                                className={cn(
                                                    'flex flex-col items-center gap-0.5 px-2.5 py-2 rounded-xl border min-w-[44px] transition-all shrink-0',
                                                    sel ? 'bg-rose-500 border-rose-500 text-white' : 'bg-white/5 border-white/8 text-slate-300 hover:border-white/15'
                                                )}
                                            >
                                                <span className="text-[10px] opacity-70">{format(d, 'EEE').slice(0, 3)}</span>
                                                <span className="text-sm font-bold leading-none">{format(d, 'd')}</span>
                                            </button>
                                        )
                                    })}
                                </div>
                            </Field>

                            {/* Time slots */}
                            {hairdresserId && date && (
                                <Field icon={Clock} label="Slobodni Termini">
                                    {slots.length === 0 ? (
                                        <p className="text-sm text-slate-500 py-2">Nema slobodnih termina</p>
                                    ) : (
                                        <div className="grid grid-cols-5 gap-2">
                                            {slots.map((slot) => (
                                                <button
                                                    key={slot.time}
                                                    disabled={!slot.available}
                                                    onClick={() => setTime(slot.time)}
                                                    className={cn(
                                                        'py-2 rounded-xl text-xs font-medium transition-all',
                                                        !slot.available && 'opacity-30 cursor-not-allowed bg-slate-800 text-slate-600',
                                                        slot.available && time === slot.time && 'bg-rose-500 text-white',
                                                        slot.available && time !== slot.time && 'bg-white/5 border border-white/8 text-slate-300 hover:border-rose-500/30'
                                                    )}
                                                >
                                                    {slot.time}
                                                </button>
                                            ))}
                                        </div>
                                    )}
                                </Field>
                            )}

                            {/* Notes */}
                            <Field icon={FileText} label="Napomena (opciono)">
                                <textarea
                                    value={notes}
                                    onChange={(e) => setNotes(e.target.value)}
                                    placeholder="Posebni zahtjevi..."
                                    rows={3}
                                    className="w-full px-3 py-2.5 rounded-xl bg-white/5 border border-white/8 text-white placeholder:text-slate-600 text-sm focus:outline-none focus:border-rose-500/50 transition-all resize-none"
                                />
                            </Field>
                        </div>

                        {/* Footer */}
                        <div className="sticky bottom-0 bg-[#0F1623] px-5 py-4 border-t border-white/5 flex gap-3">
                            <button onClick={onClose} className="flex-1 py-2.5 rounded-xl bg-white/5 text-slate-300 text-sm hover:text-white hover:bg-white/8 transition-colors">
                                Odustani
                            </button>
                            <button
                                onClick={handleSave}
                                disabled={!isValid || isSaving}
                                className="flex-1 py-2.5 rounded-xl bg-rose-500 hover:bg-rose-600 disabled:opacity-40 disabled:cursor-not-allowed text-white text-sm font-semibold transition-colors flex items-center justify-center gap-2"
                            >
                                {isSaving ? (
                                    <><span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />Čuvam...</>
                                ) : editAppointment ? 'Spremi Izmjene' : 'Kreiraj Termin'}
                            </button>
                        </div>
                    </motion.div>
                </>
            )}
        </AnimatePresence>
    )
}

function Field({ icon: Icon, label, children }: { icon: React.ElementType; label: string; children: React.ReactNode }) {
  return (
    <div>
      <label className="flex items-center gap-1.5 text-xs font-medium text-slate-400 uppercase tracking-wider mb-2">
        <Icon className="w-3.5 h-3.5" /> {label}
      </label>
      {children}
    </div>
  )
}