import React from 'react';
import { createRoot } from 'react-dom/client';
import {
  createConfigItem,
  deleteConfigItem,
  disableConfigItem,
  evaluateRules,
  fetchAuditLogs,
  fetchAlarmDetail,
  fetchAlarmHealth,
  fetchAlarms,
  closeAlarm,
  confirmAlarm,
  dispatchAlarm,
  falseCloseAlarm,
  feedbackAlarm,
  fetchConfigItems,
  fetchCurrentUser,
  fetchMyPermissions,
  publishConfigItem,
  updateConfigItem
} from '@psm/api-client';
import { clearTokens, hasAccessToken } from '@psm/auth';
import type {
  AuditLogRecord,
  AuthUser,
  AlarmDetailRecord,
  AlarmEventRecord,
  AlarmStatus,
  ConfigItemPath,
  ConfigItemRecord,
  ConfigItemRequest,
  PageResult,
  RuleEvaluationResult
} from '@psm/domain-types';
import { buildNavItems, DEFAULT_NAV, groupNavItems, navIconFor, readSidebarCollapsed, saveSidebarCollapsed, viewTitle, type AppView } from './nav';
import { BaseDataLedgerPanel, ContractorCompaniesPanel, ContractorWorkersPanel, MajorHazardsPanel, MenusPanel, OrgPanel, RolesPanel, UsersPanel } from './panels';
import { AcceptancePanel, DashboardPanel, ReportOverviewPanel } from './report-panel';
import { WorkPermitsPanel } from './work-permit-panel';
import { NotificationInboxPanel, WorkbenchTodosPanel } from './message-todo-panels';
import { FileStoragePanel } from './file-storage-panel';
import {
  DualPreventionHazardsPanel,
  DualPreventionRiskPanel,
  InspectionPanel,
  IntegrationRegPanel,
  LocationPanel,
  Phase2ReportPanel,
  SimopsPanel,
  VideoPanel
} from './phase2-panels';
import {
  BarriersPanel,
  GovernanceDashboardPanel,
  IncidentsPanel,
  MocChangesPanel,
  Phase3ReportPanel,
  PhaProjectsPanel,
  PhaRecommendationsPanel,
  PhaLopaPanel,
  MiEquipmentPanel,
  PssrProjectsPanel
} from './phase3-panels';
import {
  confirmAction,
  errorMessage,
  formatTime,
  parseObjectJson,
  StatusTag,
  stringifyJson
} from './ui-helpers';
import { AuthBootScreen, LoginView } from './login-view';
import { AppProviders, TableEmptyRow, TableSkeleton, ThemeProvider, ThemeToggle } from '@psm/ui';
import './styles.css';

const CONFIG_TYPES: Array<{ path: ConfigItemPath; label: string }> = [
  { path: 'dictionaries', label: '字典' },
  { path: 'forms', label: '表单模板' },
  { path: 'workflows', label: '审批流' },
  { path: 'rules', label: '规则' },
  { path: 'notifications/templates', label: '通知模板' },
  { path: 'attachments/policies', label: '附件策略' }
];

function App() {
  const [user, setUser] = React.useState<AuthUser | null>(null);
  const [checkingSession, setCheckingSession] = React.useState<boolean>(hasAccessToken());

  React.useEffect(() => {
    if (!hasAccessToken()) {
      setCheckingSession(false);
      return;
    }
    fetchCurrentUser()
      .then(setUser)
      .catch(() => clearTokens())
      .finally(() => setCheckingSession(false));
  }, []);

  if (checkingSession) {
    return <AuthBootScreen />;
  }

  if (!user) {
    return <LoginView onLogin={setUser} />;
  }

  return (
    <Shell
      user={user}
      onLogout={() => {
        clearTokens();
        setUser(null);
      }}
    />
  );
}

function Shell({ user, onLogout }: { user: AuthUser; onLogout: () => void }) {
  const [view, setView] = React.useState<AppView>('master-data:areas');
  const [navItems, setNavItems] = React.useState(DEFAULT_NAV);
  const [navError, setNavError] = React.useState<string>('');
  const [sidebarCollapsed, setSidebarCollapsed] = React.useState<boolean>(readSidebarCollapsed);

  function toggleSidebar() {
    setSidebarCollapsed((prev) => {
      const next = !prev;
      saveSidebarCollapsed(next);
      return next;
    });
  }

  React.useEffect(() => {
    fetchMyPermissions()
      .then((summary) => {
        const items = buildNavItems(summary.menus || []);
        setNavItems(items);
        if (!items.some((item) => item.view === view)) {
          setView(items[0]?.view || 'master-data:areas');
        }
      })
      .catch((err: unknown) => {
        setNavError(errorMessage(err, '菜单权限加载失败，已使用默认菜单'));
        setNavItems(DEFAULT_NAV);
      });
  }, []);

  const groupedNav = groupNavItems(navItems);

  function renderWorkspace() {
    if (view.startsWith('master-data:')) {
      return <BaseDataLedgerPanel tenantId={user.tenantId} view={view} />;
    }
    if (view === 'iam:orgs') {
      return <OrgPanel tenantId={user.tenantId} />;
    }
    if (view === 'iam:users') {
      return <UsersPanel tenantId={user.tenantId} />;
    }
    if (view === 'iam:roles') {
      return <RolesPanel tenantId={user.tenantId} />;
    }
    if (view === 'iam:menus') {
      return <MenusPanel tenantId={user.tenantId} />;
    }
    if (view === 'config') {
      return <ConfigRulePanel tenantId={user.tenantId} />;
    }
    if (view === 'audit') {
      return <AuditPanel tenantId={user.tenantId} />;
    }
    if (view === 'notification:inbox') {
      return <NotificationInboxPanel user={user} />;
    }
    if (view === 'workbench:todos') {
      return <WorkbenchTodosPanel user={user} />;
    }
    if (view === 'file:storage') {
      return <FileStoragePanel user={user} />;
    }
    if (view === 'contractor:companies') {
      return <ContractorCompaniesPanel tenantId={user.tenantId} />;
    }
    if (view === 'contractor:workers') {
      return <ContractorWorkersPanel tenantId={user.tenantId} />;
    }
    if (view === 'hazard:ledger') {
      return <MajorHazardsPanel tenantId={user.tenantId} />;
    }
    if (view === 'alarm:list') {
      return <AlarmsPanel tenantId={user.tenantId} />;
    }
    if (view === 'work-permit:list') {
      return <WorkPermitsPanel tenantId={user.tenantId} />;
    }
    if (view === 'report:overview') {
      return <ReportOverviewPanel tenantId={user.tenantId} />;
    }
    if (view === 'report:dashboard') {
      return <DashboardPanel tenantId={user.tenantId} />;
    }
    if (view === 'report:acceptance') {
      return <AcceptancePanel tenantId={user.tenantId} />;
    }
    if (view === 'report:phase2') {
      return <Phase2ReportPanel tenantId={user.tenantId} />;
    }
    if (view === 'dual-prevention:risk') {
      return <DualPreventionRiskPanel tenantId={user.tenantId} />;
    }
    if (view === 'dual-prevention:hazards') {
      return <DualPreventionHazardsPanel tenantId={user.tenantId} />;
    }
    if (view === 'inspection:tasks') {
      return <InspectionPanel tenantId={user.tenantId} />;
    }
    if (view === 'location:overview') {
      return <LocationPanel tenantId={user.tenantId} />;
    }
    if (view === 'video:events') {
      return <VideoPanel tenantId={user.tenantId} />;
    }
    if (view === 'simops:conflicts') {
      return <SimopsPanel tenantId={user.tenantId} />;
    }
    if (view === 'integration:reg') {
      return <IntegrationRegPanel tenantId={user.tenantId} />;
    }
    if (view === 'pha:projects') {
      return <PhaProjectsPanel tenantId={user.tenantId} />;
    }
    if (view === 'pha:recommendations') {
      return <PhaRecommendationsPanel tenantId={user.tenantId} />;
    }
    if (view === 'pha:lopa') {
      return <PhaLopaPanel tenantId={user.tenantId} />;
    }
    if (view === 'moc:changes') {
      return <MocChangesPanel tenantId={user.tenantId} />;
    }
    if (view === 'pssr:projects') {
      return <PssrProjectsPanel tenantId={user.tenantId} />;
    }
    if (view === 'barrier:ledger') {
      return <BarriersPanel tenantId={user.tenantId} />;
    }
    if (view === 'barrier:mi') {
      return <MiEquipmentPanel tenantId={user.tenantId} />;
    }
    if (view === 'incident:list') {
      return <IncidentsPanel tenantId={user.tenantId} />;
    }
    if (view === 'governance:dashboard') {
      return <GovernanceDashboardPanel tenantId={user.tenantId} />;
    }
    if (view === 'report:phase3') {
      return <Phase3ReportPanel tenantId={user.tenantId} />;
    }
    if (view.startsWith('hazard:')) {
      return <ModulePlaceholderPanel title="重大危险源" hint="该视图尚未实现" />;
    }
    return null;
  }

  return (
    <>
      <a href="#main-content" className="psm-skip-link">
        跳转到主内容
      </a>
      <main className={`app-shell${sidebarCollapsed ? ' sidebar-collapsed' : ''}`}>
      <aside className="sidebar" aria-label="主导航">
        <div className="sidebar-header">
          <div className="brand">
            <i className="fa-solid fa-shield-halved brand__icon" aria-hidden="true" />
            <span className="brand__text">PSM</span>
          </div>
          <button
            type="button"
            className="sidebar-toggle"
            onClick={toggleSidebar}
            aria-label={sidebarCollapsed ? '展开菜单' : '折叠菜单'}
            title={sidebarCollapsed ? '展开菜单' : '折叠菜单'}
          >
            <i className={`fa-solid ${sidebarCollapsed ? 'fa-angles-right' : 'fa-angles-left'}`} />
          </button>
        </div>
        <nav>
          {groupedNav.map((group) => (
            <div key={group.group} className="nav-group">
              <div className="nav-group-title" title={group.group}>
                {group.group}
              </div>
              {group.items.map((item) => (
                <button
                  key={item.view}
                  type="button"
                  className={`nav-item ${view === item.view ? 'active' : ''}`}
                  onClick={() => setView(item.view)}
                  title={sidebarCollapsed ? item.label : undefined}
                  aria-current={view === item.view ? 'page' : undefined}
                >
                  <i className={`fa-solid ${navIconFor(item.view)} nav-item__icon`} aria-hidden="true" />
                  <span className="nav-item__label">{item.label}</span>
                </button>
              ))}
            </div>
          ))}
        </nav>
      </aside>
      <section className="workspace" id="main-content" tabIndex={-1}>
        <header className="topbar">
          <div className="topbar__leading">
            <button
              type="button"
              className="topbar-toggle secondary"
              onClick={toggleSidebar}
              aria-label={sidebarCollapsed ? '展开菜单' : '折叠菜单'}
            >
              <i className={`fa-solid ${sidebarCollapsed ? 'fa-bars' : 'fa-bars-staggered'}`} />
            </button>
            <div>
              <p className="eyebrow">一期 + 二期</p>
              <h1>{viewTitle(view)}</h1>
            </div>
          </div>
          <div className="userbar">
            <ThemeToggle />
            <div className="user-avatar">{(user.displayName || user.username || '?').slice(0, 1).toUpperCase()}</div>
            <span>{user.displayName || user.username}</span>
            <button type="button" className="secondary" onClick={onLogout}>
              退出
            </button>
          </div>
        </header>
        {navError && <div className="nav-warning">{navError}</div>}
        {renderWorkspace()}
      </section>
    </main>
    </>
  );
}

function ConfigRulePanel({ tenantId }: { tenantId: number }) {
  const [activeType, setActiveType] = React.useState<ConfigItemPath>('dictionaries');
  const [records, setRecords] = React.useState<ConfigItemRecord[]>([]);
  const [keyword, setKeyword] = React.useState<string>('');
  const [status, setStatus] = React.useState<string>('');
  const [bizScene, setBizScene] = React.useState<string>('');
  const [pageNo, setPageNo] = React.useState<number>(1);
  const [total, setTotal] = React.useState<number>(0);
  const [loading, setLoading] = React.useState<boolean>(true);
  const [message, setMessage] = React.useState<string>('');
  const [error, setError] = React.useState<string>('');
  const [editing, setEditing] = React.useState<ConfigItemRecord | null | 'new'>(null);

  const load = React.useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const page = await fetchConfigItems(activeType, {
        tenantId,
        keyword,
        status,
        bizScene,
        pageNo,
        pageSize: 10
      });
      setRecords(page.records);
      setTotal(page.total);
    } catch (err: unknown) {
      setError(errorMessage(err, '配置项加载失败'));
    } finally {
      setLoading(false);
    }
  }, [activeType, tenantId, keyword, status, bizScene, pageNo]);

  React.useEffect(() => {
    load();
  }, [load]);

  function switchType(type: ConfigItemPath) {
    setActiveType(type);
    setPageNo(1);
    setMessage('');
    setError('');
  }

  async function runAction(action: () => Promise<void>, success: string) {
    setMessage('');
    setError('');
    try {
      await action();
      setMessage(success);
      await load();
    } catch (err: unknown) {
      setError(errorMessage(err, '操作失败'));
    }
  }

  const totalPages = Math.max(1, Math.ceil(total / 10));

  return (
    <div className="stacked-panels">
      <section className="content-panel">
        <div className="panel-header">
          <div>
            <h2>配置项管理</h2>
            <p>维护字典、表单、流程、规则、通知和附件策略，发布后供业务模块调用。</p>
          </div>
          <button type="button" onClick={() => setEditing('new')}>新增配置</button>
        </div>

        <div className="tabs">
          {CONFIG_TYPES.map((item) => (
            <button
              key={item.path}
              type="button"
              className={`tab-button ${activeType === item.path ? 'active' : ''}`}
              onClick={() => switchType(item.path)}
            >
              {item.label}
            </button>
          ))}
        </div>

        <div className="toolbar config-toolbar">
          <input
            placeholder="搜索编码或名称"
            value={keyword}
            onChange={(event) => {
              setKeyword(event.target.value);
              setPageNo(1);
            }}
          />
          <select value={status} onChange={(event) => {
            setStatus(event.target.value);
            setPageNo(1);
          }}>
            <option value="">全部状态</option>
            <option value="DRAFT">草稿</option>
            <option value="PUBLISHED">已发布</option>
            <option value="DISABLED">已停用</option>
          </select>
          <input
            placeholder="业务场景"
            value={bizScene}
            onChange={(event) => {
              setBizScene(event.target.value);
              setPageNo(1);
            }}
          />
          <button type="button" className="secondary" onClick={load}>刷新</button>
        </div>

        {message && <div className="success">{message}</div>}
        {error && <div className="error">{error}</div>}
        <div className="psm-table-scroll">
          {loading ? (
            <TableSkeleton rows={4} columns={5} hasActions />
          ) : (
          <table className="psm-data-table">
              <thead>
                <tr>
                  <th>编码</th>
                  <th>名称</th>
                  <th>场景</th>
                  <th>版本</th>
                  <th>状态</th>
                  <th>更新时间</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                {records.map((record) => (
                  <tr key={record.id}>
                    <td>{record.configCode}</td>
                    <td>{record.configName}</td>
                    <td>{record.bizScene || '-'}</td>
                    <td>v{record.versionNo || 1}</td>
                    <td><StatusTag status={record.status} /></td>
                    <td>{formatTime(record.updatedAt || record.createdAt)}</td>
                    <td className="actions">
                      <button type="button" className="secondary" onClick={() => setEditing(record)}>编辑</button>
                      {record.status !== 'PUBLISHED' && (
                        <button
                          type="button"
                          onClick={() => runAction(() => publishConfigItem(record.id, tenantId), '配置已发布')}
                        >
                          发布
                        </button>
                      )}
                      {record.status === 'PUBLISHED' && (
                        <button
                          type="button"
                          className="warning"
                          onClick={() => confirmAction('停用后业务模块将不再读取该发布配置，确认停用？', () => runAction(() => disableConfigItem(record.id, tenantId), '配置已停用'))}
                        >
                          停用
                        </button>
                      )}
                      <button
                        type="button"
                        className="danger"
                        onClick={() => confirmAction('删除会保留审计记录，已发布配置需先停用，确认删除？', () => runAction(() => deleteConfigItem(record.id, tenantId), '配置已删除'))}
                      >
                        删除
                      </button>
                    </td>
                  </tr>
                ))}
                {records.length === 0 && (
                  <TableEmptyRow
                    colSpan={7}
                    message="暂无配置项"
                    hasActiveFilters={Boolean(keyword || status || bizScene)}
                    onClearFilters={() => {
                      setKeyword('');
                      setStatus('');
                      setBizScene('');
                      setPageNo(1);
                    }}
                  />
                )}
              </tbody>
            </table>
          )}
          </div>
          <div className="pager">
              <span>共 {total} 条，第 {pageNo} / {totalPages} 页</span>
              <div>
                <button className="secondary" disabled={pageNo <= 1} onClick={() => setPageNo(pageNo - 1)}>上一页</button>
                <button className="secondary" disabled={pageNo >= totalPages} onClick={() => setPageNo(pageNo + 1)}>下一页</button>
              </div>
            </div>

        {editing && (
          <ConfigItemDialog
            tenantId={tenantId}
            type={activeType}
            record={editing === 'new' ? null : editing}
            onClose={() => setEditing(null)}
            onSaved={async () => {
              setEditing(null);
              setMessage('保存成功');
              await load();
            }}
            onError={setError}
          />
        )}
      </section>

      <RuleEvaluationPanel tenantId={tenantId} />
    </div>
  );
}

function ConfigItemDialog({
  tenantId,
  type,
  record,
  onClose,
  onSaved,
  onError
}: {
  tenantId: number;
  type: ConfigItemPath;
  record: ConfigItemRecord | null;
  onClose: () => void;
  onSaved: () => Promise<void>;
  onError: (error: string) => void;
}) {
  const [form, setForm] = React.useState<ConfigItemRequest>({
    tenantId,
    configCode: record?.configCode || '',
    configName: record?.configName || '',
    bizScene: record?.bizScene || '',
    content: record?.content || {},
    remark: record?.remark || '',
    status: record?.status || 'DRAFT'
  });
  const [contentText, setContentText] = React.useState<string>(stringifyJson(record?.content || {}));
  const [saving, setSaving] = React.useState<boolean>(false);
  const [jsonError, setJsonError] = React.useState<string>('');

  async function submit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSaving(true);
    setJsonError('');
    onError('');
    try {
      const content = parseObjectJson(contentText);
      const payload = { ...form, content };
      if (record) {
        await updateConfigItem(type, record.id, payload);
      } else {
        await createConfigItem(type, payload);
      }
      await onSaved();
    } catch (err: unknown) {
      if (err instanceof SyntaxError) {
        setJsonError(err.message);
      } else {
        onError(errorMessage(err, '保存失败'));
      }
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="modal-backdrop">
      <form className="modal config-modal" onSubmit={submit}>
        <header>
          <h2>{record ? '编辑配置' : '新增配置'}</h2>
          <button type="button" className="icon-button" onClick={onClose}>×</button>
        </header>
        <div className="form-grid">
          <label>
            <span>配置编码</span>
            <input
              value={form.configCode}
              disabled={Boolean(record)}
              onChange={(event) => setForm({ ...form, configCode: event.target.value })}
              required
            />
          </label>
          <label>
            <span>配置名称</span>
            <input
              value={form.configName}
              onChange={(event) => setForm({ ...form, configName: event.target.value })}
              required
            />
          </label>
          <label>
            <span>业务场景</span>
            <input value={form.bizScene || ''} onChange={(event) => setForm({ ...form, bizScene: event.target.value })} />
          </label>
          <label>
            <span>状态</span>
            <select value={form.status || 'DRAFT'} onChange={(event) => setForm({ ...form, status: event.target.value })}>
              <option value="DRAFT">草稿</option>
              <option value="PUBLISHED">已发布</option>
              <option value="DISABLED">已停用</option>
            </select>
          </label>
          <label className="wide-field">
            <span>备注</span>
            <input value={form.remark || ''} onChange={(event) => setForm({ ...form, remark: event.target.value })} />
          </label>
          <label className="wide-field">
            <span>配置内容 JSON</span>
            <textarea value={contentText} onChange={(event) => setContentText(event.target.value)} rows={10} />
          </label>
          {jsonError && <div className="error inline-error">{jsonError}</div>}
        </div>
        <footer>
          <button type="button" className="secondary" onClick={onClose}>取消</button>
          <button type="submit" disabled={saving}>{saving ? '保存中...' : '保存'}</button>
        </footer>
      </form>
    </div>
  );
}

function RuleEvaluationPanel({ tenantId }: { tenantId: number }) {
  const [scene, setScene] = React.useState<string>('WORK_PERMIT');
  const [ruleCode, setRuleCode] = React.useState<string>('');
  const [factsText, setFactsText] = React.useState<string>(stringifyJson({ workType: 'HOT_WORK', areaRiskLevel: 'HIGH' }));
  const [results, setResults] = React.useState<RuleEvaluationResult[]>([]);
  const [running, setRunning] = React.useState<boolean>(false);
  const [error, setError] = React.useState<string>('');

  async function submit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setRunning(true);
    setError('');
    setResults([]);
    try {
      const facts = parseObjectJson(factsText);
      const response = await evaluateRules({
        tenantId,
        scene,
        ruleCode: ruleCode || undefined,
        facts
      });
      setResults(response);
    } catch (err: unknown) {
      setError(errorMessage(err, '规则评估失败'));
    } finally {
      setRunning(false);
    }
  }

  return (
    <section className="content-panel">
      <div className="panel-header">
        <div>
          <h2>规则评估验证</h2>
          <p>按业务场景提交事实数据，验证规则返回的阻断、预警和解释信息。</p>
        </div>
      </div>
      <form className="evaluation-form" onSubmit={submit}>
        <div className="form-grid">
          <label>
            <span>业务场景</span>
            <input value={scene} onChange={(event) => setScene(event.target.value)} required />
          </label>
          <label>
            <span>规则编码</span>
            <input value={ruleCode} onChange={(event) => setRuleCode(event.target.value)} placeholder="留空评估场景下全部规则" />
          </label>
          <label className="wide-field">
            <span>事实数据 JSON</span>
            <textarea value={factsText} onChange={(event) => setFactsText(event.target.value)} rows={8} />
          </label>
        </div>
        {error && <div className="error inline-error">{error}</div>}
        <footer className="form-actions">
          <button type="submit" disabled={running}>{running ? '评估中...' : '执行评估'}</button>
        </footer>
      </form>
      <div className="evaluation-results">
        {results.length === 0 && !running && <div className="empty">暂无评估结果</div>}
        {results.map((item, index) => (
          <div className="result-row" key={`${item.ruleCode || 'rule'}-${index}`}>
            <div>
              <strong>{item.ruleCode || '未命名规则'}</strong>
              <StatusTag status={item.level || (item.passed ? 'PASS' : 'BLOCK')} />
            </div>
            <p>{item.message || '-'}</p>
            {item.evidence && item.evidence.length > 0 && <p>依据：{item.evidence.join('；')}</p>}
            {item.suggestion && <p>建议：{item.suggestion}</p>}
          </div>
        ))}
      </div>
    </section>
  );
}

function AlarmsPanel({ tenantId }: { tenantId: number }) {
  const [health, setHealth] = React.useState<string>('');
  const [page, setPage] = React.useState<PageResult<AlarmEventRecord> | null>(null);
  const [keyword, setKeyword] = React.useState('');
  const [status, setStatus] = React.useState('');
  const [alarmLevel, setAlarmLevel] = React.useState('');
  const [selectedId, setSelectedId] = React.useState<number | null>(null);
  const [detail, setDetail] = React.useState<AlarmDetailRecord | null>(null);
  const [actionNote, setActionNote] = React.useState('');
  const [falseCloseReason, setFalseCloseReason] = React.useState('');
  const [error, setError] = React.useState<string>('');
  const [message, setMessage] = React.useState<string>('');
  const [loading, setLoading] = React.useState<boolean>(true);
  const [acting, setActing] = React.useState<boolean>(false);

  const loadList = React.useCallback(() => {
    setLoading(true);
    setError('');
    return Promise.all([
      fetchAlarmHealth(),
      fetchAlarms({
        tenantId,
        pageNo: 1,
        pageSize: 20,
        keyword: keyword || undefined,
        status: status || undefined,
        alarmLevel: alarmLevel || undefined
      })
    ])
      .then(([healthInfo, alarmPage]) => {
        setHealth(`${healthInfo.service} ${healthInfo.version}`);
        setPage(alarmPage);
      })
      .catch((err: unknown) => setError(errorMessage(err, '报警服务不可用')))
      .finally(() => setLoading(false));
  }, [tenantId, keyword, status, alarmLevel]);

  const loadDetail = React.useCallback(
    (id: number) => {
      setSelectedId(id);
      setDetail(null);
      setActionNote('');
      setFalseCloseReason('');
      fetchAlarmDetail(id, tenantId)
        .then(setDetail)
        .catch((err: unknown) => setError(errorMessage(err, '加载报警详情失败')));
    },
    [tenantId]
  );

  React.useEffect(() => {
    loadList();
  }, [loadList]);

  async function runAction(action: () => Promise<AlarmEventRecord>, success: string) {
    if (selectedId == null) {
      return;
    }
    setActing(true);
    setError('');
    try {
      await action();
      setMessage(success);
      await loadList();
      await loadDetail(selectedId);
    } catch (err: unknown) {
      setError(errorMessage(err, '操作失败'));
    } finally {
      setActing(false);
    }
  }

  function availableActions(current: AlarmStatus): string[] {
    switch (current) {
      case 'NEW':
      case 'ESCALATED':
        return ['confirm', 'false-close'];
      case 'CONFIRMED':
        return ['dispatch', 'false-close'];
      case 'IN_PROGRESS':
        return ['feedback', 'false-close'];
      case 'PENDING_REVIEW':
        return ['close'];
      default:
        return [];
    }
  }

  return (
    <section className="content-panel">
      <div className="panel-header">
        <div>
          <h2>实时报警</h2>
          <p>接入、去重合并、确认派发与关闭闭环（第 3 迭代批次 6）。</p>
        </div>
        <button type="button" className="secondary" onClick={() => loadList()}>
          刷新
        </button>
      </div>
      {health && <p className="hint">{health}</p>}
      {message && <div className="success-banner">{message}</div>}
      {error && <div className="error-banner">{error}</div>}
      <div className="toolbar">
        <input
          placeholder="编号/标题"
          value={keyword}
          onChange={(event) => setKeyword(event.target.value)}
        />
        <select value={status} onChange={(event) => setStatus(event.target.value)}>
          <option value="">全部状态</option>
          <option value="NEW">NEW</option>
          <option value="CONFIRMED">CONFIRMED</option>
          <option value="IN_PROGRESS">IN_PROGRESS</option>
          <option value="PENDING_REVIEW">PENDING_REVIEW</option>
          <option value="ESCALATED">ESCALATED</option>
          <option value="CLOSED">CLOSED</option>
          <option value="FALSE_CLOSED">FALSE_CLOSED</option>
        </select>
        <select value={alarmLevel} onChange={(event) => setAlarmLevel(event.target.value)}>
          <option value="">全部等级</option>
          <option value="LEVEL_1">LEVEL_1</option>
          <option value="LEVEL_2">LEVEL_2</option>
          <option value="LEVEL_3">LEVEL_3</option>
          <option value="LEVEL_4">LEVEL_4</option>
        </select>
        <button type="button" className="secondary" onClick={() => loadList()}>
          查询
        </button>
      </div>
      <div className="table-wrap">
        <div className="psm-table-scroll">
          {loading ? (
            <TableSkeleton rows={4} columns={8} hasActions />
          ) : page ? (
          <table className="psm-data-table">
            <thead>
              <tr>
                <th>编号</th>
                <th>等级</th>
                <th>状态</th>
                <th>标题</th>
                <th>来源</th>
                <th>次数</th>
                <th>危险源</th>
                <th>最近发生</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              {page.records.length === 0 ? (
                <TableEmptyRow
                  colSpan={9}
                  message="暂无报警数据"
                  hasActiveFilters={Boolean(keyword || status || alarmLevel)}
                  onClearFilters={() => {
                    setKeyword('');
                    setStatus('');
                    setAlarmLevel('');
                  }}
                />
              ) : (
                page.records.map((item) => (
                  <tr key={item.id} className={selectedId === item.id ? 'selected-row' : ''}>
                    <td>{item.alarmNo}</td>
                    <td>{item.alarmLevel}</td>
                    <td>
                      <StatusTag status={item.status} />
                    </td>
                    <td>{item.title}</td>
                    <td>{item.sourceType}</td>
                    <td>{item.occurrenceCount ?? 1}</td>
                    <td>{item.hazardId ?? '-'}</td>
                    <td>{formatTime(item.lastOccurredAt)}</td>
                    <td>
                      <button type="button" className="linkish" onClick={() => loadDetail(item.id)}>
                        详情
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
          ) : null}
        </div>
      </div>

      {detail && (
        <div className="detail-panel">
          <h3>
            {detail.event.alarmNo} · {detail.event.title}
          </h3>
          <p>
            等级 {detail.event.alarmLevel} · 状态 <StatusTag status={detail.event.status} /> · 危险源 ID{' '}
            {detail.event.hazardId ?? '-'}
          </p>
          <div className="detail-grid">
            <div>
              <h4>发生明细（{detail.occurrences.length}）</h4>
              <ul>
                {detail.occurrences.map((item) => (
                  <li key={item.id}>
                    {formatTime(item.occurredAt)} {item.rawValue ? `· ${item.rawValue}` : ''}
                  </li>
                ))}
              </ul>
            </div>
            <div>
              <h4>处置记录（{detail.actions.length}）</h4>
              <ul>
                {detail.actions.map((item) => (
                  <li key={item.id}>
                    {item.actionType} · {item.operatorName || '-'} · {formatTime(item.operatedAt)}
                    {item.actionContent ? ` · ${item.actionContent}` : ''}
                  </li>
                ))}
              </ul>
            </div>
          </div>
          {availableActions(detail.event.status).length > 0 && (
            <div className="form-actions">
              <input
                placeholder="处置说明（可选）"
                value={actionNote}
                onChange={(event) => setActionNote(event.target.value)}
              />
              {availableActions(detail.event.status).includes('confirm') && (
                <button
                  type="button"
                  disabled={acting}
                  onClick={() => runAction(() => confirmAlarm(detail.event.id, tenantId, { content: actionNote }), '已确认')}
                >
                  确认
                </button>
              )}
              {availableActions(detail.event.status).includes('dispatch') && (
                <button
                  type="button"
                  disabled={acting}
                  onClick={() =>
                    runAction(
                      () => dispatchAlarm(detail.event.id, tenantId, { content: actionNote, assignee: '值班员' }),
                      '已派发'
                    )
                  }
                >
                  派发
                </button>
              )}
              {availableActions(detail.event.status).includes('feedback') && (
                <button
                  type="button"
                  disabled={acting}
                  onClick={() => runAction(() => feedbackAlarm(detail.event.id, tenantId, { content: actionNote }), '已反馈')}
                >
                  处置反馈
                </button>
              )}
              {availableActions(detail.event.status).includes('close') && (
                <button
                  type="button"
                  disabled={acting}
                  onClick={() => runAction(() => closeAlarm(detail.event.id, tenantId, { content: actionNote }), '已关闭')}
                >
                  复核关闭
                </button>
              )}
              {availableActions(detail.event.status).includes('false-close') && (
                <>
                  <input
                    placeholder="误报原因（必填）"
                    value={falseCloseReason}
                    onChange={(event) => setFalseCloseReason(event.target.value)}
                  />
                  <button
                    type="button"
                    className="secondary"
                    disabled={acting || !falseCloseReason.trim()}
                    onClick={() =>
                      runAction(
                        () => falseCloseAlarm(detail.event.id, tenantId, { reason: falseCloseReason.trim() }),
                        '已误报关闭'
                      )
                    }
                  >
                    误报关闭
                  </button>
                </>
              )}
            </div>
          )}
        </div>
      )}
    </section>
  );
}

function ModulePlaceholderPanel({ title, hint }: { title: string; hint: string }) {
  return (
    <section className="content-panel">
      <div className="panel-header">
        <div>
          <h2>{title}</h2>
          <p>{hint}</p>
        </div>
      </div>
      <div className="empty">功能开发中，敬请期待。</div>
    </section>
  );
}

function AuditPanel({ tenantId }: { tenantId: number }) {
  const [page, setPage] = React.useState<PageResult<AuditLogRecord> | null>(null);
  const [loading, setLoading] = React.useState<boolean>(true);
  const [error, setError] = React.useState<string>('');
  const [bizTypePrefix, setBizTypePrefix] = React.useState('');

  const load = React.useCallback(() => {
    setLoading(true);
    setError('');
    fetchAuditLogs({
      tenantId,
      pageNo: 1,
      pageSize: 20,
      bizTypePrefix: bizTypePrefix || undefined
    })
      .then(setPage)
      .catch((err: unknown) => setError(errorMessage(err, '审计日志加载失败')))
      .finally(() => setLoading(false));
  }, [tenantId, bizTypePrefix]);

  React.useEffect(() => {
    load();
  }, [load]);

  return (
    <section className="content-panel">
      <div className="panel-header">
        <div>
          <h2>审计日志</h2>
          <p>查询主数据、承包商与重大危险源的关键变更（中央审计库）。</p>
        </div>
      </div>
      <div className="toolbar">
        <select value={bizTypePrefix} onChange={(event) => setBizTypePrefix(event.target.value)}>
          <option value="">全部类型</option>
          <option value="CONTRACTOR_">承包商（CONTRACTOR_*）</option>
          <option value="MAJOR_HAZARD">重大危险源（MAJOR_HAZARD*）</option>
          <option value="ALARM">报警（ALARM*）</option>
          <option value="AREA">区域（AREA）</option>
          <option value="UNIT">装置（UNIT）</option>
        </select>
        <button type="button" className="secondary" onClick={load}>
          刷新
        </button>
      </div>
      {error && <div className="error">{error}</div>}
      <div className="psm-table-scroll">
        {loading ? (
          <TableSkeleton rows={4} columns={6} />
        ) : (
          <table className="psm-data-table">
            <thead>
              <tr>
                <th>操作时间</th>
                <th>操作人</th>
                <th>动作</th>
                <th>对象类型</th>
                <th>对象 ID</th>
                <th>结果</th>
              </tr>
            </thead>
            <tbody>
              {(page?.records || []).map((record) => (
                <tr key={record.id}>
                  <td>{formatTime(record.operatedAt)}</td>
                  <td>{record.operatorName || '-'}</td>
                  <td>{record.action}</td>
                  <td>{record.bizType}</td>
                  <td>{record.bizId || '-'}</td>
                  <td><StatusTag status={record.result} /></td>
                </tr>
              ))}
              {(!page || page.records.length === 0) && (
                <TableEmptyRow
                  colSpan={6}
                  message="暂无审计记录"
                  hasActiveFilters={Boolean(bizTypePrefix)}
                  onClearFilters={() => setBizTypePrefix('')}
                />
              )}
            </tbody>
          </table>
        )}
      </div>
    </section>
  );
}

createRoot(document.getElementById('root') as HTMLElement).render(
  <ThemeProvider>
    <AppProviders>
      <App />
    </AppProviders>
  </ThemeProvider>
);
