import { z } from 'zod'

export const loginSchema = z.object({
  email: z.string().email('Unesite ispravnu email adresu'),
  password: z.string().min(6, 'Lozinka mora imati najmanje 6 karaktera'),
})

export const registerSchema = z
  .object({
    fullName: z.string().min(2, 'Ime mora imati najmanje 2 karaktera'),
    email: z.string().email('Unesite ispravnu email adresu'),
    phone: z.string().optional(),
    password: z.string().min(8, 'Lozinka mora imati najmanje 8 karaktera'),
    confirmPassword: z.string(),
    role: z.enum(['CLIENT', 'SALON_OWNER']).default('CLIENT'),
  })
  .refine((d) => d.password === d.confirmPassword, {
    message: 'Lozinke se ne podudaraju',
    path: ['confirmPassword'],
  })

export const reviewSchema = z.object({
  rating: z.number().min(1).max(5),
  comment: z.string().min(10, 'Recenzija mora imati najmanje 10 karaktera').max(500),
})

export const salonSchema = z.object({
  name: z.string().min(2, 'Naziv salona je obavezan'),
  description: z.string().optional(),
  city: z.string().min(2, 'Grad je obavezan'),
  address: z.string().min(5, 'Adresa je obavezna'),
  phone: z.string().optional(),
  website: z.string().url('Unesite ispravnu URL adresu').optional().or(z.literal('')),
})

export type LoginForm = z.infer<typeof loginSchema>
export type RegisterForm = z.infer<typeof registerSchema>
export type ReviewForm = z.infer<typeof reviewSchema>
export type SalonForm = z.infer<typeof salonSchema>