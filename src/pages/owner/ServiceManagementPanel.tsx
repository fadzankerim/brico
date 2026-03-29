import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { useState } from "react"
import { salonService } from "../../services/salon.service"
import type { Service } from "../../types/salon.typs"
import { toast } from "sonner"
import { motion } from "motion/react"
import { Clock, Pencil, Plus, Scissors, Trash2 } from "lucide-react"
import { formatDuration } from "date-fns"
import { formatPrice } from "../../utils/dateUtils"
import { cn } from "../../lib/utils"
import ServiceFormModal from "../../components/ServiceFormModal"



interface Props {
  salonId: number
}


export default function ServiceManagementPanel({ salonId }: Props) {

  const [modalOpen, setModalOpen] = useState(false)
  const [editTarget, setEditTarget] = useState<Service | null>(null)
  const [deleteId, setDeleteId] = useState<number | null>(null)
  const queryClient = useQueryClient()
  const servicesKey = ['salon', salonId, 'services']

  const { data: services = [], isLoading } = useQuery({
    queryKey: servicesKey,
    queryFn: () => salonService.getServices(salonId),
  })

  const remove = useMutation({
    mutationFn: (id: number) => salonService.deleteService(salonId, id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: servicesKey })
      toast.success('Usluga uklonjena')
      setDeleteId(null)
    },
    onError: () => toast.error('Greška pri brisanju usluge'),
  })
  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h2 className="font-display text-lg font-semibold text-white">Upravljanje Uslugama</h2>
          <p className="text-sm text-slate-400 mt-0.5">{services.length} aktivnih usluga</p>
        </div>
        <button
          onClick={() => { setEditTarget(null); setModalOpen(true) }}
          className="flex items-center gap-2 px-4 py-2 rounded-xl bg-rose-500 hover:bg-rose-600 text-white text-sm font-medium transition-colors"
        >
          <Plus className="w-4 h-4" /> Dodaj Uslugu
        </button>
      </div>

      {isLoading ? (
        <div className="space-y-3">
          {[1, 2, 3].map((i) => <div key={i} className="h-16 rounded-xl bg-slate-800 animate-pulse" />)}
        </div>
      ) : services.length === 0 ? (
        <div className="text-center py-14 bg-[#0F1623] rounded-2xl border border-white/5">
          <Scissors className="w-10 h-10 text-slate-700 mx-auto mb-3" />
          <p className="text-slate-400 text-sm">Nema usluga. Dodaj prvu uslugu.</p>
        </div>
      ) : (
        <div className="space-y-2">
          {services.map((s, i) => (
            <motion.div
              key={s.id}
              initial={{ opacity: 0, y: 6 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: i * 0.04 }}
              className="flex items-center gap-4 px-4 py-3.5 rounded-xl bg-[#0F1623] border border-white/5 hover:border-white/8 transition-colors"
            >
              <div className="w-8 h-8 rounded-lg bg-rose-500/10 flex items-center justify-center shrink-0">
                <Scissors className="w-4 h-4 text-rose-400" />
              </div>

              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium text-white truncate">{s.name}</p>
                <div className="flex items-center gap-3 mt-0.5 text-xs text-slate-500">
                  <span className="flex items-center gap-1"><Clock className="w-3 h-3" />{formatDuration({minutes: s.durationMinutes})}</span>
                  {s.description && <span className="truncate max-w-[200px]">{s.description}</span>}
                </div>
              </div>

              <div className="flex items-center gap-1 shrink-0">
                <span className={cn(
                  'text-xs px-2 py-0.5 rounded-full',
                  s.isActive ? 'bg-emerald-500/10 text-emerald-400' : 'bg-slate-500/10 text-slate-500'
                )}>
                  {s.isActive ? 'Aktivna' : 'Neaktivna'}
                </span>
              </div>

              <p className="font-semibold text-white text-sm shrink-0">{formatPrice(s.price)}</p>

              <div className="flex gap-1 shrink-0">
                <button
                  onClick={() => { setEditTarget(s); setModalOpen(true) }}
                  className="w-7 h-7 rounded-lg flex items-center justify-center text-slate-500 hover:text-white hover:bg-white/8 transition-colors"
                >
                  <Pencil className="w-3.5 h-3.5" />
                </button>

                {deleteId === s.id ? (
                  <div className="flex gap-1">
                    <button onClick={() => remove.mutate(s.id)} className="px-2 py-1 rounded-lg bg-rose-500/15 text-rose-400 text-xs hover:bg-rose-500 hover:text-white transition-colors">OK</button>
                    <button onClick={() => setDeleteId(null)} className="px-2 py-1 rounded-lg bg-white/5 text-slate-400 text-xs">✕</button>
                  </div>
                ) : (
                  <button
                    onClick={() => setDeleteId(s.id)}
                    className="w-7 h-7 rounded-lg flex items-center justify-center text-slate-500 hover:text-rose-400 hover:bg-rose-500/10 transition-colors"
                  >
                    <Trash2 className="w-3.5 h-3.5" />
                  </button>
                )}
              </div>
            </motion.div>
          ))}
        </div>
      )
    }

    <ServiceFormModal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        salonId={salonId}
        editTarget={editTarget}
        onSuccess={() => queryClient.invalidateQueries({ queryKey: servicesKey })}
      />
    </div>
  )
}