export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

export interface DemoInfo {
  service: string;
  module: string;
  version: string;
}

export interface AuthUser {
  id: number;
  tenantId: number;
  username: string;
  displayName?: string;
  mobile?: string;
  email?: string;
  accountType: string;
  status: string;
  lastLoginAt?: string;
}

export interface AuthTokenResponse {
  accessToken: string;
  tokenType: string;
  refreshToken: string;
  accessExpiresAt: string;
  refreshExpiresAt: string;
  user: AuthUser;
}

export interface LoginRequest {
  tenantId: number;
  username: string;
  password: string;
}

export interface PageResult<T> {
  total: number;
  pageNo: number;
  pageSize: number;
  records: T[];
}

export interface MasterDataRecord {
  id: number;
  tenantId: number;
  category: string;
  code: string;
  name: string;
  parentId?: number;
  type?: string;
  status: string;
  attributes?: Record<string, unknown>;
}
