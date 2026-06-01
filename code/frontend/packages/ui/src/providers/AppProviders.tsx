import React from 'react';
import { ToastProvider } from './ToastProvider';
import { ConfirmProvider } from './ConfirmProvider';

/** 聚合 Toast 与 Confirm 的全局反馈 Provider。 */
export function AppProviders({ children }: { children: React.ReactNode }) {
  return (
    <ToastProvider>
      <ConfirmProvider>{children}</ConfirmProvider>
    </ToastProvider>
  );
}

export { ToastProvider, useToast } from './ToastProvider';
export { ConfirmProvider, useConfirm } from './ConfirmProvider';
export type { ToastVariant, ToastItem } from './ToastProvider';
export type { ConfirmOptions } from './ConfirmProvider';
