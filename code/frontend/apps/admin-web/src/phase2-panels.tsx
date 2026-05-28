import React from 'react';
import {
  completeInspectionTask,
  confirmHazard,
  coordinateSimopsConflict,
  escalateHazard,
  fetchAreaHeadcount,
  fetchHazardDetail,
  fetchHazardStatistics,
  fetchHazards,
  fetchInspectionPlans,
  fetchInspectionStatistics,
  fetchInspectionTaskDetail,
  fetchInspectionTasks,
  fetchLocEvents,
  fetchLocTags,
  fetchPhase2ReportSummary,
  fetchRegReportTasks,
  fetchRiskColorMap,
  fetchRiskUnitTree,
  fetchSimopsConflicts,
  fetchSimopsRules,
  fetchSimopsStatistics,
  fetchVideoAiEvents,
  fetchVideoCameras,
  fetchVisitorRecords,
  ignoreVideoAiEvent,
  rectifyHazard,
  retryRegReportTask,
  reviewHazard,
  triggerRegReport,
  videoAiEventToAlarm
} from '@psm/api-client';
import type {
  HazardReportRecord,
  InspectionPlanRecord,
  InspectionTaskRecord,
  LocEventRecord,
  LocTagRecord,
  PageResult,
  Phase2ReportSummaryRecord,
  RegReportTaskRecord,
  RiskUnitTreeNode,
  SimopsConflictRuleRecord,
  SimopsScanResultRecord,
  VideoAiEventRecord,
  VideoCameraRecord,
  VisitorAccessRecord
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

function TreeList({ nodes, depth = 0 }: { nodes: RiskUnitTreeNode[]; depth?: number }) {
  return (
    <ul className="tree-list" style={{ paddingLeft: depth ? 16 : 0 }}>
      {nodes.map((node) => (
        <li key={node.id}>
          <span>{node.unitName}</span>
          {node.riskLevel && <StatusTag status={node.riskLevel} />}
          {node.children && node.children.length > 0 && <TreeList nodes={node.children} depth={depth + 1} />}
        </li>
      ))}
    </ul>
  );
}

export function DualPreventionRiskPanel({ tenantId }: { tenantId: number }) {
  const [colorMap, setColorMap] = React.useState<Record<string, number>>({});
  const { data: tree, loading, error, reload } = useListLoader(() => fetchRiskUnitTree(tenantId), [tenantId]);

  React.useEffect(() => {
    fetchRiskColorMap(tenantId).then(setColorMap).catch(() => setColorMap({}));
  }, [tenantId]);

  return (
    <section className="content-panel">
      <div className="panel-header">
        <div>
          <h2>风险清单与四色图</h2>
          <p>风险单元树形清单及红橙黄蓝分布统计。</p>
        </div>
        <button type="button" className="secondary" onClick={() => reload()}>
          刷新
        </button>
      </div>
      {error && <div className="error">{error}</div>}
      <div className="split-layout">
        <div className="detail-card">
          <h3>四色分布</h3>
          {Object.keys(colorMap).length === 0 && !loading && <p className="hint">暂无数据</p>}
          <div className="stat-grid">
            {Object.entries(colorMap).map(([level, count]) => (
              <div key={level} className="stat-card">
                <strong>{level}</strong>
                <span>{count}</span>
              </div>
            ))}
          </div>
        </div>
        <div className="detail-card">
          <h3>风险单元树</h3>
          {loading && <p>加载中...</p>}
          {tree && tree.length > 0 ? <TreeList nodes={tree} /> : !loading && <p className="hint">暂无风险单元</p>}
        </div>
      </div>
    </section>
  );
}

export function DualPreventionHazardsPanel({ tenantId }: { tenantId: number }) {
  const [page, setPage] = React.useState<PageResult<HazardReportRecord> | null>(null);
  const [stats, setStats] = React.useState<{ totalCount?: number; overdueCount?: number }>({});
  const [selectedId, setSelectedId] = React.useState<number | null>(null);
  const [detail, setDetail] = React.useState<HazardReportRecord | null>(null);
  const [status, setStatus] = React.useState('');
  const [message, setMessage] = React.useState('');
  const [error, setError] = React.useState('');
  const [loading, setLoading] = React.useState(true);
  const [acting, setActing] = React.useState(false);

  const load = React.useCallback(() => {
    setLoading(true);
    return Promise.all([
      fetchHazards({ tenantId, status: status || undefined, pageNo: 1, pageSize: 30 }),
      fetchHazardStatistics(tenantId)
    ])
      .then(([list, stat]) => {
        setPage(list);
        setStats(stat);
      })
      .catch((err: unknown) => setError(errorMessage(err, '隐患服务不可用')))
      .finally(() => setLoading(false));
  }, [tenantId, status]);

  React.useEffect(() => {
    load();
  }, [load]);

  function openDetail(id: number) {
    setSelectedId(id);
    fetchHazardDetail(id, tenantId).then(setDetail).catch((err: unknown) => setError(errorMessage(err, '加载详情失败')));
  }

  async function run(action: () => Promise<unknown>, ok: string) {
    if (!selectedId) return;
    setActing(true);
    setError('');
    try {
      await action();
      setMessage(ok);
      openDetail(selectedId);
      await load();
    } catch (err: unknown) {
      setError(errorMessage(err, '操作失败'));
    } finally {
      setActing(false);
    }
  }

  return (
    <section className="content-panel">
      <div className="panel-header">
        <div>
          <h2>隐患台账</h2>
          <p>
            总计 {stats.totalCount ?? '-'} · 逾期 {stats.overdueCount ?? '-'}
          </p>
        </div>
      </div>
      <div className="filter-row">
        <label>
          状态
          <select value={status} onChange={(e) => setStatus(e.target.value)}>
            <option value="">全部</option>
            <option value="REPORTED">已上报</option>
            <option value="CONFIRMED">已确认</option>
            <option value="RECTIFYING">整改中</option>
            <option value="REVIEWING">待复查</option>
            <option value="CLOSED">已销项</option>
          </select>
        </label>
      </div>
      {error && <div className="error">{error}</div>}
      {message && <div className="message">{message}</div>}
      <div className="split-layout">
        <table className="data-table">
          <thead>
            <tr>
              <th>编号</th>
              <th>等级</th>
              <th>状态</th>
              <th>来源</th>
            </tr>
          </thead>
          <tbody>
            {loading && (
              <tr>
                <td colSpan={4}>加载中...</td>
              </tr>
            )}
            {page?.records.map((row) => (
              <tr key={row.id} className={selectedId === row.id ? 'selected' : ''} onClick={() => openDetail(row.id)}>
                <td>{row.hazardNo || row.id}</td>
                <td>{row.hazardLevel}</td>
                <td>
                  <StatusTag status={row.status} />
                  {row.overdueFlag === 1 && <span className="tag-warn">逾期</span>}
                </td>
                <td>{row.sourceType}</td>
              </tr>
            ))}
          </tbody>
        </table>
        {detail && (
          <div className="detail-card">
            <h3>{detail.hazardNo || detail.id}</h3>
            <p>{detail.description}</p>
            <p>
              截止 {formatTime(detail.rectificationDeadline)} · 发现 {formatTime(detail.foundAt)}
            </p>
            <div className="form-actions">
              {detail.status === 'REPORTED' && (
                <button type="button" disabled={acting} onClick={() => run(() => confirmHazard(detail.id, { tenantId, assigneeUserId: 1, hazardLevel: detail.hazardLevel }), '已确认派单')}>
                  确认派单
                </button>
              )}
              {detail.status === 'RECTIFYING' && (
                <button type="button" disabled={acting} onClick={() => run(() => rectifyHazard(detail.id, { tenantId, content: 'Web整改完成' }), '已提交整改')}>
                  提交整改
                </button>
              )}
              {detail.status === 'REVIEWING' && (
                <>
                  <button type="button" disabled={acting} onClick={() => run(() => reviewHazard(detail.id, { tenantId, passed: true, content: '复查通过' }), '复查通过')}>
                    复查通过
                  </button>
                  <button type="button" className="secondary" disabled={acting} onClick={() => run(() => reviewHazard(detail.id, { tenantId, passed: false, content: '退回整改' }), '已退回')}>
                    退回整改
                  </button>
                </>
              )}
              {detail.overdueFlag === 1 && (
                <button type="button" className="secondary" disabled={acting} onClick={() => run(() => escalateHazard(detail.id, { tenantId, reason: 'Web逾期升级' }), '已升级')}>
                  逾期升级
                </button>
              )}
            </div>
          </div>
        )}
      </div>
    </section>
  );
}

export function InspectionPanel({ tenantId }: { tenantId: number }) {
  const [tab, setTab] = React.useState<'tasks' | 'plans'>('tasks');
  const [tasks, setTasks] = React.useState<PageResult<InspectionTaskRecord> | null>(null);
  const [plans, setPlans] = React.useState<PageResult<InspectionPlanRecord> | null>(null);
  const [stats, setStats] = React.useState<{ completionRate?: number; missedCount?: number }>({});
  const [selectedId, setSelectedId] = React.useState<number | null>(null);
  const [detail, setDetail] = React.useState<InspectionTaskRecord | null>(null);
  const [error, setError] = React.useState('');
  const [message, setMessage] = React.useState('');
  const [acting, setActing] = React.useState(false);

  const load = React.useCallback(() => {
    return Promise.all([
      fetchInspectionTasks({ tenantId, pageNo: 1, pageSize: 30 }),
      fetchInspectionPlans(tenantId),
      fetchInspectionStatistics(tenantId)
    ])
      .then(([taskPage, planPage, stat]) => {
        setTasks(taskPage);
        setPlans(planPage);
        setStats(stat);
      })
      .catch((err: unknown) => setError(errorMessage(err, '巡检服务不可用')));
  }, [tenantId]);

  React.useEffect(() => {
    load();
  }, [load]);

  function openTask(id: number) {
    setSelectedId(id);
    fetchInspectionTaskDetail(id, tenantId).then(setDetail).catch((err: unknown) => setError(errorMessage(err, '加载任务失败')));
  }

  async function completeTask() {
    if (!selectedId) return;
    setActing(true);
    try {
      await completeInspectionTask(selectedId, tenantId);
      setMessage('任务已完成');
      openTask(selectedId);
      await load();
    } catch (err: unknown) {
      setError(errorMessage(err, '完成失败'));
    } finally {
      setActing(false);
    }
  }

  return (
    <section className="content-panel">
      <div className="panel-header">
        <div>
          <h2>智能巡检</h2>
          <p>
            完成率 {(stats.completionRate ?? 0) * 100}% · 漏检 {stats.missedCount ?? 0}
          </p>
        </div>
        <div className="form-actions">
          <button type="button" className={tab === 'tasks' ? '' : 'secondary'} onClick={() => setTab('tasks')}>
            巡检任务
          </button>
          <button type="button" className={tab === 'plans' ? '' : 'secondary'} onClick={() => setTab('plans')}>
            巡检计划
          </button>
        </div>
      </div>
      {error && <div className="error">{error}</div>}
      {message && <div className="message">{message}</div>}
      {tab === 'plans' ? (
        <table className="data-table">
          <thead>
            <tr>
              <th>计划</th>
              <th>周期</th>
              <th>危险源</th>
            </tr>
          </thead>
          <tbody>
            {plans?.records.map((p) => (
              <tr key={p.id}>
                <td>
                  {p.planName} ({p.planCode})
                </td>
                <td>{p.cycleType}</td>
                <td>{p.majorHazardId || '-'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <div className="split-layout">
          <table className="data-table">
            <thead>
              <tr>
                <th>任务号</th>
                <th>状态</th>
                <th>计划开始</th>
                <th>异常</th>
              </tr>
            </thead>
            <tbody>
              {tasks?.records.map((t) => (
                <tr key={t.id} className={selectedId === t.id ? 'selected' : ''} onClick={() => openTask(t.id)}>
                  <td>{t.taskNo || t.id}</td>
                  <td>
                    <StatusTag status={t.status} />
                  </td>
                  <td>{formatTime(t.scheduledStart)}</td>
                  <td>{t.abnormalCount ?? 0}</td>
                </tr>
              ))}
            </tbody>
          </table>
          {detail && (
            <div className="detail-card">
              <h3>{detail.taskNo}</h3>
              <p>
                状态 {detail.status} · 完成率 {detail.completionRate ?? 0}
              </p>
              <button type="button" disabled={acting || detail.status === 'COMPLETED'} onClick={completeTask}>
                标记完成
              </button>
            </div>
          )}
        </div>
      )}
    </section>
  );
}

export function LocationPanel({ tenantId }: { tenantId: number }) {
  const [tab, setTab] = React.useState<'tags' | 'events' | 'visitors'>('tags');
  const [tags, setTags] = React.useState<PageResult<LocTagRecord> | null>(null);
  const [events, setEvents] = React.useState<PageResult<LocEventRecord> | null>(null);
  const [visitors, setVisitors] = React.useState<PageResult<VisitorAccessRecord> | null>(null);
  const [headcount, setHeadcount] = React.useState<number | null>(null);
  const [error, setError] = React.useState('');

  React.useEffect(() => {
    Promise.all([fetchLocTags(tenantId), fetchLocEvents(tenantId), fetchVisitorRecords(tenantId), fetchAreaHeadcount(tenantId, 1)])
      .then(([tagPage, eventPage, visitorPage, hc]) => {
        setTags(tagPage);
        setEvents(eventPage);
        setVisitors(visitorPage);
        setHeadcount(hc.headcount);
      })
      .catch((err: unknown) => setError(errorMessage(err, '定位服务不可用')));
  }, [tenantId]);

  return (
    <section className="content-panel">
      <div className="panel-header">
        <div>
          <h2>人员定位与封闭化</h2>
          <p>区域 1 当前人数：{headcount ?? '-'}</p>
        </div>
        <div className="form-actions">
          {(['tags', 'events', 'visitors'] as const).map((key) => (
            <button key={key} type="button" className={tab === key ? '' : 'secondary'} onClick={() => setTab(key)}>
              {key === 'tags' ? '标签' : key === 'events' ? '事件' : '访客'}
            </button>
          ))}
        </div>
      </div>
      {error && <div className="error">{error}</div>}
      {tab === 'tags' && (
        <table className="data-table">
          <thead>
            <tr>
              <th>标签号</th>
              <th>类型</th>
              <th>状态</th>
            </tr>
          </thead>
          <tbody>
            {tags?.records.map((t) => (
              <tr key={t.id}>
                <td>{t.tagNo}</td>
                <td>{t.tagType}</td>
                <td>{t.status}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
      {tab === 'events' && (
        <table className="data-table">
          <thead>
            <tr>
              <th>类型</th>
              <th>标签</th>
              <th>时间</th>
            </tr>
          </thead>
          <tbody>
            {events?.records.map((e) => (
              <tr key={e.id}>
                <td>{e.eventType}</td>
                <td>{e.tagNo}</td>
                <td>{formatTime(e.eventTime)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
      {tab === 'visitors' && (
        <table className="data-table">
          <thead>
            <tr>
              <th>访客</th>
              <th>门禁</th>
              <th>时间</th>
            </tr>
          </thead>
          <tbody>
            {visitors?.records.map((v) => (
              <tr key={v.id}>
                <td>{v.visitorName}</td>
                <td>{v.gateName}</td>
                <td>{formatTime(v.accessTime)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </section>
  );
}

export function VideoPanel({ tenantId }: { tenantId: number }) {
  const [cameras, setCameras] = React.useState<PageResult<VideoCameraRecord> | null>(null);
  const [events, setEvents] = React.useState<PageResult<VideoAiEventRecord> | null>(null);
  const [selected, setSelected] = React.useState<VideoAiEventRecord | null>(null);
  const [error, setError] = React.useState('');
  const [message, setMessage] = React.useState('');

  const load = React.useCallback(() => {
    return Promise.all([fetchVideoCameras(tenantId), fetchVideoAiEvents(tenantId)])
      .then(([camPage, evPage]) => {
        setCameras(camPage);
        setEvents(evPage);
      })
      .catch((err: unknown) => setError(errorMessage(err, '视频服务不可用')));
  }, [tenantId]);

  React.useEffect(() => {
    load();
  }, [load]);

  async function act(fn: () => Promise<VideoAiEventRecord>, ok: string) {
    if (!selected) return;
    try {
      const updated = await fn();
      setSelected(updated);
      setMessage(ok);
      await load();
    } catch (err: unknown) {
      setError(errorMessage(err, '操作失败'));
    }
  }

  return (
    <section className="content-panel">
      <div className="panel-header">
        <div>
          <h2>视频 AI 与过程监护</h2>
          <p>摄像头 {cameras?.total ?? 0} · AI 事件 {events?.total ?? 0}</p>
        </div>
      </div>
      {error && <div className="error">{error}</div>}
      {message && <div className="message">{message}</div>}
      <div className="split-layout">
        <table className="data-table">
          <thead>
            <tr>
              <th>事件</th>
              <th>等级</th>
              <th>状态</th>
            </tr>
          </thead>
          <tbody>
            {events?.records.map((e) => (
              <tr key={e.id} className={selected?.id === e.id ? 'selected' : ''} onClick={() => setSelected(e)}>
                <td>
                  {e.title || e.eventType} ({e.eventNo})
                </td>
                <td>{e.severity}</td>
                <td>{e.status}</td>
              </tr>
            ))}
          </tbody>
        </table>
        {selected && (
          <div className="detail-card">
            <h3>{selected.title || selected.eventType}</h3>
            <p>
              {formatTime(selected.eventTime)} · 报警 #{selected.alarmId || '-'}
            </p>
            <div className="form-actions">
              <button type="button" onClick={() => act(() => videoAiEventToAlarm(selected.id, tenantId), '已转报警')}>
                转报警
              </button>
              <button type="button" className="secondary" onClick={() => act(() => ignoreVideoAiEvent(selected.id, tenantId, 'Web忽略'), '已忽略')}>
                忽略
              </button>
            </div>
          </div>
        )}
      </div>
    </section>
  );
}

export function SimopsPanel({ tenantId }: { tenantId: number }) {
  const [rules, setRules] = React.useState<SimopsConflictRuleRecord[]>([]);
  const [conflicts, setConflicts] = React.useState<PageResult<SimopsScanResultRecord> | null>(null);
  const [stats, setStats] = React.useState<{ totalConflicts?: number; blockCount?: number }>({});
  const [error, setError] = React.useState('');
  const [message, setMessage] = React.useState('');

  React.useEffect(() => {
    Promise.all([fetchSimopsRules(tenantId), fetchSimopsConflicts(tenantId), fetchSimopsStatistics(tenantId)])
      .then(([ruleList, conflictPage, stat]) => {
        setRules(ruleList);
        setConflicts(conflictPage);
        setStats(stat);
      })
      .catch((err: unknown) => setError(errorMessage(err, 'SIMOPS 服务不可用')));
  }, [tenantId]);

  return (
    <section className="content-panel">
      <div className="panel-header">
        <div>
          <h2>SIMOPS 交叉作业</h2>
          <p>
            冲突 {stats.totalConflicts ?? 0} · 阻断 {stats.blockCount ?? 0} · 规则 {rules.length}
          </p>
        </div>
      </div>
      {error && <div className="error">{error}</div>}
      {message && <div className="message">{message}</div>}
      <table className="data-table">
        <thead>
          <tr>
            <th>作业票</th>
            <th>阶段</th>
            <th>结果</th>
            <th>冲突数</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          {conflicts?.records.map((c) => (
            <tr key={c.id}>
              <td>{c.workPermitId}</td>
              <td>{c.scanStage}</td>
              <td>
                <StatusTag status={c.finalAction || '-'} />
              </td>
              <td>{c.conflictCount ?? 0}</td>
              <td>
                {c.finalAction === 'COORDINATE' && (
                  <button
                    type="button"
                    onClick={() =>
                      coordinateSimopsConflict(c.id, { tenantId, decision: 'APPROVED', opinion: 'Web协调通过' })
                        .then(() => setMessage('协调已通过'))
                        .catch((err: unknown) => setError(errorMessage(err, '协调失败')))
                    }
                  >
                    批准协调
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}

export function IntegrationRegPanel({ tenantId }: { tenantId: number }) {
  const [tasks, setTasks] = React.useState<PageResult<RegReportTaskRecord> | null>(null);
  const [error, setError] = React.useState('');
  const [message, setMessage] = React.useState('');

  const load = React.useCallback(() => {
    return fetchRegReportTasks(tenantId)
      .then(setTasks)
      .catch((err: unknown) => setError(errorMessage(err, '监管服务不可用')));
  }, [tenantId]);

  React.useEffect(() => {
    load();
  }, [load]);

  return (
    <section className="content-panel">
      <div className="panel-header">
        <div>
          <h2>监管园区上报</h2>
          <p>定时/手动上报任务与重试。</p>
        </div>
        <button
          type="button"
          onClick={() =>
            triggerRegReport({ tenantId, platformCode: 'MOCK', dataDomain: 'DUAL_PREVENTION' })
              .then(() => {
                setMessage('已触发上报');
                return load();
              })
              .catch((err: unknown) => setError(errorMessage(err, '触发失败')))
          }
        >
          手动补报
        </button>
      </div>
      {error && <div className="error">{error}</div>}
      {message && <div className="message">{message}</div>}
      <table className="data-table">
        <thead>
          <tr>
            <th>任务号</th>
            <th>平台</th>
            <th>域</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          {tasks?.records.map((t) => (
            <tr key={t.id}>
              <td>{t.taskNo || t.id}</td>
              <td>{t.platformCode}</td>
              <td>{t.dataDomain}</td>
              <td>{t.status}</td>
              <td>
                {t.status === 'FAILED' && (
                  <button type="button" className="secondary" onClick={() => retryRegReportTask(t.id, tenantId).then(load)}>
                    重试
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}

export function Phase2ReportPanel({ tenantId }: { tenantId: number }) {
  const { data, loading, error } = useListLoader(() => fetchPhase2ReportSummary(tenantId), [tenantId]);
  const summary = data as Phase2ReportSummaryRecord | null;

  return (
    <section className="content-panel">
      <div className="panel-header">
        <div>
          <h2>二期指标总览</h2>
          <p>{summary?.dataSource || '聚合各二期服务指标'}</p>
        </div>
      </div>
      {error && <div className="error">{error}</div>}
      {loading && <p>加载中...</p>}
      {summary && (
        <div className="stat-grid">
          <div className="stat-card">
            <strong>隐患总数</strong>
            <span>{summary.dualPrevention?.totalCount ?? 0}</span>
          </div>
          <div className="stat-card">
            <strong>隐患逾期</strong>
            <span>{summary.dualPrevention?.overdueCount ?? 0}</span>
          </div>
          <div className="stat-card">
            <strong>巡检完成</strong>
            <span>{summary.inspection?.completedCount ?? 0}</span>
          </div>
          <div className="stat-card">
            <strong>定位事件</strong>
            <span>{summary.location?.eventCount ?? 0}</span>
          </div>
          <div className="stat-card">
            <strong>视频 AI</strong>
            <span>{summary.video?.aiEventCount ?? 0}</span>
          </div>
          <div className="stat-card">
            <strong>SIMOPS 冲突</strong>
            <span>{summary.simops?.totalConflicts ?? 0}</span>
          </div>
        </div>
      )}
    </section>
  );
}
