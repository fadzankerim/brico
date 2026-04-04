import { useState } from 'react'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { motion, AnimatePresence } from 'motion/react'
import { Plus, Trash2, Star, ImageOff, Link as LinkIcon, X, Loader2 } from 'lucide-react'
import { toast } from 'sonner'
import { salonService } from '../services/salon.service'
import { salonKeys } from '../hooks/useSalons'
import type { SalonPhoto } from '../types/salon.typs'
import { cn } from '../lib/utils'

interface Props {
  salonId: number
  photos: SalonPhoto[]
}

export default function SalonPhotosPanel({ salonId, photos }: Props) {
  const [urlInput, setUrlInput]   = useState('')
  const [addOpen, setAddOpen]     = useState(false)
  const [urlError, setUrlError]   = useState('')
  const queryClient               = useQueryClient()

  const invalidate = () => {
    queryClient.invalidateQueries({ queryKey: salonKeys.mine(undefined) })
    queryClient.invalidateQueries({ queryKey: ['salons', 'mine'] })
    queryClient.invalidateQueries({ queryKey: salonKeys.detail(salonId) })
  }

  const addMutation = useMutation({
    mutationFn: ({ url, isPrimary }: { url: string; isPrimary: boolean }) =>
      salonService.addPhoto(salonId, url, isPrimary),
    onSuccess: () => {
      invalidate()
      setUrlInput('')
      setAddOpen(false)
      toast.success('Fotografija dodana')
    },
    onError: () => toast.error('Greška pri dodavanju fotografije'),
  })

  const primaryMutation = useMutation({
    mutationFn: (photoId: number) => salonService.setPrimaryPhoto(salonId, photoId),
    onSuccess: () => { invalidate(); toast.success('Naslovna fotografija ažurirana') },
  })

  const deleteMutation = useMutation({
    mutationFn: (photoId: number) => salonService.deletePhoto(salonId, photoId),
    onSuccess: () => { invalidate(); toast.success('Fotografija uklonjena') },
  })

  const handleAdd = () => {
    setUrlError('')
    const trimmed = urlInput.trim()
    if (!trimmed) { setUrlError('URL je obavezan'); return }
    try { new URL(trimmed) } catch { setUrlError('Unesite validan URL'); return }
    addMutation.mutate({ url: trimmed, isPrimary: photos.length === 0 })
  }

  return (
    <div className="space-y-4">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h3 className="font-semibold text-white">Fotografije salona</h3>
          <p className="text-xs text-slate-500 mt-0.5">{photos.length} fotografija · prva je naslovna</p>
        </div>
        <button
          onClick={() => setAddOpen(true)}
          className="flex items-center gap-1.5 px-3 py-2 rounded-xl bg-rose-500/10 border border-rose-500/20 text-rose-400 hover:bg-rose-500/20 text-sm font-medium transition-all"
        >
          <Plus className="w-4 h-4" /> Dodaj foto
        </button>
      </div>

      {/* Add photo drawer */}
      <AnimatePresence>
        {addOpen && (
          <motion.div
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: 'auto' }}
            exit={{ opacity: 0, height: 0 }}
            className="overflow-hidden"
          >
            <div className="bg-white/3 border border-white/8 rounded-xl p-4 space-y-3">
              <div className="flex items-center justify-between">
                <p className="text-sm font-medium text-white">Dodaj fotografiju putem URL-a</p>
                <button onClick={() => { setAddOpen(false); setUrlError('') }} className="text-slate-500 hover:text-white">
                  <X className="w-4 h-4" />
                </button>
              </div>
              <div className="flex gap-2">
                <div className="flex-1 relative">
                  <LinkIcon className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500" />
                  <input
                    value={urlInput}
                    onChange={e => { setUrlInput(e.target.value); setUrlError('') }}
                    onKeyDown={e => e.key === 'Enter' && handleAdd()}
                    placeholder="https://primjer.com/slika.jpg"
                    className="w-full pl-9 pr-3 py-2.5 rounded-xl bg-white/5 border border-white/10 text-white placeholder:text-slate-600 text-sm focus:outline-none focus:border-rose-500/50 transition-all"
                  />
                </div>
                <button
                  onClick={handleAdd}
                  disabled={addMutation.isPending}
                  className="px-4 py-2.5 rounded-xl bg-rose-500 hover:bg-rose-600 disabled:opacity-50 text-white text-sm font-semibold transition-colors flex items-center gap-1.5"
                >
                  {addMutation.isPending ? <Loader2 className="w-4 h-4 animate-spin" /> : <Plus className="w-4 h-4" />}
                  Dodaj
                </button>
              </div>
              {urlError && <p className="text-rose-400 text-xs">{urlError}</p>}
              <p className="text-xs text-slate-600">Preporučeno: Unsplash ili direktni link na fotografiju (.jpg, .webp)</p>
            </div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Photo grid */}
      {photos.length === 0 ? (
        <div className="flex flex-col items-center justify-center py-12 bg-white/2 rounded-2xl border border-dashed border-white/10">
          <ImageOff className="w-10 h-10 text-slate-600 mb-3" />
          <p className="text-slate-500 text-sm">Nema fotografija</p>
          <p className="text-slate-600 text-xs mt-1">Dodajte fotografiju da privučete klijente</p>
          <button
            onClick={() => setAddOpen(true)}
            className="mt-4 flex items-center gap-1.5 px-4 py-2 rounded-xl bg-rose-500/10 border border-rose-500/20 text-rose-400 hover:bg-rose-500/20 text-sm font-medium transition-all"
          >
            <Plus className="w-4 h-4" /> Dodaj prvu fotografiju
          </button>
        </div>
      ) : (
        <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
          <AnimatePresence>
            {photos.map(photo => (
              <motion.div
                key={photo.id}
                initial={{ opacity: 0, scale: 0.9 }}
                animate={{ opacity: 1, scale: 1 }}
                exit={{ opacity: 0, scale: 0.9 }}
                className="relative group aspect-square rounded-xl overflow-hidden bg-white/5"
              >
                <img
                  src={photo.url}
                  alt="Salon"
                  className="w-full h-full object-cover"
                  onError={e => {
                    (e.target as HTMLImageElement).src = 'https://images.unsplash.com/photo-1560066984-138dadb4c035?w=400'
                  }}
                />

                {/* Primary badge */}
                {photo.isPrimary && (
                  <div className="absolute top-2 left-2 flex items-center gap-1 bg-amber-500 text-white text-[10px] font-bold px-2 py-0.5 rounded-full">
                    <Star className="w-2.5 h-2.5" /> Naslovna
                  </div>
                )}

                {/* Overlay actions */}
                <div className={cn(
                  'absolute inset-0 bg-black/50 flex items-center justify-center gap-2 transition-opacity',
                  'opacity-0 group-hover:opacity-100'
                )}>
                  {!photo.isPrimary && (
                    <button
                      onClick={() => primaryMutation.mutate(photo.id)}
                      disabled={primaryMutation.isPending}
                      title="Postavi kao naslovnu"
                      className="w-8 h-8 rounded-lg bg-amber-500/20 border border-amber-500/40 text-amber-400 hover:bg-amber-500/30 flex items-center justify-center transition-colors"
                    >
                      {primaryMutation.isPending ? <Loader2 className="w-3.5 h-3.5 animate-spin" /> : <Star className="w-3.5 h-3.5" />}
                    </button>
                  )}
                  <button
                    onClick={() => {
                      if (window.confirm('Ukloniti fotografiju?')) deleteMutation.mutate(photo.id)
                    }}
                    disabled={deleteMutation.isPending}
                    title="Ukloni fotografiju"
                    className="w-8 h-8 rounded-lg bg-rose-500/20 border border-rose-500/40 text-rose-400 hover:bg-rose-500/30 flex items-center justify-center transition-colors"
                  >
                    {deleteMutation.isPending ? <Loader2 className="w-3.5 h-3.5 animate-spin" /> : <Trash2 className="w-3.5 h-3.5" />}
                  </button>
                </div>
              </motion.div>
            ))}
          </AnimatePresence>
        </div>
      )}
    </div>
  )
}
