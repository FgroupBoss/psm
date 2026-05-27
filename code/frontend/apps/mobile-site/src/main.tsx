import React from 'react';
import { createRoot } from 'react-dom/client';
import { clearTokens, getAccessToken } from '@psm/auth';
import { login, request } from '@psm/api-client';
import type { AuthUser } from '@psm/domain-types';
import { MOBILE_API } from '@psm/domain-types';

interface MobileTask {
  taskType: string;
  title: string;
  bizId: number;
  status?: string;
  occurredAt?: string;
}

function App() {
  const [user, setUser] = React.useState<AuthUser | null>(null);
  const [tasks, setTasks] = React.useState<MobileTask[]>([]);
  const [error, setError] = React.useState<string>('');

  async function handleLogin(event: React.FormEvent) {
    event.preventDefault();
    setError('');
    try {
      const result = await login({ tenantId: 1, username: 'admin', password: '' });
      setUser(result.user);
      await loadTasks(result.user.tenantId, result.user.id);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : '登录失败');
    }
  }

  async function loadTasks(tenantId: number, userId: number) {
    const list = await request<MobileTask[]>(
      `${MOBILE_API.tasks}?tenantId=${tenantId}&userId=${userId}&role=ALL`
    );
    setTasks(list);
  }

  React.useEffect(() => {
    if (!getAccessToken()) return;
    request<AuthUser>('/auth/me')
      .then((current) => {
        setUser(current);
        return loadTasks(current.tenantId, current.id);
      })
      .catch(() => clearTokens());
  }, []);

  if (!user) {
    return (
      <main className="mobile-shell">
        <h1>PSM 移动现场</h1>
        <p>M07 现场待办、许可与弱网草稿（经 BFF）。</p>
        <form onSubmit={handleLogin}>
          <button type="submit">使用试点账号登录</button>
        </form>
        {error && <p className="error">{error}</p>}
      </main>
    );
  }

  return (
    <main className="mobile-shell">
      <header>
        <h1>待办</h1>
        <button
          type="button"
          onClick={() => {
            clearTokens();
            setUser(null);
          }}
        >
          退出
        </button>
      </header>
      {error && <p className="error">{error}</p>}
      <ul>
        {tasks.length === 0 && <li>暂无待办</li>}
        {tasks.map((task, index) => (
          <li key={`${task.taskType}-${task.bizId}-${index}`}>
            <strong>{task.title}</strong>
            <span>{task.taskType}</span>
            {task.status && <span>{task.status}</span>}
          </li>
        ))}
      </ul>
    </main>
  );
}

createRoot(document.getElementById('root') as HTMLElement).render(<App />);
