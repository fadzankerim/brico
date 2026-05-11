import { useState } from "react";
import { useAuthStore } from "../../store/authStore"
import { useCancelAppointment, useMyAppointments } from "../../hooks/useBooking";
import { useFavorites } from "../../hooks/useSalons";
import { useQuery } from "@tanstack/react-query";
import { reviewService } from "../../services/review.service";
import { formatDate, formatTime, isAppointmentPast } from "../../utils/dateUtils";
import StatCard from "../../components/StatCard";
import { Calendar, Clock, Scissors, Star } from "lucide-react";
import EmptyState from "../../components/EmptyState";
import AppointmentCard from "../../components/AppointmentCard";
import { Link, useSearchParams } from "react-router-dom";
import {motion} from "motion/react"


type Filter = 'upcoming' | 'past' | 'all'

export default function ClientDashboard() {

    const { user } = useAuthStore();
    const [filter, setFilter] = useState<Filter>('upcoming')
    const [searchParams] = useSearchParams()
    const { data: appointments = [], isLoading } = useMyAppointments();
    const { data: favorites = [] } = useFavorites();

    const cancelAppointment = useCancelAppointment();

    // Dohvati appointmentId-eve za koje klijent već ima recenziju
    const { data: myReviews = [] } = useQuery({
        queryKey: ['my-reviews', user?.id],
        queryFn:  () => user ? reviewService.getClientReviews(user.id) : Promise.resolve([]),
        enabled:  !!user,
        staleTime: 1000 * 60 * 5,
    })
    const reviewedAppointmentIds = new Set(myReviews.map((r: any) => r.appointmentId).filter(Boolean))


    const upcoming = appointments.filter((a) =>
        !isAppointmentPast(a.endTime) && a.status !== 'CANCELLED'
    )
    const past = appointments.filter((a) =>
        isAppointmentPast(a.endTime) || a.status === 'CANCELLED'
    )
    const displayed = filter === 'upcoming' ? upcoming : filter === 'past' ? past : appointments

    const nextAppointment = upcoming[0]

    return (
         <div className="max-w-4xl mx-auto space-y-8">
      {/* Greeting */}
      <div>
        <h1 className="font-display text-2xl font-bold text-white">
          Zdravo, {user?.fullName.split(' ')[0]} 👋
        </h1>
        <p className="text-slate-400 text-sm mt-1">
          {nextAppointment
            ? `Sljedeći termin: ${formatDate(nextAppointment.startTime)} u ${formatTime(nextAppointment.startTime)}`
            : 'Nemate nadolazećih termina'}
        </p>
      </div>
 
      {/* Stats */}
      <div className="grid grid-cols-2 sm:grid-cols-3 gap-4">
        <StatCard
          label="Nadolazeći termini"
          value={(upcoming.length).toString()}
          icon={Calendar}
          accent="rose"
        />
        <StatCard
          label="Prošli termini"
          value={(past.length).toString()}
          icon={Clock}
          accent="blue"
        />
        <StatCard
          label="Omiljeni saloni"
          value={(favorites.length).toString()}
          icon={Star}
          accent="amber"
          className="col-span-2 sm:col-span-1"
        />
      </div>
 
      {/* Appointments */}
      <div>
        {/* Filter tabs */}
        <div className="flex items-center gap-1 mb-5 bg-white/3 p-1 rounded-xl w-fit">
          {([
            { key: 'upcoming', label: 'Nadolazeći' },
            { key: 'past',     label: 'Prošli' },
            { key: 'all',      label: 'Svi' },
          ] as { key: Filter; label: string }[]).map((f) => (
            <button
              key={f.key}
              onClick={() => setFilter(f.key)}
              className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
                filter === f.key
                  ? 'bg-[#0F1623] text-white shadow-sm'
                  : 'text-slate-400 hover:text-white'
              }`}
            >
              {f.label}
            </button>
          ))}
        </div>
 
        {/* List */}
        {isLoading ? (
          <div className="space-y-3">
            {[1, 2, 3].map((i) => (
              <div key={i} className="h-24 rounded-2xl bg-slate-800 animate-pulse" />
            ))}
          </div>
        ) : displayed.length === 0 ? (
          <EmptyState />
        ) : (
          <div className="space-y-3">
            {displayed.map((appt, i) => (
              <motion.div
                key={appt.id}
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: i * 0.05 }}
              >
                <AppointmentCard
                  appointment={appt}
                  onCancel={() => cancelAppointment.mutate(appt.id)}
                  isPast={isAppointmentPast(appt.endTime)}
                  alreadyReviewed={reviewedAppointmentIds.has(appt.id)}
                />
              </motion.div>
            ))}
          </div>
        )}
      </div>
 
      {/* Favorites quick access */}
      {favorites.length > 0 && (
        <div>
          <div className="flex items-center justify-between mb-4">
            <h2 className="font-display text-lg font-semibold text-white">Omiljeni Saloni</h2>
            <Link to="/favorites" className="text-xs text-rose-400 hover:text-rose-300">Prikaži sve</Link>
          </div>
          <div className="flex gap-3 overflow-x-auto pb-2 scrollbar-none">
            {favorites.slice(0, 5).map((fav) => (
              <Link
                key={fav.id}
                to={`/salons/${fav.salon.slug}`}
                className="shrink-0 w-36 p-3 rounded-xl bg-[#0F1623] border border-white/5 hover:border-white/10 transition-colors"
              >
                <div className="w-full h-20 rounded-lg bg-gradient-to-br from-slate-700 to-slate-800 mb-2 flex items-center justify-center">
                  <Scissors className="w-6 h-6 text-slate-600" />
                </div>
                <p className="text-xs font-medium text-white truncate">{fav.salon.name}</p>
                <p className="text-xs text-slate-500">{fav.salon.city}</p>
              </Link>
            ))}
          </div>
        </div>
      )}
    </div>
    )

}