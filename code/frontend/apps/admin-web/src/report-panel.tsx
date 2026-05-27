import React from 'react';
import {
  createAcceptanceTestRun,
  createReportExport,
  downloadReportExport,
  fetchReportExportTask,
  fetchAcceptanceTestCases,
  fetchAcceptanceTestRuns,
  fetchAlarmReportDetails,
  fetchAlarmReportSummary,
  fetchAuditReportSummary,
  fetchContractorReportSummary,
  fetchDashboardOverview,
  fetchMajorHazardReportSummary,
  fetchWorkPermitReportDetails,
  fetchWorkPermitReportSummary
} from '@psm/api-client';
import type {
  AcceptanceTestCaseRecord,
  AcceptanceTestRunRecord,
  AlarmReportDetail,
  AlarmReportSummary,
  AuditReportSummary,
  ContractorReportSummary,
  DashboardOverviewRecord,
  MajorHazardReportSummary,
  WorkPermitReportDetail,
  WorkPermitReportSummary,
  ReportExportTaskRecord
} from '@psm/domain-types';
import { errorMessage, formatTime } from './ui-helpers';

type ReportTab = 'work' | 'alarm' | 'hazard' | 'contractor' | 'audit';

export function ReportOverviewPanel({ tenantId }: { tenantId: number }) {
  const [tab, setTab] = React.useState<ReportTab>('work');
  const [workSummary, setWorkSummary] = React.useState<WorkPermitReportSummary | null>(null);
  const [alarmSummary, setAlarmSummary] = React.useState<AlarmReportSummary | null>(null);
  const [hazardSummary, setHazardSummary] = React.useState<MajorHazardReportSummary | null>(null);
  const [contractorSummary, setContractorSummary] = React.useState<ContractorReportSummary | null>(null);
  const [auditSummary, setAuditSummary] = React.useState<AuditReportSummary | null>(null);
  const [workDetails, setWorkDetails] = React.useState<WorkPermitReportDetail[]>([]);
  const [alarmDetails, setAlarmDetails] = React.useState<AlarmReportDetail[]>([]);
  const [exportType, setExportType] = React.useState('WORK_PERMIT');
  const [lastExport, setLastExport] = React.useState<ReportExportTaskRecord | null>(null);
  const [message, setMessage] = React.useState('');
  const [error, setError] = React.useState('');
  const [loading, setLoading] = React.useState(true);

  const load = React.useCallback(() => {
    setLoading(true);
    setError('');
    Promise.all([
      fetchWorkPermitReportSummary(tenantId),
      fetchAlarmReportSummary(tenantId),
      fetchMajorHazardReportSummary(tenantId),
      fetchContractorReportSummary(tenantId),
      fetchAuditReportSummary(tenantId),
      fetchWorkPermitReportDetails(tenantId),
      fetchAlarmReportDetails(tenantId)
    ])
      .then(([work, alarm, hazard, contractor, audit, workDetailList, alarmDetailList]) => {
        setWorkSummary(work);
        setAlarmSummary(alarm);
        setHazardSummary(hazard);
        setContractorSummary(contractor);
        setAuditSummary(audit);
        setWorkDetails(workDetailList);
        setAlarmDetails(alarmDetailList);
      })
      .catch((err: unknown) => setError(errorMessage(err, '报表服务不可用')))
      .finally(() => setLoading(false));
  }, [tenantId]);

  React.useEffect(() => {
    load();
  }, [load]);

  async function handleExport() {
    setMessage('');
    setError('');
    try {
      const task = await createReportExport({ tenantId, reportType: exportType, exportFormat: 'CSV', requestedBy: 'admin' });
      setLastExport(task);
      if (task.status === 'COMPLETED') {
        setMessage(`导出完成 #${task.id}，可下载 CSV 文件`);
      } else {
        setMessage(`导出失败 #${task.id}：${task.errorMessage || task.status}`);
      }
    } catch (err: unknown) {
      setError(errorMessage(err, '导出失败'));
    }
  }

  async function handleDownloadExport() {
    if (!lastExport || lastExport.status !== 'COMPLETED') return;
    setError('');
    try {
      const latest = await fetchReportExportTask(tenantId, lastExport.id);
      await downloadReportExport(tenantId, latest.id, `${latest.reportType.toLowerCase()}-${latest.id}.csv`);
      setMessage(`已下载导出文件 #${latest.id}`);
    } catch (err: unknown) {
      setError(errorMessage(err, '下载失败'));
    }
  }

  return (
    <section className="content-panel">
      <div className="panel-header">
        <h2>报表总览</h2>
        <p>作业、报警、危险源、承包商、审计指标与明细下钻（M08）。</p>
      </div>
      <div className="toolbar">
        {(['work', 'alarm', 'hazard', 'contractor', 'audit'] as ReportTab[]).map((key) => (
          <button key={key} type="button" className={tab === key ? '' : 'secondary'} onClick={() => setTab(key)}>
            {key === 'work' && '作业票'}
            {key === 'alarm' && '报警'}
            {key === 'hazard' && '危险源'}
            {key === 'contractor' && '承包商'}
            {key === 'audit' && '审计'}
          </button>
        ))}
        <select value={exportType} onChange={(e) => setExportType(e.target.value)}>
          <option value="WORK_PERMIT">作业票报表</option>
          <option value="ALARM">报警报表</option>
          <option value="CONTRACTOR">承包商报表</option>
        </select>
        <button type="button" className="secondary" onClick={handleExport}>
          导出 CSV
        </button>
        {lastExport?.status === 'COMPLETED' && (
          <button type="button" onClick={handleDownloadExport}>
            下载 #{lastExport.id}
          </button>
        )}
        <button type="button" className="secondary" onClick={load}>
          刷新
        </button>
      </div>
      {error && <div className="error-banner">{error}</div>}
      {message && <div className="success-banner">{message}</div>}
      {loading && <div className="empty">加载中...</div>}
      {!loading && tab === 'work' && workSummary && (
        <>
          <div className="metric-grid">
            <Metric label="作业票总数" value={workSummary.totalCount} />
            <Metric label="系统办理率" value={formatRate(workSummary.systemHandlingRate)} />
            <Metric label="许可留痕率" value={formatRate(workSummary.sitePermitTraceRate)} />
            <Metric label="承包商校验覆盖率" value={formatRate(workSummary.contractorEligibilityCoverage)} />
          </div>
          <DetailTable
            headers={['编号', '类型', '状态', '留痕', '资质校验']}
            rows={workDetails.map((row) => [
              row.permitNo,
              row.workType,
              row.status,
              row.sitePermitTraced ? '是' : '否',
              row.contractorChecked ? '是' : '否'
            ])}
          />
        </>
      )}
      {!loading && tab === 'alarm' && alarmSummary && (
        <>
          <div className="metric-grid">
            <Metric label="报警总数" value={alarmSummary.totalCount} />
            <Metric label="闭环率" value={formatRate(alarmSummary.closureRate)} />
            <Metric label="平均确认(分)" value={alarmSummary.avgConfirmMinutes?.toFixed(1) || '-'} />
            <Metric label="平均处置(分)" value={alarmSummary.avgDisposeMinutes?.toFixed(1) || '-'} />
          </div>
          <DetailTable
            headers={['编号', '等级', '状态', '确认分', '处置分']}
            rows={alarmDetails.map((row) => [
              row.alarmNo,
              row.alarmLevel,
              row.status,
              row.confirmMinutes ?? '-',
              row.disposeMinutes ?? '-'
            ])}
          />
        </>
      )}
      {!loading && tab === 'hazard' && hazardSummary && (
        <div className="metric-grid">
          <Metric label="危险源总数" value={hazardSummary.totalCount} />
          <Metric label="已发布" value={hazardSummary.publishedCount ?? '-'} />
          <Metric label="档案完整率" value={formatRate(hazardSummary.archiveCompletenessRate)} />
        </div>
      )}
      {!loading && tab === 'contractor' && contractorSummary && (
        <div className="metric-grid">
          <Metric label="单位数" value={contractorSummary.companyCount ?? '-'} />
          <Metric label="人员数" value={contractorSummary.workerCount ?? '-'} />
          <Metric label="准入单位" value={contractorSummary.approvedCompanyCount ?? '-'} />
          <Metric label="黑名单" value={contractorSummary.blacklistCount ?? '-'} />
        </div>
      )}
      {!loading && tab === 'audit' && auditSummary && (
        <div className="metric-grid">
          <Metric label="审计条数" value={auditSummary.totalCount ?? '-'} />
          <Metric label="关键操作" value={auditSummary.keyActionCount ?? '-'} />
          <Metric label="覆盖率" value={formatRate(auditSummary.coverageRate)} />
        </div>
      )}
    </section>
  );
}

export function DashboardPanel({ tenantId }: { tenantId: number }) {
  return (
    <section className="content-panel">
      <p className="hint">管理端内嵌预览；独立全屏大屏请访问 dashboard-web（端口 5175）。</p>
      <DashboardPreview tenantId={tenantId} compact />
    </section>
  );
}

export function DashboardPreview({ tenantId, compact }: { tenantId: number; compact?: boolean }) {
  const [overview, setOverview] = React.useState<DashboardOverviewRecord | null>(null);
  const [error, setError] = React.useState('');

  React.useEffect(() => {
    fetchDashboardOverview(tenantId)
      .then(setOverview)
      .catch((err: unknown) => setError(errorMessage(err, '大屏数据不可用')));
  }, [tenantId]);

  return (
    <div className={compact ? '' : 'dashboard-fullscreen'}>
      <div className="panel-header">
        <h2>态势大屏</h2>
        {overview && (
          <p className="hint">
            刷新 {formatTime(overview.refreshedAt)} · 来源 {overview.dataSources.join('、')}
          </p>
        )}
      </div>
      {error && <div className="error-banner">{error}</div>}
      {overview && (
        <div className="metric-grid dashboard-metrics">
          <Metric label="作业票总量" value={overview.workPermitTotal} />
          <Metric label="进行中" value={overview.workPermitInProgress} />
          <Metric label="待许可" value={overview.workPermitPendingPermit} />
          <Metric label="未关闭报警" value={overview.alarmOpenCount} />
          <Metric label="报警闭环率" value={formatRate(overview.alarmClosedRate)} />
          <Metric label="重大危险源" value={overview.hazardCount} />
        </div>
      )}
    </div>
  );
}

export function AcceptancePanel({ tenantId }: { tenantId: number }) {
  const [cases, setCases] = React.useState<AcceptanceTestCaseRecord[]>([]);
  const [runs, setRuns] = React.useState<AcceptanceTestRunRecord[]>([]);
  const [selectedCaseId, setSelectedCaseId] = React.useState<number | ''>('');
  const [runResult, setRunResult] = React.useState('PASS');
  const [evidenceRef, setEvidenceRef] = React.useState('');
  const [message, setMessage] = React.useState('');
  const [error, setError] = React.useState('');

  const load = React.useCallback(() => {
    Promise.all([fetchAcceptanceTestCases(tenantId), fetchAcceptanceTestRuns(tenantId)])
      .then(([caseList, runList]) => {
        setCases(caseList);
        setRuns(runList);
        if (!selectedCaseId && caseList.length > 0) {
          setSelectedCaseId(caseList[0].id);
        }
      })
      .catch((err: unknown) => setError(errorMessage(err, '验收数据不可用')));
  }, [tenantId, selectedCaseId]);

  React.useEffect(() => {
    load();
  }, [load]);

  async function recordRun() {
    if (!selectedCaseId) return;
    setError('');
    setMessage('');
    try {
      await createAcceptanceTestRun({
        tenantId,
        caseId: Number(selectedCaseId),
        runNo: `RUN-${Date.now()}`,
        runStatus: runResult,
        executorName: 'admin',
        evidenceRef: evidenceRef || undefined,
        remark: '管理端登记'
      });
      setMessage('已登记 UAT 执行记录');
      setEvidenceRef('');
      load();
    } catch (err: unknown) {
      setError(errorMessage(err, '登记失败'));
    }
  }

  return (
    <section className="content-panel">
      <div className="panel-header">
        <h2>上线验收（UAT）</h2>
        <p>用例与执行记录，支撑 M08 试运行签收。</p>
      </div>
      {error && <div className="error-banner">{error}</div>}
      {message && <div className="success-banner">{message}</div>}
      <div className="inline-form">
        <select value={selectedCaseId} onChange={(e) => setSelectedCaseId(e.target.value ? Number(e.target.value) : '')}>
          <option value="">选择用例</option>
          {cases.map((item) => (
            <option key={item.id} value={item.id}>
              {item.caseCode} · {item.caseName}
            </option>
          ))}
        </select>
        <select value={runResult} onChange={(e) => setRunResult(e.target.value)}>
          <option value="PASS">通过</option>
          <option value="FAIL">失败</option>
          <option value="BLOCKED">阻塞</option>
        </select>
        <input placeholder="证据引用（截图/日志路径）" value={evidenceRef} onChange={(e) => setEvidenceRef(e.target.value)} />
        <button type="button" onClick={recordRun}>
          登记执行
        </button>
      </div>
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
              <td>{item.runResult || item.runStatus}</td>
              <td>{item.evidenceRef || '-'}</td>
              <td>{formatTime(item.executedAt)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
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

function DetailTable({ headers, rows }: { headers: string[]; rows: Array<Array<string | number>> }) {
  return (
    <table className="data-table">
      <thead>
        <tr>
          {headers.map((h) => (
            <th key={h}>{h}</th>
          ))}
        </tr>
      </thead>
      <tbody>
        {rows.map((row, index) => (
          <tr key={index}>
            {row.map((cell, cellIndex) => (
              <td key={cellIndex}>{cell}</td>
            ))}
          </tr>
        ))}
      </tbody>
    </table>
  );
}

function formatRate(value?: number): string {
  if (value == null) return '-';
  return `${(value * 100).toFixed(1)}%`;
}
