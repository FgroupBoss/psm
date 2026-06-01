import React from 'react';

export interface TableEmptyStateProps {
  message?: string;
  /** 有活跃筛选时显示「重置筛选」 */
  hasActiveFilters?: boolean;
  onClearFilters?: () => void;
  clearFiltersLabel?: string;
}

function EmptyIllustration() {
  return (
    <svg
      className="psm-table-empty__art"
      width="120"
      height="96"
      viewBox="0 0 120 96"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      aria-hidden="true"
      focusable="false"
    >
      <rect x="16" y="20" width="88" height="56" rx="12" fill="currentColor" opacity="0.08" />
      <rect x="28" y="34" width="48" height="6" rx="3" fill="currentColor" opacity="0.15" />
      <rect x="28" y="48" width="64" height="6" rx="3" fill="currentColor" opacity="0.1" />
      <rect x="28" y="62" width="40" height="6" rx="3" fill="currentColor" opacity="0.1" />
      <circle cx="88" cy="68" r="14" fill="currentColor" opacity="0.12" />
      <path
        d="M82 68h12M88 62v12"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        opacity="0.35"
      />
    </svg>
  );
}

/** 列表空状态：插画、文案、可选重置筛选。 */
export function TableEmptyState({
  message = '暂无数据',
  hasActiveFilters,
  onClearFilters,
  clearFiltersLabel = '重置筛选'
}: TableEmptyStateProps) {
  return (
    <div className="psm-table-empty" role="status">
      <EmptyIllustration />
      <p className="psm-table-empty__message">{message}</p>
      {hasActiveFilters && onClearFilters && (
        <button type="button" className="psm-btn psm-btn--secondary" onClick={onClearFilters}>
          {clearFiltersLabel}
        </button>
      )}
    </div>
  );
}
