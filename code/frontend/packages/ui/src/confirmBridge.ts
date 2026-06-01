import type { ConfirmOptions } from './providers/ConfirmProvider';

type ConfirmFn = (options: ConfirmOptions) => Promise<boolean>;

let confirmImpl: ConfirmFn | null = null;

export function setConfirmImplementation(fn: ConfirmFn | null) {
  confirmImpl = fn;
}

/** 全局确认弹窗；Provider 未挂载时回退到 window.confirm。 */
export async function showConfirm(options: ConfirmOptions): Promise<boolean> {
  if (confirmImpl) {
    return confirmImpl(options);
  }
  const text = options.title ? `${options.title}\n\n${options.message}` : options.message;
  return window.confirm(text);
}
