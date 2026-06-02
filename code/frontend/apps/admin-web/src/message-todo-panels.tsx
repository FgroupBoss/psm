import React from 'react';
import {
  fetchNotificationChannelStatus,
  fetchNotificationDetail,
  fetchNotificationInbox,
  fetchNotificationUnreadCount,
  fetchWorkbenchTodoCount,
  fetchWorkbenchTodos,
  markAllNotificationsRead,
  markNotificationRead
} from '@psm/api-client';
import type {
  AuthUser,
  MobileTaskRecord,
  NotificationChannelStatusRecord,
  NotificationMessageRecord,
  PageResult
} from '@psm/domain-types';
import { errorMessage, formatTime } from './ui-helpers';

type InboxFilter = 'all' | 'unread' | 'read';

export function NotificationInboxPanel({ user }: { user: AuthUser }) {
  const [filter, setFilter] = React.useState<InboxFilter>('all');
  const [bizType, setBizType] = React.useState<string>('');
  const [page, setPage] = React.useState<PageResult<NotificationMessageRecord> | null>(null);
  const [unread, setUnread] = React.useState<number>(0);
  const [channels, setChannels] = React.useState<NotificationChannelStatusRecord[]>([]);
  const [selected, setSelected] = React.useState<NotificationMessageRecord | null>(null);
  const [loading, setLoading] = React.useState(true);
  const [error, setError] = React.useState('');
  const [message, setMessage] = React.useState('');

  const load = React.useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const readParam = filter === 'all' ? undefined : filter === 'read';
      const [inbox, count, status] = await Promise.all([
        fetchNotificationInbox(user.tenantId, user.id, {
          read: readParam,
          bizType: bizType || undefined,
          pageNo: 1,
          pageSize: 50
        }),
        fetchNotificationUnreadCount(user.tenantId, user.id),
        fetchNotificationChannelStatus()
      ]);
      setPage(inbox);
      setUnread(count.unreadCount);
      setChannels(status);
    } catch (err: unknown) {
      setError(errorMessage(err, '消息加载失败'));
    } finally {
      setLoading(false);
    }
  }, [user.tenantId, user.id, filter, bizType]);

  React.useEffect(() => {
    load();
  }, [load]);

  async function openDetail(item: NotificationMessageRecord) {
    try {
      const detail = await fetchNotificationDetail(item.id, user.tenantId, user.id);
      setSelected(detail);
      if (!detail.read) {
        await markNotificationRead(item.id, user.tenantId, user.id);
        setUnread((prev) => Math.max(0, prev - 1));
        load();
      }
    } catch (err: unknown) {
      setError(errorMessage(err, '打开消息失败'));
    }
  }

  async function handleMarkAllRead() {
    try {
      const updated = await markAllNotificationsRead(user.tenantId, user.id);
      setMessage(`已标记 ${updated} 条为已读`);
      setUnread(0);
      load();
    } catch (err: unknown) {
      setError(errorMessage(err, '全部已读失败'));
    }
  }

  return (
    <div className="panel-stack">
      <div className="toolbar">
        <span className="badge">未读 {unread}</span>
        <select value={filter} onChange={(e) => setFilter(e.target.value as InboxFilter)} aria-label="已读筛选">
          <option value="all">全部</option>
          <option value="unread">未读</option>
          <option value="read">已读</option>
        </select>
        <select value={bizType} onChange={(e) => setBizType(e.target.value)} aria-label="业务类型">
          <option value="">全部业务</option>
          <option value="WORK_PERMIT">作业票</option>
          <option value="ALARM">报警</option>
          <option value="CONTRACTOR_CERT">承包商证书</option>
        </select>
        <button type="button" className="secondary" onClick={load}>
          刷新
        </button>
        <button type="button" className="primary" onClick={handleMarkAllRead}>
          全部已读
        </button>
      </div>
      {message && <div className="hint success">{message}</div>}
      {error && <div className="hint error">{error}</div>}
      <div className="channel-status-row">
        {channels.map((ch) => (
          <span key={ch.channel} className="meta-chip">
            {ch.channel}: {ch.enabled ? (ch.configured ? 'HTTP已配置' : ch.mode) : '关闭'}
          </span>
        ))}
      </div>
      <div className="split-layout">
        <div className="table-wrap">
          <table className="data-table">
            <thead>
              <tr>
                <th>状态</th>
                <th>标题</th>
                <th>业务</th>
                <th>时间</th>
              </tr>
            </thead>
            <tbody>
              {loading && (
                <tr>
                  <td colSpan={4}>加载中…</td>
                </tr>
              )}
              {!loading && (page?.records.length || 0) === 0 && (
                <tr>
                  <td colSpan={4}>暂无消息</td>
                </tr>
              )}
              {page?.records.map((item) => (
                <tr
                  key={item.id}
                  className={selected?.id === item.id ? 'row-active' : ''}
                  onClick={() => openDetail(item)}
                  style={{ cursor: 'pointer' }}
                >
                  <td>
                    <span className={`status-tag ${item.read ? 'enabled' : 'disabled'}`}>{item.read ? '已读' : '未读'}</span>
                  </td>
                  <td>{item.title}</td>
                  <td>{item.bizType || '—'}</td>
                  <td>{formatTime(item.createdAt)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        <div className="detail-card">
          {selected ? (
            <>
              <h3>{selected.title}</h3>
              <p className="meta">{formatTime(selected.createdAt)}</p>
              <p style={{ whiteSpace: 'pre-wrap' }}>{selected.content}</p>
              {selected.bizType && selected.bizId != null && (
                <p className="meta">
                  关联：{selected.bizType} #{selected.bizId}
                </p>
              )}
            </>
          ) : (
            <p className="meta">选择一条消息查看详情</p>
          )}
        </div>
      </div>
    </div>
  );
}

export function WorkbenchTodosPanel({ user }: { user: AuthUser }) {
  const [tasks, setTasks] = React.useState<MobileTaskRecord[]>([]);
  const [taskType, setTaskType] = React.useState<string>('');
  const [counts, setCounts] = React.useState({ total: 0, sitePermit: 0, monitor: 0, acceptance: 0, alarm: 0 });
  const [loading, setLoading] = React.useState(true);
  const [error, setError] = React.useState('');

  const load = React.useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const [list, count] = await Promise.all([
        fetchWorkbenchTodos(user.tenantId, user.id, 'ALL', taskType || undefined),
        fetchWorkbenchTodoCount(user.tenantId, user.id, 'ALL')
      ]);
      setTasks(list);
      setCounts(count);
    } catch (err: unknown) {
      setError(errorMessage(err, '待办加载失败'));
    } finally {
      setLoading(false);
    }
  }, [user.tenantId, user.id, taskType]);

  React.useEffect(() => {
    load();
  }, [load]);

  return (
    <div className="panel-stack">
      <div className="toolbar">
        <span className="badge">合计 {counts.total}</span>
        <span className="meta-chip">许可 {counts.sitePermit}</span>
        <span className="meta-chip">监护 {counts.monitor}</span>
        <span className="meta-chip">验收 {counts.acceptance}</span>
        <span className="meta-chip">报警 {counts.alarm}</span>
        <select value={taskType} onChange={(e) => setTaskType(e.target.value)} aria-label="待办类型">
          <option value="">全部类型</option>
          <option value="SITE_PERMIT">待现场许可</option>
          <option value="MONITOR">待监护</option>
          <option value="ACCEPTANCE">待验收</option>
          <option value="ALARM_FEEDBACK">报警处置</option>
        </select>
        <button type="button" className="secondary" onClick={load}>
          刷新
        </button>
      </div>
      {error && <div className="hint error">{error}</div>}
      <div className="table-wrap">
        <table className="data-table">
          <thead>
            <tr>
              <th>类型</th>
              <th>标题</th>
              <th>状态</th>
              <th>提示</th>
              <th>截止</th>
            </tr>
          </thead>
          <tbody>
            {loading && (
              <tr>
                <td colSpan={5}>加载中…</td>
              </tr>
            )}
            {!loading && tasks.length === 0 && (
              <tr>
                <td colSpan={5}>暂无待办</td>
              </tr>
            )}
            {tasks.map((task, index) => (
              <tr key={`${task.taskType}-${task.bizId}-${index}`}>
                <td>{task.taskType}</td>
                <td>{task.title}</td>
                <td>{task.status || '—'}</td>
                <td>{task.actionHint || '—'}</td>
                <td>{formatTime(task.dueAt)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
