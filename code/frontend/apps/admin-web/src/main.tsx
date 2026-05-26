import React from 'react';
import { createRoot } from 'react-dom/client';
import {
  createConfigItem,
  deleteConfigItem,
  disableConfigItem,
  evaluateRules,
  fetchAuditLogs,
  fetchConfigItems,
  fetchCurrentUser,
  fetchMyPermissions,
  login,
  publishConfigItem,
  updateConfigItem
} from '@psm/api-client';
import { clearTokens, hasAccessToken } from '@psm/auth';
import type {
  AuditLogRecord,
  AuthUser,
  ConfigItemPath,
  ConfigItemRecord,
  ConfigItemRequest,
  PageResult,
  RuleEvaluationResult
} from '@psm/domain-types';
import { buildNavItems, DEFAULT_NAV, groupNavItems, viewTitle, type AppView } from './nav';
import { BaseDataLedgerPanel, MenusPanel, OrgPanel, RolesPanel, UsersPanel } from './panels';
import {
  confirmAction,
  errorMessage,
  formatTime,
  parseObjectJson,
  StatusTag,
  stringifyJson
} from './ui-helpers';
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
  const [error, setError] = React.useState<string>('');

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
    return <main className="boot">正在恢复登录状态...</main>;
  }

  if (!user) {
    return <LoginView onLogin={setUser} error={error} setError={setError} />;
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

interface LoginViewProps {
  onLogin: (user: AuthUser) => void;
  error: string;
  setError: (error: string) => void;
}

function LoginView({ onLogin, error, setError }: LoginViewProps) {
  const [tenantId, setTenantId] = React.useState<string>('1');
  const [username, setUsername] = React.useState<string>('admin');
  const [password, setPassword] = React.useState<string>('');
  const [submitting, setSubmitting] = React.useState<boolean>(false);

  async function submit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSubmitting(true);
    setError('');
    try {
      const result = await login({
        tenantId: Number(tenantId),
        username,
        password
      });
      onLogin(result.user);
    } catch (err: unknown) {
      setError(errorMessage(err, '登录失败'));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <main className="login-page">
      <form className="login-panel" onSubmit={submit}>
        <div>
          <p className="eyebrow">PSM 安全管理平台</p>
          <h1>管理端登录</h1>
        </div>
        <label>
          <span>租户 ID</span>
          <input value={tenantId} onChange={(event) => setTenantId(event.target.value)} inputMode="numeric" />
        </label>
        <label>
          <span>用户名</span>
          <input value={username} onChange={(event) => setUsername(event.target.value)} autoComplete="username" />
        </label>
        <label>
          <span>密码</span>
          <input
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            type="password"
            autoComplete="current-password"
          />
        </label>
        {error && <div className="error">{error}</div>}
        <button type="submit" disabled={submitting}>
          {submitting ? '登录中...' : '登录'}
        </button>
      </form>
    </main>
  );
}

function Shell({ user, onLogout }: { user: AuthUser; onLogout: () => void }) {
  const [view, setView] = React.useState<AppView>('master-data:areas');
  const [navItems, setNavItems] = React.useState(DEFAULT_NAV);
  const [navError, setNavError] = React.useState<string>('');

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
    return null;
  }

  return (
    <main className="app-shell">
      <aside className="sidebar">
        <div className="brand">PSM</div>
        <nav>
          {groupedNav.map((group) => (
            <div key={group.group} className="nav-group">
              <div className="nav-group-title">{group.group}</div>
              {group.items.map((item) => (
                <button
                  key={item.view}
                  className={`nav-item ${view === item.view ? 'active' : ''}`}
                  onClick={() => setView(item.view)}
                >
                  {item.label}
                </button>
              ))}
            </div>
          ))}
          <button className="nav-item" disabled>
            危险工作票
          </button>
          <button className="nav-item" disabled>
            报警中心
          </button>
        </nav>
      </aside>
      <section className="workspace">
        <header className="topbar">
          <div>
            <p className="eyebrow">一期试点版</p>
            <h1>{viewTitle(view)}</h1>
          </div>
          <div className="userbar">
            <span>{user.displayName || user.username}</span>
            <button type="button" onClick={onLogout}>
              退出
            </button>
          </div>
        </header>
        {navError && <div className="nav-warning">{navError}</div>}
        {renderWorkspace()}
      </section>
    </main>
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
        {loading && <div className="empty">正在加载...</div>}
        {!loading && (
          <>
            <table>
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
                  <tr>
                    <td colSpan={7} className="empty">暂无配置项</td>
                  </tr>
                )}
              </tbody>
            </table>
            <div className="pager">
              <span>共 {total} 条，第 {pageNo} / {totalPages} 页</span>
              <div>
                <button className="secondary" disabled={pageNo <= 1} onClick={() => setPageNo(pageNo - 1)}>上一页</button>
                <button className="secondary" disabled={pageNo >= totalPages} onClick={() => setPageNo(pageNo + 1)}>下一页</button>
              </div>
            </div>
          </>
        )}

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

function AuditPanel({ tenantId }: { tenantId: number }) {
  const [page, setPage] = React.useState<PageResult<AuditLogRecord> | null>(null);
  const [loading, setLoading] = React.useState<boolean>(true);
  const [error, setError] = React.useState<string>('');

  React.useEffect(() => {
    setLoading(true);
    fetchAuditLogs({ tenantId, pageNo: 1, pageSize: 20 })
      .then(setPage)
      .catch((err: unknown) => setError(errorMessage(err, '审计日志加载失败')))
      .finally(() => setLoading(false));
  }, [tenantId]);

  return (
    <section className="content-panel">
      <div className="panel-header">
        <div>
          <h2>审计日志</h2>
          <p>按统一审计表查询主数据和权限类变更。</p>
        </div>
      </div>
      {loading && <div className="empty">正在加载...</div>}
      {error && <div className="error">{error}</div>}
      {!loading && !error && (
        <table>
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
              <tr>
                <td colSpan={6} className="empty">暂无审计记录</td>
              </tr>
            )}
          </tbody>
        </table>
      )}
    </section>
  );
}

createRoot(document.getElementById('root') as HTMLElement).render(<App />);
