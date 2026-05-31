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
import { ThemeProvider, ThemeToggle } from '@psm/ui';
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
        <div className="dashboard-header__brand">
          <div className="dashboard-header__icon">
            <i className="fa-solid fa-chart-line" />
          </div>
          <div>
            <h1>PSM 中控态势大屏</h1>
            {overview && (
              <p className="hint">
                数据刷新 {overview.refreshedAt} · 来源 {overview.dataSources.join('、')}
                {refreshing ? ' · 刷新中…' : ` · 自动刷新 ${REFRESH_MS / 1000}s`}
              </p>
            )}
          </div>
        </div>
        <div className="dashboard-header__actions">
          <ThemeToggle className="theme-toggle--labeled" showLabel />
          <button type="button" className="refresh-btn" onClick={load} disabled={refreshing}>
            <i className={`fa-solid ${refreshing ? 'fa-spinner fa-spin' : 'fa-arrows-rotate'}`} />
            立即刷新
          </button>
        </div>
      </header>
      {error && <p className="error">{error}</p>}
      {overview && (
        <div className="metric-grid">
          <Metric label="作业票总量" value={overview.workPermitTotal} icon="fa-clipboard-list" accent className="stagger-1" />
          <Metric label="进行中" value={overview.workPermitInProgress} icon="fa-person-digging" className="stagger-2" />
          <Metric label="待许可" value={overview.workPermitPendingPermit} icon="fa-hourglass-half" className="stagger-3" />
          <Metric
            label="未关闭报警"
            value={overview.alarmOpenCount}
            icon="fa-bell"
            warn={overview.alarmOpenCount > 0}
            className="stagger-4"
          />
          <Metric label="报警闭环率" value={formatRate(overview.alarmClosedRate)} icon="fa-circle-check" className="stagger-5" />
          <Metric label="重大危险源" value={overview.hazardCount} icon="fa-triangle-exclamation" className="stagger-6" />
        </div>
      )}
      <div className="sub-metric-grid">
        {hazardSummary && (
          <div className="sub-card stagger-1">
            <h3>
              <i className="fa-solid fa-radiation" /> 危险源档案
            </h3>
            <p>
              总数 <span className="value">{hazardSummary.totalCount}</span>
            </p>
            <p>
              完整率 <span className="value">{formatRate(hazardSummary.archiveCompletenessRate)}</span>
            </p>
          </div>
        )}
        {contractorSummary && (
          <div className="sub-card stagger-2">
            <h3>
              <i className="fa-solid fa-helmet-safety" /> 承包商态势
            </h3>
            <p>
              单位 <span className="value">{contractorSummary.companyCount}</span>
            </p>
            <p>
              人员 <span className="value">{contractorSummary.workerCount}</span>
            </p>
          </div>
        )}
        {alarmSummary && (
          <div className="sub-card stagger-3">
            <h3>
              <i className="fa-solid fa-bell" /> 报警统计
            </h3>
            <p>
              总数 <span className="value">{alarmSummary.totalCount}</span>
            </p>
            <p>
              闭环率 <span className="value">{formatRate(alarmSummary.closureRate)}</span>
            </p>
          </div>
        )}
      </div>
      <div className="trend-panel">
        <TrendCard title="作业票趋势（7日）" series={permitTrend} color="linear-gradient(180deg, #0A84FF, #5E5CE6)" icon="fa-chart-column" />
        <TrendCard title="报警趋势（7日）" series={alarmTrend} color="linear-gradient(180deg, #FF9F0A, #FF453A)" icon="fa-chart-bar" />
      </div>
    </main>
  );
}

function Metric({
  label,
  value,
  icon,
  accent,
  warn,
  className
}: {
  label: string;
  value: string | number;
  icon: string;
  accent?: boolean;
  warn?: boolean;
  className?: string;
}) {
  return (
    <div className={`metric-card${accent ? ' accent' : ''}${warn ? ' warn' : ''}${className ? ` ${className}` : ''}`}>
      <span>
        <i className={`fa-solid ${icon}`} />
        {label}
      </span>
      <strong>{value}</strong>
    </div>
  );
}

function TrendCard({
  title,
  series,
  color,
  icon
}: {
  title: string;
  series: TrendSeriesRecord | null;
  color: string;
  icon: string;
}) {
  const max = Math.max(...(series?.points || []).map((p) => p.value), 1);
  return (
    <section className="trend-card">
      <h3>
        <i className={`fa-solid ${icon}`} />
        {title}
      </h3>
      {series && <p className="hint">{series.dataSource}</p>}
      <div className="bar-chart">
        {(series?.points || []).map((point, index) => (
          <div key={point.label} className="bar-item">
            <div className="bar-wrap">
              <div
                className="bar"
                style={{
                  height: `${(point.value / max) * 100}%`,
                  background: color,
                  animationDelay: `${index * 0.06}s`
                }}
                title={String(point.value)}
              />
            </div>
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

createRoot(document.getElementById('root') as HTMLElement).render(
  <ThemeProvider>
    <App />
  </ThemeProvider>
);
