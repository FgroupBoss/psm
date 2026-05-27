import React from 'react';
import {
  acceptWorkPermit,
  addWorkPermitGasTest,
  addWorkPermitMonitorRecord,
  addWorkPermitWorker,
  approveWorkPermit,
  checkInWorkPermit,
  confirmWorkPermitMeasure,
  createWorkPermit,
  fetchWorkPermitDetail,
  fetchWorkPermitHealth,
  fetchWorkPermits,
  preCheckWorkPermit,
  resumeWorkPermit,
  saveWorkPermitRiskAnalysis,
  sitePermitWorkPermit,
  submitWorkPermit,
  suspendWorkPermit,
  rejectWorkPermit,
  returnWorkPermit,
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
  const [workerName, setWorkerName] = React.useState('试点作业人');
  const [riskDesc, setRiskDesc] = React.useState('动火点周围易燃物已清理');
  const [riskControl, setRiskControl] = React.useState('配备灭火器并设监护人');
  const [gasName, setGasName] = React.useState('可燃气体');
  const [gasQualified, setGasQualified] = React.useState(true);
  const [signatureText, setSignatureText] = React.useState('Web许可签名');
  const [monitorContent, setMonitorContent] = React.useState('巡检正常');

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

  async function runAndRefresh(action: () => Promise<unknown>, success: string) {
    if (!selectedId) return;
    setActing(true);
    setMessage('');
    setError('');
    try {
      await action();
      setMessage(success);
      openDetail(selectedId);
      await loadList();
    } catch (err: unknown) {
      setError(errorMessage(err, '操作失败'));
    } finally {
      setActing(false);
    }
  }

  async function handleCreate(type: 'HOT_WORK' | 'CONFINED_SPACE') {
    setActing(true);
    setMessage('');
    setError('');
    try {
      const created = await createWorkPermit({
        tenantId,
        workType: type,
        title: type === 'HOT_WORK' ? '试点动火作业' : '试点受限空间作业',
        areaId: 1,
        contractorCompanyId: 1,
        planStartAt: new Date().toISOString().slice(0, 19).replace('T', ' ')
      });
      setMessage(`已创建草稿 ${created.permitNo}`);
      await loadList();
      openDetail(created.id);
    } catch (err: unknown) {
      setError(errorMessage(err, '创建失败'));
    } finally {
      setActing(false);
    }
  }

  const permitStatus = detail?.permit.status;

  return (
    <section className="content-panel">
      <div className="panel-header">
        <div>
          <h2>危险工作票</h2>
          <p>动火/受限空间 Web 闭环：人员 → 风险/措施 → 审批 → 预检 → 许可 → 监护 → 验收。</p>
          {health && <p className="hint">{health}</p>}
        </div>
        <div className="form-actions">
          <button type="button" onClick={() => handleCreate('HOT_WORK')} disabled={acting}>
            新建动火票
          </button>
          <button type="button" className="secondary" onClick={() => handleCreate('CONFINED_SPACE')} disabled={acting}>
            新建受限空间票
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
            <option value="SUSPENDED">已暂停</option>
            <option value="PENDING_ACCEPTANCE">待验收</option>
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
      {error && <div className="error-banner">{error}</div>}
      {message && <div className="success-banner">{message}</div>}
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
              <tr key={row.id} className={selectedId === row.id ? 'selected-row' : ''}>
                <td>{row.permitNo}</td>
                <td>{row.workType}</td>
                <td>
                  <StatusTag status={row.status} />
                </td>
                <td>{row.title || '-'}</td>
                <td>{formatTime(row.updatedAt)}</td>
                <td>
                  <button type="button" className="linkish" onClick={() => openDetail(row.id)}>
                    详情
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
      {detail && selectedId && (
        <div className="detail-panel">
          <h3>
            {detail.permit.permitNo} · <StatusTag status={detail.permit.status} />
          </h3>
          <p>
            人员 {detail.workers.length} · 措施 {detail.safetyMeasures.length} · 气体 {detail.gasTests.length} · 风险{' '}
            {detail.riskAnalysis.length}
          </p>

          <div className="detail-grid">
            <div>
              <h4>人员与风险</h4>
              <div className="inline-form">
                <input value={workerName} onChange={(e) => setWorkerName(e.target.value)} placeholder="作业人姓名" />
                <button
                  type="button"
                  disabled={acting}
                  onClick={() =>
                    runAndRefresh(
                      () =>
                        addWorkPermitWorker(selectedId, tenantId, {
                          workerType: 'CONTRACTOR',
                          workerId: 1,
                          workerName,
                          companyId: 1
                        }),
                      '已添加作业人员'
                    )
                  }
                >
                  添加承包商人员
                </button>
              </div>
              <div className="inline-form">
                <input value={riskDesc} onChange={(e) => setRiskDesc(e.target.value)} placeholder="风险描述" />
                <input value={riskControl} onChange={(e) => setRiskControl(e.target.value)} placeholder="控制措施" />
                <button
                  type="button"
                  disabled={acting}
                  onClick={() =>
                    runAndRefresh(
                      () => saveWorkPermitRiskAnalysis(selectedId, tenantId, { hazardDesc: riskDesc, controlMeasure: riskControl, riskLevel: 'MEDIUM' }),
                      '已保存风险分析'
                    )
                  }
                >
                  保存风险分析
                </button>
              </div>
              <ul>
                {detail.safetyMeasures.map((m) => (
                  <li key={m.id}>
                    {m.measureName} · {m.confirmStatus}
                    {m.confirmStatus !== 'CONFIRMED' && (
                      <button
                        type="button"
                        className="linkish"
                        disabled={acting}
                        onClick={() =>
                          runAndRefresh(() => confirmWorkPermitMeasure(selectedId, m.id, tenantId), `措施已确认：${m.measureName}`)
                        }
                      >
                        确认
                      </button>
                    )}
                  </li>
                ))}
              </ul>
            </div>
            <div>
              <h4>气体与预检</h4>
              <div className="inline-form">
                <input value={gasName} onChange={(e) => setGasName(e.target.value)} />
                <select value={gasQualified ? '1' : '0'} onChange={(e) => setGasQualified(e.target.value === '1')}>
                  <option value="1">合格</option>
                  <option value="0">不合格</option>
                </select>
                <button
                  type="button"
                  disabled={acting}
                  onClick={() =>
                    runAndRefresh(
                      () => addWorkPermitGasTest(selectedId, tenantId, { gasName, qualified: gasQualified }),
                      '已记录气体检测'
                    )
                  }
                >
                  记录气体
                </button>
              </div>
              <div className="form-actions">
                <button type="button" disabled={acting} onClick={() => runAndRefresh(() => preCheckWorkPermit(selectedId, tenantId, 'SUBMIT'), '已执行提交预检')}>
                  提交预检
                </button>
                <button type="button" disabled={acting} onClick={() => runAndRefresh(() => preCheckWorkPermit(selectedId, tenantId, 'SITE_PERMIT'), '已执行许可预检')}>
                  许可预检
                </button>
                <button type="button" disabled={acting} onClick={() => runAndRefresh(() => submitWorkPermit(selectedId, tenantId), '已提交审批')}>
                  提交审批
                </button>
                <button type="button" disabled={acting} onClick={() => runAndRefresh(() => approveWorkPermit(selectedId, tenantId, '一级审批通过'), '审批通过')}>
                  审批通过
                </button>
                <button type="button" className="secondary" disabled={acting} onClick={() => runAndRefresh(() => returnWorkPermit(selectedId, tenantId, '资料不全退回'), '已退回修改')}>
                  退回
                </button>
                <button type="button" className="secondary" disabled={acting} onClick={() => runAndRefresh(() => rejectWorkPermit(selectedId, tenantId, '不符合条件驳回'), '已驳回关闭')}>
                  驳回
                </button>
              </div>
            </div>
          </div>

          <div className="detail-grid">
            <div>
              <h4>现场许可（Web 模拟）</h4>
              <input value={signatureText} onChange={(e) => setSignatureText(e.target.value)} placeholder="许可签名" />
              <div className="form-actions">
                <button
                  type="button"
                  disabled={acting || permitStatus !== 'PENDING_SITE_PERMIT'}
                  onClick={() =>
                    runAndRefresh(
                      () =>
                        Promise.all([
                          checkInWorkPermit(selectedId, tenantId, { locationText: 'Web现场签到', scanCode: 'AREA-1' }),
                          sitePermitWorkPermit(selectedId, tenantId, { signatureText, locationText: '作业点A', remark: 'Web模拟许可' })
                        ]),
                      '现场许可完成，进入作业中'
                    )
                  }
                >
                  签到并许可开工
                </button>
              </div>
            </div>
            <div>
              <h4>监护 / 暂停 / 验收</h4>
              <input value={monitorContent} onChange={(e) => setMonitorContent(e.target.value)} placeholder="监护记录" />
              <div className="form-actions">
                <button
                  type="button"
                  disabled={acting || (permitStatus !== 'IN_PROGRESS' && permitStatus !== 'SUSPENDED')}
                  onClick={() =>
                    runAndRefresh(
                      () => addWorkPermitMonitorRecord(selectedId, tenantId, { recordType: 'PATROL', content: monitorContent, abnormalFlag: false }),
                      '已记录监护'
                    )
                  }
                >
                  记录监护
                </button>
                <button
                  type="button"
                  disabled={acting || permitStatus !== 'IN_PROGRESS'}
                  onClick={() => runAndRefresh(() => suspendWorkPermit(selectedId, tenantId, '现场异常暂停'), '已暂停作业')}
                >
                  暂停
                </button>
                <button
                  type="button"
                  disabled={acting || permitStatus !== 'SUSPENDED'}
                  onClick={() => runAndRefresh(() => resumeWorkPermit(selectedId, tenantId, '异常消除恢复'), '已恢复作业')}
                >
                  恢复
                </button>
                <button
                  type="button"
                  disabled={acting || (permitStatus !== 'IN_PROGRESS' && permitStatus !== 'PENDING_ACCEPTANCE')}
                  onClick={() =>
                    runAndRefresh(
                      () => acceptWorkPermit(selectedId, tenantId, { acceptanceResult: 'PASS', signatureText, opinion: '验收合格' }),
                      '验收完成并关闭'
                    )
                  }
                >
                  完工验收
                </button>
              </div>
            </div>
          </div>

          <h4>时间线</h4>
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
