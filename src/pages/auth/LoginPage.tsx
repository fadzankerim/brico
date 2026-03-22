import React, { useState } from "react"
import { useLogin } from "../../hooks/useAuth";
import { loginSchema, type LoginForm } from "../../utils/validators";
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { motion } from "motion/react";
import { Link } from "react-router-dom";
import { Scissors, Mail, Lock, Eye, EyeOff } from "lucide-react";

export default function LoginPage() {

    const [showPassword, setShowPassword] = useState(false);

    const { mutate: login, isPending } = useLogin();

    const { register, handleSubmit, formState: { errors } } = useForm<LoginForm>({
        resolver: zodResolver(loginSchema),

    })

    return (
        <div className="min-h-screen bg-[#080C14] flex items-center justify-center px-4 relative overflow-hidden">
            {/* Background orbs */}
            <div className="absolute top-1/3 -left-32 w-80 h-80 rounded-full bg-rose-500/6 blur-3xl pointer-events-none" />
            <div className="absolute bottom-1/3 -right-32 w-80 h-80 rounded-full bg-rose-700/5 blur-3xl pointer-events-none" />

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
                    <h1 className="font-display text-2xl font-bold text-white">Dobrodošli nazad</h1>
                    <p className="text-slate-400 text-sm mt-1">Prijavite se na svoj račun</p>
                </div>

                {/* Form card */}
                <div className="bg-[#0F1623] rounded-2xl border border-white/8 p-6 shadow-2xl shadow-black/40">
                    <form onSubmit={handleSubmit((d) => login(d))} noValidate className="space-y-4">
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

                        {/* Password */}
                        <div>
                            <div className="flex items-center justify-between mb-1.5">
                                <label className="text-xs font-medium text-slate-400">Lozinka</label>
                                <Link to="/forgot-password" className="text-xs text-rose-400 hover:text-rose-300 transition-colors">
                                    Zaboravili ste?
                                </Link>
                            </div>
                            <div className="relative">
                                <Lock className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500" />
                                <input
                                    {...register('password')}
                                    type={showPassword ? 'text' : 'password'}
                                    placeholder="••••••••"
                                    autoComplete="current-password"
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

                        {/* Submit */}
                        <button
                            type="submit"
                            disabled={isPending}
                            className="w-full py-3 rounded-xl bg-rose-500 hover:bg-rose-600 disabled:opacity-50 disabled:cursor-not-allowed text-white font-semibold text-sm transition-colors shadow-lg shadow-rose-500/20 mt-2 flex items-center justify-center gap-2"
                        >
                            {isPending ? (
                                <><span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />Prijavljujem...</>
                            ) : 'Prijavi se'}
                        </button>
                    </form>
                </div>

                <p className="text-center text-sm text-slate-500 mt-5">
                    Nemate račun?{' '}
                    <Link to="/register" className="text-rose-400 hover:text-rose-300 font-medium transition-colors">
                        Registrujte se
                    </Link>
                </p>
            </motion.div>
        </div>
    )

}