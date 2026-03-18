import { create } from "zustand";
import { persist } from "zustand/middleware";

type Role = "CLIENT" | "HAIRDRESSER" | "SALON_OWNER" | "ADMIN";

interface User {
    id: number;
    email: string;
    fullName: string;
    role: Role;
}

interface AuthStore {
    user: User | null;
    token: string | null;
    setAuth: (user: User, token: string) => void;
    logout: () => void;
    isAuthenticated: () => boolean;
}

export const useAuthStore = create<AuthStore>()(
    persist(
        (set, get) => ({
            user: null,
            token: null,
            setAuth: (user, token) => set({ user, token }),
            logout: () => set({ user: null, token: null }),
            isAuthenticated: () => !!get().token,
        }),
        { name: "brico-auth" }

    )
)

export default useAuthStore;