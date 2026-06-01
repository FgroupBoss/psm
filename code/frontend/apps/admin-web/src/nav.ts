import type { MenuTreeNode } from '@psm/domain-types';

/** 管理端可导航视图，与 IAM 菜单 routePath / resourceCode 对齐。 */
export type AppView =
  | 'master-data:areas'
  | 'master-data:units'
  | 'master-data:equipments'
  | 'master-data:monitor-points'
  | 'iam:orgs'
  | 'iam:users'
  | 'iam:roles'
  | 'iam:menus'
  | 'config'
  | 'audit'
  | 'contractor:companies'
  | 'contractor:workers'
  | 'hazard:ledger'
  | 'alarm:list'
  | 'work-permit:list'
  | 'report:overview'
  | 'report:dashboard'
  | 'report:acceptance'
  | 'report:phase2'
  | 'dual-prevention:risk'
  | 'dual-prevention:hazards'
  | 'inspection:tasks'
  | 'location:overview'
  | 'video:events'
  | 'simops:conflicts'
  | 'integration:reg'
  | 'pha:projects'
  | 'pha:recommendations'
  | 'pha:lopa'
  | 'moc:changes'
  | 'pssr:projects'
  | 'barrier:ledger'
  | 'barrier:mi'
  | 'incident:list'
  | 'governance:dashboard'
  | 'report:phase3';

export interface NavItem {
  view: AppView;
  label: string;
  group: string;
}

const ROUTE_ALIASES: Record<string, AppView> = {
  'master-data/areas': 'master-data:areas',
  'master-data:areas': 'master-data:areas',
  'MENU_MD_AREAS': 'master-data:areas',
  'master-data/units': 'master-data:units',
  'master-data:units': 'master-data:units',
  'MENU_MD_UNITS': 'master-data:units',
  'master-data/equipments': 'master-data:equipments',
  'master-data:equipments': 'master-data:equipments',
  'MENU_MD_EQUIPMENTS': 'master-data:equipments',
  'master-data/monitor-points': 'master-data:monitor-points',
  'master-data:monitor-points': 'master-data:monitor-points',
  'MENU_MD_POINTS': 'master-data:monitor-points',
  'iam/orgs': 'iam:orgs',
  'iam:orgs': 'iam:orgs',
  'MENU_IAM_ORGS': 'iam:orgs',
  'iam/users': 'iam:users',
  'iam:users': 'iam:users',
  'MENU_IAM_USERS': 'iam:users',
  'iam/roles': 'iam:roles',
  'iam:roles': 'iam:roles',
  'MENU_IAM_ROLES': 'iam:roles',
  'iam/menus': 'iam:menus',
  'iam:menus': 'iam:menus',
  'MENU_IAM_MENUS': 'iam:menus',
  config: 'config',
  'system/config': 'config',
  'MENU_CONFIG': 'config',
  audit: 'audit',
  'system/audit': 'audit',
  'MENU_AUDIT': 'audit',
  'contractor/companies': 'contractor:companies',
  'contractor:companies': 'contractor:companies',
  MENU_CTR_COMPANIES: 'contractor:companies',
  'contractor/workers': 'contractor:workers',
  'contractor:workers': 'contractor:workers',
  MENU_CTR_WORKERS: 'contractor:workers',
  'hazard/ledger': 'hazard:ledger',
  'hazard:ledger': 'hazard:ledger',
  MENU_HAZ_LEDGER: 'hazard:ledger',
  'alarm/list': 'alarm:list',
  'alarm:list': 'alarm:list',
  MENU_ALM_LIST: 'alarm:list',
  'work-permit/list': 'work-permit:list',
  'work-permit:list': 'work-permit:list',
  MENU_WKP_LIST: 'work-permit:list',
  'report/overview': 'report:overview',
  'report:overview': 'report:overview',
  MENU_RPT_OVERVIEW: 'report:overview',
  'report/dashboard': 'report:dashboard',
  'report:dashboard': 'report:dashboard',
  MENU_RPT_DASHBOARD: 'report:dashboard',
  'report/acceptance': 'report:acceptance',
  'report:acceptance': 'report:acceptance',
  MENU_RPT_ACCEPTANCE: 'report:acceptance',
  'report/phase2': 'report:phase2',
  'report:phase2': 'report:phase2',
  MENU_RPT_PHASE2: 'report:phase2',
  'dual-prevention/risk': 'dual-prevention:risk',
  'dual-prevention:risk': 'dual-prevention:risk',
  MENU_DP_RISK: 'dual-prevention:risk',
  'dual-prevention/hazards': 'dual-prevention:hazards',
  'dual-prevention:hazards': 'dual-prevention:hazards',
  MENU_DP_HAZARDS: 'dual-prevention:hazards',
  'inspection/tasks': 'inspection:tasks',
  'inspection:tasks': 'inspection:tasks',
  MENU_INSP_TASKS: 'inspection:tasks',
  'location/overview': 'location:overview',
  'location:overview': 'location:overview',
  MENU_LOC_OVERVIEW: 'location:overview',
  'video/events': 'video:events',
  'video:events': 'video:events',
  MENU_VIDEO_EVENTS: 'video:events',
  'simops/conflicts': 'simops:conflicts',
  'simops:conflicts': 'simops:conflicts',
  MENU_SIMOPS: 'simops:conflicts',
  'integration/reg': 'integration:reg',
  'integration:reg': 'integration:reg',
  MENU_REG: 'integration:reg',
  'pha/projects': 'pha:projects',
  'pha:projects': 'pha:projects',
  MENU_PHA_PROJECTS: 'pha:projects',
  'pha/recommendations': 'pha:recommendations',
  'pha:recommendations': 'pha:recommendations',
  MENU_PHA_RECOMMENDATIONS: 'pha:recommendations',
  'pha/lopa': 'pha:lopa',
  'pha:lopa': 'pha:lopa',
  MENU_PHA_LOPA: 'pha:lopa',
  'moc/changes': 'moc:changes',
  'moc:changes': 'moc:changes',
  MENU_MOC_CHANGES: 'moc:changes',
  'pssr/projects': 'pssr:projects',
  'pssr:projects': 'pssr:projects',
  MENU_PSSR_PROJECTS: 'pssr:projects',
  'barrier/ledger': 'barrier:ledger',
  'barrier:ledger': 'barrier:ledger',
  MENU_BARRIER_LEDGER: 'barrier:ledger',
  'barrier/mi': 'barrier:mi',
  'barrier:mi': 'barrier:mi',
  MENU_BARRIER_MI: 'barrier:mi',
  'incident/list': 'incident:list',
  'incident:list': 'incident:list',
  MENU_INCIDENT_LIST: 'incident:list',
  'governance/dashboard': 'governance:dashboard',
  'governance:dashboard': 'governance:dashboard',
  MENU_GOV_DASHBOARD: 'governance:dashboard',
  'report/phase3': 'report:phase3',
  'report:phase3': 'report:phase3',
  MENU_RPT_PHASE3: 'report:phase3'
};

/** 无 IAM 菜单数据时的默认可访问菜单（试点管理员）。 */
export const DEFAULT_NAV: NavItem[] = [
  { view: 'master-data:areas', label: '区域台账', group: '基础主数据' },
  { view: 'master-data:units', label: '装置台账', group: '基础主数据' },
  { view: 'master-data:equipments', label: '设备台账', group: '基础主数据' },
  { view: 'master-data:monitor-points', label: '监测点位', group: '基础主数据' },
  { view: 'iam:orgs', label: '组织管理', group: '权限管理' },
  { view: 'iam:users', label: '用户管理', group: '权限管理' },
  { view: 'iam:roles', label: '角色管理', group: '权限管理' },
  { view: 'iam:menus', label: '菜单资源', group: '权限管理' },
  { view: 'config', label: '系统配置与规则', group: '系统配置' },
  { view: 'audit', label: '权限审计', group: '系统配置' },
  { view: 'contractor:companies', label: '承包商单位', group: '承包商管理' },
  { view: 'contractor:workers', label: '承包商人员', group: '承包商管理' },
  { view: 'hazard:ledger', label: '危险源台账', group: '重大危险源' },
  { view: 'alarm:list', label: '实时报警', group: '报警中心' },
  { view: 'work-permit:list', label: '作业票列表', group: '危险工作票' },
  { view: 'report:overview', label: '报表总览', group: '报表大屏' },
  { view: 'report:dashboard', label: '态势大屏', group: '报表大屏' },
  { view: 'report:acceptance', label: '上线验收', group: '报表大屏' },
  { view: 'report:phase2', label: '二期指标', group: '报表大屏' },
  { view: 'dual-prevention:risk', label: '风险清单', group: '双重预防' },
  { view: 'dual-prevention:hazards', label: '隐患台账', group: '双重预防' },
  { view: 'inspection:tasks', label: '智能巡检', group: '智能巡检' },
  { view: 'location:overview', label: '人员定位', group: '人员定位' },
  { view: 'video:events', label: '视频 AI', group: '视频智能' },
  { view: 'simops:conflicts', label: 'SIMOPS', group: 'SIMOPS' },
  { view: 'integration:reg', label: '监管上报', group: '监管接口' },
  { view: 'pha:projects', label: 'PHA 项目', group: '三期 PSM' },
  { view: 'pha:recommendations', label: 'PHA 建议项', group: '三期 PSM' },
  { view: 'pha:lopa', label: 'LOPA 场景', group: '三期 PSM' },
  { view: 'moc:changes', label: 'MOC 变更', group: '三期 PSM' },
  { view: 'pssr:projects', label: 'PSSR 审查', group: '三期 PSM' },
  { view: 'barrier:ledger', label: '屏障管理', group: '三期 PSM' },
  { view: 'barrier:mi', label: '机械完整性', group: '三期 PSM' },
  { view: 'incident:list', label: '事故调查', group: '三期 PSM' },
  { view: 'governance:dashboard', label: '集团治理', group: '三期 PSM' },
  { view: 'report:phase3', label: '三期指标', group: '报表大屏' }
];

const VIEW_TITLES: Record<AppView, string> = {
  'master-data:areas': '区域台账',
  'master-data:units': '装置台账',
  'master-data:equipments': '设备台账',
  'master-data:monitor-points': '监测点位',
  'iam:orgs': '组织管理',
  'iam:users': '用户管理',
  'iam:roles': '角色管理',
  'iam:menus': '菜单资源',
  config: '系统配置与规则',
  audit: '权限审计',
  'contractor:companies': '承包商单位',
  'contractor:workers': '承包商人员',
  'hazard:ledger': '危险源台账',
  'alarm:list': '实时报警',
  'work-permit:list': '作业票列表',
  'report:overview': '报表总览',
  'report:dashboard': '态势大屏',
  'report:acceptance': '上线验收',
  'report:phase2': '二期指标',
  'dual-prevention:risk': '风险清单',
  'dual-prevention:hazards': '隐患台账',
  'inspection:tasks': '智能巡检',
  'location:overview': '人员定位',
  'video:events': '视频 AI',
  'simops:conflicts': 'SIMOPS',
  'integration:reg': '监管上报',
  'pha:projects': 'PHA 项目',
  'pha:recommendations': 'PHA 建议项',
  'pha:lopa': 'LOPA 场景',
  'moc:changes': 'MOC 变更',
  'pssr:projects': 'PSSR 审查',
  'barrier:ledger': '屏障管理',
  'barrier:mi': '机械完整性',
  'incident:list': '事故调查',
  'governance:dashboard': '集团治理',
  'report:phase3': '三期指标'
};

export function viewTitle(view: AppView): string {
  return VIEW_TITLES[view] || '管理端';
}

function resolveView(routePath?: string, resourceCode?: string): AppView | null {
  const candidates = [routePath, resourceCode].filter(Boolean) as string[];
  for (const candidate of candidates) {
    const normalized = candidate.trim();
    if (ROUTE_ALIASES[normalized]) {
      return ROUTE_ALIASES[normalized];
    }
  }
  return null;
}

function flattenMenuNodes(nodes: MenuTreeNode[]): MenuTreeNode[] {
  const result: MenuTreeNode[] = [];
  const walk = (items: MenuTreeNode[]) => {
    items.forEach((item) => {
      if (item.resourceType === 'MENU' && item.visible !== false && item.status === 'ENABLED') {
        result.push(item);
      }
      if (item.children && item.children.length > 0) {
        walk(item.children);
      }
    });
  };
  walk(nodes);
  return result;
}

/** 将 IAM 菜单树转换为侧栏导航项；无菜单时回退默认导航。 */
export function buildNavItems(menus: MenuTreeNode[]): NavItem[] {
  const flat = flattenMenuNodes(menus);
  const items: NavItem[] = [];
  const seen = new Set<AppView>();
  flat.forEach((menu) => {
    const view = resolveView(menu.routePath, menu.resourceCode);
    if (!view || seen.has(view)) {
      return;
    }
    seen.add(view);
    const fallback = DEFAULT_NAV.find((item) => item.view === view);
    items.push({
      view,
      // 优先使用前端内置中文标签，避免 DB 种子数据字符集错误导致侧栏乱码
      label: fallback?.label || menu.resourceName || view,
      group: fallback?.group || '功能菜单'
    });
  });
  if (items.length === 0) {
    return DEFAULT_NAV;
  }
  return items.sort((a, b) => {
    const orderA = DEFAULT_NAV.findIndex((item) => item.view === a.view);
    const orderB = DEFAULT_NAV.findIndex((item) => item.view === b.view);
    return (orderA < 0 ? 999 : orderA) - (orderB < 0 ? 999 : orderB);
  });
}

export function groupNavItems(items: NavItem[]): Array<{ group: string; items: NavItem[] }> {
  const groups = new Map<string, NavItem[]>();
  items.forEach((item) => {
    const list = groups.get(item.group) || [];
    list.push(item);
    groups.set(item.group, list);
  });
  return Array.from(groups.entries()).map(([group, groupItems]) => ({ group, items: groupItems }));
}

const NAV_ICONS: Record<AppView, string> = {
  'master-data:areas': 'fa-map-location-dot',
  'master-data:units': 'fa-industry',
  'master-data:equipments': 'fa-gears',
  'master-data:monitor-points': 'fa-bullseye',
  'iam:orgs': 'fa-sitemap',
  'iam:users': 'fa-users',
  'iam:roles': 'fa-user-shield',
  'iam:menus': 'fa-bars',
  config: 'fa-sliders',
  audit: 'fa-clipboard-list',
  'contractor:companies': 'fa-building',
  'contractor:workers': 'fa-helmet-safety',
  'hazard:ledger': 'fa-radiation',
  'alarm:list': 'fa-bell',
  'work-permit:list': 'fa-file-signature',
  'report:overview': 'fa-chart-pie',
  'report:dashboard': 'fa-tv',
  'report:acceptance': 'fa-circle-check',
  'report:phase2': 'fa-chart-line',
  'dual-prevention:risk': 'fa-shield-halved',
  'dual-prevention:hazards': 'fa-triangle-exclamation',
  'inspection:tasks': 'fa-route',
  'location:overview': 'fa-location-dot',
  'video:events': 'fa-video',
  'simops:conflicts': 'fa-layer-group',
  'integration:reg': 'fa-cloud-arrow-up',
  'pha:projects': 'fa-flask',
  'pha:recommendations': 'fa-list-check',
  'pha:lopa': 'fa-diagram-project',
  'moc:changes': 'fa-arrows-rotate',
  'pssr:projects': 'fa-clipboard-check',
  'barrier:ledger': 'fa-shield',
  'barrier:mi': 'fa-wrench',
  'incident:list': 'fa-file-circle-exclamation',
  'governance:dashboard': 'fa-building-columns',
  'report:phase3': 'fa-chart-column'
};

/** 侧栏菜单项图标（折叠态展示）。 */
export function navIconFor(view: AppView): string {
  return NAV_ICONS[view] || 'fa-circle';
}

const SIDEBAR_COLLAPSED_KEY = 'psm.admin.sidebarCollapsed';

/** 读取侧栏折叠偏好（PC 端持久化）。 */
export function readSidebarCollapsed(): boolean {
  try {
    return window.localStorage.getItem(SIDEBAR_COLLAPSED_KEY) === '1';
  } catch {
    return false;
  }
}

/** 保存侧栏折叠偏好。 */
export function saveSidebarCollapsed(collapsed: boolean): void {
  try {
    window.localStorage.setItem(SIDEBAR_COLLAPSED_KEY, collapsed ? '1' : '0');
  } catch {
    /* ignore */
  }
}
