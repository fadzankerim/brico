import { useState } from "react"
import type { Hairdresser } from "../types/salon.typs"
import { useMutation } from "@tanstack/react-query"
import { salonService } from "../services/salon.service"
import { toast } from "sonner"
import { AnimatePresence, motion } from "motion/react"
import { X } from "lucide-react"


export default function StaffFormModal({
    open,
    onClose,
    salonId,
    editTarget,
    onSuccess,
}:{
    open: boolean
    onClose: () => void
    salonId: number
    editTarget: Hairdresser | null
    onSuccess: () => void
}){
    const [fullName,    setFullName]    = useState(editTarget?.fullName ?? '')
  const [bio,         setBio]         = useState(editTarget?.bio ?? '')
  const [specialties, setSpecialties] = useState(editTarget?.specialties ?? '')

  // Sync fields when editTarget changes
  useState(() => {
    setFullName(editTarget?.fullName ?? '')
    setBio(editTarget?.bio ?? '')
    setSpecialties(editTarget?.specialties ?? '')
  })

  const save = useMutation({
    mutationFn: () => {
      const data = { fullName, bio: bio || undefined, specialties: specialties || undefined }
      return editTarget
        ? salonService.updateHairdresser(salonId, editTarget.id, data)
        : salonService.addHairdresser(salonId, data)
    },
    onSuccess: () => {
      toast.success(editTarget ? 'Frizer ažuriran' : 'Frizer dodat u salon')
      onSuccess()
      onClose()
    },
    onError: () => toast.error('Greška pri čuvanju'),
  })

  return(
    <AnimatePresence>
        {open && (
        <>
          <motion.div
            initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
            onClick={onClose}
            className="fixed inset-0 bg-black/60 backdrop-blur-sm z-40"
          />
          <motion.div
            initial={{ opacity: 0, y: 30, scale: 0.97 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: 20, scale: 0.97 }}
            transition={{ type: 'spring', damping: 28, stiffness: 280 }}
            className="fixed z-50 inset-x-4 sm:inset-x-auto sm:left-1/2 sm:-translate-x-1/2 top-1/2 -translate-y-1/2 sm:w-[420px] bg-[#0F1623] rounded-2xl border border-white/10 shadow-2xl shadow-black/60"
          >
            <div className="flex items-center justify-between px-5 py-4 border-b border-white/5">
              <h3 className="font-display font-semibold text-white">
                {editTarget ? 'Uredi Frizera' : 'Dodaj Frizera'}
              </h3>
              <button onClick={onClose} className="w-8 h-8 rounded-xl flex items-center justify-center text-slate-500 hover:text-white hover:bg-white/8 transition-colors">
                <X className="w-4 h-4" />
              </button>
            </div>

            <div className="p-5 space-y-4">
              <div>
                <label className="block text-xs font-medium text-slate-400 uppercase tracking-wider mb-1.5">
                  Ime i prezime *
                </label>
                <input
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                  placeholder="npr. Amina Hodžić"
                  className="w-full px-3 py-2.5 rounded-xl bg-white/5 border border-white/8 text-white placeholder:text-slate-600 text-sm focus:outline-none focus:border-rose-500/50 transition-all"
                />
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-400 uppercase tracking-wider mb-1.5">
                  Specijalnosti
                </label>
                <input
                  value={specialties}
                  onChange={(e) => setSpecialties(e.target.value)}
                  placeholder="npr. Balayage, Šišanje, Bojenje (odvojeno zarezom)"
                  className="w-full px-3 py-2.5 rounded-xl bg-white/5 border border-white/8 text-white placeholder:text-slate-600 text-sm focus:outline-none focus:border-rose-500/50 transition-all"
                />
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-400 uppercase tracking-wider mb-1.5">
                  Biografija
                </label>
                <textarea
                  value={bio}
                  onChange={(e) => setBio(e.target.value)}
                  placeholder="Kratki opis frizera..."
                  rows={3}
                  className="w-full px-3 py-2.5 rounded-xl bg-white/5 border border-white/8 text-white placeholder:text-slate-600 text-sm focus:outline-none focus:border-rose-500/50 transition-all resize-none"
                />
              </div>
            </div>

            <div className="px-5 pb-5 flex gap-3">
              <button onClick={onClose} className="flex-1 py-2.5 rounded-xl bg-white/5 text-slate-300 text-sm hover:text-white hover:bg-white/8 transition-colors">
                Odustani
              </button>
              <button
                onClick={() => save.mutate()}
                disabled={!fullName.trim() || save.isPending}
                className="flex-1 py-2.5 rounded-xl bg-rose-500 hover:bg-rose-600 disabled:opacity-40 text-white text-sm font-semibold transition-colors flex items-center justify-center gap-2"
              >
                {save.isPending
                  ? <><span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />Čuvam...</>
                  : editTarget ? 'Spremi Izmjene' : 'Dodaj Frizera'}
              </button>
            </div>
          </motion.div>
        </>
      )}
    </AnimatePresence>
  )

}