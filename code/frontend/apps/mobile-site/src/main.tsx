import React from 'react';
import { createRoot } from 'react-dom/client';
import { clearTokens, getAccessToken } from '@psm/auth';
import {
  fetchCurrentUser,
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
  syncMobileDraft
} from '@psm/api-client';
import type { AuthUser, MobileTaskRecord, SafetyMeasureRecord, WorkPermitDetailRecord } from '@psm/domain-types';
import './styles.css';

type Screen = 'tasks' | 'permit' | 'alarm';

function App() {
  const [user, setUser] = React.useState<AuthUser | null>(null);
  const [screen, setScreen] = React.useState<Screen>('tasks');
  const [tasks, setTasks] = React.useState<MobileTaskRecord[]>([]);
  const [selectedTask, setSelectedTask] = React.useState<MobileTaskRecord | null>(null);
  const [permitDetail, setPermitDetail] = React.useState<WorkPermitDetailRecord | null>(null);
  const [error, setError] = React.useState('');
  const [message, setMessage] = React.useState('');
  const [acting, setActing] = React.useState(false);
  const [signature, setSignature] = React.useState('移动许可签名');
  const [gasName, setGasName] = React.useState('O2');
  const [gasQualified, setGasQualified] = React.useState(true);
  const [monitorText, setMonitorText] = React.useState('巡检正常');
  const [selectedMeasure, setSelectedMeasure] = React.useState<SafetyMeasureRecord | null>(null);

  async function handleLogin(event: React.FormEvent) {
    event.preventDefault();
    setError('');
    try {
      const result = await login({ tenantId: 1, username: 'admin', password: '' });
      setUser(result.user);
      await loadTasks(result.user);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : '登录失败');
    }
  }

  async function loadTasks(current: AuthUser) {
    const list = await fetchMobileTasks(current.tenantId, current.id, 'ALL');
    setTasks(list);
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
    const detail = await fetchMobileWorkPermitDetail(task.bizId, user!.tenantId);
    setPermitDetail(detail);
    const pending = detail.safetyMeasures.find((m) => m.confirmStatus !== 'CONFIRMED');
    setSelectedMeasure(pending || detail.safetyMeasures[0] || null);
  }

  async function runAction(action: () => Promise<unknown>, success: string) {
    if (!user || !selectedTask) return;
    setActing(true);
    setError('');
    try {
      await action();
      setMessage(success);
      if (screen === 'permit') {
        const detail = await fetchMobileWorkPermitDetail(selectedTask.bizId, user.tenantId);
        setPermitDetail(detail);
      }
      await loadTasks(user);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : '操作失败');
    } finally {
      setActing(false);
    }
  }

  async function handlePhotoUpload(event: React.ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0];
    if (!file || !user || !selectedTask) return;
    await runAction(
      () => mobileUploadFile(user.tenantId, file, 'WORK_PERMIT', selectedTask.bizId),
      `附件已上传：${file.name}`
    );
  }

  React.useEffect(() => {
    if (!getAccessToken()) return;
    fetchCurrentUser()
      .then((current) => {
        setUser(current);
        return loadTasks(current);
      })
      .catch(() => clearTokens());
  }, []);

  if (!user) {
    return (
      <main className="mobile-shell">
        <h1>PSM 移动现场</h1>
        <p>M07 待办、许可、监护、验收与弱网草稿（经 BFF）。</p>
        <form onSubmit={handleLogin}>
          <button type="submit">使用试点账号登录</button>
        </form>
        {error && <p className="error">{error}</p>}
      </main>
    );
  }

  if (screen === 'tasks') {
    return (
      <main className="mobile-shell">
        <header>
          <h1>待办</h1>
          <button
            type="button"
            className="secondary"
            onClick={() => {
              clearTokens();
              setUser(null);
            }}
          >
            退出
          </button>
        </header>
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

  if (screen === 'alarm' && selectedTask) {
    return (
      <main className="mobile-shell">
        <button type="button" className="back-link" onClick={() => setScreen('tasks')}>
          ← 返回待办
        </button>
        <div className="detail-card">
          <h3>报警处置</h3>
          <p>{selectedTask.title}</p>
          <div className="action-grid">
            <button
              type="button"
              disabled={acting}
              onClick={() =>
                runAction(
                  () => mobileAlarmFeedback(selectedTask.bizId, user.tenantId, '移动端现场反馈'),
                  '报警反馈已提交'
                )
              }
            >
              提交处置反馈
            </button>
          </div>
        </div>
        {error && <p className="error">{error}</p>}
        {message && <p className="message">{message}</p>}
      </main>
    );
  }

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
            人员 {permitDetail.workers.length} · 措施 {permitDetail.safetyMeasures.length} · 气体{' '}
            {permitDetail.gasTests.length}
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
      </div>

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

createRoot(document.getElementById('root') as HTMLElement).render(<App />);
