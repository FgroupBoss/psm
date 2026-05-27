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
  | 'alarm:list';

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
  MENU_ALM_LIST: 'alarm:list'
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
  { view: 'alarm:list', label: '实时报警', group: '报警中心' }
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
  'alarm:list': '实时报警'
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
      label: menu.resourceName || fallback?.label || view,
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
