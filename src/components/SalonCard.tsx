import { useAuthStore } from "../store/authStore";
import type { Salon } from "../types/salon.typs";


interface SalonCardProps{
    salon: Salon
    className?: string
}

export default function SalonCard( {salon : className} : SalonCardProps){

    const { isAuthenticated } = useAuthStore()
}