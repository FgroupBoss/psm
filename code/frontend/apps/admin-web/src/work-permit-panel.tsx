import React from 'react';
import { TableEmptyRow, TableSkeleton } from '@psm/ui';
import {
  acceptWorkPermit,
  addWorkPermitGasTest,
  addWorkPermitMonitorRecord,
  addWorkPermitWorker,
  approveWorkPermit,
  checkInWorkPermit,
  confirmWorkPermitMeasure,
  createWorkPermit,
  fetchAvailableHotWorkWorkflows,
  fetchHotWorkApprovalProgress,
  saveHeightWorkDetail,
  addHeightWorkHazardFactor,
  addHeightWorkProtectionCheck,
  addHeightWorkEnvironmentCheck,
  heightWorkPreCheck,
  saveConfinedSpaceDetail,
  saveLiftingDetail,
  saveTempElectricDetail,
  saveBlindPlateDetail,
  createBlindPlateRegistry,
  saveExcavationDetail,
  saveRoadBreakDetail,
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
import type {
  HotWorkApprovalProgress,
  HotWorkWorkflowSummary,
  PageResult,
  TimelineItemRecord,
  WorkPermitDetailRecord,
  WorkPermitRecord
} from '@psm/domain-types';
import { WORK_PERMIT_API, WORK_TYPE_LABELS } from '@psm/domain-types';
import { errorMessage, formatTime, StatusTag } from './ui-helpers';

type WorkPermitType =
  | 'HOT_WORK'
  | 'CONFINED_SPACE'
  | 'HEIGHT_WORK'
  | 'LIFTING'
  | 'TEMPORARY_ELECTRIC'
  | 'BLIND_PLATE'
  | 'EXCAVATION'
  | 'ROAD_BREAK';

const PLAN_END = () => new Date(Date.now() + 2 * 24 * 3600 * 1000).toISOString().slice(0, 19).replace('T', ' ');
const PLAN_START = () => new Date().toISOString().slice(0, 19).replace('T', ' ');

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
  const [hotWorkLevel, setHotWorkLevel] = React.useState('LEVEL_2');
  const [workflows, setWorkflows] = React.useState<HotWorkWorkflowSummary[]>([]);
  const [selectedWorkflowId, setSelectedWorkflowId] = React.useState<number | ''>('');
  const [approvalProgress, setApprovalProgress] = React.useState<HotWorkApprovalProgress | null>(null);
  const [selectedTaskId, setSelectedTaskId] = React.useState<number | ''>('');
  const [workHeightM, setWorkHeightM] = React.useState('6');
  const [workLocation, setWorkLocation] = React.useState('装置A三层平台');
  const [workMethod, setWorkMethod] = React.useState('SCAFFOLD');

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

  React.useEffect(() => {
    fetchAvailableHotWorkWorkflows(tenantId, hotWorkLevel, 1)
      .then((items) => {
        setWorkflows(items);
        if (items.length > 0) {
          setSelectedWorkflowId(items[0].id);
        } else {
          setSelectedWorkflowId('');
        }
      })
      .catch(() => setWorkflows([]));
  }, [tenantId, hotWorkLevel]);

  function loadApprovalProgress(id: number) {
    fetchHotWorkApprovalProgress(id, tenantId)
      .then((progress) => {
        setApprovalProgress(progress);
        const firstTask = progress.pendingTasks?.[0];
        setSelectedTaskId(firstTask?.id ?? '');
      })
      .catch(() => {
        setApprovalProgress(null);
        setSelectedTaskId('');
      });
  }

  function openDetail(id: number) {
    setSelectedId(id);
    setDetail(null);
    setApprovalProgress(null);
    Promise.all([
      fetchWorkPermitDetail(id, tenantId),
      request<TimelineItemRecord[]>(`${WORK_PERMIT_API.base}/${id}/timeline?tenantId=${tenantId}`)
    ])
      .then(([detailData, timelineData]) => {
        setDetail(detailData);
        setTimeline(timelineData);
        if (detailData.permit.workType === 'HOT_WORK' && detailData.permit.status === 'APPROVING') {
          loadApprovalProgress(id);
        }
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

  async function seedSpecialtyDetail(type: WorkPermitType, permitId: number) {
    if (type === 'HEIGHT_WORK') {
      await saveHeightWorkDetail(permitId, {
        tenantId,
        workHeightM: Number(workHeightM),
        fallDatumDescription: '距地面基准面',
        workLocation,
        workMethod
      });
      return;
    }
    if (type === 'CONFINED_SPACE') {
      await saveConfinedSpaceDetail(permitId, { tenantId, spaceId: 1, spaceName: '试点罐区受限空间', entryCount: 2, ventilationType: 'FORCED', continuousMonitoring: true });
      return;
    }
    if (type === 'LIFTING') {
      await saveLiftingDetail(permitId, { tenantId, loadName: '试点设备', loadWeightT: 25, craneId: 1, radiusM: 8, liftingPoint: 'A点', landingPoint: 'B点', planRef: 'LF-PLAN-001' });
      return;
    }
    if (type === 'TEMPORARY_ELECTRIC') {
      await saveTempElectricDetail(permitId, { tenantId, sourceId: 1, voltage: '380V', loadKw: 15, hazardousAreaFlag: false, planRef: 'TE-PLAN-001' });
      return;
    }
    if (type === 'BLIND_PLATE') {
      const registry = await createBlindPlateRegistry({ tenantId, blindPlateNo: `BP-${Date.now()}`, pipelineId: 'PL-001', position: '装置A出口', spec: 'DN200', material: 'Q235', tagNo: 'TAG-001' });
      await saveBlindPlateDetail(permitId, { tenantId, blindPlateId: registry.id, operationType: 'INSTALL', pipelineId: 'PL-001', positionDescription: '装置A出口', mediumName: '蒸汽', temperature: '120', pressure: '0.8MPa' });
      return;
    }
    if (type === 'EXCAVATION') {
      await saveExcavationDetail(permitId, { tenantId, depthM: 1.5, areaM2: 20, method: 'MANUAL', drawingRef: 'EX-DRW-001', areaGeoJson: '{}' });
      return;
    }
    if (type === 'ROAD_BREAK') {
      await saveRoadBreakDetail(permitId, { tenantId, roadId: 'RD-001', reason: '管道检修', responsibleUnit: '维修车间', startAt: PLAN_START(), endAt: PLAN_END(), areaGeoJson: '{}' });
    }
  }

  async function handleCreate(type: WorkPermitType) {
    setActing(true);
    setMessage('');
    setError('');
    try {
      const workflow = workflows.find((w) => w.id === selectedWorkflowId);
      const created = await createWorkPermit({
        tenantId,
        workType: type,
        title: `试点${WORK_TYPE_LABELS[type] || type}作业`,
        areaId: 1,
        contractorCompanyId: 1,
        guardianUserId: 1,
        permitIssuerUserId: 2,
        planStartAt: PLAN_START(),
        planEndAt: PLAN_END(),
        ...(type === 'HOT_WORK'
          ? {
              hotWorkLevel,
              workflowTemplateId: workflow?.id,
              workflowTemplateVersion: workflow?.versionNo,
              workflowTemplateName: workflow?.templateName
            }
          : {})
      });
      if (type !== 'HOT_WORK') {
        await seedSpecialtyDetail(type, created.id);
      }
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
          <p>八大作业票 Web 闭环：人员 → 风险/措施 → 审批 → 预检 → 许可 → 监护 → 验收。</p>
          {health && <p className="hint">{health}</p>}
        </div>
        <div className="form-actions">
          <label>
            动火级别
            <select value={hotWorkLevel} onChange={(e) => setHotWorkLevel(e.target.value)}>
              <option value="SPECIAL">特级</option>
              <option value="LEVEL_1">一级</option>
              <option value="LEVEL_2">二级</option>
            </select>
          </label>
          <label>
            审批流
            <select
              value={selectedWorkflowId}
              onChange={(e) => setSelectedWorkflowId(e.target.value ? Number(e.target.value) : '')}
            >
              {workflows.length === 0 && <option value="">（无可用审批流）</option>}
              {workflows.map((w) => (
                <option key={w.id} value={w.id}>
                  {w.templateName} v{w.versionNo}
                </option>
              ))}
            </select>
          </label>
          <button type="button" onClick={() => handleCreate('HOT_WORK')} disabled={acting}>
            新建动火票
          </button>
          <label>
            高度(m)
            <input value={workHeightM} onChange={(e) => setWorkHeightM(e.target.value)} style={{ width: 64 }} />
          </label>
          <label>
            方式
            <select value={workMethod} onChange={(e) => setWorkMethod(e.target.value)}>
              <option value="SCAFFOLD">脚手架</option>
              <option value="PLATFORM">平台</option>
              <option value="LADDER">梯具</option>
            </select>
          </label>
          <button type="button" className="secondary" onClick={() => handleCreate('HEIGHT_WORK')} disabled={acting}>
            新建高处票
          </button>
          <button type="button" className="secondary" onClick={() => handleCreate('CONFINED_SPACE')} disabled={acting}>
            受限空间
          </button>
          <button type="button" className="secondary" onClick={() => handleCreate('LIFTING')} disabled={acting}>
            吊装
          </button>
          <button type="button" className="secondary" onClick={() => handleCreate('TEMPORARY_ELECTRIC')} disabled={acting}>
            临电
          </button>
          <button type="button" className="secondary" onClick={() => handleCreate('BLIND_PLATE')} disabled={acting}>
            盲板
          </button>
          <button type="button" className="secondary" onClick={() => handleCreate('EXCAVATION')} disabled={acting}>
            动土
          </button>
          <button type="button" className="secondary" onClick={() => handleCreate('ROAD_BREAK')} disabled={acting}>
            断路
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
            {Object.entries(WORK_TYPE_LABELS).map(([code, label]) => (
              <option key={code} value={code}>
                {label}
              </option>
            ))}
          </select>
        </label>
        <button type="button" className="secondary" onClick={loadList}>
          刷新
        </button>
      </div>
      {error && <div className="error-banner">{error}</div>}
      {message && <div className="success-banner">{message}</div>}
      <div className="psm-table-scroll">
        {loading ? (
          <TableSkeleton rows={4} columns={5} hasActions />
        ) : page ? (
          <table className="psm-data-table">
            <thead>
              <tr>
                <th>编号</th>
                <th>类型</th>
                <th>状态</th>
                <th>标题</th>
                <th>更新时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              {page.records.length === 0 ? (
                <TableEmptyRow
                  colSpan={6}
                  message="暂无作业票"
                  hasActiveFilters={Boolean(status || workType)}
                  onClearFilters={() => {
                    setStatus('');
                    setWorkType('');
                  }}
                />
              ) : (
                page.records.map((row) => (
                  <tr key={row.id} className={selectedId === row.id ? 'selected-row' : ''}>
                    <td>{row.permitNo}</td>
                    <td>{WORK_TYPE_LABELS[row.workType] || row.workType}</td>
                    <td>
                      <StatusTag status={row.status} />
                    </td>
                    <td>{row.title || '-'}</td>
                    <td>{formatTime(row.updatedAt)}</td>
                    <td className="actions">
                      <button type="button" className="linkish" onClick={() => openDetail(row.id)}>
                        详情
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        ) : null}
      </div>
      {detail && selectedId && (
        <div className="detail-panel">
          <h3>
            {detail.permit.permitNo} · <StatusTag status={detail.permit.status} />
          </h3>
          <p>
            人员 {detail.workers.length} · 措施 {detail.safetyMeasures.length} · 气体 {detail.gasTests.length} · 风险{' '}
            {detail.riskAnalysis.length}
            {detail.permit.workType === 'HOT_WORK' && detail.permit.workflowTemplateName && (
              <> · 审批流 {detail.permit.workflowTemplateName}</>
            )}
            {detail.permit.workType === 'HEIGHT_WORK' && detail.heightWorkDetail && (
              <>
                {' '}
                · {detail.heightWorkDetail.heightLevel} / {detail.heightWorkDetail.riskClass}类 · {detail.heightWorkDetail.workHeightM}m
              </>
            )}
          </p>

          {['CONFINED_SPACE', 'LIFTING', 'TEMPORARY_ELECTRIC', 'BLIND_PLATE', 'EXCAVATION', 'ROAD_BREAK'].includes(
            detail.permit.workType
          ) && (
            <div className="detail-panel-sub">
              <h4>{WORK_TYPE_LABELS[detail.permit.workType]}专项详情</h4>
              <pre className="hint" style={{ whiteSpace: 'pre-wrap', fontSize: 12 }}>
                {JSON.stringify(
                  detail.confinedSpaceDetail ||
                    detail.liftingDetail ||
                    detail.tempElectricDetail ||
                    detail.blindPlateDetail ||
                    detail.excavationDetail ||
                    detail.roadBreakDetail ||
                    detail.confinedSpaceFlowProgress ||
                    detail.liftingFlowProgress ||
                    {},
                  null,
                  2
                )}
              </pre>
            </div>
          )}

          {detail.permit.workType === 'HEIGHT_WORK' && detail.heightWorkFlowProgress && (
            <div className="detail-panel-sub">
              <h4>高处作业流程进度</h4>
              <ul>
                {detail.heightWorkFlowProgress.nodes.map((node) => (
                  <li key={node.nodeCode} className={node.current ? 'selected-row' : ''}>
                    {node.nodeName} · {node.current ? '当前' : node.status}
                  </li>
                ))}
              </ul>
              {selectedId && (
                <div className="form-actions">
                  <button
                    type="button"
                    disabled={acting}
                    onClick={() =>
                      runAndRefresh(
                        () => addHeightWorkHazardFactor(selectedId, tenantId, { factorCode: 'SLIPPERY', controlMeasure: '已清理易滑物' }),
                        '已添加危险因素'
                      )
                    }
                  >
                    添加易滑因素(B类)
                  </button>
                  <button
                    type="button"
                    disabled={acting}
                    onClick={() =>
                      runAndRefresh(async () => {
                        const items = [
                          { code: 'HWPC_FACILITY', name: '登高设施合格' },
                          { code: 'HWPC_HARNESS', name: '安全带系挂可靠' },
                          { code: 'HWPC_ANCHOR', name: '锚点可靠' },
                          { code: 'HWPC_BELOW_GUARD', name: '下方警戒区' },
                          { code: 'HWPC_TOOL_TETHER', name: '工具防坠落' }
                        ];
                        for (const item of items) {
                          await addHeightWorkProtectionCheck(selectedId, {
                            tenantId,
                            checkStage: 'SITE_PERMIT',
                            itemCode: item.code,
                            itemName: item.name,
                            checkResult: 'PASS'
                          });
                        }
                        await addHeightWorkEnvironmentCheck(selectedId, {
                          tenantId,
                          checkStage: 'SITE_PERMIT',
                          windLevel: '3',
                          weatherType: 'CLEAR',
                          checkResult: 'PASS',
                          dataSource: 'MANUAL'
                        });
                      }, '已记录现场防护与环境检查')
                    }
                  >
                    模拟现场检查
                  </button>
                  <button
                    type="button"
                    disabled={acting}
                    onClick={() =>
                      runAndRefresh(() => heightWorkPreCheck(selectedId, tenantId, 'SITE_PERMIT'), '已执行高处专项预检')
                    }
                  >
                    高处许可预检
                  </button>
                </div>
              )}
            </div>
          )}

          {detail.permit.status === 'APPROVING' && approvalProgress?.instanceId && (
            <div className="detail-panel-sub">
              <h4>多节点审批进度</h4>
              <p>
                当前节点：{approvalProgress.currentNodeName || '-'}（{approvalProgress.approvedCount ?? 0}/
                {approvalProgress.requiredCount ?? 0}，{approvalProgress.signMode === 'ALL' ? '会签' : '或签'}）
              </p>
              <ul>
                {approvalProgress.nodes.map((node) => (
                  <li key={node.nodeSeq}>
                    {node.nodeSeq}. {node.nodeName} · {node.status}
                  </li>
                ))}
              </ul>
              {approvalProgress.pendingTasks.length > 0 && (
                <label>
                  待办任务
                  <select
                    value={selectedTaskId}
                    onChange={(e) => setSelectedTaskId(e.target.value ? Number(e.target.value) : '')}
                  >
                    {approvalProgress.pendingTasks.map((task) => (
                      <option key={task.id} value={task.id}>
                        {task.nodeName} · {task.assigneeName || task.assigneeUserId}
                      </option>
                    ))}
                  </select>
                </label>
              )}
            </div>
          )}

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
                <button
                  type="button"
                  disabled={acting}
                  onClick={() =>
                    runAndRefresh(() => {
                      const taskId = typeof selectedTaskId === 'number' ? selectedTaskId : undefined;
                      return approveWorkPermit(
                        selectedId,
                        tenantId,
                        taskId
                          ? { opinion: '节点审批通过', approvalTaskId: taskId, action: 'APPROVE' }
                          : '一级审批通过'
                      );
                    }, '审批通过')
                  }
                >
                  审批通过
                </button>
                <button
                  type="button"
                  className="secondary"
                  disabled={acting}
                  onClick={() =>
                    runAndRefresh(() => {
                      const taskId = typeof selectedTaskId === 'number' ? selectedTaskId : undefined;
                      return returnWorkPermit(
                        selectedId,
                        tenantId,
                        taskId
                          ? { reason: '资料不全退回', approvalTaskId: taskId, action: 'RETURN' }
                          : '资料不全退回'
                      );
                    }, '已退回修改')
                  }
                >
                  退回
                </button>
                <button
                  type="button"
                  className="secondary"
                  disabled={acting}
                  onClick={() =>
                    runAndRefresh(() => {
                      const taskId = typeof selectedTaskId === 'number' ? selectedTaskId : undefined;
                      return rejectWorkPermit(
                        selectedId,
                        tenantId,
                        taskId
                          ? { reason: '不符合条件驳回', approvalTaskId: taskId, action: 'REJECT' }
                          : '不符合条件驳回'
                      );
                    }, '已驳回关闭')
                  }
                >
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
