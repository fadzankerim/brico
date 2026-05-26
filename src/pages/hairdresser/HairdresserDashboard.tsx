import { useState } from "react";
import { CalendarDays, Clock, DollarSign, Star, TrendingUp, Ban, Plus, Trash2, Scissors } from "lucide-react";
import StatCard from "../../components/StatCard";
import { useAuthStore } from "../../store/authStore";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { salonService } from "../../services/salon.service";
import { bookingService } from "../../services/booking.service";
import { appointmentKeys } from "../../hooks/useBooking";
import { toast } from "sonner";
import { formatDate, formatPrice, formatTime, isAppointmentPast } from "../../utils/dateUtils";
import { motion } from "motion/react";
import AppointmentStatusBadge from "../../components/AppointmentStatusBadge";
import { useSearchParams } from "react-router-dom";
import AppointmentCalendar from "../../components/AppointmentCalendar";
import type { Appointment } from "../../types/booking.types";
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';
import AddAppointmentModal from "../../components/AddAppointmentModal";

const PIE_COLORS_H = ['#e94560', '#f97316', '#3b82f6', '#10b981', '#6b7280']

const CustomTooltip = ({ active, payload, label }: any) => {
  if (!active || !payload?.length) return null;
  return (
    <div className="bg-[#0F1623] border border-white/10 rounded-xl p-3 text-sm shadow-xl">
      <p className="text-slate-400 mb-1">{label}</p>
      <p className="font-semibold text-white">{payload[0].value.toLocaleString()} KM</p>
    </div>
  );
};

export default function HairdresserDashboard() {
  const { user } = useAuthStore();
  const queryClient = useQueryClient();
  const [addModalOpen, setAddModal] = useState(false);
  const [editTarget, setEditTarget] = useState<Appointment | null>(null);
  const [cancelId, setCancelId] = useState<number | null>(null);
  const [unavailStart, setUnavailStart] = useState('');
  const [unavailEnd,   setUnavailEnd]   = useState('');
  const [unavailReason, setUnavailReason] = useState('');
  const [svcName, setSvcName]         = useState('');
  const [svcPrice, setSvcPrice]       = useState('');
  const [svcDuration, setSvcDuration] = useState('');

  // Dohvati hairdresser profil za prijavljenog korisnika
  const { data: hairdresserProfile } = useQuery({
    queryKey: ['hairdresser-profile'],
    queryFn:  () => salonService.getMyHairdresserProfile(),
    enabled:  !!user,
    staleTime: 1000 * 60 * 5,
  })

  // Dohvati termine za ovog frizera (po hairdresserId iz profila)
  const { data: appointments = [], isLoading } = useQuery({
    queryKey: ['appointments', 'hairdresser', hairdresserProfile?.id],
    queryFn:  () => bookingService.getHairdresserAppointments(hairdresserProfile!.id),
    enabled:  !!hairdresserProfile?.id,
    staleTime: 1000 * 60,
  })

  const { data: services = [] } = useQuery({
    queryKey: ['services', hairdresserProfile?.salonId],
    queryFn:  () => salonService.getServices(hairdresserProfile!.salonId),
    enabled:  !!hairdresserProfile?.salonId,
    staleTime: 1000 * 60 * 5,
  })

  const { data: salonInfo } = useQuery({
    queryKey: ['salon', hairdresserProfile?.salonId],
    queryFn:  () => salonService.getById(hairdresserProfile!.salonId),
    enabled:  !!hairdresserProfile?.salonId,
    staleTime: 1000 * 60 * 5,
  })
  const [searchParams] = useSearchParams();
  const search = searchParams.get('tab') ?? 'overview';

  // Unavailability
  const { data: unavailability = [] } = useQuery({
    queryKey: ['unavailability', hairdresserProfile?.salonId, hairdresserProfile?.id],
    queryFn:  () => salonService.getUnavailability(hairdresserProfile!.salonId, hairdresserProfile!.id),
    enabled:  !!hairdresserProfile?.id,
  })

  const addUnavail = useMutation({
    mutationFn: () => salonService.addUnavailability(hairdresserProfile!.salonId, hairdresserProfile!.id, {
      startTime: unavailStart, endTime: unavailEnd, reason: unavailReason || undefined,
    }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['unavailability'] })
      toast.success('Nedostupnost dodana')
      setUnavailStart(''); setUnavailEnd(''); setUnavailReason('')
    },
    onError: () => toast.error('Greška pri dodavanju'),
  })

  const removeUnavail = useMutation({
    mutationFn: (id: number) => salonService.deleteUnavailability(hairdresserProfile!.salonId, hairdresserProfile!.id, id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['unavailability'] }),
  })

  const cancelMutation = useMutation({
    mutationFn: (id: number) => bookingService.cancel(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['appointments', 'hairdresser', hairdresserProfile?.id] })
      toast.success('Termin otkazan')
      setCancelId(null)
    },
    onError: () => toast.error('Greška pri otkazivanju'),
  })

  const createAppt = useMutation({
    mutationFn: (data: { hairdresserId: number; serviceId: number; startTime: string; notes?: string; clientName?: string; clientPhone?: string }) => {
      const service = services.find((s: any) => s.id === data.serviceId)
      return bookingService.create({
        clientId:        user?.id,
        clientName:      data.clientName ?? 'Gost',
        clientPhone:     data.clientPhone,
        hairdresserId:   data.hairdresserId,
        hairdresserName: user?.fullName ?? '',
        salonId:         hairdresserProfile!.salonId,
        salonName:       salonInfo?.name ?? '',
        salonAddress:    salonInfo?.address,
        startTime:       data.startTime,
        notes:           data.notes,
        items: service ? [{ serviceId: service.id, serviceName: service.name, price: service.price, durationMinutes: service.durationMinutes }] : [],
      })
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: appointmentKeys.all })
      setAddModal(false)
      setEditTarget(null)
      toast.success('Termin kreiran')
    },
    onError: () => toast.error('Greška pri kreiranju termina'),
  })

  const addService = useMutation({
    mutationFn: () => salonService.addService(hairdresserProfile!.salonId, {
      name: svcName, price: Number(svcPrice), durationMinutes: Number(svcDuration),
    } as any),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['services', hairdresserProfile?.salonId] })
      toast.success('Usluga dodana')
      setSvcName(''); setSvcPrice(''); setSvcDuration('')
    },
    onError: () => toast.error('Greška pri dodavanju usluge'),
  })

  const deleteService = useMutation({
    mutationFn: (id: number) => salonService.deleteService(hairdresserProfile!.salonId, id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['services', hairdresserProfile?.salonId] })
      toast.success('Usluga uklonjena')
    },
    onError: () => toast.error('Greška pri uklanjanju usluge'),
  })

  const today = new Date().toDateString();
  const todayAppts = appointments.filter((a) => new Date(a.startTime).toDateString() === today && a.status !== 'CANCELLED');
  const upcoming = appointments.filter((a) => !isAppointmentPast(a.endTime) && a.status !== 'CANCELLED');
  const completed = appointments.filter((a) => isAppointmentPast(a.endTime) && a.status === 'COMPLETED');

  const weekRevenue = completed
    .filter((a) => {
      const d = new Date(a.startTime);
      const now = new Date();
      return now.getTime() - d.getTime() < 7 * 24 * 60 * 60 * 1000;
    })
    .reduce((sum, a) => sum + (a.totalPrice ?? a.price ?? 0), 0);

  const monthrevenue = completed.reduce((sum, a) => sum + (a.totalPrice ?? a.price ?? 0), 0)

  const MONTHS = ['Jan','Feb','Mar','Apr','Maj','Jun','Jul','Aug','Sep','Okt','Nov','Dec']
  const revenueChart = MONTHS.slice(0, new Date().getMonth() + 1).map(m => ({
    month: m,
    revenue: Math.round(completed.filter(a => MONTHS[new Date(a.startTime).getMonth()] === m)
      .reduce((s, a) => s + (a.totalPrice ?? a.price ?? 0), 0))
  }))

  const svcMap: Record<string, number> = {}
  completed.forEach(a => { const n = a.serviceName ?? 'Ostalo'; svcMap[n] = (svcMap[n] ?? 0) + 1 })
  const total = Object.values(svcMap).reduce((s, v) => s + v, 0) || 1
  const pieChart = Object.entries(svcMap).slice(0, 4).map(([name, count], i) => ({
    name, value: Math.round((count / total) * 100), color: PIE_COLORS_H[i]
  }))

  const hairdressers = hairdresserProfile ? [{
    id: hairdresserProfile.id,
    userId: user?.id || 1,
    fullName: user?.fullName || "Frizer",
    isActive: true,
  }] : [];

  return (
    <div className="max-w-7xl mx-auto space-y-8">
      <div>
        <h1 className="font-display text-2xl font-bold text-white">
          Zdravo, {user?.fullName?.split(' ')[0] || 'Frizer'} ✂
        </h1>
        <p className="text-slate-400 text-sm mt-1">
          {todayAppts.length > 0 ? `${todayAppts.length} termina danas` : 'Nema termina danas'}
        </p>
      </div>

      {search === 'overview' && (
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="space-y-8 max-w-4xl">
          {/* Stats */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
            <StatCard label="Danas" value={(todayAppts.length).toString()} icon={CalendarDays} accent="rose" />
            <StatCard label="Nadolazeći" value={(upcoming.length).toString()} icon={Clock} accent="blue" />
            <StatCard label="Sedmica" value={formatPrice(weekRevenue)} icon={DollarSign} accent="emerald" />
            <StatCard label="Ocjena" value={`${(hairdresserProfile?.avgRating ?? 0).toFixed(1)} ★`} icon={Star} accent="amber" />
          </div>

          {/* Today's appointments */}
          <div>
            <h2 className="font-display text-lg font-semibold text-white mb-4">Danas</h2>
            {isLoading ? (
              <div className="space-y-3">{[1, 2].map(i => <div key={i} className="h-20 rounded-2xl bg-slate-800 animate-pulse" />)}</div>
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
                      <p className="text-xs text-slate-400">{a.items?.[0]?.serviceName ?? a.serviceName ?? '—'}</p>
                    </div>
                    <div className="text-right shrink-0">
                      <p className="text-sm font-medium text-white">{formatTime(a.startTime)}</p>
                      <p className="text-xs text-slate-500">{formatPrice(a.totalPrice ?? a.price)}</p>
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
                    <p className="text-xs text-slate-400">{a.items?.[0]?.serviceName ?? a.serviceName ?? '—'} · {formatDate(a.startTime)} {formatTime(a.startTime)}</p>
                  </div>
                  <span className="text-sm font-semibold text-white shrink-0">{formatPrice(a.totalPrice ?? a.price)}</span>
                  {a.status !== 'CANCELLED' && a.status !== 'COMPLETED' && (
                    cancelId === a.id ? (
                      <div className="flex gap-1">
                        <button onClick={() => cancelMutation.mutate(a.id)} className="px-2 py-1 rounded-lg bg-rose-500/15 text-rose-400 text-xs hover:bg-rose-500 hover:text-white">Potvrdi</button>
                        <button onClick={() => setCancelId(null)} className="px-2 py-1 rounded-lg bg-white/5 text-slate-400 text-xs">✕</button>
                      </div>
                    ) : (
                      <button onClick={() => setCancelId(a.id)} title="Otkaži termin" className="w-7 h-7 rounded-lg flex items-center justify-center text-slate-500 hover:text-rose-400 hover:bg-rose-500/10 transition-colors shrink-0">
                        <Ban className="w-3.5 h-3.5" />
                      </button>
                    )
                  )}
                </motion.div>
              ))}
            </div>
          </div>
        </motion.div>
      )}

      {/* Calendar */}
      {search === 'calendar' && (
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="h-[700px]">
          <AppointmentCalendar
            appointments={appointments}
            hairdressers={hairdressers as any}
            onAddAppointment={() => { setEditTarget(null); setAddModal(true) }}
            onEditAppointment={(a) => { setEditTarget(a); setAddModal(true) }}
          />
        </motion.div>
      )}

      {/* Analytics */}
      {search === 'revenue' && (
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="space-y-6">
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <StatCard label="Ovaj mjesec" value={formatPrice(monthrevenue)} icon={DollarSign}   accent="emerald" change={{ value: 12 }} />
            <StatCard label="Termina"     value={(completed.length).toString()}          icon={CalendarDays} accent="rose"    change={{ value: 5 }} />
            <StatCard label="Avg/termin"  value={completed.length ? formatPrice(monthrevenue / completed.length) : formatPrice(0)} icon={TrendingUp} accent="blue" />
          </div>

          <div className="p-6 rounded-2xl bg-[#0F1623] border border-white/5">
            <h3 className="font-display font-semibold text-white mb-5">Moj Prihod po Mjesecu</h3>
            <ResponsiveContainer width="100%" height={220}>
              <AreaChart data={revenueChart.length > 0 ? revenueChart : [{ month: '-', revenue: 0 }]}>
                <defs>
                  <linearGradient id="grad" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%"  stopColor="#e94560" stopOpacity={0.3} />
                    <stop offset="95%" stopColor="#e94560" stopOpacity={0} />
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.04)" />
                <XAxis dataKey="month" tick={{ fill: '#64748b', fontSize: 12 }} axisLine={false} tickLine={false} />
                <YAxis tick={{ fill: '#64748b', fontSize: 12 }} axisLine={false} tickLine={false} tickFormatter={(v) => `${v} KM`} />
                <Tooltip content={<CustomTooltip />} />
                <Area type="monotone" dataKey="revenue" stroke="#e94560" strokeWidth={2} fill="url(#grad)" dot={false} activeDot={{ r: 5, fill: '#e94560' }} />
              </AreaChart>
            </ResponsiveContainer>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="p-6 rounded-2xl bg-[#0F1623] border border-white/5">
              <h3 className="font-display font-semibold text-white mb-5">Moje Najpopularnije Usluge</h3>
              <div className="flex items-center gap-6">
                <PieChart width={140} height={140}>
                  <Pie data={pieChart.length > 0 ? pieChart : [{ name: 'Nema', value: 1, color: '#334155' }]} cx={65} cy={65} innerRadius={38} outerRadius={62} paddingAngle={3} dataKey="value">
                    {(pieChart.length > 0 ? pieChart : [{ color: '#334155' }]).map((s: any, i: number) => <Cell key={i} fill={s.color} />)}
                  </Pie>
                </PieChart>
                <div className="space-y-2.5 flex-1">
                  {(pieChart.length > 0 ? pieChart : []).map((s: any) => (
                    <div key={s.name} className="flex items-center justify-between">
                      <div className="flex items-center gap-2">
                        <div className="w-2.5 h-2.5 rounded-full" style={{ backgroundColor: s.color }} />
                        <span className="text-sm text-slate-300">{s.name}</span>
                      </div>
                      <span className="text-sm font-semibold text-white">{s.value}%</span>
                    </div>
                  ))}
                </div>
              </div>
            </div>
            
            <div className="p-6 rounded-2xl bg-[#0F1623] border border-white/5">
                <h3 className="font-display font-semibold text-white mb-2">Performanse</h3>
                <p className="text-sm text-slate-400 mb-6">Tvoj napredak prema mjesečnim ciljevima.</p>
                
                <div className="space-y-5">
                    <div>
                        <div className="flex justify-between text-sm mb-2">
                            <span className="text-white">Cilj prihoda</span>
                            <span className="text-emerald-400 font-medium">85%</span>
                        </div>
                        <div className="h-2 bg-white/5 rounded-full overflow-hidden">
                            <div className="h-full bg-emerald-500 rounded-full" style={{ width: '85%' }} />
                        </div>
                    </div>
                    <div>
                        <div className="flex justify-between text-sm mb-2">
                            <span className="text-white">Zadržavanje klijenata</span>
                            <span className="text-blue-400 font-medium">92%</span>
                        </div>
                        <div className="h-2 bg-white/5 rounded-full overflow-hidden">
                            <div className="h-full bg-blue-500 rounded-full" style={{ width: '92%' }} />
                        </div>
                    </div>
                    <div>
                        <div className="flex justify-between text-sm mb-2">
                            <span className="text-white">Pozitivne recenzije</span>
                            <span className="text-amber-400 font-medium">98%</span>
                        </div>
                        <div className="h-2 bg-white/5 rounded-full overflow-hidden">
                            <div className="h-full bg-amber-500 rounded-full" style={{ width: '98%' }} />
                        </div>
                    </div>
                </div>
            </div>
          </div>
        </motion.div>
      )}

      {/* Settings tab */}
      {search === 'settings' && hairdresserProfile && (
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="max-w-2xl space-y-8">

          {/* ── Usluge ── */}
          <div className="space-y-4">
            <div>
              <h3 className="font-semibold text-white mb-1">Usluge salona</h3>
              <p className="text-xs text-slate-500">Dodaj ili ukloni usluge koje salon nudi klijentima.</p>
            </div>

            {/* Add service form */}
            <div className="p-5 rounded-2xl bg-[#0F1623] border border-white/5 space-y-4">
              <h4 className="text-sm font-medium text-white">Dodaj novu uslugu</h4>
              <div className="grid grid-cols-1 gap-3">
                <input
                  type="text"
                  value={svcName}
                  onChange={e => setSvcName(e.target.value)}
                  placeholder="Naziv usluge (npr. Šišanje)"
                  className="w-full px-3 py-2 rounded-xl bg-white/5 border border-white/10 text-white text-sm placeholder:text-slate-600 focus:outline-none focus:border-rose-500/50"
                />
                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <label className="text-xs text-slate-500 mb-1 block">Cijena (KM)</label>
                    <input
                      type="number"
                      value={svcPrice}
                      onChange={e => setSvcPrice(e.target.value)}
                      placeholder="15"
                      min="0"
                      className="w-full px-3 py-2 rounded-xl bg-white/5 border border-white/10 text-white text-sm placeholder:text-slate-600 focus:outline-none focus:border-rose-500/50"
                    />
                  </div>
                  <div>
                    <label className="text-xs text-slate-500 mb-1 block">Trajanje (min)</label>
                    <input
                      type="number"
                      value={svcDuration}
                      onChange={e => setSvcDuration(e.target.value)}
                      placeholder="30"
                      min="5"
                      className="w-full px-3 py-2 rounded-xl bg-white/5 border border-white/10 text-white text-sm placeholder:text-slate-600 focus:outline-none focus:border-rose-500/50"
                    />
                  </div>
                </div>
              </div>
              <button
                onClick={() => addService.mutate()}
                disabled={!svcName.trim() || !svcPrice || !svcDuration || addService.isPending}
                className="flex items-center gap-2 px-4 py-2 rounded-xl bg-rose-500 hover:bg-rose-600 disabled:opacity-40 text-white text-sm font-medium transition-colors"
              >
                <Plus className="w-4 h-4" /> Dodaj uslugu
              </button>
            </div>

            {/* Existing services */}
            {services.length > 0 && (
              <div className="space-y-2">
                {services.map((s: any) => (
                  <div key={s.id} className="flex items-center gap-3 p-4 rounded-xl bg-[#0F1623] border border-white/5">
                    <Scissors className="w-4 h-4 text-rose-400 shrink-0" />
                    <div className="flex-1 min-w-0">
                      <p className="text-sm text-white">{s.name}</p>
                      <p className="text-xs text-slate-500">{s.durationMinutes} min · {s.price} KM</p>
                    </div>
                    <button
                      onClick={() => deleteService.mutate(s.id)}
                      className="w-7 h-7 rounded-lg flex items-center justify-center text-slate-500 hover:text-rose-400 hover:bg-rose-500/10 transition-colors"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* ── Nedostupnost ── */}
          <div className="space-y-4">
            <div>
              <h3 className="font-semibold text-white mb-1">Moja nedostupnost</h3>
              <p className="text-xs text-slate-500">Dodaj periode kada nisi dostupan — mušterije neće moći zakazivati u tim terminima.</p>
            </div>

          {/* Add new */}
          <div className="p-5 rounded-2xl bg-[#0F1623] border border-white/5 space-y-4">
            <h4 className="text-sm font-medium text-white">Dodaj nedostupni period</h4>
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="text-xs text-slate-500 mb-1 block">Od</label>
                <input type="datetime-local" value={unavailStart} onChange={e => setUnavailStart(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl bg-white/5 border border-white/10 text-white text-sm focus:outline-none focus:border-rose-500/50" />
              </div>
              <div>
                <label className="text-xs text-slate-500 mb-1 block">Do</label>
                <input type="datetime-local" value={unavailEnd} onChange={e => setUnavailEnd(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl bg-white/5 border border-white/10 text-white text-sm focus:outline-none focus:border-rose-500/50" />
              </div>
            </div>
            <input type="text" value={unavailReason} onChange={e => setUnavailReason(e.target.value)}
              placeholder="Razlog (opcionalno)"
              className="w-full px-3 py-2 rounded-xl bg-white/5 border border-white/10 text-white text-sm placeholder:text-slate-600 focus:outline-none focus:border-rose-500/50" />
            <button
              onClick={() => addUnavail.mutate()}
              disabled={!unavailStart || !unavailEnd || addUnavail.isPending}
              className="flex items-center gap-2 px-4 py-2 rounded-xl bg-rose-500 hover:bg-rose-600 disabled:opacity-40 text-white text-sm font-medium transition-colors"
            >
              <Plus className="w-4 h-4" /> Dodaj period
            </button>
          </div>

          {/* List */}
          {unavailability.length > 0 && (
            <div className="space-y-2">
              {unavailability.map((u: any) => (
                <div key={u.id} className="flex items-center gap-3 p-4 rounded-xl bg-[#0F1623] border border-white/5">
                  <Clock className="w-4 h-4 text-amber-400 shrink-0" />
                  <div className="flex-1 min-w-0">
                    <p className="text-sm text-white">{u.startTime?.replace('T', ' ')} – {u.endTime?.replace('T', ' ')}</p>
                    {u.reason && <p className="text-xs text-slate-500">{u.reason}</p>}
                  </div>
                  <button onClick={() => removeUnavail.mutate(u.id)} className="w-7 h-7 rounded-lg flex items-center justify-center text-slate-500 hover:text-rose-400 hover:bg-rose-500/10 transition-colors">
                    <Trash2 className="w-3.5 h-3.5" />
                  </button>
                </div>
              ))}
            </div>
          )}
          </div>{/* end nedostupnost */}
        </motion.div>
      )}

      <AddAppointmentModal
        open={addModalOpen}
        onClose={() => { setAddModal(false); setEditTarget(null); }}
        onSave={(data) => createAppt.mutate(data)}
        hairdressers={hairdressers as any}
        services={services}
        salonId={hairdresserProfile?.salonId}
        editAppointment={editTarget}
        isSaving={createAppt.isPending}
      />
    </div>
  );
}
