import { useSearchParams } from "react-router-dom"
import { useUIStore } from "../../store/uiStore"
import { useEffect, useState } from "react"
import { useSalonSearch } from "../../hooks/useSalons"
import { ChevronLeft, ChevronRight, MapPin, SlidersHorizontal, X } from "lucide-react"
import { AnimatePresence, motion } from "motion/react"
import type { SalonSearchParams } from "../../types/salon.typs"
import SalonCardSkeleton from "../../components/SalonCardSkeleton"
import SalonCard from "../../components/SalonCard"


const SORT_OPTIONS = [
    { value: 'rating', label: 'Najviša ocjena' },
    { value: 'popularity', label: 'Najpopularniji' },
    { value: 'price_asc', label: 'Cijena: niža → viša' },
    { value: 'price_desc', label: 'Cijena: viša → niža' },
    { value: 'distance', label: 'Najbliži' },
]

const CITIES = ['Sarajevo', 'Mostar', 'Banja Luka', 'Tuzla', 'Zenica', 'Bijeljina', 'Trebinje']

export default function SalonSearchPage() {


    const [searchParams, setSearchParams] = useSearchParams()

    const { searchFilters, setSearchFilters, resetFilters } = useUIStore();
    const [showFilters, setShowFilters] = useState(false);

    useEffect(() => {
        const city = searchParams.get('city') || '';

        if (city) {
            setSearchFilters({ city })
        }

    }, [])

    const { data, isLoading, isFetching } = useSalonSearch(searchFilters)

    const salons = data?.content ?? []
    const totalPages = data?.totalPages ?? 1
    const currentPage = data?.currentPage ?? 0
    const totalElements = data?.totalElements ?? 0

    const hasActiveFilters =
        !!searchFilters.city ||
        !!searchFilters.minRating ||
        !!searchFilters.maxPrice ||
        searchFilters.sortBy !== 'rating'

    function handleCityChange(city: string) {
        setSearchFilters({ city })
        setSearchParams(city ? { city } : {})
    }



    return (
       
    <div className="min-h-screen pt-16">
      {/* ── Top bar ── */}
      <div className="sticky top-16 z-20 bg-[#080C14]/95 backdrop-blur-xl border-b border-white/5">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 h-14 flex items-center gap-3">
          {/* City search */}
          <div className="relative flex-1 max-w-xs">
            <MapPin className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500" />
            <input
              type="text"
              value={searchFilters.city ?? ''}
              onChange={(e) => handleCityChange(e.target.value)}
              placeholder="Pretraži po gradu..."
              className="w-full pl-9 pr-4 py-2 rounded-xl bg-white/5 border border-white/8 text-sm text-white placeholder:text-slate-500 focus:outline-none focus:border-rose-500/50 focus:ring-1 focus:ring-rose-500/20 transition-all"
            />
          </div>
 
          {/* Sort */}
          <select
            value={searchFilters.sortBy ?? 'rating'}
            onChange={(e) => setSearchFilters({ sortBy: e.target.value as SalonSearchParams['sortBy'] })}
            className="hidden sm:block px-3 py-2 rounded-xl bg-white/5 border border-white/8 text-sm text-slate-300 focus:outline-none cursor-pointer"
          >
            {SORT_OPTIONS.map((o) => (
              <option key={o.value} value={o.value} className="bg-[#0F1623]">{o.label}</option>
            ))}
          </select>
 
          {/* Filter toggle */}
          <button
            onClick={() => setShowFilters(!showFilters)}
            className="flex items-center gap-2 px-3 py-2 rounded-xl bg-white/5 border border-white/8 text-sm text-slate-300 hover:text-white hover:border-white/15 transition-colors"
          >
            <SlidersHorizontal className="w-4 h-4" />
            <span className="hidden sm:inline">Filteri</span>
            {hasActiveFilters && (
              <span className="w-2 h-2 rounded-full bg-rose-500" />
            )}
          </button>
 
          {hasActiveFilters && (
            <button
              onClick={resetFilters}
              className="flex items-center gap-1.5 text-xs text-slate-500 hover:text-rose-400 transition-colors"
            >
              <X className="w-3.5 h-3.5" />
              Resetuj
            </button>
          )}
 
          {/* Results count */}
          <span className="ml-auto text-xs text-slate-500 shrink-0">
            {isFetching ? 'Učitavam...' : `${totalElements} salona`}
          </span>
        </div>
 
        {/* Filter panel */}
        <AnimatePresence>
          {showFilters && (
            <motion.div
              initial={{ height: 0, opacity: 0 }}
              animate={{ height: 'auto', opacity: 1 }}
              exit={{ height: 0, opacity: 0 }}
              transition={{ duration: 0.2 }}
              className="overflow-hidden border-t border-white/5"
            >
              <div className="max-w-7xl mx-auto px-4 sm:px-6 py-4 flex flex-wrap gap-4 items-center">
                {/* Cities quick select */}
                <div className="flex flex-wrap gap-2">
                  {CITIES.map((c) => (
                    <button
                      key={c}
                      onClick={() => handleCityChange(searchFilters.city === c ? '' : c)}
                      className={`px-3 py-1.5 rounded-lg text-xs font-medium transition-colors ${
                        searchFilters.city === c
                          ? 'bg-rose-500 text-white'
                          : 'bg-white/5 text-slate-400 hover:text-white border border-white/8'
                      }`}
                    >
                      {c}
                    </button>
                  ))}
                </div>
 
                <div className="w-px h-6 bg-white/10" />
 
                {/* Min rating */}
                <div className="flex items-center gap-2">
                  <span className="text-xs text-slate-500">Min. ocjena:</span>
                  {[0, 3, 4, 4.5].map((r) => (
                    <button
                      key={r}
                      onClick={() => setSearchFilters({ minRating: r || undefined })}
                      className={`px-2.5 py-1 rounded-lg text-xs font-medium transition-colors ${
                        (searchFilters.minRating ?? 0) === r
                          ? 'bg-amber-500 text-white'
                          : 'bg-white/5 text-slate-400 hover:text-white border border-white/8'
                      }`}
                    >
                      {r === 0 ? 'Sve' : `${r}+★`}
                    </button>
                  ))}
                </div>
 
                <div className="w-px h-6 bg-white/10" />
 
                {/* Verified only */}
                <label className="flex items-center gap-2 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={!!searchFilters.verified}
                    onChange={(e) => setSearchFilters({ verified: e.target.checked || undefined })}
                    className="w-4 h-4 rounded accent-rose-500"
                  />
                  <span className="text-xs text-slate-400">Samo verifikovani</span>
                </label>
              </div>
            </motion.div>
          )}
        </AnimatePresence>
      </div>
 
      {/* ── Main content ── */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 py-8">
 
        {/* Empty state */}
        {!isLoading && salons.length === 0 && (
          <div className="text-center py-24">
            <div className="w-16 h-16 rounded-2xl bg-slate-800 flex items-center justify-center mx-auto mb-4">
              <MapPin className="w-8 h-8 text-slate-600" />
            </div>
            <h3 className="font-display text-xl font-semibold text-white mb-2">Nema rezultata</h3>
            <p className="text-slate-400 text-sm mb-5">Pokušaj promijeniti filtere ili pretraži drugi grad</p>
            <button
              onClick={resetFilters}
              className="px-5 py-2.5 rounded-xl bg-rose-500 text-white text-sm font-medium hover:bg-rose-600 transition-colors"
            >
              Resetuj filtere
            </button>
          </div>
        )}
 
        {/* Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-5">
          {isLoading
            ? Array.from({ length: 8 }).map((_, i) => <SalonCardSkeleton key={i} />)
            : salons.map((salon, i) => (
                <motion.div
                  key={salon.id}
                  initial={{ opacity: 0, y: 16 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: i * 0.04, duration: 0.3 }}
                >
                  <SalonCard salon={salon} />
                </motion.div>
              ))}
        </div>
 
        {/* Pagination */}
        {totalPages > 1 && (
          <div className="flex items-center justify-center gap-2 mt-10">
            <button
              onClick={() => setSearchFilters({ page: currentPage - 1 })}
              disabled={currentPage === 0}
              className="w-9 h-9 rounded-xl flex items-center justify-center bg-white/5 border border-white/8 text-slate-400 hover:text-white disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
            >
              <ChevronLeft className="w-4 h-4" />
            </button>
 
            {Array.from({ length: totalPages }).map((_, i) => (
              <button
                key={i}
                onClick={() => setSearchFilters({ page: i })}
                className={`w-9 h-9 rounded-xl text-sm font-medium transition-colors ${
                  i === currentPage
                    ? 'bg-rose-500 text-white'
                    : 'bg-white/5 border border-white/8 text-slate-400 hover:text-white'
                }`}
              >
                {i + 1}
              </button>
            ))}
 
            <button
              onClick={() => setSearchFilters({ page: currentPage + 1 })}
              disabled={currentPage >= totalPages - 1}
              className="w-9 h-9 rounded-xl flex items-center justify-center bg-white/5 border border-white/8 text-slate-400 hover:text-white disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
            >
              <ChevronRight className="w-4 h-4" />
            </button>
          </div>
        )}
      </div>
    </div>

    )
}