import { useRef } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { motion, useInView } from 'motion/react'
import { useQuery } from '@tanstack/react-query'
import { salonService } from '../../services/salon.service'
import {
  Scissors,
  MapPin,
  Star,
  BadgeCheck,
  CalendarDays,
  Zap,
  Shield,
  ChevronRight,
  Search,
} from 'lucide-react'
import { useState } from 'react'
import Navbar from '../../components/Navbar'
import Footer from '../../components/Footer'

//  Mock featured - replace with api call 
const FEATURED = [
  { id: 1, slug: 'elite-cut-sarajevo', name: 'Elite Cut', city: 'Sarajevo', rating: 4.9, reviews: 213, price: 15, verified: true, color: 'from-rose-900 to-rose-800' },
  { id: 2, slug: 'barber-king-mostar', name: 'Barber King', city: 'Mostar', rating: 4.7, reviews: 148, price: 12, verified: true, color: 'from-slate-800 to-slate-700' },
  { id: 3, slug: 'style-lab-banja-luka', name: 'Style Lab', city: 'Banja Luka', rating: 4.8, reviews: 97, price: 18, verified: false, color: 'from-indigo-900 to-indigo-800' },
]

const HOW_IT_WORKS = [
  { step: '01', title: 'Pronađi Salon', desc: 'Pretraži salone po gradu, ocjeni ili cijeni. Filtriraj po blizini koristeći GPS.', icon: Search },
  { step: '02', title: 'Rezerviši Termin', desc: 'Odaberi frizera, uslugu i slobodan termin. Cijeli proces za manje od 60 sekundi.', icon: CalendarDays },
  { step: '03', title: 'Uživaj', desc: 'Dobij potvrdu na email. Podsjetnik 24h i 1h ranije. Ostavi recenziju nakon posjete.', icon: Star },
]

const FEATURES = [
  { icon: Zap, title: 'Instant Rezervacija', desc: 'Rezerviši termin u realnom vremenu, bez čekanja na potvrdu telefona.' },
  { icon: BadgeCheck, title: 'Verifikovani Saloni', desc: 'Svi saloni prolaze provjeru kvaliteta. Badge vjerodostojnosti za pouzdanost.' },
  { icon: Shield, title: 'Sigurna Plaćanja', desc: 'Nema skrivenih naknada. Cijena prikazana unaprijed, plaćaš direktno u salonu.' },
  { icon: Star, title: 'Provjerene Recenzije', desc: 'Recenzije samo od verifikovanih klijenata koji su stvarno posjetili salon.' },
]

function FadeIn({ children, delay = 0, className = '' }: { children: React.ReactNode; delay?: number; className?: string }) {
  const ref = useRef(null)
  const inView = useInView(ref, { once: true, margin: '-80px' })
  return (
    <motion.div
      ref={ref}
      initial={{ opacity: 0, y: 24 }}
      animate={inView ? { opacity: 1, y: 0 } : {}}
      transition={{ duration: 0.5, delay, ease: [0.25, 0.1, 0.25, 1] }}
      className={className}
    >
      {children}
    </motion.div>
  )
}

const CARD_COLORS = [
  'from-rose-900 to-rose-800',
  'from-slate-800 to-slate-700',
  'from-indigo-900 to-indigo-800',
]

export default function LandingPage() {
  const navigate = useNavigate()
  const [city, setCity] = useState('')

  const { data: featuredData } = useQuery({
    queryKey: ['salons', 'featured'],
    queryFn: () => salonService.search({ size: 3 } as any),
    staleTime: 1000 * 60 * 5,
  })
  const featured = featuredData?.content ?? []

  function handleSearch(e: React.SubmitEvent) {
    e.preventDefault()

    if(city.trim()){
      const params = new URLSearchParams()
      params.set('city', city.trim())

      navigate(`/salons?${params.toString()}`)
    } else {
      navigate(`/salons`)
    }
  }

  return (
    <div className="overflow-x-hidden">
      
      {/* ── HERO ────────────────────────────────────────────────────────────── */}
      <section className="relative min-h-screen flex items-center pt-16">
        {/* Background orbs */}
        <div className="absolute inset-0 overflow-hidden pointer-events-none">
          <div className="absolute top-1/4 -left-32 w-96 h-96 rounded-full bg-rose-500/8 blur-3xl" />
          <div className="absolute top-1/3 -right-32 w-80 h-80 rounded-full bg-rose-700/6 blur-3xl" />
          <div className="absolute bottom-0 left-1/2 -translate-x-1/2 w-full h-px bg-gradient-to-r from-transparent via-white/5 to-transparent" />
          {/* Grid texture */}
          <div
            className="absolute inset-0 opacity-[0.02]"
            style={{ backgroundImage: 'linear-gradient(rgba(255,255,255,0.1) 1px, transparent 1px), linear-gradient(90deg, rgba(255,255,255,0.1) 1px, transparent 1px)', backgroundSize: '60px 60px' }}
          />
        </div>

        <div className="max-w-7xl mx-auto px-4 sm:px-6 py-24 relative">
          <div className="max-w-3xl">
            {/* Eyebrow */}
            <motion.div
              initial={{ opacity: 0, y: 16 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.5 }}
              className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-rose-500/10 border border-rose-500/20 text-rose-400 text-sm font-medium mb-6"
            >
              <span className="w-1.5 h-1.5 rounded-full bg-rose-500 animate-pulse" />
              500+ salona u BiH i regiji
            </motion.div>

            {/* Heading */}
            <motion.h1
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.55, delay: 0.1 }}
              className="font-display text-5xl sm:text-6xl lg:text-7xl font-bold leading-[1.05] tracking-tight mb-6"
            >
              Pronađi savršenog{' '}
              <span className="relative">
                <span className="text-transparent bg-clip-text bg-gradient-to-r from-rose-400 to-rose-600">
                  frizera
                </span>
                <svg className="absolute -bottom-2 left-0 w-full" viewBox="0 0 300 12" fill="none">
                  <path d="M2 8 Q75 2 150 8 Q225 14 298 8" stroke="#e94560" strokeWidth="2.5" strokeLinecap="round" fill="none" opacity="0.6" />
                </svg>
              </span>{' '}
              u tvom gradu
            </motion.h1>

            <motion.p
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.55, delay: 0.2 }}
              className="text-xl text-slate-400 leading-relaxed mb-10 max-w-xl"
            >
              Rezerviši termin za manje od 60 sekundi. Bez čekanja na telefon, bez iznenađenja s cijenom.
            </motion.p>

            {/* Search bar */}
            <motion.form
              onSubmit={handleSearch}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.55, delay: 0.3 }}
              className="flex gap-2 max-w-lg"
            >
              <div className="flex-1 relative">
                <MapPin className="absolute left-4 top-1/2 -translate-y-1/2 w-4.5 h-4.5 text-slate-500" />
                <input
                  type="text"
                  value={city}
                  onChange={(e) => setCity(e.target.value)}
                  placeholder="Grad ili lokacija..."
                  className="w-full pl-11 pr-4 py-3.5 rounded-2xl bg-[#0F1623] border border-white/10 text-white placeholder:text-slate-500 text-sm focus:outline-none focus:border-rose-500/50 focus:ring-2 focus:ring-rose-500/20 transition-all"
                />
              </div>
              <button
                type="submit"
                className="px-6 py-3.5 rounded-2xl bg-rose-500 hover:bg-rose-600 text-white font-semibold text-sm transition-colors shadow-lg shadow-rose-500/25 whitespace-nowrap hover:cursor-pointer"
              >
                Pretraži
              </button>
            </motion.form>

            {/* Trust badges */}
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ duration: 0.6, delay: 0.5 }}
              className="flex flex-wrap items-center gap-5 mt-8 text-sm text-slate-500"
            >
              {[
                { icon: BadgeCheck, text: '500+ salona' },
                { icon: Star, text: '4.9 prosječna ocjena' },
                { icon: CalendarDays, text: 'Instant rezervacija' },
              ].map(({ icon: Icon, text }) => (
                <div key={text} className="flex items-center gap-1.5">
                  <Icon className="w-4 h-4 text-rose-500/70" />
                  {text}
                </div>
              ))}
            </motion.div>
          </div>
        </div>
      </section>

      {/* ── FEATURED SALONS ─────────────────────────────────────────────────── */}
      <section className="py-20 border-t border-white/5">
        <div className="max-w-7xl mx-auto px-4 sm:px-6">
          <FadeIn className="flex items-end justify-between mb-10">
            <div>
              <p className="text-rose-500 text-sm font-semibold uppercase tracking-wider mb-2">Istaknuti</p>
              <h2 className="font-display text-3xl font-bold text-white">Popularni Saloni</h2>
            </div>
            <Link
              to="/salons"
              className="hidden sm:flex items-center gap-1 text-sm text-slate-400 hover:text-rose-400 transition-colors"
            >
              Prikaži sve <ChevronRight className="w-4 h-4" />
            </Link>
          </FadeIn>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
            {featured.map((salon, i) => (
              <FadeIn key={salon.id} delay={i * 0.1}>
                <Link
                  to={`/salons/${salon.slug}`}
                  className="group block rounded-2xl overflow-hidden bg-[#0F1623] border border-white/5 hover:border-rose-500/20 transition-all hover:shadow-xl hover:shadow-black/40"
                >
                  <div className={`h-44 bg-gradient-to-br ${CARD_COLORS[i % CARD_COLORS.length]} flex items-center justify-center relative`}>
                    <Scissors className="w-12 h-12 text-white/20" />
                    {salon.verified && (
                      <div className="absolute top-3 left-3 flex items-center gap-1 px-2 py-1 rounded-lg bg-rose-500/90 text-xs font-semibold text-white">
                        <BadgeCheck className="w-3 h-3" />Verified
                      </div>
                    )}
                  </div>
                  <div className="p-4">
                    <h3 className="font-display font-semibold text-white mb-1 group-hover:text-rose-300 transition-colors">{salon.name}</h3>
                    <div className="flex items-center justify-between">
                      <span className="text-sm text-slate-400 flex items-center gap-1"><MapPin className="w-3 h-3" />{salon.city}</span>
                      <div className="flex items-center gap-1 text-sm">
                        <Star className="w-3.5 h-3.5 text-amber-400 fill-amber-400" />
                        <span className="text-white font-medium">{(salon.avgRating ?? 0).toFixed(1)}</span>
                        <span className="text-slate-500">({salon.reviewCount ?? 0})</span>
                      </div>
                    </div>
                  </div>
                </Link>
              </FadeIn>
            ))}
          </div>

          <div className="mt-8 text-center sm:hidden">
            <Link to="/salons" className="inline-flex items-center gap-1 text-sm text-rose-400 hover:text-rose-300">
              Prikaži sve salone <ChevronRight className="w-4 h-4" />
            </Link>
          </div>
        </div>
      </section>

      {/* ── HOW IT WORKS ─────────────────────────────────────────────────────── */}
      <section id="how-it-works" className="py-20 border-t border-white/5">
        <div className="max-w-7xl mx-auto px-4 sm:px-6">
          <FadeIn className="text-center mb-14">
            <p className="text-rose-500 text-sm font-semibold uppercase tracking-wider mb-2">Jednostavno</p>
            <h2 className="font-display text-3xl sm:text-4xl font-bold text-white">Kako Funkcioniše?</h2>
          </FadeIn>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8 relative">
            {/* Connector line (desktop) */}
            <div className="hidden md:block absolute top-10 left-1/3 right-1/3 h-px bg-gradient-to-r from-transparent via-rose-500/30 to-transparent" />

            {HOW_IT_WORKS.map((item, i) => (
              <FadeIn key={item.step} delay={i * 0.15} className="relative">
                <div className="text-center">
                  <div className="inline-flex items-center justify-center w-20 h-20 rounded-2xl bg-[#0F1623] border border-white/8 mb-5 relative">
                    <item.icon className="w-8 h-8 text-rose-400" />
                    <span className="absolute -top-2 -right-2 w-6 h-6 rounded-full bg-rose-500 text-white text-xs font-bold flex items-center justify-center">
                      {i + 1}
                    </span>
                  </div>
                  <h3 className="font-display text-xl font-semibold text-white mb-2">{item.title}</h3>
                  <p className="text-slate-400 text-sm leading-relaxed">{item.desc}</p>
                </div>
              </FadeIn>
            ))}
          </div>
        </div>
      </section>

      {/* ── FEATURES ─────────────────────────────────────────────────────────── */}
      <section className="py-20 border-t border-white/5">
        <div className="max-w-7xl mx-auto px-4 sm:px-6">
          <FadeIn className="text-center mb-14">
            <p className="text-rose-500 text-sm font-semibold uppercase tracking-wider mb-2">Prednosti</p>
            <h2 className="font-display text-3xl sm:text-4xl font-bold text-white">Zašto Brico?</h2>
          </FadeIn>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
            {FEATURES.map((f, i) => (
              <FadeIn key={f.title} delay={i * 0.1}>
                <div className="flex gap-4 p-5 rounded-2xl bg-[#0F1623] border border-white/5 hover:border-white/10 transition-colors">
                  <div className="w-11 h-11 rounded-xl bg-rose-500/10 flex items-center justify-center shrink-0">
                    <f.icon className="w-5 h-5 text-rose-400" />
                  </div>
                  <div>
                    <h3 className="font-semibold text-white mb-1">{f.title}</h3>
                    <p className="text-sm text-slate-400 leading-relaxed">{f.desc}</p>
                  </div>
                </div>
              </FadeIn>
            ))}
          </div>
        </div>
      </section>

      {/* ── CTA ─────────────────────────────────────────────────────────────── */}
      <section id="za-salone" className="py-20 border-t border-white/5">
        <div className="max-w-7xl mx-auto px-4 sm:px-6">
          <FadeIn>
            <div className="relative rounded-3xl overflow-hidden bg-gradient-to-br from-rose-600 to-rose-800 p-10 sm:p-16 text-center">
              <div className="absolute inset-0 opacity-10" style={{ backgroundImage: 'radial-gradient(circle at 50% 50%, white 1px, transparent 1px)', backgroundSize: '30px 30px' }} />
              <div className="relative">
                <h2 className="font-display text-3xl sm:text-4xl font-bold text-white mb-4">
                  Imaš frizerski salon?
                </h2>
                <p className="text-rose-100 text-lg mb-8 max-w-xl mx-auto">
                  Registruj se besplatno i počni primati online rezervacije danas.
                </p>
                <div className="flex flex-col sm:flex-row gap-3 justify-center">
                  <Link
                    to="/register"
                    className="px-8 py-3.5 rounded-xl bg-white text-rose-600 font-semibold hover:bg-rose-50 transition-colors shadow-lg"
                  >
                    Registruj Salon — Besplatno
                  </Link>
                  <Link
                    to="/salons"
                    className="px-8 py-3.5 rounded-xl bg-white/15 text-white font-semibold hover:bg-white/20 transition-colors border border-white/20"
                  >
                    Pretraži Salone
                  </Link>
                </div>
              </div>
            </div>
          </FadeIn>
        </div>
      </section>
      
    </div>
  )
}