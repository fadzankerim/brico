import { useState } from "react"
import type { Hairdresser } from "../../types/salon.typs"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { salonService } from "../../services/salon.service"
import { toast } from "sonner"


interface StaffManagementPanelProps{
    salonId: number
}

export default function StaffManagementPanel({salonId}: StaffManagementPanelProps){


    const [openModal, setOpenModal] = useState(false)
    const [editTarget , setEditTarget] = useState<Hairdresser | null>(null)
    const [deleteId, setDeleteId] = useState<number | null>(null)
    const queryClient = useQueryClient()

    const staffKey = ['salon', salonId, 'hairdressers']

    const {data: staff} = useQuery({
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
            <h1>Staff Management Panel</h1>
        </div>
    )
}