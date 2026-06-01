#!/usr/bin/env node
/**
 * 将 Controller 上的简短 Javadoc 升级为含业务说明、@param、@return 的详细注释。
 */
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const ROOT = path.join(__dirname, '..', 'code', 'backend');

const MAPPING_ANNOT = /^\s*@(Get|Post|Put|Delete|Patch|Request)Mapping\b(.*)$/;
const CLASS_NAME_RE = /public\s+class\s+(\w+Controller)\b/;
const CLASS_REQUEST_MAPPING = /@RequestMapping\s*\(\s*"([^"]+)"\s*\)/;

const CLASS_DETAIL = {
  WorkPermitController: {
    title: '危险作业票（通用）接口',
  },
  ExcavationController: { title: '动土作业专项接口', scope: '挂载于作业票下的动土详情、地下设施、会签、现场检查与流程节点。' },
  HeightWorkController: { title: '高处作业专项接口', scope: '高处作业危险因素、防护/环境检查及流程进度。' },
  LiftingController: { title: '吊装作业专项接口', scope: '吊装方案、吊具检查、指挥与司索人员确认等。' },
  RoadBreakController: { title: '断路作业专项接口', scope: '断路范围、交通组织方案及相关方确认。' },
  TempElectricController: { title: '临时用电作业专项接口', scope: '用电设备、接地与漏电保护等专项数据。' },
  ConfinedSpaceController: { title: '受限空间作业专项接口', scope: '受限空间隔离、气体检测与人员进出登记。' },
  BlindPlateController: { title: '盲板抽堵作业专项接口', scope: '盲板位置、隔离措施及作业确认。' },
  BlindPlateRegistryController: { title: '盲板台账接口', scope: '全厂盲板主数据维护，供作业票引用。' },
  ContractorWorkerController: {
    title: '承包商人员准入接口',
    scope: '人员档案、证书/培训/违章子资源及作业前资格校验。',
  },
  ContractorCompanyController: { title: '承包商单位准入接口', scope: '单位资质、审批流与准入状态管理。' },
  ContractorController: { title: '承包商域入口', scope: '承包商模块聚合或健康检查入口。' },
  AuthController: { title: '认证与会话接口', scope: '本地注册登录、令牌刷新、SSO 与当前用户信息。' },
  AuditLogController: { title: '审计日志接口', scope: '跨域操作留痕的查询与写入。' },
  GatewayProxyController: { title: 'API 网关代理', scope: '将认证后的请求转发至作业、实时、风险等下游微服务。' },
};

const PARAM_HINT = {
  tenantId: '租户 ID，多租户隔离必填',
  id: '资源主键 ID',
  companyId: '承包商单位 ID',
  workerId: '承包商人员 ID',
  certId: '证书记录 ID',
  trainingId: '培训记录 ID',
  factorId: '危险因素记录 ID',
  keyword: '模糊搜索关键字',
  status: '业务状态筛选',
  accessStatus: '准入/授权状态',
  workType: '作业类型编码',
  areaId: '区域 ID',
  hazardId: '重大危险源 ID',
  pageNo: '页码，从 1 开始',
  pageSize: '每页条数，默认 20',
  checkPoint: '流程校验节点编码（如 SUBMIT、SITE_PERMIT）',
  stage: '检查阶段（如 BEFORE、DURING、AFTER）',
  bizType: '业务类型',
  bizTypePrefix: '业务类型前缀，用于模糊匹配',
  bizId: '业务实体 ID',
  action: '操作动作编码',
  operatorName: '操作人姓名',
  startTime: '查询起始时间（含）',
  endTime: '查询截止时间（含）',
  authorization: 'Authorization 请求头中的 Bearer 令牌',
  userId: '当前操作人用户 ID（请求头透传）',
  username: '当前操作人用户名（请求头透传）',
  operator: '操作人标识，缺省为 system',
  request: '请求体',
  servletRequest: '原始 HTTP 请求，用于解析客户端 IP 等',
};

const PATH_ID_HINT = [
  { pattern: /work-permits\/\{id\}/, name: 'id', hint: '作业票 ID' },
  { pattern: /workers\/\{id\}/, name: 'id', hint: '承包商人员 ID' },
  { pattern: /companies\/\{id\}/, name: 'id', hint: '承包商单位 ID' },
  { pattern: /nodes\/\{nodeId\}/, name: 'nodeId', hint: 'PHA 节点 ID' },
  { pattern: /certificates\/\{certId\}/, name: 'certId', hint: '证书记录 ID' },
  { pattern: /trainings\/\{trainingId\}/, name: 'trainingId', hint: '培训记录 ID' },
  { pattern: /factorId/, name: 'factorId', hint: '危险因素记录 ID' },
];

const HTTP_LABEL = { Get: 'GET', Post: 'POST', Put: 'PUT', Delete: 'DELETE', Patch: 'PATCH', Request: 'REQUEST' };

function walk(dir, acc = []) {
  for (const name of fs.readdirSync(dir)) {
    const p = path.join(dir, name);
    if (fs.statSync(p).isDirectory()) walk(p, acc);
    else if (name.endsWith('Controller.java')) acc.push(p);
  }
  return acc;
}

function isMethodMapping(lines, idx) {
  for (let j = idx + 1; j < Math.min(idx + 12, lines.length); j++) {
    const s = lines[j].trim();
    if (!s) continue;
    if (s.startsWith('public class')) return false;
    if (/^\s*public\s+/.test(lines[j])) return true;
    if (!s.startsWith('@')) return false;
  }
  return false;
}

function extractMappingPath(rest) {
  const m = rest.match(/"([^"]*)"/);
  return m ? m[1] : '';
}

function getClassMapping(content) {
  const m = content.match(CLASS_REQUEST_MAPPING);
  return m ? m[1] : '';
}

function fullPath(classPath, methodPath) {
  const base = (classPath || '').replace(/\/$/, '');
  const sub = methodPath || '';
  if (!base) return sub || '/';
  if (!sub) return base;
  if (sub.startsWith('/api/')) return sub;
  return `${base}${sub.startsWith('/') ? '' : '/'}${sub}`;
}

function parseMethodBlock(lines, mappingIdx) {
  let i = mappingIdx + 1;
  while (i < lines.length) {
    const t = lines[i].trim();
    if (t.startsWith('public ')) break;
    if (t && !t.startsWith('@')) break;
    i++;
  }
  const buf = [];
  let parenDepth = 0;
  let started = false;
  while (i < lines.length) {
    buf.push(lines[i]);
    for (const ch of lines[i]) {
      if (ch === '(') {
        parenDepth++;
        started = true;
      } else if (ch === ')') {
        parenDepth--;
      }
    }
    if (started && parenDepth === 0) break;
    i++;
  }
  const sig = buf.join(' ').replace(/\s+/g, ' ');
  const rm = sig.match(/public\s+([\w<>,\s\[\]?]+)\s+(\w+)\s*\(/);
  if (!rm) return null;
  const returnType = rm[1].trim();
  const methodName = rm[2];
  const paramSection = sig.slice(sig.indexOf('(') + 1, sig.lastIndexOf(')'));
  const params = [];
  // 按逗号拆分参数块，再提取注解、类型与名称
  const parts = paramSection.split(/,(?![^<]*>)/);
  for (const part of parts) {
    const p = part.trim();
    if (!p) continue;
    const annotMatch = p.match(/@(PathVariable|RequestParam|RequestBody|RequestHeader)/);
    if (!annotMatch) continue;
    const nameMatch = p.match(/([\w<>,\s\[\]?]+)\s+(\w+)\s*$/);
    if (!nameMatch) continue;
    params.push({ annot: annotMatch[1], type: nameMatch[1].trim(), name: nameMatch[2] });
  }
  return { methodName, returnType, params, signature: sig };
}

function simplifyReturnType(rt) {
  const m = rt.match(/ResponseVO<([^>]+)>/);
  if (!m) return '操作结果';
  const inner = m[1];
  if (inner.includes('PageResult')) return '分页数据';
  if (inner === 'Void' || inner === 'void') return '无业务载荷（成功即可）';
  if (inner.startsWith('List<')) return '列表数据';
  return '业务数据对象';
}

function paramDescription(name, annot, fullUrl) {
  for (const ph of PATH_ID_HINT) {
    if (ph.name === name && ph.pattern.test(fullUrl)) return ph.hint;
  }
  if (PARAM_HINT[name]) return PARAM_HINT[name];
  if (name === 'id' && annot === 'PathVariable') return '路径中的资源 ID';
  if (name.endsWith('Id')) return `${name.replace(/Id$/, '')} ID`;
  if (annot === 'RequestBody') return 'JSON 请求体';
  if (annot === 'RequestHeader') return 'HTTP 请求头字段';
  return `${name} 参数`;
}

function inferSummary(http, methodName, methodPath, className) {
  const lower = methodName.toLowerCase();
  const seg = methodPath.replace(/\/\{[^}]+\}/g, '').split('/').filter(Boolean).pop() || '';

  const byClassMethod = {
    'ContractorWorkerController.page': '分页查询承包商作业人员',
    'ContractorCompanyController.page': '分页查询承包商单位',
    'WorkPermitController.page': '分页查询危险作业票',
    'WorkPermitController.detail': '查询作业票详情（含专项扩展字段）',
    'AuditLogController.page': '按条件分页检索审计日志',
    'AuditLogController.append': '写入一条审计日志（供各域埋点调用）',
    'ExcavationController.getDetail': '查询动土作业专项详情',
    'ExcavationController.saveDetail': '保存动土作业专项详情',
    'ExcavationController.preCheck': '动土作业流程前置校验',
    'ExcavationController.flowProgress': '查询动土作业流程进度',
  };
  const key = `${className}.${methodName}`;
  if (byClassMethod[key]) return byClassMethod[key];

  const byName = {
    page: '分页查询列表',
    list: '查询列表',
    get: '查询单条详情',
    getDetail: '查询专项详情',
    detail: '查询作业票详情',
    create: '新建记录',
    update: '更新记录',
    save: '保存数据',
    saveDetail: '保存专项详情',
    delete: '删除记录',
    remove: '移除关联数据',
    submit: '提交审批',
    approve: '审批通过',
    reject: '审批驳回',
    suspend: '暂停/挂起',
    blacklist: '加入黑名单',
    preCheck: '执行流程前置校验',
    flowProgress: '查询流程进度与节点完成情况',
    health: '服务健康检查',
    eligibilityCheck: '作业前人员资格校验',
    recalculate: '按规则重新计算派生字段',
    register: '注册本地账号',
    login: '账号登录并签发令牌',
    logout: '注销当前会话',
    refresh: '刷新访问令牌',
    append: '写入审计日志',
    proxy: '代理转发下游服务',
  };
  if (byName[lower]) return byName[lower];

  const segMap = {
    detail: '专项详情',
    facilities: '地下设施',
    countersigns: '会签记录',
    'site-checks': '现场检查记录',
    certificates: '人员证书',
    trainings: '培训记录',
    violations: '违章记录',
    'hazard-factors': '危险因素',
    'protection-checks': '防护措施检查',
    'environment-checks': '作业环境检查',
    'traffic-plan': '交通组织方案',
    workers: '作业人员',
    'gas-tests': '气体检测记录',
    'safety-measures': '安全措施',
    'flow-progress': '流程进度',
    'pre-check': '前置校验',
    logs: '审计日志',
  };
  const subject = segMap[seg] || (seg ? seg.replace(/-/g, ' ') : '业务数据');

  if (http === 'Get') return `查询${subject}`;
  if (http === 'Post') return `新增${subject}或触发${subject}相关动作`;
  if (http === 'Put') return `更新${subject}`;
  if (http === 'Delete') return `删除${subject}`;
  if (http === 'Request') return `代理访问${subject}`;
  return `处理${subject}`;
}

function extraNote(methodName, methodPath, params) {
  const notes = [];
  if (params.some((p) => p.name === 'tenantId')) {
    notes.push('所有查询与变更均按租户隔离。');
  }
  if (params.some((p) => p.name === 'operator' || p.name === 'userId')) {
    notes.push('写操作从请求头解析操作人并写入审计字段。');
  }
  if (methodName === 'preCheck') {
    notes.push('根据 checkPoint 返回是否允许进入下一流程节点。');
  }
  if (methodPath.includes('eligibility-check')) {
    notes.push('用于作业票选人前的准入规则校验。');
  }
  return notes.length ? notes.join('') : '';
}

function buildMethodJavadoc(http, classPath, methodPath, className, methodInfo) {
  const url = fullPath(classPath, methodPath);
  const summary = inferSummary(http, methodInfo.methodName, methodPath, className);
  const indent = '    ';
  const lines = [];
  lines.push(`${indent}/**`);
  lines.push(`${indent} * ${summary}。`);
  lines.push(`${indent} * <p>HTTP ${HTTP_LABEL[http]} {@code ${url}}</p>`);
  const note = extraNote(methodInfo.methodName, methodPath, methodInfo.params);
  if (note) lines.push(`${indent} * <p>${note}</p>`);
  for (const p of methodInfo.params) {
    if (p.name === 'operator' && methodInfo.params.some((x) => x.name === 'userId')) continue;
    lines.push(`${indent} * @param ${p.name} ${paramDescription(p.name, p.annot, url)}`);
  }
  lines.push(`${indent} * @return ${simplifyReturnType(methodInfo.returnType)}，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}`);
  lines.push(`${indent} */`);
  return lines;
}

function scopeFromPath(classPath) {
  if (!classPath) return '';
  if (classPath.includes('/excavation')) return '挂载于作业票下的动土详情、地下设施、会签、现场检查与流程节点。';
  if (classPath.includes('/height-work')) return '高处作业危险因素、防护/环境检查及流程进度。';
  if (classPath.includes('/lifting')) return '吊装方案、吊具检查、指挥与司索人员确认等。';
  if (classPath.includes('/road-break')) return '断路范围、交通组织方案及相关方确认。';
  if (classPath.includes('/temporary-electric')) return '临时用电设备、接地与漏电保护等专项数据。';
  if (classPath.includes('/confined-space')) return '受限空间隔离、气体检测与人员进出登记。';
  if (classPath.includes('/blind-plate')) return '盲板位置、隔离措施及作业确认。';
  if (classPath.includes('/work-permits')) return '作业票全生命周期：建档、审批、现场许可、验收与关闭。';
  if (classPath.includes('/contractors/workers')) return '人员档案、证书/培训/违章子资源及作业前资格校验。';
  if (classPath.includes('/contractors/companies')) return '单位资质、审批流与准入状态管理。';
  if (classPath.includes('/audit')) return '跨域操作留痕的查询与写入。';
  if (classPath.includes('/auth')) return '本地注册登录、令牌刷新、SSO 与当前用户信息。';
  if (classPath.includes('/pha/')) return '工艺危害分析（PHA/HAZOP/LOPA）项目与节点数据。';
  if (classPath.includes('/alarms')) return '实时告警事件与规则配置。';
  if (classPath.includes('/location')) return '人员/车辆/访客定位与出入记录。';
  if (classPath.includes('/inspection')) return '巡检计划、路线、任务与检查表。';
  if (classPath.includes('/dual-prevention')) return '双重预防机制：风险单元、事件与管控措施。';
  if (classPath.includes('/governance') || classPath.includes('/incidents')) return '隐患治理与事故事件闭环。';
  return '';
}

function buildClassJavadoc(className, classPath) {
  const detail = CLASS_DETAIL[className];
  const scope = detail?.scope || scopeFromPath(classPath);
  const title = detail?.title || `${className.replace('Controller', '')} 模块 HTTP API`;
  const lines = [];
  lines.push('/**');
  lines.push(` * ${title.endsWith('。') ? title : title + '。'}`);
  if (scope) lines.push(` * <p>${scope}</p>`);
  if (classPath) lines.push(` * <p>基础路径：{@code ${classPath}}</p>`);
  lines.push(' * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>');
  lines.push(' */');
  return lines;
}

function findJavadocRange(lines, beforeIdx) {
  let j = beforeIdx - 1;
  while (j >= 0 && lines[j].trim() === '') j--;
  if (j < 0 || lines[j].trim() !== '*/') return null;
  const end = j;
  while (j >= 0) {
    if (lines[j].trim().startsWith('/**')) return [j, end];
    j--;
  }
  return null;
}

function processFile(filePath) {
  const original = fs.readFileSync(filePath, 'utf8');
  const classMatch = original.match(CLASS_NAME_RE);
  if (!classMatch) return false;
  const className = classMatch[1];
  const classPath = getClassMapping(original);

  let lines = original.split(/\r?\n/);

  // 类注释：保留已有多段详细说明，其余替换为增强版
  const restIdx = lines.findIndex((l) => l.includes('@RestController'));
  if (restIdx > 0) {
    const range = findJavadocRange(lines, restIdx);
    const block = range ? lines.slice(range[0], range[1] + 1).join('\n') : '';
    const keepExisting =
      range &&
      block.split('\n').length >= 5 &&
      /[\u4e00-\u9fff]/.test(block) &&
      block.includes('基础路径') === false &&
      !block.includes('返回体均为');
    if (!keepExisting) {
      const newClassDoc = buildClassJavadoc(className, classPath);
      if (range) {
        lines.splice(range[0], range[1] - range[0] + 1, ...newClassDoc);
      } else {
        lines.splice(restIdx, 0, ...newClassDoc);
      }
    }
  }

  // 方法注释：从后往前替换，避免下标错位
  const mappingIndices = [];
  for (let i = 0; i < lines.length; i++) {
    if (MAPPING_ANNOT.test(lines[i]) && isMethodMapping(lines, i)) {
      mappingIndices.push(i);
    }
  }

  for (let k = mappingIndices.length - 1; k >= 0; k--) {
    const i = mappingIndices[k];
    const mm = lines[i].match(MAPPING_ANNOT);
    const http = mm[1];
    const methodPath = extractMappingPath(mm[2] || '');
    const methodInfo = parseMethodBlock(lines, i);
    if (!methodInfo) continue;
    const newDoc = buildMethodJavadoc(http, classPath, methodPath, className, methodInfo);
    const range = findJavadocRange(lines, i);
    if (range) {
      lines.splice(range[0], range[1] - range[0] + 1, ...newDoc);
    } else {
      lines.splice(i, 0, ...newDoc);
    }
  }

  let result = lines.join('\n');
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
console.log(`Enhanced ${changed.length} controller(s)`);
