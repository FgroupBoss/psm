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

export type ConfigItemPath =
  | 'dictionaries'
  | 'forms'
  | 'workflows'
  | 'rules'
  | 'notifications/templates'
  | 'attachments/policies';

export interface ConfigItemRecord {
  id: number;
  tenantId: number;
  configType: string;
  configCode: string;
  configName: string;
  versionNo: number;
  status: string;
  bizScene?: string;
  content?: Record<string, unknown>;
  remark?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface ConfigItemRequest {
  tenantId: number;
  configCode: string;
  configName: string;
  bizScene?: string;
  content?: Record<string, unknown>;
  remark?: string;
  status?: string;
}

export interface ConfigItemQuery {
  tenantId: number;
  keyword?: string;
  status?: string;
  bizScene?: string;
  pageNo?: number;
  pageSize?: number;
}

export interface RuleEvaluationRequest {
  tenantId: number;
  scene: string;
  ruleCode?: string;
  facts?: Record<string, unknown>;
}

export interface RuleEvaluationResult {
  passed: boolean;
  level: string;
  ruleCode?: string;
  message?: string;
  evidence?: string[];
  suggestion?: string;
}

export interface MenuTreeNode {
  id: number;
  tenantId?: number;
  parentId?: number;
  resourceType: string;
  resourceCode: string;
  resourceName: string;
  routePath?: string;
  apiPath?: string;
  httpMethod?: string;
  sortOrder?: number;
  visible?: boolean;
  status: string;
  children?: MenuTreeNode[];
}

export interface OrgTreeNode {
  id: number;
  tenantId: number;
  parentId?: number;
  orgCode: string;
  orgName: string;
  orgType: string;
  sortOrder?: number;
  status: string;
  children?: OrgTreeNode[];
}

export interface IamUserRecord {
  id: number;
  tenantId: number;
  authUserId?: number;
  username: string;
  displayName: string;
  mobile?: string;
  email?: string;
  orgId?: number;
  postId?: number;
  accountType?: string;
  status: string;
  permissionVersion?: number;
}

export interface IamUserRequest {
  tenantId: number;
  authUserId?: number;
  username: string;
  displayName: string;
  mobile?: string;
  email?: string;
  orgId?: number;
  postId?: number;
  accountType?: string;
  status?: string;
}

export interface RoleRecord {
  id: number;
  tenantId: number;
  roleCode: string;
  roleName: string;
  roleType?: string;
  description?: string;
  status: string;
}

export interface RoleRequest {
  tenantId: number;
  roleCode: string;
  roleName: string;
  roleType?: string;
  description?: string;
  status?: string;
}

export interface OrgRequest {
  tenantId: number;
  parentId?: number;
  orgCode: string;
  orgName: string;
  orgType: string;
  sortOrder?: number;
  status?: string;
}

export interface MenuResourceRequest {
  tenantId?: number;
  parentId?: number;
  resourceType: string;
  resourceCode: string;
  resourceName: string;
  routePath?: string;
  apiPath?: string;
  httpMethod?: string;
  sortOrder?: number;
  visible: boolean;
  status?: string;
}

export interface AssignUserRoleRequest {
  tenantId: number;
  roleIds: number[];
}

export interface UserPermissionSummary {
  tenantId: number;
  userId: number;
  username: string;
  permissionVersion?: number;
  permissionCodes: string[];
  menus: MenuTreeNode[];
}
