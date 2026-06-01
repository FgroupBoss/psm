import React from 'react';
import { useFocusTrap } from '../hooks/useFocusTrap';
import { useIdPrefix } from '../hooks/useIdPrefix';

interface ModalDialogProps {
  open: boolean;
  title: string;
  onClose: () => void;
  children: React.ReactNode;
  footer?: React.ReactNode;
  wide?: boolean;
  /** 表单场景传 form 以绑定 submit */
  formId?: string;
}

/** 可访问模态框：focus trap、Esc 关闭、aria 语义。 */
export function ModalDialog({ open, title, onClose, children, footer, wide, formId }: ModalDialogProps) {
  const dialogRef = React.useRef<HTMLDivElement>(null);
  const titleId = useIdPrefix('modal-title');

  useFocusTrap(dialogRef, open, onClose);

  if (!open) {
    return null;
  }

  return (
    <div className="psm-modal-backdrop" onClick={onClose}>
      <div
        ref={dialogRef}
        className={`psm-modal ${wide ? 'psm-modal--wide' : ''}`}
        role="dialog"
        aria-modal="true"
        aria-labelledby={titleId}
        onClick={(e) => e.stopPropagation()}
      >
        <header>
          <h3 id={titleId}>{title}</h3>
          <button type="button" className="psm-icon-btn" aria-label="关闭对话框" onClick={onClose}>
            ×
          </button>
        </header>
        <div className="psm-modal__body">{children}</div>
        {footer && (
          <footer>
            {formId ? (
              <>
                <button type="button" className="psm-btn psm-btn--secondary" onClick={onClose}>
                  取消
                </button>
                <button type="submit" form={formId} className="psm-btn">
                  保存
                </button>
              </>
            ) : (
              footer
            )}
          </footer>
        )}
      </div>
    </div>
  );
}
