import React from 'react';

export function StatusTag({ status }: { status: string }) {
  const enabled =
    status === 'ENABLED' ||
    status === 'SUCCESS' ||
    status === 'PUBLISHED' ||
    status === 'PASS' ||
    status === 'APPROVED';
  return <span className={`status-tag ${enabled ? 'enabled' : 'disabled'}`}>{status}</span>;
}

export function confirmAction(message: string, action: () => void) {
  if (window.confirm(message)) {
    action();
  }
}

export function errorMessage(err: unknown, fallback: string) {
  return err instanceof Error ? err.message : fallback;
}

export function formatTime(value?: string) {
  if (!value) {
    return '-';
  }
  return value.replace('T', ' ').slice(0, 19);
}

export function stringifyJson(value: Record<string, unknown>) {
  return JSON.stringify(value, null, 2);
}

export function parseObjectJson(value: string): Record<string, unknown> {
  const trimmed = value.trim();
  if (!trimmed) {
    return {};
  }
  const parsed = JSON.parse(trimmed) as unknown;
  if (!parsed || Array.isArray(parsed) || typeof parsed !== 'object') {
    throw new SyntaxError('JSON 内容必须是对象');
  }
  return parsed as Record<string, unknown>;
}

export function useAsyncAction() {
  const [message, setMessage] = React.useState<string>('');
  const [error, setError] = React.useState<string>('');

  async function run(action: () => Promise<void>, success: string, reload?: () => Promise<void>) {
    setMessage('');
    setError('');
    try {
      await action();
      setMessage(success);
      if (reload) {
        await reload();
      }
    } catch (err: unknown) {
      setError(errorMessage(err, '操作失败'));
    }
  }

  return { message, error, setError, run, setMessage };
}
