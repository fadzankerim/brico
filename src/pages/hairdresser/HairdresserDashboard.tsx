import { CalendarDays, Clock, DollarSign, Star } from "lucide-react";
import StatCard from "../../components/StatCard";
import { useMyAppointments } from "../../hooks/useBooking";
import { useAuthStore } from "../../store/authStore"
import { formatDate, formatPrice, formatTime, isAppointmentPast } from "../../utils/dateUtils";
import {motion} from "motion/react"
import AppointmentStatusBadge from "../../components/AppointmentStatusBadge";

export default function HairdresserDashboard(){

    const { user } = useAuthStore();

    const { data: appointments = [], isLoading } = useMyAppointments();

    const today = new Date().toDateString();
    const todayAppts = appointments.filter((a) => new Date(a.startTime).toDateString() === today && a.status !== 'CANCELLED')

    const upcoming = appointments.filter((a) => !isAppointmentPast(a.endTime) && a.status !== 'CANCELLED')

    const completed = appointments.filter((a) => isAppointmentPast(a.endTime) && a.status === 'COMPLETED')

    const weekRevenue = completed
    .filter((a) => {
      const d = new Date(a.startTime)
      const now = new Date()
      return now.getTime() - d.getTime() < 7 * 24 * 60 * 60 * 1000
    })
    .reduce((sum, a) => sum + a.price, 0)

    return (
        <div className="max-w-4xl mx-auto space-y-8">
      <div>
        <h1 className="font-display text-2xl font-bold text-white">
          Zdravo, {user?.fullName.split(' ')[0]} ✂
        </h1>
        <p className="text-slate-400 text-sm mt-1">
          {todayAppts.length > 0 ? `${todayAppts.length} termina danas` : 'Nema termina danas'}
        </p>
      </div>
 
      {/* Stats */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
        <StatCard label="Danas" value={(todayAppts.length).toString()} icon={CalendarDays} accent="rose" />
        <StatCard label="Nadolazeći" value={(upcoming.length).toString()} icon={Clock} accent="blue" />
        <StatCard label="Sedmica" value={formatPrice(weekRevenue)} icon={DollarSign} accent="emerald" />
        <StatCard label="Ocjena" value="4.8 ★" icon={Star} accent="amber" />
      </div>
 
      {/* Today's appointments */}
      <div>
        <h2 className="font-display text-lg font-semibold text-white mb-4">Danas</h2>
        {isLoading ? (
          <div className="space-y-3">{[1,2].map(i => <div key={i} className="h-20 rounded-2xl bg-slate-800 animate-pulse" />)}</div>
        ) : todayAppts.length === 0 ? (
          <div className="py-10 text-center text-slate-500 text-sm bg-[#0F1623] rounded-2xl border border-white/5">
            Nema termina za danas
          </div>
        ) : (
          <div className="space-y-3">
            {todayAppts.map((a, i) => (
              <motion.div key={a.id} initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: i * 0.05 }}
                className="flex items-center gap-4 p-4 rounded-2xl bg-[#0F1623] border border-white/5"
              >
                <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-slate-700 to-slate-600 flex items-center justify-center text-sm font-bold text-white shrink-0">
                  {a.clientName.charAt(0)}
                </div>
                <div className="flex-1 min-w-0">
                  <p className="font-medium text-white text-sm">{a.clientName}</p>
                  <p className="text-xs text-slate-400">{a.serviceName}</p>
                </div>
                <div className="text-right shrink-0">
                  <p className="text-sm font-medium text-white">{formatTime(a.startTime)}</p>
                  <p className="text-xs text-slate-500">{formatPrice(a.price)}</p>
                </div>
                <AppointmentStatusBadge status={a.status} />
              </motion.div>
            ))}
          </div>
        )}
      </div>
 
      {/* Upcoming */}
      <div>
        <h2 className="font-display text-lg font-semibold text-white mb-4">Nadolazeći Termini</h2>
        <div className="space-y-3">
          {upcoming.slice(0, 5).map((a, i) => (
            <motion.div key={a.id} initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: i * 0.05 }}
              className="flex items-center gap-4 p-4 rounded-xl bg-[#0F1623] border border-white/5"
            >
              <div className="w-9 h-9 rounded-lg bg-slate-700 flex items-center justify-center text-sm font-bold text-white shrink-0">
                {a.clientName.charAt(0)}
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium text-white">{a.clientName}</p>
                <p className="text-xs text-slate-400">{a.serviceName} · {formatDate(a.startTime)} {formatTime(a.startTime)}</p>
              </div>
              <span className="text-sm font-semibold text-white shrink-0">{formatPrice(a.price)}</span>
            </motion.div>
          ))}
        </div>
      </div>
    </div>

    )

}