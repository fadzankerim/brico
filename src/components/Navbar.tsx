import { Link, NavLink, useNavigate } from "react-router-dom";
import { useLogout } from "../hooks/useAuth";
import { useAuthStore } from "../store/authStore"
import { useEffect, useState } from "react";
import {
  Bell,
  Calendar,
  ChevronDown,
  Heart,
  LayoutDashboard,
  LogOut,
  Menu,
  Scissors,
  Settings,
  User,
  X,
} from "lucide-react";
import { cn } from "../lib/utils";
import { AnimatePresence, motion } from "motion/react";
import type { LucideIcon } from "lucide-react";
import { useUnreadCount } from "../hooks/useNotifications";

function DropItem({ icon: Icon, label, onClick, danger }: {
  icon: LucideIcon;
  label: string;
  onClick: () => void;
  danger?: boolean;
}) {
  return (
    <button
      onClick={onClick}
      className={cn(
        'flex items-center gap-2.5 w-full px-2.5 py-2 rounded-xl text-sm font-medium transition-colors',
        danger
          ? 'text-rose-400 hover:bg-rose-500/10'
          : 'text-slate-300 hover:text-white hover:bg-white/8'
      )}
    >
      <Icon className="w-4 h-4" />
      {label}
    </button>
  );
}


const NAV_LINKS = [
  { label: 'Pronađi Salon', href: '/salons' },
  { label: 'Za Salone', href: '/za-salone' },
  { label: 'Kako Funkcioniše', href: '/#how-it-works' },
]

export default function Navbar(){

  const { user, isAuthenticated } = useAuthStore();
  const logout = useLogout();
  const navigate = useNavigate();
  const { data: unreadCount = 0 } = useUnreadCount();

  const [mobileOpen, setMobileOpen] = useState(false);
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [scrolled, setScrolled] = useState(false);

  useEffect(() => {
    const handler = () => setScrolled(window.scrollY > 20);
    window.addEventListener('scroll', handler);
    return () => window.removeEventListener('scroll', handler);
  }, []);

  // close dropdown on outside click

  useEffect(() => {
    if (!dropdownOpen) return;
    const close = () => setDropdownOpen(false);
    window.addEventListener('click', close);
    return () => window.removeEventListener('click', close);

  }, [dropdownOpen])

  const dashboardHref =
    user?.role === 'SALON_OWNER'
      ? '/owner/dashboard'
      : user?.role === 'HAIRDRESSER'
        ? '/hairdresser/dashboard'
        : '/dashboard'


  const initials = user?.fullName ? user?.fullName.split(' ').map((n) => n[0]).join('').slice(0, 2).toUpperCase() : 'U';

  return (
    <header className={`fixed top-0 left-0 right-0 z-50 transition-all duration-300 ${scrolled ? 'bg-slate-900/95 backdrop-blur-md border-b border-white/5 shadow-lg' : 'bg-transparent border-transparent'}`}>
      <nav className="max-w-7xl mx-auto px-4 sm:px-6 h-16 flex items-center justify-between gap-4">
        {/* Logo */}
        <Link
          to="/"
          className="flex items-center gap-2 shrink-0 group"
          onClick={() => setMobileOpen(false)}
        >
          <div className="w-8 h-8 rounded-xl bg-gradient-to-br from-rose-500 to-rose-600 flex items-center justify-center shadow-lg shadow-rose-500/30 group-hover:shadow-rose-500/50 transition-shadow">
            <Scissors className="w-4 h-4 text-white" strokeWidth={2.5} />
          </div>
          <span className="font-display font-bold text-lg tracking-tight">
            Bri<span className="text-rose-500">co</span>
          </span>
        </Link>

        {/* Desktop nav links */}
        <div className="hidden md:flex items-center gap-1">
          {NAV_LINKS.map((link) => (
            <NavLink
              key={link.href}
              to={link.href}
              className={({ isActive }) =>
                cn(
                  'px-4 py-2 rounded-xl text-sm font-medium transition-colors',
                  isActive
                    ? 'text-white bg-white/8'
                    : 'text-slate-400 hover:text-white hover:bg-white/5'
                )
              }
            >
              {link.label}
            </NavLink>
          ))}
        </div>

        {/* Right side */}
        <div className="flex items-center gap-2 shrink-0">
          {isAuthenticated() && user ? (
            <>
              {/* Notification bell */}
              <button className="relative w-9 h-9 rounded-xl flex items-center justify-center text-slate-400 hover:text-white hover:bg-white/8 transition-colors">
                <Bell className="w-4.5 h-4.5" />
                {unreadCount > 0 && (
                  <span className="absolute top-1.5 right-1.5 w-2 h-2 rounded-full bg-rose-500 ring-2 ring-[#080C14]" />
                )}
              </button>
 
              {/* Avatar dropdown */}
              <div className="relative">
                <button
                  onClick={(e) => { e.stopPropagation(); setDropdownOpen((o) => !o) }}
                  className="flex items-center gap-2 pl-1 pr-2 py-1 rounded-xl hover:bg-white/8 transition-colors"
                >
                  <div className="w-7 h-7 rounded-lg bg-gradient-to-br from-rose-500 to-rose-700 flex items-center justify-center text-xs font-bold text-white">
                    {initials}
                  </div>
                  <span className="hidden sm:block text-sm font-medium text-slate-200 max-w-[100px] truncate">
                    {user.fullName.split(' ')[0]}
                  </span>
                  <ChevronDown
                    className={cn('w-3.5 h-3.5 text-slate-500 transition-transform', dropdownOpen && 'rotate-180')}
                  />
                </button>
 
                <AnimatePresence>
                  {dropdownOpen && (
                    <motion.div
                      initial={{ opacity: 0, y: 6, scale: 0.97 }}
                      animate={{ opacity: 1, y: 0, scale: 1 }}
                      exit={{ opacity: 0, y: 4, scale: 0.97 }}
                      transition={{ duration: 0.15 }}
                      className="absolute right-0 mt-2 w-52 rounded-2xl bg-[#0F1623] border border-white/8 shadow-2xl shadow-black/60 overflow-hidden"
                    >
                      <div className="px-3 py-2.5 border-b border-white/5">
                        <p className="text-xs text-slate-500">Prijavljeni kao</p>
                        <p className="text-sm font-medium text-white truncate">{user.email}</p>
                      </div>
 
                      <div className="p-1.5">
                        <DropItem icon={LayoutDashboard} label="Dashboard" onClick={() => { navigate(dashboardHref); setDropdownOpen(false) }} />
                        {user.role === 'CLIENT' && (
                          <>
                            <DropItem icon={Calendar} label="Moji Termini" onClick={() => { navigate('/dashboard'); setDropdownOpen(false) }} />
                            <DropItem icon={Heart} label="Omiljeni Saloni" onClick={() => { navigate('/favorites'); setDropdownOpen(false) }} />
                          </>
                        )}
                        <DropItem icon={User} label="Profil" onClick={() => { navigate('/profile'); setDropdownOpen(false) }} />
                        <DropItem icon={Settings} label="Postavke" onClick={() => { navigate('/settings'); setDropdownOpen(false) }} />
                      </div>
 
                      <div className="p-1.5 border-t border-white/5">
                        <DropItem
                          icon={LogOut}
                          label="Odjava"
                          onClick={() => { logout(); setDropdownOpen(false) }}
                          danger
                        />
                      </div>
                    </motion.div>
                  )}
                </AnimatePresence>
              </div>
            </>
          ) : (
            <>
              <Link
                to="/login"
                className="hidden sm:block px-4 py-2 text-sm font-medium text-slate-300 hover:text-white transition-colors"
              >
                Login
              </Link>
              <Link
                to="/register"
                className="px-4 py-2 text-sm font-semibold text-white bg-rose-500 hover:bg-rose-600 rounded-xl transition-colors shadow-lg shadow-rose-500/20"
              >
                Register
              </Link>
            </>
          )}
 
          {/* Mobile burger */}
          <button
            onClick={() => setMobileOpen((o) => !o)}
            className="md:hidden w-9 h-9 flex items-center justify-center rounded-xl text-slate-400 hover:text-white hover:bg-white/8 transition-colors"
          >
            {mobileOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
          </button>
        </div>
      </nav>

       {/* Mobile menu */}
      <AnimatePresence>
        {mobileOpen && (
          <motion.div
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: 'auto' }}
            exit={{ opacity: 0, height: 0 }}
            transition={{ duration: 0.2 }}
            className="md:hidden bg-[#080C14]/98 backdrop-blur-xl border-t border-white/5"
          >
            <div className="max-w-7xl mx-auto px-4 py-3 flex flex-col gap-1">
              {NAV_LINKS.map((link) => (
                <NavLink
                  key={link.href}
                  to={link.href}
                  onClick={() => setMobileOpen(false)}
                  className={({ isActive }) =>
                    cn(
                      'px-4 py-3 rounded-xl text-sm font-medium transition-colors',
                      isActive ? 'text-white bg-white/8' : 'text-slate-400 hover:text-white'
                    )
                  }
                >
                  {link.label}
                </NavLink>
              ))}
 
              {!isAuthenticated() && (
                <div className="flex gap-2 pt-2 border-t border-white/5 mt-1">
                  <Link
                    to="/login"
                    onClick={() => setMobileOpen(false)}
                    className="flex-1 text-center py-2.5 rounded-xl text-sm font-medium text-slate-300 border border-white/10 hover:border-white/20"
                  >
                    Prijava
                  </Link>
                  <Link
                    to="/register"
                    onClick={() => setMobileOpen(false)}
                    className="flex-1 text-center py-2.5 rounded-xl text-sm font-semibold text-white bg-rose-500"
                  >
                    Registracija
                  </Link>
                </div>
              )}
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </header>
  )
}

