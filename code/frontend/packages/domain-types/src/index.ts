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

/** 网关统一入口（开发环境 Vite 代理目标，生产环境同源部署）。 */
export const GATEWAY_URL = 'http://localhost:18080';

/** 认证 API（由 身份与接入域 psm-identity-api 提供）。 */
export const AUTH_API = {
  login: '/auth/login',
  me: '/auth/me',
  refresh: '/auth/refresh',
  logout: '/auth/logout'
} as const;

// ============================================================
// 领域1: 身份与接入域 (psm-identity-api) — auth / iam / audit / file / notification / masterdata / gateway
// ============================================================

/** IAM 用户/角色/组织/菜单 API。 */
export const IAM_API = {
  users: '/api/iam/users',
  roles: '/api/iam/roles',
  orgs: '/api/iam/orgs',
  menus: '/api/iam/menus',
  permissions: '/api/iam/permissions'
} as const;

/** 审计日志 API。 */
export const AUDIT_API = {
  logs: '/api/audit/logs'
} as const;

/** 基础主数据 API（区域/装置/设备/监测点位）。 */
export const MASTER_DATA_API = {
  base: '/api/master-data',
  areas: '/api/areas',
  units: '/api/units',
  equipments: '/api/equipments',
  monitorPoints: '/api/monitor-points'
} as const;

/** 文件中心 API（身份与接入域 — psm-identity-api）。 */
export const FILE_API = {
  health: '/api/files/health',
  upload: '/api/files/upload',
  base: '/api/files'
} as const;

/** 消息通知 API。 */
export const NOTIFICATION_API = {
  base: '/api/notifications'
} as const;

/** 配置规则 API。 */
export const CONFIG_API = {
  base: '/api/config'
} as const;

// ============================================================
// 领域2: 作业管控域 (psm-operation-api) — work-permit / contractor / mobile-bff / simops
// ============================================================
/** 承包商 API 路径常量（作业管控域 — psm-operation-api）。 */
export const CONTRACTOR_API = {
  base: '/api/contractors',
  health: '/api/contractors/health',
  companies: '/api/contractors/companies',
  workers: '/api/contractors/workers',
  eligibilityCheck: '/api/contractors/workers/eligibility-check'
} as const;

/** 重大危险源 API（风险防控域 — psm-risk-api）。 */
export const MAJOR_HAZARD_API = {
  base: '/api/major-hazards',
  health: '/api/major-hazards/health',
  riskContext: '/api/major-hazards/risk-context'
} as const;

/** 报警中心 API（实时感知域 — psm-realtime-api）。 */
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

/** 危险工作票 API（作业管控域 — psm-operation-api）。 */
export const WORK_PERMIT_API = {
  base: '/api/work-permits',
  health: '/api/work-permits/health',
  byHazard: '/api/work-permits/by-hazard',
  hotWorkAvailableWorkflows: '/api/work-permits/hot-work/available-workflows',
  heightWorkBase: (id: number) => `/api/work-permits/${id}/height-work`
} as const;

/** 报表与大屏 API（事件治理域 — psm-incident-governance-api）。 */
export const REPORT_API = {
  health: '/api/reports/health',
  workPermitSummary: '/api/reports/work-permits/summary',
  workPermitDetails: '/api/reports/work-permits/details',
  alarmSummary: '/api/reports/alarms/summary',
  alarmDetails: '/api/reports/alarms/details',
  majorHazardSummary: '/api/reports/major-hazards/summary',
  contractorSummary: '/api/reports/contractors/summary',
  auditSummary: '/api/reports/audit/summary',
  dashboardOverview: '/api/dashboard/overview',
  workPermitTrend: '/api/dashboard/work-permits/trend',
  alarmTrend: '/api/dashboard/alarms/trend',
  export: '/api/reports/export',
  exportDownload: '/api/reports/export',
  acceptanceCases: '/api/acceptance/test-cases',
  acceptanceRuns: '/api/acceptance/test-runs',
  phase2Summary: '/api/reports/phase2/summary',
  phase3Summary: '/api/reports/phase3/summary'
} as const;

/** 移动端 BFF API（作业管控域 — psm-operation-api）。 */
export const MOBILE_API = {
  tasks: '/api/mobile/tasks',
  workPermit: '/api/mobile/work-permits',
  draftsSync: '/api/mobile/drafts/sync',
  fileUpload: '/api/mobile/files/upload',
  alarmFeedback: '/api/mobile/alarms'
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
  hotWorkLevel?: string;
  workflowTemplateId?: number;
  workflowTemplateVersion?: number;
  workflowTemplateName?: string;
  updatedAt?: string;
}

export interface HeightWorkPreCheckResult {
  passed: boolean;
  checkPoint?: string;
  ruleVersion?: string;
  weatherSampledAt?: string;
  reasons: HeightWorkPreCheckReason[];
}

export interface HeightWorkPreCheckReason {
  code: string;
  level: string;
  message: string;
}

export interface HeightWorkDetail {
  id?: number;
  workPermitId?: number;
  workHeightM: number;
  fallDatumDescription: string;
  workLocation: string;
  workMethod: string;
  heightLevel?: string;
  riskClass?: string;
  manualUpgradeFlag?: boolean;
  manualUpgradeReason?: string;
  rescuePlanRef?: string;
  rescueContact?: string;
  communicationConfirmed?: boolean;
  ruleVersion?: string;
  validUntil?: string;
}

export interface HeightWorkHazardFactor {
  id: number;
  factorCode: string;
  factorName: string;
  hitSource?: string;
  controlMeasure?: string;
  confirmedAt?: string;
}

export interface HeightWorkFlowNode {
  nodeCode: string;
  nodeName: string;
  status: string;
  current: boolean;
}

export interface HeightWorkFlowProgress {
  permitStatus?: string;
  heightLevel?: string;
  riskClass?: string;
  validUntil?: string;
  nodes: HeightWorkFlowNode[];
}

export interface HeightWorkDetailRequest {
  tenantId: number;
  workHeightM: number;
  fallDatumDescription: string;
  workLocation: string;
  workMethod: string;
  manualUpgradeFlag?: boolean;
  manualUpgradeReason?: string;
  rescuePlanRef?: string;
  rescueContact?: string;
  communicationConfirmed?: boolean;
}

export interface HeightWorkProtectionCheckRequest {
  tenantId: number;
  checkStage: string;
  itemCode: string;
  itemName: string;
  checkResult: string;
  locationText?: string;
}

export interface HeightWorkEnvironmentCheckRequest {
  tenantId: number;
  checkStage: string;
  windLevel?: string;
  weatherType?: string;
  checkResult: string;
  reviewReason?: string;
  dataSource?: string;
}

export const WORK_TYPE_LABELS: Record<string, string> = {
  HOT_WORK: '动火',
  CONFINED_SPACE: '受限空间',
  BLIND_PLATE: '盲板抽堵',
  HEIGHT_WORK: '高处',
  LIFTING: '吊装',
  TEMPORARY_ELECTRIC: '临时用电',
  EXCAVATION: '动土',
  ROAD_BREAK: '断路'
};

export interface WorkPermitDetailRecord {
  permit: WorkPermitRecord;
  workers: WorkPermitWorkerRecord[];
  riskAnalysis: RiskAnalysisRecord[];
  safetyMeasures: SafetyMeasureRecord[];
  gasTests: GasTestRecord[];
  timeline: TimelineItemRecord[];
  heightWorkDetail?: HeightWorkDetail;
  heightWorkHazardFactors?: HeightWorkHazardFactor[];
  heightWorkFlowProgress?: HeightWorkFlowProgress;
  confinedSpaceDetail?: Record<string, unknown>;
  confinedSpaceFlowProgress?: HeightWorkFlowProgress;
  liftingDetail?: Record<string, unknown>;
  liftingFlowProgress?: HeightWorkFlowProgress;
  tempElectricDetail?: Record<string, unknown>;
  tempElectricFlowProgress?: HeightWorkFlowProgress;
  blindPlateDetail?: Record<string, unknown>;
  excavationDetail?: Record<string, unknown>;
  roadBreakDetail?: Record<string, unknown>;
  roadBreakTrafficPlan?: Record<string, unknown>;
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
  hotWorkLevel?: string;
  workflowTemplateId?: number;
  workflowTemplateVersion?: number;
  workflowTemplateName?: string;
}

export type HotWorkLevel = 'SPECIAL' | 'LEVEL_1' | 'LEVEL_2';

export interface HotWorkWorkflowSummary {
  id: number;
  templateCode: string;
  templateName: string;
  hotWorkLevel: string;
  versionNo: number;
  status: string;
}

export interface HotWorkApprovalTaskRecord {
  id: number;
  nodeInstanceId: number;
  nodeSeq?: number;
  nodeName?: string;
  signMode?: string;
  assigneeUserId?: number;
  assigneeName?: string;
  status: string;
}

export interface HotWorkApprovalNodeProgress {
  nodeSeq: number;
  nodeName: string;
  signMode: string;
  status: string;
  approvedCount?: number;
  requiredCount?: number;
}

export interface HotWorkApprovalProgress {
  instanceId?: number;
  instanceStatus?: string;
  currentNodeSeq?: number;
  totalNodes?: number;
  currentNodeName?: string;
  signMode?: string;
  approvedCount?: number;
  requiredCount?: number;
  nodes: HotWorkApprovalNodeProgress[];
  pendingTasks: HotWorkApprovalTaskRecord[];
}

export interface PermitActionPayload {
  opinion?: string;
  reason?: string;
  approvalTaskId?: number;
  action?: 'APPROVE' | 'RETURN' | 'REJECT';
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
  runNo?: string;
  runResult: string;
  runStatus?: string;
  executorName?: string;
  evidenceRef?: string;
  remark?: string;
  executedAt?: string;
}

export interface FileObjectRecord {
  id: number;
  tenantId: number;
  fileName: string;
  contentType?: string;
  sizeBytes?: number;
  sha256?: string;
  downloadUrl?: string;
  createdAt?: string;
}

export interface MobileTaskRecord {
  taskType: string;
  title: string;
  bizId: number;
  status?: string;
  occurredAt?: string;
}

export interface TrendPointRecord {
  label: string;
  value: number;
}

export interface TrendSeriesRecord {
  metric: string;
  days: number;
  points: TrendPointRecord[];
  dataRefreshedAt?: string;
  dataSource?: string;
}

export interface AlarmReportSummary {
  totalCount: number;
  actionableCount?: number;
  closedCount?: number;
  closureRate?: number;
  avgConfirmMinutes?: number;
  avgDisposeMinutes?: number;
  levelCounts?: Record<string, number>;
  statusCounts?: Record<string, number>;
  dataRefreshedAt?: string;
  dataSource?: string;
}

export interface AlarmReportDetail {
  id: number;
  alarmNo: string;
  alarmLevel: string;
  status: string;
  title?: string;
  confirmMinutes?: number;
  disposeMinutes?: number;
}

export interface MajorHazardReportSummary {
  totalCount: number;
  publishedCount?: number;
  archiveCompletenessRate?: number;
  levelCounts?: Record<string, number>;
  dataRefreshedAt?: string;
  dataSource?: string;
}

export interface ContractorReportSummary {
  companyCount?: number;
  workerCount?: number;
  approvedCompanyCount?: number;
  approvedWorkerCount?: number;
  certificateExpiringCount?: number;
  blacklistCount?: number;
  violationCount?: number;
  dataRefreshedAt?: string;
  dataSource?: string;
}

export interface AuditReportSummary {
  totalCount?: number;
  keyActionCount?: number;
  coverageRate?: number;
  dataRefreshedAt?: string;
  dataSource?: string;
}

export interface WorkPermitReportDetail {
  id: number;
  permitNo: string;
  workType: string;
  status: string;
  title?: string;
  sitePermitTraced?: boolean;
  contractorChecked?: boolean;
}

export interface ReportExportTaskRecord {
  id: number;
  tenantId: number;
  reportType: string;
  exportFormat?: string;
  status: string;
  filePath?: string;
  downloadUrl?: string;
  errorMessage?: string;
  createdAt?: string;
  completedAt?: string;
}

export interface MobileFileUploadRecord {
  fileId: string;
  fileName: string;
  contentType?: string;
  sizeBytes?: number;
  sha256?: string;
  url?: string;
  uploadedAt?: string;
}

export interface AcceptanceTestRunRequest {
  tenantId: number;
  caseId: number;
  runNo: string;
  executorName?: string;
  runStatus: string;
  evidenceRef?: string;
  remark?: string;
}

// ============================================================
// 领域4: 风险防控域 (psm-risk-api) — dual-prevention / inspection / major-hazard
// ============================================================

/** 双防 API（风险防控域 — psm-risk-api）。 */
export const DUAL_PREVENTION_API = {
  base: '/api/dual-prevention',
  riskUnits: '/api/dual-prevention/risk-units',
  hazards: '/api/dual-prevention/hazards'
} as const;

/** 巡检 API（风险防控域 — psm-risk-api）。 */
export const INSPECTION_API = {
  base: '/api/inspection',
  plans: '/api/inspection/plans',
  tasks: '/api/inspection/tasks',
  statistics: '/api/inspection/tasks/statistics'
} as const;

// ============================================================
// 领域3: 实时感知域 (psm-realtime-api) — alarm / location / video
// ============================================================

/** 人员定位 API（实时感知域 — psm-realtime-api）。 */
export const LOCATION_API = {
  base: '/api/location',
  tags: '/api/location/tags',
  events: '/api/location/events',
  geofences: '/api/location/geofences',
  visitors: '/api/location/visitors/records',
  headcount: '/api/location/areas'
} as const;

/** 视频 AI API（实时感知域 — psm-realtime-api）。 */
export const VIDEO_API = {
  base: '/api/video',
  cameras: '/api/video/cameras',
  aiEvents: '/api/video/ai-events'
} as const;

// ============================================================
// 领域2续: 作业管控域 (psm-operation-api) — simops
// ============================================================

/** SIMOPS 交叉作业 API（作业管控域 — psm-operation-api）。 */
export const SIMOPS_API = {
  base: '/api/simops',
  rules: '/api/simops/rules',
  conflicts: '/api/simops/conflicts',
  statistics: '/api/simops/statistics'
} as const;

// ============================================================
// 领域6: 事件治理域 (psm-incident-governance-api) — integration / report
// ============================================================

/** 监管集成 API（事件治理域 — psm-incident-governance-api）。 */
export const INTEGRATION_REG_API = {
  base: '/api/integration/reg',
  tasks: '/api/integration/reg/tasks',
  platforms: '/api/integration/reg/platforms'
} as const;

export interface RiskUnitRecord {
  id: number;
  tenantId: number;
  unitCode?: string;
  unitName: string;
  areaId?: number;
  parentId?: number;
  riskLevel?: string;
  status?: string;
}

export interface RiskUnitTreeNode {
  id: number;
  unitName: string;
  riskLevel?: string;
  children?: RiskUnitTreeNode[];
}

export interface HazardReportRecord {
  id: number;
  tenantId: number;
  hazardNo?: string;
  hazardLevel: string;
  sourceType?: string;
  sourceBizId?: number;
  riskUnitId?: number;
  areaId?: number;
  description: string;
  foundAt?: string;
  rectificationDeadline?: string;
  status: string;
  overdueFlag?: number;
  assigneeUserId?: number;
}

export interface HazardStatisticsRecord {
  totalCount?: number;
  openCount?: number;
  overdueCount?: number;
  closedCount?: number;
}

export interface InspectionPlanRecord {
  id: number;
  tenantId: number;
  planCode: string;
  planName: string;
  routeId?: number;
  cycleType?: string;
  majorHazardId?: number;
  enabled?: number;
}

export interface InspectionTaskRecord {
  id: number;
  tenantId: number;
  taskNo?: string;
  planId?: number;
  routeId?: number;
  scheduledStart?: string;
  scheduledEnd?: string;
  actualStart?: string;
  actualEnd?: string;
  executorId?: number;
  status: string;
  completionRate?: number;
  abnormalCount?: number;
}

export interface InspectionStatisticsRecord {
  totalCount?: number;
  pendingCount?: number;
  inProgressCount?: number;
  completedCount?: number;
  missedCount?: number;
  abnormalItemCount?: number;
  completionRate?: number;
  missedRate?: number;
  abnormalRate?: number;
}

export interface LocTagRecord {
  id: number;
  tenantId: number;
  tagNo: string;
  tagType?: string;
  status?: string;
  batteryLevel?: number;
}

export interface LocEventRecord {
  id: number;
  tenantId: number;
  eventType: string;
  tagNo?: string;
  personId?: number;
  areaId?: number;
  eventTime?: string;
  alarmId?: number;
}

export interface VisitorAccessRecord {
  id: number;
  tenantId: number;
  visitorName?: string;
  idNoMasked?: string;
  visitPurpose?: string;
  accessType?: string;
  gateName?: string;
  accessTime?: string;
}

export interface VideoCameraRecord {
  id: number;
  tenantId: number;
  cameraCode?: string;
  cameraName: string;
  areaId?: number;
  status?: string;
}

export interface VideoAiEventRecord {
  id: number;
  tenantId: number;
  eventNo?: string;
  eventType: string;
  eventTime?: string;
  cameraId?: number;
  severity?: string;
  status: string;
  title?: string;
  alarmId?: number;
}

export interface SimopsConflictRuleRecord {
  id: number;
  tenantId: number;
  ruleCode?: string;
  ruleName?: string;
  workTypeA?: string;
  workTypeB?: string;
  conflictAction?: string;
  enabled?: boolean;
}

export interface SimopsScanResultRecord {
  id: number;
  tenantId: number;
  workPermitId?: number;
  scanStage?: string;
  finalAction?: string;
  conflictCount?: number;
  scannedAt?: string;
}

export interface SimopsStatisticsRecord {
  totalScans?: number;
  totalConflicts?: number;
  blockCount?: number;
  coordinateCount?: number;
  warnCount?: number;
}

export interface RegReportTaskRecord {
  id: number;
  tenantId: number;
  taskNo?: string;
  platformCode?: string;
  dataDomain?: string;
  status: string;
  scheduledAt?: string;
  completedAt?: string;
}

export interface Phase2ReportSummaryRecord {
  tenantId?: number;
  generatedAt?: string;
  dataSource?: string;
  dualPrevention?: { totalCount?: number; overdueCount?: number; closedCount?: number };
  inspection?: { totalCount?: number; completedCount?: number; missedCount?: number; abnormalItemCount?: number };
  location?: { eventCount?: number };
  video?: { aiEventCount?: number };
  simops?: { totalScans?: number; totalConflicts?: number; blockCount?: number; coordinateCount?: number; warnCount?: number };
}

// ============================================================
// 领域5: 过程安全域 (psm-process-safety-api) — pha / moc / pssr / barrier
// ============================================================

/** PHA/HAZOP API（过程安全域 — psm-process-safety-api）。 */
export const PHA_API = {
  projects: '/api/pha/projects',
  recommendations: '/api/pha/recommendations',
  lopaScenarios: '/api/pha/lopa-scenarios'
} as const;

/** MOC 变更管理 API（过程安全域 — psm-process-safety-api）。 */
export const MOC_API = {
  changes: '/api/moc/changes'
} as const;

/** PSSR 启动前审查 API（过程安全域 — psm-process-safety-api）。 */
export const PSSR_API = {
  projects: '/api/pssr/projects',
  templates: '/api/pssr/templates',
  issues: '/api/pssr/issues'
} as const;

/** 屏障与机械完整性 API（过程安全域 — psm-process-safety-api）。 */
export const BARRIER_API = {
  barriers: '/api/barriers',
  mechanicalIntegrity: '/api/mechanical-integrity'
} as const;

// ============================================================
// 领域6: 事件治理域 (psm-incident-governance-api) — incident / governance
// ============================================================

/** 事故调查 API（事件治理域 — psm-incident-governance-api）。 */
export const INCIDENT_API = {
  base: '/api/incidents'
} as const;

/** 集团治理 API（事件治理域 — psm-incident-governance-api）。 */
export const GOVERNANCE_API = {
  base: '/api/governance',
  dashboard: '/api/governance/dashboard/overview',
  benchmark: '/api/governance/benchmark'
} as const;

export interface PhaProjectRecord {
  id: number;
  tenantId: number;
  projectNo?: string;
  projectName?: string;
  siteId?: number;
  unitId?: number;
  majorHazardId?: number;
  method?: string;
  version?: string;
  reviewDueAt?: string;
  status: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface LopaScenarioRecord {
  id: number;
  tenantId: number;
  projectId?: number;
  scenarioNo?: string;
  initiatingEventFrequency?: number;
  targetFrequency?: number;
  mitigatedFrequency?: number;
  silRecommendation?: string;
  status?: string;
}

export interface MiEquipmentRecord {
  id: number;
  tenantId: number;
  equipmentCode?: string;
  equipmentName?: string;
  criticality?: string;
  status: string;
}

export interface PhaRecommendationRecord {
  id: number;
  tenantId: number;
  projectId?: number;
  recommendationNo?: string;
  content?: string;
  assigneeUserId?: number;
  rectificationDeadline?: string;
  status: string;
  overdueFlag?: number;
}

export interface MocChangeRecord {
  id: number;
  tenantId: number;
  changeNo?: string;
  title?: string;
  changeType?: string;
  changeLevel?: string;
  areaId?: number;
  status: string;
  emergencyFlag?: number;
}

export interface PssrProjectRecord {
  id: number;
  tenantId: number;
  projectNo?: string;
  projectName?: string;
  mocId?: number;
  status: string;
  startupApproved?: number;
}

export interface BarrierRecord {
  id: number;
  tenantId: number;
  barrierCode?: string;
  barrierName?: string;
  barrierType?: string;
  healthStatus?: string;
  status: string;
}

export interface IncidentRecord {
  id: number;
  tenantId: number;
  incidentNo?: string;
  title?: string;
  severity?: string;
  status: string;
  sourceType?: string;
}

export interface GovernanceDashboardRecord {
  tenantId?: number;
  metricCount?: number;
  openAuditIssues?: number;
  siteCount?: number;
  snapshotCount?: number;
  [key: string]: unknown;
}

export interface Phase3ReportSummaryRecord {
  tenantId?: number;
  generatedAt?: string;
  dataSource?: string;
  governanceDashboard?: Record<string, unknown>;
  pha?: Record<string, unknown>;
  moc?: Record<string, unknown>;
  pssr?: Record<string, unknown>;
  barrier?: Record<string, unknown>;
  incident?: Record<string, unknown>;
}
