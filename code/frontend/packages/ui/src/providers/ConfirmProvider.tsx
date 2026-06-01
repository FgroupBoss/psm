import React from 'react';
import { useFocusTrap } from '../hooks/useFocusTrap';

export interface ConfirmOptions {
  title: string;
  message: string;
  confirmLabel?: string;
  cancelLabel?: string;
  variant?: 'danger' | 'warning' | 'default';
}

interface ConfirmContextValue {
  confirm: (options: ConfirmOptions) => Promise<boolean>;
}

const ConfirmContext = React.createContext<ConfirmContextValue | null>(null);

export function ConfirmProvider({ children }: { children: React.ReactNode }) {
  const [pending, setPending] = React.useState<(ConfirmOptions & { resolve: (v: boolean) => void }) | null>(null);
  const dialogRef = React.useRef<HTMLDivElement>(null);

  useFocusTrap(dialogRef, !!pending, () => {
    pending?.resolve(false);
    setPending(null);
  });

  const confirm = React.useCallback((options: ConfirmOptions) => {
    return new Promise<boolean>((resolve) => {
      setPending({ ...options, resolve });
    });
  }, []);

  function close(result: boolean) {
    pending?.resolve(result);
    setPending(null);
  }

  return (
    <ConfirmContext.Provider value={{ confirm }}>
      {children}
      {pending && (
        <div className="psm-modal-backdrop" onClick={() => close(false)}>
          <div
            ref={dialogRef}
            className="psm-modal psm-confirm"
            role="alertdialog"
            aria-modal="true"
            aria-labelledby="psm-confirm-title"
            aria-describedby="psm-confirm-desc"
            onClick={(e) => e.stopPropagation()}
          >
            <header>
              <h3 id="psm-confirm-title">{pending.title}</h3>
            </header>
            <p id="psm-confirm-desc" className="psm-confirm__message">
              {pending.message}
            </p>
            <footer>
              <button type="button" className="psm-btn psm-btn--secondary" onClick={() => close(false)}>
                {pending.cancelLabel || '取消'}
              </button>
              <button
                type="button"
                className={`psm-btn ${pending.variant === 'danger' ? 'psm-btn--danger' : pending.variant === 'warning' ? 'psm-btn--warning' : ''}`}
                onClick={() => close(true)}
                autoFocus
              >
                {pending.confirmLabel || '确认'}
              </button>
            </footer>
          </div>
        </div>
      )}
    </ConfirmContext.Provider>
  );
}

export function useConfirm() {
  const ctx = React.useContext(ConfirmContext);
  if (!ctx) {
    throw new Error('useConfirm 必须在 ConfirmProvider 内使用');
  }
  return ctx;
}
