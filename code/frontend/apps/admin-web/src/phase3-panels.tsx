import React from 'react';
import {
  approvePssrStartup,
  closeMocChange,
  closePhaRecommendation,
  createMocChange,
  createPhaProject,
  degradeBarrier,
  calculateLopaScenario,
  fetchBarriers,
  fetchGovernanceDashboard,
  fetchIncidents,
  fetchLopaScenarios,
  fetchMiEquipment,
  fetchMocChanges,
  fetchPhase3ReportSummary,
  fetchPhaProjects,
  fetchPhaRecommendations,
  fetchPssrProjects,
  publishPhaProject,
  startIncidentInvestigation,
  submitMocChange,
  submitPhaProject
} from '@psm/api-client';
import type {
  BarrierRecord,
  GovernanceDashboardRecord,
  IncidentRecord,
  LopaScenarioRecord,
  MiEquipmentRecord,
  MocChangeRecord,
  PageResult,
  Phase3ReportSummaryRecord,
  PhaProjectRecord,
  PhaRecommendationRecord,
  PssrProjectRecord
} from '@psm/domain-types';
import { errorMessage, formatTime, StatusTag } from './ui-helpers';

function useListLoader<T>(loader: () => Promise<T>, deps: React.DependencyList) {
  const [data, setData] = React.useState<T | null>(null);
  const [loading, setLoading] = React.useState(true);
  const [error, setError] = React.useState('');
  const reload = React.useCallback(() => {
    setLoading(true);
    setError('');
    return loader()
      .then(setData)
      .catch((err: unknown) => setError(errorMessage(err, '加载失败')))
      .finally(() => setLoading(false));
  }, deps);
  React.useEffect(() => {
    reload();
  }, [reload]);
  return { data, loading, error, reload, setError };
}

function LedgerTable<T extends { id: number }>(props: {
  title: string;
  subtitle?: string;
  loading: boolean;
  error: string;
  rows: T[];
  columns: Array<{ key: string; label: string; render: (row: T) => React.ReactNode }>;
  actions?: (row: T) => React.ReactNode;
  toolbar?: React.ReactNode;
}) {
  return (
    <section className="content-panel">
      <div className="panel-header">
        <div>
          <h2>{props.title}</h2>
          {props.subtitle && <p>{props.subtitle}</p>}
        </div>
        {props.toolbar}
      </div>
      {props.error && <div className="error">{props.error}</div>}
      <table className="data-table">
        <thead>
          <tr>
            {props.columns.map((col) => (
              <th key={col.key}>{col.label}</th>
            ))}
            {props.actions && <th>操作</th>}
          </tr>
        </thead>
        <tbody>
          {props.loading && (
            <tr>
              <td colSpan={props.columns.length + (props.actions ? 1 : 0)}>加载中...</td>
            </tr>
          )}
          {!props.loading &&
            props.rows.map((row) => (
              <tr key={row.id}>
                {props.columns.map((col) => (
                  <td key={col.key}>{col.render(row)}</td>
                ))}
                {props.actions && <td>{props.actions(row)}</td>}
              </tr>
            ))}
        </tbody>
      </table>
    </section>
  );
}

export function PhaProjectsPanel({ tenantId }: { tenantId: number }) {
  const { data, loading, error, reload, setError } = useListLoader(
    () => fetchPhaProjects(tenantId, 1, 50),
    [tenantId]
  );
  const page = data as PageResult<PhaProjectRecord> | null;
  const [acting, setActing] = React.useState(false);

  async function runAction(action: () => Promise<unknown>) {
    setActing(true);
    setError('');
    try {
      await action();
      await reload();
    } catch (err: unknown) {
      setError(errorMessage(err, '操作失败'));
    } finally {
      setActing(false);
    }
  }

  return (
    <LedgerTable
      title="PHA 项目台账"
      subtitle="HAZOP/LOPA 项目立项与版本发布"
      loading={loading}
      error={error}
      rows={page?.records || []}
      toolbar={
        <button
          type="button"
          disabled={acting}
          onClick={() =>
            runAction(() =>
              createPhaProject({ tenantId, projectName: `PHA项目-${Date.now()}`, method: 'HAZOP' })
            )
          }
        >
          新建项目
        </button>
      }
      columns={[
        { key: 'no', label: '编号', render: (r) => r.projectNo || r.id },
        { key: 'name', label: '名称', render: (r) => r.projectName || '-' },
        { key: 'method', label: '方法', render: (r) => r.method || '-' },
        { key: 'status', label: '状态', render: (r) => <StatusTag status={r.status} /> },
        { key: 'due', label: '复审期限', render: (r) => formatTime(r.reviewDueAt) }
      ]}
      actions={(row) => (
        <>
          {row.status === 'DRAFT' && (
            <button type="button" disabled={acting} onClick={() => runAction(() => submitPhaProject(row.id, tenantId))}>
              提交
            </button>
          )}
          {row.status === 'PENDING_REVIEW' && (
            <button type="button" disabled={acting} onClick={() => runAction(() => publishPhaProject(row.id, tenantId))}>
              发布
            </button>
          )}
        </>
      )}
    />
  );
}

export function PhaRecommendationsPanel({ tenantId }: { tenantId: number }) {
  const { data, loading, error, reload, setError } = useListLoader(
    () => fetchPhaRecommendations(tenantId, undefined, 1, 50),
    [tenantId]
  );
  const page = data as PageResult<PhaRecommendationRecord> | null;
  const [acting, setActing] = React.useState(false);

  return (
    <LedgerTable
      title="PHA 建议项"
      subtitle="分派、整改、验证与关闭"
      loading={loading}
      error={error}
      rows={page?.records || []}
      columns={[
        { key: 'no', label: '编号', render: (r) => r.recommendationNo || r.id },
        { key: 'content', label: '内容', render: (r) => (r.content || '-').slice(0, 40) },
        { key: 'status', label: '状态', render: (r) => <StatusTag status={r.status} /> },
        { key: 'due', label: '期限', render: (r) => formatTime(r.rectificationDeadline) }
      ]}
      actions={(row) =>
        row.status !== 'CLOSED' ? (
          <button
            type="button"
            disabled={acting}
            onClick={async () => {
              setActing(true);
              try {
                await closePhaRecommendation(row.id, tenantId);
                await reload();
              } catch (err: unknown) {
                setError(errorMessage(err, '关闭失败'));
              } finally {
                setActing(false);
              }
            }}
          >
            关闭
          </button>
        ) : null
      }
    />
  );
}

export function PhaLopaPanel({ tenantId }: { tenantId: number }) {
  const { data, loading, error, reload, setError } = useListLoader(
    () => fetchLopaScenarios(tenantId, undefined, 1, 50),
    [tenantId]
  );
  const page = data as PageResult<LopaScenarioRecord> | null;
  const [acting, setActing] = React.useState(false);

  return (
    <LedgerTable
      title="LOPA 场景"
      subtitle="独立保护层与 SIL 建议计算"
      loading={loading}
      error={error}
      rows={page?.records || []}
      columns={[
        { key: 'no', label: '场景', render: (r) => r.scenarioNo || r.id },
        { key: 'ief', label: '初始频率', render: (r) => r.initiatingEventFrequency ?? '-' },
        { key: 'target', label: '目标频率', render: (r) => r.targetFrequency ?? '-' },
        { key: 'mitigated', label: '保护后', render: (r) => r.mitigatedFrequency ?? '-' },
        { key: 'sil', label: 'SIL', render: (r) => r.silRecommendation || '-' }
      ]}
      actions={(row) => (
        <button
          type="button"
          disabled={acting}
          onClick={async () => {
            setActing(true);
            try {
              await calculateLopaScenario(row.id, tenantId);
              await reload();
            } catch (err: unknown) {
              setError(errorMessage(err, '计算失败'));
            } finally {
              setActing(false);
            }
          }}
        >
          计算
        </button>
      )}
    />
  );
}

export function MiEquipmentPanel({ tenantId }: { tenantId: number }) {
  const { data, loading, error } = useListLoader(() => fetchMiEquipment(tenantId, 1, 50), [tenantId]);
  const page = data as PageResult<MiEquipmentRecord> | null;

  return (
    <LedgerTable
      title="关键设备台账"
      subtitle="机械完整性检验与缺陷闭环"
      loading={loading}
      error={error}
      rows={page?.records || []}
      columns={[
        { key: 'code', label: '编码', render: (r) => r.equipmentCode || r.id },
        { key: 'name', label: '名称', render: (r) => r.equipmentName || '-' },
        { key: 'criticality', label: '关键度', render: (r) => r.criticality || '-' },
        { key: 'status', label: '状态', render: (r) => <StatusTag status={r.status} /> }
      ]}
    />
  );
}

export function MocChangesPanel({ tenantId }: { tenantId: number }) {
  const { data, loading, error, reload, setError } = useListLoader(
    () => fetchMocChanges(tenantId, 1, 50),
    [tenantId]
  );
  const page = data as PageResult<MocChangeRecord> | null;
  const [acting, setActing] = React.useState(false);

  async function run(action: () => Promise<unknown>) {
    setActing(true);
    try {
      await action();
      await reload();
    } catch (err: unknown) {
      setError(errorMessage(err, '操作失败'));
    } finally {
      setActing(false);
    }
  }

  return (
    <LedgerTable
      title="MOC 变更台账"
      subtitle="变更申请至关闭全流程"
      loading={loading}
      error={error}
      rows={page?.records || []}
      toolbar={
        <button
          type="button"
          disabled={acting}
          onClick={() =>
            run(() => createMocChange({ tenantId, title: `变更-${Date.now()}`, changeType: 'PROCESS', changeLevel: 'MEDIUM' }))
          }
        >
          新建变更
        </button>
      }
      columns={[
        { key: 'no', label: '编号', render: (r) => r.changeNo || r.id },
        { key: 'title', label: '标题', render: (r) => r.title || '-' },
        { key: 'type', label: '类型', render: (r) => r.changeType || '-' },
        { key: 'level', label: '等级', render: (r) => r.changeLevel || '-' },
        { key: 'status', label: '状态', render: (r) => <StatusTag status={r.status} /> }
      ]}
      actions={(row) => (
        <>
          {row.status === 'DRAFT' && (
            <button type="button" disabled={acting} onClick={() => run(() => submitMocChange(row.id, tenantId))}>
              提交
            </button>
          )}
          {row.status === 'PENDING_VERIFY' && (
            <button type="button" disabled={acting} onClick={() => run(() => closeMocChange(row.id, tenantId))}>
              关闭
            </button>
          )}
        </>
      )}
    />
  );
}

export function PssrProjectsPanel({ tenantId }: { tenantId: number }) {
  const { data, loading, error, reload, setError } = useListLoader(
    () => fetchPssrProjects(tenantId, 1, 50),
    [tenantId]
  );
  const page = data as PageResult<PssrProjectRecord> | null;
  const [acting, setActing] = React.useState(false);

  return (
    <LedgerTable
      title="PSSR 开车前审查"
      subtitle="审查项目与开车批准"
      loading={loading}
      error={error}
      rows={page?.records || []}
      columns={[
        { key: 'no', label: '编号', render: (r) => r.projectNo || r.id },
        { key: 'name', label: '名称', render: (r) => r.projectName || '-' },
        { key: 'moc', label: '关联MOC', render: (r) => r.mocId || '-' },
        { key: 'status', label: '状态', render: (r) => <StatusTag status={r.status} /> }
      ]}
      actions={(row) =>
        row.status !== 'APPROVED' && row.status !== 'ARCHIVED' ? (
          <button
            type="button"
            disabled={acting}
            onClick={async () => {
              setActing(true);
              try {
                await approvePssrStartup(row.id, tenantId);
                await reload();
              } catch (err: unknown) {
                setError(errorMessage(err, '批准失败'));
              } finally {
                setActing(false);
              }
            }}
          >
            批准开车
          </button>
        ) : null
      }
    />
  );
}

export function BarriersPanel({ tenantId }: { tenantId: number }) {
  const { data, loading, error, reload, setError } = useListLoader(
    () => fetchBarriers(tenantId, 1, 50),
    [tenantId]
  );
  const page = data as PageResult<BarrierRecord> | null;
  const [acting, setActing] = React.useState(false);

  return (
    <LedgerTable
      title="屏障台账"
      subtitle="屏障健康度与降级管理"
      loading={loading}
      error={error}
      rows={page?.records || []}
      columns={[
        { key: 'code', label: '编码', render: (r) => r.barrierCode || r.id },
        { key: 'name', label: '名称', render: (r) => r.barrierName || '-' },
        { key: 'type', label: '类型', render: (r) => r.barrierType || '-' },
        { key: 'health', label: '健康度', render: (r) => <StatusTag status={r.healthStatus || r.status} /> }
      ]}
      actions={(row) =>
        row.status === 'NORMAL' || row.healthStatus === 'NORMAL' ? (
          <button
            type="button"
            disabled={acting}
            onClick={async () => {
              setActing(true);
              try {
                await degradeBarrier(row.id, tenantId);
                await reload();
              } catch (err: unknown) {
                setError(errorMessage(err, '降级失败'));
              } finally {
                setActing(false);
              }
            }}
          >
            降级
          </button>
        ) : null
      }
    />
  );
}

export function IncidentsPanel({ tenantId }: { tenantId: number }) {
  const { data, loading, error, reload, setError } = useListLoader(
    () => fetchIncidents(tenantId, 1, 50),
    [tenantId]
  );
  const page = data as PageResult<IncidentRecord> | null;
  const [acting, setActing] = React.useState(false);

  return (
    <LedgerTable
      title="事故调查"
      subtitle="事件报告、调查立项与 CAPA"
      loading={loading}
      error={error}
      rows={page?.records || []}
      columns={[
        { key: 'no', label: '编号', render: (r) => r.incidentNo || r.id },
        { key: 'title', label: '标题', render: (r) => r.title || '-' },
        { key: 'severity', label: '等级', render: (r) => r.severity || '-' },
        { key: 'status', label: '状态', render: (r) => <StatusTag status={r.status} /> }
      ]}
      actions={(row) =>
        row.status === 'REPORTED' ? (
          <button
            type="button"
            disabled={acting}
            onClick={async () => {
              setActing(true);
              try {
                await startIncidentInvestigation(row.id, tenantId);
                await reload();
              } catch (err: unknown) {
                setError(errorMessage(err, '立项失败'));
              } finally {
                setActing(false);
              }
            }}
          >
            立项调查
          </button>
        ) : null
      }
    />
  );
}

export function GovernanceDashboardPanel({ tenantId }: { tenantId: number }) {
  const { data, loading, error } = useListLoader(() => fetchGovernanceDashboard(tenantId), [tenantId]);
  const dash = data as GovernanceDashboardRecord | null;

  return (
    <section className="content-panel">
      <div className="panel-header">
        <div>
          <h2>集团治理驾驶舱</h2>
          <p>多基地指标、审计与对标</p>
        </div>
      </div>
      {error && <div className="error">{error}</div>}
      {loading && <p>加载中...</p>}
      {dash && (
        <div className="stat-grid">
          <div className="stat-card">
            <strong>指标定义</strong>
            <span>{dash.metricCount ?? 0}</span>
          </div>
          <div className="stat-card">
            <strong>开放审计问题</strong>
            <span>{dash.openAuditIssues ?? 0}</span>
          </div>
          <div className="stat-card">
            <strong>基地映射</strong>
            <span>{dash.siteCount ?? 0}</span>
          </div>
          <div className="stat-card">
            <strong>指标快照</strong>
            <span>{dash.snapshotCount ?? 0}</span>
          </div>
        </div>
      )}
    </section>
  );
}

export function Phase3ReportPanel({ tenantId }: { tenantId: number }) {
  const { data, loading, error } = useListLoader(() => fetchPhase3ReportSummary(tenantId), [tenantId]);
  const summary = data as Phase3ReportSummaryRecord | null;

  return (
    <section className="content-panel">
      <div className="panel-header">
        <div>
          <h2>三期指标总览</h2>
          <p>{summary?.dataSource || '聚合 PHA/MOC/PSSR/屏障/事故/治理服务'}</p>
        </div>
      </div>
      {error && <div className="error">{error}</div>}
      {loading && <p>加载中...</p>}
      {summary && (
        <div className="stat-grid">
          <div className="stat-card">
            <strong>PHA 项目</strong>
            <span>{(summary.pha?.total as number) ?? (summary.pha?.totalCount as number) ?? 0}</span>
          </div>
          <div className="stat-card">
            <strong>MOC 变更</strong>
            <span>{(summary.moc?.total as number) ?? (summary.moc?.totalCount as number) ?? 0}</span>
          </div>
          <div className="stat-card">
            <strong>PSSR 项目</strong>
            <span>{(summary.pssr?.total as number) ?? (summary.pssr?.totalCount as number) ?? 0}</span>
          </div>
          <div className="stat-card">
            <strong>屏障</strong>
            <span>{(summary.barrier?.total as number) ?? (summary.barrier?.totalCount as number) ?? 0}</span>
          </div>
          <div className="stat-card">
            <strong>事故调查</strong>
            <span>{(summary.incident?.total as number) ?? (summary.incident?.totalCount as number) ?? 0}</span>
          </div>
        </div>
      )}
    </section>
  );
}
