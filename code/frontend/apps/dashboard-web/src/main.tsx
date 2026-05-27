import React from 'react';
import { createRoot } from 'react-dom/client';
import { fetchAlarmTrend, fetchDashboardOverview, fetchWorkPermitTrend, login } from '@psm/api-client';
import type { DashboardOverviewRecord, TrendSeriesRecord } from '@psm/domain-types';
import './styles.css';

const TENANT_ID = 1;

function App() {
  const [overview, setOverview] = React.useState<DashboardOverviewRecord | null>(null);
  const [permitTrend, setPermitTrend] = React.useState<TrendSeriesRecord | null>(null);
  const [alarmTrend, setAlarmTrend] = React.useState<TrendSeriesRecord | null>(null);
  const [error, setError] = React.useState('');

  React.useEffect(() => {
    login({ tenantId: TENANT_ID, username: 'admin', password: '' })
      .then(() =>
        Promise.all([
          fetchDashboardOverview(TENANT_ID),
          fetchWorkPermitTrend(TENANT_ID, 7),
          fetchAlarmTrend(TENANT_ID, 7)
        ])
      )
      .then(([overviewData, permitSeries, alarmSeries]) => {
        setOverview(overviewData);
        setPermitTrend(permitSeries);
        setAlarmTrend(alarmSeries);
      })
      .catch((err: unknown) => setError(err instanceof Error ? err.message : '大屏加载失败'));
  }, []);

  return (
    <main className="dashboard-shell">
      <header className="dashboard-header">
        <div>
          <h1>PSM 中控态势大屏</h1>
          {overview && (
            <p className="hint">
              数据刷新 {overview.refreshedAt} · 来源 {overview.dataSources.join('、')}
            </p>
          )}
        </div>
      </header>
      {error && <p className="error">{error}</p>}
      {overview && (
        <div className="metric-grid">
          <Metric label="作业票总量" value={overview.workPermitTotal} />
          <Metric label="进行中" value={overview.workPermitInProgress} />
          <Metric label="待许可" value={overview.workPermitPendingPermit} />
          <Metric label="未关闭报警" value={overview.alarmOpenCount} />
          <Metric label="报警闭环率" value={formatRate(overview.alarmClosedRate)} />
          <Metric label="重大危险源" value={overview.hazardCount} />
        </div>
      )}
      <div className="trend-panel">
        <TrendCard title="作业票趋势（7日）" series={permitTrend} />
        <TrendCard title="报警趋势（7日）" series={alarmTrend} />
      </div>
    </main>
  );
}

function Metric({ label, value }: { label: string; value: string | number }) {
  return (
    <div className="metric-card">
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

function TrendCard({ title, series }: { title: string; series: TrendSeriesRecord | null }) {
  return (
    <section className="trend-card">
      <h3>{title}</h3>
      {series && <p className="hint">{series.dataSource} · 刷新 {series.dataRefreshedAt}</p>}
      <ul>
        {(series?.points || []).map((point) => (
          <li key={point.label}>
            <span>{point.label}</span>
            <strong>{point.value}</strong>
          </li>
        ))}
      </ul>
    </section>
  );
}

function formatRate(value?: number): string {
  if (value == null) return '-';
  return `${(value * 100).toFixed(1)}%`;
}

createRoot(document.getElementById('root') as HTMLElement).render(<App />);
