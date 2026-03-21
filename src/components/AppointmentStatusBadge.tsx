import { cn } from "../lib/utils";
import type { AppointmentStatus } from "../types/booking.types";

const CONFIG : Record<AppointmentStatus, {label: string, className: string}> = {

    PENDING: {label: 'Čeka na potvrdu', className: 'bg-amber-500/10 text-amber-400 border-amber-500/20'},
    CONFIRMED: {label: 'Potvrđeno', className: 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20'},
    COMPLETED: {label: 'Završeno', className: 'bg-slate-500/10 text-slate-400 border-slate-500/20'},
    CANCELLED: {label: 'Otkazano', className: 'bg-rose-500/10 text-rose-400 border-rose-500/20'},
    NO_SHOW: {label: 'Nije se pojavio', className: 'bg-rose-500/10 text-rose-400 border-rose-500/20'},

}



export default function AppointmentStatusBadge({ status }: { status: AppointmentStatus }) {

    const { label, className } = CONFIG[status] || CONFIG.PENDING;

    return (
        <span className={cn('inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border', className)}>
            {label}
        </span>
    )

}
    

