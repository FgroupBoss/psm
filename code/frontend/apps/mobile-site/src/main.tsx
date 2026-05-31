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
import { ThemeProvider, ThemeToggle } from '@psm/ui';
import './styles.css';

type Tab = 'home' | 'search' | 'notifications' | 'profile';
type Screen = 'home' | 'tasks' | 'inspection' | 'hazard' | 'permit' | 'alarm' | 'inspection-detail';

function formatDate(): string {
  const now = new Date();
  const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
  return `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日 ${weekdays[now.getDay()]}`;
}

function formatTime(): string {
  const now = new Date();
  return `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`;
}

function userInitials(user: AuthUser): string {
  const name = user.displayName || user.username || '?';
  return name.slice(0, 1).toUpperCase();
}

function StatusBar() {
  const [time, setTime] = React.useState(formatTime);

  React.useEffect(() => {
    const timer = window.setInterval(() => setTime(formatTime()), 30000);
    return () => window.clearInterval(timer);
  }, []);

  return (
    <div className="status-bar">
      <span className="status-bar__time">{time}</span>
      <div className="status-bar__icons">
        <i className="fa-solid fa-signal" />
        <i className="fa-solid fa-wifi" />
        <i className="fa-solid fa-battery-three-quarters" />
      </div>
    </div>
  );
}

function BottomNav({ active, onChange, hasNotifications }: { active: Tab; onChange: (tab: Tab) => void; hasNotifications: boolean }) {
  const items: { id: Tab; label: string; icon: string }[] = [
    { id: 'home', label: '首页', icon: 'fa-house' },
    { id: 'search', label: '搜索', icon: 'fa-magnifying-glass' },
    { id: 'notifications', label: '通知', icon: 'fa-bell' },
    { id: 'profile', label: '我的', icon: 'fa-user' }
  ];

  return (
    <nav className="bottom-nav">
      {items.map((item) => (
        <button
          key={item.id}
          type="button"
          className={`bottom-nav__item${active === item.id ? ' active' : ''}${item.id === 'notifications' && hasNotifications ? ' bottom-nav__badge' : ''}`}
          onClick={() => onChange(item.id)}
        >
          <i className={`fa-solid ${item.icon}`} />
          <span>{item.label}</span>
        </button>
      ))}
    </nav>
  );
}

function PhoneShell({
  children,
  showNav,
  bottomTab,
  onTabChange,
  hasNotifications
}: {
  children: React.ReactNode;
  showNav: boolean;
  bottomTab: Tab;
  onTabChange: (tab: Tab) => void;
  hasNotifications: boolean;
}) {
  return (
    <div className="app-viewport">
      <div className="phone-frame">
        <StatusBar />
        <div className="phone-content">{children}</div>
        {showNav && <BottomNav active={bottomTab} onChange={onTabChange} hasNotifications={hasNotifications} />}
      </div>
    </div>
  );
}

function WelcomeSection({ user }: { user: AuthUser }) {
  return (
    <section className="welcome-section">
      <div className="welcome-avatar">{userInitials(user)}</div>
      <div className="welcome-text">
        <h2>你好，{user.displayName || user.username}</h2>
        <p>{formatDate()}</p>
      </div>
    </section>
  );
}

function Alerts({ error, message }: { error: string; message: string }) {
  return (
    <>
      {error && (
        <div className="alert alert--error">
          <i className="fa-solid fa-circle-exclamation" /> {error}
        </div>
      )}
      {message && (
        <div className="alert alert--success">
          <i className="fa-solid fa-circle-check" /> {message}
        </div>
      )}
    </>
  );
}

function App() {
  const [user, setUser] = React.useState<AuthUser | null>(null);
  const [bottomTab, setBottomTab] = React.useState<Tab>('home');
  const [screen, setScreen] = React.useState<Screen>('home');
  const [tasks, setTasks] = React.useState<MobileTaskRecord[]>([]);
  const [inspectionTasks, setInspectionTasks] = React.useState<InspectionTaskRecord[]>([]);
  const [selectedTask, setSelectedTask] = React.useState<MobileTaskRecord | null>(null);
  const [selectedInspection, setSelectedInspection] = React.useState<InspectionTaskRecord | null>(null);
  const [permitDetail, setPermitDetail] = React.useState<WorkPermitDetailRecord | null>(null);
  const [error, setError] = React.useState('');
  const [message, setMessage] = React.useState('');
  const [acting, setActing] = React.useState(false);
  const [searchQuery, setSearchQuery] = React.useState('');
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

  const alarmTasks = tasks.filter((t) => t.taskType === 'ALARM');
  const hasNotifications = alarmTasks.length > 0;

  async function handleLogin(event: React.FormEvent) {
    event.preventDefault();
    setError('');
    try {
      const result = await login({ tenantId: 1, username: 'admin', password: '' });
      setUser(result.user);
      setScreen('home');
      setBottomTab('home');
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

  function goHome() {
    setScreen('home');
    setBottomTab('home');
    setMessage('');
    setError('');
  }

  function handleBottomTab(tab: Tab) {
    setBottomTab(tab);
    setMessage('');
    setError('');
    if (tab === 'home') setScreen('home');
    if (tab === 'search') setScreen('tasks');
    if (tab === 'notifications') setScreen('tasks');
    if (tab === 'profile') setScreen('home');
  }

  function navigateTo(target: Screen) {
    setScreen(target);
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

  const showNav = !['permit', 'alarm', 'inspection-detail'].includes(screen);
  const shellProps = { showNav, bottomTab, onTabChange: handleBottomTab, hasNotifications };

  if (!user) {
    return (
      <div className="app-viewport app-viewport--auth">
        <ThemeToggle className="theme-toggle--viewport" />
        <PhoneShell {...shellProps} showNav={false} bottomTab="home" onTabChange={() => undefined} hasNotifications={false}>
        <div className="login-hero">
          <div className="login-hero__logo">
            <i className="fa-solid fa-shield-halved" />
          </div>
          <h1>PSM 移动现场</h1>
          <p>作业许可 · 智能巡检 · 隐患上报</p>
        </div>
        <form className="login-form" onSubmit={handleLogin}>
          <button type="submit" className="btn btn-primary">
            <i className="fa-solid fa-arrow-right-to-bracket" /> 使用试点账号登录
          </button>
        </form>
        {error && <div className="alert alert--error">{error}</div>}
        </PhoneShell>
      </div>
    );
  }

  const filteredTasks = tasks.filter((task) => {
    if (!searchQuery.trim()) return true;
    const q = searchQuery.toLowerCase();
    return (
      task.title.toLowerCase().includes(q) ||
      task.taskType.toLowerCase().includes(q) ||
      (task.status || '').toLowerCase().includes(q)
    );
  });

  if (screen === 'alarm' && selectedTask) {
    return (
      <PhoneShell {...shellProps} showNav={false}>
        <button type="button" className="back-link" onClick={() => setScreen('tasks')}>
          <i className="fa-solid fa-chevron-left" /> 返回
        </button>
        <div className="glass-card">
          <h3>
            <i className="fa-solid fa-triangle-exclamation" style={{ color: '#ff9f0a', marginRight: 8 }} />
            报警处置
          </h3>
          <p>{selectedTask.title}</p>
          <button
            type="button"
            className="btn btn-primary"
            disabled={acting}
            onClick={() => runAction(() => mobileAlarmFeedback(selectedTask.bizId, user.tenantId, '移动端现场反馈'), '报警反馈已提交')}
          >
            提交处置反馈
          </button>
        </div>
        <Alerts error={error} message={message} />
      </PhoneShell>
    );
  }

  if (screen === 'permit') {
    return (
      <PhoneShell {...shellProps} showNav={false}>
        <button type="button" className="back-link" onClick={() => setScreen('tasks')}>
          <i className="fa-solid fa-chevron-left" /> 返回待办
        </button>
        {permitDetail && (
          <div className="glass-card">
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
        <Alerts error={error} message={message} />

        <div className="glass-card">
          <h4>现场签到</h4>
          <button
            type="button"
            className="btn btn-primary"
            disabled={acting}
            onClick={() =>
              runAction(
                () =>
                  mobileCheckIn(selectedTask!.bizId, {
                    tenantId: user.tenantId,
                    locationText: '移动端GPS',
                    scanCode: 'MOBILE-1'
                  }),
                '签到完成'
              )
            }
          >
            <i className="fa-solid fa-location-dot" /> 现场签到
          </button>
        </div>

        <div className="glass-card">
          <h4>气体检测</h4>
          <div className="form-field">
            <input value={gasName} onChange={(e) => setGasName(e.target.value)} placeholder="气体名称" />
          </div>
          <div className="form-field">
            <select value={gasQualified ? '1' : '0'} onChange={(e) => setGasQualified(e.target.value === '1')}>
              <option value="1">合格</option>
              <option value="0">不合格</option>
            </select>
          </div>
          <button
            type="button"
            className="btn btn-primary"
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

        <div className="glass-card">
          <h4>措施确认</h4>
          <div className="form-field">
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
          </div>
          <button
            type="button"
            className="btn btn-primary"
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

        <div className="glass-card">
          <h4>附件拍照</h4>
          <input type="file" accept="image/*" capture="environment" onChange={handlePhotoUpload} />
          {uploadedFileName && <p className="alert alert--success">最近上传：{uploadedFileName}</p>}
        </div>

        {timeline.length > 0 && (
          <div className="glass-card">
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

        <div className="glass-card">
          <h4>许可开工</h4>
          <div className="form-field">
            <input value={signature} onChange={(e) => setSignature(e.target.value)} placeholder="电子签名" />
          </div>
          <button
            type="button"
            className="btn btn-primary"
            disabled={acting}
            onClick={() =>
              runAction(
                () =>
                  mobileSitePermit(selectedTask!.bizId, {
                    tenantId: user.tenantId,
                    signatureText: signature,
                    locationText: '作业点'
                  }),
                '许可开工成功'
              )
            }
          >
            <i className="fa-solid fa-signature" /> 电子签名许可
          </button>
        </div>

        <div className="glass-card">
          <h4>监护 / 暂停 / 验收</h4>
          <div className="form-field">
            <input value={monitorText} onChange={(e) => setMonitorText(e.target.value)} />
          </div>
          <div className="action-grid">
            <button
              type="button"
              className="btn btn-primary btn-sm"
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
            <button
              type="button"
              className="btn btn-secondary btn-sm"
              disabled={acting}
              onClick={() => runAction(() => mobileSuspend(selectedTask!.bizId, user.tenantId, '异常暂停'), '已暂停')}
            >
              暂停作业
            </button>
            <button
              type="button"
              className="btn btn-secondary btn-sm"
              disabled={acting}
              onClick={() => runAction(() => mobileResume(selectedTask!.bizId, user.tenantId, '恢复作业'), '已恢复')}
            >
              恢复作业
            </button>
            <button
              type="button"
              className="btn btn-primary btn-sm"
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
      </PhoneShell>
    );
  }

  if (screen === 'inspection-detail' && selectedInspection) {
    return (
      <PhoneShell {...shellProps} showNav={false}>
        <button type="button" className="back-link" onClick={() => setScreen('inspection')}>
          <i className="fa-solid fa-chevron-left" /> 返回巡检
        </button>
        <div className="glass-card">
          <h3>{selectedInspection.taskNo || selectedInspection.id}</h3>
          <p>
            状态 {selectedInspection.status} · 异常 {selectedInspection.abnormalCount ?? 0}
          </p>
        </div>
        <Alerts error={error} message={message} />
        <div className="glass-card action-grid">
          <button
            type="button"
            className="btn btn-primary btn-sm"
            disabled={acting}
            onClick={() =>
              runAction(() => startInspectionTask(selectedInspection.id, { tenantId: user.tenantId, executorId: user.id }), '已开始')
            }
          >
            开始任务
          </button>
          <button
            type="button"
            className="btn btn-primary btn-sm"
            disabled={acting}
            onClick={() =>
              runAction(
                () =>
                  signInInspectionTask(selectedInspection.id, {
                    tenantId: user.tenantId,
                    routePointId: 1,
                    signType: 'QR',
                    signCode: 'POINT-1'
                  }),
                '签到成功'
              )
            }
          >
            扫码签到
          </button>
          <button
            type="button"
            className="btn btn-primary btn-sm"
            disabled={acting}
            onClick={() => runAction(() => completeInspectionTask(selectedInspection.id, user.tenantId), '任务完成')}
          >
            完成任务
          </button>
        </div>
        <div className="glass-card">
          <h4>异常登记</h4>
          <div className="form-field">
            <input value={abnormalDesc} onChange={(e) => setAbnormalDesc(e.target.value)} />
          </div>
          <div className="action-grid">
            <button
              type="button"
              className="btn btn-primary btn-sm"
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
              className="btn btn-secondary btn-sm"
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
        </div>
      </PhoneShell>
    );
  }

  if (screen === 'inspection') {
    return (
      <PhoneShell {...shellProps}>
        <div className="page-header">
          <h1>智能巡检</h1>
          <button type="button" className="btn btn-ghost btn-sm" onClick={goHome}>
            <i className="fa-solid fa-house" />
          </button>
        </div>
        <Alerts error={error} message={message} />
        <ul className="task-list">
          {inspectionTasks.length === 0 && <li className="empty-item">暂无巡检任务</li>}
          {inspectionTasks.map((task) => (
            <li key={task.id} onClick={() => openInspection(task)}>
              <strong>{task.taskNo || `任务#${task.id}`}</strong>
              <span>{task.status}</span>
              <span>{task.scheduledStart || ''}</span>
            </li>
          ))}
        </ul>
      </PhoneShell>
    );
  }

  if (screen === 'hazard') {
    return (
      <PhoneShell {...shellProps}>
        <div className="page-header">
          <h1>隐患上报</h1>
          <button type="button" className="btn btn-ghost btn-sm" onClick={goHome}>
            <i className="fa-solid fa-house" />
          </button>
        </div>
        <Alerts error={error} message={message} />
        <div className="glass-card">
          <div className="form-field">
            <label>等级</label>
            <select value={hazardLevel} onChange={(e) => setHazardLevel(e.target.value)}>
              <option value="GENERAL">一般</option>
              <option value="MAJOR">较大</option>
              <option value="CRITICAL">重大</option>
            </select>
          </div>
          <div className="form-field">
            <label>描述</label>
            <textarea value={hazardDesc} onChange={(e) => setHazardDesc(e.target.value)} rows={4} />
          </div>
          <button
            type="button"
            className="btn btn-primary"
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
            <i className="fa-solid fa-paper-plane" /> 提交隐患
          </button>
        </div>
      </PhoneShell>
    );
  }

  if (bottomTab === 'profile' && screen === 'home') {
    return (
      <PhoneShell {...shellProps}>
        <WelcomeSection user={user} />
        <div className="glass-card profile-card">
          <div className="welcome-avatar">{userInitials(user)}</div>
          <h2>{user.displayName || user.username}</h2>
          <p>租户 #{user.tenantId}</p>
          <div className="profile-stats">
            <div className="profile-stat">
              <strong>{tasks.length}</strong>
              <span>待办</span>
            </div>
            <div className="profile-stat">
              <strong>{inspectionTasks.length}</strong>
              <span>巡检</span>
            </div>
            <div className="profile-stat">
              <strong>{alarmTasks.length}</strong>
              <span>报警</span>
            </div>
          </div>
          <div className="profile-theme">
            <span>外观模式</span>
            <ThemeToggle className="theme-toggle--labeled" showLabel />
          </div>
          <button
            type="button"
            className="btn btn-secondary"
            onClick={() => {
              clearTokens();
              setUser(null);
            }}
          >
            <i className="fa-solid fa-right-from-bracket" /> 退出登录
          </button>
        </div>
      </PhoneShell>
    );
  }

  if (bottomTab === 'notifications') {
    return (
      <PhoneShell {...shellProps}>
        <div className="page-header">
          <h1>通知</h1>
        </div>
        <Alerts error={error} message={message} />
        {alarmTasks.length === 0 && (
          <div className="glass-card">
            <p style={{ textAlign: 'center', margin: 0 }}>暂无新通知</p>
          </div>
        )}
        {alarmTasks.map((task, index) => (
          <div key={`alarm-${task.bizId}-${index}`} className="notif-item" onClick={() => openTask(task)}>
            <div className="notif-item__dot" />
            <div className="notif-item__body">
              <strong>{task.title}</strong>
              <span>{task.taskType} · {task.status || '待处理'}</span>
            </div>
          </div>
        ))}
        {tasks.filter((t) => t.taskType !== 'ALARM').slice(0, 5).map((task, index) => (
          <div key={`todo-${task.bizId}-${index}`} className="notif-item" onClick={() => openTask(task)}>
            <div className="notif-item__body">
              <strong>{task.title}</strong>
              <span>{task.taskType} · {task.status || '待办'}</span>
            </div>
          </div>
        ))}
      </PhoneShell>
    );
  }

  if (bottomTab === 'search' || screen === 'tasks') {
    return (
      <PhoneShell {...shellProps}>
        <div className="page-header">
          <h1>{bottomTab === 'search' ? '搜索' : '待办'}</h1>
          {screen === 'tasks' && bottomTab !== 'search' && (
            <button type="button" className="btn btn-ghost btn-sm" onClick={goHome}>
              <i className="fa-solid fa-house" />
            </button>
          )}
        </div>
        {bottomTab === 'search' && (
          <div className="search-box">
            <i className="fa-solid fa-magnifying-glass" />
            <input
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="搜索待办、作业类型..."
            />
          </div>
        )}
        <Alerts error={error} message={message} />
        {screen === 'tasks' && bottomTab !== 'search' && (
          <button
            type="button"
            className="btn btn-secondary btn-sm"
            style={{ marginBottom: 12, width: '100%' }}
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
            <i className="fa-solid fa-cloud-arrow-up" /> 同步监护草稿
          </button>
        )}
        <ul className="task-list">
          {filteredTasks.length === 0 && <li className="empty-item">暂无待办</li>}
          {filteredTasks.map((task, index) => (
            <li key={`${task.taskType}-${task.bizId}-${index}`} onClick={() => openTask(task)}>
              <strong>{task.title}</strong>
              <span>{task.taskType}</span>
              {task.status && <span>{task.status}</span>}
            </li>
          ))}
        </ul>
      </PhoneShell>
    );
  }

  return (
    <PhoneShell {...shellProps}>
      <WelcomeSection user={user} />
      <p className="section-title">主要功能</p>
      <div className="feature-grid">
        <button type="button" className="feature-card stagger-1" onClick={() => navigateTo('tasks')}>
          <div className="feature-card__icon">
            <i className="fa-solid fa-clipboard-list" />
          </div>
          <h3>待办作业</h3>
          <p>{tasks.length} 项待处理</p>
        </button>
        <button type="button" className="feature-card stagger-2" onClick={() => navigateTo('inspection')}>
          <div className="feature-card__icon feature-card__icon--green">
            <i className="fa-solid fa-route" />
          </div>
          <h3>智能巡检</h3>
          <p>{inspectionTasks.length} 个巡检任务</p>
        </button>
        <button type="button" className="feature-card stagger-3" onClick={() => navigateTo('hazard')}>
          <div className="feature-card__icon feature-card__icon--purple">
            <i className="fa-solid fa-triangle-exclamation" />
          </div>
          <h3>隐患上报</h3>
          <p>现场快速登记</p>
        </button>
        <button
          type="button"
          className="feature-card stagger-4"
          onClick={() => {
            setBottomTab('notifications');
            setScreen('tasks');
          }}
        >
          <div className="feature-card__icon feature-card__icon--orange">
            <i className="fa-solid fa-bell" />
          </div>
          <h3>报警通知</h3>
          <p>{alarmTasks.length} 条待处置</p>
        </button>
      </div>

      {tasks.length > 0 && (
        <>
          <p className="section-title">最近待办</p>
          <ul className="task-list">
            {tasks.slice(0, 3).map((task, index) => (
              <li key={`recent-${task.taskType}-${task.bizId}-${index}`} onClick={() => openTask(task)}>
                <strong>{task.title}</strong>
                <span>{task.taskType}</span>
              </li>
            ))}
          </ul>
        </>
      )}
      <Alerts error={error} message={message} />
    </PhoneShell>
  );
}

createRoot(document.getElementById('root') as HTMLElement).render(
  <ThemeProvider>
    <App />
  </ThemeProvider>
);
