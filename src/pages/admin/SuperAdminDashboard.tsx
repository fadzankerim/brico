import { useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { motion } from 'motion/react'
import {
  LayoutDashboard, Building2, Users, CreditCard, TrendingUp,
  CheckCircle2, XCircle, Crown, Zap, Star, BarChart3,
  ShieldCheck, ToggleLeft, ToggleRight, Search, ChevronUp, ChevronDown,
} from 'lucide-react'
import {
  AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip,
  ResponsiveContainer, PieChart, Pie, Cell, Legend,
} from 'recharts'
import StatCard from '../../components/StatCard'
import { cn } from '../../lib/utils'
import { MOCK_SALONS, MOCK_USERS } from '../../db/db'

// ─── Mock analytics data ──────────────────────────────────────────────────────

const MONTHLY_REVENUE = [
  { month: 'Oct', revenue: 100 }, { month: 'Nov', revenue: 150 },
  { month: 'Dec', revenue: 150 }, { month: 'Jan', revenue: 200 },
  { month: 'Feb', revenue: 250 }, { month: 'Mar', revenue: 350 },
  { month: 'Apr', revenue: 400 },
]

const PLAN_DISTRIBUTION = [
  { name: 'BASIC', value: 3, color: '#6b7280' },
  { name: 'PRO',   value: 2, color: '#f59e0b' },
]

const MOCK_SUBSCRIPTIONS: Record<number, { plan: 'BASIC' | 'PRO'; status: 'ACTIVE' | 'TRIAL' | 'PAST_DUE' }> = {
  1: { plan: 'PRO',   status: 'ACTIVE' },
  2: { plan: 'PRO',   status: 'ACTIVE' },
  3: { plan: 'BASIC', status: 'ACTIVE' },
  4: { plan: 'BASIC', status: 'TRIAL'  },
  5: { plan: 'BASIC', status: 'PAST_DUE' },
}

const STATUS_COLORS = {
  ACTIVE:   'bg-emerald-500/15 text-emerald-400',
  TRIAL:    'bg-blue-500/15 text-blue-400',
  PAST_DUE: 'bg-rose-500/15 text-rose-400',
}
const STATUS_LABELS = { ACTIVE: 'Aktivan', TRIAL: 'Trial', PAST_DUE: 'Neplaćen' }

const ROLE_COLORS: Record<string, string> = {
  ADMIN:        'bg-amber-500/15 text-amber-400',
  SALON_OWNER:  'bg-purple-500/15 text-purple-400',
  HAIRDRESSER:  'bg-blue-500/15 text-blue-400',
  CLIENT:       'bg-slate-500/15 text-slate-400',
}
const ROLE_LABELS: Record<string, string> = {
  ADMIN: 'Super Admin', SALON_OWNER: 'Vlasnik', HAIRDRESSER: 'Frizer', CLIENT: 'Klijent',
}

// ─── Custom Tooltip ───────────────────────────────────────────────────────────

const ChartTooltip = ({ active, payload, label }: any) => {
  if (!active || !payload?.length) return null
  return (
    <div className="bg-[#0F1623] border border-white/10 rounded-xl p-3 text-sm shadow-xl">
      <p className="text-slate-400 mb-1">{label}</p>
      <p className="font-semibold text-amber-400">{payload[0].value} KM</p>
    </div>
  )
}

// ─── Plan feature row ─────────────────────────────────────────────────────────

function FeatureRow({ label, basic, pro }: { label: string; basic: boolean | string; pro: boolean | string }) {
  const cell = (v: boolean | string) =>
    typeof v === 'string' ? (
      <span className="text-slate-300 text-sm">{v}</span>
    ) : v ? (
      <CheckCircle2 className="w-4 h-4 text-emerald-400 mx-auto" />
    ) : (
      <XCircle className="w-4 h-4 text-slate-600 mx-auto" />
    )

  return (
    <tr className="border-t border-white/5">
      <td className="py-3 px-4 text-sm text-slate-400">{label}</td>
      <td className="py-3 px-4 text-center">{cell(basic)}</td>
      <td className="py-3 px-4 text-center">{cell(pro)}</td>
    </tr>
  )
}

// ─── Salon row ────────────────────────────────────────────────────────────────

function SalonRow({ salon, sub }: { salon: typeof MOCK_SALONS[0]; sub: typeof MOCK_SUBSCRIPTIONS[1] }) {
  const [active, setActive] = useState(salon.isActive)

  return (
    <tr className="border-t border-white/5 hover:bg-white/2 transition-colors">
      <td className="py-3 px-4">
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 rounded-lg bg-white/8 flex items-center justify-center">
            <Building2 className="w-4 h-4 text-slate-400" />
          </div>
          <div>
            <p className="text-sm font-medium text-white">{salon.name}</p>
            <p className="text-xs text-slate-500">{salon.city}</p>
          </div>
        </div>
      </td>
      <td className="py-3 px-4">
        <span className={cn(
          'inline-flex items-center gap-1.5 text-xs font-semibold px-2.5 py-1 rounded-lg',
          sub.plan === 'PRO'
            ? 'bg-amber-500/15 text-amber-400'
            : 'bg-slate-500/15 text-slate-400'
        )}>
          {sub.plan === 'PRO' && <Crown className="w-3 h-3" />}
          {sub.plan}
        </span>
      </td>
      <td className="py-3 px-4">
        <span className={cn('text-xs font-medium px-2.5 py-1 rounded-lg', STATUS_COLORS[sub.status])}>
          {STATUS_LABELS[sub.status]}
        </span>
      </td>
      <td className="py-3 px-4">
        {salon.verified ? (
          <span className="inline-flex items-center gap-1 text-xs text-emerald-400">
            <ShieldCheck className="w-3.5 h-3.5" /> Verificiran
          </span>
        ) : (
          <span className="text-xs text-slate-600">—</span>
        )}
      </td>
      <td className="py-3 px-4">
        <button
          onClick={() => setActive(v => !v)}
          className="flex items-center gap-1.5 text-xs font-medium transition-colors"
          title={active ? 'Deaktiviraj salon' : 'Aktiviraj salon'}
        >
          {active
            ? <><ToggleRight className="w-5 h-5 text-emerald-400" /><span className="text-emerald-400">Aktivan</span></>
            : <><ToggleLeft className="w-5 h-5 text-slate-500"    /><span className="text-slate-500">Neaktivan</span></>}
        </button>
      </td>
    </tr>
  )
}

// ─── Main Component ───────────────────────────────────────────────────────────

export default function SuperAdminDashboard() {
  const [searchParams] = useSearchParams()
  const tab = searchParams.get('tab') ?? 'overview'

  const [userSearch, setUserSearch] = useState('')

  const owners      = MOCK_USERS.filter(u => u.role === 'SALON_OWNER')
  const hairdressers = MOCK_USERS.filter(u => u.role === 'HAIRDRESSER')
  const clients     = MOCK_USERS.filter(u => u.role === 'CLIENT')
  const proCount    = Object.values(MOCK_SUBSCRIPTIONS).filter(s => s.plan === 'PRO').length
  const monthlyRevenue = proCount * 50

  const filteredUsers = MOCK_USERS.filter(u =>
    u.fullName.toLowerCase().includes(userSearch.toLowerCase()) ||
    u.email.toLowerCase().includes(userSearch.toLowerCase())
  )

  return (
    <div className="max-w-7xl mx-auto">
      {/* Header */}
      <div className="mb-6">
        <div className="flex items-center gap-3 mb-1">
          <div className="w-8 h-8 rounded-lg bg-amber-500/15 flex items-center justify-center">
            <Crown className="w-4 h-4 text-amber-400" />
          </div>
          <h1 className="font-display text-2xl font-bold text-white">Super Admin</h1>
        </div>
        <p className="text-slate-400 text-sm ml-11">Centralni upravljački panel platforme Brico</p>
      </div>

      {/* ── OVERVIEW ────────────────────────────────────────────────────────── */}
      {tab === 'overview' && (
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="space-y-8">
          {/* Stat cards */}
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
            <StatCard
              label="Ukupno salona"
              value={MOCK_SALONS.length.toString()}
              icon={Building2}
              accent="blue"
              change={{ value: 20, label: 'novi ovaj mjesec' }}
            />
            <StatCard
              label="Registrovanih korisnika"
              value={MOCK_USERS.filter(u => u.role !== 'ADMIN').length.toString()}
              icon={Users}
              accent="emerald"
              change={{ value: 15 }}
            />
            <StatCard
              label="PRO pretplata"
              value={proCount.toString()}
              icon={Crown}
              accent="amber"
              change={{ value: 10, label: `${proCount} aktivnih` }}
            />
            <StatCard
              label="Prihod (ovaj mj.)"
              value={`${monthlyRevenue} KM`}
              icon={TrendingUp}
              accent="rose"
              change={{ value: 25, label: 'vs prošli mj.' }}
            />
          </div>

          {/* Charts row */}
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            {/* Revenue area chart */}
            <div className="lg:col-span-2 bg-[#0F1623] rounded-2xl border border-white/5 p-6">
              <div className="flex items-center justify-between mb-6">
                <div>
                  <h3 className="font-semibold text-white">Prihod od pretplata</h3>
                  <p className="text-xs text-slate-500 mt-0.5">Mjesečni prihod u KM</p>
                </div>
                <span className="text-xs bg-amber-500/10 text-amber-400 px-2.5 py-1 rounded-lg font-medium">
                  +{Math.round((MONTHLY_REVENUE[MONTHLY_REVENUE.length - 1].revenue /
                    MONTHLY_REVENUE[MONTHLY_REVENUE.length - 2].revenue - 1) * 100)}% ovaj mj.
                </span>
              </div>
              <ResponsiveContainer width="100%" height={200}>
                <AreaChart data={MONTHLY_REVENUE}>
                  <defs>
                    <linearGradient id="adminRevGrad" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%"  stopColor="#f59e0b" stopOpacity={0.3} />
                      <stop offset="95%" stopColor="#f59e0b" stopOpacity={0}   />
                    </linearGradient>
                  </defs>
                  <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.04)" />
                  <XAxis dataKey="month" tick={{ fill: '#64748b', fontSize: 12 }} axisLine={false} tickLine={false} />
                  <YAxis tick={{ fill: '#64748b', fontSize: 12 }} axisLine={false} tickLine={false} />
                  <Tooltip content={<ChartTooltip />} />
                  <Area type="monotone" dataKey="revenue" stroke="#f59e0b" strokeWidth={2}
                    fill="url(#adminRevGrad)" dot={false} />
                </AreaChart>
              </ResponsiveContainer>
            </div>

            {/* Plan distribution pie */}
            <div className="bg-[#0F1623] rounded-2xl border border-white/5 p-6">
              <h3 className="font-semibold text-white mb-1">Distribucija planova</h3>
              <p className="text-xs text-slate-500 mb-4">BASIC vs PRO saloni</p>
              <ResponsiveContainer width="100%" height={160}>
                <PieChart>
                  <Pie data={PLAN_DISTRIBUTION} cx="50%" cy="50%" innerRadius={45} outerRadius={70}
                    paddingAngle={4} dataKey="value">
                    {PLAN_DISTRIBUTION.map((entry, i) => (
                      <Cell key={i} fill={entry.color} />
                    ))}
                  </Pie>
                  <Tooltip formatter={(v: any) => [`${v} salona`]} />
                </PieChart>
              </ResponsiveContainer>
              <div className="space-y-2 mt-2">
                {PLAN_DISTRIBUTION.map(p => (
                  <div key={p.name} className="flex items-center justify-between text-sm">
                    <div className="flex items-center gap-2">
                      <div className="w-2.5 h-2.5 rounded-full" style={{ background: p.color }} />
                      <span className="text-slate-400">{p.name}</span>
                    </div>
                    <span className="font-semibold text-white">{p.value} salona</span>
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* Recent salons quick table */}
          <div className="bg-[#0F1623] rounded-2xl border border-white/5 overflow-hidden">
            <div className="px-6 py-4 border-b border-white/5">
              <h3 className="font-semibold text-white">Posljednji registrovani saloni</h3>
            </div>
            <table className="w-full">
              <thead>
                <tr className="text-xs text-slate-500 uppercase tracking-wider">
                  <th className="text-left py-3 px-4">Salon</th>
                  <th className="text-left py-3 px-4">Plan</th>
                  <th className="text-left py-3 px-4">Status</th>
                  <th className="text-left py-3 px-4">Verificiran</th>
                </tr>
              </thead>
              <tbody>
                {MOCK_SALONS.slice(0, 3).map(s => (
                  <tr key={s.id} className="border-t border-white/5 hover:bg-white/2 transition-colors">
                    <td className="py-3 px-4">
                      <div className="flex items-center gap-3">
                        <div className="w-8 h-8 rounded-lg bg-white/8 flex items-center justify-center">
                          <Building2 className="w-4 h-4 text-slate-400" />
                        </div>
                        <div>
                          <p className="text-sm font-medium text-white">{s.name}</p>
                          <p className="text-xs text-slate-500">{s.city}</p>
                        </div>
                      </div>
                    </td>
                    <td className="py-3 px-4">
                      <span className={cn('text-xs font-semibold px-2.5 py-1 rounded-lg inline-flex items-center gap-1',
                        MOCK_SUBSCRIPTIONS[s.id]?.plan === 'PRO'
                          ? 'bg-amber-500/15 text-amber-400'
                          : 'bg-slate-500/15 text-slate-400'
                      )}>
                        {MOCK_SUBSCRIPTIONS[s.id]?.plan === 'PRO' && <Crown className="w-3 h-3" />}
                        {MOCK_SUBSCRIPTIONS[s.id]?.plan ?? 'BASIC'}
                      </span>
                    </td>
                    <td className="py-3 px-4">
                      <span className={cn('text-xs px-2.5 py-1 rounded-lg font-medium',
                        STATUS_COLORS[MOCK_SUBSCRIPTIONS[s.id]?.status ?? 'ACTIVE']
                      )}>
                        {STATUS_LABELS[MOCK_SUBSCRIPTIONS[s.id]?.status ?? 'ACTIVE']}
                      </span>
                    </td>
                    <td className="py-3 px-4">
                      {s.verified
                        ? <span className="inline-flex items-center gap-1 text-xs text-emerald-400"><ShieldCheck className="w-3.5 h-3.5" />Da</span>
                        : <span className="text-xs text-slate-600">Ne</span>}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </motion.div>
      )}

      {/* ── PLANS ───────────────────────────────────────────────────────────── */}
      {tab === 'plans' && (
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="space-y-8">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* BASIC card */}
            <div className="bg-[#0F1623] rounded-2xl border border-white/8 p-6 relative overflow-hidden">
              <div className="absolute top-0 right-0 w-40 h-40 rounded-full bg-slate-500/5 blur-3xl pointer-events-none" />
              <div className="flex items-start justify-between mb-4">
                <div>
                  <div className="flex items-center gap-2 mb-1">
                    <Zap className="w-5 h-5 text-slate-400" />
                    <h3 className="text-lg font-bold text-white">BASIC</h3>
                  </div>
                  <p className="text-slate-500 text-sm">Besplatni starter plan</p>
                </div>
                <span className="bg-slate-500/15 text-slate-400 text-xs font-semibold px-3 py-1.5 rounded-xl">
                  Aktivan
                </span>
              </div>
              <div className="mb-6">
                <span className="text-4xl font-display font-bold text-white">0</span>
                <span className="text-slate-400 ml-1">KM / mj.</span>
              </div>
              <div className="space-y-2.5 mb-6">
                {[
                  'Do 3 frizera',
                  'Neograničeni termini',
                  'Osnovna analitika',
                  'Email notifikacije',
                ].map(f => (
                  <div key={f} className="flex items-center gap-2.5 text-sm text-slate-300">
                    <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
                    {f}
                  </div>
                ))}
                {[
                  'Napredna analitika',
                  'Featured status',
                  'Prioritetna podrška',
                ].map(f => (
                  <div key={f} className="flex items-center gap-2.5 text-sm text-slate-600">
                    <XCircle className="w-4 h-4 text-slate-700 shrink-0" />
                    {f}
                  </div>
                ))}
              </div>
              <div className="pt-4 border-t border-white/5 flex items-center justify-between text-sm text-slate-500">
                <span>{Object.values(MOCK_SUBSCRIPTIONS).filter(s => s.plan === 'BASIC').length} aktivnih pretplata</span>
                <span className="text-slate-600 text-xs">Stripe ID: —</span>
              </div>
            </div>

            {/* PRO card */}
            <div className="bg-[#0F1623] rounded-2xl border border-amber-500/20 p-6 relative overflow-hidden">
              <div className="absolute top-0 right-0 w-40 h-40 rounded-full bg-amber-500/8 blur-3xl pointer-events-none" />
              <div className="absolute top-4 right-4">
                <span className="bg-amber-500/20 text-amber-400 text-xs font-bold px-2.5 py-1 rounded-lg uppercase tracking-wide">
                  Preporučeno
                </span>
              </div>
              <div className="flex items-start justify-between mb-4">
                <div>
                  <div className="flex items-center gap-2 mb-1">
                    <Crown className="w-5 h-5 text-amber-400" />
                    <h3 className="text-lg font-bold text-white">PRO</h3>
                  </div>
                  <p className="text-slate-500 text-sm">Profesionalni plan</p>
                </div>
              </div>
              <div className="mb-6">
                <span className="text-4xl font-display font-bold text-white">50</span>
                <span className="text-slate-400 ml-1">KM / mj.</span>
              </div>
              <div className="space-y-2.5 mb-6">
                {[
                  'Neograničeni frizeri',
                  'Neograničeni termini',
                  'Napredna analitika',
                  'Featured status u pretrazi',
                  'Prioritetna podrška',
                  'Stripe automatska naplata',
                ].map(f => (
                  <div key={f} className="flex items-center gap-2.5 text-sm text-slate-300">
                    <CheckCircle2 className="w-4 h-4 text-amber-400 shrink-0" />
                    {f}
                  </div>
                ))}
              </div>
              <div className="pt-4 border-t border-amber-500/10 flex items-center justify-between text-sm">
                <span className="text-slate-500">{Object.values(MOCK_SUBSCRIPTIONS).filter(s => s.plan === 'PRO').length} aktivnih pretplata</span>
                <span className="text-amber-600 text-xs font-mono">price_brico_pro_monthly</span>
              </div>
            </div>
          </div>

          {/* Feature comparison table */}
          <div className="bg-[#0F1623] rounded-2xl border border-white/5 overflow-hidden">
            <div className="px-6 py-4 border-b border-white/5">
              <h3 className="font-semibold text-white">Usporedba planova</h3>
            </div>
            <table className="w-full">
              <thead>
                <tr className="text-xs text-slate-500 uppercase tracking-wider">
                  <th className="text-left py-3 px-4 w-1/2">Funkcionalnost</th>
                  <th className="py-3 px-4 text-center w-1/4">
                    <span className="flex items-center justify-center gap-1.5">
                      <Zap className="w-3.5 h-3.5" /> BASIC
                    </span>
                  </th>
                  <th className="py-3 px-4 text-center w-1/4">
                    <span className="flex items-center justify-center gap-1.5 text-amber-400">
                      <Crown className="w-3.5 h-3.5" /> PRO
                    </span>
                  </th>
                </tr>
              </thead>
              <tbody>
                <FeatureRow label="Broj frizera"             basic="Do 3"       pro="Neograničeno" />
                <FeatureRow label="Rezervacije termina"      basic={true}       pro={true} />
                <FeatureRow label="Osnovna analitika"        basic={true}       pro={true} />
                <FeatureRow label="Napredna analitika"       basic={false}      pro={true} />
                <FeatureRow label="Featured status"          basic={false}      pro={true} />
                <FeatureRow label="Email notifikacije"       basic={true}       pro={true} />
                <FeatureRow label="Prioritetna podrška"      basic={false}      pro={true} />
                <FeatureRow label="Stripe automatska naplata" basic={false}     pro={true} />
              </tbody>
            </table>
          </div>
        </motion.div>
      )}

      {/* ── SALONS ──────────────────────────────────────────────────────────── */}
      {tab === 'salons' && (
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="space-y-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-slate-400 text-sm">{MOCK_SALONS.length} ukupno registrovanih salona</p>
            </div>
            <div className="flex items-center gap-2 bg-white/5 border border-white/8 rounded-xl px-3 py-2">
              <Search className="w-4 h-4 text-slate-500" />
              <input placeholder="Pretraži salone..." className="bg-transparent text-sm text-white placeholder:text-slate-600 outline-none w-40" />
            </div>
          </div>

          <div className="bg-[#0F1623] rounded-2xl border border-white/5 overflow-hidden">
            <table className="w-full">
              <thead>
                <tr className="text-xs text-slate-500 uppercase tracking-wider border-b border-white/5">
                  <th className="text-left py-3 px-4">Salon</th>
                  <th className="text-left py-3 px-4">Plan</th>
                  <th className="text-left py-3 px-4">Pretplata</th>
                  <th className="text-left py-3 px-4">Verificiran</th>
                  <th className="text-left py-3 px-4">Status</th>
                </tr>
              </thead>
              <tbody>
                {MOCK_SALONS.map(s => (
                  <SalonRow
                    key={s.id}
                    salon={s}
                    sub={MOCK_SUBSCRIPTIONS[s.id] ?? { plan: 'BASIC', status: 'TRIAL' }}
                  />
                ))}
              </tbody>
            </table>
          </div>
        </motion.div>
      )}

      {/* ── USERS ───────────────────────────────────────────────────────────── */}
      {tab === 'users' && (
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="space-y-6">
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
            <StatCard label="Vlasnici salona" value={owners.length.toString()}       icon={Building2} accent="blue"    />
            <StatCard label="Frizeri"          value={hairdressers.length.toString()} icon={Users}     accent="rose"    />
            <StatCard label="Klijenti"          value={clients.length.toString()}     icon={Users}     accent="emerald" />
            <StatCard label="Ukupno korisnika"  value={(MOCK_USERS.length - 1).toString()} icon={Users} accent="amber" />
          </div>

          <div className="bg-[#0F1623] rounded-2xl border border-white/5 overflow-hidden">
            <div className="px-4 py-3 border-b border-white/5 flex items-center gap-2">
              <Search className="w-4 h-4 text-slate-500" />
              <input
                value={userSearch}
                onChange={e => setUserSearch(e.target.value)}
                placeholder="Pretraži korisnike..."
                className="bg-transparent text-sm text-white placeholder:text-slate-600 outline-none flex-1"
              />
            </div>
            <table className="w-full">
              <thead>
                <tr className="text-xs text-slate-500 uppercase tracking-wider border-b border-white/5">
                  <th className="text-left py-3 px-4">Korisnik</th>
                  <th className="text-left py-3 px-4">Email</th>
                  <th className="text-left py-3 px-4">Uloga</th>
                  <th className="text-left py-3 px-4">Verifikovan</th>
                  <th className="text-left py-3 px-4">Registrovan</th>
                </tr>
              </thead>
              <tbody>
                {filteredUsers.filter(u => u.role !== 'ADMIN').map(u => (
                  <tr key={u.id} className="border-t border-white/5 hover:bg-white/2 transition-colors">
                    <td className="py-3 px-4">
                      <div className="flex items-center gap-3">
                        <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-rose-500/20 to-rose-700/20 flex items-center justify-center text-xs font-bold text-rose-400">
                          {u.fullName.split(' ').map(n => n[0]).join('').slice(0, 2)}
                        </div>
                        <span className="text-sm font-medium text-white">{u.fullName}</span>
                      </div>
                    </td>
                    <td className="py-3 px-4 text-sm text-slate-400">{u.email}</td>
                    <td className="py-3 px-4">
                      <span className={cn('text-xs font-medium px-2.5 py-1 rounded-lg', ROLE_COLORS[u.role])}>
                        {ROLE_LABELS[u.role]}
                      </span>
                    </td>
                    <td className="py-3 px-4">
                      {u.emailVerified
                        ? <CheckCircle2 className="w-4 h-4 text-emerald-400" />
                        : <XCircle className="w-4 h-4 text-slate-600" />}
                    </td>
                    <td className="py-3 px-4 text-xs text-slate-500">
                      {new Date(u.createdAt).toLocaleDateString('bs-BA')}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </motion.div>
      )}

      {/* ── ANALYTICS ───────────────────────────────────────────────────────── */}
      {tab === 'analytics' && (
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="space-y-6">
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
            <StatCard label="Termini ovaj mj." value="184"  icon={BarChart3} accent="rose"    change={{ value: 12 }} />
            <StatCard label="Kompletiranih"    value="156"  icon={CheckCircle2} accent="emerald" change={{ value: 8 }} />
            <StatCard label="Otkazanih"        value="18"   icon={XCircle}   accent="amber"   change={{ value: -3 }} />
            <StatCard label="Prihod platf."    value="400 KM" icon={CreditCard} accent="blue" change={{ value: 14 }} />
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <div className="bg-[#0F1623] rounded-2xl border border-white/5 p-6">
              <h3 className="font-semibold text-white mb-1">Prihod platforme</h3>
              <p className="text-xs text-slate-500 mb-4">Ukupan prihod od PRO pretplata (KM)</p>
              <ResponsiveContainer width="100%" height={200}>
                <AreaChart data={MONTHLY_REVENUE}>
                  <defs>
                    <linearGradient id="revGrad2" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%"  stopColor="#f59e0b" stopOpacity={0.3} />
                      <stop offset="95%" stopColor="#f59e0b" stopOpacity={0}   />
                    </linearGradient>
                  </defs>
                  <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.04)" />
                  <XAxis dataKey="month" tick={{ fill: '#64748b', fontSize: 12 }} axisLine={false} tickLine={false} />
                  <YAxis tick={{ fill: '#64748b', fontSize: 12 }} axisLine={false} tickLine={false} />
                  <Tooltip content={<ChartTooltip />} />
                  <Area type="monotone" dataKey="revenue" stroke="#f59e0b" strokeWidth={2} fill="url(#revGrad2)" dot={false} />
                </AreaChart>
              </ResponsiveContainer>
            </div>

            <div className="bg-[#0F1623] rounded-2xl border border-white/5 p-6">
              <h3 className="font-semibold text-white mb-1">Distribucija planova</h3>
              <p className="text-xs text-slate-500 mb-4">Aktivni saloni po planu</p>
              <ResponsiveContainer width="100%" height={200}>
                <PieChart>
                  <Pie data={PLAN_DISTRIBUTION} cx="50%" cy="50%" outerRadius={80}
                    paddingAngle={4} dataKey="value" label={({ name, value }) => `${name}: ${value}`}
                    labelLine={false}>
                    {PLAN_DISTRIBUTION.map((entry, i) => (
                      <Cell key={i} fill={entry.color} />
                    ))}
                  </Pie>
                  <Tooltip formatter={(v: any) => [`${v} salona`]} />
                  <Legend />
                </PieChart>
              </ResponsiveContainer>
            </div>
          </div>
        </motion.div>
      )}
    </div>
  )
}
