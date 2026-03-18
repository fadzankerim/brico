import { create } from 'zustand'
import type { SalonSearchParams } from '../types/salon.typs'

interface UIStore {
  sidebarOpen: boolean
  setSidebarOpen: (open: boolean) => void
  toggleSidebar: () => void

  searchFilters: SalonSearchParams
  setSearchFilters: (filters: Partial<SalonSearchParams>) => void
  resetFilters: () => void
}

const DEFAULT_FILTERS: SalonSearchParams = {
  sortBy: 'rating',
  page: 0,
  size: 12,
}

export const useUIStore = create<UIStore>((set) => ({
  sidebarOpen: false,
  setSidebarOpen: (open) => set({ sidebarOpen: open }),
  toggleSidebar: () => set((s) => ({ sidebarOpen: !s.sidebarOpen })),

  searchFilters: DEFAULT_FILTERS,
  setSearchFilters: (filters) =>
    set((s) => ({ searchFilters: { ...s.searchFilters, ...filters, page: 0 } })),
  resetFilters: () => set({ searchFilters: DEFAULT_FILTERS }),
}))