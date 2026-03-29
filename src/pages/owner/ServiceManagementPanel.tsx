import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { useState } from "react"
import { salonService } from "../../services/salon.service"
import type { Service } from "../../types/salon.typs"
import { toast } from "sonner"


interface Props{
    salonId: number
}


export default function ServiceManagementPanel({salonId}: Props){

    const [modalOpen, setModalOpen]   = useState(false)
  const [editTarget, setEditTarget] = useState<Service | null>(null)
  const [deleteId,   setDeleteId]   = useState<number | null>(null)
  const queryClient = useQueryClient()
  const servicesKey = ['salon', salonId, 'services']

  //const { data: services = [], isLoading } = useQuery({
  //  queryKey: servicesKey,
  //  queryFn:  () => salonService.getServices(salonId),
  //})

  const remove = useMutation({
    mutationFn: (id: number) => salonService.deleteService(salonId, id),
    onSuccess:  () => {
      queryClient.invalidateQueries({ queryKey: servicesKey })
      toast.success('Usluga uklonjena')
      setDeleteId(null)
    },
    onError: () => toast.error('Greška pri brisanju usluge'),
  })
    return(
        <div>
            <h1>Service Management Panel</h1>
        </div>
    )
}