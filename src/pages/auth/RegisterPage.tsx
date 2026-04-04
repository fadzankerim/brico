import { Link } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { motion, AnimatePresence } from 'motion/react'
import {
  Mail, Lock, Eye, EyeOff, User, Phone, Scissors,
  Crown, Zap, CheckCircle2, XCircle, CreditCard,
  X, ShieldCheck, Building2, MapPin, Globe, FileText, ChevronRight, ChevronLeft,
} from 'lucide-react'
import { useState } from 'react'
import { registerSchema, type RegisterForm } from '../../utils/validators'
import { useRegister } from '../../hooks/useAuth'
import { cn } from '../../lib/utils'

// ─── Types ────────────────────────────────────────────────────────────────────

type Plan = 'BASIC' | 'PRO'

interface SalonDraft {
  name: string
  city: string
  address: string
  phone: string
  description: string
  website: string
}

// ─── Step Indicator ───────────────────────────────────────────────────────────

function StepIndicator({ step }: { step: 1 | 2 | 3 }) {
  const steps = [
    { n: 1, label: 'Lični podaci' },
    { n: 2, label: 'Salon' },
    { n: 3, label: 'Plan' },
  ]
  return (
    <div className="flex items-center justify-center gap-0 mb-6">
      {steps.map((s, i) => (
        <div key={s.n} className="flex items-center">
          <div className="flex flex-col items-center gap-1">
            <div className={cn(
              'w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold transition-all',
              step > s.n  ? 'bg-emerald-500 text-white' :
              step === s.n ? 'bg-rose-500 text-white ring-4 ring-rose-500/20' :
              'bg-white/8 text-slate-500'
            )}>
              {step > s.n ? <CheckCircle2 className="w-4 h-4" /> : s.n}
            </div>
            <span className={cn(
              'text-[10px] font-medium',
              step === s.n ? 'text-rose-400' : step > s.n ? 'text-emerald-400' : 'text-slate-600'
            )}>{s.label}</span>
          </div>
          {i < steps.length - 1 && (
            <div className={cn(
              'h-px w-10 mb-4 mx-1 transition-all',
              step > s.n ? 'bg-emerald-500/50' : 'bg-white/8'
            )} />
          )}
        </div>
      ))}
    </div>
  )
}

// ─── Salon Form Step ──────────────────────────────────────────────────────────

const CITIES = ['Sarajevo', 'Mostar', 'Banja Luka', 'Tuzla', 'Zenica', 'Bijeljina', 'Brčko', 'Travnik', 'Cazin', 'Visoko']

function SalonStep({
  data, onChange, errors, onNext, onBack,
}: {
  data: SalonDraft
  onChange: (field: keyof SalonDraft, value: string) => void
  errors: Partial<Record<keyof SalonDraft, string>>
  onNext: () => void
  onBack: () => void
}) {
  const field = (
    id: keyof SalonDraft, label: string, icon: React.ElementType,
    props: React.InputHTMLAttributes<HTMLInputElement> = {}
  ) => {
    const Icon = icon
    return (
      <div>
        <label className="block text-xs font-medium text-slate-400 mb-1.5">{label}</label>
        <div className="relative">
          <Icon className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500" />
          <input
            value={data[id]}
            onChange={e => onChange(id, e.target.value)}
            className={cn(
              'w-full pl-10 pr-4 py-3 rounded-xl bg-white/5 border text-white placeholder:text-slate-600 text-sm focus:outline-none focus:ring-2 transition-all',
              errors[id]
                ? 'border-rose-500/50 focus:border-rose-500/50 focus:ring-rose-500/15'
                : 'border-white/8 focus:border-rose-500/50 focus:ring-rose-500/15'
            )}
            {...props}
          />
        </div>
        {errors[id] && <p className="text-rose-400 text-xs mt-1">{errors[id]}</p>}
      </div>
    )
  }

  return (
    <motion.div
      key="salon-step"
      initial={{ opacity: 0, x: 20 }}
      animate={{ opacity: 1, x: 0 }}
      exit={{ opacity: 0, x: -20 }}
      transition={{ duration: 0.2 }}
      className="space-y-4"
    >
      <div className="flex items-center gap-2 mb-2">
        <div className="w-8 h-8 rounded-lg bg-blue-500/15 flex items-center justify-center">
          <Building2 className="w-4 h-4 text-blue-400" />
        </div>
        <div>
          <p className="text-sm font-semibold text-white">Podaci o salonu</p>
          <p className="text-xs text-slate-500">Bit će prikazani klijentima</p>
        </div>
      </div>

      {field('name', 'Naziv salona *', Building2, { placeholder: 'npr. Elite Cut Salon' })}

      {/* City dropdown */}
      <div>
        <label className="block text-xs font-medium text-slate-400 mb-1.5">Grad *</label>
        <div className="relative">
          <MapPin className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500 pointer-events-none" />
          <select
            value={data.city}
            onChange={e => onChange('city', e.target.value)}
            className={cn(
              'w-full pl-10 pr-4 py-3 rounded-xl bg-white/5 border text-sm transition-all focus:outline-none focus:ring-2 appearance-none',
              data.city ? 'text-white' : 'text-slate-600',
              errors.city
                ? 'border-rose-500/50 focus:ring-rose-500/15'
                : 'border-white/8 focus:border-rose-500/50 focus:ring-rose-500/15'
            )}
          >
            <option value="" disabled className="bg-[#0F1623]">Odaberi grad</option>
            {CITIES.map(c => (
              <option key={c} value={c} className="bg-[#0F1623] text-white">{c}</option>
            ))}
          </select>
        </div>
        {errors.city && <p className="text-rose-400 text-xs mt-1">{errors.city}</p>}
      </div>

      {field('address', 'Adresa *', MapPin, { placeholder: 'npr. Ferhadija 12' })}
      {field('phone', 'Telefon salona', Phone, { placeholder: '+387 33 123 456', type: 'tel' })}

      {/* Description textarea */}
      <div>
        <label className="block text-xs font-medium text-slate-400 mb-1.5">
          Opis <span className="text-slate-600">(opciono)</span>
        </label>
        <div className="relative">
          <FileText className="absolute left-3.5 top-3.5 w-4 h-4 text-slate-500" />
          <textarea
            value={data.description}
            onChange={e => onChange('description', e.target.value)}
            rows={3}
            placeholder="Kratki opis vašeg salona..."
            className="w-full pl-10 pr-4 py-3 rounded-xl bg-white/5 border border-white/8 text-white placeholder:text-slate-600 text-sm focus:outline-none focus:border-rose-500/50 focus:ring-2 focus:ring-rose-500/15 transition-all resize-none"
          />
        </div>
      </div>

      {field('website', 'Website', Globe, { placeholder: 'https://vasisalon.ba', type: 'url' })}

      <div className="flex gap-3 pt-1">
        <button
          type="button"
          onClick={onBack}
          className="flex items-center gap-1.5 px-4 py-3 rounded-xl border border-white/10 text-slate-400 hover:text-white hover:border-white/20 text-sm font-medium transition-all"
        >
          <ChevronLeft className="w-4 h-4" /> Nazad
        </button>
        <button
          type="button"
          onClick={onNext}
          className="flex-1 py-3 rounded-xl bg-rose-500 hover:bg-rose-600 text-white font-semibold text-sm transition-colors flex items-center justify-center gap-1.5"
        >
          Dalje <ChevronRight className="w-4 h-4" />
        </button>
      </div>
    </motion.div>
  )
}

// ─── Plan Step ────────────────────────────────────────────────────────────────

function PlanStep({
  selected, onSelect, onBack, onSubmit, isPending, proActivated, onProCheckout,
}: {
  selected: Plan
  onSelect: (p: Plan) => void
  onBack: () => void
  onSubmit: () => void
  isPending: boolean
  proActivated: boolean
  onProCheckout: () => void
}) {
  return (
    <motion.div
      key="plan-step"
      initial={{ opacity: 0, x: 20 }}
      animate={{ opacity: 1, x: 0 }}
      exit={{ opacity: 0, x: -20 }}
      transition={{ duration: 0.2 }}
      className="space-y-4"
    >
      <div className="flex items-center gap-2 mb-2">
        <div className="w-8 h-8 rounded-lg bg-amber-500/15 flex items-center justify-center">
          <Crown className="w-4 h-4 text-amber-400" />
        </div>
        <div>
          <p className="text-sm font-semibold text-white">Odaberi plan pretplate</p>
          <p className="text-xs text-slate-500">Možete promijeniti u bilo koje doba</p>
        </div>
      </div>

      <div className="grid grid-cols-2 gap-2.5">
        {/* BASIC */}
        <button
          type="button"
          onClick={() => onSelect('BASIC')}
          className={cn(
            'p-4 rounded-xl border text-left transition-all',
            selected === 'BASIC'
              ? 'bg-slate-500/10 border-slate-400/40 ring-1 ring-slate-400/20'
              : 'bg-white/3 border-white/8 hover:border-white/15'
          )}
        >
          <div className="flex items-center gap-1.5 mb-2">
            <Zap className="w-4 h-4 text-slate-400" />
            <span className="text-sm font-semibold text-white">BASIC</span>
            {selected === 'BASIC' && <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400 ml-auto" />}
          </div>
          <p className="text-xl font-bold text-white mb-2">Besplatno</p>
          <div className="space-y-1.5">
            {['Do 3 frizera', 'Rezervacije', 'Osnovna analitika'].map(f => (
              <div key={f} className="flex items-center gap-1.5 text-xs text-slate-400">
                <CheckCircle2 className="w-3 h-3 text-emerald-500 shrink-0" /> {f}
              </div>
            ))}
            {['Featured', 'Napredna analitika'].map(f => (
              <div key={f} className="flex items-center gap-1.5 text-xs text-slate-600">
                <XCircle className="w-3 h-3 text-slate-700 shrink-0" /> {f}
              </div>
            ))}
          </div>
        </button>

        {/* PRO */}
        <button
          type="button"
          onClick={() => { onSelect('PRO'); if (!proActivated) onProCheckout() }}
          className={cn(
            'p-4 rounded-xl border text-left transition-all relative overflow-hidden',
            selected === 'PRO'
              ? 'bg-amber-500/10 border-amber-500/50 ring-1 ring-amber-500/20'
              : 'bg-white/3 border-amber-500/15 hover:border-amber-500/35'
          )}
        >
          <div className="absolute top-2 right-2 text-[10px] font-bold text-amber-400 bg-amber-500/15 px-1.5 py-0.5 rounded-md uppercase tracking-wide">
            PRO
          </div>
          <div className="flex items-center gap-1.5 mb-2">
            <Crown className="w-4 h-4 text-amber-400" />
            <span className="text-sm font-semibold text-white">PRO</span>
            {selected === 'PRO' && <CheckCircle2 className="w-3.5 h-3.5 text-amber-400 ml-auto" />}
          </div>
          <p className="text-xl font-bold text-white mb-2">50 KM<span className="text-xs text-slate-500 font-normal">/mj.</span></p>
          <div className="space-y-1.5">
            {['Neograničeni frizeri', 'Napredna analitika', 'Featured status'].map(f => (
              <div key={f} className="flex items-center gap-1.5 text-xs text-slate-400">
                <CheckCircle2 className="w-3 h-3 text-amber-500 shrink-0" /> {f}
              </div>
            ))}
          </div>
        </button>
      </div>

      {/* PRO activated badge */}
      <AnimatePresence>
        {proActivated && (
          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            className="flex items-center gap-2 bg-amber-500/10 border border-amber-500/20 rounded-xl px-3 py-2.5"
          >
            <Crown className="w-4 h-4 text-amber-400 shrink-0" />
            <div>
              <p className="text-amber-400 text-xs font-semibold">PRO plan aktiviran</p>
              <p className="text-amber-400/60 text-xs">Plaćanje uspješno procesovano</p>
            </div>
            <CheckCircle2 className="w-4 h-4 text-emerald-400 ml-auto shrink-0" />
          </motion.div>
        )}
      </AnimatePresence>

      <div className="flex gap-3 pt-1">
        <button
          type="button"
          onClick={onBack}
          className="flex items-center gap-1.5 px-4 py-3 rounded-xl border border-white/10 text-slate-400 hover:text-white hover:border-white/20 text-sm font-medium transition-all"
        >
          <ChevronLeft className="w-4 h-4" /> Nazad
        </button>
        <button
          type="button"
          disabled={isPending || (selected === 'PRO' && !proActivated)}
          onClick={onSubmit}
          className={cn(
            'flex-1 py-3 rounded-xl text-white font-semibold text-sm transition-all flex items-center justify-center gap-2 shadow-lg disabled:opacity-50',
            selected === 'PRO' && !proActivated
              ? 'bg-amber-500 shadow-amber-500/20 cursor-not-allowed'
              : 'bg-rose-500 hover:bg-rose-600 shadow-rose-500/20'
          )}
        >
          {isPending ? (
            <><span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />Kreiram račun...</>
          ) : selected === 'PRO' && !proActivated ? (
            <><Crown className="w-4 h-4" />Platite PRO plan</>
          ) : (
            'Kreiraj Račun'
          )}
        </button>
      </div>
    </motion.div>
  )
}

// ─── Stripe Mock Modal ────────────────────────────────────────────────────────

function StripeModal({ onSuccess, onClose }: { onSuccess: () => void; onClose: () => void }) {
  const [cardNum, setCardNum] = useState('')
  const [expiry, setExpiry]   = useState('')
  const [cvc, setCvc]         = useState('')
  const [name, setName]       = useState('')
  const [paying, setPaying]   = useState(false)
  const [error, setError]     = useState('')

  const fmtCard   = (v: string) => v.replace(/\D/g, '').slice(0, 16).replace(/(.{4})/g, '$1 ').trim()
  const fmtExpiry = (v: string) => { const d = v.replace(/\D/g, '').slice(0, 4); return d.length >= 3 ? `${d.slice(0,2)}/${d.slice(2)}` : d }

  const handlePay = async () => {
    if (!name.trim()) return setError('Unesite ime vlasnika kartice')
    if (cardNum.replace(/\s/g,'').length < 16) return setError('Unesite validan broj kartice')
    if (expiry.length < 5) return setError('Unesite datum isteka')
    if (cvc.length < 3) return setError('Unesite CVV kod')
    setError('')
    setPaying(true)
    await new Promise(r => setTimeout(r, 1800))
    setPaying(false)
    onSuccess()
  }

  return (
    <motion.div
      initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
      className="fixed inset-0 z-50 flex items-center justify-center px-4"
    >
      <div className="absolute inset-0 bg-black/70 backdrop-blur-sm" onClick={onClose} />
      <motion.div
        initial={{ opacity: 0, scale: 0.95, y: 20 }}
        animate={{ opacity: 1, scale: 1, y: 0 }}
        exit={{ opacity: 0, scale: 0.95, y: 20 }}
        transition={{ type: 'spring', damping: 25, stiffness: 300 }}
        className="relative z-10 w-full max-w-md bg-[#0F1623] rounded-2xl border border-white/10 shadow-2xl overflow-hidden"
      >
        <div className="bg-[#080C14] px-6 py-4 flex items-center justify-between border-b border-white/8">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 rounded-lg bg-[#635BFF]/20 flex items-center justify-center">
              <span className="text-[#635BFF] font-bold text-sm">S</span>
            </div>
            <div>
              <p className="text-white text-sm font-semibold">Stripe Secure Checkout</p>
              <p className="text-slate-500 text-xs">brico.ba · PRO plan · 50 KM/mj.</p>
            </div>
          </div>
          <button onClick={onClose} className="text-slate-500 hover:text-white transition-colors">
            <X className="w-4 h-4" />
          </button>
        </div>

        <div className="p-6 space-y-4">
          <div className="bg-white/4 rounded-xl p-4 flex items-center justify-between">
            <div className="flex items-center gap-3">
              <Crown className="w-5 h-5 text-amber-400" />
              <div>
                <p className="text-sm font-semibold text-white">PRO Plan</p>
                <p className="text-xs text-slate-400">Automatski obnavlja svaki mjesec</p>
              </div>
            </div>
            <p className="text-white font-bold text-lg">50 KM</p>
          </div>

          <div>
            <label className="block text-xs font-medium text-slate-400 mb-1.5">Ime vlasnika kartice</label>
            <input value={name} onChange={e => setName(e.target.value)} placeholder="Ime Prezime"
              className="w-full px-3.5 py-3 rounded-xl bg-white/5 border border-white/10 text-white placeholder:text-slate-600 text-sm focus:outline-none focus:border-[#635BFF]/50 focus:ring-2 focus:ring-[#635BFF]/15 transition-all" />
          </div>

          <div>
            <label className="block text-xs font-medium text-slate-400 mb-1.5">Broj kartice</label>
            <div className="relative">
              <CreditCard className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500" />
              <input value={cardNum} onChange={e => setCardNum(fmtCard(e.target.value))} placeholder="1234 5678 9012 3456"
                className="w-full pl-10 pr-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white placeholder:text-slate-600 text-sm font-mono tracking-wider focus:outline-none focus:border-[#635BFF]/50 focus:ring-2 focus:ring-[#635BFF]/15 transition-all" />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-medium text-slate-400 mb-1.5">Datum isteka</label>
              <input value={expiry} onChange={e => setExpiry(fmtExpiry(e.target.value))} placeholder="MM/GG"
                className="w-full px-3.5 py-3 rounded-xl bg-white/5 border border-white/10 text-white placeholder:text-slate-600 text-sm font-mono focus:outline-none focus:border-[#635BFF]/50 focus:ring-2 focus:ring-[#635BFF]/15 transition-all" />
            </div>
            <div>
              <label className="block text-xs font-medium text-slate-400 mb-1.5">CVV</label>
              <input value={cvc} onChange={e => setCvc(e.target.value.replace(/\D/g,'').slice(0,4))} placeholder="•••" type="password"
                className="w-full px-3.5 py-3 rounded-xl bg-white/5 border border-white/10 text-white placeholder:text-slate-600 text-sm font-mono focus:outline-none focus:border-[#635BFF]/50 focus:ring-2 focus:ring-[#635BFF]/15 transition-all" />
            </div>
          </div>

          {error && <p className="text-rose-400 text-xs bg-rose-500/10 rounded-lg px-3 py-2">{error}</p>}

          <button onClick={handlePay} disabled={paying}
            className="w-full py-3.5 rounded-xl bg-[#635BFF] hover:bg-[#5851e5] disabled:opacity-70 text-white font-semibold text-sm transition-colors flex items-center justify-center gap-2 shadow-lg shadow-[#635BFF]/20">
            {paying
              ? <><span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />Obrađujem...</>
              : <><Lock className="w-4 h-4" />Platite 50 KM / mj.</>}
          </button>

          <div className="flex items-center justify-center gap-4">
            <span className="flex items-center gap-1 text-xs text-slate-600"><ShieldCheck className="w-3.5 h-3.5" />SSL</span>
            <span className="flex items-center gap-1 text-xs text-slate-600"><Lock className="w-3 h-3" />Stripe</span>
            <span className="text-xs text-slate-600">Otkažite bilo kada</span>
          </div>
        </div>
      </motion.div>
    </motion.div>
  )
}

// ─── Main Component ────────────────────────────────────────────────────────────

export default function RegisterPage() {
  const [showPassword, setShowPassword] = useState(false)
  const [step, setStep]                 = useState<1 | 2 | 3>(1)
  const [selectedPlan, setSelectedPlan] = useState<Plan>('BASIC')
  const [stripeOpen, setStripeOpen]     = useState(false)
  const [proActivated, setProActivated] = useState(false)
  const [salonErrors, setSalonErrors]   = useState<Partial<Record<keyof SalonDraft, string>>>({})
  const [salonData, setSalonData]       = useState<SalonDraft>({
    name: '', city: '', address: '', phone: '', description: '', website: '',
  })

  const { mutate: register_, isPending } = useRegister()

  const { register, handleSubmit, watch, setValue, trigger, formState: { errors } } = useForm<RegisterForm>({
    resolver: zodResolver(registerSchema),
    defaultValues: { role: 'CLIENT' },
  })

  const selectedRole = watch('role')
  const isSalonOwner = selectedRole === 'SALON_OWNER'

  // ── Validate personal step & advance ────────────────────────────────────────
  const goToSalonStep = async () => {
    const ok = await trigger(['fullName', 'email', 'password', 'confirmPassword'])
    if (ok) setStep(2)
  }

  // ── Validate salon step & advance ────────────────────────────────────────────
  const goToPlanStep = () => {
    const errs: typeof salonErrors = {}
    if (!salonData.name.trim())    errs.name    = 'Naziv salona je obavezan'
    if (!salonData.city)           errs.city    = 'Grad je obavezan'
    if (!salonData.address.trim()) errs.address = 'Adresa je obavezna'
    if (Object.keys(errs).length) { setSalonErrors(errs); return }
    setSalonErrors({})
    setStep(3)
  }

  // ── Final submit ─────────────────────────────────────────────────────────────
  const onSubmit = (formData: RegisterForm) => {
    if (isSalonOwner && step < 3) return // shouldn't happen but guard
    register_({
      ...formData,
      ...(isSalonOwner && {
        salonData: {
          name:        salonData.name,
          city:        salonData.city,
          address:     salonData.address,
          phone:       salonData.phone || undefined,
          description: salonData.description || undefined,
          website:     salonData.website || undefined,
        },
      }),
    })
  }

  return (
    <>
      <AnimatePresence>
        {stripeOpen && (
          <StripeModal
            onSuccess={() => { setStripeOpen(false); setProActivated(true) }}
            onClose={() => { setStripeOpen(false); setSelectedPlan('BASIC') }}
          />
        )}
      </AnimatePresence>

      <div className="min-h-screen bg-[#080C14] flex items-center justify-center px-4 py-12 relative overflow-hidden">
        <div className="absolute top-1/3 -left-32 w-80 h-80 rounded-full bg-rose-500/6 blur-3xl pointer-events-none" />
        <div className="absolute bottom-1/4 -right-32 w-80 h-80 rounded-full bg-rose-700/5 blur-3xl pointer-events-none" />

        <motion.div
          initial={{ opacity: 0, y: 24 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4 }}
          className="w-full max-w-md"
        >
          {/* Logo */}
          <div className="text-center mb-8">
            <Link to="/" className="inline-flex items-center gap-2 mb-6">
              <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-rose-500 to-rose-600 flex items-center justify-center shadow-lg shadow-rose-500/30">
                <Scissors className="w-5 h-5 text-white" strokeWidth={2.5} />
              </div>
              <span className="font-display font-bold text-xl">Bri<span className="text-rose-500">co</span></span>
            </Link>
            <h1 className="font-display text-2xl font-bold text-white">Kreiranje računa</h1>
            <p className="text-slate-400 text-sm mt-1">
              {step === 1 ? 'Registrujte se besplatno u par sekundi' :
               step === 2 ? 'Unesite podatke vašeg salona' :
               'Odaberite plan pretplate'}
            </p>
          </div>

          {/* Step indicator (only for SALON_OWNER after choosing role) */}
          {isSalonOwner && <StepIndicator step={step} />}

          <div className="bg-[#0F1623] rounded-2xl border border-white/8 p-6 shadow-2xl shadow-black/40">
            <AnimatePresence mode="wait">

              {/* ── STEP 1: Personal data ─────────────────────────────────── */}
              {step === 1 && (
                <motion.div
                  key="personal-step"
                  initial={{ opacity: 0, x: 0 }}
                  animate={{ opacity: 1, x: 0 }}
                  exit={{ opacity: 0, x: -20 }}
                  transition={{ duration: 0.2 }}
                  className="space-y-4"
                >
                  <form onSubmit={handleSubmit(onSubmit)} noValidate>

                    {/* Role selector */}
                    <div className="mb-4">
                      <label className="block text-xs font-medium text-slate-400 mb-2">Registrujem se kao</label>
                      <div className="grid grid-cols-2 gap-2">
                        {[
                          { value: 'CLIENT',       label: '👤 Klijent',       desc: 'Rezerviši termine' },
                          { value: 'SALON_OWNER',  label: '✂ Vlasnik Salona', desc: 'Upravljaj salonom' },
                        ].map(opt => (
                          <button
                            key={opt.value}
                            type="button"
                            onClick={() => {
                              setValue('role', opt.value as 'CLIENT' | 'SALON_OWNER')
                              if (opt.value === 'CLIENT') { setSelectedPlan('BASIC'); setProActivated(false) }
                            }}
                            className={cn(
                              'p-3 rounded-xl border text-left transition-all',
                              selectedRole === opt.value
                                ? 'bg-rose-500/10 border-rose-500/40 text-white'
                                : 'bg-white/3 border-white/8 text-slate-400 hover:border-white/15'
                            )}
                          >
                            <div className="text-sm font-medium">{opt.label}</div>
                            <div className="text-xs opacity-60 mt-0.5">{opt.desc}</div>
                          </button>
                        ))}
                      </div>
                    </div>

                    {/* Full name */}
                    <div className="mb-4">
                      <label className="block text-xs font-medium text-slate-400 mb-1.5">Ime i prezime</label>
                      <div className="relative">
                        <User className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500" />
                        <input {...register('fullName')} placeholder="Vaše puno ime"
                          className="w-full pl-10 pr-4 py-3 rounded-xl bg-white/5 border border-white/8 text-white placeholder:text-slate-600 text-sm focus:outline-none focus:border-rose-500/50 focus:ring-2 focus:ring-rose-500/15 transition-all" />
                      </div>
                      {errors.fullName && <p className="text-rose-400 text-xs mt-1">{errors.fullName.message}</p>}
                    </div>

                    {/* Email */}
                    <div className="mb-4">
                      <label className="block text-xs font-medium text-slate-400 mb-1.5">Email adresa</label>
                      <div className="relative">
                        <Mail className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500" />
                        <input {...register('email')} type="email" placeholder="vase@email.com" autoComplete="email"
                          className="w-full pl-10 pr-4 py-3 rounded-xl bg-white/5 border border-white/8 text-white placeholder:text-slate-600 text-sm focus:outline-none focus:border-rose-500/50 focus:ring-2 focus:ring-rose-500/15 transition-all" />
                      </div>
                      {errors.email && <p className="text-rose-400 text-xs mt-1">{errors.email.message}</p>}
                    </div>

                    {/* Phone */}
                    <div className="mb-4">
                      <label className="block text-xs font-medium text-slate-400 mb-1.5">Telefon <span className="text-slate-600">(opciono)</span></label>
                      <div className="relative">
                        <Phone className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500" />
                        <input {...register('phone')} type="tel" placeholder="+387 61 123 456"
                          className="w-full pl-10 pr-4 py-3 rounded-xl bg-white/5 border border-white/8 text-white placeholder:text-slate-600 text-sm focus:outline-none focus:border-rose-500/50 focus:ring-2 focus:ring-rose-500/15 transition-all" />
                      </div>
                    </div>

                    {/* Password */}
                    <div className="mb-4">
                      <label className="block text-xs font-medium text-slate-400 mb-1.5">Lozinka</label>
                      <div className="relative">
                        <Lock className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500" />
                        <input {...register('password')} type={showPassword ? 'text' : 'password'} placeholder="Minimalno 8 karaktera" autoComplete="new-password"
                          className="w-full pl-10 pr-11 py-3 rounded-xl bg-white/5 border border-white/8 text-white placeholder:text-slate-600 text-sm focus:outline-none focus:border-rose-500/50 focus:ring-2 focus:ring-rose-500/15 transition-all" />
                        <button type="button" onClick={() => setShowPassword(!showPassword)} className="absolute right-3.5 top-1/2 -translate-y-1/2 text-slate-500 hover:text-slate-300 transition-colors">
                          {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                        </button>
                      </div>
                      {errors.password && <p className="text-rose-400 text-xs mt-1">{errors.password.message}</p>}
                    </div>

                    {/* Confirm password */}
                    <div className="mb-4">
                      <label className="block text-xs font-medium text-slate-400 mb-1.5">Potvrda lozinke</label>
                      <div className="relative">
                        <Lock className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500" />
                        <input {...register('confirmPassword')} type="password" placeholder="Ponovite lozinku" autoComplete="new-password"
                          className="w-full pl-10 pr-4 py-3 rounded-xl bg-white/5 border border-white/8 text-white placeholder:text-slate-600 text-sm focus:outline-none focus:border-rose-500/50 focus:ring-2 focus:ring-rose-500/15 transition-all" />
                      </div>
                      {errors.confirmPassword && <p className="text-rose-400 text-xs mt-1">{errors.confirmPassword.message}</p>}
                    </div>

                    {/* Button — if CLIENT submit directly, if SALON_OWNER go to step 2 */}
                    {isSalonOwner ? (
                      <button
                        type="button"
                        onClick={goToSalonStep}
                        className="w-full py-3 rounded-xl bg-rose-500 hover:bg-rose-600 text-white font-semibold text-sm transition-colors flex items-center justify-center gap-1.5 shadow-lg shadow-rose-500/20"
                      >
                        Dalje — Podaci o salonu <ChevronRight className="w-4 h-4" />
                      </button>
                    ) : (
                      <button
                        type="submit"
                        disabled={isPending}
                        className="w-full py-3 rounded-xl bg-rose-500 hover:bg-rose-600 disabled:opacity-50 text-white font-semibold text-sm transition-colors shadow-lg shadow-rose-500/20 flex items-center justify-center gap-2 mt-2"
                      >
                        {isPending
                          ? <><span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />Kreiram račun...</>
                          : 'Kreiraj Račun'}
                      </button>
                    )}
                  </form>
                </motion.div>
              )}

              {/* ── STEP 2: Salon data ────────────────────────────────────── */}
              {step === 2 && (
                <SalonStep
                  data={salonData}
                  onChange={(f, v) => setSalonData(prev => ({ ...prev, [f]: v }))}
                  errors={salonErrors}
                  onNext={goToPlanStep}
                  onBack={() => setStep(1)}
                />
              )}

              {/* ── STEP 3: Plan selection + final submit ─────────────────── */}
              {step === 3 && (
                <PlanStep
                  selected={selectedPlan}
                  onSelect={p => { setSelectedPlan(p); if (p === 'BASIC') setProActivated(false) }}
                  onBack={() => setStep(2)}
                  onSubmit={handleSubmit(onSubmit)}
                  isPending={isPending}
                  proActivated={proActivated}
                  onProCheckout={() => setStripeOpen(true)}
                />
              )}

            </AnimatePresence>
          </div>

          <p className="text-center text-sm text-slate-500 mt-5">
            Već imate račun?{' '}
            <Link to="/login" className="text-rose-400 hover:text-rose-300 font-medium transition-colors">
              Prijavite se
            </Link>
          </p>
        </motion.div>
      </div>
    </>
  )
}
