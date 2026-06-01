import React from 'react';

export interface TableRowActionsProps {
  children: React.ReactNode;
}

/** 行内操作按钮容器：保证 44px 点击区域与 Tab 聚焦顺序。 */
export function TableRowActions({ children }: TableRowActionsProps) {
  return <div className="psm-table-actions actions">{children}</div>;
}

export interface TableActionButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'danger' | 'warning';
}

/** 列表行内操作按钮（最小 44×44px 触达区域）。 */
export function TableActionButton({ variant = 'secondary', className = '', children, ...rest }: TableActionButtonProps) {
  const variantClass =
    variant === 'danger'
      ? 'psm-btn--danger danger'
      : variant === 'warning'
        ? 'psm-btn--warning warning'
        : variant === 'primary'
          ? ''
          : 'psm-btn--secondary secondary';
  return (
    <button type="button" className={`psm-btn psm-table-action-btn ${variantClass} ${className}`.trim()} {...rest}>
      {children}
    </button>
  );
}
