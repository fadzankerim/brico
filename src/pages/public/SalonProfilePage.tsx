import { Link, useNavigate, useParams } from "react-router-dom"
import { useAuthStore } from "../../store/authStore";
import { useState } from "react";
import { useFavorites, useSalon, useToggleFavorite } from "../../hooks/useSalons";
import { useQuery } from "@tanstack/react-query";
import { reviewService } from "../../services/review.service";
import type { Review } from "../../types/review.types";
import { formatDistanceToNow, parseISO } from "date-fns";
import ProfileSkeleton from "../../components/ProfileSkeleton";
import { ArrowLeft, BadgeCheck, Calendar, ChevronRight, Clock, Globe, Heart, MapPin, Phone, Scissors, Star } from "lucide-react";
import StarRating from "../../components/StarRating";
import { cn } from "../../lib/utils";
import { formatDuration } from "date-fns";
import { formatPrice } from "../../utils/dateUtils";
import { motion } from "motion/react";


type Tab = 'hairdressers' | 'services' | 'reviews'

export default function SalonProfilePage() {

    const { slug } = useParams<{ slug: string }>()
    const navigate = useNavigate();

    const { isAuthenticated } = useAuthStore();
    const [tab, setTab] = useState<Tab>('hairdressers');

    const { data: salon, isLoading, error } = useSalon(slug!)
    const { data: favorites } = useFavorites();
    const { data: reviews = [] } = useQuery<Review[]>({
      queryKey: ['reviews', 'salon', salon?.id],
      queryFn:  () => reviewService.getSalonReviews(salon!.id),
      enabled:  !!salon?.id,
      staleTime: 1000 * 60,
    })

    const toggleFavorite = useToggleFavorite();

    const isFavorited = favorites?.some((f) => f.id === salon?.id) ?? false;

    if (isLoading) return <ProfileSkeleton />

    if (error || !salon) return (
        <div className="min-h-screen pt-24 flex items-center justify-center">
            <div className="text-center">
                <p className="text-slate-400 mb-4">Salon nije pronađen.</p>
                <Link to="/salons" className="text-rose-400 hover:text-rose-300 text-sm">← Nazad na pretragu</Link>
            </div>
        </div>
    )


    const todayHours = salon.workingHours?.find(
        (wh) => wh.dayOfWeek === new Date().getDay()
    )
    const isOpenToday = todayHours && !todayHours.isDayOff

    return (
        <div className="min-h-screen pt-16">
            {/* ── Breadcrumb ── */}
            <div className="max-w-7xl mx-auto px-4 sm:px-6 pt-6 pb-2">
                <button
                    onClick={() => navigate(-1)}
                    className="flex items-center gap-2 text-sm text-slate-500 hover:text-white transition-colors"
                >
                    <ArrowLeft className="w-4 h-4" /> Nazad
                </button>
            </div>

            {/* ── Hero ── */}
            <div className="max-w-7xl mx-auto px-4 sm:px-6 py-4">
                {/* Dodajemo 'aspect-[2/1]' ili fiksni 'h-[400px]' za stabilnost na desktopu */}
                <div className="grid grid-cols-1 lg:grid-cols-3 gap-4 h-64 sm:h-80 lg:h-[400px]">

                    {/* Main photo */}
                    <div className="lg:col-span-2 relative rounded-2xl overflow-hidden bg-slate-800">
                        {salon.photos?.[0]?.url ? (
                            <img
                                src={salon.photos[0].url}
                                alt={salon.name}
                                /* Ključno: h-full i object-cover sprečavaju ispadanje */
                                className="absolute inset-0 w-full h-full object-cover transition-transform hover:scale-105 duration-500"
                            />
                        ) : (
                            <div className="flex items-center justify-center h-full">
                                <Scissors className="w-16 h-16 text-slate-600" />
                            </div>
                        )}
                    </div>

                    {/* Side photos */}
                    <div className="hidden lg:grid grid-rows-2 gap-4 h-full">
                        {[1, 2].map((i) => (
                            <div key={i} className="relative rounded-2xl overflow-hidden bg-slate-800">
                                {salon.photos?.[i]?.url ? (
                                    <img
                                        src={salon.photos[i].url}
                                        alt=""
                                        className="absolute inset-0 w-full h-full object-cover transition-transform hover:scale-105 duration-500"
                                    />
                                ) : (
                                    <div className="flex items-center justify-center h-full">
                                        <Scissors className="w-8 h-8 text-slate-600" />
                                    </div>
                                )}
                            </div>
                        ))}
                    </div>
                </div>
            </div>

            {/* ── Info + Book ── */}
            <div className="max-w-7xl mx-auto px-4 sm:px-6 pb-4">
                <div className="flex flex-col lg:flex-row lg:items-start lg:justify-between gap-6">
                    {/* Left: info */}
                    <div className="flex-1">
                        <div className="flex items-center gap-3 flex-wrap mb-2">
                            <h1 className="font-display text-3xl font-bold text-white">{salon.name}</h1>
                            {salon.verified && (
                                <span className="flex items-center gap-1 px-2.5 py-1 rounded-lg bg-rose-500/10 border border-rose-500/20 text-rose-400 text-xs font-semibold">
                                    <BadgeCheck className="w-3.5 h-3.5" /> Verified
                                </span>
                            )}
                        </div>

                        <div className="flex flex-wrap items-center gap-4 text-sm text-slate-400 mb-4">
                            <span className="flex items-center gap-1.5"><MapPin className="w-4 h-4 text-slate-500" />{salon.city}, {salon.address}</span>
                            {salon.phone && <span className="flex items-center gap-1.5"><Phone className="w-4 h-4 text-slate-500" />{salon.phone}</span>}
                            {salon.website && (
                                <a href={salon.website} target="_blank" rel="noreferrer" className="flex items-center gap-1.5 hover:text-white transition-colors">
                                    <Globe className="w-4 h-4 text-slate-500" />{salon.website.replace('https://', '')}
                                </a>
                            )}
                        </div>

                        <div className="flex items-center gap-4">
                            <div className="flex items-center gap-2">
                                <StarRating value={Math.round(salon.avgRating)} readonly size="sm" />
                                <span className="font-semibold text-white">{salon.avgRating.toFixed(1)}</span>
                                <span className="text-slate-500 text-sm">({salon.reviewCount} recenzija)</span>
                            </div>

                            <span className={cn('flex items-center gap-1.5 text-sm font-medium px-2.5 py-1 rounded-lg',
                                isOpenToday ? 'bg-emerald-500/10 text-emerald-400' : 'bg-slate-500/10 text-slate-400'
                            )}>
                                <Clock className="w-3.5 h-3.5" />
                                {isOpenToday ? `Otvoreno · ${todayHours.startTime}–${todayHours.endTime}` : 'Zatvoreno danas'}
                            </span>
                        </div>

                        {salon.description && (
                            <p className="mt-4 text-slate-400 text-sm leading-relaxed max-w-2xl">{salon.description}</p>
                        )}
                    </div>

                    {/* Right: actions */}
                    <div className="flex items-center gap-3 lg:shrink-0">
                        <button
                            onClick={() => toggleFavorite.mutate({ salon: { id: salon.id, name: salon.name, slug: salon.slug, city: salon.city, avgRating: salon.avgRating, verified: salon.verified }, isFavorited })}
                            className={cn('w-11 h-11 rounded-xl flex items-center justify-center border transition-all',
                                isFavorited
                                    ? 'bg-rose-500/15 border-rose-500/30 text-rose-400'
                                    : 'bg-white/5 border-white/8 text-slate-400 hover:text-white hover:border-white/15'
                            )}
                        >
                            <Heart className={cn('w-5 h-5', isFavorited && 'fill-current')} />
                        </button>

                        {isAuthenticated() ? (
                            <Link
                                to={`/book/${salon.id}`}
                                className="flex items-center gap-2 px-6 py-3 rounded-xl bg-rose-500 hover:bg-rose-600 text-white font-semibold transition-colors shadow-lg shadow-rose-500/20"
                            >
                                <Calendar className="w-4 h-4" /> Rezerviši Termin
                            </Link>
                        ) : (
                            <Link
                                to="/login"
                                className="flex items-center gap-2 px-6 py-3 rounded-xl bg-rose-500 hover:bg-rose-600 text-white font-semibold transition-colors"
                            >
                                Prijavi se za rezervaciju
                            </Link>
                        )}
                    </div>
                </div>
            </div>

            {/* ── Tabs ── */}
            <div className="sticky top-16 z-10 bg-[#080C14]/95 backdrop-blur-xl border-y border-white/5 mt-4">
                <div className="max-w-7xl mx-auto px-4 sm:px-6">
                    <div className="flex gap-1 -mb-px">
                        {([
                            { key: 'hairdressers', label: 'Frizeri', count: salon.hairdressers?.length },
                            { key: 'services', label: 'Usluge', count: salon.services?.length },
                            { key: 'reviews', label: 'Recenzije', count: salon.reviewCount },
                        ] as { key: Tab; label: string; count?: number }[]).map((t) => (
                            <button
                                key={t.key}
                                onClick={() => setTab(t.key)}
                                className={cn('flex items-center gap-2 px-4 py-3.5 text-sm font-medium border-b-2 transition-colors',
                                    tab === t.key
                                        ? 'text-white border-rose-500'
                                        : 'text-slate-400 hover:text-white border-transparent'
                                )}
                            >
                                {t.label}
                                {t.count !== undefined && (
                                    <span className={cn('text-xs px-1.5 py-0.5 rounded-md',
                                        tab === t.key ? 'bg-rose-500/20 text-rose-400' : 'bg-white/5 text-slate-500'
                                    )}>
                                        {t.count}
                                    </span>
                                )}
                            </button>
                        ))}
                    </div>
                </div>
            </div>

            {/* ── Tab content ── */}
            <div className="max-w-7xl mx-auto px-4 sm:px-6 py-8">

                {/* Hairdressers tab */}
                {tab === 'hairdressers' && (
                    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
                        {salon.hairdressers?.map((h) => (
                            <motion.div
                                key={h.id}
                                initial={{ opacity: 0, y: 12 }}
                                animate={{ opacity: 1, y: 0 }}
                                className="p-5 rounded-2xl bg-[#0F1623] border border-white/5 hover:border-white/10 transition-all"
                            >
                                <div className="flex items-start gap-4 mb-4">
                                    <div className="w-14 h-14 rounded-xl bg-gradient-to-br from-rose-500 to-rose-700 flex items-center justify-center text-white font-bold text-lg shrink-0">
                                        {h.fullName.split(' ').map((n) => n[0]).join('').slice(0, 2)}
                                    </div>
                                    <div className="flex-1 min-w-0">
                                        <h3 className="font-semibold text-white truncate">{h.fullName}</h3>
                                        {h.specialties && (
                                            <p className="text-xs text-slate-500 mt-0.5 truncate">{h.specialties}</p>
                                        )}
                                        {h.avgRating && (
                                            <div className="flex items-center gap-1 mt-1">
                                                <Star className="w-3 h-3 text-amber-400 fill-amber-400" />
                                                <span className="text-xs text-white">{h.avgRating.toFixed(1)}</span>
                                            </div>
                                        )}
                                    </div>
                                </div>
                                {h.bio && <p className="text-xs text-slate-400 leading-relaxed mb-4 line-clamp-2">{h.bio}</p>}
                                {isAuthenticated() ? (
                                    <Link
                                        to={`/book/${salon.id}?hairdresser=${h.id}`}
                                        className="w-full flex items-center justify-center gap-2 py-2.5 rounded-xl bg-rose-500/10 border border-rose-500/20 text-rose-400 text-sm font-medium hover:bg-rose-500 hover:text-white hover:border-rose-500 transition-all"
                                    >
                                        Rezerviši <ChevronRight className="w-3.5 h-3.5" />
                                    </Link>
                                ) : (
                                    <Link to="/login" className="w-full flex items-center justify-center py-2.5 rounded-xl bg-white/5 border border-white/8 text-slate-400 text-sm hover:text-white transition-colors">
                                        Prijavi se za rezervaciju
                                    </Link>
                                )}
                            </motion.div>
                        ))}
                    </div>
                )}

                {/* Services tab */}
                {tab === 'services' && (
                    <div className="max-w-2xl space-y-3">
                        {salon.services?.map((s, i) => (
                            <motion.div
                                key={s.id}
                                initial={{ opacity: 0, x: -12 }}
                                animate={{ opacity: 1, x: 0 }}
                                transition={{ delay: i * 0.05 }}
                                className="flex items-center justify-between p-4 rounded-xl bg-[#0F1623] border border-white/5 hover:border-white/10 transition-colors"
                            >
                                <div className="flex items-center gap-3">
                                    <div className="w-9 h-9 rounded-lg bg-rose-500/10 flex items-center justify-center shrink-0">
                                        <Scissors className="w-4 h-4 text-rose-400" />
                                    </div>
                                    <div>
                                        <p className="text-sm font-medium text-white">{s.name}</p>
                                        {s.description && <p className="text-xs text-slate-500 mt-0.5 line-clamp-1">{s.description}</p>}
                                        <p className="text-xs text-slate-500 mt-0.5">{formatDuration({ hours: Math.floor(s.durationMinutes / 60), minutes: s.durationMinutes % 60 })}</p>
                                    </div>
                                </div>
                                <div className="text-right shrink-0 ml-4">
                                    <p className="font-semibold text-white">{formatPrice(s.price)}</p>
                                </div>
                            </motion.div>
                        ))}
                    </div>
                )}

                {/* Reviews tab */}
                {tab === 'reviews' && (
                    <div className="max-w-2xl space-y-4">
                        {/* Average rating summary */}
                        <div className="p-5 rounded-2xl bg-[#0F1623] border border-white/5 flex items-center gap-6 mb-6">
                            <div className="text-center">
                                <div className="text-5xl font-display font-bold text-white">{salon.avgRating.toFixed(1)}</div>
                                <StarRating value={Math.round(salon.avgRating)} readonly size="sm" />
                                <p className="text-xs text-slate-500 mt-1">{salon.reviewCount} recenzija</p>
                            </div>
                            <div className="flex-1 space-y-1.5">
                                {[5, 4, 3, 2, 1].map((star) => {
                                    const count = reviews.filter(r => r.rating === star).length
                                    const pct = reviews.length > 0 ? (count / reviews.length) * 100 : 0
                                    return (
                                      <div key={star} className="flex items-center gap-2">
                                        <span className="text-xs text-slate-500 w-2">{star}</span>
                                        <div className="flex-1 h-1.5 rounded-full bg-slate-800 overflow-hidden">
                                          <div className="h-full bg-amber-400 rounded-full transition-all" style={{ width: `${pct}%` }} />
                                        </div>
                                        <span className="text-xs text-slate-600 w-5">{count}</span>
                                      </div>
                                    )
                                })}
                            </div>
                        </div>

                        {reviews.length === 0 ? (
                          <div className="py-10 text-center text-slate-500 text-sm bg-[#0F1623] rounded-2xl border border-white/5">
                            Nema recenzija za ovaj salon
                          </div>
                        ) : (
                          reviews.map((r) => (
                            <div key={r.id} className="p-4 rounded-xl bg-[#0F1623] border border-white/5">
                              <div className="flex items-start gap-3">
                                <div className="w-9 h-9 rounded-lg bg-slate-700 flex items-center justify-center text-xs font-bold text-white shrink-0">
                                  {r.clientName?.charAt(0) ?? '?'}
                                </div>
                                <div className="flex-1">
                                  <div className="flex items-center justify-between mb-1">
                                    <span className="text-sm font-medium text-white">{r.clientName}</span>
                                    <span className="text-xs text-slate-500">
                                      {r.createdAt ? formatDistanceToNow(parseISO(r.createdAt), { addSuffix: true }) : ''}
                                    </span>
                                  </div>
                                  <StarRating value={r.rating} readonly size="sm" />
                                  {r.comment && (
                                    <p className="text-sm text-slate-400 mt-2 leading-relaxed">{r.comment}</p>
                                  )}
                                  {r.ownerReply && (
                                    <div className="mt-3 pl-3 border-l-2 border-rose-500/30">
                                      <p className="text-xs text-slate-500 mb-1">Odgovor vlasnika:</p>
                                      <p className="text-sm text-slate-300">{r.ownerReply}</p>
                                    </div>
                                  )}
                                </div>
                              </div>
                            </div>
                          ))
                        )}
                    </div>
                )}
            </div>

            {/* ── Sticky mobile book CTA ── */}
            <div className="lg:hidden fixed bottom-0 inset-x-0 p-4 bg-[#080C14]/95 backdrop-blur-xl border-t border-white/5 z-20">
                {isAuthenticated() ? (
                    <Link
                        to={`/book/${salon.id}`}
                        className="flex items-center justify-center gap-2 w-full py-3.5 rounded-xl bg-rose-500 hover:bg-rose-600 text-white font-semibold transition-colors"
                    >
                        <Calendar className="w-4.5 h-4.5" /> Rezerviši Termin
                    </Link>
                ) : (
                    <Link to="/login" className="flex items-center justify-center gap-2 w-full py-3.5 rounded-xl bg-rose-500 text-white font-semibold">
                        Prijavi se za rezervaciju
                    </Link>
                )}
            </div>
            <div className="lg:hidden h-20" /> {/* spacer for sticky CTA */}
        </div>
    )





}