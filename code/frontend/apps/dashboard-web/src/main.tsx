import React from 'react';
import { createRoot } from 'react-dom/client';
import {
  fetchAlarmReportSummary,
  fetchAlarmTrend,
  fetchContractorReportSummary,
  fetchDashboardOverview,
  fetchMajorHazardReportSummary,
  fetchWorkPermitTrend,
  login
} from '@psm/api-client';
import type {
  AlarmReportSummary,
  ContractorReportSummary,
  DashboardOverviewRecord,
  MajorHazardReportSummary,
  TrendSeriesRecord
} from '@psm/domain-types';
import './styles.css';

const TENANT_ID = 1;
const REFRESH_MS = 30000;

function App() {
  const [overview, setOverview] = React.useState<DashboardOverviewRecord | null>(null);
  const [permitTrend, setPermitTrend] = React.useState<TrendSeriesRecord | null>(null);
  const [alarmTrend, setAlarmTrend] = React.useState<TrendSeriesRecord | null>(null);
  const [hazardSummary, setHazardSummary] = React.useState<MajorHazardReportSummary | null>(null);
  const [contractorSummary, setContractorSummary] = React.useState<ContractorReportSummary | null>(null);
  const [alarmSummary, setAlarmSummary] = React.useState<AlarmReportSummary | null>(null);
  const [error, setError] = React.useState('');
  const [refreshing, setRefreshing] = React.useState(false);

  const load = React.useCallback(async () => {
    setRefreshing(true);
    try {
      const [overviewData, permitSeries, alarmSeries, hazard, contractor, alarm] = await Promise.all([
        fetchDashboardOverview(TENANT_ID),
        fetchWorkPermitTrend(TENANT_ID, 7),
        fetchAlarmTrend(TENANT_ID, 7),
        fetchMajorHazardReportSummary(TENANT_ID),
        fetchContractorReportSummary(TENANT_ID),
        fetchAlarmReportSummary(TENANT_ID)
      ]);
      setOverview(overviewData);
      setPermitTrend(permitSeries);
      setAlarmTrend(alarmSeries);
      setHazardSummary(hazard);
      setContractorSummary(contractor);
      setAlarmSummary(alarm);
      setError('');
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : '大屏加载失败');
    } finally {
      setRefreshing(false);
    }
  }, []);

  React.useEffect(() => {
    login({ tenantId: TENANT_ID, username: 'admin', password: '' })
      .then(load)
      .catch((err: unknown) => setError(err instanceof Error ? err.message : '登录失败'));
  }, [load]);

  React.useEffect(() => {
    const timer = window.setInterval(load, REFRESH_MS);
    return () => window.clearInterval(timer);
  }, [load]);

  return (
    <main className="dashboard-shell">
      <header className="dashboard-header">
        <div>
          <h1>PSM 中控态势大屏</h1>
          {overview && (
            <p className="hint">
              数据刷新 {overview.refreshedAt} · 来源 {overview.dataSources.join('、')}
              {refreshing ? ' · 刷新中…' : ` · 自动刷新 ${REFRESH_MS / 1000}s`}
            </p>
          )}
        </div>
        <button type="button" className="refresh-btn" onClick={load} disabled={refreshing}>
          立即刷新
        </button>
      </header>
      {error && <p className="error">{error}</p>}
      {overview && (
        <div className="metric-grid">
          <Metric label="作业票总量" value={overview.workPermitTotal} accent />
          <Metric label="进行中" value={overview.workPermitInProgress} />
          <Metric label="待许可" value={overview.workPermitPendingPermit} />
          <Metric label="未关闭报警" value={overview.alarmOpenCount} warn={overview.alarmOpenCount > 0} />
          <Metric label="报警闭环率" value={formatRate(overview.alarmClosedRate)} />
          <Metric label="重大危险源" value={overview.hazardCount} />
        </div>
      )}
      <div className="sub-metric-grid">
        {hazardSummary && (
          <div className="sub-card">
            <h3>危险源档案</h3>
            <p>总数 {hazardSummary.totalCount}</p>
            <p>完整率 {formatRate(hazardSummary.archiveCompletenessRate)}</p>
          </div>
        )}
        {contractorSummary && (
          <div className="sub-card">
            <h3>承包商态势</h3>
            <p>单位 {contractorSummary.companyCount}</p>
            <p>人员 {contractorSummary.workerCount}</p>
          </div>
        )}
        {alarmSummary && (
          <div className="sub-card">
            <h3>报警统计</h3>
            <p>总数 {alarmSummary.totalCount}</p>
            <p>闭环率 {formatRate(alarmSummary.closureRate)}</p>
          </div>
        )}
      </div>
      <div className="trend-panel">
        <TrendCard title="作业票趋势（7日）" series={permitTrend} color="#38bdf8" />
        <TrendCard title="报警趋势（7日）" series={alarmTrend} color="#f97316" />
      </div>
    </main>
  );
}

function Metric({
  label,
  value,
  accent,
  warn
}: {
  label: string;
  value: string | number;
  accent?: boolean;
  warn?: boolean;
}) {
  return (
    <div className={`metric-card${accent ? ' accent' : ''}${warn ? ' warn' : ''}`}>
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

function TrendCard({ title, series, color }: { title: string; series: TrendSeriesRecord | null; color: string }) {
  const max = Math.max(...(series?.points || []).map((p) => p.value), 1);
  return (
    <section className="trend-card">
      <h3>{title}</h3>
      {series && <p className="hint">{series.dataSource}</p>}
      <div className="bar-chart">
        {(series?.points || []).map((point) => (
          <div key={point.label} className="bar-item">
            <div className="bar" style={{ height: `${(point.value / max) * 100}%`, background: color }} title={String(point.value)} />
            <span>{point.label}</span>
            <strong>{point.value}</strong>
          </div>
        ))}
      </div>
    </section>
  );
}

function formatRate(value?: number): string {
  if (value == null) return '-';
  return `${(value * 100).toFixed(1)}%`;
}

createRoot(document.getElementById('root') as HTMLElement).render(<App />);
