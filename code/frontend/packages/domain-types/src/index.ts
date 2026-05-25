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

export interface BaseDataRecord {
  id: number;
  tenantId: number;
  code: string;
  name: string;
  parentId?: number;
  areaId?: number;
  unitId?: number;
  equipmentId?: number;
  type?: string;
  siteId?: number;
  riskLevel?: string;
  majorHazardFlag?: boolean;
  sortNo?: number;
  runningStatus?: string;
  sourceSystem?: string;
  sourceTag?: string;
  metricType?: string;
  unit?: string;
  highHigh?: number;
  high?: number;
  low?: number;
  lowLow?: number;
  status: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface BaseDataRequest {
  tenantId: number;
  code?: string;
  name: string;
  parentId?: number;
  areaId?: number;
  unitId?: number;
  equipmentId?: number;
  type?: string;
  siteId?: number;
  riskLevel?: string;
  majorHazardFlag?: boolean;
  sortNo?: number;
  runningStatus?: string;
  sourceSystem?: string;
  sourceTag?: string;
  metricType?: string;
  unit?: string;
  highHigh?: number;
  high?: number;
  low?: number;
  lowLow?: number;
  status?: string;
}

export interface AuditLogRecord {
  id: number;
  tenantId: number;
  operatorId?: number;
  operatorName?: string;
  action: string;
  bizType: string;
  bizId?: number;
  beforeValue?: string;
  afterValue?: string;
  result: string;
  clientIp?: string;
  userAgent?: string;
  operatedAt: string;
}
