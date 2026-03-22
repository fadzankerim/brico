

export default function ProfileSkeleton() {

    return (
        <div className="min-h-screen pt-16 animate-pulse">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 py-8">
                <div className="h-72 rounded-2xl bg-slate-800 mb-6" />
                <div className="h-6 bg-slate-800 rounded-lg w-1/3 mb-3" />
                <div className="h-4 bg-slate-800 rounded-lg w-1/2 mb-3" />
                <div className="h-4 bg-slate-800 rounded-lg w-1/4" />
            </div>
        </div>
    )

}