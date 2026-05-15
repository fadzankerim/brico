import { useState, useEffect } from "react"
import { useAuthStore } from "../../store/authStore"
import type { Appointment } from "../../types/booking.types"
import type { Salon } from "../../types/salon.typs"
import type { SubscriptionResponse } from "../../services/salon.service"
import { useSalonAppointments, useUpdateAppointmentStatus } from "../../hooks/useBooking"
import { useMySalon, salonKeys } from "../../hooks/useSalons"
import { CalendarDays, DollarSign, Star, TrendingUp, Users, Building2, Edit2, Check, X, Ban, Clock, Lock, Crown, AlertTriangle } from "lucide-react"
import StatCard from "../../components/StatCard"
import { formatPrice, formatTime } from "../../utils/dateUtils"
import { motion } from "motion/react"
import AppointmentStatusBadge from "../../components/AppointmentStatusBadge"
import AppointmentCalendar from "../../components/AppointmentCalendar"
import StaffManagementPanel from "./StaffManagementPanel"
import ServiceManagementPanel from "./ServiceManagementPanel"
import SalonPhotosPanel from "../../components/SalonPhotosPanel"
import { useSearchParams } from "react-router-dom"
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell, BarChart, Bar } from 'recharts'
import AddAppointmentModal from "../../components/AddAppointmentModal"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { salonService } from "../../services/salon.service"
import { bookingService } from "../../services/booking.service"
import { toast } from "sonner"
import {
  AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent,
  AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle, AlertDialogTrigger,
} from "../../components/ui/alert-dialog"

const DAY_NAMES = ['Ponedjeljak', 'Utorak', 'Srijeda', 'Četvrtak', 'Petak', 'Subota', 'Nedjelja']

const PIE_COLORS = ['#e94560', '#f97316', '#3b82f6', '#10b981', '#6b7280', '#a855f7']

function buildRevenueChart(appointments: any[]) {
  const MONTHS = ['Jan', 'Feb', 'Mar', 'Apr', 'Maj', 'Jun', 'Jul', 'Aug', 'Sep', 'Okt', 'Nov', 'Dec']
  const map: Record<string, number> = {}
  appointments.filter(a => a.status === 'COMPLETED').forEach(a => {
    const m = MONTHS[new Date(a.startTime).getMonth()]
    map[m] = (map[m] ?? 0) + (a.totalPrice ?? a.price ?? 0)
  })
  return MONTHS.slice(0, new Date().getMonth() + 1).map(m => ({ month: m, revenue: Math.round(map[m] ?? 0) }))
}

function buildPieChart(appointments: any[]) {
  const map: Record<string, number> = {}
  appointments.filter(a => a.status === 'COMPLETED').forEach(a => {
    const name = a.serviceName ?? 'Ostalo'
    map[name] = (map[name] ?? 0) + 1
  })
  const total = Object.values(map).reduce((s, v) => s + v, 0) || 1
  return Object.entries(map).slice(0, 5).map(([name, count], i) => ({
    name, value: Math.round((count / total) * 100), color: PIE_COLORS[i % PIE_COLORS.length]
  }))
}

function buildHairdresserRevenueChart(appointments: any[]) {
  const map: Record<string, number> = {}
  appointments.filter(a => a.status === 'COMPLETED').forEach(a => {
    const name = a.hairdresserName ?? 'Nepoznat'
    map[name] = (map[name] ?? 0) + (a.totalPrice ?? a.price ?? 0)
  })
  return Object.entries(map)
    .sort((a, b) => b[1] - a[1])
    .slice(0, 6)
    .map(([name, revenue]) => ({ name: name.split(' ')[0], revenue: Math.round(revenue) }))
}

function buildPeakHoursChart(appointments: any[]) {
  const map: Record<number, number> = {}
  appointments.forEach(a => {
    const h = new Date(a.startTime).getHours()
    map[h] = (map[h] ?? 0) + 1
  })
  return Array.from({ length: 24 }, (_, h) => ({ hour: `${h}h`, count: map[h] ?? 0 }))
    .filter(e => e.count > 0)
}

function buildDayOfWeekChart(appointments: any[]) {
  const DAYS = ['Ned', 'Pon', 'Uto', 'Sri', 'Čet', 'Pet', 'Sub']
  const map: Record<number, number> = {}
  appointments.forEach(a => {
    const d = new Date(a.startTime).getDay()
    map[d] = (map[d] ?? 0) + 1
  })
  return [1, 2, 3, 4, 5, 6, 0].map(d => ({ day: DAYS[d], count: map[d] ?? 0 }))
}

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

type DayState = { isDayOff: boolean; startTime: string; endTime: string }

function WorkingHoursEditor({ salonId }: { salonId: number }) {
  const queryClient = useQueryClient()
  const { data: wh = [] } = useQuery({
    queryKey: ['working-hours', salonId],
    queryFn:  () => salonService.getWorkingHours(salonId),
    staleTime: 1000 * 60 * 5,
  })

  const [days, setDays] = useState<DayState[]>(() =>
    Array.from({ length: 7 }, (_, i) => ({ isDayOff: i === 6, startTime: '08:00', endTime: '20:00' }))
  )

  // Sync server data into local state; backend returns "HH:MM:SS" — truncate to "HH:MM"
  useEffect(() => {
    if (!Array.isArray(wh) || wh.length === 0) return
    setDays(prev => {
      const next = [...prev]
      wh.forEach((d: any) => {
        const idx = d.dayOfWeek === 0 ? 6 : d.dayOfWeek - 1
        next[idx] = {
          isDayOff:  !!d.isDayOff,
          startTime: (d.startTime ?? '08:00').substring(0, 5),
          endTime:   (d.endTime   ?? '20:00').substring(0, 5),
        }
      })
      return next
    })
  }, [wh])

  const update = useMutation({
    mutationFn: ({ day, start, end, off }: { day: number; start: string; end: string; off: boolean }) =>
      salonService.updateWorkingHours(salonId, day, { startTime: off ? undefined : start, endTime: off ? undefined : end, isDayOff: off }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['working-hours', salonId] })
      toast.success('Radno vrijeme ažurirano')
    },
    onError: () => toast.error('Greška pri ažuriranju radnog vremena'),
  })

  return (
    <div className="p-5 rounded-2xl bg-[#0F1623] border border-white/5 space-y-3">
      <h4 className="text-sm font-semibold text-white flex items-center gap-2"><Clock className="w-4 h-4 text-rose-400" />Radno vrijeme</h4>
      {DAY_NAMES.map((name, i) => {
        const backendDay = i === 6 ? 0 : i + 1
        const { isDayOff, startTime, endTime } = days[i]
        return (
          <div key={i} className="flex items-center gap-3 text-sm">
            <span className="w-28 text-slate-400 shrink-0">{name}</span>
            <input
              type="checkbox"
              checked={isDayOff}
              onChange={e => {
                const off = e.target.checked
                setDays(prev => prev.map((d, j) => j === i ? { ...d, isDayOff: off } : d))
                update.mutate({ day: backendDay, start: startTime, end: endTime, off })
              }}
              className="accent-rose-500"
            />
            <span className="text-xs text-slate-500">Neradni dan</span>
            {!isDayOff && (
              <>
                <input
                  type="time"
                  value={startTime}
                  onChange={e => {
                    const v = e.target.value
                    setDays(prev => prev.map((d, j) => j === i ? { ...d, startTime: v } : d))
                    if (v) update.mutate({ day: backendDay, start: v, end: endTime, off: false })
                  }}
                  className="bg-white/5 border border-white/10 rounded-lg px-2 py-1 text-white text-xs focus:outline-none focus:border-rose-500/50"
                />
                <span className="text-slate-600">–</span>
                <input
                  type="time"
                  value={endTime}
                  onChange={e => {
                    const v = e.target.value
                    setDays(prev => prev.map((d, j) => j === i ? { ...d, endTime: v } : d))
                    if (v) update.mutate({ day: backendDay, start: startTime, end: v, off: false })
                  }}
                  className="bg-white/5 border border-white/10 rounded-lg px-2 py-1 text-white text-xs focus:outline-none focus:border-rose-500/50"
                />
              </>
            )}
          </div>
        )
      })}
    </div>
  )
}

function SettingsTab({ salon, subscription }: { salon: Salon; subscription: SubscriptionResponse | undefined }) {
  const queryClient = useQueryClient()
  const [editing, setEditing] = useState(false)
  const [form, setForm] = useState({
    name:        salon.name,
    description: salon.description ?? '',
    city:        salon.city,
    address:     salon.address,
    phone:       salon.phone ?? '',
    website:     salon.website ?? '',
    idBroj:      salon.idBroj ?? '',
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

  const cancelSubMutation = useMutation({
    mutationFn: () => salonService.cancelSubscription(salon.id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['subscription', salon.id] })
      queryClient.invalidateQueries({ queryKey: ['salon-limits', salon.id] })
      toast.success('Pretplata otkazana. Prebačeni ste na BASIC plan.')
    },
    onError: () => toast.error('Greška pri otkazivanju pretplate'),
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
              onClick={() => { setEditing(false); setForm({ name: salon.name, description: salon.description ?? '', city: salon.city, address: salon.address, phone: salon.phone ?? '', website: salon.website ?? '', idBroj: salon.idBroj ?? '' }) }}
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
        <div>
          <label className="text-xs text-slate-500 uppercase tracking-wide mb-1.5 block">Identifikacioni / PDV broj</label>
          <input
            disabled={!editing}
            value={form.idBroj}
            onChange={e => setForm(f => ({ ...f, idBroj: e.target.value.replace(/\D/g, '').slice(0, 13) }))}
            inputMode="numeric"
            placeholder="4200000000000"
            className="w-full px-3 py-2.5 rounded-xl bg-white/5 border border-white/10 text-white text-sm font-mono placeholder:text-slate-600 focus:outline-none focus:border-rose-500/50 transition-all disabled:opacity-50"
          />
          <p className="text-xs text-slate-600 mt-1">13 cifara · jedinstven identifikator salona</p>
        </div>
      </div>

      <WorkingHoursEditor salonId={salon.id} />

      {subscription && (
        <div className="p-5 rounded-2xl bg-[#0F1623] border border-white/5 space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="font-semibold text-white text-sm">Pretplata</h3>
              <p className="text-xs text-slate-500 mt-0.5">Upravljajte vašim planom</p>
            </div>
            <span className={`px-2.5 py-1 rounded-full text-xs font-semibold ${
              subscription.plan.planType === 'PRO'
                ? 'bg-rose-500/15 text-rose-400'
                : 'bg-slate-700/50 text-slate-400'
            }`}>
              {subscription.plan.planType}
            </span>
          </div>

          <div className="grid grid-cols-3 gap-3 text-center">
            <div className="p-3 rounded-xl bg-white/3 border border-white/5">
              <p className="text-xs text-slate-500 mb-1">Status</p>
              <p className={`text-sm font-medium ${
                subscription.status === 'ACTIVE' ? 'text-emerald-400' :
                subscription.status === 'CANCELLED' ? 'text-red-400' : 'text-amber-400'
              }`}>
                {subscription.status === 'ACTIVE' ? 'Aktivan' :
                 subscription.status === 'CANCELLED' ? 'Otkazan' :
                 subscription.status === 'TRIAL' ? 'Probni' : subscription.status}
              </p>
            </div>
            <div className="p-3 rounded-xl bg-white/3 border border-white/5">
              <p className="text-xs text-slate-500 mb-1">Cijena</p>
              <p className="text-sm font-medium text-white">
                {subscription.plan.monthlyPrice > 0 ? `${subscription.plan.monthlyPrice} KM/mj` : 'Besplatno'}
              </p>
            </div>
            <div className="p-3 rounded-xl bg-white/3 border border-white/5">
              <p className="text-xs text-slate-500 mb-1">Aktivna od</p>
              <p className="text-sm font-medium text-white">
                {subscription.startDate ? new Date(subscription.startDate).toLocaleDateString('bs-BA') : '—'}
              </p>
            </div>
          </div>

          {subscription.plan.planType === 'PRO' && subscription.status === 'ACTIVE' && (
            <AlertDialog>
              <AlertDialogTrigger asChild>
                <button className="w-full flex items-center justify-center gap-2 px-4 py-2.5 rounded-xl border border-red-500/30 text-red-400 hover:bg-red-500/10 text-sm transition-colors">
                  <AlertTriangle className="w-4 h-4" />
                  Otkaži pretplatu
                </button>
              </AlertDialogTrigger>
              <AlertDialogContent>
                <AlertDialogHeader>
                  <AlertDialogTitle>Otkazivanje PRO pretplate</AlertDialogTitle>
                  <AlertDialogDescription>
                    Nakon otkazivanja, vaš salon će biti prebačen na <strong>BASIC plan</strong> koji uključuje
                    ograničen broj frizera (maksimalno 3) i nema pristup naprednoj analitici.
                    Ova akcija se ne može poništiti.
                  </AlertDialogDescription>
                </AlertDialogHeader>
                <AlertDialogFooter>
                  <AlertDialogCancel>Odustani</AlertDialogCancel>
                  <AlertDialogAction
                    variant="destructive"
                    onClick={() => cancelSubMutation.mutate()}
                    className="bg-red-600 hover:bg-red-700"
                  >
                    {cancelSubMutation.isPending ? 'Otkazivanje...' : 'Potvrdi otkazivanje'}
                  </AlertDialogAction>
                </AlertDialogFooter>
              </AlertDialogContent>
            </AlertDialog>
          )}

          {subscription.status === 'CANCELLED' && (
            <p className="text-xs text-slate-500 text-center">
              Pretplata je otkazana{subscription.endDate ? ` dana ${new Date(subscription.endDate).toLocaleDateString('bs-BA')}` : ''}.
              Salon je na BASIC planu.
            </p>
          )}
        </div>
      )}
    </div>
  )
}

export default function OwnerDashboard() {
  const { user } = useAuthStore()
  const [searchParams] = useSearchParams()
  const [addModalOpen, setAddModal] = useState(false)
  const [editTarget, setEditTarget] = useState<Appointment | null>(null)
  const [cancelId, setCancelId] = useState<number | null>(null)
  const queryClient = useQueryClient()

  const tab = searchParams.get('tab') ?? 'overview'

  const { data: salon, isLoading: salonLoading } = useMySalon()
  const { data: appointments = [] } = useSalonAppointments(salon?.id ?? 0)
  const { data: subscription } = useQuery({
    queryKey: ['subscription', salon?.id],
    queryFn: () => salonService.getSubscription(salon!.id),
    enabled: !!salon?.id,
    retry: false,
  })

  const cancelMutation = useMutation({
    mutationFn: (id: number) => bookingService.cancel(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['appointments'] })
      toast.success('Termin otkazan')
      setCancelId(null)
    },
    onError: () => toast.error('Greška pri otkazivanju'),
  })

  if (salonLoading) return <DashboardSkeleton />
  if (!salon)       return <NoSalonState />

  const todayStr    = new Date().toDateString()
  const todayAppts  = appointments.filter(a => new Date(a.startTime).toDateString() === todayStr)
  const completed   = appointments.filter(a => a.status === 'COMPLETED')
  const monthrevenue = completed.reduce((sum, a) => sum + (a.totalPrice ?? a.price ?? 0), 0)
  const revenueChart         = buildRevenueChart(appointments)
  const pieChart             = buildPieChart(appointments)
  const hairdresserRevChart  = buildHairdresserRevenueChart(appointments)
  const peakHoursChart       = buildPeakHoursChart(appointments)
  const dayOfWeekChart       = buildDayOfWeekChart(appointments)
  const isPro = subscription?.plan?.planType === 'PRO' && subscription?.status === 'ACTIVE'
  const completionRate = appointments.filter(a => a.status !== 'PENDING').length > 0
    ? Math.round((completed.length / appointments.filter(a => a.status !== 'PENDING').length) * 100)
    : 0

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
            <StatCard label="Avg. ocjena"     value={`${(salon.avgRating ?? 0).toFixed(1)} ★`} icon={Star} accent="amber" />
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
                    <span className="text-sm font-semibold text-white shrink-0">{formatPrice(a.totalPrice ?? a.price)}</span>
                    <AppointmentStatusBadge status={a.status} />
                    {a.status !== 'CANCELLED' && a.status !== 'COMPLETED' && (
                      cancelId === a.id ? (
                        <div className="flex gap-1">
                          <button onClick={() => cancelMutation.mutate(a.id)} className="px-2 py-1 rounded-lg bg-rose-500/15 text-rose-400 text-xs hover:bg-rose-500 hover:text-white transition-colors">Potvrdi</button>
                          <button onClick={() => setCancelId(null)} className="px-2 py-1 rounded-lg bg-white/5 text-slate-400 text-xs">✕</button>
                        </div>
                      ) : (
                        <button onClick={() => setCancelId(a.id)} title="Otkaži termin" className="w-7 h-7 rounded-lg flex items-center justify-center text-slate-500 hover:text-rose-400 hover:bg-rose-500/10 transition-colors">
                          <Ban className="w-3.5 h-3.5" />
                        </button>
                      )
                    )}
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
          {/* Basic stats — svima dostupno */}
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <StatCard label="Ovaj mjesec" value={formatPrice(monthrevenue)}  icon={DollarSign}   accent="emerald" change={{ value: 15 }} />
            <StatCard label="Termina"     value={completed.length.toString()} icon={CalendarDays} accent="rose"    change={{ value: 8 }} />
            <StatCard label="Avg/termin"  value={completed.length ? formatPrice(monthrevenue / completed.length) : '€0'} icon={TrendingUp} accent="blue" />
          </div>

          <div className="p-6 rounded-2xl bg-[#0F1623] border border-white/5">
            <h3 className="font-display font-semibold text-white mb-5">Prihod po Mjesecu</h3>
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
                  <Pie data={pieChart.length > 0 ? pieChart : [{ name: 'Nema podataka', value: 1, color: '#334155' }]} cx={65} cy={65} innerRadius={38} outerRadius={62} paddingAngle={3} dataKey="value">
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

          {/* PRO Analytics sekcija */}
          {isPro ? (
            <div className="space-y-6">
              <div className="flex items-center gap-2 pt-2">
                <Crown className="w-4 h-4 text-rose-400" />
                <h3 className="font-display font-semibold text-white text-sm">PRO Analitika</h3>
                <span className="px-2 py-0.5 rounded-full text-xs bg-rose-500/15 text-rose-400 font-medium">PRO</span>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                <StatCard label="Stopa završetka" value={`${completionRate}%`} icon={TrendingUp} accent="emerald" />
                <StatCard label="Ukupni prihod"   value={formatPrice(appointments.filter(a => a.status === 'COMPLETED').reduce((s, a) => s + (a.totalPrice ?? a.price ?? 0), 0))} icon={DollarSign} accent="blue" />
                <StatCard label="Svi termini"      value={appointments.length.toString()} icon={CalendarDays} accent="rose" />
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="p-6 rounded-2xl bg-[#0F1623] border border-white/5">
                  <h3 className="font-display font-semibold text-white mb-5">Prihod po Frizeru</h3>
                  {hairdresserRevChart.length > 0 ? (
                    <ResponsiveContainer width="100%" height={200}>
                      <BarChart data={hairdresserRevChart} barSize={28}>
                        <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.04)" vertical={false} />
                        <XAxis dataKey="name" tick={{ fill: '#64748b', fontSize: 12 }} axisLine={false} tickLine={false} />
                        <YAxis tick={{ fill: '#64748b', fontSize: 12 }} axisLine={false} tickLine={false} tickFormatter={v => `€${v}`} />
                        <Tooltip
                          contentStyle={{ background: '#0F1623', border: '1px solid rgba(255,255,255,0.1)', borderRadius: 12, fontSize: 13 }}
                          labelStyle={{ color: '#94a3b8' }}
                          formatter={(v: unknown) => [`€${v}`, 'Prihod']}
                        />
                        <Bar dataKey="revenue" fill="#e94560" radius={[6, 6, 0, 0]} />
                      </BarChart>
                    </ResponsiveContainer>
                  ) : (
                    <p className="text-sm text-slate-500 text-center py-8">Nema podataka o prihodima</p>
                  )}
                </div>

                <div className="p-6 rounded-2xl bg-[#0F1623] border border-white/5">
                  <h3 className="font-display font-semibold text-white mb-5">Termini po Danu</h3>
                  <ResponsiveContainer width="100%" height={200}>
                    <BarChart data={dayOfWeekChart} barSize={28}>
                      <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.04)" vertical={false} />
                      <XAxis dataKey="day" tick={{ fill: '#64748b', fontSize: 12 }} axisLine={false} tickLine={false} />
                      <YAxis tick={{ fill: '#64748b', fontSize: 12 }} axisLine={false} tickLine={false} allowDecimals={false} />
                      <Tooltip
                        contentStyle={{ background: '#0F1623', border: '1px solid rgba(255,255,255,0.1)', borderRadius: 12, fontSize: 13 }}
                        labelStyle={{ color: '#94a3b8' }}
                        formatter={(v: unknown) => [String(v ?? 0), 'Termina']}
                      />
                      <Bar dataKey="count" fill="#3b82f6" radius={[6, 6, 0, 0]} />
                    </BarChart>
                  </ResponsiveContainer>
                </div>
              </div>

              <div className="p-6 rounded-2xl bg-[#0F1623] border border-white/5">
                <h3 className="font-display font-semibold text-white mb-5">Vršni Sati Rezervacija</h3>
                {peakHoursChart.length > 0 ? (
                  <ResponsiveContainer width="100%" height={180}>
                    <BarChart data={peakHoursChart} barSize={20}>
                      <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.04)" vertical={false} />
                      <XAxis dataKey="hour" tick={{ fill: '#64748b', fontSize: 11 }} axisLine={false} tickLine={false} />
                      <YAxis tick={{ fill: '#64748b', fontSize: 12 }} axisLine={false} tickLine={false} allowDecimals={false} />
                      <Tooltip
                        contentStyle={{ background: '#0F1623', border: '1px solid rgba(255,255,255,0.1)', borderRadius: 12, fontSize: 13 }}
                        labelStyle={{ color: '#94a3b8' }}
                        formatter={(v: unknown) => [String(v ?? 0), 'Termina']}
                      />
                      <Bar dataKey="count" fill="#10b981" radius={[4, 4, 0, 0]} />
                    </BarChart>
                  </ResponsiveContainer>
                ) : (
                  <p className="text-sm text-slate-500 text-center py-8">Nema podataka o rezervacijama</p>
                )}
              </div>
            </div>
          ) : (
            <div className="relative overflow-hidden rounded-2xl border border-white/5 bg-[#0F1623] p-8 text-center">
              <div className="absolute inset-0 bg-gradient-to-b from-transparent to-[#0F1623]/80" />
              <div className="relative z-10 flex flex-col items-center gap-4">
                <div className="w-14 h-14 rounded-2xl bg-rose-500/10 flex items-center justify-center">
                  <Lock className="w-7 h-7 text-rose-400" />
                </div>
                <div>
                  <h3 className="font-display font-semibold text-white mb-1">Napredna analitika</h3>
                  <p className="text-sm text-slate-500 max-w-xs mx-auto">
                    Prihod po frizeru, vršni sati rezervacija i distribucija po danima dostupni su isključivo na <strong className="text-slate-300">PRO planu</strong>.
                  </p>
                </div>
                <a
                  href="/register?plan=PRO"
                  className="inline-flex items-center gap-2 px-5 py-2.5 rounded-xl bg-rose-500 hover:bg-rose-600 text-white text-sm font-medium transition-colors"
                >
                  <Crown className="w-4 h-4" />
                  Nadogradite na PRO
                </a>
              </div>
            </div>
          )}
        </motion.div>
      )}

      {/* Settings */}
      {tab === 'settings' && (
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }}>
          <SettingsTab salon={salon} subscription={subscription} />
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
