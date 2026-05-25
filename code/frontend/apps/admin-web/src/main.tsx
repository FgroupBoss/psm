import React from 'react';
import { createRoot } from 'react-dom/client';
import { fetchCurrentUser, fetchMasterDataPage, login } from '@psm/api-client';
import { clearTokens, hasAccessToken } from '@psm/auth';
import type { AuthUser, MasterDataRecord } from '@psm/domain-types';
import './styles.css';

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
      setError(err instanceof Error ? err.message : '登录失败');
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
  return (
    <main className="app-shell">
      <aside className="sidebar">
        <div className="brand">PSM</div>
        <nav>
          <button className="nav-item active">基础主数据</button>
          <button className="nav-item">权限审计</button>
          <button className="nav-item">危险工作票</button>
          <button className="nav-item">报警中心</button>
        </nav>
      </aside>
      <section className="workspace">
        <header className="topbar">
          <div>
            <p className="eyebrow">一期试点版</p>
            <h1>基础主数据</h1>
          </div>
          <div className="userbar">
            <span>{user.displayName || user.username}</span>
            <button type="button" onClick={onLogout}>
              退出
            </button>
          </div>
        </header>
        <MasterDataPanel tenantId={user.tenantId} />
      </section>
    </main>
  );
}

function MasterDataPanel({ tenantId }: { tenantId: number }) {
  const [records, setRecords] = React.useState<MasterDataRecord[]>([]);
  const [loading, setLoading] = React.useState<boolean>(true);
  const [error, setError] = React.useState<string>('');

  React.useEffect(() => {
    setLoading(true);
    fetchMasterDataPage('area', tenantId)
      .then((page) => {
        setRecords(page.records);
        setError('');
      })
      .catch((err: unknown) => {
        setError(err instanceof Error ? err.message : '主数据加载失败');
      })
      .finally(() => setLoading(false));
  }, [tenantId]);

  return (
    <section className="content-panel">
      <div className="panel-header">
        <div>
          <h2>区域台账</h2>
          <p>通过网关访问受保护的主数据接口。</p>
        </div>
        <button type="button">新增区域</button>
      </div>
      {loading && <div className="empty">正在加载...</div>}
      {error && <div className="error">{error}</div>}
      {!loading && !error && (
        <table>
          <thead>
            <tr>
              <th>编码</th>
              <th>名称</th>
              <th>类型</th>
              <th>状态</th>
            </tr>
          </thead>
          <tbody>
            {records.map((record) => (
              <tr key={record.id}>
                <td>{record.code}</td>
                <td>{record.name}</td>
                <td>{record.type || '-'}</td>
                <td>{record.status}</td>
              </tr>
            ))}
            {records.length === 0 && (
              <tr>
                <td colSpan={4} className="empty">
                  暂无区域数据
                </td>
              </tr>
            )}
          </tbody>
        </table>
      )}
    </section>
  );
}

createRoot(document.getElementById('root') as HTMLElement).render(<App />);
