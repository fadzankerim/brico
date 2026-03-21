import { Link, useNavigate } from "react-router-dom";
import { useAuthStore } from "../store/authStore";
import type { Salon } from "../types/salon.typs";
import { useFavorites, useToggleFavorite } from "../hooks/useSalons";
import { useState } from "react";
import { motion } from "motion/react"
import { BadgeCheck, Heart, MapPin, Scissors, Star } from "lucide-react";
import { cn } from "../lib/utils";
import { formatPrice } from "../utils/dateUtils";


interface SalonCardProps{
    salon: Salon
    className?: string
}

export default function SalonCard( {salon , className} : SalonCardProps){

    const { isAuthenticated } = useAuthStore();
    const navigate = useNavigate();

    const { data: favorites } = useFavorites();
    const toggleFavorite = useToggleFavorite();

    const isFavorited = favorites?.some((f) => f.salonId === salon.id) ?? false;
    const [localFav, setLocalFav] = useState(isFavorited);

    const minPrice = salon.services?.length ? Math.min(...salon.services.map((s) => s.price)) : null;

    const primaryPhoto = salon.photos?.find((p) => p.isPrimary)?.url

    function handleFavorite(e: React.MouseEvent){
        e.preventDefault();
        e.stopPropagation();
        if(!isAuthenticated){
            navigate('/login')
            return;
        }

        setLocalFav((prev) => !prev);
        toggleFavorite.mutate(
      { salonId: salon.id, isFavorited: localFav },
      { onError: () => setLocalFav((prev) => !prev) } 
    )

    }

    return (
    <motion.div
      whileHover={{ y: -3 }}
      transition={{ type: 'spring', stiffness: 300, damping: 20 }}
      className={cn('group relative', className)}
    >
      <Link
        to={`/salons/${salon.slug}`}
        className="block rounded-2xl overflow-hidden bg-[#0F1623] border border-white/5 hover:border-white/10 transition-all duration-300 shadow-lg hover:shadow-xl hover:shadow-black/40"
      >
        {/* Photo */}
        <div className="relative h-48 bg-gradient-to-br from-slate-800 to-slate-900 overflow-hidden">
          {primaryPhoto ? (
            <img
              src={primaryPhoto}
              alt={salon.name}
              className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
            />
          ) : (
            <div className="w-full h-full flex items-center justify-center">
              <Scissors className="w-12 h-12 text-slate-700" />
            </div>
          )}
 
          {/* Gradient overlay */}
          <div className="absolute inset-0 bg-gradient-to-t from-[#0F1623]/80 via-transparent to-transparent" />
 
          {/* Verified badge */}
          {salon.verified && (
            <div className="absolute top-3 left-3 flex items-center gap-1 px-2 py-1 rounded-lg bg-rose-500/90 backdrop-blur-sm text-xs font-semibold text-white">
              <BadgeCheck className="w-3 h-3" />
              Verified
            </div>
          )}
 
          {/* Heart button */}
          <button
            onClick={handleFavorite}
            className={cn(
              'absolute top-3 right-3 w-8 h-8 rounded-xl flex items-center justify-center backdrop-blur-sm transition-all',
              localFav
                ? 'bg-rose-500/90 text-white'
                : 'bg-black/40 text-white/70 hover:bg-black/60 hover:text-white'
            )}
          >
            <Heart
              className={cn('w-4 h-4 transition-all', localFav && 'fill-current')}
            />
          </button>
 
          {/* Price tag */}
          {minPrice !== null && (
            <div className="absolute bottom-3 right-3 px-2 py-1 rounded-lg bg-black/50 backdrop-blur-sm text-xs font-semibold text-white">
              od {formatPrice(minPrice)}
            </div>
          )}
        </div>
 
        {/* Body */}
        <div className="p-4">
          <div className="flex items-start justify-between gap-2 mb-2">
            <h3 className="font-display font-semibold text-white text-base leading-tight group-hover:text-rose-300 transition-colors line-clamp-1">
              {salon.name}
            </h3>
          </div>
 
          <div className="flex items-center gap-1 text-slate-400 text-sm mb-3">
            <MapPin className="w-3.5 h-3.5 shrink-0 text-slate-500" />
            <span className="truncate">{salon.city}</span>
            {salon.address && (
              <>
                <span className="text-slate-700">·</span>
                <span className="truncate text-slate-500">{salon.address}</span>
              </>
            )}
          </div>
 
          {/* Rating + hairdressers count */}
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-1.5">
              <div className="flex items-center gap-0.5">
                {[1, 2, 3, 4, 5].map((star) => (
                  <Star
                    key={star}
                    className={cn(
                      'w-3.5 h-3.5',
                      star <= Math.round(salon.avgRating)
                        ? 'text-amber-400 fill-amber-400'
                        : 'text-slate-700 fill-slate-700'
                    )}
                  />
                ))}
              </div>
              <span className="text-sm font-semibold text-white">
                {salon.avgRating.toFixed(1)}
              </span>
              <span className="text-xs text-slate-500">({salon.reviewCount})</span>
            </div>
 
            {salon.hairdressers && (
              <div className="flex items-center gap-1 text-xs text-slate-500">
                <Scissors className="w-3 h-3" />
                {salon.hairdressers.length} frizera
              </div>
            )}
          </div>
        </div>
 
        {/* Footer - Book CTA */}
        <div className="px-4 pb-4">
          <div className="w-full py-2.5 rounded-xl bg-rose-500/10 border border-rose-500/20 text-rose-400 text-sm font-semibold text-center group-hover:bg-rose-500 group-hover:text-white group-hover:border-rose-500 transition-all duration-200">
            Rezerviši Termin
          </div>
        </div>
      </Link>
    </motion.div>
  )
      
}