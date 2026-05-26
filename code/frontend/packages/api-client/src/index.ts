import { clearTokens, getAccessToken, saveTokens } from '@psm/auth';
import type {
  ApiResponse,
  AuditLogRecord,
  AuthTokenResponse,
  AuthUser,
  BaseDataRecord,
  BaseDataRequest,
  ConfigItemPath,
  ConfigItemQuery,
  ConfigItemRecord,
  ConfigItemRequest,
  DemoInfo,
  LoginRequest,
  MasterDataRecord,
  PageResult,
  RuleEvaluationRequest,
  RuleEvaluationResult,
  AssignUserRoleRequest,
  IamUserRecord,
  IamUserRequest,
  MenuResourceRequest,
  MenuTreeNode,
  OrgRequest,
  OrgTreeNode,
  RoleRecord,
  RoleRequest,
  UserPermissionSummary
} from '@psm/domain-types';

export async function fetchDemoInfo(): Promise<DemoInfo> {
  return request<DemoInfo>('/api/demo');
}

export async function login(payload: LoginRequest): Promise<AuthTokenResponse> {
  const result = await request<AuthTokenResponse>('/auth/login', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
  saveTokens(result.accessToken, result.refreshToken);
  return result;
}

export async function fetchCurrentUser(): Promise<AuthUser> {
  return request<AuthUser>('/auth/me');
}

export async function fetchMasterDataPage(
  type: string,
  tenantId: number,
  pageNo = 1,
  pageSize = 20
): Promise<PageResult<MasterDataRecord>> {
  const params = new URLSearchParams({
    tenantId: String(tenantId),
    pageNo: String(pageNo),
    pageSize: String(pageSize)
  });
  return request<PageResult<MasterDataRecord>>(`/api/master-data/${type}?${params.toString()}`);
}

export interface PageQuery {
  tenantId: number;
  keyword?: string;
  status?: string;
  pageNo?: number;
  pageSize?: number;
}

export async function fetchBaseDataPage(type: string, query: PageQuery): Promise<PageResult<BaseDataRecord>> {
  const params = new URLSearchParams({
    tenantId: String(query.tenantId),
    pageNo: String(query.pageNo || 1),
    pageSize: String(query.pageSize || 20)
  });
  if (query.keyword) {
    params.set('keyword', query.keyword);
  }
  if (query.status) {
    params.set('status', query.status);
  }
  return request<PageResult<BaseDataRecord>>(`/api/${type}?${params.toString()}`);
}

export async function fetchBaseDataTree(type: string, tenantId: number): Promise<BaseDataRecord[]> {
  return request<BaseDataRecord[]>(`/api/${type}/tree?tenantId=${tenantId}`);
}

export async function createBaseData(type: string, payload: BaseDataRequest): Promise<BaseDataRecord> {
  return request<BaseDataRecord>(`/api/${type}`, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function updateBaseData(type: string, id: number, payload: BaseDataRequest): Promise<BaseDataRecord> {
  return request<BaseDataRecord>(`/api/${type}/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  });
}

export async function enableBaseData(type: string, id: number, tenantId: number): Promise<void> {
  return request<void>(`/api/${type}/${id}/enable?tenantId=${tenantId}`, {
    method: 'POST'
  });
}

export async function disableBaseData(type: string, id: number, tenantId: number): Promise<void> {
  return request<void>(`/api/${type}/${id}/disable?tenantId=${tenantId}`, {
    method: 'POST'
  });
}

export async function deleteBaseData(type: string, id: number, tenantId: number): Promise<void> {
  return request<void>(`/api/${type}/${id}?tenantId=${tenantId}`, {
    method: 'DELETE'
  });
}

export async function fetchAuditLogs(query: PageQuery & { bizType?: string; action?: string }): Promise<PageResult<AuditLogRecord>> {
  const params = new URLSearchParams({
    tenantId: String(query.tenantId),
    pageNo: String(query.pageNo || 1),
    pageSize: String(query.pageSize || 20)
  });
  if (query.bizType) {
    params.set('bizType', query.bizType);
  }
  if (query.action) {
    params.set('action', query.action);
  }
  return request<PageResult<AuditLogRecord>>(`/api/audit/logs?${params.toString()}`);
}

export async function fetchConfigItems(
  type: ConfigItemPath,
  query: ConfigItemQuery
): Promise<PageResult<ConfigItemRecord>> {
  const params = new URLSearchParams({
    tenantId: String(query.tenantId),
    pageNo: String(query.pageNo || 1),
    pageSize: String(query.pageSize || 20)
  });
  if (query.keyword) {
    params.set('keyword', query.keyword);
  }
  if (query.status) {
    params.set('status', query.status);
  }
  if (query.bizScene) {
    params.set('bizScene', query.bizScene);
  }
  return request<PageResult<ConfigItemRecord>>(`/api/config/${type}?${params.toString()}`);
}

export async function createConfigItem(
  type: ConfigItemPath,
  payload: ConfigItemRequest
): Promise<ConfigItemRecord> {
  return request<ConfigItemRecord>(`/api/config/${type}`, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function updateConfigItem(
  type: ConfigItemPath,
  id: number,
  payload: ConfigItemRequest
): Promise<ConfigItemRecord> {
  return request<ConfigItemRecord>(`/api/config/${type}/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  });
}

export async function publishConfigItem(id: number, tenantId: number): Promise<void> {
  return request<void>(`/api/config/items/${id}/publish?tenantId=${tenantId}`, {
    method: 'POST'
  });
}

export async function disableConfigItem(id: number, tenantId: number): Promise<void> {
  return request<void>(`/api/config/items/${id}/disable?tenantId=${tenantId}`, {
    method: 'POST'
  });
}

export async function deleteConfigItem(id: number, tenantId: number): Promise<void> {
  return request<void>(`/api/config/items/${id}?tenantId=${tenantId}`, {
    method: 'DELETE'
  });
}

export async function evaluateRules(payload: RuleEvaluationRequest): Promise<RuleEvaluationResult[]> {
  return request<RuleEvaluationResult[]>('/api/config/rules/evaluate', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function fetchMyPermissions(): Promise<UserPermissionSummary> {
  return request<UserPermissionSummary>('/api/iam/users/me/permissions');
}

export async function fetchOrgTree(tenantId: number): Promise<OrgTreeNode[]> {
  return request<OrgTreeNode[]>(`/api/iam/orgs/tree?tenantId=${tenantId}`);
}

export async function createOrg(payload: OrgRequest): Promise<OrgTreeNode> {
  return request<OrgTreeNode>('/api/iam/orgs', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function updateOrg(id: number, payload: OrgRequest): Promise<OrgTreeNode> {
  return request<OrgTreeNode>(`/api/iam/orgs/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  });
}

export async function deleteOrg(id: number, tenantId: number): Promise<void> {
  return request<void>(`/api/iam/orgs/${id}?tenantId=${tenantId}`, {
    method: 'DELETE'
  });
}

export async function fetchIamUsers(query: PageQuery): Promise<PageResult<IamUserRecord>> {
  const params = new URLSearchParams({
    tenantId: String(query.tenantId),
    pageNo: String(query.pageNo || 1),
    pageSize: String(query.pageSize || 20)
  });
  if (query.keyword) {
    params.set('keyword', query.keyword);
  }
  return request<PageResult<IamUserRecord>>(`/api/iam/users?${params.toString()}`);
}

export async function createIamUser(payload: IamUserRequest): Promise<IamUserRecord> {
  return request<IamUserRecord>('/api/iam/users', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function updateIamUser(id: number, payload: IamUserRequest): Promise<IamUserRecord> {
  return request<IamUserRecord>(`/api/iam/users/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  });
}

export async function updateIamUserStatus(
  id: number,
  tenantId: number,
  status: string
): Promise<void> {
  return request<void>(`/api/iam/users/${id}/status`, {
    method: 'PUT',
    body: JSON.stringify({ tenantId, status })
  });
}

export async function assignUserRoles(id: number, payload: AssignUserRoleRequest): Promise<void> {
  return request<void>(`/api/iam/users/${id}/roles`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  });
}

export async function fetchRoles(query: PageQuery): Promise<PageResult<RoleRecord>> {
  const params = new URLSearchParams({
    tenantId: String(query.tenantId),
    pageNo: String(query.pageNo || 1),
    pageSize: String(query.pageSize || 20)
  });
  if (query.keyword) {
    params.set('keyword', query.keyword);
  }
  return request<PageResult<RoleRecord>>(`/api/iam/roles?${params.toString()}`);
}

export async function createRole(payload: RoleRequest): Promise<RoleRecord> {
  return request<RoleRecord>('/api/iam/roles', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function updateRole(id: number, payload: RoleRequest): Promise<RoleRecord> {
  return request<RoleRecord>(`/api/iam/roles/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  });
}

export async function deleteRole(id: number, tenantId: number): Promise<void> {
  return request<void>(`/api/iam/roles/${id}?tenantId=${tenantId}`, {
    method: 'DELETE'
  });
}

export async function fetchMenuTree(tenantId: number): Promise<MenuTreeNode[]> {
  return request<MenuTreeNode[]>(`/api/iam/menus/tree?tenantId=${tenantId}`);
}

export async function createMenu(payload: MenuResourceRequest): Promise<MenuTreeNode> {
  return request<MenuTreeNode>('/api/iam/menus', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function updateMenu(id: number, payload: MenuResourceRequest): Promise<MenuTreeNode> {
  return request<MenuTreeNode>(`/api/iam/menus/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  });
}

export async function deleteMenu(id: number, tenantId: number): Promise<void> {
  return request<void>(`/api/iam/menus/${id}?tenantId=${tenantId}`, {
    method: 'DELETE'
  });
}

export async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  const headers = new Headers(init.headers);
  if (!headers.has('Content-Type') && init.body) {
    headers.set('Content-Type', 'application/json');
  }
  const token = getAccessToken();
  if (token) {
    headers.set('Authorization', `Bearer ${token}`);
  }
  const response = await fetch(path, {
    ...init,
    headers
  });
  if (!response.ok) {
    if (response.status === 401) {
      clearTokens();
    }
    throw new Error(await resolveErrorMessage(response));
  }
  const body = (await response.json()) as ApiResponse<T>;
  if (body.code !== 0) {
    if (body.code === 401) {
      clearTokens();
    }
    throw new Error(body.message);
  }
  return body.data as T;
}

async function resolveErrorMessage(response: Response): Promise<string> {
  try {
    const body = (await response.json()) as Partial<ApiResponse<unknown>>;
    return body.message || `HTTP ${response.status}`;
  } catch {
    return `HTTP ${response.status}`;
  }
}
