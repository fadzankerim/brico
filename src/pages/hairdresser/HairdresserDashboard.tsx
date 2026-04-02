import { useState } from "react";
import { CalendarDays, Clock, DollarSign, Star, TrendingUp } from "lucide-react";
import StatCard from "../../components/StatCard";
import { useMyAppointments } from "../../hooks/useBooking";
import { useAuthStore } from "../../store/authStore";
import { formatDate, formatPrice, formatTime, isAppointmentPast } from "../../utils/dateUtils";
import { motion } from "motion/react";
import AppointmentStatusBadge from "../../components/AppointmentStatusBadge";
import { useSearchParams } from "react-router-dom";
import AppointmentCalendar from "../../components/AppointmentCalendar";
import type { Appointment } from "../../types/booking.types";
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';
import AddAppointmentModal from "../../components/AddAppointmentModal";

const MOCK_REVENUE = [
  { month: 'Jan', revenue: 1400 }, { month: 'Feb', revenue: 1800 },
  { month: 'Mar', revenue: 1500 }, { month: 'Apr', revenue: 2100 },
  { month: 'Maj', revenue: 2600 }, { month: 'Jun', revenue: 2300 },
];

const MOCK_PIE = [
  { name: 'Šišanje', value: 45, color: '#e94560' },
  { name: 'Bojenje', value: 30, color: '#f97316' },
  { name: 'Tretman', value: 15, color: '#3b82f6' },
  { name: 'Ostalo', value: 10, color: '#6b7280' },
];

const CustomTooltip = ({ active, payload, label }: any) => {
  if (!active || !payload?.length) return null;
  return (
    <div className="bg-[#0F1623] border border-white/10 rounded-xl p-3 text-sm shadow-xl">
      <p className="text-slate-400 mb-1">{label}</p>
      <p className="font-semibold text-white">€{payload[0].value.toLocaleString()}</p>
    </div>
  );
};

export default function HairdresserDashboard() {
  const { user } = useAuthStore();
  const [addModalOpen, setAddModal] = useState(false);
  const [editTarget, setEditTarget] = useState<Appointment | null>(null);

  const { data: appointments = [], isLoading } = useMyAppointments();
  const [searchParams] = useSearchParams();
  const search = searchParams.get('tab') ?? 'overview';

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
    .reduce((sum, a) => sum + a.price, 0);

  const monthrevenue = completed.reduce((sum, a) => sum + a.price, 0);

  const hairdressers = user ? [{
    id: user.id || 1,
    userId: user.id || 1,
    fullName: user.fullName || "Frizer",
    isActive: true
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
            <StatCard label="Ocjena" value="4.8 ★" icon={Star} accent="amber" />
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
            <StatCard label="Avg/termin"  value={completed.length ? formatPrice(monthrevenue / completed.length) : '€0'} icon={TrendingUp} accent="blue" />
          </div>

          <div className="p-6 rounded-2xl bg-[#0F1623] border border-white/5">
            <h3 className="font-display font-semibold text-white mb-5">Moj Prihod po Mjesecu</h3>
            <ResponsiveContainer width="100%" height={220}>
              <AreaChart data={MOCK_REVENUE}>
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
                  <Pie data={MOCK_PIE} cx={65} cy={65} innerRadius={38} outerRadius={62} paddingAngle={3} dataKey="value">
                    {MOCK_PIE.map((_, i) => <Cell key={i} fill={MOCK_PIE[i].color} />)}
                  </Pie>
                </PieChart>
                <div className="space-y-2.5 flex-1">
                  {MOCK_PIE.map((s) => (
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

      <AddAppointmentModal
        open={addModalOpen}
        onClose={() => { setAddModal(false); setEditTarget(null); }}
        onSave={() => { setAddModal(false); setEditTarget(null); }}
        hairdressers={hairdressers as any}
        services={[]}
        editAppointment={editTarget}
      />
    </div>
  );
}
