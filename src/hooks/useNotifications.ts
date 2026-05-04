import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { notificationService } from '../services/notification.service'
import { useAuthStore } from '../store/authStore'

export function useNotifications() {
  const { isAuthenticated } = useAuthStore()
  return useQuery({
    queryKey: ['notifications'],
    queryFn:  () => notificationService.getAll(),
    enabled:  isAuthenticated(),
    refetchInterval: 30_000, // polling svakih 30s
    staleTime: 15_000,
  })
}

export function useUnreadCount() {
  const { isAuthenticated } = useAuthStore()
  return useQuery({
    queryKey: ['notifications', 'unread'],
    queryFn:  () => notificationService.getUnreadCount(),
    enabled:  isAuthenticated(),
    refetchInterval: 30_000,
    staleTime: 15_000,
  })
}

export function useMarkRead() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => notificationService.markRead(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['notifications'] })
    },
  })
}

export function useMarkAllRead() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: () => notificationService.markAllRead(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['notifications'] })
    },
  })
}
