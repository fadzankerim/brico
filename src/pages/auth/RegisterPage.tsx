import { Link } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { motion } from 'motion/react'
import { Mail, Lock, Eye, EyeOff, User, Phone, Scissors } from 'lucide-react'
import { useState } from 'react'
import { registerSchema, type RegisterForm } from '../../utils/validators'
import { useRegister } from '../../hooks/useAuth'
import { cn } from '../../lib/utils'


export default function RegisterPage() {
  const [showPassword, setShowPassword] = useState(false)
  const { mutate: register_, isPending } = useRegister()

  const { register, handleSubmit, watch, setValue, formState: { errors } } = useForm<RegisterForm>({
    resolver: zodResolver(registerSchema),
    defaultValues: { role: 'CLIENT' },
  })

  const selectedRole = watch('role')

  return (
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
          <form onSubmit={handleSubmit((d) => register_(d))} noValidate className="space-y-4">

            {/* Role selector */}
            <div>
              <label className="block text-xs font-medium text-slate-400 mb-2">Registrujem se kao</label>
              <div className="grid grid-cols-2 gap-2">
                {[
                  { value: 'CLIENT', label: '👤 Klijent', desc: 'Rezerviši termine' },
                  { value: 'SALON_OWNER', label: '✂ Vlasnik Salona', desc: 'Upravljaj salonom' },
                ].map((opt) => (
                  <button
                    key={opt.value}
                    type="button"
                    onClick={() => setValue('role', opt.value as 'CLIENT' | 'SALON_OWNER')}
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

            {/* Phone (optional) */}
            <div>
              <label className="block text-xs font-medium text-slate-400 mb-1.5">Telefon <span className="text-slate-600">(opciono)</span></label>
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
                <button type="button" onClick={() => setShowPassword(!showPassword)} className="absolute right-3.5 top-1/2 -translate-y-1/2 text-slate-500 hover:text-slate-300 transition-colors">
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
              className="w-full py-3 rounded-xl bg-rose-500 hover:bg-rose-600 disabled:opacity-50 text-white font-semibold text-sm transition-colors shadow-lg shadow-rose-500/20 mt-2 flex items-center justify-center gap-2"
            >
              {isPending ? (
                <><span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />Kreiram račun...</>
              ) : 'Kreiraj Račun'}
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
  )
}