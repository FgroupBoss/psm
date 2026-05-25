export interface TenantContext {
  tenantId: string;
  tenantCode: string;
}

const ACCESS_TOKEN_KEY = 'psm.accessToken';
const REFRESH_TOKEN_KEY = 'psm.refreshToken';

export function hasPermission(permissions: string[], permission: string): boolean {
  return permissions.includes(permission);
}

export function getAccessToken(): string | null {
  return window.localStorage.getItem(ACCESS_TOKEN_KEY);
}

export function hasAccessToken(): boolean {
  return Boolean(getAccessToken());
}

export function saveTokens(accessToken: string, refreshToken: string): void {
  window.localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
  window.localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
}

export function clearTokens(): void {
  window.localStorage.removeItem(ACCESS_TOKEN_KEY);
  window.localStorage.removeItem(REFRESH_TOKEN_KEY);
}
