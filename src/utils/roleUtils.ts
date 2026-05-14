export function getRoleDashboardHref(role: string | undefined): string {
  if (role === 'ADMIN')       return '/admin/dashboard'
  if (role === 'SALON_OWNER') return '/owner/dashboard'
  if (role === 'HAIRDRESSER') return '/hairdresser/dashboard'
  return '/dashboard'
}
