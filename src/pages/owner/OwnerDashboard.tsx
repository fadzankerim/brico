



type Tab = 'overview' | 'calendar' | 'staff' | 'services' | 'analytics'

const MOCK_REVENUE = [
  { month: 'Jan', revenue: 2400 }, { month: 'Feb', revenue: 3100 },
  { month: 'Mar', revenue: 2800 }, { month: 'Apr', revenue: 3600 },
  { month: 'Maj', revenue: 4200 }, { month: 'Jun', revenue: 3900 },
]
const MOCK_PIE = [
  { name: 'Šišanje', value: 38, color: '#e94560' },
  { name: 'Bojenje',  value: 28, color: '#f97316' },
  { name: 'Tretman',  value: 18, color: '#3b82f6' },
  { name: 'Ostalo',   value: 16, color: '#6b7280' },
]


const OWNER_SALON_ID = 1 // replace with real id from auth context



const CustomTooltip = ({ active, payload, label }: any) => {
  if (!active || !payload?.length) return null
  return (
    <div className="bg-[#0F1623] border border-white/10 rounded-xl p-3 text-sm shadow-xl">
      <p className="text-slate-400 mb-1">{label}</p>
      <p className="font-semibold text-white">€{payload[0].value.toLocaleString()}</p>
    </div>
  )
}

export default function OwnerDashboard(){

    return (
        <div>
            <h1>Owner Dashboard</h1>
        </div>
    )

}