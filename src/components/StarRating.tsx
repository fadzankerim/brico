import { useState } from "react"
import { cn } from "../lib/utils"
import { Star } from "lucide-react"


interface StarRatingProps{
    value: number
    onChange?: (value: number) => void
    readonly?: boolean
    size?: 'sm' | 'md' | 'lg'
    showLabel?: boolean
}

const LABELS = ['', 'Loše', 'Ispod prosjeka', 'Dobro', 'Odlično', 'Izvrsno']

const SIZES = {
    sm: 'w-3.5 h-3.5',
  md: 'w-5 h-5',
  lg: 'w-7 h-7',
}

export default function StarRating({ value, onChange, readonly, size = 'md', showLabel = false }: StarRatingProps){

    const [hovered, setHovered] = useState(0);
    
    const active = hovered || value;

     return (
    <div className="flex items-center gap-2">
      <div
        className={cn('flex items-center gap-0.5', !readonly && 'cursor-pointer')}
        onMouseLeave={() => !readonly && setHovered(0)}
      >
        {[1, 2, 3, 4, 5].map((star) => (
          <button
            key={star}
            type="button"
            disabled={readonly}
            onClick={() => onChange?.(star)}
            onMouseEnter={() => !readonly && setHovered(star)}
            className={cn('transition-transform', !readonly && 'hover:scale-110')}
          >
            <Star
              className={cn(
                SIZES[size],
                'transition-colors',
                star <= active
                  ? 'text-amber-400 fill-amber-400'
                  : 'text-slate-700 fill-slate-700'
              )}
            />
          </button>
        ))}
      </div>
      {showLabel && active > 0 && (
        <span className="text-sm font-medium text-amber-400">{LABELS[active]}</span>
      )}
    </div>
  )

}