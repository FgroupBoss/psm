import React from 'react';
import { createRoot } from 'react-dom/client';
import {
  createBaseData,
  deleteBaseData,
  disableBaseData,
  enableBaseData,
  fetchAuditLogs,
  fetchBaseDataPage,
  fetchCurrentUser,
  login,
  updateBaseData
} from '@psm/api-client';
import { clearTokens, hasAccessToken } from '@psm/auth';
import type { AuditLogRecord, AuthUser, BaseDataRecord, BaseDataRequest, PageResult } from '@psm/domain-types';
import './styles.css';

const AREA_TYPE = 'areas';

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
  const [view, setView] = React.useState<'master-data' | 'audit'>('master-data');

  return (
    <main className="app-shell">
      <aside className="sidebar">
        <div className="brand">PSM</div>
        <nav>
          <button className={`nav-item ${view === 'master-data' ? 'active' : ''}`} onClick={() => setView('master-data')}>
            基础主数据
          </button>
          <button className={`nav-item ${view === 'audit' ? 'active' : ''}`} onClick={() => setView('audit')}>
            权限审计
          </button>
          <button className="nav-item" disabled>危险工作票</button>
          <button className="nav-item" disabled>报警中心</button>
        </nav>
      </aside>
      <section className="workspace">
        <header className="topbar">
          <div>
            <p className="eyebrow">一期试点版</p>
            <h1>{view === 'audit' ? '权限审计' : '基础主数据'}</h1>
          </div>
          <div className="userbar">
            <span>{user.displayName || user.username}</span>
            <button type="button" onClick={onLogout}>
              退出
            </button>
          </div>
        </header>
        {view === 'audit' ? <AuditPanel tenantId={user.tenantId} /> : <AreaLedgerPanel tenantId={user.tenantId} />}
      </section>
    </main>
  );
}

function AreaLedgerPanel({ tenantId }: { tenantId: number }) {
  const [records, setRecords] = React.useState<BaseDataRecord[]>([]);
  const [keyword, setKeyword] = React.useState<string>('');
  const [status, setStatus] = React.useState<string>('');
  const [pageNo, setPageNo] = React.useState<number>(1);
  const [total, setTotal] = React.useState<number>(0);
  const [loading, setLoading] = React.useState<boolean>(true);
  const [message, setMessage] = React.useState<string>('');
  const [error, setError] = React.useState<string>('');
  const [editing, setEditing] = React.useState<BaseDataRecord | null | 'new'>(null);

  const load = React.useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const page = await fetchBaseDataPage(AREA_TYPE, { tenantId, keyword, status, pageNo, pageSize: 10 });
      setRecords(page.records);
      setTotal(page.total);
    } catch (err: unknown) {
      setError(errorMessage(err, '区域台账加载失败'));
    } finally {
      setLoading(false);
    }
  }, [tenantId, keyword, status, pageNo]);

  React.useEffect(() => {
    load();
  }, [load]);

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
    <section className="content-panel">
      <div className="panel-header">
        <div>
          <h2>区域台账</h2>
          <p>支持新增、编辑、启停、删除、搜索和分页，变更写入审计日志。</p>
        </div>
        <button type="button" onClick={() => setEditing('new')}>新增区域</button>
      </div>

      <div className="toolbar">
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
          <option value="ENABLED">启用</option>
          <option value="DISABLED">禁用</option>
        </select>
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
                <th>风险等级</th>
                <th>重大危险源</th>
                <th>排序</th>
                <th>状态</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              {records.map((record) => (
                <tr key={record.id}>
                  <td>{record.code}</td>
                  <td>{record.name}</td>
                  <td>{record.riskLevel || '-'}</td>
                  <td>{record.majorHazardFlag ? '是' : '否'}</td>
                  <td>{record.sortNo ?? 0}</td>
                  <td><StatusTag status={record.status} /></td>
                  <td className="actions">
                    <button type="button" className="secondary" onClick={() => setEditing(record)}>编辑</button>
                    {record.status === 'ENABLED' ? (
                      <button type="button" className="warning" onClick={() => confirmAction('禁用父级区域可能影响下级引用，确认禁用？', () => runAction(() => disableBaseData(AREA_TYPE, record.id, tenantId), '区域已禁用'))}>禁用</button>
                    ) : (
                      <button type="button" className="secondary" onClick={() => runAction(() => enableBaseData(AREA_TYPE, record.id, tenantId), '区域已启用')}>启用</button>
                    )}
                    <button type="button" className="danger" onClick={() => confirmAction('删除后仅做软删除，确认删除？', () => runAction(() => deleteBaseData(AREA_TYPE, record.id, tenantId), '区域已删除'))}>删除</button>
                  </td>
                </tr>
              ))}
              {records.length === 0 && (
                <tr>
                  <td colSpan={7} className="empty">暂无区域数据</td>
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
        <AreaFormDialog
          tenantId={tenantId}
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
  );
}

function AreaFormDialog({
  tenantId,
  record,
  onClose,
  onSaved,
  onError
}: {
  tenantId: number;
  record: BaseDataRecord | null;
  onClose: () => void;
  onSaved: () => Promise<void>;
  onError: (error: string) => void;
}) {
  const [form, setForm] = React.useState<BaseDataRequest>({
    tenantId,
    code: record?.code || '',
    name: record?.name || '',
    type: record?.type || '',
    riskLevel: record?.riskLevel || '',
    majorHazardFlag: Boolean(record?.majorHazardFlag),
    sortNo: record?.sortNo || 0,
    status: record?.status || 'ENABLED'
  });
  const [saving, setSaving] = React.useState<boolean>(false);

  async function submit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSaving(true);
    onError('');
    try {
      if (record) {
        await updateBaseData(AREA_TYPE, record.id, form);
      } else {
        await createBaseData(AREA_TYPE, form);
      }
      await onSaved();
    } catch (err: unknown) {
      onError(errorMessage(err, '保存失败'));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="modal-backdrop">
      <form className="modal" onSubmit={submit}>
        <header>
          <h2>{record ? '编辑区域' : '新增区域'}</h2>
          <button type="button" className="icon-button" onClick={onClose}>×</button>
        </header>
        <div className="form-grid">
          <label>
            <span>区域编码</span>
            <input
              value={form.code || ''}
              disabled={Boolean(record)}
              onChange={(event) => setForm({ ...form, code: event.target.value })}
              required
            />
          </label>
          <label>
            <span>区域名称</span>
            <input value={form.name} onChange={(event) => setForm({ ...form, name: event.target.value })} required />
          </label>
          <label>
            <span>区域类型</span>
            <input value={form.type || ''} onChange={(event) => setForm({ ...form, type: event.target.value })} />
          </label>
          <label>
            <span>风险等级</span>
            <select value={form.riskLevel || ''} onChange={(event) => setForm({ ...form, riskLevel: event.target.value })}>
              <option value="">未设置</option>
              <option value="LOW">低</option>
              <option value="MEDIUM">中</option>
              <option value="HIGH">高</option>
              <option value="MAJOR">重大</option>
            </select>
          </label>
          <label>
            <span>排序号</span>
            <input
              type="number"
              value={form.sortNo ?? 0}
              onChange={(event) => setForm({ ...form, sortNo: Number(event.target.value) })}
            />
          </label>
          <label>
            <span>状态</span>
            <select value={form.status || 'ENABLED'} onChange={(event) => setForm({ ...form, status: event.target.value })}>
              <option value="ENABLED">启用</option>
              <option value="DISABLED">禁用</option>
            </select>
          </label>
          <label className="checkbox-row">
            <input
              type="checkbox"
              checked={Boolean(form.majorHazardFlag)}
              onChange={(event) => setForm({ ...form, majorHazardFlag: event.target.checked })}
            />
            <span>重大危险源区域</span>
          </label>
        </div>
        <footer>
          <button type="button" className="secondary" onClick={onClose}>取消</button>
          <button type="submit" disabled={saving}>{saving ? '保存中...' : '保存'}</button>
        </footer>
      </form>
    </div>
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

function StatusTag({ status }: { status: string }) {
  const enabled = status === 'ENABLED' || status === 'SUCCESS';
  return <span className={`status-tag ${enabled ? 'enabled' : 'disabled'}`}>{status}</span>;
}

function confirmAction(message: string, action: () => void) {
  if (window.confirm(message)) {
    action();
  }
}

function errorMessage(err: unknown, fallback: string) {
  return err instanceof Error ? err.message : fallback;
}

function formatTime(value?: string) {
  if (!value) {
    return '-';
  }
  return value.replace('T', ' ').slice(0, 19);
}

createRoot(document.getElementById('root') as HTMLElement).render(<App />);
