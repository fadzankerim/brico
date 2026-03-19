import { createBrowserRouter, Navigate, Outlet } from "react-router-dom";


import { lazy, Suspense } from "react";
import { useAuthStore } from "./store/authStore";

const LandingPage = lazy(() => import("@/pages/public/LandingPage"));
const SalonSearchPage = lazy(() => import("@/pages/public/SalonSearchPage"));
const SalonProfilePage = lazy(() => import("@/pages/public/SalonProfilePage"));
const LoginPage = lazy(() => import("@/pages/auth/LoginPage"));
const RegisterPage = lazy(() => import("@/pages/auth/RegisterPage"));
const ClientDashboard = lazy(() => import("@/pages/client/ClientDashboard"));
const BookingPage = lazy(() => import("@/pages/client/BookingPage"));
const HairdresserDashboard = lazy(() => import("@/pages/hairdresser/HairdresserDashboard"));
const OwnerDashboard = lazy(() => import("@/pages/owner/OwnerDashboard"));

function ProtectedRoute({ roles }: { roles?: string[] }) {
    const { user, isAuthenticated } = useAuthStore();
    if (!isAuthenticated()) return <Navigate to="/login" replace />;
    if (roles && user && !roles.includes(user.role)) return <Navigate to="/" replace />;
    return <Outlet />;
}

export const router = createBrowserRouter([
    { path: "/", element: <LandingPage /> },
    { path: "/salons", element: <SalonSearchPage /> },
    { path: "/salons/:slug", element: <SalonProfilePage /> },
    { path: "/login", element: <LoginPage /> },
    { path: "/register", element: <RegisterPage /> },
    {
        element: <ProtectedRoute roles={["CLIENT"]} />,
        children: [
            { path: "/dashboard", element: <ClientDashboard /> },
            { path: "/book/:salonId", element: <BookingPage /> },
        ],
    },
    {
        element: <ProtectedRoute roles={["HAIRDRESSER"]} />,
        children: [
            { path: "/hairdresser/dashboard", element: <HairdresserDashboard /> },
        ],
    },
    {
        element: <ProtectedRoute roles={["SALON_OWNER"]} />,
        children: [
            { path: "/owner/dashboard", element: <OwnerDashboard /> },
        ],
    },
]);