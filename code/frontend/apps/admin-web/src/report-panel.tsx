import React from 'react';
import {
  fetchAcceptanceTestCases,
  fetchAcceptanceTestRuns,
  fetchDashboardOverview,
  fetchWorkPermitReportSummary
} from '@psm/api-client';
import type {
  AcceptanceTestCaseRecord,
  AcceptanceTestRunRecord,
  DashboardOverviewRecord,
  WorkPermitReportSummary
} from '@psm/domain-types';
import { errorMessage, formatTime } from './ui-helpers';

export function ReportOverviewPanel({ tenantId }: { tenantId: number }) {
  const [summary, setSummary] = React.useState<WorkPermitReportSummary | null>(null);
  const [error, setError] = React.useState<string>('');
  const [loading, setLoading] = React.useState<boolean>(true);

  React.useEffect(() => {
    setLoading(true);
    fetchWorkPermitReportSummary(tenantId)
      .then(setSummary)
      .catch((err: unknown) => setError(errorMessage(err, '报表服务不可用')))
      .finally(() => setLoading(false));
  }, [tenantId]);

  return (
    <section className="content-panel">
      <div className="panel-header">
        <h2>报表总览</h2>
        <p>作业票办理率、现场许可留痕率等 M08 指标（聚合各业务服务）。</p>
      </div>
      {error && <div className="error">{error}</div>}
      {loading && <div className="empty">加载中...</div>}
      {summary && (
        <div className="metric-grid">
          <div className="metric-card">
            <span>作业票总数</span>
            <strong>{summary.totalCount}</strong>
          </div>
          <div className="metric-card">
            <span>系统办理率</span>
            <strong>{formatRate(summary.systemHandlingRate)}</strong>
          </div>
          <div className="metric-card">
            <span>许可留痕率</span>
            <strong>{formatRate(summary.sitePermitTraceRate)}</strong>
          </div>
          <div className="metric-card">
            <span>承包商校验覆盖率</span>
            <strong>{formatRate(summary.contractorEligibilityCoverage)}</strong>
          </div>
        </div>
      )}
    </section>
  );
}

export function DashboardPanel({ tenantId }: { tenantId: number }) {
  const [overview, setOverview] = React.useState<DashboardOverviewRecord | null>(null);
  const [error, setError] = React.useState<string>('');

  React.useEffect(() => {
    fetchDashboardOverview(tenantId)
      .then(setOverview)
      .catch((err: unknown) => setError(errorMessage(err, '大屏数据不可用')));
  }, [tenantId]);

  return (
    <section className="content-panel dashboard-panel">
      <div className="panel-header">
        <h2>态势大屏</h2>
        {overview && (
          <p className="hint">
            刷新时间 {formatTime(overview.refreshedAt)} · 来源 {overview.dataSources.join('、')}
          </p>
        )}
      </div>
      {error && <div className="error">{error}</div>}
      {overview && (
        <div className="metric-grid">
          <div className="metric-card">
            <span>今日/全部作业票</span>
            <strong>{overview.workPermitTotal}</strong>
          </div>
          <div className="metric-card">
            <span>进行中</span>
            <strong>{overview.workPermitInProgress}</strong>
          </div>
          <div className="metric-card">
            <span>待许可</span>
            <strong>{overview.workPermitPendingPermit}</strong>
          </div>
          <div className="metric-card">
            <span>未关闭报警</span>
            <strong>{overview.alarmOpenCount}</strong>
          </div>
          <div className="metric-card">
            <span>重大危险源</span>
            <strong>{overview.hazardCount}</strong>
          </div>
        </div>
      )}
    </section>
  );
}

export function AcceptancePanel({ tenantId }: { tenantId: number }) {
  const [cases, setCases] = React.useState<AcceptanceTestCaseRecord[]>([]);
  const [runs, setRuns] = React.useState<AcceptanceTestRunRecord[]>([]);
  const [error, setError] = React.useState<string>('');

  React.useEffect(() => {
    Promise.all([fetchAcceptanceTestCases(tenantId), fetchAcceptanceTestRuns(tenantId)])
      .then(([caseList, runList]) => {
        setCases(caseList);
        setRuns(runList);
      })
      .catch((err: unknown) => setError(errorMessage(err, '验收数据不可用')));
  }, [tenantId]);

  return (
    <section className="content-panel">
      <div className="panel-header">
        <h2>上线验收（UAT）</h2>
        <p>用例与执行记录，支撑 M08 试运行签收。</p>
      </div>
      {error && <div className="error">{error}</div>}
      <h3>用例</h3>
      <table className="data-table">
        <thead>
          <tr>
            <th>编码</th>
            <th>名称</th>
            <th>模块</th>
            <th>状态</th>
          </tr>
        </thead>
        <tbody>
          {cases.map((item) => (
            <tr key={item.id}>
              <td>{item.caseCode}</td>
              <td>{item.caseName}</td>
              <td>{item.moduleName || '-'}</td>
              <td>{item.status}</td>
            </tr>
          ))}
        </tbody>
      </table>
      <h3>执行记录</h3>
      <table className="data-table">
        <thead>
          <tr>
            <th>用例ID</th>
            <th>结果</th>
            <th>证据</th>
            <th>时间</th>
          </tr>
        </thead>
        <tbody>
          {runs.map((item) => (
            <tr key={item.id}>
              <td>{item.caseId}</td>
              <td>{item.runResult}</td>
              <td>{item.evidenceRef || '-'}</td>
              <td>{formatTime(item.executedAt)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}

function formatRate(value?: number): string {
  if (value == null) return '-';
  return `${(value * 100).toFixed(1)}%`;
}
