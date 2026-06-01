#!/usr/bin/env node
/** 为所有 Controller 补全类级与方法级 Javadoc（接口用途：…）。 */
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const ROOT = path.join(__dirname, '..', 'code', 'backend');

const MAPPING_ANNOT = /^\s*@(Get|Post|Put|Delete|Patch|Request)Mapping\b(.*)$/;
const CLASS_NAME_RE = /public\s+class\s+(\w+Controller)\b/;
const REQUEST_MAPPING_RE = /@RequestMapping\s*\(\s*"([^"]+)"\s*\)/;
const METHOD_SIG_RE = /^\s*public\s+[\w<>,\s\[\]?]+\s+(\w+)\s*\(/;

const CLASS_DESC = {
  WorkPermitController: '危险工作票接口（动火/受限空间等通用作业票生命周期）。',
  ExcavationController: '动土作业专项接口。',
  HeightWorkController: '高处作业专项接口。',
  LiftingController: '吊装作业专项接口。',
  RoadBreakController: '断路作业专项接口。',
  TempElectricController: '临时用电作业专项接口。',
  ConfinedSpaceController: '受限空间作业专项接口。',
  BlindPlateController: '盲板抽堵作业专项接口。',
  BlindPlateRegistryController: '盲板台账接口。',
  SimopsController: '同期作业（SIMOPS）冲突检测接口。',
  MobileController: '移动端作业票同步接口。',
  ContractorController: '承包商域入口接口。',
  ContractorCompanyController: '承包商单位准入接口。',
  ContractorWorkerController: '承包商人员管理接口。',
  AuthController: '本地账号与 SSO 认证接口。',
  DemoController: '演示/探活接口。',
  FileController: '文件上传与下载接口。',
  IamAdminController: 'IAM 租户与用户管理接口。',
  InternalIamController: 'IAM 内部服务调用接口。',
  AuditLogController: '审计日志查询接口。',
  NotificationController: '站内通知接口。',
  BaseDataController: '基础主数据接口。',
  MasterDataController: '主数据维护接口。',
  GatewayProxyController: '网关代理控制器，转发非 identity 域外部服务请求。',
  HotWorkWorkflowController: '动火作业审批流配置接口。',
  ConfigRuleController: '工艺安全配置规则接口。',
  BarrierController: '安全屏障管理接口。',
  MechanicalIntegrityController: '机械完整性管理接口。',
  MocChangeController: '变更管理（MOC）接口。',
  PssrProjectController: 'PSSR 项目接口。',
  PssrIssueController: 'PSSR 问题项接口。',
  PssrTemplateController: 'PSSR 模板接口。',
  PhaProjectController: 'PHA 项目接口。',
  PhaNodeController: 'PHA 节点接口。',
  PhaRecommendationController: 'PHA 建议项接口。',
  HazopDeviationController: 'HAZOP 偏差分析接口。',
  LopaScenarioController: 'LOPA 场景接口。',
  Phase2ReportController: '二期报表接口。',
  Phase3ReportController: '三期报表接口。',
  ReportController: '报表查询与导出接口。',
  DashboardController: '治理看板接口。',
  AcceptanceController: '验收管理接口。',
  GovernanceController: '隐患治理接口。',
  IncidentController: '事故事件管理接口。',
  IntegrationController: '外部系统集成接口。',
  RegMappingController: '监管平台字段映射接口。',
  RegPlatformController: '监管平台对接配置接口。',
  RegReportController: '监管平台上报接口。',
  AlarmController: '实时告警接口。',
  AlarmRuleController: '告警规则配置接口。',
  AreaController: '区域定位接口。',
  LocationController: '人员定位服务接口。',
  LocTagController: '定位标签管理接口。',
  LocLocationController: '实时位置查询接口。',
  LocEventController: '定位事件接口。',
  LocGeofenceController: '电子围栏接口。',
  GateAccessController: '门禁出入记录接口。',
  VehicleAccessController: '车辆出入记录接口。',
  VisitorAccessController: '访客出入记录接口。',
  VideoCameraController: '视频监控设备接口。',
  VideoAiEventController: '视频 AI 事件接口。',
  VideoWatchSessionController: '视频观看会话接口。',
  DualPreventionController: '双重预防机制入口接口。',
  HazardController: '危险源辨识接口。',
  RiskUnitController: '风险单元接口。',
  RiskEventController: '风险事件接口。',
  MajorHazardController: '重大危险源管理接口。',
  InspectionController: '巡检任务执行接口。',
  InspectionPlanController: '巡检计划接口。',
  InspectionRouteController: '巡检路线接口。',
  InspectionTaskController: '巡检任务接口。',
  ChecklistTemplateController: '检查表模板接口。',
};

const PATH_SEGMENTS = {
  health: '服务健康状态',
  detail: '详情',
  workers: '作业人员',
  'gas-tests': '气体检测记录',
  'safety-measures': '安全措施',
  'risk-analysis': '风险分析',
  'pre-check': '前置校验',
  'flow-progress': '流程进度',
  'site-checks': '现场检查',
  facilities: '地下设施',
  countersigns: '会签记录',
  'hazard-factors': '危险因素',
  'protection-checks': '防护检查',
  'environment-checks': '环境检查',
  'traffic-plan': '交通组织方案',
  parties: '相关方确认',
  isolation: '隔离措施',
  registry: '盲板台账',
  qualifications: '资质证书',
  timeline: '操作时间线',
  'hot-work': '动火审批',
  approval: '审批',
  tasks: '待办任务',
  workflows: '审批流',
  'by-hazard': '按重大危险源关联作业票',
  recalculate: '重新计算',
  submit: '提交',
  approve: '审批通过',
  reject: '审批驳回',
  cancel: '作废',
  close: '关闭',
  suspend: '暂停',
  resume: '恢复',
  'check-in': '现场签到',
  acceptance: '验收',
  'monitor-records': '监护记录',
  'site-permit': '现场许可',
  'mobile-draft': '移动端草稿',
  conflicts: '冲突检测',
  register: '注册',
  login: '登录',
  logout: '注销',
  refresh: '刷新令牌',
  upload: '上传',
  download: '下载',
  preview: '预览',
  export: '导出',
  import: '导入',
  publish: '发布',
  archive: '归档',
  resolve: '解析审批人',
  activate: '启用',
  deactivate: '停用',
};

const METHOD_NAME_DESC = {
  page: '分页查询业务数据',
  list: '查询列表',
  get: '查询详情',
  detail: '查询详情',
  getDetail: '查询详情',
  create: '创建业务数据',
  update: '更新业务数据',
  delete: '删除业务数据',
  remove: '删除业务数据',
  save: '保存业务数据',
  saveDetail: '保存详情',
  health: '查询服务健康状态',
  preCheck: '执行前置校验',
  flowProgress: '查询流程进度',
  recalculate: '重新计算专项数据',
  submit: '提交审批',
  approve: '审批通过',
  reject: '审批驳回',
  cancel: '作废作业票',
  close: '关闭作业票',
  suspend: '暂停作业',
  resume: '恢复作业',
  register: '注册本地账号',
  login: '完成账号登录并签发令牌',
  logout: '注销当前令牌会话',
  refresh: '刷新令牌会话',
  upload: '上传文件',
  download: '下载文件',
  export: '导出数据',
  import: '导入数据',
  publish: '发布配置',
  activate: '启用',
  deactivate: '停用',
};

const HTTP_VERB = {
  Get: '查询',
  Post: '新增或提交',
  Put: '更新或保存',
  Delete: '删除',
  Patch: '部分更新',
  Request: '代理转发',
};

const MOJIBAKE_MARKERS = ['鎺ュ彛', '鍗遍櫓', '鐢ㄩ€', '鏌ヨ', '鍒涘缓', '鏇存柊'];

function walk(dir, acc = []) {
  for (const name of fs.readdirSync(dir)) {
    const p = path.join(dir, name);
    if (fs.statSync(p).isDirectory()) walk(p, acc);
    else if (name.endsWith('Controller.java')) acc.push(p);
  }
  return acc;
}

function isGarbled(block) {
  if (block.includes('接口用途')) return false;
  return MOJIBAKE_MARKERS.some((m) => block.includes(m));
}

function isMethodMapping(lines, idx) {
  for (let j = idx + 1; j < Math.min(idx + 10, lines.length); j++) {
    const s = lines[j].trim();
    if (!s) continue;
    if (s.startsWith('public class')) return false;
    if (METHOD_SIG_RE.test(lines[j])) return true;
    if (!s.startsWith('@')) return false;
  }
  return false;
}

function hasValidJavadoc(lines, idx) {
  let j = idx - 1;
  while (j >= 0 && lines[j].trim() === '') j--;
  if (j < 0 || lines[j].trim() !== '*/') return false;
  let k = j;
  while (k >= 0) {
    if (lines[k].trim().startsWith('/**')) {
      const block = lines.slice(k, j + 1).join('\n');
      return !isGarbled(block);
    }
    k--;
  }
  return false;
}

function findJavadocBlock(lines, idx) {
  let j = idx - 1;
  while (j >= 0 && lines[j].trim() === '') j--;
  if (j < 0 || lines[j].trim() !== '*/') return null;
  const end = j;
  while (j >= 0) {
    if (lines[j].trim().startsWith('/**')) return [j, end];
    j--;
  }
  return null;
}

function extractMappingPath(rest) {
  const m = rest.match(/"([^"]*)"/);
  return m ? m[1] : '';
}

function lastPathSegment(p) {
  const parts = p.replace('/**', '').split('/').filter((x) => x && !x.startsWith('{'));
  return parts.length ? parts[parts.length - 1] : '';
}

function inferMethodDesc(http, mappingPath, methodName) {
  if (METHOD_NAME_DESC[methodName]) return METHOD_NAME_DESC[methodName];
  const lower = methodName.toLowerCase();
  const seg = lastPathSegment(mappingPath);
  const segCn = PATH_SEGMENTS[seg] || (seg ? seg.replace(/-/g, ' ') : '');

  if (lower.startsWith('list') || lower.startsWith('page')) {
    return segCn ? `查询${segCn}列表` : '分页查询业务数据';
  }
  if (lower.startsWith('get') || lower === 'detail') {
    return segCn ? `查询${segCn}` : '查询详情';
  }
  if (lower.startsWith('add')) return segCn ? `新增${segCn}` : '新增业务数据';
  if (lower.startsWith('save')) return segCn ? `保存${segCn}` : '保存业务数据';
  if (lower.startsWith('create')) return '创建业务数据';
  if (lower.startsWith('update')) return '更新业务数据';
  if (lower.startsWith('remove') || lower.startsWith('delete')) {
    return segCn ? `删除${segCn}` : '删除业务数据';
  }
  if (lower.startsWith('confirm')) return segCn ? `确认${segCn}` : '确认业务数据';
  if (lower.includes('proxy')) return `代理转发 ${mappingPath || '外部服务'} 请求`;

  const verb = HTTP_VERB[http] || '处理';
  if (segCn) return `${verb}${segCn}`;
  if (mappingPath) return `${verb}（${mappingPath}）`;
  return `${verb}业务数据`;
}

function classDescription(className, content) {
  if (CLASS_DESC[className]) return CLASS_DESC[className];
  const rm = content.match(REQUEST_MAPPING_RE);
  const p = rm ? rm[1] : '';
  const base = className.replace('Controller', '');
  return p ? `${base} 接口（${p}）。` : `${base} 接口。`;
}

function formatJavadoc(desc, indent) {
  return [`${indent}/**`, `${indent} * 接口用途：${desc}。`, `${indent} */`];
}

function removeClassLevelMappingJavadocs(lines) {
  const out = [];
  for (let i = 0; i < lines.length; i++) {
    const line = lines[i];
    if (/^\s*@RequestMapping\b/.test(line)) {
      let j = out.length - 1;
      while (j >= 0 && out[j].trim() === '') j--;
      if (j >= 0 && out[j].trim() === '*/') {
        let k = j;
        while (k >= 0 && !out[k].trim().startsWith('/**')) k--;
        if (k >= 0) {
          const block = out.slice(k, j + 1).join('\n');
          if (block.includes('接口用途')) {
            let n = i + 1;
        while (n < lines.length) {
          const s = lines[n].trim();
          if (!s) {
            n++;
            continue;
          }
          if (s.startsWith('public class')) break;
          if (s.startsWith('@')) {
            n++;
            continue;
          }
          break;
        }
            if (n < lines.length && lines[n].trim().startsWith('public class')) {
              out.splice(k, j - k + 1);
              while (out.length && out[out.length - 1].trim() === '') out.pop();
            }
          }
        }
      }
    }
    out.push(line);
  }
  return out;
}

function normalizeClassJavadoc(content, className) {
  return content.replace(
    /\/\*\*([\s\S]*?)\*\/\s*@RestController/,
    (m, body) => {
      const block = `/**${body}*/`;
      if (!isGarbled(block) && /[\u4e00-\u9fff]/.test(body)) {
        return m;
      }
      const desc = classDescription(className, content).replace(/。$/, '');
      return `/**\n * ${desc}。\n */\n@RestController`;
    }
  );
}

function processFile(filePath) {
  const original = fs.readFileSync(filePath, 'utf8');
  const cm = original.match(CLASS_NAME_RE);
  if (!cm) return false;
  const className = cm[1];

  let text = normalizeClassJavadoc(original, className);
  let rawLines = text.split(/\r?\n/);
  rawLines = removeClassLevelMappingJavadocs(rawLines);

  const newLines = [];
  for (let i = 0; i < rawLines.length; i++) {
    const line = rawLines[i];
    const mm = line.match(MAPPING_ANNOT);
    if (mm && isMethodMapping(rawLines, i)) {
      const http = mm[1];
      const pathStr = extractMappingPath(mm[2] || '');
      let methodName = 'handle';
      for (let j = i + 1; j < rawLines.length; j++) {
        const sig = rawLines[j].match(METHOD_SIG_RE);
        if (sig) {
          methodName = sig[1];
          break;
        }
        if (rawLines[j].trim() && !rawLines[j].trim().startsWith('@')) break;
      }
      const indent = (line.match(/^(\s*)/) || ['', ''])[1];
      const desc = inferMethodDesc(http, pathStr, methodName);
      const javadoc = formatJavadoc(desc, indent);
      const existing = findJavadocBlock(newLines, newLines.length);
      if (existing && isGarbled(newLines.slice(existing[0], existing[1] + 1).join('\n'))) {
        newLines.splice(existing[0], existing[1] - existing[0] + 1);
        while (newLines.length && newLines[newLines.length - 1].trim() === '') newLines.pop();
        newLines.push(...javadoc);
      } else if (!hasValidJavadoc(newLines, newLines.length)) {
        newLines.push(...javadoc);
      }
    }
    newLines.push(line);
  }

  let result = newLines.join('\n');
  if (!result.endsWith('\n')) result += '\n';
  if (result !== original) {
    fs.writeFileSync(filePath, result, 'utf8');
    return true;
  }
  return false;
}

const changed = [];
for (const f of walk(ROOT).sort()) {
  if (processFile(f)) changed.push(path.relative(path.join(__dirname, '..'), f));
}
console.log(`Updated ${changed.length} controller(s)`);
changed.forEach((p) => console.log(`  - ${p}`));
