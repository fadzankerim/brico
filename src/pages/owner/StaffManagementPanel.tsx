import { useState } from "react"
import type { Hairdresser } from "../../types/salon.typs"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { salonService } from "../../services/salon.service"
import { toast } from "sonner"
import { AlertCircle, CheckCircle, Pencil, Plus, Star, Trash2, User } from "lucide-react"
import { cn } from "../../lib/utils"
import StaffFormModal from "../../components/StaffFormModal"
import {motion} from "motion/react"


interface StaffManagementPanelProps{
    salonId: number
}

export default function StaffManagementPanel({salonId}: StaffManagementPanelProps){


    const [openModal, setOpenModal] = useState(false)
    const [editTarget , setEditTarget] = useState<Hairdresser | null>(null)
    const [deleteId, setDeleteId] = useState<number | null>(null)
    const queryClient = useQueryClient()

    const staffKey = ['salon', salonId, 'hairdressers']

    const {data: staff = [], isLoading} = useQuery({
        queryKey: staffKey,
        queryFn: () => salonService.getHairdressers(salonId)

    })

    const toggleActive = useMutation({
        mutationFn: (h: Hairdresser) => salonService.updateHairdresser(salonId, h.id, {isActive: !h.isActive}),
        onSuccess: () => queryClient.invalidateQueries({queryKey: staffKey}),
        onError: () => toast.error("Greška pri ažuriranju statusa")
    })

    const remove = useMutation({
        mutationFn: (id: number) => salonService.removeHairdresser(salonId, id),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: staffKey})
            toast.success("Frizer uklonjen iz salona!")
            setDeleteId(null)
        },
        onError: () => {
            toast.error("Greška pri uklanjanju frizera!")
            setDeleteId(null)
        }
    })

    function openAdd(){
        setEditTarget(null)
        setOpenModal(true)
    }

    function openEdit(h: Hairdresser){
        setEditTarget(h)
        setOpenModal(true)
    }

    return(
        <div>
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h2 className="font-display text-lg font-semibold text-white">Upravljanje Osobljem</h2>
          <p className="text-sm text-slate-400 mt-0.5">{staff.length} frizera u salonu</p>
        </div>
        <button
          onClick={openAdd}
          className="flex items-center gap-2 px-4 py-2 rounded-xl bg-rose-500 hover:bg-rose-600 text-white text-sm font-medium transition-colors"
        >
          <Plus className="w-4 h-4" /> Dodaj Frizera
        </button>
      </div>

      {/* Staff list */}
      {isLoading ? (
        <div className="space-y-3">
          {[1, 2, 3].map((i) => (
            <div key={i} className="h-20 rounded-2xl bg-slate-800 animate-pulse" />
          ))}
        </div>
      ) : staff.length === 0 ? (
        <div className="text-center py-14 bg-[#0F1623] rounded-2xl border border-white/5">
          <User className="w-10 h-10 text-slate-700 mx-auto mb-3" />
          <p className="text-slate-400 text-sm">Nema frizera u salonu</p>
          <button onClick={openAdd} className="mt-4 text-rose-400 text-sm hover:text-rose-300 transition-colors">
            + Dodaj prvog frizera
          </button>
        </div>
      ) : (
        <div className="space-y-3">
          {staff.map((h, i) => (
            <motion.div
              key={h.id}
              initial={{ opacity: 0, y: 8 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: i * 0.05 }}
              className="flex items-center gap-4 p-4 rounded-2xl bg-[#0F1623] border border-white/5 hover:border-white/8 transition-colors"
            >
              {/* Avatar */}
              <div className={cn(
                'w-11 h-11 rounded-xl flex items-center justify-center text-white font-bold text-sm shrink-0 bg-gradient-to-br',
                h.isActive ? 'from-rose-500 to-rose-700' : 'from-slate-600 to-slate-700'
              )}>
                {h.fullName.split(' ').map((n) => n[0]).join('').slice(0, 2)}
              </div>

              {/* Info */}
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2 flex-wrap">
                  <p className="font-medium text-white truncate">{h.fullName}</p>
                  <span className={cn(
                    'inline-flex items-center gap-1 text-xs px-2 py-0.5 rounded-full',
                    h.isActive
                      ? 'bg-emerald-500/10 text-emerald-400'
                      : 'bg-slate-500/10 text-slate-500'
                  )}>
                    {h.isActive ? <CheckCircle className="w-3 h-3" /> : <AlertCircle className="w-3 h-3" />}
                    {h.isActive ? 'Aktivan' : 'Neaktivan'}
                  </span>
                </div>
                {h.specialties && (
                  <div className="flex flex-wrap gap-1 mt-1">
                    {h.specialties.split(',').slice(0, 3).map((s) => (
                      <span key={s} className="text-xs px-2 py-0.5 rounded-full bg-white/5 text-slate-400">
                        {s.trim()}
                      </span>
                    ))}
                  </div>
                )}
              </div>

              {/* Stats */}
              <div className="hidden sm:flex items-center gap-4 text-sm shrink-0">
                {h.avgRating && (
                  <div className="flex items-center gap-1 text-amber-400">
                    <Star className="w-3.5 h-3.5 fill-current" />
                    <span className="text-white font-medium">{h.avgRating.toFixed(1)}</span>
                  </div>
                )}
              </div>

              {/* Actions */}
              <div className="flex items-center gap-1 shrink-0">
                {/* Toggle active */}
                <button
                  onClick={() => toggleActive.mutate(h)}
                  disabled={toggleActive.isPending}
                  title={h.isActive ? 'Deaktiviraj' : 'Aktiviraj'}
                  className={cn(
                    'w-8 h-8 rounded-lg flex items-center justify-center text-xs transition-colors',
                    h.isActive
                      ? 'text-slate-500 hover:text-amber-400 hover:bg-amber-500/10'
                      : 'text-slate-600 hover:text-emerald-400 hover:bg-emerald-500/10'
                  )}
                >
                  {h.isActive ? '●' : '○'}
                </button>

                <button
                  onClick={() => openEdit(h)}
                  className="w-8 h-8 rounded-lg flex items-center justify-center text-slate-500 hover:text-white hover:bg-white/8 transition-colors"
                >
                  <Pencil className="w-3.5 h-3.5" />
                </button>

                {deleteId === h.id ? (
                  <div className="flex gap-1">
                    <button
                      onClick={() => remove.mutate(h.id)}
                      className="px-2 py-1.5 rounded-lg bg-rose-500/15 text-rose-400 text-xs hover:bg-rose-500 hover:text-white transition-colors"
                    >
                      Potvrdi
                    </button>
                    <button
                      onClick={() => setDeleteId(null)}
                      className="px-2 py-1.5 rounded-lg bg-white/5 text-slate-400 text-xs hover:text-white transition-colors"
                    >
                      ✕
                    </button>
                  </div>
                ) : (
                  <button
                    onClick={() => setDeleteId(h.id)}
                    className="w-8 h-8 rounded-lg flex items-center justify-center text-slate-500 hover:text-rose-400 hover:bg-rose-500/10 transition-colors"
                  >
                    <Trash2 className="w-3.5 h-3.5" />
                  </button>
                )}
              </div>
            </motion.div>
          ))}
        </div>
      )}

      {/* Add/Edit modal */}
      <StaffFormModal
        open={openModal}
        onClose={() => setOpenModal(false)}
        salonId={salonId}
        editTarget={editTarget}
        onSuccess={() => queryClient.invalidateQueries({ queryKey: staffKey })}
      />
    </div>
    )
}