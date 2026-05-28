import React from 'react';
import { createRoot } from 'react-dom/client';
import { clearTokens, getAccessToken } from '@psm/auth';
import {
  completeInspectionTask,
  createHazard,
  fetchCurrentUser,
  fetchInspectionTaskDetail,
  fetchInspectionTasks,
  fetchMobileTasks,
  fetchMobileWorkPermitDetail,
  login,
  mobileAcceptance,
  mobileAlarmFeedback,
  mobileCheckIn,
  mobileConfirmMeasure,
  mobileGasTest,
  mobileMonitorRecord,
  mobileResume,
  mobileSitePermit,
  mobileSuspend,
  mobileUploadFile,
  registerInspectionAbnormal,
  signInInspectionTask,
  startInspectionTask,
  syncInspectionDraft,
  syncMobileDraft,
  fetchWorkPermitTimeline
} from '@psm/api-client';
import type {
  AuthUser,
  InspectionTaskRecord,
  MobileTaskRecord,
  SafetyMeasureRecord,
  TimelineItemRecord,
  WorkPermitDetailRecord
} from '@psm/domain-types';
import './styles.css';

type Tab = 'tasks' | 'inspection' | 'hazard';
type Screen = Tab | 'permit' | 'alarm' | 'inspection-detail';

function App() {
  const [user, setUser] = React.useState<AuthUser | null>(null);
  const [tab, setTab] = React.useState<Tab>('tasks');
  const [screen, setScreen] = React.useState<Screen>('tasks');
  const [tasks, setTasks] = React.useState<MobileTaskRecord[]>([]);
  const [inspectionTasks, setInspectionTasks] = React.useState<InspectionTaskRecord[]>([]);
  const [selectedTask, setSelectedTask] = React.useState<MobileTaskRecord | null>(null);
  const [selectedInspection, setSelectedInspection] = React.useState<InspectionTaskRecord | null>(null);
  const [permitDetail, setPermitDetail] = React.useState<WorkPermitDetailRecord | null>(null);
  const [error, setError] = React.useState('');
  const [message, setMessage] = React.useState('');
  const [acting, setActing] = React.useState(false);
  const [signature, setSignature] = React.useState('移动许可签名');
  const [gasName, setGasName] = React.useState('O2');
  const [gasQualified, setGasQualified] = React.useState(true);
  const [monitorText, setMonitorText] = React.useState('巡检正常');
  const [selectedMeasure, setSelectedMeasure] = React.useState<SafetyMeasureRecord | null>(null);
  const [timeline, setTimeline] = React.useState<TimelineItemRecord[]>([]);
  const [uploadedFileName, setUploadedFileName] = React.useState('');
  const [hazardDesc, setHazardDesc] = React.useState('现场发现一般隐患');
  const [hazardLevel, setHazardLevel] = React.useState('GENERAL');
  const [abnormalDesc, setAbnormalDesc] = React.useState('巡检异常项');

  async function handleLogin(event: React.FormEvent) {
    event.preventDefault();
    setError('');
    try {
      const result = await login({ tenantId: 1, username: 'admin', password: '' });
      setUser(result.user);
      await loadAll(result.user);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : '登录失败');
    }
  }

  async function loadAll(current: AuthUser) {
    const [todoList, inspPage] = await Promise.all([
      fetchMobileTasks(current.tenantId, current.id, 'ALL'),
      fetchInspectionTasks({ tenantId: current.tenantId, pageNo: 1, pageSize: 30 })
    ]);
    setTasks(todoList);
    setInspectionTasks(inspPage.records);
  }

  async function openTask(task: MobileTaskRecord) {
    setSelectedTask(task);
    setMessage('');
    setError('');
    if (task.taskType === 'ALARM') {
      setScreen('alarm');
      return;
    }
    setScreen('permit');
    const [detail, timelineData] = await Promise.all([
      fetchMobileWorkPermitDetail(task.bizId, user!.tenantId),
      fetchWorkPermitTimeline(task.bizId, user!.tenantId)
    ]);
    setPermitDetail(detail);
    setTimeline(timelineData);
    const pending = detail.safetyMeasures.find((m) => m.confirmStatus !== 'CONFIRMED');
    setSelectedMeasure(pending || detail.safetyMeasures[0] || null);
  }

  async function openInspection(task: InspectionTaskRecord) {
    setSelectedInspection(task);
    setScreen('inspection-detail');
    const detail = await fetchInspectionTaskDetail(task.id, user!.tenantId);
    setSelectedInspection(detail);
  }

  async function runAction(action: () => Promise<unknown>, success: string) {
    if (!user) return;
    setActing(true);
    setError('');
    try {
      await action();
      setMessage(success);
      if (screen === 'permit' && selectedTask) {
        const [detail, timelineData] = await Promise.all([
          fetchMobileWorkPermitDetail(selectedTask.bizId, user.tenantId),
          fetchWorkPermitTimeline(selectedTask.bizId, user.tenantId)
        ]);
        setPermitDetail(detail);
        setTimeline(timelineData);
      }
      if (screen === 'inspection-detail' && selectedInspection) {
        const detail = await fetchInspectionTaskDetail(selectedInspection.id, user.tenantId);
        setSelectedInspection(detail);
      }
      await loadAll(user);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : '操作失败');
    } finally {
      setActing(false);
    }
  }

  function switchTab(next: Tab) {
    setTab(next);
    setScreen(next);
    setMessage('');
    setError('');
  }

  async function handlePhotoUpload(event: React.ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0];
    if (!file || !user || !selectedTask) return;
    await runAction(async () => {
      const uploaded = await mobileUploadFile(user.tenantId, file, 'WORK_PERMIT', selectedTask.bizId);
      setUploadedFileName(`${uploaded.fileName} (#${uploaded.id})`);
    }, `附件已上传：${file.name}`);
  }

  React.useEffect(() => {
    if (!getAccessToken()) return;
    fetchCurrentUser()
      .then((current) => {
        setUser(current);
        return loadAll(current);
      })
      .catch(() => clearTokens());
  }, []);

  if (!user) {
    return (
      <main className="mobile-shell">
        <h1>PSM 移动现场</h1>
        <p>作业许可、智能巡检、隐患上报（经网关）。</p>
        <form onSubmit={handleLogin}>
          <button type="submit">使用试点账号登录</button>
        </form>
        {error && <p className="error">{error}</p>}
      </main>
    );
  }

  const tabBar = (
    <nav className="mobile-tabs">
      <button type="button" className={tab === 'tasks' ? 'active' : ''} onClick={() => switchTab('tasks')}>
        待办
      </button>
      <button type="button" className={tab === 'inspection' ? 'active' : ''} onClick={() => switchTab('inspection')}>
        巡检
      </button>
      <button type="button" className={tab === 'hazard' ? 'active' : ''} onClick={() => switchTab('hazard')}>
        隐患
      </button>
    </nav>
  );

  if (screen === 'alarm' && selectedTask) {
    return (
      <main className="mobile-shell">
        <button type="button" className="back-link" onClick={() => setScreen('tasks')}>
          ← 返回
        </button>
        <div className="detail-card">
          <h3>报警处置</h3>
          <p>{selectedTask.title}</p>
          <button
            type="button"
            disabled={acting}
            onClick={() => runAction(() => mobileAlarmFeedback(selectedTask.bizId, user.tenantId, '移动端现场反馈'), '报警反馈已提交')}
          >
            提交处置反馈
          </button>
        </div>
        {error && <p className="error">{error}</p>}
        {message && <p className="message">{message}</p>}
      </main>
    );
  }

  if (screen === 'permit') {
    return (
      <main className="mobile-shell">
        <button type="button" className="back-link" onClick={() => setScreen('tasks')}>
          ← 返回待办
        </button>
        {permitDetail && (
          <div className="detail-card">
            <h3>{permitDetail.permit.permitNo}</h3>
            <p>
              {permitDetail.permit.workType} · {permitDetail.permit.status}
            </p>
            <p>
              人员 {permitDetail.workers.length} · 措施 {permitDetail.safetyMeasures.length} · 气体 {permitDetail.gasTests.length}
            </p>
          </div>
        )}
        {error && <p className="error">{error}</p>}
        {message && <p className="message">{message}</p>}

        <div className="detail-card">
          <h4>现场签到</h4>
          <button
            type="button"
            disabled={acting}
            onClick={() =>
              runAction(
                () => mobileCheckIn(selectedTask!.bizId, { tenantId: user.tenantId, locationText: '移动端GPS', scanCode: 'MOBILE-1' }),
                '签到完成'
              )
            }
          >
            现场签到
          </button>
        </div>

        <div className="detail-card">
          <h4>气体检测</h4>
          <input value={gasName} onChange={(e) => setGasName(e.target.value)} />
          <select value={gasQualified ? '1' : '0'} onChange={(e) => setGasQualified(e.target.value === '1')}>
            <option value="1">合格</option>
            <option value="0">不合格</option>
          </select>
          <button
            type="button"
            disabled={acting}
            onClick={() =>
              runAction(
                () => mobileGasTest(selectedTask!.bizId, { tenantId: user.tenantId, gasName, qualified: gasQualified }),
                '气体检测已提交'
              )
            }
          >
            提交气体检测
          </button>
        </div>

        <div className="detail-card">
          <h4>措施确认</h4>
          <select
            value={selectedMeasure?.id || ''}
            onChange={(e) => {
              const measure = permitDetail?.safetyMeasures.find((m) => m.id === Number(e.target.value));
              setSelectedMeasure(measure || null);
            }}
          >
            {permitDetail?.safetyMeasures.map((m) => (
              <option key={m.id} value={m.id}>
                {m.measureName} ({m.confirmStatus})
              </option>
            ))}
          </select>
          <button
            type="button"
            disabled={acting || !selectedMeasure}
            onClick={() =>
              runAction(
                () =>
                  mobileConfirmMeasure(selectedTask!.bizId, {
                    tenantId: user.tenantId,
                    measureId: selectedMeasure!.id,
                    remark: '移动端确认'
                  }),
                '措施已确认'
              )
            }
          >
            确认措施
          </button>
        </div>

        <div className="detail-card">
          <h4>附件拍照</h4>
          <input type="file" accept="image/*" capture="environment" onChange={handlePhotoUpload} />
          {uploadedFileName && <p className="message">最近上传：{uploadedFileName}</p>}
        </div>

        {timeline.length > 0 && (
          <div className="detail-card">
            <h4>作业时间线</h4>
            <ul className="timeline-list">
              {timeline.map((item, index) => (
                <li key={`${item.itemType}-${index}`}>
                  <strong>{item.title}</strong>
                  <span>
                    {item.operatorName || '-'} · {item.occurredAt || ''}
                  </span>
                </li>
              ))}
            </ul>
          </div>
        )}

        <div className="detail-card">
          <h4>许可开工</h4>
          <input value={signature} onChange={(e) => setSignature(e.target.value)} placeholder="电子签名" />
          <button
            type="button"
            disabled={acting}
            onClick={() =>
              runAction(
                () => mobileSitePermit(selectedTask!.bizId, { tenantId: user.tenantId, signatureText: signature, locationText: '作业点' }),
                '许可开工成功'
              )
            }
          >
            电子签名许可
          </button>
        </div>

        <div className="detail-card">
          <h4>监护 / 暂停 / 验收</h4>
          <input value={monitorText} onChange={(e) => setMonitorText(e.target.value)} />
          <div className="action-grid">
            <button
              type="button"
              disabled={acting}
              onClick={() =>
                runAction(
                  () =>
                    mobileMonitorRecord(selectedTask!.bizId, {
                      tenantId: user.tenantId,
                      recordType: 'PATROL',
                      content: monitorText,
                      abnormalFlag: false
                    }),
                  '监护记录已保存'
                )
              }
            >
              记录监护
            </button>
            <button type="button" className="secondary" disabled={acting} onClick={() => runAction(() => mobileSuspend(selectedTask!.bizId, user.tenantId, '异常暂停'), '已暂停')}>
              暂停作业
            </button>
            <button type="button" className="secondary" disabled={acting} onClick={() => runAction(() => mobileResume(selectedTask!.bizId, user.tenantId, '恢复作业'), '已恢复')}>
              恢复作业
            </button>
            <button
              type="button"
              disabled={acting}
              onClick={() =>
                runAction(
                  () =>
                    mobileAcceptance(selectedTask!.bizId, {
                      tenantId: user.tenantId,
                      acceptanceResult: 'PASS',
                      signatureText: signature,
                      opinion: '移动验收'
                    }),
                  '验收完成'
                )
              }
            >
              完工验收
            </button>
          </div>
        </div>
      </main>
    );
  }

  if (screen === 'inspection-detail' && selectedInspection) {
    return (
      <main className="mobile-shell">
        <button type="button" className="back-link" onClick={() => setScreen('inspection')}>
          ← 返回巡检
        </button>
        <div className="detail-card">
          <h3>{selectedInspection.taskNo || selectedInspection.id}</h3>
          <p>
            状态 {selectedInspection.status} · 异常 {selectedInspection.abnormalCount ?? 0}
          </p>
        </div>
        {error && <p className="error">{error}</p>}
        {message && <p className="message">{message}</p>}
        <div className="detail-card action-grid">
          <button type="button" disabled={acting} onClick={() => runAction(() => startInspectionTask(selectedInspection.id, { tenantId: user.tenantId, executorId: user.id }), '已开始')}>
            开始任务
          </button>
          <button
            type="button"
            disabled={acting}
            onClick={() =>
              runAction(
                () => signInInspectionTask(selectedInspection.id, { tenantId: user.tenantId, routePointId: 1, signType: 'QR', signCode: 'POINT-1' }),
                '签到成功'
              )
            }
          >
            扫码签到
          </button>
          <button type="button" disabled={acting} onClick={() => runAction(() => completeInspectionTask(selectedInspection.id, user.tenantId), '任务完成')}>
            完成任务
          </button>
        </div>
        <div className="detail-card">
          <h4>异常登记</h4>
          <input value={abnormalDesc} onChange={(e) => setAbnormalDesc(e.target.value)} />
          <button
            type="button"
            disabled={acting}
            onClick={() =>
              runAction(
                () =>
                  registerInspectionAbnormal(selectedInspection.id, {
                    tenantId: user.tenantId,
                    abnormalDesc,
                    photoUrls: 'mobile://photo/demo.jpg',
                    createHazard: true,
                    severity: 'GENERAL',
                    routePointId: 1
                  }),
                '异常已登记并转隐患'
              )
            }
          >
            登记异常并转隐患
          </button>
          <button
            type="button"
            className="secondary"
            disabled={acting}
            onClick={() =>
              runAction(
                () =>
                  syncInspectionDraft({
                    tenantId: user.tenantId,
                    taskId: selectedInspection.id,
                    clientDraftId: `task-${selectedInspection.id}`,
                    payloadJson: JSON.stringify({ abnormalDesc, savedAt: Date.now() })
                  }),
                '离线草稿已同步'
              )
            }
          >
            同步离线草稿
          </button>
        </div>
      </main>
    );
  }

  if (screen === 'inspection') {
    return (
      <main className="mobile-shell">
        <header>
          <h1>智能巡检</h1>
          <button type="button" className="secondary" onClick={() => { clearTokens(); setUser(null); }}>
            退出
          </button>
        </header>
        {tabBar}
        {error && <p className="error">{error}</p>}
        {message && <p className="message">{message}</p>}
        <ul className="task-list">
          {inspectionTasks.length === 0 && <li>暂无巡检任务</li>}
          {inspectionTasks.map((task) => (
            <li key={task.id} onClick={() => openInspection(task)}>
              <strong>{task.taskNo || `任务#${task.id}`}</strong>
              <span>{task.status}</span>
              <span>{task.scheduledStart || ''}</span>
            </li>
          ))}
        </ul>
      </main>
    );
  }

  if (screen === 'hazard') {
    return (
      <main className="mobile-shell">
        <header>
          <h1>隐患上报</h1>
          <button type="button" className="secondary" onClick={() => { clearTokens(); setUser(null); }}>
            退出
          </button>
        </header>
        {tabBar}
        {error && <p className="error">{error}</p>}
        {message && <p className="message">{message}</p>}
        <div className="detail-card">
          <label>
            等级
            <select value={hazardLevel} onChange={(e) => setHazardLevel(e.target.value)}>
              <option value="GENERAL">一般</option>
              <option value="MAJOR">较大</option>
              <option value="CRITICAL">重大</option>
            </select>
          </label>
          <label>
            描述
            <textarea value={hazardDesc} onChange={(e) => setHazardDesc(e.target.value)} rows={4} />
          </label>
          <button
            type="button"
            disabled={acting}
            onClick={() =>
              runAction(
                () =>
                  createHazard({
                    tenantId: user.tenantId,
                    hazardLevel,
                    sourceType: 'MOBILE',
                    description: hazardDesc,
                    areaId: 1
                  }),
                '隐患已上报'
              )
            }
          >
            提交隐患
          </button>
        </div>
      </main>
    );
  }

  return (
    <main className="mobile-shell">
      <header>
        <h1>待办</h1>
        <button type="button" className="secondary" onClick={() => { clearTokens(); setUser(null); }}>
          退出
        </button>
      </header>
      {tabBar}
      {error && <p className="error">{error}</p>}
      {message && <p className="message">{message}</p>}
      <button
        type="button"
        className="secondary"
        disabled={acting}
        onClick={() =>
          runAction(
            () =>
              syncMobileDraft({
                tenantId: user.tenantId,
                userId: user.id,
                draftType: 'MONITOR',
                payloadJson: JSON.stringify({ content: monitorText, savedAt: Date.now() })
              }),
            '弱网草稿已同步'
          )
        }
      >
        同步监护草稿
      </button>
      <ul className="task-list">
        {tasks.length === 0 && <li>暂无待办</li>}
        {tasks.map((task, index) => (
          <li key={`${task.taskType}-${task.bizId}-${index}`} onClick={() => openTask(task)}>
            <strong>{task.title}</strong>
            <span>{task.taskType}</span>
            {task.status && <span>{task.status}</span>}
          </li>
        ))}
      </ul>
    </main>
  );
}

createRoot(document.getElementById('root') as HTMLElement).render(<App />);
