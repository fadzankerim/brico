import { api } from './api'

export type NotificationType =
  | 'APPOINTMENT_CONFIRMED'
  | 'APPOINTMENT_CANCELLED'
  | 'NEW_APPOINTMENT'
  | 'NEW_REVIEW'

export interface Notification {
  id:          number
  type:        NotificationType | string
  title:       string
  message:     string
  referenceId: number | null
  isRead:      boolean
  createdAt:   string
}

export const notificationService = {
  getAll: () =>
    api.get<Notification[]>('/notifications').then(r => r.data),

  getUnreadCount: () =>
    api.get<{ count: number }>('/notifications/unread-count').then(r => r.data.count),

  markRead: (id: number) =>
    api.patch(`/notifications/${id}/read`),

  markAllRead: () =>
    api.patch('/notifications/read-all'),
}
