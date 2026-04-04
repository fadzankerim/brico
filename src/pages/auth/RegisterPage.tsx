import { Link } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { motion, AnimatePresence } from 'motion/react'
import {
  Mail, Lock, Eye, EyeOff, User, Phone, Scissors,
  Crown, Zap, CheckCircle2, XCircle, CreditCard, X, Lock as LockIcon, ShieldCheck,
} from 'lucide-react'
import { useState } from 'react'
import { registerSchema, type RegisterForm } from '../../utils/validators'
import { useRegister } from '../../hooks/useAuth'
import { cn } from '../../lib/utils'

// ─── Stripe mock modal ────────────────────────────────────────────────────────

function StripeCheckoutModal({ onSuccess, onClose }: { onSuccess: () => void; onClose: () => void }) {
  const [cardNum, setCardNum]   = useState('')
  const [expiry, setExpiry]     = useState('')
  const [cvc, setCvc]           = useState('')
  const [name, setName]         = useState('')
  const [paying, setPaying]     = useState(false)
  const [error, setError]       = useState('')

  const formatCard = (v: string) =>
    v.replace(/\D/g, '').slice(0, 16).replace(/(.{4})/g, '$1 ').trim()

  const formatExpiry = (v: string) => {
    const d = v.replace(/\D/g, '').slice(0, 4)
    return d.length >= 3 ? `${d.slice(0, 2)}/${d.slice(2)}` : d
  }

  const handlePay = async () => {
    if (!name.trim()) { setError('Unesite ime vlasnika kartice'); return }
    if (cardNum.replace(/\s/g, '').length < 16) { setError('Unesite validan broj kartice'); return }
    if (expiry.length < 5) { setError('Unesite datum isteka'); return }
    if (cvc.length < 3)    { setError('Unesite CVV kod'); return }
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
        className="relative z-10 w-full max-w-md bg-[#0F1623] rounded-2xl border border-white/10 shadow-2xl shadow-black/50 overflow-hidden"
      >
        {/* Header */}
        <div className="bg-[#080C14] px-6 py-4 flex items-center justify-between border-b border-white/8">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 rounded-lg bg-[#635BFF]/20 flex items-center justify-center">
              <span className="text-[#635BFF] font-bold text-sm">S</span>
            </div>
            <div>
              <p className="text-white text-sm font-semibold">Stripe Secure Checkout</p>
              <p className="text-slate-500 text-xs">brico.ba · PRO plan</p>
            </div>
          </div>
          <button onClick={onClose} className="text-slate-500 hover:text-white transition-colors">
            <X className="w-4 h-4" />
          </button>
        </div>

        <div className="p-6 space-y-5">
          {/* Summary */}
          <div className="bg-white/4 rounded-xl p-4 flex items-center justify-between">
            <div className="flex items-center gap-3">
              <Crown className="w-5 h-5 text-amber-400" />
              <div>
                <p className="text-sm font-semibold text-white">PRO Plan</p>
                <p className="text-xs text-slate-400">Mjesečna pretplata · automatski obnavlja</p>
              </div>
            </div>
            <p className="text-white font-bold text-lg">50 KM</p>
          </div>

          {/* Card fields */}
          <div>
            <label className="block text-xs font-medium text-slate-400 mb-1.5">Ime vlasnika kartice</label>
            <input
              value={name} onChange={e => setName(e.target.value)}
              placeholder="Ime Prezime"
              className="w-full px-3.5 py-3 rounded-xl bg-white/5 border border-white/10 text-white placeholder:text-slate-600 text-sm focus:outline-none focus:border-[#635BFF]/50 focus:ring-2 focus:ring-[#635BFF]/15 transition-all"
            />
          </div>

          <div>
            <label className="block text-xs font-medium text-slate-400 mb-1.5">Broj kartice</label>
            <div className="relative">
              <CreditCard className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500" />
              <input
                value={cardNum}
                onChange={e => setCardNum(formatCard(e.target.value))}
                placeholder="1234 5678 9012 3456"
                className="w-full pl-10 pr-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white placeholder:text-slate-600 text-sm font-mono tracking-wider focus:outline-none focus:border-[#635BFF]/50 focus:ring-2 focus:ring-[#635BFF]/15 transition-all"
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-medium text-slate-400 mb-1.5">Datum isteka</label>
              <input
                value={expiry} onChange={e => setExpiry(formatExpiry(e.target.value))}
                placeholder="MM/GG"
                className="w-full px-3.5 py-3 rounded-xl bg-white/5 border border-white/10 text-white placeholder:text-slate-600 text-sm font-mono focus:outline-none focus:border-[#635BFF]/50 focus:ring-2 focus:ring-[#635BFF]/15 transition-all"
              />
            </div>
            <div>
              <label className="block text-xs font-medium text-slate-400 mb-1.5">CVV</label>
              <div className="relative">
                <input
                  value={cvc} onChange={e => setCvc(e.target.value.replace(/\D/g, '').slice(0, 4))}
                  placeholder="•••"
                  type="password"
                  className="w-full px-3.5 py-3 rounded-xl bg-white/5 border border-white/10 text-white placeholder:text-slate-600 text-sm font-mono focus:outline-none focus:border-[#635BFF]/50 focus:ring-2 focus:ring-[#635BFF]/15 transition-all"
                />
              </div>
            </div>
          </div>

          {error && (
            <p className="text-rose-400 text-xs bg-rose-500/10 rounded-lg px-3 py-2">{error}</p>
          )}

          <button
            onClick={handlePay}
            disabled={paying}
            className="w-full py-3.5 rounded-xl bg-[#635BFF] hover:bg-[#5851e5] disabled:opacity-70 text-white font-semibold text-sm transition-colors shadow-lg shadow-[#635BFF]/20 flex items-center justify-center gap-2"
          >
            {paying ? (
              <><span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />Obrađujem plaćanje...</>
            ) : (
              <><LockIcon className="w-4 h-4" />Platite 50 KM / mj.</>
            )}
          </button>

          {/* Trust badges */}
          <div className="flex items-center justify-center gap-4 pt-1">
            <div className="flex items-center gap-1 text-xs text-slate-600">
              <ShieldCheck className="w-3.5 h-3.5 text-slate-600" />
              SSL enkripcija
            </div>
            <div className="flex items-center gap-1 text-xs text-slate-600">
              <LockIcon className="w-3 h-3 text-slate-600" />
              Powered by Stripe
            </div>
            <div className="text-xs text-slate-600">Otkažite bilo kada</div>
          </div>
        </div>
      </motion.div>
    </motion.div>
  )
}

// ─── Plan Selector ─────────────────────────────────────────────────────────────

type Plan = 'BASIC' | 'PRO'

function PlanSelector({
  selected, onSelect, onProCheckout,
}: { selected: Plan; onSelect: (p: Plan) => void; onProCheckout: () => void }) {
  return (
    <motion.div
      initial={{ opacity: 0, height: 0 }}
      animate={{ opacity: 1, height: 'auto' }}
      exit={{ opacity: 0, height: 0 }}
      transition={{ duration: 0.25 }}
      className="overflow-hidden"
    >
      <div className="pt-1 pb-2">
        <label className="block text-xs font-medium text-slate-400 mb-2">Odaberi plan pretplate</label>
        <div className="grid grid-cols-2 gap-2.5">
          {/* BASIC */}
          <button
            type="button"
            onClick={() => onSelect('BASIC')}
            className={cn(
              'p-3.5 rounded-xl border text-left transition-all',
              selected === 'BASIC'
                ? 'bg-slate-500/10 border-slate-400/40 ring-1 ring-slate-400/20'
                : 'bg-white/3 border-white/8 hover:border-white/15'
            )}
          >
            <div className="flex items-center gap-1.5 mb-2">
              <Zap className="w-3.5 h-3.5 text-slate-400" />
              <span className="text-sm font-semibold text-white">BASIC</span>
              {selected === 'BASIC' && <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400 ml-auto" />}
            </div>
            <p className="text-lg font-bold text-white mb-1.5">Besplatno</p>
            <div className="space-y-1">
              {['Do 3 frizera', 'Osnovna analitika'].map(f => (
                <div key={f} className="flex items-center gap-1.5 text-xs text-slate-400">
                  <CheckCircle2 className="w-3 h-3 text-emerald-500 shrink-0" /> {f}
                </div>
              ))}
              {['Featured status', 'Napredna analitika'].map(f => (
                <div key={f} className="flex items-center gap-1.5 text-xs text-slate-600">
                  <XCircle className="w-3 h-3 text-slate-700 shrink-0" /> {f}
                </div>
              ))}
            </div>
          </button>

          {/* PRO */}
          <button
            type="button"
            onClick={() => { onSelect('PRO'); onProCheckout() }}
            className={cn(
              'p-3.5 rounded-xl border text-left transition-all relative overflow-hidden',
              selected === 'PRO'
                ? 'bg-amber-500/10 border-amber-500/50 ring-1 ring-amber-500/20'
                : 'bg-white/3 border-amber-500/20 hover:border-amber-500/40'
            )}
          >
            <div className="absolute top-2 right-2">
              <span className="text-[10px] font-bold text-amber-400 bg-amber-500/15 px-1.5 py-0.5 rounded-md uppercase tracking-wide">
                PRO
              </span>
            </div>
            <div className="flex items-center gap-1.5 mb-2">
              <Crown className="w-3.5 h-3.5 text-amber-400" />
              <span className="text-sm font-semibold text-white">PRO</span>
              {selected === 'PRO' && <CheckCircle2 className="w-3.5 h-3.5 text-amber-400 ml-auto" />}
            </div>
            <p className="text-lg font-bold text-white mb-1.5">50 KM<span className="text-xs text-slate-500 font-normal">/mj.</span></p>
            <div className="space-y-1">
              {['Neograničeni frizeri', 'Napredna analitika', 'Featured status'].map(f => (
                <div key={f} className="flex items-center gap-1.5 text-xs text-slate-400">
                  <CheckCircle2 className="w-3 h-3 text-amber-500 shrink-0" /> {f}
                </div>
              ))}
            </div>
          </button>
        </div>

        {selected === 'PRO' && (
          <motion.div
            initial={{ opacity: 0, y: -4 }}
            animate={{ opacity: 1, y: 0 }}
            className="mt-2 flex items-center gap-2 bg-amber-500/8 border border-amber-500/15 rounded-xl px-3 py-2"
          >
            <Crown className="w-3.5 h-3.5 text-amber-400 shrink-0" />
            <p className="text-xs text-amber-400/80">
              PRO plan zahtjeva plaćanje putem Stripe-a. Kliknite "Kreiraj Račun" da nastavite.
            </p>
          </motion.div>
        )}
      </div>
    </motion.div>
  )
}

// ─── Main Component ────────────────────────────────────────────────────────────

export default function RegisterPage() {
  const [showPassword, setShowPassword]   = useState(false)
  const [selectedPlan, setSelectedPlan]   = useState<Plan>('BASIC')
  const [stripeOpen, setStripeOpen]       = useState(false)
  const [proActivated, setProActivated]   = useState(false)
  const { mutate: register_, isPending }  = useRegister()

  const { register, handleSubmit, watch, setValue, formState: { errors } } = useForm<RegisterForm>({
    resolver: zodResolver(registerSchema),
    defaultValues: { role: 'CLIENT' },
  })

  const selectedRole = watch('role')

  const onSubmit = (data: RegisterForm) => {
    if (selectedRole === 'SALON_OWNER' && selectedPlan === 'PRO' && !proActivated) {
      setStripeOpen(true)
      return
    }
    register_({ ...data })
  }

  const handleProSuccess = () => {
    setStripeOpen(false)
    setProActivated(true)
  }

  return (
    <>
      {/* Stripe modal */}
      <AnimatePresence>
        {stripeOpen && (
          <StripeCheckoutModal
            onSuccess={handleProSuccess}
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
          <div className="text-center mb-8">
            <Link to="/" className="inline-flex items-center gap-2 mb-6">
              <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-rose-500 to-rose-600 flex items-center justify-center shadow-lg shadow-rose-500/30">
                <Scissors className="w-5 h-5 text-white" strokeWidth={2.5} />
              </div>
              <span className="font-display font-bold text-xl">Bri<span className="text-rose-500">co</span></span>
            </Link>
            <h1 className="font-display text-2xl font-bold text-white">Kreiranje računa</h1>
            <p className="text-slate-400 text-sm mt-1">Registrujte se besplatno u par sekundi</p>
          </div>

          <div className="bg-[#0F1623] rounded-2xl border border-white/8 p-6 shadow-2xl shadow-black/40">
            <form onSubmit={handleSubmit(onSubmit)} noValidate className="space-y-4">

              {/* Role selector */}
              <div>
                <label className="block text-xs font-medium text-slate-400 mb-2">Registrujem se kao</label>
                <div className="grid grid-cols-2 gap-2">
                  {[
                    { value: 'CLIENT',       label: '👤 Klijent',        desc: 'Rezerviši termine' },
                    { value: 'SALON_OWNER',  label: '✂ Vlasnik Salona',  desc: 'Upravljaj salonom' },
                  ].map((opt) => (
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

              {/* Plan selector — only for SALON_OWNER */}
              <AnimatePresence>
                {selectedRole === 'SALON_OWNER' && (
                  <PlanSelector
                    selected={selectedPlan}
                    onSelect={p => { setSelectedPlan(p); if (p === 'BASIC') setProActivated(false) }}
                    onProCheckout={() => setStripeOpen(true)}
                  />
                )}
              </AnimatePresence>

              {/* PRO aktiviran badge */}
              <AnimatePresence>
                {proActivated && (
                  <motion.div
                    initial={{ opacity: 0, scale: 0.95 }}
                    animate={{ opacity: 1, scale: 1 }}
                    exit={{ opacity: 0, scale: 0.95 }}
                    className="flex items-center gap-2 bg-amber-500/10 border border-amber-500/20 rounded-xl px-3 py-2.5"
                  >
                    <Crown className="w-4 h-4 text-amber-400 shrink-0" />
                    <div>
                      <p className="text-amber-400 text-xs font-semibold">PRO plan aktiviran</p>
                      <p className="text-amber-400/60 text-xs">Plaćanje uspješno • Pretplata aktivna</p>
                    </div>
                    <CheckCircle2 className="w-4 h-4 text-emerald-400 ml-auto shrink-0" />
                  </motion.div>
                )}
              </AnimatePresence>

              {/* Full name */}
              <div>
                <label className="block text-xs font-medium text-slate-400 mb-1.5">Ime i prezime</label>
                <div className="relative">
                  <User className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500" />
                  <input
                    {...register('fullName')}
                    placeholder="Vaše puno ime"
                    className="w-full pl-10 pr-4 py-3 rounded-xl bg-white/5 border border-white/8 text-white placeholder:text-slate-600 text-sm focus:outline-none focus:border-rose-500/50 focus:ring-2 focus:ring-rose-500/15 transition-all"
                  />
                </div>
                {errors.fullName && <p className="text-rose-400 text-xs mt-1">{errors.fullName.message}</p>}
              </div>

              {/* Email */}
              <div>
                <label className="block text-xs font-medium text-slate-400 mb-1.5">Email adresa</label>
                <div className="relative">
                  <Mail className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500" />
                  <input
                    {...register('email')}
                    type="email"
                    placeholder="vase@email.com"
                    autoComplete="email"
                    className="w-full pl-10 pr-4 py-3 rounded-xl bg-white/5 border border-white/8 text-white placeholder:text-slate-600 text-sm focus:outline-none focus:border-rose-500/50 focus:ring-2 focus:ring-rose-500/15 transition-all"
                  />
                </div>
                {errors.email && <p className="text-rose-400 text-xs mt-1">{errors.email.message}</p>}
              </div>

              {/* Phone */}
              <div>
                <label className="block text-xs font-medium text-slate-400 mb-1.5">
                  Telefon <span className="text-slate-600">(opciono)</span>
                </label>
                <div className="relative">
                  <Phone className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500" />
                  <input
                    {...register('phone')}
                    type="tel"
                    placeholder="+387 61 123 456"
                    className="w-full pl-10 pr-4 py-3 rounded-xl bg-white/5 border border-white/8 text-white placeholder:text-slate-600 text-sm focus:outline-none focus:border-rose-500/50 focus:ring-2 focus:ring-rose-500/15 transition-all"
                  />
                </div>
              </div>

              {/* Password */}
              <div>
                <label className="block text-xs font-medium text-slate-400 mb-1.5">Lozinka</label>
                <div className="relative">
                  <Lock className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500" />
                  <input
                    {...register('password')}
                    type={showPassword ? 'text' : 'password'}
                    placeholder="Minimalno 8 karaktera"
                    autoComplete="new-password"
                    className="w-full pl-10 pr-11 py-3 rounded-xl bg-white/5 border border-white/8 text-white placeholder:text-slate-600 text-sm focus:outline-none focus:border-rose-500/50 focus:ring-2 focus:ring-rose-500/15 transition-all"
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute right-3.5 top-1/2 -translate-y-1/2 text-slate-500 hover:text-slate-300 transition-colors"
                  >
                    {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                  </button>
                </div>
                {errors.password && <p className="text-rose-400 text-xs mt-1">{errors.password.message}</p>}
              </div>

              {/* Confirm password */}
              <div>
                <label className="block text-xs font-medium text-slate-400 mb-1.5">Potvrda lozinke</label>
                <div className="relative">
                  <Lock className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500" />
                  <input
                    {...register('confirmPassword')}
                    type="password"
                    placeholder="Ponovite lozinku"
                    autoComplete="new-password"
                    className="w-full pl-10 pr-4 py-3 rounded-xl bg-white/5 border border-white/8 text-white placeholder:text-slate-600 text-sm focus:outline-none focus:border-rose-500/50 focus:ring-2 focus:ring-rose-500/15 transition-all"
                  />
                </div>
                {errors.confirmPassword && <p className="text-rose-400 text-xs mt-1">{errors.confirmPassword.message}</p>}
              </div>

              <button
                type="submit"
                disabled={isPending}
                className={cn(
                  'w-full py-3 rounded-xl text-white font-semibold text-sm transition-all shadow-lg mt-2 flex items-center justify-center gap-2',
                  selectedRole === 'SALON_OWNER' && selectedPlan === 'PRO' && !proActivated
                    ? 'bg-amber-500 hover:bg-amber-600 disabled:opacity-50 shadow-amber-500/20'
                    : 'bg-rose-500 hover:bg-rose-600 disabled:opacity-50 shadow-rose-500/20'
                )}
              >
                {isPending ? (
                  <><span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />Kreiram račun...</>
                ) : selectedRole === 'SALON_OWNER' && selectedPlan === 'PRO' && !proActivated ? (
                  <><Crown className="w-4 h-4" />Nastavi na plaćanje (Stripe)</>
                ) : (
                  'Kreiraj Račun'
                )}
              </button>
            </form>
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
