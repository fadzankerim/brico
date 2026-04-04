import { useState } from "react"
import { useAuthStore } from "../../store/authStore"
import type { Appointment } from "../../types/booking.types"
import type { Salon } from "../../types/salon.typs"
import { useSalonAppointments } from "../../hooks/useBooking"
import { useMySalon, salonKeys } from "../../hooks/useSalons"
import { CalendarDays, DollarSign, Star, TrendingUp, Users, Building2, Edit2, Check, X } from "lucide-react"
import StatCard from "../../components/StatCard"
import { formatPrice, formatTime } from "../../utils/dateUtils"
import { motion } from "motion/react"
import AppointmentStatusBadge from "../../components/AppointmentStatusBadge"
import AppointmentCalendar from "../../components/AppointmentCalendar"
import StaffManagementPanel from "./StaffManagementPanel"
import ServiceManagementPanel from "./ServiceManagementPanel"
import SalonPhotosPanel from "../../components/SalonPhotosPanel"
import { useSearchParams } from "react-router-dom"
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts'
import AddAppointmentModal from "../../components/AddAppointmentModal"
import { useMutation, useQueryClient } from "@tanstack/react-query"
import { salonService } from "../../services/salon.service"
import { toast } from "sonner"

const MOCK_REVENUE = [
  { month: 'Jan', revenue: 2400 }, { month: 'Feb', revenue: 3100 },
  { month: 'Mar', revenue: 2800 }, { month: 'Apr', revenue: 3600 },
  { month: 'Maj', revenue: 4200 }, { month: 'Jun', revenue: 3900 },
]
const MOCK_PIE = [
  { name: 'Šišanje', value: 38, color: '#e94560' },
  { name: 'Bojenje', value: 28, color: '#f97316' },
  { name: 'Tretman', value: 18, color: '#3b82f6' },
  { name: 'Ostalo',  value: 16, color: '#6b7280' },
]

const CITIES = ['Sarajevo', 'Mostar', 'Banja Luka', 'Tuzla', 'Zenica', 'Bijeljina', 'Brčko', 'Trebinje']

const CustomTooltip = ({ active, payload, label }: any) => {
  if (!active || !payload?.length) return null
  return (
    <div className="bg-[#0F1623] border border-white/10 rounded-xl p-3 text-sm shadow-xl">
      <p className="text-slate-400 mb-1">{label}</p>
      <p className="font-semibold text-white">€{payload[0].value.toLocaleString()}</p>
    </div>
  )
}

function DashboardSkeleton() {
  return (
    <div className="max-w-7xl mx-auto animate-pulse space-y-6">
      <div className="h-8 w-48 bg-white/5 rounded-xl" />
      <div className="h-4 w-64 bg-white/5 rounded-xl" />
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        {[...Array(4)].map((_, i) => (
          <div key={i} className="h-28 bg-white/5 rounded-2xl" />
        ))}
      </div>
      <div className="h-64 bg-white/5 rounded-2xl" />
    </div>
  )
}

function NoSalonState() {
  return (
    <div className="max-w-7xl mx-auto flex flex-col items-center justify-center py-24 text-center">
      <Building2 className="w-14 h-14 text-slate-600 mb-4" />
      <h2 className="font-display text-xl font-semibold text-white mb-2">Nema salona</h2>
      <p className="text-slate-400 text-sm max-w-sm">
        Vaš račun još nema pridružen salon. Kontaktirajte administratora ili se registrujte kao vlasnik salona.
      </p>
    </div>
  )
}

function SettingsTab({ salon }: { salon: Salon }) {
  const queryClient = useQueryClient()
  const [editing, setEditing] = useState(false)
  const [form, setForm] = useState({
    name:        salon.name,
    description: salon.description ?? '',
    city:        salon.city,
    address:     salon.address,
    phone:       salon.phone ?? '',
    website:     salon.website ?? '',
  })

  const updateMutation = useMutation({
    mutationFn: (data: typeof form) => salonService.update(salon.id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: salonKeys.mine(undefined) })
      queryClient.invalidateQueries({ queryKey: ['salons', 'mine'] })
      queryClient.invalidateQueries({ queryKey: salonKeys.detail(salon.id) })
      toast.success('Podaci salona ažurirani')
      setEditing(false)
    },
    onError: () => toast.error('Greška pri čuvanju podataka'),
  })

  const field = (label: string, key: keyof typeof form, type: 'input' | 'textarea' | 'select' = 'input') => (
    <div>
      <label className="text-xs text-slate-500 uppercase tracking-wide mb-1.5 block">{label}</label>
      {type === 'textarea' ? (
        <textarea
          disabled={!editing}
          value={form[key]}
          onChange={e => setForm(f => ({ ...f, [key]: e.target.value }))}
          rows={3}
          className="w-full px-3 py-2.5 rounded-xl bg-white/5 border border-white/10 text-white text-sm placeholder:text-slate-600 focus:outline-none focus:border-rose-500/50 transition-all disabled:opacity-50 resize-none"
        />
      ) : type === 'select' ? (
        <select
          disabled={!editing}
          value={form[key]}
          onChange={e => setForm(f => ({ ...f, [key]: e.target.value }))}
          className="w-full px-3 py-2.5 rounded-xl bg-[#0F1623] border border-white/10 text-white text-sm focus:outline-none focus:border-rose-500/50 transition-all disabled:opacity-50"
        >
          {CITIES.map(c => <option key={c} value={c}>{c}</option>)}
        </select>
      ) : (
        <input
          disabled={!editing}
          value={form[key]}
          onChange={e => setForm(f => ({ ...f, [key]: e.target.value }))}
          className="w-full px-3 py-2.5 rounded-xl bg-white/5 border border-white/10 text-white text-sm placeholder:text-slate-600 focus:outline-none focus:border-rose-500/50 transition-all disabled:opacity-50"
        />
      )}
    </div>
  )

  return (
    <div className="max-w-2xl space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h3 className="font-semibold text-white">Postavke salona</h3>
          <p className="text-xs text-slate-500 mt-0.5">Uredite podatke vašeg salona</p>
        </div>
        {editing ? (
          <div className="flex gap-2">
            <button
              onClick={() => { setEditing(false); setForm({ name: salon.name, description: salon.description ?? '', city: salon.city, address: salon.address, phone: salon.phone ?? '', website: salon.website ?? '' }) }}
              className="flex items-center gap-1.5 px-3 py-2 rounded-xl border border-white/10 text-slate-400 hover:text-white text-sm transition-colors"
            >
              <X className="w-4 h-4" /> Odustani
            </button>
            <button
              onClick={() => updateMutation.mutate(form)}
              disabled={updateMutation.isPending}
              className="flex items-center gap-1.5 px-3 py-2 rounded-xl bg-rose-500 hover:bg-rose-600 text-white text-sm font-medium transition-colors disabled:opacity-50"
            >
              <Check className="w-4 h-4" /> Sačuvaj
            </button>
          </div>
        ) : (
          <button
            onClick={() => setEditing(true)}
            className="flex items-center gap-1.5 px-3 py-2 rounded-xl bg-white/5 border border-white/10 text-slate-300 hover:text-white text-sm transition-colors"
          >
            <Edit2 className="w-4 h-4" /> Uredi
          </button>
        )}
      </div>

      <div className="p-5 rounded-2xl bg-[#0F1623] border border-white/5 space-y-4">
        {field('Naziv salona', 'name')}
        {field('Opis', 'description', 'textarea')}
        <div className="grid grid-cols-2 gap-4">
          {field('Grad', 'city', 'select')}
          {field('Adresa', 'address')}
        </div>
        <div className="grid grid-cols-2 gap-4">
          {field('Telefon', 'phone')}
          {field('Web stranica', 'website')}
        </div>
      </div>
    </div>
  )
}

export default function OwnerDashboard() {
  const { user } = useAuthStore()
  const [searchParams] = useSearchParams()
  const [addModalOpen, setAddModal] = useState(false)
  const [editTarget, setEditTarget] = useState<Appointment | null>(null)

  const tab = searchParams.get('tab') ?? 'overview'

  const { data: salon, isLoading: salonLoading } = useMySalon()
  const { data: appointments = [] } = useSalonAppointments(salon?.id ?? 0)

  if (salonLoading) return <DashboardSkeleton />
  if (!salon)       return <NoSalonState />

  const todayStr    = new Date().toDateString()
  const todayAppts  = appointments.filter(a => new Date(a.startTime).toDateString() === todayStr)
  const completed   = appointments.filter(a => a.status === 'COMPLETED')
  const monthrevenue = completed.reduce((sum, a) => sum + a.price, 0)

  const hairdressers = Array.from(
    new Map(appointments.map(a => [
      a.hairdresserId,
      { id: a.hairdresserId, userId: a.hairdresserId, salonId: salon.id, fullName: a.hairdresserName, isActive: true }
    ])).values()
  )

  return (
    <div className="max-w-7xl mx-auto">
      <div className="mb-6">
        <h1 className="font-display text-2xl font-bold text-white">{salon.name}</h1>
        <p className="text-slate-400 text-sm mt-1">{salon.city} · {salon.address}</p>
      </div>

      {/* Overview */}
      {tab === 'overview' && (
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="space-y-8">
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
            <StatCard label="Danas termini"   value={todayAppts.length.toString()}    icon={CalendarDays} accent="rose"    change={{ value: 12 }} />
            <StatCard label="Mj. prihod"      value={formatPrice(monthrevenue)}        icon={DollarSign}   accent="emerald" change={{ value: 8 }} />
            <StatCard label="Aktivni frizeri" value={hairdressers.length.toString()}   icon={Users}        accent="blue" />
            <StatCard label="Avg. ocjena"     value="4.8 ★"                            icon={Star}         accent="amber" />
          </div>
          <div>
            <h2 className="font-display text-lg font-semibold text-white mb-4">Termini Danas</h2>
            {todayAppts.length === 0
              ? <div className="text-center py-10 bg-[#0F1623] rounded-2xl border border-white/5 text-slate-500 text-sm">Nema termina za danas</div>
              : <div className="space-y-3">{todayAppts.map((a, i) => (
                  <motion.div key={a.id} initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: i * 0.04 }}
                    className="flex items-center gap-4 p-4 rounded-xl bg-[#0F1623] border border-white/5"
                  >
                    <div className="w-9 h-9 rounded-lg bg-slate-700 flex items-center justify-center text-sm font-bold text-white shrink-0">{a.clientName.charAt(0)}</div>
                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-medium text-white">{a.clientName}</p>
                      <p className="text-xs text-slate-400">{a.serviceName} · {a.hairdresserName}</p>
                    </div>
                    <span className="text-sm text-slate-300 shrink-0">{formatTime(a.startTime)}</span>
                    <span className="text-sm font-semibold text-white shrink-0">{formatPrice(a.price)}</span>
                    <AppointmentStatusBadge status={a.status} />
                  </motion.div>
                ))}</div>
            }
          </div>
        </motion.div>
      )}

      {/* Calendar */}
      {tab === 'calendar' && (
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="h-[700px]">
          <AppointmentCalendar
            appointments={appointments}
            hairdressers={hairdressers}
            onAddAppointment={() => { setEditTarget(null); setAddModal(true) }}
            onEditAppointment={(a) => { setEditTarget(a); setAddModal(true) }}
          />
        </motion.div>
      )}

      {/* Staff */}
      {tab === 'staff' && (
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }}>
          <StaffManagementPanel salonId={salon.id} />
        </motion.div>
      )}

      {/* Services */}
      {tab === 'services' && (
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }}>
          <ServiceManagementPanel salonId={salon.id} />
        </motion.div>
      )}

      {/* Photos */}
      {tab === 'photos' && (
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }}>
          <SalonPhotosPanel salonId={salon.id} photos={salon.photos ?? []} />
        </motion.div>
      )}

      {/* Analytics */}
      {tab === 'analytics' && (
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="space-y-6">
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <StatCard label="Ovaj mjesec" value={formatPrice(monthrevenue)}  icon={DollarSign}   accent="emerald" change={{ value: 15 }} />
            <StatCard label="Termina"     value={completed.length.toString()} icon={CalendarDays} accent="rose"    change={{ value: 8 }} />
            <StatCard label="Avg/termin"  value={completed.length ? formatPrice(monthrevenue / completed.length) : '€0'} icon={TrendingUp} accent="blue" />
          </div>

          <div className="p-6 rounded-2xl bg-[#0F1623] border border-white/5">
            <h3 className="font-display font-semibold text-white mb-5">Prihod po Mjesecu</h3>
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
                <YAxis tick={{ fill: '#64748b', fontSize: 12 }} axisLine={false} tickLine={false} tickFormatter={v => `€${v}`} />
                <Tooltip content={<CustomTooltip />} />
                <Area type="monotone" dataKey="revenue" stroke="#e94560" strokeWidth={2} fill="url(#grad)" dot={false} activeDot={{ r: 5, fill: '#e94560' }} />
              </AreaChart>
            </ResponsiveContainer>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="p-6 rounded-2xl bg-[#0F1623] border border-white/5">
              <h3 className="font-display font-semibold text-white mb-5">Najpopularnije Usluge</h3>
              <div className="flex items-center gap-6">
                <PieChart width={140} height={140}>
                  <Pie data={MOCK_PIE} cx={65} cy={65} innerRadius={38} outerRadius={62} paddingAngle={3} dataKey="value">
                    {MOCK_PIE.map((_, i) => <Cell key={i} fill={MOCK_PIE[i].color} />)}
                  </Pie>
                </PieChart>
                <div className="space-y-2.5 flex-1">
                  {MOCK_PIE.map(s => (
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
              <h3 className="font-display font-semibold text-white mb-5">Top Frizeri</h3>
              <div className="space-y-3">
                {hairdressers.slice(0, 4).map((h, i) => (
                  <div key={h.id} className="flex items-center gap-3">
                    <span className="text-xs text-slate-600 w-4">{i + 1}</span>
                    <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-rose-500 to-rose-700 flex items-center justify-center text-white text-xs font-bold shrink-0">
                      {h.fullName.charAt(0)}
                    </div>
                    <div className="flex-1 min-w-0">
                      <p className="text-sm text-white truncate">{h.fullName}</p>
                      <div className="h-1.5 bg-white/5 rounded-full mt-1 overflow-hidden">
                        <div className="h-full bg-rose-500 rounded-full" style={{ width: `${85 - i * 15}%` }} />
                      </div>
                    </div>
                    <span className="text-xs text-slate-400 shrink-0">{85 - i * 15}%</span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </motion.div>
      )}

      {/* Settings */}
      {tab === 'settings' && (
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }}>
          <SettingsTab salon={salon} />
        </motion.div>
      )}

      <AddAppointmentModal
        open={addModalOpen}
        onClose={() => { setAddModal(false); setEditTarget(null) }}
        onSave={() => { setAddModal(false); setEditTarget(null) }}
        hairdressers={hairdressers}
        services={[]}
        editAppointment={editTarget}
      />
    </div>
  )
}
