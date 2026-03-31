import { Heart, MapPin, Scissors, Star } from "lucide-react";
import { useFavorites, useToggleFavorite } from "../../hooks/useSalons"
import { Link } from "react-router-dom";
import { motion } from "motion/react"

export default function FavoritesPage(){

    const {data: favorites = [] , isLoading } = useFavorites();

    const toggleFavorites = useToggleFavorite();

    return (
        <div className="max-w-4xl mx-auto">
      <div className="flex items-center gap-3 mb-8">
        <div className="w-10 h-10 rounded-xl bg-rose-500/10 flex items-center justify-center">
          <Heart className="w-5 h-5 text-rose-400" />
        </div>
        <div>
          <h1 className="font-display text-2xl font-bold text-white">Omiljeni Saloni</h1>
          <p className="text-sm text-slate-400">{favorites.length} sačuvanih salona</p>
        </div>
      </div>
 
      {isLoading ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {[1, 2, 3].map((i) => <div key={i} className="h-56 rounded-2xl bg-slate-800 animate-pulse" />)}
        </div>
      ) : favorites.length === 0 ? (
        <div className="text-center py-20">
          <Heart className="w-12 h-12 text-slate-700 mx-auto mb-4" />
          <h3 className="font-display font-semibold text-white mb-2">Nema sačuvanih salona</h3>
          <p className="text-slate-400 text-sm mb-5">Dodajte salone u favorite klikom na srce</p>
          <Link to="/salons" className="inline-flex px-5 py-2.5 rounded-xl bg-rose-500 text-white text-sm font-medium">
            Pretraži Salone
          </Link>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {favorites.map((fav, i) => (
            <motion.div key={fav.id} initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: i * 0.05 }}>
              <div className="rounded-2xl bg-[#0F1623] border border-white/5 overflow-hidden hover:border-white/10 transition-all group">
                <Link to={`/salons/${fav.salon.slug}`} className="block">
                  <div className="h-40 bg-gradient-to-br from-slate-800 to-slate-700 flex items-center justify-center">
                    <Scissors className="w-10 h-10 text-slate-600 group-hover:text-slate-500 transition-colors" />
                  </div>
                  <div className="p-4">
                    <h3 className="font-semibold text-white mb-1">{fav.salon.name}</h3>
                    <div className="flex items-center justify-between text-sm">
                      <span className="flex items-center gap-1 text-slate-400"><MapPin className="w-3.5 h-3.5" />{fav.salon.city}</span>
                      <span className="flex items-center gap-1"><Star className="w-3.5 h-3.5 text-amber-400 fill-amber-400" /><span className="text-white">{fav.salon.avgRating.toFixed(1)}</span></span>
                    </div>
                  </div>
                </Link>
                <div className="px-4 pb-4 flex gap-2">
                  <Link to={`/book/${fav.salonId}`} className="flex-1 py-2 rounded-xl bg-rose-500/10 border border-rose-500/20 text-rose-400 text-xs font-medium text-center hover:bg-rose-500 hover:text-white transition-all">
                    Rezerviši
                  </Link>
                  <button
                    onClick={() => toggleFavorites.mutate({ salonId: fav.salonId, isFavorited: true })}
                    className="w-9 h-9 rounded-xl bg-white/5 border border-white/8 flex items-center justify-center text-rose-400 hover:bg-rose-500/10 transition-colors"
                  >
                    <Heart className="w-4 h-4 fill-current" />
                  </button>
                </div>
              </div>
            </motion.div>
          ))}
        </div>
      )}
    </div>
    )

}