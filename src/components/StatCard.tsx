import { TrendingDown, TrendingUp } from "lucide-react";
import { cn } from "../lib/utils";

interface StatCardProps{
    label:string;
    value: string;
    icon: React.ElementType;
    change?: {value:number, label?: string};
    accent?: 'rose' | 'emerald' | 'blue' | 'amber'
  className?: string
}
 
const ACCENT = {
  rose:    { icon: 'bg-rose-500/15 text-rose-400',    border: 'border-rose-500/10' },
  emerald: { icon: 'bg-emerald-500/15 text-emerald-400', border: 'border-emerald-500/10' },
  blue:    { icon: 'bg-blue-500/15 text-blue-400',    border: 'border-blue-500/10' },
  amber:   { icon: 'bg-amber-500/15 text-amber-400',  border: 'border-amber-500/10' },
}

export default function StatCard({label, value, icon: Icon, change, accent = 'rose', className}: StatCardProps){


    const a = ACCENT[accent];

    const positive = change?.value ?? 0 >= 0;

    
  return (
    <div
      className={cn(
        'rounded-2xl p-5 bg-[#0F1623] border border-white/5',
        a.border,
        className
      )}
    >
      <div className="flex items-start justify-between mb-4">
        <div className={cn('w-10 h-10 rounded-xl flex items-center justify-center', a.icon)}>
          <Icon className="w-5 h-5" />
        </div>
        {change !== undefined && (
          <div
            className={cn(
              'flex items-center gap-1 text-xs font-semibold px-2 py-1 rounded-lg',
              positive
                ? 'bg-emerald-500/10 text-emerald-400'
                : 'bg-rose-500/10 text-rose-400'
            )}
          >
            {positive ? <TrendingUp className="w-3 h-3" /> : <TrendingDown className="w-3 h-3" />}
            {Math.abs(change.value)}%
          </div>
        )}
      </div>
 
      <div>
        <div className="text-2xl font-display font-bold text-white mb-1">{value}</div>
        <div className="text-sm text-slate-400">{label}</div>
        {change?.label && (
          <div className="text-xs text-slate-600 mt-0.5">{change.label}</div>
        )}
      </div>
    </div>
  )
}