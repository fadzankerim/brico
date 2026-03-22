

export default function SalonCardSkeleton() {
    return (
        <div className="bg-white/5 rounded-2xl p-4 border border-white/5">
            <div className="h-48 bg-white/5 rounded-xl mb-4 animate-pulse" />
            <div className="h-6 bg-white/5 rounded-lg mb-2 animate-pulse" />
            <div className="h-4 bg-white/5 rounded-lg mb-4 animate-pulse" />
            <div className="flex items-center gap-2">
                <div className="h-8 w-8 rounded-full bg-white/5 animate-pulse" />
                <div className="h-8 w-8 rounded-full bg-white/5 animate-pulse" />
                <div className="h-8 w-8 rounded-full bg-white/5 animate-pulse" />
            </div>
        </div>
    )
}