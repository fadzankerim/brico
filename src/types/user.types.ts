
export type UserRole = 'CLIENT' | 'HAIRDRESSER' | 'SALON_OWNER' | 'ADMIN';

export type User = {

    id: number
    email: string
    fullName: string
    phone?: string
    profilePhoto?: string
    role: UserRole
    updatedAt: Date;
    createdAt: string
};

export interface AuthResponse {
    accessToken: string
    refreshToken: string
    user: User
}

export interface LoginRequest {
    email: string
    password: string
}

export interface RegisterRequest {
    email: string
    password: string
    fullName: string
    phone?: string
    role?: UserRole
}

