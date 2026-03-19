import { useState } from 'react'
import { NavLink, Outlet, Link } from 'react-router-dom'
import { motion, AnimatePresence } from 'motion/react'
import {
  Scissors,
  LayoutDashboard,
  CalendarDays,
  Users,
  Sparkles,
  BarChart3,
  Settings,
  Heart,
  Menu,
  X,
  Bell,
  ChevronDown,
  LogOut,
} from 'lucide-react'
import { useAuthStore } from '../store/authStore'
import { useLogout } from '../hooks/useAuth'
import { cn } from '../lib/utils'


type NavItem = { label: string; href: string; icon: React.ElementType }

const OWNER_NAV: NavItem[] = [
  { label: 'Pregled', href: '/owner/dashboard', icon: LayoutDashboard },
  { label: 'Kalendar', href: '/owner/calendar', icon: CalendarDays },
  { label: 'Osoblje', href: '/owner/staff', icon: Users },
  { label: 'Usluge', href: '/owner/services', icon: Sparkles },
  { label: 'Analytics', href: '/owner/analytics', icon: BarChart3 },
  { label: 'Postavke', href: '/owner/settings', icon: Settings },
]

const HAIRDRESSER_NAV: NavItem[] = [
  { label: 'Pregled', href: '/hairdresser/dashboard', icon: LayoutDashboard },
  { label: 'Kalendar', href: '/hairdresser/calendar', icon: CalendarDays },
  { label: 'Zarada', href: '/hairdresser/revenue', icon: BarChart3 },
  { label: 'Postavke', href: '/hairdresser/settings', icon: Settings },
]

const CLIENT_NAV: NavItem[] = [
  { label: 'Termini', href: '/dashboard', icon: CalendarDays },
  { label: 'Omiljeni', href: '/favorites', icon: Heart },
  { label: 'Postavke', href: '/settings', icon: Settings },
]

export default function DashboardLayout() {
  const { user } = useAuthStore()
  const logout = useLogout()
  const [mobileOpen, setMobileOpen] = useState(false)

  const navItems =
    user?.role === 'SALON_OWNER'
      ? OWNER_NAV
      : user?.role === 'HAIRDRESSER'
        ? HAIRDRESSER_NAV
        : CLIENT_NAV

  const roleLabel =
    user?.role === 'SALON_OWNER'
      ? 'Vlasnik Salona'
      : user?.role === 'HAIRDRESSER'
        ? 'Frizer'
        : 'Klijent'

  const initials = user?.fullName
    ? user.fullName.split(' ').map((n) => n[0]).join('').slice(0, 2).toUpperCase()
    : 'U'

  const Sidebar = ({ mobile = false }: { mobile?: boolean }) => (
    <aside
      className={cn(
        'flex flex-col bg-[#060912] border-r border-white/5',
        mobile ? 'w-full h-full' : 'w-60 hidden md:flex sticky top-0 h-screen'
      )}
    >
      {/* Logo */}
      <div className="px-5 h-16 flex items-center border-b border-white/5 shrink-0">
        <Link to="/" className="flex items-center gap-2">
          <div className="w-7 h-7 rounded-lg bg-gradient-to-br from-rose-500 to-rose-600 flex items-center justify-center">
            <Scissors className="w-3.5 h-3.5 text-white" strokeWidth={2.5} />
          </div>
          <span className="font-display font-bold text-base">
            Hair<span className="text-rose-500">Book</span>
          </span>
        </Link>
      </div>

      {/* Role badge */}
      <div className="px-4 py-3 border-b border-white/5">
        <span className="text-xs text-slate-500 uppercase tracking-wider">{roleLabel}</span>
      </div>

      {/* Nav items */}
      <nav className="flex-1 p-3 space-y-0.5 overflow-y-auto">
        {navItems.map((item) => (
          <NavLink
            key={item.href}
            to={item.href}
            end={item.href.split('/').length <= 2}
            onClick={() => setMobileOpen(false)}
            className={({ isActive }) =>
              cn(
                'flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all',
                isActive
                  ? 'bg-rose-500/15 text-rose-400 border-l-2 border-rose-500 pl-[10px]'
                  : 'text-slate-400 hover:text-white hover:bg-white/5'
              )
            }
          >
            <item.icon className="w-4 h-4 shrink-0" />
            {item.label}
          </NavLink>
        ))}
      </nav>

      {/* User footer */}
      <div className="p-3 border-t border-white/5 shrink-0">
        <div className="flex items-center gap-3 px-2 py-2 rounded-xl hover:bg-white/5 transition-colors">
          <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-rose-500 to-rose-700 flex items-center justify-center text-xs font-bold text-white shrink-0">
            {initials}
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-sm font-medium text-white truncate">{user?.fullName}</p>
            <p className="text-xs text-slate-500 truncate">{user?.email}</p>
          </div>
          <button
            onClick={logout}
            className="w-7 h-7 flex items-center justify-center rounded-lg text-slate-500 hover:text-rose-400 hover:bg-rose-500/10 transition-colors shrink-0"
            title="Odjava"
          >
            <LogOut className="w-3.5 h-3.5" />
          </button>
        </div>
      </div>
    </aside>
  )

  return (
    <div className="min-h-screen flex bg-[#080C14] text-white">
      {/* Desktop sidebar */}
      <Sidebar />

      {/* Mobile sidebar overlay */}
      <AnimatePresence>
        {mobileOpen && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 z-40 md:hidden"
          >
            <div className="absolute inset-0 bg-black/70" onClick={() => setMobileOpen(false)} />
            <motion.div
              initial={{ x: -280 }}
              animate={{ x: 0 }}
              exit={{ x: -280 }}
              transition={{ type: 'spring', damping: 25, stiffness: 200 }}
              className="relative z-10 w-72 h-full"
            >
              <Sidebar mobile />
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Main content */}
      <div className="flex-1 flex flex-col min-w-0">
        {/* Top bar */}
        <header className="h-16 flex items-center justify-between px-5 border-b border-white/5 bg-[#080C14] sticky top-0 z-30">
          <button
            onClick={() => setMobileOpen(true)}
            className="md:hidden w-9 h-9 flex items-center justify-center rounded-xl text-slate-400 hover:text-white hover:bg-white/8 transition-colors"
          >
            <Menu className="w-5 h-5" />
          </button>

          <div className="hidden md:block">
            <h1 className="text-sm font-medium text-slate-400">
              {user?.role === 'SALON_OWNER' && 'Admin Panel'}
              {user?.role === 'HAIRDRESSER' && 'Frizer Panel'}
              {user?.role === 'CLIENT' && 'Moj Profil'}
            </h1>
          </div>

          <div className="flex items-center gap-2 ml-auto">
            <button className="relative w-9 h-9 rounded-xl flex items-center justify-center text-slate-400 hover:text-white hover:bg-white/8 transition-colors">
              <Bell className="w-4.5 h-4.5" />
              <span className="absolute top-1.5 right-1.5 w-2 h-2 rounded-full bg-rose-500 ring-2 ring-[#080C14]" />
            </button>
          </div>
        </header>

        {/* Page content */}
        <main className="flex-1 p-5 md:p-8 overflow-auto">
          <Outlet />
        </main>
      </div>
    </div>
  )
}