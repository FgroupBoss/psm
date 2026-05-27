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

/** 承包商 API 路径常量（与网关 /api/contractors/** 对齐）。 */
export const CONTRACTOR_API = {
  base: '/api/contractors',
  health: '/api/contractors/health',
  companies: '/api/contractors/companies',
  workers: '/api/contractors/workers',
  eligibilityCheck: '/api/contractors/workers/eligibility-check'
} as const;

/** 重大危险源 API 路径常量（与网关 /api/major-hazards/** 对齐）。 */
export const MAJOR_HAZARD_API = {
  base: '/api/major-hazards',
  health: '/api/major-hazards/health',
  riskContext: '/api/major-hazards/risk-context'
} as const;

/** 报警中心 API 路径常量（与网关 /api/alarms/** 对齐）。 */
export const ALARM_API = {
  base: '/api/alarms',
  health: '/api/alarms/health',
  ingest: '/api/alarms/ingest',
  areaActiveCheck: '/api/alarms/area-active-check'
} as const;

export interface AlarmHealthInfo {
  service: string;
  module: string;
  version: string;
  seedEventCount: number;
}

export interface AlarmEventRecord {
  id: number;
  tenantId: number;
  alarmNo: string;
  sourceType: string;
  sourceCode?: string;
  title: string;
  content?: string;
  alarmLevel: string;
  status: AlarmStatus;
  areaId?: number;
  unitId?: number;
  equipmentId?: number;
  monitorPointId?: number;
  hazardId?: number;
  occurrenceCount?: number;
  firstOccurredAt?: string;
  lastOccurredAt?: string;
}

export interface AlarmOccurrenceRecord {
  id: number;
  occurredAt?: string;
  rawValue?: string;
}

export interface AlarmActionSummaryRecord {
  id: number;
  actionType: string;
  actionContent?: string;
  operatorName?: string;
  operatedAt?: string;
}

export interface AlarmDetailRecord {
  event: AlarmEventRecord;
  occurrences: AlarmOccurrenceRecord[];
  actions: AlarmActionSummaryRecord[];
}

export interface AlarmIngestRequest {
  tenantId: number;
  sourceType: string;
  sourceCode?: string;
  title: string;
  content?: string;
  alarmLevel: string;
  areaId?: number;
  unitId?: number;
  equipmentId?: number;
  monitorPointId?: number;
  hazardId?: number;
  rawValue?: string;
  occurredAt?: string;
}

export interface AlarmActionRequest {
  content?: string;
  assignee?: string;
}

export interface AlarmFalseCloseRequest {
  reason: string;
}

export interface AlarmAreaActiveCheckRequest {
  tenantId: number;
  areaId: number;
  minLevel?: string;
}

export interface AlarmAreaActiveCheckResult {
  hasBlocking: boolean;
  count: number;
  alarms: AlarmEventRecord[];
}

export interface HazardAlarmSummaryRecord {
  id: number;
  alarmNo: string;
  title: string;
  alarmLevel: string;
  status: AlarmStatus;
  occurrenceCount?: number;
  lastOccurredAt?: string;
}

/** 报警状态（批次 2 起用于列表/详情）。 */
export type AlarmStatus =
  | 'NEW'
  | 'CONFIRMED'
  | 'IN_PROGRESS'
  | 'PENDING_REVIEW'
  | 'CLOSED'
  | 'ESCALATED'
  | 'FALSE_CLOSED';

export interface ContractorCompanyRecord {
  id: number;
  tenantId: number;
  companyCode: string;
  companyName: string;
  contactName?: string;
  contactPhone?: string;
  businessScope?: string;
  status: string;
  blacklistFlag?: number;
  remark?: string;
}

export interface ContractorCompanyRequest {
  tenantId: number;
  companyCode: string;
  companyName: string;
  contactName?: string;
  contactPhone?: string;
  businessScope?: string;
  remark?: string;
}

export interface CompanyApproveRequest {
  passed: boolean;
  opinion?: string;
}

export interface CompanyReasonRequest {
  reason: string;
}

export interface ContractorQualificationRecord {
  id: number;
  tenantId: number;
  companyId: number;
  qualType: string;
  qualName: string;
  qualNo?: string;
  validFrom?: string;
  validTo?: string;
  coreFlag?: number;
  fileId?: number;
  status: string;
  expired?: boolean;
  coreExpired?: boolean;
}

export interface ContractorQualificationRequest {
  tenantId: number;
  qualType: string;
  qualName: string;
  qualNo?: string;
  validFrom?: string;
  validTo?: string;
  coreFlag?: boolean;
  fileId?: number;
}

export interface ContractorWorkerRecord {
  id: number;
  tenantId: number;
  companyId: number;
  workerCode: string;
  name: string;
  phoneMasked?: string;
  tradeType?: string;
  accessStatus: string;
  trainingStatus?: string;
  certificateStatus?: string;
  gateCardNo?: string;
  locationTagNo?: string;
  status?: string;
}

export interface ContractorWorkerRequest {
  tenantId: number;
  companyId: number;
  workerCode: string;
  name: string;
  phoneMasked?: string;
  tradeType?: string;
  gateCardNo?: string;
  locationTagNo?: string;
}

export interface WorkerCertificateRecord {
  id: number;
  tenantId: number;
  workerId: number;
  certType: string;
  certNo?: string;
  validFrom?: string;
  validTo?: string;
  fileId?: number;
  status: string;
  expired?: boolean;
}

export interface WorkerCertificateRequest {
  tenantId: number;
  certType: string;
  certNo?: string;
  validFrom?: string;
  validTo?: string;
  fileId?: number;
}

export interface WorkerTrainingRecord {
  id: number;
  tenantId: number;
  workerId: number;
  trainingName: string;
  trainingResult: string;
  validFrom?: string;
  validTo?: string;
  fileId?: number;
  expired?: boolean;
  valid?: boolean;
}

export interface WorkerTrainingRequest {
  tenantId: number;
  trainingName: string;
  trainingResult: string;
  validFrom?: string;
  validTo?: string;
  fileId?: number;
}

export interface WorkerViolationRecord {
  id: number;
  tenantId: number;
  companyId?: number;
  workerId: number;
  violationTime: string;
  violationDesc: string;
  severity?: string;
  rectificationStatus?: string;
}

export interface WorkerViolationRequest {
  tenantId: number;
  companyId?: number;
  violationTime: string;
  violationDesc: string;
  severity?: string;
  rectificationStatus?: string;
}

export interface EligibilityCheckRequest {
  tenantId: number;
  companyId: number;
  workerIds: number[];
  workType?: string;
  checkPoint?: string;
}

export interface EligibilityReason {
  code: string;
  message: string;
  workerId?: number;
}

export interface EligibilityCheckResult {
  passed: boolean;
  reasons?: EligibilityReason[];
}

export interface MajorHazardRecord {
  id: number;
  tenantId: number;
  hazardNo: string;
  name: string;
  hazardType?: string;
  level: string;
  areaId?: number;
  unitId?: number;
  material?: string;
  designCapacity?: string;
  actualCapacity?: string;
  criticalQuantity?: string;
  emergencyPlanId?: number;
  status: string;
  publishedAt?: string;
}

export interface MajorHazardRequest {
  tenantId: number;
  hazardNo: string;
  name: string;
  hazardType?: string;
  level: string;
  areaId?: number;
  unitId?: number;
  material?: string;
  designCapacity?: string;
  actualCapacity?: string;
  criticalQuantity?: string;
  emergencyPlanId?: number;
}

export interface HazardStatusRequest {
  targetStatus: string;
  reason?: string;
}

export interface MajorHazardResponsibilityRecord {
  id: number;
  tenantId: number;
  hazardId: number;
  responsibilityType: string;
  personName: string;
  personPhone?: string;
  personId?: number;
  sortNo?: number;
}

export interface ResponsibilityRequest {
  responsibilityType: string;
  personName: string;
  personPhone?: string;
  personId?: number;
  sortNo?: number;
}

export interface ResponsibilityReplaceRequest {
  responsibilities: ResponsibilityRequest[];
}

export interface HazardPointRecord {
  id: number;
  tenantId: number;
  hazardId: number;
  monitorPointId: number;
  pointCode?: string;
  pointName?: string;
}

export interface HazardPointRequest {
  monitorPointId: number;
  pointCode?: string;
  pointName?: string;
}

export interface HazardAttachmentRecord {
  id: number;
  tenantId: number;
  hazardId: number;
  attachmentType: string;
  fileId: number;
  fileName?: string;
}

export interface HazardAttachmentRequest {
  attachmentType: string;
  fileId: number;
  fileName?: string;
}

export interface RiskContextRequest {
  tenantId: number;
  areaId?: number;
  unitId?: number;
  pointIds?: number[];
}

export interface RiskContextHazardSummary {
  id: number;
  name: string;
  level: string;
  status: string;
}

export interface RiskContextResult {
  areaId?: number;
  unitId?: number;
  hazards: RiskContextHazardSummary[];
  maxLevel?: string;
  blockingAlarm: boolean;
  blockingReason?: string | null;
}

/** 危险工作票 API 路径常量。 */
export const WORK_PERMIT_API = {
  base: '/api/work-permits',
  health: '/api/work-permits/health',
  byHazard: '/api/work-permits/by-hazard'
} as const;

/** 报表与大屏 API 路径常量。 */
export const REPORT_API = {
  health: '/api/reports/health',
  workPermitSummary: '/api/reports/work-permits/summary',
  alarmSummary: '/api/reports/alarms/summary',
  dashboardOverview: '/api/dashboard/overview',
  export: '/api/reports/export',
  acceptanceCases: '/api/acceptance/test-cases',
  acceptanceRuns: '/api/acceptance/test-runs'
} as const;

export const MOBILE_API = {
  tasks: '/api/mobile/tasks',
  draftsSync: '/api/mobile/drafts/sync'
} as const;

export type WorkPermitStatus =
  | 'DRAFT'
  | 'APPROVING'
  | 'RETURNED'
  | 'PENDING_SITE_PERMIT'
  | 'IN_PROGRESS'
  | 'SUSPENDED'
  | 'PENDING_ACCEPTANCE'
  | 'CLOSED';

export interface WorkPermitHealthInfo {
  service: string;
  module: string;
  version: string;
  permitCount: number;
}

export interface WorkPermitRecord {
  id: number;
  tenantId: number;
  permitNo: string;
  workType: string;
  status: WorkPermitStatus;
  title?: string;
  areaId?: number;
  hazardId?: number;
  contractorCompanyId?: number;
  planStartAt?: string;
  planEndAt?: string;
  updatedAt?: string;
}

export interface WorkPermitDetailRecord {
  permit: WorkPermitRecord;
  workers: WorkPermitWorkerRecord[];
  riskAnalysis: RiskAnalysisRecord[];
  safetyMeasures: SafetyMeasureRecord[];
  gasTests: GasTestRecord[];
  timeline: TimelineItemRecord[];
}

export interface WorkPermitWorkerRecord {
  id: number;
  workerType: string;
  workerId?: number;
  workerName: string;
  roleCode?: string;
  companyId?: number;
}

export interface WorkPermitRequest {
  tenantId: number;
  workType: string;
  title?: string;
  workContent?: string;
  areaId?: number;
  unitId?: number;
  equipmentId?: number;
  hazardId?: number;
  contractorCompanyId?: number;
  planStartAt?: string;
  planEndAt?: string;
  supervisorUserId?: number;
  permitIssuerUserId?: number;
  guardianUserId?: number;
}

export interface WorkPermitWorkerRequest {
  workerType: string;
  workerId?: number;
  workerName: string;
  roleCode?: string;
  companyId?: number;
}

export interface RiskAnalysisRecord {
  id: number;
  hazardDesc?: string;
  controlMeasure?: string;
  riskLevel?: string;
}

export interface SafetyMeasureRecord {
  id: number;
  measureCode: string;
  measureName: string;
  requiredFlag?: boolean;
  confirmStatus: string;
}

export interface GasTestRecord {
  id: number;
  gasName: string;
  qualified: boolean;
  testedAt?: string;
}

export interface TimelineItemRecord {
  itemType: string;
  title: string;
  content?: string;
  operatorName?: string;
  occurredAt?: string;
}

export interface PreCheckResult {
  passed: boolean;
  reasons: string[];
}

export interface DashboardOverviewRecord {
  refreshedAt: string;
  dataSources: string[];
  workPermitTotal: number;
  workPermitInProgress: number;
  workPermitPendingPermit: number;
  alarmOpenCount: number;
  alarmClosedRate?: number;
  hazardCount: number;
  contractorOnSite?: number;
}

export interface WorkPermitReportSummary {
  totalCount: number;
  systemHandlingRate?: number;
  sitePermitTraceRate?: number;
  contractorEligibilityCoverage?: number;
  byStatus?: Record<string, number>;
  byWorkType?: Record<string, number>;
}

export interface AcceptanceTestCaseRecord {
  id: number;
  caseCode: string;
  caseName: string;
  moduleName?: string;
  status: string;
}

export interface AcceptanceTestRunRecord {
  id: number;
  caseId: number;
  runResult: string;
  evidenceRef?: string;
  executedAt?: string;
}
