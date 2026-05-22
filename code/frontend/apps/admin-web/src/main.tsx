import React from 'react';
import { createRoot } from 'react-dom/client';
import { fetchDemoInfo } from '@psm/api-client';
import type { DemoInfo } from '@psm/domain-types';
import './styles.css';

function App() {
  const [demo, setDemo] = React.useState<DemoInfo | null>(null);
  const [error, setError] = React.useState<string>('');

  React.useEffect(() => {
    fetchDemoInfo()
      .then(setDemo)
      .catch((err: unknown) => {
        const message = err instanceof Error ? err.message : 'unknown error';
        setError(message);
      });
  }, []);

  return (
    <main className="page">
      <section className="panel">
        <p className="eyebrow">PSM Platform</p>
        <h1>前后端分离脚手架 Demo</h1>
        <p className="summary">
          管理端通过 Vite 代理访问后端 auth 服务的 <code>/api/demo</code> 接口。
        </p>
        <div className="result">
          {demo && (
            <>
              <span>service: {demo.service}</span>
              <span>module: {demo.module}</span>
              <span>version: {demo.version}</span>
            </>
          )}
          {!demo && !error && <span>正在请求后端 demo 接口...</span>}
          {error && <span>请求失败: {error}</span>}
        </div>
      </section>
    </main>
  );
}

createRoot(document.getElementById('root') as HTMLElement).render(<App />);

