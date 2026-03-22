import { Calendar } from "lucide-react";
import { Link } from "react-router-dom";

export default function EmptyState() {
  return (
    <div className="text-center py-16">
      <div className="w-14 h-14 rounded-2xl bg-slate-800 flex items-center justify-center mx-auto mb-4">
        <Calendar className="w-7 h-7 text-slate-600" />
      </div>
      <h3 className="font-display font-semibold text-white mb-1">Nema termina</h3>
      <p className="text-slate-400 text-sm mb-5">Rezervišite vaš prvi termin</p>
      <Link
        to="/salons"
        className="inline-flex px-5 py-2.5 rounded-xl bg-rose-500 text-white text-sm font-medium hover:bg-rose-600 transition-colors"
      >
        Pronađi Salon
      </Link>
    </div>
  )
}