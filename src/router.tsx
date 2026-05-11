import { lazy, Suspense } from 'react'
import { createBrowserRouter, Navigate, Outlet } from 'react-router-dom'
import { useAuthStore } from './store/authStore.ts'
import type { UserRole } from './types/user.types.ts'
import PageLayout from './layouts/Pagelayout.tsx'
import DashboardLayout from './layouts/DashboardLayout.tsx'


// ─── Lazy page imports ────────────────────────────────────────────────────────
const LandingPage           = lazy(() => import('./pages/public/LandingPage.tsx'))
const SalonSearchPage       = lazy(() => import('./pages/public/SalonSearchPage.tsx'))
const SalonProfilePage      = lazy(() => import('./pages/public/SalonProfilePage.tsx'))
const LoginPage             = lazy(() => import('./pages/auth/LoginPage'))
const RegisterPage          = lazy(() => import('./pages/auth/RegisterPage'))
const ClientDashboard       = lazy(() => import('./pages/client/ClientDashboard'))
const BookingPage           = lazy(() => import('./pages/client/BookingPage'))
const FavoritesPage         = lazy(() => import('./pages/client/FavoritesPage'))
const HairdresserDashboard  = lazy(() => import('./pages/hairdresser/HairdresserDashboard'))
const OwnerDashboard        = lazy(() => import('./pages/owner/OwnerDashboard'))
const SuperAdminDashboard   = lazy(() => import('./pages/admin/SuperAdminDashboard'))

// ─── Fallback while lazy loading ─────────────────────────────────────────────
function PageLoader() {
  return (
    <div className="min-h-screen bg-[#080C14] flex items-center justify-center">
      <div className="w-8 h-8 rounded-full border-2 border-rose-500 border-t-transparent animate-spin" />
    </div>
  )
}

// ─── Protected route wrapper ─────────────────────────────────────────────────
function ProtectedRoute({ roles }: { roles?: UserRole[] }) {
  const { user, isAuthenticated } = useAuthStore()
  if (!isAuthenticated()) return <Navigate to="/login" replace />
  if (roles && user && !roles.includes(user.role)) return <Navigate to="/" replace />
  return <Outlet />
}

// ─── Wrap all lazy pages in Suspense ─────────────────────────────────────────
function S({ children }: { children: React.ReactNode }) {
  return <Suspense fallback={<PageLoader />}>{children}</Suspense>
}

// ─── Router ──────────────────────────────────────────────────────────────────
export const router = createBrowserRouter([
  {
    element: <PageLayout />,
    children: [
      { index: true, element: <S><LandingPage /></S> },
      { path: 'salons', element: <S><SalonSearchPage /></S> },
      { path: 'salons/:slug', element: <S><SalonProfilePage /></S> },
    ],
  },
  {
    path: 'login',
    element: <S><LoginPage /></S>,
  },
  {
    path: 'register',
    element: <S><RegisterPage /></S>,
  },

  // ── Client routes ──────────────────────────────────────────────────────────
  {
    element: <ProtectedRoute roles={['CLIENT']} />,
    children: [
      {
        element: <DashboardLayout />,
        children: [
          { path: 'dashboard', element: <S><ClientDashboard /></S> },
          { path: 'favorites', element: <S><FavoritesPage /></S> },
        ],
      },
      { path: 'book/:salonId', element: <S><BookingPage /></S> },
    ],
  },

  // ── Hairdresser routes ─────────────────────────────────────────────────────
  {
    element: <ProtectedRoute roles={['HAIRDRESSER']} />,
    children: [
      {
        element: <DashboardLayout />,
        children: [
          { path: 'hairdresser/dashboard', element: <S><HairdresserDashboard /></S> },
        ],
      },
    ],
  },

  // ── Owner routes ───────────────────────────────────────────────────────────
  {
    element: <ProtectedRoute roles={['SALON_OWNER']} />,
    children: [
      {
        element: <DashboardLayout />,
        children: [
          { path: 'owner/dashboard', element: <S><OwnerDashboard /></S> },
        ],
      },
    ],
  },

  // ── Admin routes ───────────────────────────────────────────────────────────
  {
    element: <ProtectedRoute roles={['ADMIN']} />,
    children: [
      {
        element: <DashboardLayout />,
        children: [
          { path: 'admin/dashboard', element: <S><SuperAdminDashboard /></S> },
        ],
      },
    ],
  },

  // ── Misc redirects ────────────────────────────────────────────────────────
  { path: 'settings',    element: <Navigate to="/dashboard" replace /> },
  { path: 'za-salone',  element: <Navigate to="/register" replace /> },
  { path: 'profile',    element: <Navigate to="/dashboard" replace /> },

  // ── Fallback ───────────────────────────────────────────────────────────────
  { path: '*', element: <Navigate to="/" replace /> },
])