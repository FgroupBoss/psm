import { clearTokens, getAccessToken, saveTokens } from '@psm/auth';
import { CONTRACTOR_API, MAJOR_HAZARD_API, ALARM_API, WORK_PERMIT_API, REPORT_API, FILE_API, MOBILE_API } from '@psm/domain-types';
import type {
  ApiResponse,
  AuditLogRecord,
  AlarmHealthInfo,
  AlarmEventRecord,
  AlarmDetailRecord,
  AlarmIngestRequest,
  AlarmActionRequest,
  AlarmFalseCloseRequest,
  AlarmAreaActiveCheckRequest,
  AlarmAreaActiveCheckResult,
  HazardAlarmSummaryRecord,
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
  CompanyApproveRequest,
  CompanyReasonRequest,
  ContractorCompanyRecord,
  ContractorCompanyRequest,
  ContractorQualificationRecord,
  ContractorQualificationRequest,
  ContractorWorkerRecord,
  ContractorWorkerRequest,
  EligibilityCheckRequest,
  EligibilityCheckResult,
  HazardStatusRequest,
  HazardPointRecord,
  HazardPointRequest,
  HazardAttachmentRecord,
  HazardAttachmentRequest,
  WorkerCertificateRecord,
  WorkerCertificateRequest,
  WorkerTrainingRecord,
  WorkerTrainingRequest,
  WorkerViolationRecord,
  WorkerViolationRequest,
  IamUserRecord,
  MajorHazardRecord,
  MajorHazardRequest,
  MajorHazardResponsibilityRecord,
  ResponsibilityReplaceRequest,
  RiskContextRequest,
  RiskContextResult,
  IamUserRequest,
  MenuResourceRequest,
  MenuTreeNode,
  OrgRequest,
  OrgTreeNode,
  RoleRecord,
  RoleRequest,
  UserPermissionSummary,
  WorkPermitHealthInfo,
  WorkPermitRecord,
  WorkPermitDetailRecord,
  WorkPermitRequest,
  WorkPermitWorkerRequest,
  WorkPermitWorkerRecord,
  RiskAnalysisRecord,
  SafetyMeasureRecord,
  GasTestRecord,
  PreCheckResult,
  DashboardOverviewRecord,
  WorkPermitReportSummary,
  AcceptanceTestCaseRecord,
  AcceptanceTestRunRecord,
  AcceptanceTestRunRequest,
  FileObjectRecord,
  AlarmReportSummary,
  AlarmReportDetail,
  MajorHazardReportSummary,
  ContractorReportSummary,
  AuditReportSummary,
  WorkPermitReportDetail,
  ReportExportTaskRecord,
  TrendSeriesRecord,
  MobileTaskRecord
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

export async function fetchContractorCompanies(query: PageQuery & { status?: string }): Promise<PageResult<ContractorCompanyRecord>> {
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
  return request<PageResult<ContractorCompanyRecord>>(`${CONTRACTOR_API.companies}?${params.toString()}`);
}

export async function fetchContractorCompany(id: number, tenantId: number): Promise<ContractorCompanyRecord> {
  return request<ContractorCompanyRecord>(`${CONTRACTOR_API.companies}/${id}?tenantId=${tenantId}`);
}

export async function createContractorCompany(payload: ContractorCompanyRequest): Promise<ContractorCompanyRecord> {
  return request<ContractorCompanyRecord>(CONTRACTOR_API.companies, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function updateContractorCompany(
  id: number,
  payload: ContractorCompanyRequest
): Promise<ContractorCompanyRecord> {
  return request<ContractorCompanyRecord>(`${CONTRACTOR_API.companies}/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  });
}

export async function submitContractorCompany(id: number, tenantId: number): Promise<ContractorCompanyRecord> {
  return request<ContractorCompanyRecord>(`${CONTRACTOR_API.companies}/${id}/submit?tenantId=${tenantId}`, {
    method: 'POST'
  });
}

export async function approveContractorCompany(
  id: number,
  tenantId: number,
  payload: CompanyApproveRequest
): Promise<ContractorCompanyRecord> {
  return request<ContractorCompanyRecord>(`${CONTRACTOR_API.companies}/${id}/approve?tenantId=${tenantId}`, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function suspendContractorCompany(
  id: number,
  tenantId: number,
  payload: CompanyReasonRequest
): Promise<ContractorCompanyRecord> {
  return request<ContractorCompanyRecord>(`${CONTRACTOR_API.companies}/${id}/suspend?tenantId=${tenantId}`, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function blacklistContractorCompany(
  id: number,
  tenantId: number,
  payload: CompanyReasonRequest
): Promise<ContractorCompanyRecord> {
  return request<ContractorCompanyRecord>(`${CONTRACTOR_API.companies}/${id}/blacklist?tenantId=${tenantId}`, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function fetchCompanyQualifications(
  companyId: number,
  tenantId: number
): Promise<ContractorQualificationRecord[]> {
  return request<ContractorQualificationRecord[]>(
    `${CONTRACTOR_API.companies}/${companyId}/qualifications?tenantId=${tenantId}`
  );
}

export async function createCompanyQualification(
  companyId: number,
  payload: ContractorQualificationRequest
): Promise<ContractorQualificationRecord> {
  return request<ContractorQualificationRecord>(`${CONTRACTOR_API.companies}/${companyId}/qualifications`, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function updateCompanyQualification(
  companyId: number,
  qualId: number,
  payload: ContractorQualificationRequest
): Promise<ContractorQualificationRecord> {
  return request<ContractorQualificationRecord>(
    `${CONTRACTOR_API.companies}/${companyId}/qualifications/${qualId}`,
    {
      method: 'PUT',
      body: JSON.stringify(payload)
    }
  );
}

export async function deleteCompanyQualification(
  companyId: number,
  qualId: number,
  tenantId: number
): Promise<void> {
  return request<void>(`${CONTRACTOR_API.companies}/${companyId}/qualifications/${qualId}?tenantId=${tenantId}`, {
    method: 'DELETE'
  });
}

export async function fetchContractorWorkers(
  query: PageQuery & { companyId?: number; accessStatus?: string }
): Promise<PageResult<ContractorWorkerRecord>> {
  const params = new URLSearchParams({
    tenantId: String(query.tenantId),
    pageNo: String(query.pageNo || 1),
    pageSize: String(query.pageSize || 20)
  });
  if (query.keyword) {
    params.set('keyword', query.keyword);
  }
  if (query.companyId != null) {
    params.set('companyId', String(query.companyId));
  }
  if (query.accessStatus) {
    params.set('accessStatus', query.accessStatus);
  }
  return request<PageResult<ContractorWorkerRecord>>(`${CONTRACTOR_API.workers}?${params.toString()}`);
}

export async function fetchContractorWorker(id: number, tenantId: number): Promise<ContractorWorkerRecord> {
  return request<ContractorWorkerRecord>(`${CONTRACTOR_API.workers}/${id}?tenantId=${tenantId}`);
}

export async function createContractorWorker(payload: ContractorWorkerRequest): Promise<ContractorWorkerRecord> {
  return request<ContractorWorkerRecord>(CONTRACTOR_API.workers, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function updateContractorWorker(
  id: number,
  payload: ContractorWorkerRequest
): Promise<ContractorWorkerRecord> {
  return request<ContractorWorkerRecord>(`${CONTRACTOR_API.workers}/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  });
}

export async function submitContractorWorker(id: number, tenantId: number): Promise<ContractorWorkerRecord> {
  return request<ContractorWorkerRecord>(`${CONTRACTOR_API.workers}/${id}/submit?tenantId=${tenantId}`, {
    method: 'POST'
  });
}

export async function approveContractorWorker(
  id: number,
  tenantId: number,
  payload: CompanyApproveRequest
): Promise<ContractorWorkerRecord> {
  return request<ContractorWorkerRecord>(`${CONTRACTOR_API.workers}/${id}/approve?tenantId=${tenantId}`, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function suspendContractorWorker(
  id: number,
  tenantId: number,
  payload: CompanyReasonRequest
): Promise<ContractorWorkerRecord> {
  return request<ContractorWorkerRecord>(`${CONTRACTOR_API.workers}/${id}/suspend?tenantId=${tenantId}`, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function blacklistContractorWorker(
  id: number,
  tenantId: number,
  payload: CompanyReasonRequest
): Promise<ContractorWorkerRecord> {
  return request<ContractorWorkerRecord>(`${CONTRACTOR_API.workers}/${id}/blacklist?tenantId=${tenantId}`, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function fetchWorkerCertificates(workerId: number, tenantId: number): Promise<WorkerCertificateRecord[]> {
  return request<WorkerCertificateRecord[]>(`${CONTRACTOR_API.workers}/${workerId}/certificates?tenantId=${tenantId}`);
}

export async function createWorkerCertificate(
  workerId: number,
  payload: WorkerCertificateRequest
): Promise<WorkerCertificateRecord> {
  return request<WorkerCertificateRecord>(`${CONTRACTOR_API.workers}/${workerId}/certificates`, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function deleteWorkerCertificate(workerId: number, certId: number, tenantId: number): Promise<void> {
  return request<void>(`${CONTRACTOR_API.workers}/${workerId}/certificates/${certId}?tenantId=${tenantId}`, {
    method: 'DELETE'
  });
}

export async function fetchWorkerTrainings(workerId: number, tenantId: number): Promise<WorkerTrainingRecord[]> {
  return request<WorkerTrainingRecord[]>(`${CONTRACTOR_API.workers}/${workerId}/trainings?tenantId=${tenantId}`);
}

export async function createWorkerTraining(
  workerId: number,
  payload: WorkerTrainingRequest
): Promise<WorkerTrainingRecord> {
  return request<WorkerTrainingRecord>(`${CONTRACTOR_API.workers}/${workerId}/trainings`, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function fetchWorkerViolations(workerId: number, tenantId: number): Promise<WorkerViolationRecord[]> {
  return request<WorkerViolationRecord[]>(`${CONTRACTOR_API.workers}/${workerId}/violations?tenantId=${tenantId}`);
}

export async function createWorkerViolation(
  workerId: number,
  payload: WorkerViolationRequest
): Promise<WorkerViolationRecord> {
  return request<WorkerViolationRecord>(`${CONTRACTOR_API.workers}/${workerId}/violations`, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function checkWorkerEligibility(payload: EligibilityCheckRequest): Promise<EligibilityCheckResult> {
  return request<EligibilityCheckResult>(CONTRACTOR_API.eligibilityCheck, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function fetchMajorHazards(
  query: PageQuery & { status?: string; level?: string }
): Promise<PageResult<MajorHazardRecord>> {
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
  if (query.level) {
    params.set('level', query.level);
  }
  return request<PageResult<MajorHazardRecord>>(`${MAJOR_HAZARD_API.base}?${params.toString()}`);
}

export async function fetchMajorHazard(id: number, tenantId: number): Promise<MajorHazardRecord> {
  return request<MajorHazardRecord>(`${MAJOR_HAZARD_API.base}/${id}?tenantId=${tenantId}`);
}

export async function createMajorHazard(payload: MajorHazardRequest): Promise<MajorHazardRecord> {
  return request<MajorHazardRecord>(MAJOR_HAZARD_API.base, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function updateMajorHazard(id: number, payload: MajorHazardRequest): Promise<MajorHazardRecord> {
  return request<MajorHazardRecord>(`${MAJOR_HAZARD_API.base}/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  });
}

export async function publishMajorHazard(id: number, tenantId: number): Promise<MajorHazardRecord> {
  return request<MajorHazardRecord>(`${MAJOR_HAZARD_API.base}/${id}/publish?tenantId=${tenantId}`, {
    method: 'POST'
  });
}

export async function changeMajorHazardStatus(
  id: number,
  tenantId: number,
  payload: HazardStatusRequest
): Promise<MajorHazardRecord> {
  return request<MajorHazardRecord>(`${MAJOR_HAZARD_API.base}/${id}/status?tenantId=${tenantId}`, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function fetchMajorHazardResponsibilities(
  id: number,
  tenantId: number
): Promise<MajorHazardResponsibilityRecord[]> {
  return request<MajorHazardResponsibilityRecord[]>(
    `${MAJOR_HAZARD_API.base}/${id}/responsibilities?tenantId=${tenantId}`
  );
}

export async function replaceMajorHazardResponsibilities(
  id: number,
  tenantId: number,
  payload: ResponsibilityReplaceRequest
): Promise<MajorHazardResponsibilityRecord[]> {
  return request<MajorHazardResponsibilityRecord[]>(
    `${MAJOR_HAZARD_API.base}/${id}/responsibilities?tenantId=${tenantId}`,
    {
      method: 'PUT',
      body: JSON.stringify(payload)
    }
  );
}

export async function fetchMajorHazardPoints(id: number, tenantId: number): Promise<HazardPointRecord[]> {
  return request<HazardPointRecord[]>(`${MAJOR_HAZARD_API.base}/${id}/points?tenantId=${tenantId}`);
}

export async function bindMajorHazardPoint(
  id: number,
  tenantId: number,
  payload: HazardPointRequest
): Promise<HazardPointRecord> {
  return request<HazardPointRecord>(`${MAJOR_HAZARD_API.base}/${id}/points?tenantId=${tenantId}`, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function unbindMajorHazardPoint(id: number, tenantId: number, relId: number): Promise<void> {
  return request<void>(`${MAJOR_HAZARD_API.base}/${id}/points/${relId}?tenantId=${tenantId}`, {
    method: 'DELETE'
  });
}

export async function fetchMajorHazardAttachments(id: number, tenantId: number): Promise<HazardAttachmentRecord[]> {
  return request<HazardAttachmentRecord[]>(`${MAJOR_HAZARD_API.base}/${id}/attachments?tenantId=${tenantId}`);
}

export async function createMajorHazardAttachment(
  id: number,
  tenantId: number,
  payload: HazardAttachmentRequest
): Promise<HazardAttachmentRecord> {
  return request<HazardAttachmentRecord>(`${MAJOR_HAZARD_API.base}/${id}/attachments?tenantId=${tenantId}`, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function deleteMajorHazardAttachment(id: number, tenantId: number, attachmentId: number): Promise<void> {
  return request<void>(`${MAJOR_HAZARD_API.base}/${id}/attachments/${attachmentId}?tenantId=${tenantId}`, {
    method: 'DELETE'
  });
}

export async function fetchMajorHazardRiskContext(payload: RiskContextRequest): Promise<RiskContextResult> {
  return request<RiskContextResult>(MAJOR_HAZARD_API.riskContext, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function fetchAlarmHealth(): Promise<AlarmHealthInfo> {
  return request<AlarmHealthInfo>(ALARM_API.health);
}

export async function fetchAlarms(
  query: PageQuery & {
    keyword?: string;
    status?: string;
    alarmLevel?: string;
    areaId?: number;
    hazardId?: number;
    sourceType?: string;
    occurredFrom?: string;
    occurredTo?: string;
  }
): Promise<PageResult<AlarmEventRecord>> {
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
  if (query.alarmLevel) {
    params.set('alarmLevel', query.alarmLevel);
  }
  if (query.areaId != null) {
    params.set('areaId', String(query.areaId));
  }
  if (query.hazardId != null) {
    params.set('hazardId', String(query.hazardId));
  }
  if (query.sourceType) {
    params.set('sourceType', query.sourceType);
  }
  if (query.occurredFrom) {
    params.set('occurredFrom', query.occurredFrom);
  }
  if (query.occurredTo) {
    params.set('occurredTo', query.occurredTo);
  }
  return request<PageResult<AlarmEventRecord>>(`${ALARM_API.base}?${params.toString()}`);
}

export async function fetchAlarmDetail(id: number, tenantId: number): Promise<AlarmDetailRecord> {
  return request<AlarmDetailRecord>(`${ALARM_API.base}/${id}?tenantId=${tenantId}`);
}

export async function ingestAlarm(payload: AlarmIngestRequest): Promise<AlarmEventRecord> {
  return request<AlarmEventRecord>(ALARM_API.ingest, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function confirmAlarm(
  id: number,
  tenantId: number,
  payload?: AlarmActionRequest
): Promise<AlarmEventRecord> {
  return request<AlarmEventRecord>(`${ALARM_API.base}/${id}/confirm?tenantId=${tenantId}`, {
    method: 'POST',
    body: JSON.stringify(payload || {})
  });
}

export async function dispatchAlarm(
  id: number,
  tenantId: number,
  payload?: AlarmActionRequest
): Promise<AlarmEventRecord> {
  return request<AlarmEventRecord>(`${ALARM_API.base}/${id}/dispatch?tenantId=${tenantId}`, {
    method: 'POST',
    body: JSON.stringify(payload || {})
  });
}

export async function feedbackAlarm(
  id: number,
  tenantId: number,
  payload?: AlarmActionRequest
): Promise<AlarmEventRecord> {
  return request<AlarmEventRecord>(`${ALARM_API.base}/${id}/feedback?tenantId=${tenantId}`, {
    method: 'POST',
    body: JSON.stringify(payload || {})
  });
}

export async function closeAlarm(
  id: number,
  tenantId: number,
  payload?: AlarmActionRequest
): Promise<AlarmEventRecord> {
  return request<AlarmEventRecord>(`${ALARM_API.base}/${id}/close?tenantId=${tenantId}`, {
    method: 'POST',
    body: JSON.stringify(payload || {})
  });
}

export async function falseCloseAlarm(
  id: number,
  tenantId: number,
  payload: AlarmFalseCloseRequest
): Promise<AlarmEventRecord> {
  return request<AlarmEventRecord>(`${ALARM_API.base}/${id}/false-close?tenantId=${tenantId}`, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function checkAreaActiveAlarms(
  payload: AlarmAreaActiveCheckRequest
): Promise<AlarmAreaActiveCheckResult> {
  return request<AlarmAreaActiveCheckResult>(ALARM_API.areaActiveCheck, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function fetchMajorHazardAlarms(
  hazardId: number,
  tenantId: number
): Promise<HazardAlarmSummaryRecord[]> {
  return request<HazardAlarmSummaryRecord[]>(
    `${MAJOR_HAZARD_API.base}/${hazardId}/alarms?tenantId=${tenantId}`
  );
}

export async function fetchAuditLogs(
  query: PageQuery & { bizType?: string; bizTypePrefix?: string; action?: string }
): Promise<PageResult<AuditLogRecord>> {
  const params = new URLSearchParams({
    tenantId: String(query.tenantId),
    pageNo: String(query.pageNo || 1),
    pageSize: String(query.pageSize || 20)
  });
  if (query.bizType) {
    params.set('bizType', query.bizType);
  }
  if (query.bizTypePrefix) {
    params.set('bizTypePrefix', query.bizTypePrefix);
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

export async function fetchWorkPermitHealth(): Promise<WorkPermitHealthInfo> {
  return request<WorkPermitHealthInfo>(WORK_PERMIT_API.health);
}

export async function fetchWorkPermits(query: PageQuery & { workType?: string; status?: string; areaId?: number }): Promise<PageResult<WorkPermitRecord>> {
  const params = new URLSearchParams({
    tenantId: String(query.tenantId),
    pageNo: String(query.pageNo || 1),
    pageSize: String(query.pageSize || 20)
  });
  if (query.keyword) params.set('keyword', query.keyword);
  if (query.status) params.set('status', query.status);
  if (query.workType) params.set('workType', query.workType);
  if (query.areaId) params.set('areaId', String(query.areaId));
  return request<PageResult<WorkPermitRecord>>(`${WORK_PERMIT_API.base}?${params.toString()}`);
}

export async function fetchWorkPermitsByHazard(tenantId: number, hazardId: number): Promise<WorkPermitRecord[]> {
  return request<WorkPermitRecord[]>(`${WORK_PERMIT_API.byHazard}?tenantId=${tenantId}&hazardId=${hazardId}`);
}

export async function fetchWorkPermitDetail(id: number, tenantId: number): Promise<WorkPermitDetailRecord> {
  return request<WorkPermitDetailRecord>(`${WORK_PERMIT_API.base}/${id}?tenantId=${tenantId}`);
}

export async function createWorkPermit(payload: WorkPermitRequest): Promise<WorkPermitRecord> {
  return request<WorkPermitRecord>(WORK_PERMIT_API.base, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function submitWorkPermit(id: number, tenantId: number): Promise<WorkPermitRecord> {
  return request<WorkPermitRecord>(`${WORK_PERMIT_API.base}/${id}/submit?tenantId=${tenantId}`, { method: 'POST' });
}

export async function approveWorkPermit(id: number, tenantId: number, opinion?: string): Promise<WorkPermitRecord> {
  return request<WorkPermitRecord>(`${WORK_PERMIT_API.base}/${id}/approve?tenantId=${tenantId}`, {
    method: 'POST',
    body: JSON.stringify({ opinion })
  });
}

export async function preCheckWorkPermit(id: number, tenantId: number, checkPoint: string): Promise<PreCheckResult> {
  return request<PreCheckResult>(`${WORK_PERMIT_API.base}/${id}/pre-check?tenantId=${tenantId}`, {
    method: 'POST',
    body: JSON.stringify({ tenantId, checkPoint })
  });
}

export async function addWorkPermitWorker(id: number, tenantId: number, payload: WorkPermitWorkerRequest): Promise<WorkPermitWorkerRecord> {
  return request<WorkPermitWorkerRecord>(`${WORK_PERMIT_API.base}/${id}/workers?tenantId=${tenantId}`, {
    method: 'POST',
    body: JSON.stringify({ ...payload, tenantId })
  });
}

export async function saveWorkPermitRiskAnalysis(
  id: number,
  tenantId: number,
  payload: { hazardDesc: string; controlMeasure: string; riskLevel?: string }
): Promise<RiskAnalysisRecord> {
  return request<RiskAnalysisRecord>(`${WORK_PERMIT_API.base}/${id}/risk-analysis`, {
    method: 'POST',
    body: JSON.stringify({ tenantId, ...payload })
  });
}

export async function confirmWorkPermitMeasure(
  id: number,
  measureId: number,
  tenantId: number,
  remark?: string
): Promise<SafetyMeasureRecord> {
  return request<SafetyMeasureRecord>(`${WORK_PERMIT_API.base}/${id}/safety-measures/${measureId}`, {
    method: 'POST',
    body: JSON.stringify({ tenantId, confirmStatus: 'CONFIRMED', remark })
  });
}

export async function addWorkPermitGasTest(
  id: number,
  tenantId: number,
  payload: { gasName: string; qualified: boolean; measuredValue?: string }
): Promise<GasTestRecord> {
  return request<GasTestRecord>(`${WORK_PERMIT_API.base}/${id}/gas-tests`, {
    method: 'POST',
    body: JSON.stringify({ tenantId, ...payload, testedAt: new Date().toISOString().slice(0, 19).replace('T', ' ') })
  });
}

export async function checkInWorkPermit(
  id: number,
  tenantId: number,
  payload?: { locationText?: string; scanCode?: string }
): Promise<unknown> {
  return request(`${WORK_PERMIT_API.base}/${id}/check-in`, {
    method: 'POST',
    body: JSON.stringify({ tenantId, ...payload })
  });
}

export async function sitePermitWorkPermit(
  id: number,
  tenantId: number,
  payload?: { signatureText?: string; locationText?: string; remark?: string }
): Promise<WorkPermitRecord> {
  return request<WorkPermitRecord>(`${WORK_PERMIT_API.base}/${id}/site-permit`, {
    method: 'POST',
    body: JSON.stringify({ tenantId, ...payload })
  });
}

export async function addWorkPermitMonitorRecord(
  id: number,
  tenantId: number,
  payload: { recordType: string; content: string; abnormalFlag?: boolean }
): Promise<unknown> {
  return request(`${WORK_PERMIT_API.base}/${id}/monitor-records`, {
    method: 'POST',
    body: JSON.stringify({ tenantId, ...payload })
  });
}

export async function suspendWorkPermit(id: number, tenantId: number, reason?: string): Promise<WorkPermitRecord> {
  return request<WorkPermitRecord>(`${WORK_PERMIT_API.base}/${id}/suspend?tenantId=${tenantId}`, {
    method: 'POST',
    body: JSON.stringify({ reason })
  });
}

export async function resumeWorkPermit(id: number, tenantId: number, reason?: string): Promise<WorkPermitRecord> {
  return request<WorkPermitRecord>(`${WORK_PERMIT_API.base}/${id}/resume?tenantId=${tenantId}`, {
    method: 'POST',
    body: JSON.stringify({ reason })
  });
}

export async function acceptWorkPermit(
  id: number,
  tenantId: number,
  payload: { acceptanceResult: string; opinion?: string; signatureText?: string }
): Promise<WorkPermitRecord> {
  return request<WorkPermitRecord>(`${WORK_PERMIT_API.base}/${id}/acceptance`, {
    method: 'POST',
    body: JSON.stringify({ tenantId, ...payload })
  });
}

export async function uploadFile(
  tenantId: number,
  file: File,
  bizType?: string,
  bizId?: number
): Promise<FileObjectRecord> {
  const form = new FormData();
  form.append('tenantId', String(tenantId));
  form.append('file', file);
  if (bizType) form.append('bizType', bizType);
  if (bizId != null) form.append('bizId', String(bizId));
  return uploadRequest<FileObjectRecord>(FILE_API.upload, form);
}

export async function fetchMobileTasks(tenantId: number, userId: number, role = 'ALL'): Promise<MobileTaskRecord[]> {
  return request<MobileTaskRecord[]>(`${MOBILE_API.tasks}?tenantId=${tenantId}&userId=${userId}&role=${role}`);
}

export async function fetchMobileWorkPermitDetail(id: number, tenantId: number): Promise<WorkPermitDetailRecord> {
  return request<WorkPermitDetailRecord>(`${MOBILE_API.workPermit}/${id}?tenantId=${tenantId}`);
}

export async function mobileCheckIn(
  id: number,
  payload: { tenantId: number; locationText?: string; scanCode?: string }
): Promise<unknown> {
  return request(`${MOBILE_API.workPermit}/${id}/check-in`, { method: 'POST', body: JSON.stringify(payload) });
}

export async function mobileGasTest(
  id: number,
  payload: { tenantId: number; gasName: string; qualified: boolean; measuredValue?: string }
): Promise<GasTestRecord> {
  return request<GasTestRecord>(`${MOBILE_API.workPermit}/${id}/gas-tests`, {
    method: 'POST',
    body: JSON.stringify({ ...payload, testedAt: new Date().toISOString().slice(0, 19).replace('T', ' ') })
  });
}

export async function mobileConfirmMeasure(
  id: number,
  payload: { tenantId: number; measureId: number; remark?: string }
): Promise<SafetyMeasureRecord> {
  return request<SafetyMeasureRecord>(`${MOBILE_API.workPermit}/${id}/measures/confirm`, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function mobileSitePermit(
  id: number,
  payload: { tenantId: number; signatureText?: string; locationText?: string }
): Promise<WorkPermitRecord> {
  return request<WorkPermitRecord>(`${MOBILE_API.workPermit}/${id}/site-permit`, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function mobileMonitorRecord(
  id: number,
  payload: { tenantId: number; recordType: string; content: string; abnormalFlag?: boolean }
): Promise<unknown> {
  return request(`${MOBILE_API.workPermit}/${id}/monitor-records`, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function mobileSuspend(id: number, tenantId: number, reason?: string): Promise<WorkPermitRecord> {
  return request<WorkPermitRecord>(`${MOBILE_API.workPermit}/${id}/suspend?tenantId=${tenantId}`, {
    method: 'POST',
    body: JSON.stringify({ reason })
  });
}

export async function mobileResume(id: number, tenantId: number, reason?: string): Promise<WorkPermitRecord> {
  return request<WorkPermitRecord>(`${MOBILE_API.workPermit}/${id}/resume?tenantId=${tenantId}`, {
    method: 'POST',
    body: JSON.stringify({ reason })
  });
}

export async function mobileAcceptance(
  id: number,
  payload: { tenantId: number; acceptanceResult: string; signatureText?: string; opinion?: string }
): Promise<WorkPermitRecord> {
  return request<WorkPermitRecord>(`${MOBILE_API.workPermit}/${id}/acceptance`, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function mobileAlarmFeedback(id: number, tenantId: number, remark?: string): Promise<unknown> {
  return request(`${MOBILE_API.alarmFeedback}/${id}/feedback?tenantId=${tenantId}`, {
    method: 'POST',
    body: JSON.stringify({ remark })
  });
}

export async function mobileUploadFile(tenantId: number, file: File, bizType?: string, bizId?: number): Promise<FileObjectRecord> {
  const form = new FormData();
  form.append('tenantId', String(tenantId));
  form.append('file', file);
  if (bizType) form.append('bizType', bizType);
  if (bizId != null) form.append('bizId', String(bizId));
  const uploaded = await uploadRequest<{ fileId: string; fileName: string; url?: string; sizeBytes?: number }>(
    MOBILE_API.fileUpload,
    form
  );
  return {
    id: Number(uploaded.fileId),
    tenantId,
    fileName: uploaded.fileName,
    sizeBytes: uploaded.sizeBytes,
    downloadUrl: uploaded.url
  };
}

export async function syncMobileDraft(payload: {
  tenantId: number;
  userId: number;
  draftType: string;
  payloadJson: string;
}): Promise<unknown> {
  return request(MOBILE_API.draftsSync, { method: 'POST', body: JSON.stringify(payload) });
}

export async function fetchDashboardOverview(tenantId: number): Promise<DashboardOverviewRecord> {
  return request<DashboardOverviewRecord>(`${REPORT_API.dashboardOverview}?tenantId=${tenantId}`);
}

export async function fetchWorkPermitReportSummary(tenantId: number): Promise<WorkPermitReportSummary> {
  return request<WorkPermitReportSummary>(`${REPORT_API.workPermitSummary}?tenantId=${tenantId}`);
}

export async function fetchAlarmReportSummary(tenantId: number): Promise<AlarmReportSummary> {
  return request<AlarmReportSummary>(`${REPORT_API.alarmSummary}?tenantId=${tenantId}`);
}

export async function fetchAlarmReportDetails(tenantId: number): Promise<AlarmReportDetail[]> {
  return request<AlarmReportDetail[]>(`${REPORT_API.alarmDetails}?tenantId=${tenantId}`);
}

export async function fetchMajorHazardReportSummary(tenantId: number): Promise<MajorHazardReportSummary> {
  return request<MajorHazardReportSummary>(`${REPORT_API.majorHazardSummary}?tenantId=${tenantId}`);
}

export async function fetchContractorReportSummary(tenantId: number): Promise<ContractorReportSummary> {
  return request<ContractorReportSummary>(`${REPORT_API.contractorSummary}?tenantId=${tenantId}`);
}

export async function fetchAuditReportSummary(tenantId: number): Promise<AuditReportSummary> {
  return request<AuditReportSummary>(`${REPORT_API.auditSummary}?tenantId=${tenantId}`);
}

export async function fetchWorkPermitReportDetails(tenantId: number): Promise<WorkPermitReportDetail[]> {
  return request<WorkPermitReportDetail[]>(`${REPORT_API.workPermitDetails}?tenantId=${tenantId}`);
}

export async function fetchWorkPermitTrend(tenantId: number, days = 7): Promise<TrendSeriesRecord> {
  return request<TrendSeriesRecord>(`${REPORT_API.workPermitTrend}?tenantId=${tenantId}&days=${days}`);
}

export async function fetchAlarmTrend(tenantId: number, days = 7): Promise<TrendSeriesRecord> {
  return request<TrendSeriesRecord>(`${REPORT_API.alarmTrend}?tenantId=${tenantId}&days=${days}`);
}

export async function createReportExport(
  payload: { tenantId: number; reportType: string; exportFormat?: string; requestedBy?: string }
): Promise<ReportExportTaskRecord> {
  return request<ReportExportTaskRecord>(REPORT_API.export, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function fetchAcceptanceTestCases(tenantId: number): Promise<AcceptanceTestCaseRecord[]> {
  const page = await request<PageResult<AcceptanceTestCaseRecord>>(
    `${REPORT_API.acceptanceCases}?tenantId=${tenantId}&pageNo=1&pageSize=200`
  );
  return page.records;
}

export async function fetchAcceptanceTestRuns(tenantId: number): Promise<AcceptanceTestRunRecord[]> {
  const page = await request<PageResult<AcceptanceTestRunRecord>>(
    `${REPORT_API.acceptanceRuns}?tenantId=${tenantId}&pageNo=1&pageSize=200`
  );
  return page.records;
}

export async function createAcceptanceTestRun(payload: AcceptanceTestRunRequest): Promise<AcceptanceTestRunRecord> {
  return request<AcceptanceTestRunRecord>(REPORT_API.acceptanceRuns, {
    method: 'POST',
    body: JSON.stringify(payload)
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

async function uploadRequest<T>(path: string, form: FormData): Promise<T> {
  const headers = new Headers();
  const token = getAccessToken();
  if (token) {
    headers.set('Authorization', `Bearer ${token}`);
  }
  const response = await fetch(path, { method: 'POST', headers, body: form });
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
