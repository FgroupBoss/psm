export interface TenantContext {
  tenantId: string;
  tenantCode: string;
}

export function hasPermission(permissions: string[], permission: string): boolean {
  return permissions.includes(permission);
}

