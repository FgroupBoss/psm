import React from 'react';
import {
  approveWorkPermit,
  createWorkPermit,
  fetchWorkPermitDetail,
  fetchWorkPermitHealth,
  fetchWorkPermits,
  preCheckWorkPermit,
  submitWorkPermit,
  request
} from '@psm/api-client';
import type { PageResult, TimelineItemRecord, WorkPermitDetailRecord, WorkPermitRecord } from '@psm/domain-types';
import { WORK_PERMIT_API } from '@psm/domain-types';
import { errorMessage, formatTime, StatusTag } from './ui-helpers';

export function WorkPermitsPanel({ tenantId }: { tenantId: number }) {
  const [health, setHealth] = React.useState<string>('');
  const [page, setPage] = React.useState<PageResult<WorkPermitRecord> | null>(null);
  const [status, setStatus] = React.useState<string>('');
  const [workType, setWorkType] = React.useState<string>('');
  const [selectedId, setSelectedId] = React.useState<number | null>(null);
  const [detail, setDetail] = React.useState<WorkPermitDetailRecord | null>(null);
  const [timeline, setTimeline] = React.useState<TimelineItemRecord[]>([]);
  const [message, setMessage] = React.useState<string>('');
  const [error, setError] = React.useState<string>('');
  const [loading, setLoading] = React.useState<boolean>(true);
  const [acting, setActing] = React.useState<boolean>(false);

  const loadList = React.useCallback(() => {
    setLoading(true);
    setError('');
    return Promise.all([
      fetchWorkPermitHealth(),
      fetchWorkPermits({ tenantId, pageNo: 1, pageSize: 20, status: status || undefined, workType: workType || undefined })
    ])
      .then(([healthInfo, permitPage]) => {
        setHealth(`${healthInfo.service} ${healthInfo.version} (${healthInfo.permitCount})`);
        setPage(permitPage);
      })
      .catch((err: unknown) => setError(errorMessage(err, '作业票服务不可用')))
      .finally(() => setLoading(false));
  }, [tenantId, status, workType]);

  React.useEffect(() => {
    loadList();
  }, [loadList]);

  function openDetail(id: number) {
    setSelectedId(id);
    setDetail(null);
    Promise.all([
      fetchWorkPermitDetail(id, tenantId),
      request<TimelineItemRecord[]>(`${WORK_PERMIT_API.base}/${id}/timeline?tenantId=${tenantId}`)
    ])
      .then(([detailData, timelineData]) => {
        setDetail(detailData);
        setTimeline(timelineData);
      })
      .catch((err: unknown) => setError(errorMessage(err, '加载详情失败')));
  }

  async function handleCreate() {
    setActing(true);
    setMessage('');
    setError('');
    try {
      const created = await createWorkPermit({
        tenantId,
        workType: 'HOT_WORK',
        title: '试点动火作业',
        areaId: 1,
        contractorCompanyId: 1,
        planStartAt: new Date().toISOString().slice(0, 19).replace('T', ' ')
      });
      setMessage(`已创建草稿 ${created.permitNo}`);
      await loadList();
    } catch (err: unknown) {
      setError(errorMessage(err, '创建失败'));
    } finally {
      setActing(false);
    }
  }

  async function runAction(action: 'submit' | 'approve' | 'precheck') {
    if (!selectedId) return;
    setActing(true);
    setMessage('');
    setError('');
    try {
      if (action === 'submit') {
        await submitWorkPermit(selectedId, tenantId);
        setMessage('已提交审批');
      } else if (action === 'approve') {
        await approveWorkPermit(selectedId, tenantId, '一级审批通过');
        setMessage('审批通过，进入待许可');
      } else {
        const result = await preCheckWorkPermit(selectedId, tenantId, 'SUBMIT');
        setMessage(result.passed ? '预检通过' : `预检未通过：${result.reasons.join('；')}`);
      }
      openDetail(selectedId);
      await loadList();
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
          <h2>危险工作票</h2>
          <p>动火/受限空间 Web 闭环：创建 → 审批 → 预检 → 许可 → 验收（M06）。</p>
          {health && <p className="hint">{health}</p>}
        </div>
        <div className="form-actions">
          <button type="button" onClick={handleCreate} disabled={acting}>
            新建动火票
          </button>
        </div>
      </div>
      <div className="filter-row">
        <label>
          状态
          <select value={status} onChange={(e) => setStatus(e.target.value)}>
            <option value="">全部</option>
            <option value="DRAFT">草稿</option>
            <option value="APPROVING">审批中</option>
            <option value="PENDING_SITE_PERMIT">待许可</option>
            <option value="IN_PROGRESS">作业中</option>
            <option value="CLOSED">已关闭</option>
          </select>
        </label>
        <label>
          类型
          <select value={workType} onChange={(e) => setWorkType(e.target.value)}>
            <option value="">全部</option>
            <option value="HOT_WORK">动火</option>
            <option value="CONFINED_SPACE">受限空间</option>
          </select>
        </label>
        <button type="button" className="secondary" onClick={loadList}>
          刷新
        </button>
      </div>
      {error && <div className="error">{error}</div>}
      {message && <div className="message">{message}</div>}
      {loading && <div className="empty">加载中...</div>}
      {!loading && page && (
        <table className="data-table">
          <thead>
            <tr>
              <th>编号</th>
              <th>类型</th>
              <th>状态</th>
              <th>标题</th>
              <th>更新时间</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {page.records.map((row) => (
              <tr key={row.id} className={selectedId === row.id ? 'selected' : ''}>
                <td>{row.permitNo}</td>
                <td>{row.workType}</td>
                <td>
                  <StatusTag status={row.status} />
                </td>
                <td>{row.title || '-'}</td>
                <td>{formatTime(row.updatedAt)}</td>
                <td>
                  <button type="button" className="link" onClick={() => openDetail(row.id)}>
                    详情
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
      {detail && (
        <div className="detail-drawer">
          <h3>{detail.permit.permitNo}</h3>
          <p>
            人员 {detail.workers.length} · 措施 {detail.safetyMeasures.length} · 气体 {detail.gasTests.length}
          </p>
          <div className="form-actions">
            <button type="button" disabled={acting} onClick={() => runAction('precheck')}>
              提交预检
            </button>
            <button type="button" disabled={acting} onClick={() => runAction('submit')}>
              提交审批
            </button>
            <button type="button" disabled={acting} onClick={() => runAction('approve')}>
              审批通过
            </button>
          </div>
          <ul className="timeline-list">
            {timeline.map((item, index) => (
              <li key={`${item.itemType}-${index}`}>
                <strong>{item.title}</strong> · {formatTime(item.occurredAt)} · {item.operatorName || '-'}
                {item.content && <p>{item.content}</p>}
              </li>
            ))}
          </ul>
        </div>
      )}
    </section>
  );
}
