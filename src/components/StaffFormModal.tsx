import { useState } from "react"
import type { Hairdresser } from "../types/salon.typs"
import { useMutation } from "@tanstack/react-query"
import { salonService } from "../services/salon.service"
import { authService } from "../services/auth.service"
import { useAuthStore } from "../store/authStore"
import { toast } from "sonner"
import { AnimatePresence, motion } from "motion/react"
import { X, Eye, EyeOff } from "lucide-react"


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
  const { token } = useAuthStore()
  const [fullName,    setFullName]    = useState(editTarget?.fullName ?? '')
  const [bio,         setBio]         = useState(editTarget?.bio ?? '')
  const [specialties, setSpecialties] = useState(editTarget?.specialties ?? '')
  const [email,       setEmail]       = useState('')
  const [password,    setPassword]    = useState('')
  const [showPass,    setShowPass]    = useState(false)

  const save = useMutation({
    mutationFn: async () => {
      if (editTarget) {
        // Ažuriranje profila frizera
        await salonService.updateHairdresser(salonId, editTarget.id, {
          fullName, bio: bio || undefined, specialties: specialties || undefined,
        })
        // Ako frizer nema userId ali je unesen email → kreiraj user nalog i poveži
        if (!editTarget.userId && email.trim() && password.length >= 8) {
          const regRes = await authService.register({
            email, password, fullName, role: 'HAIRDRESSER' as any,
          })
          await salonService.updateHairdresser(salonId, editTarget.id, {
            fullName, bio: bio || undefined, specialties: specialties || undefined,
            userId: regRes.user.id,
          })
          return
        }

        // Ako frizer ima userId i uneseni su novi kredencijali → ažuriraj
        if (editTarget.userId && (email.trim() || password.length >= 8)) {
          await authService.updateCredentials(editTarget.userId, {
            email:    email.trim()   || undefined,
            password: password || undefined,
          })
        }
        return
      }

      // Kreiranje novog: 1) napravi user nalog, 2) napravi hairdresser sa userId
      const regRes = await authService.register({
        email,
        password,
        fullName,
        role: 'HAIRDRESSER' as any,
      })

      return salonService.addHairdresser(salonId, {
        fullName,
        bio:         bio || undefined,
        specialties: specialties || undefined,
        userId:      regRes.user.id,
      })
    },
    onSuccess: () => {
      toast.success(editTarget ? 'Frizer ažuriran' : 'Frizer dodat — može se prijaviti s unesenim emailom')
      onSuccess()
      onClose()
    },
    onError: (err: any) => {
      const msg = err?.response?.data?.message
      if (err?.response?.data?.error === 'conflict') {
        toast.error('Korisnik s ovim emailom već postoji.')
      } else {
        toast.error(msg ?? 'Greška pri čuvanju')
      }
    },
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

              {/* Email/password — uvijek prikazano */}
              {true && (
                <>
                  {editTarget && (
                    <p className="text-xs text-slate-500 bg-white/3 rounded-lg px-3 py-2">
                      {editTarget.userId
                        ? 'Ostavi prazno da zadržiš trenutni email / lozinku'
                        : 'Frizer nema login nalog — unesi email i lozinku da mu kreiraš pristup'}
                    </p>
                  )}
                  <div>
                    <label className="block text-xs font-medium text-slate-400 uppercase tracking-wider mb-1.5">
                      Email (za prijavu){!editTarget && ' *'}</label>
                    <input
                      type="email"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                      placeholder="frizer@salon.ba"
                      className="w-full px-3 py-2.5 rounded-xl bg-white/5 border border-white/8 text-white placeholder:text-slate-600 text-sm focus:outline-none focus:border-rose-500/50 transition-all"
                    />
                  </div>
                  <div>
                    <label className="block text-xs font-medium text-slate-400 uppercase tracking-wider mb-1.5">
                      Lozinka *
                    </label>
                    <div className="relative">
                      <input
                        type={showPass ? 'text' : 'password'}
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Min. 8 karaktera"
                        className="w-full px-3 py-2.5 pr-10 rounded-xl bg-white/5 border border-white/8 text-white placeholder:text-slate-600 text-sm focus:outline-none focus:border-rose-500/50 transition-all"
                      />
                      <button
                        type="button"
                        onClick={() => setShowPass(p => !p)}
                        className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-500 hover:text-white"
                      >
                        {showPass ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                      </button>
                    </div>
                  </div>
                </>
              )}

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
                disabled={
                  !fullName.trim() || save.isPending ||
                  // Novi frizer: email i password obavezni
                  (!editTarget && (!email.trim() || password.length < 8)) ||
                  // Postojeći bez userId: ako unosi email, mora i password
                  (!!editTarget && !editTarget.userId && !!email.trim() && password.length < 8)
                }
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