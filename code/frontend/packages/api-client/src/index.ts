import { clearTokens, getAccessToken, saveTokens } from '@psm/auth';
import { CONTRACTOR_API, MAJOR_HAZARD_API, ALARM_API, WORK_PERMIT_API, REPORT_API, FILE_API, MOBILE_API, DUAL_PREVENTION_API, INSPECTION_API, LOCATION_API, VIDEO_API, SIMOPS_API, INTEGRATION_REG_API, PHA_API, MOC_API, PSSR_API, BARRIER_API, INCIDENT_API, GOVERNANCE_API } from '@psm/domain-types';
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
  HotWorkWorkflowSummary,
  HotWorkApprovalProgress,
  HotWorkApprovalTaskRecord,
  PermitActionPayload,
  HeightWorkDetail,
  HeightWorkDetailRequest,
  HeightWorkFlowProgress,
  HeightWorkHazardFactor,
  HeightWorkPreCheckResult,
  HeightWorkProtectionCheckRequest,
  HeightWorkEnvironmentCheckRequest,
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
  MobileTaskRecord,
  MobileFileUploadRecord,
  TimelineItemRecord,
  RiskUnitRecord,
  RiskUnitTreeNode,
  HazardReportRecord,
  HazardStatisticsRecord,
  InspectionPlanRecord,
  InspectionTaskRecord,
  InspectionStatisticsRecord,
  LocTagRecord,
  LocEventRecord,
  VisitorAccessRecord,
  VideoCameraRecord,
  VideoAiEventRecord,
  SimopsConflictRuleRecord,
  SimopsScanResultRecord,
  SimopsStatisticsRecord,
  RegReportTaskRecord,
  Phase2ReportSummaryRecord,
  PhaProjectRecord,
  PhaRecommendationRecord,
  LopaScenarioRecord,
  MiEquipmentRecord,
  MocChangeRecord,
  PssrProjectRecord,
  BarrierRecord,
  IncidentRecord,
  GovernanceDashboardRecord,
  Phase3ReportSummaryRecord
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

export async function fetchAvailableHotWorkWorkflows(
  tenantId: number,
  hotWorkLevel?: string,
  areaId?: number
): Promise<HotWorkWorkflowSummary[]> {
  const params = new URLSearchParams({ tenantId: String(tenantId) });
  if (hotWorkLevel) params.set('hotWorkLevel', hotWorkLevel);
  if (areaId != null) params.set('areaId', String(areaId));
  return request<HotWorkWorkflowSummary[]>(`${WORK_PERMIT_API.hotWorkAvailableWorkflows}?${params.toString()}`);
}

export async function fetchHotWorkApprovalProgress(id: number, tenantId: number): Promise<HotWorkApprovalProgress> {
  return request<HotWorkApprovalProgress>(`${WORK_PERMIT_API.base}/${id}/approval/progress?tenantId=${tenantId}`);
}

export async function fetchHotWorkApprovalTasks(
  id: number,
  tenantId: number,
  assigneeUserId?: number
): Promise<HotWorkApprovalTaskRecord[]> {
  const params = new URLSearchParams({ tenantId: String(tenantId) });
  if (assigneeUserId != null) params.set('assigneeUserId', String(assigneeUserId));
  return request<HotWorkApprovalTaskRecord[]>(`${WORK_PERMIT_API.base}/${id}/approval/tasks/mine?${params.toString()}`);
}

export async function saveHeightWorkDetail(id: number, payload: HeightWorkDetailRequest): Promise<HeightWorkDetail> {
  return request<HeightWorkDetail>(`${WORK_PERMIT_API.heightWorkBase(id)}/detail`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  });
}

export async function fetchHeightWorkFlowProgress(id: number, tenantId: number): Promise<HeightWorkFlowProgress> {
  return request<HeightWorkFlowProgress>(`${WORK_PERMIT_API.heightWorkBase(id)}/flow-progress?tenantId=${tenantId}`);
}

export async function addHeightWorkHazardFactor(
  id: number,
  tenantId: number,
  payload: { factorCode: string; factorName?: string; controlMeasure?: string }
): Promise<HeightWorkHazardFactor> {
  return request<HeightWorkHazardFactor>(`${WORK_PERMIT_API.heightWorkBase(id)}/hazard-factors`, {
    method: 'POST',
    body: JSON.stringify({ tenantId, hitSource: 'MANUAL', ...payload })
  });
}

export async function confirmHeightWorkHazardFactor(id: number, tenantId: number, factorId: number): Promise<HeightWorkHazardFactor> {
  return request<HeightWorkHazardFactor>(
    `${WORK_PERMIT_API.heightWorkBase(id)}/hazard-factors/${factorId}/confirm?tenantId=${tenantId}`,
    { method: 'POST' }
  );
}

export async function addHeightWorkProtectionCheck(
  id: number,
  payload: HeightWorkProtectionCheckRequest
): Promise<unknown> {
  return request(`${WORK_PERMIT_API.heightWorkBase(id)}/protection-checks`, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function addHeightWorkEnvironmentCheck(
  id: number,
  payload: HeightWorkEnvironmentCheckRequest
): Promise<unknown> {
  return request(`${WORK_PERMIT_API.heightWorkBase(id)}/environment-checks`, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function heightWorkPreCheck(id: number, tenantId: number, checkPoint: string): Promise<HeightWorkPreCheckResult> {
  return request<HeightWorkPreCheckResult>(
    `${WORK_PERMIT_API.heightWorkBase(id)}/pre-check?tenantId=${tenantId}&checkPoint=${checkPoint}`,
    { method: 'POST' }
  );
}

export async function saveConfinedSpaceDetail(id: number, payload: Record<string, unknown>): Promise<unknown> {
  return request(`${WORK_PERMIT_API.base}/${id}/confined-space/detail`, { method: 'PUT', body: JSON.stringify(payload) });
}

export async function saveLiftingDetail(id: number, payload: Record<string, unknown>): Promise<unknown> {
  return request(`${WORK_PERMIT_API.base}/${id}/lifting/detail`, { method: 'PUT', body: JSON.stringify(payload) });
}

export async function saveTempElectricDetail(id: number, payload: Record<string, unknown>): Promise<unknown> {
  return request(`${WORK_PERMIT_API.base}/${id}/temporary-electric/detail`, { method: 'PUT', body: JSON.stringify(payload) });
}

export async function saveBlindPlateDetail(id: number, payload: Record<string, unknown>): Promise<unknown> {
  return request(`${WORK_PERMIT_API.base}/${id}/blind-plate/detail`, { method: 'PUT', body: JSON.stringify(payload) });
}

export async function createBlindPlateRegistry(payload: Record<string, unknown>): Promise<{ id: number }> {
  return request('/api/blind-plates', { method: 'POST', body: JSON.stringify(payload) });
}

export async function saveExcavationDetail(id: number, payload: Record<string, unknown>): Promise<unknown> {
  return request(`${WORK_PERMIT_API.base}/${id}/excavation/detail`, { method: 'PUT', body: JSON.stringify(payload) });
}

export async function saveRoadBreakDetail(id: number, payload: Record<string, unknown>): Promise<unknown> {
  return request(`${WORK_PERMIT_API.base}/${id}/road-break/detail`, { method: 'PUT', body: JSON.stringify(payload) });
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

export async function approveWorkPermit(
  id: number,
  tenantId: number,
  payload?: string | PermitActionPayload
): Promise<WorkPermitRecord> {
  const body = typeof payload === 'string' ? { opinion: payload, action: 'APPROVE' as const } : { action: 'APPROVE' as const, ...payload };
  return request<WorkPermitRecord>(`${WORK_PERMIT_API.base}/${id}/approve?tenantId=${tenantId}`, {
    method: 'POST',
    body: JSON.stringify(body)
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
  const uploaded = await uploadRequest<MobileFileUploadRecord>(MOBILE_API.fileUpload, form);
  return {
    id: Number(uploaded.fileId),
    tenantId,
    fileName: uploaded.fileName,
    contentType: uploaded.contentType,
    sizeBytes: uploaded.sizeBytes,
    sha256: uploaded.sha256,
    downloadUrl: uploaded.url
  };
}

export async function fetchWorkPermitTimeline(id: number, tenantId: number): Promise<TimelineItemRecord[]> {
  return request<TimelineItemRecord[]>(`${WORK_PERMIT_API.base}/${id}/timeline?tenantId=${tenantId}`);
}

export async function fetchReportExportTask(tenantId: number, taskId: number): Promise<ReportExportTaskRecord> {
  return request<ReportExportTaskRecord>(`${REPORT_API.export}/${taskId}?tenantId=${tenantId}`);
}

export async function downloadReportExport(tenantId: number, taskId: number, fileName: string): Promise<void> {
  const headers = new Headers();
  const token = getAccessToken();
  if (token) {
    headers.set('Authorization', `Bearer ${token}`);
  }
  const response = await fetch(`${REPORT_API.exportDownload}/${taskId}/download?tenantId=${tenantId}`, { headers });
  if (!response.ok) {
    throw new Error(await resolveErrorMessage(response));
  }
  const blob = await response.blob();
  const url = URL.createObjectURL(blob);
  const anchor = document.createElement('a');
  anchor.href = url;
  anchor.download = fileName;
  anchor.click();
  URL.revokeObjectURL(url);
}

export async function rejectWorkPermit(
  id: number,
  tenantId: number,
  payload?: string | PermitActionPayload
): Promise<WorkPermitRecord> {
  const body = typeof payload === 'string' ? { reason: payload, action: 'REJECT' as const } : { action: 'REJECT' as const, ...payload };
  return request<WorkPermitRecord>(`${WORK_PERMIT_API.base}/${id}/reject?tenantId=${tenantId}`, {
    method: 'POST',
    body: JSON.stringify(body)
  });
}

export async function returnWorkPermit(
  id: number,
  tenantId: number,
  payload?: string | PermitActionPayload
): Promise<WorkPermitRecord> {
  const body = typeof payload === 'string' ? { reason: payload, action: 'RETURN' as const } : { action: 'RETURN' as const, ...payload };
  return request<WorkPermitRecord>(`${WORK_PERMIT_API.base}/${id}/return?tenantId=${tenantId}`, {
    method: 'POST',
    body: JSON.stringify(body)
  });
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

// --- Phase 2 APIs ---

export async function fetchRiskUnits(params: {
  tenantId: number;
  keyword?: string;
  status?: string;
  pageNo?: number;
  pageSize?: number;
}): Promise<PageResult<RiskUnitRecord>> {
  const q = new URLSearchParams({ tenantId: String(params.tenantId), pageNo: String(params.pageNo || 1), pageSize: String(params.pageSize || 20) });
  if (params.keyword) q.set('keyword', params.keyword);
  if (params.status) q.set('status', params.status);
  return request<PageResult<RiskUnitRecord>>(`${DUAL_PREVENTION_API.riskUnits}?${q}`);
}

export async function fetchRiskUnitTree(tenantId: number, areaId?: number): Promise<RiskUnitTreeNode[]> {
  const q = new URLSearchParams({ tenantId: String(tenantId) });
  if (areaId) q.set('areaId', String(areaId));
  return request<RiskUnitTreeNode[]>(`${DUAL_PREVENTION_API.riskUnits}/tree?${q}`);
}

export async function fetchRiskColorMap(tenantId: number, areaId?: number): Promise<Record<string, number>> {
  const q = new URLSearchParams({ tenantId: String(tenantId) });
  if (areaId) q.set('areaId', String(areaId));
  return request<Record<string, number>>(`${DUAL_PREVENTION_API.riskUnits}/color-map?${q}`);
}

export async function fetchHazards(params: {
  tenantId: number;
  keyword?: string;
  status?: string;
  overdueFlag?: number;
  pageNo?: number;
  pageSize?: number;
}): Promise<PageResult<HazardReportRecord>> {
  const q = new URLSearchParams({ tenantId: String(params.tenantId), pageNo: String(params.pageNo || 1), pageSize: String(params.pageSize || 20) });
  if (params.keyword) q.set('keyword', params.keyword);
  if (params.status) q.set('status', params.status);
  if (params.overdueFlag != null) q.set('overdueFlag', String(params.overdueFlag));
  return request<PageResult<HazardReportRecord>>(`${DUAL_PREVENTION_API.hazards}?${q}`);
}

export async function fetchHazardDetail(id: number, tenantId: number): Promise<HazardReportRecord> {
  return request<HazardReportRecord>(`${DUAL_PREVENTION_API.hazards}/${id}?tenantId=${tenantId}`);
}

export async function fetchHazardStatistics(tenantId: number, areaId?: number): Promise<HazardStatisticsRecord> {
  const q = new URLSearchParams({ tenantId: String(tenantId) });
  if (areaId) q.set('areaId', String(areaId));
  return request<HazardStatisticsRecord>(`${DUAL_PREVENTION_API.hazards}/statistics?${q}`);
}

export async function createHazard(payload: {
  tenantId: number;
  hazardLevel: string;
  sourceType: string;
  description: string;
  areaId?: number;
  riskUnitId?: number;
  sourceBizId?: number;
}): Promise<HazardReportRecord> {
  return request<HazardReportRecord>(DUAL_PREVENTION_API.hazards, { method: 'POST', body: JSON.stringify(payload) });
}

export async function confirmHazard(id: number, payload: { tenantId: number; assigneeUserId?: number; hazardLevel?: string; content?: string }): Promise<HazardReportRecord> {
  return request<HazardReportRecord>(`${DUAL_PREVENTION_API.hazards}/${id}/confirm`, { method: 'POST', body: JSON.stringify(payload) });
}

export async function rectifyHazard(id: number, payload: { tenantId: number; content: string }): Promise<HazardReportRecord> {
  return request<HazardReportRecord>(`${DUAL_PREVENTION_API.hazards}/${id}/rectify`, { method: 'POST', body: JSON.stringify(payload) });
}

export async function reviewHazard(id: number, payload: { tenantId: number; passed: boolean; content?: string }): Promise<HazardReportRecord> {
  return request<HazardReportRecord>(`${DUAL_PREVENTION_API.hazards}/${id}/review`, { method: 'POST', body: JSON.stringify(payload) });
}

export async function escalateHazard(id: number, payload: { tenantId: number; reason?: string; notifyUserId?: number }): Promise<HazardReportRecord> {
  return request<HazardReportRecord>(`${DUAL_PREVENTION_API.hazards}/${id}/escalate`, { method: 'POST', body: JSON.stringify(payload) });
}

export async function fetchInspectionPlans(tenantId: number, pageNo = 1, pageSize = 20): Promise<PageResult<InspectionPlanRecord>> {
  return request<PageResult<InspectionPlanRecord>>(`${INSPECTION_API.plans}?tenantId=${tenantId}&pageNo=${pageNo}&pageSize=${pageSize}`);
}

export async function fetchInspectionTasks(params: {
  tenantId: number;
  status?: string;
  executorId?: number;
  pageNo?: number;
  pageSize?: number;
}): Promise<PageResult<InspectionTaskRecord>> {
  const q = new URLSearchParams({ tenantId: String(params.tenantId), pageNo: String(params.pageNo || 1), pageSize: String(params.pageSize || 20) });
  if (params.status) q.set('status', params.status);
  if (params.executorId) q.set('executorId', String(params.executorId));
  return request<PageResult<InspectionTaskRecord>>(`${INSPECTION_API.tasks}?${q}`);
}

export async function fetchInspectionTaskDetail(id: number, tenantId: number): Promise<InspectionTaskRecord> {
  return request<InspectionTaskRecord>(`${INSPECTION_API.tasks}/${id}?tenantId=${tenantId}`);
}

export async function startInspectionTask(id: number, payload: { tenantId: number; executorId?: number }): Promise<InspectionTaskRecord> {
  return request<InspectionTaskRecord>(`${INSPECTION_API.tasks}/${id}/start`, { method: 'POST', body: JSON.stringify(payload) });
}

export async function signInInspectionTask(id: number, payload: { tenantId: number; routePointId: number; signType: string; signCode?: string }): Promise<unknown> {
  return request(`${INSPECTION_API.tasks}/${id}/sign-in`, { method: 'POST', body: JSON.stringify(payload) });
}

export async function completeInspectionTask(id: number, tenantId: number): Promise<InspectionTaskRecord> {
  return request<InspectionTaskRecord>(`${INSPECTION_API.tasks}/${id}/complete?tenantId=${tenantId}`, { method: 'POST' });
}

export async function registerInspectionAbnormal(id: number, payload: {
  tenantId: number;
  abnormalDesc: string;
  photoUrls: string;
  createHazard?: boolean;
  severity?: string;
  routePointId?: number;
}): Promise<unknown> {
  return request(`${INSPECTION_API.tasks}/${id}/abnormals`, { method: 'POST', body: JSON.stringify(payload) });
}

export async function fetchInspectionStatistics(tenantId: number): Promise<InspectionStatisticsRecord> {
  return request<InspectionStatisticsRecord>(`${INSPECTION_API.statistics}?tenantId=${tenantId}`);
}

export async function syncInspectionDraft(payload: { tenantId: number; taskId?: number; clientDraftId: string; payloadJson: string }): Promise<unknown> {
  return request(`${INSPECTION_API.tasks}/draft-sync`, { method: 'POST', body: JSON.stringify(payload) });
}

export async function fetchLocTags(tenantId: number, pageNo = 1, pageSize = 20): Promise<PageResult<LocTagRecord>> {
  return request<PageResult<LocTagRecord>>(`${LOCATION_API.tags}?tenantId=${tenantId}&pageNo=${pageNo}&pageSize=${pageSize}`);
}

export async function fetchLocEvents(tenantId: number, pageNo = 1, pageSize = 20): Promise<PageResult<LocEventRecord>> {
  return request<PageResult<LocEventRecord>>(`${LOCATION_API.events}?tenantId=${tenantId}&pageNo=${pageNo}&pageSize=${pageSize}`);
}

export async function fetchVisitorRecords(tenantId: number, pageNo = 1, pageSize = 20): Promise<PageResult<VisitorAccessRecord>> {
  return request<PageResult<VisitorAccessRecord>>(`${LOCATION_API.visitors}?tenantId=${tenantId}&pageNo=${pageNo}&pageSize=${pageSize}`);
}

export async function fetchAreaHeadcount(tenantId: number, areaId: number): Promise<{ areaId: number; headcount: number }> {
  return request<{ areaId: number; headcount: number }>(`${LOCATION_API.headcount}/${areaId}/headcount?tenantId=${tenantId}`);
}

export async function fetchVideoCameras(tenantId: number, pageNo = 1, pageSize = 20): Promise<PageResult<VideoCameraRecord>> {
  return request<PageResult<VideoCameraRecord>>(`${VIDEO_API.cameras}?tenantId=${tenantId}&pageNo=${pageNo}&pageSize=${pageSize}`);
}

export async function fetchVideoAiEvents(tenantId: number, pageNo = 1, pageSize = 20, status?: string): Promise<PageResult<VideoAiEventRecord>> {
  const q = new URLSearchParams({ tenantId: String(tenantId), pageNo: String(pageNo), pageSize: String(pageSize) });
  if (status) q.set('status', status);
  return request<PageResult<VideoAiEventRecord>>(`${VIDEO_API.aiEvents}?${q}`);
}

export async function videoAiEventToAlarm(id: number, tenantId: number): Promise<VideoAiEventRecord> {
  return request<VideoAiEventRecord>(`${VIDEO_API.aiEvents}/${id}/to-alarm?tenantId=${tenantId}`, { method: 'POST' });
}

export async function ignoreVideoAiEvent(id: number, tenantId: number, reason: string): Promise<VideoAiEventRecord> {
  return request<VideoAiEventRecord>(`${VIDEO_API.aiEvents}/${id}/ignore?tenantId=${tenantId}`, {
    method: 'POST',
    body: JSON.stringify({ reason })
  });
}

export async function fetchSimopsRules(tenantId: number): Promise<SimopsConflictRuleRecord[]> {
  return request<SimopsConflictRuleRecord[]>(`${SIMOPS_API.rules}?tenantId=${tenantId}`);
}

export async function fetchSimopsConflicts(tenantId: number, pageNo = 1, pageSize = 20): Promise<PageResult<SimopsScanResultRecord>> {
  return request<PageResult<SimopsScanResultRecord>>(`${SIMOPS_API.conflicts}?tenantId=${tenantId}&pageNo=${pageNo}&pageSize=${pageSize}`);
}

export async function fetchSimopsStatistics(tenantId: number): Promise<SimopsStatisticsRecord> {
  return request<SimopsStatisticsRecord>(`${SIMOPS_API.statistics}?tenantId=${tenantId}`);
}

export async function coordinateSimopsConflict(id: number, payload: { tenantId: number; decision: string; opinion?: string }): Promise<unknown> {
  return request(`${SIMOPS_API.base}/conflicts/${id}/coordinate`, { method: 'POST', body: JSON.stringify(payload) });
}

export async function fetchRegReportTasks(tenantId: number, pageNo = 1, pageSize = 20): Promise<PageResult<RegReportTaskRecord>> {
  return request<PageResult<RegReportTaskRecord>>(`${INTEGRATION_REG_API.tasks}?tenantId=${tenantId}&pageNo=${pageNo}&pageSize=${pageSize}`);
}

export async function triggerRegReport(payload: { tenantId: number; platformCode: string; dataDomain: string }): Promise<RegReportTaskRecord> {
  return request<RegReportTaskRecord>(`${INTEGRATION_REG_API.tasks}/trigger`, { method: 'POST', body: JSON.stringify(payload) });
}

export async function retryRegReportTask(id: number, tenantId: number): Promise<RegReportTaskRecord> {
  return request<RegReportTaskRecord>(`${INTEGRATION_REG_API.tasks}/${id}/retry?tenantId=${tenantId}`, { method: 'POST' });
}

export async function fetchPhase2ReportSummary(tenantId: number): Promise<Phase2ReportSummaryRecord> {
  return request<Phase2ReportSummaryRecord>(`${REPORT_API.phase2Summary}?tenantId=${tenantId}`);
}

export async function fetchPhaProjects(tenantId: number, pageNo = 1, pageSize = 20): Promise<PageResult<PhaProjectRecord>> {
  return request<PageResult<PhaProjectRecord>>(
    `${PHA_API.projects}?tenantId=${tenantId}&pageNo=${pageNo}&pageSize=${pageSize}`
  );
}

export async function createPhaProject(payload: {
  tenantId: number;
  projectName: string;
  method?: string;
  unitId?: number;
}): Promise<PhaProjectRecord> {
  return request<PhaProjectRecord>(PHA_API.projects, { method: 'POST', body: JSON.stringify(payload) });
}

export async function submitPhaProject(id: number, tenantId: number): Promise<PhaProjectRecord> {
  return request<PhaProjectRecord>(`${PHA_API.projects}/${id}/submit?tenantId=${tenantId}`, { method: 'POST' });
}

export async function publishPhaProject(id: number, tenantId: number): Promise<PhaProjectRecord> {
  return request<PhaProjectRecord>(`${PHA_API.projects}/${id}/publish?tenantId=${tenantId}`, { method: 'POST' });
}

export async function fetchPhaRecommendations(
  tenantId: number,
  projectId?: number,
  pageNo = 1,
  pageSize = 20
): Promise<PageResult<PhaRecommendationRecord>> {
  const params = new URLSearchParams({ tenantId: String(tenantId), pageNo: String(pageNo), pageSize: String(pageSize) });
  if (projectId != null) {
    params.set('projectId', String(projectId));
  }
  return request<PageResult<PhaRecommendationRecord>>(`${PHA_API.recommendations}?${params.toString()}`);
}

export async function fetchLopaScenarios(
  tenantId: number,
  projectId?: number,
  pageNo = 1,
  pageSize = 20
): Promise<PageResult<LopaScenarioRecord>> {
  const params = new URLSearchParams({ tenantId: String(tenantId), pageNo: String(pageNo), pageSize: String(pageSize) });
  if (projectId != null) {
    params.set('projectId', String(projectId));
  }
  return request<PageResult<LopaScenarioRecord>>(`${PHA_API.lopaScenarios}?${params.toString()}`);
}

export async function calculateLopaScenario(id: number, tenantId: number): Promise<unknown> {
  return request(`${PHA_API.lopaScenarios}/${id}/calculate?tenantId=${tenantId}`, { method: 'POST' });
}

export async function fetchMiEquipment(tenantId: number, pageNo = 1, pageSize = 20): Promise<PageResult<MiEquipmentRecord>> {
  return request<PageResult<MiEquipmentRecord>>(
    `${BARRIER_API.mechanicalIntegrity}/equipment?tenantId=${tenantId}&pageNo=${pageNo}&pageSize=${pageSize}`
  );
}

export async function closePhaRecommendation(id: number, tenantId: number): Promise<PhaRecommendationRecord> {
  return request<PhaRecommendationRecord>(`${PHA_API.recommendations}/${id}/close?tenantId=${tenantId}`, {
    method: 'POST',
    body: JSON.stringify({ tenantId, content: 'Web关闭' })
  });
}

export async function fetchMocChanges(tenantId: number, pageNo = 1, pageSize = 20): Promise<PageResult<MocChangeRecord>> {
  return request<PageResult<MocChangeRecord>>(
    `${MOC_API.changes}?tenantId=${tenantId}&pageNo=${pageNo}&pageSize=${pageSize}`
  );
}

export async function createMocChange(payload: {
  tenantId: number;
  title: string;
  changeType?: string;
  changeLevel?: string;
}): Promise<MocChangeRecord> {
  return request<MocChangeRecord>(MOC_API.changes, { method: 'POST', body: JSON.stringify(payload) });
}

export async function submitMocChange(id: number, tenantId: number): Promise<MocChangeRecord> {
  return request<MocChangeRecord>(`${MOC_API.changes}/${id}/submit?tenantId=${tenantId}`, { method: 'POST' });
}

export async function closeMocChange(id: number, tenantId: number): Promise<MocChangeRecord> {
  return request<MocChangeRecord>(`${MOC_API.changes}/${id}/close?tenantId=${tenantId}`, { method: 'POST' });
}

export async function fetchPssrProjects(tenantId: number, pageNo = 1, pageSize = 20): Promise<PageResult<PssrProjectRecord>> {
  return request<PageResult<PssrProjectRecord>>(
    `${PSSR_API.projects}?tenantId=${tenantId}&pageNo=${pageNo}&pageSize=${pageSize}`
  );
}

export async function approvePssrStartup(id: number, tenantId: number): Promise<PssrProjectRecord> {
  return request<PssrProjectRecord>(`${PSSR_API.projects}/${id}/approve-startup?tenantId=${tenantId}`, { method: 'POST' });
}

export async function fetchBarriers(tenantId: number, pageNo = 1, pageSize = 20): Promise<PageResult<BarrierRecord>> {
  return request<PageResult<BarrierRecord>>(
    `${BARRIER_API.barriers}?tenantId=${tenantId}&pageNo=${pageNo}&pageSize=${pageSize}`
  );
}

export async function degradeBarrier(id: number, tenantId: number, reason?: string): Promise<BarrierRecord> {
  return request<BarrierRecord>(`${BARRIER_API.barriers}/${id}/degrade?tenantId=${tenantId}`, {
    method: 'POST',
    body: JSON.stringify({ tenantId, reason: reason || 'Web降级' })
  });
}

export async function fetchIncidents(tenantId: number, pageNo = 1, pageSize = 20): Promise<PageResult<IncidentRecord>> {
  return request<PageResult<IncidentRecord>>(
    `${INCIDENT_API.base}?tenantId=${tenantId}&pageNo=${pageNo}&pageSize=${pageSize}`
  );
}

export async function startIncidentInvestigation(id: number, tenantId: number): Promise<IncidentRecord> {
  return request<IncidentRecord>(`${INCIDENT_API.base}/${id}/start-investigation?tenantId=${tenantId}`, { method: 'POST' });
}

export async function fetchGovernanceDashboard(tenantId: number): Promise<GovernanceDashboardRecord> {
  return request<GovernanceDashboardRecord>(`${GOVERNANCE_API.dashboard}?tenantId=${tenantId}`);
}

export async function fetchPhase3ReportSummary(tenantId: number): Promise<Phase3ReportSummaryRecord> {
  return request<Phase3ReportSummaryRecord>(`${REPORT_API.phase3Summary}?tenantId=${tenantId}`);
}
