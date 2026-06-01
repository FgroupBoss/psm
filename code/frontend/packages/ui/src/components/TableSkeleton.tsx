import React from 'react';

export interface TableSkeletonProps {
  /** 占位行数，默认 4 */
  rows?: number;
  /** 数据列数（不含多选与操作列） */
  columns?: number;
  selectable?: boolean;
  hasActions?: boolean;
}

/** 列表加载骨架屏（3~5 行占位）。 */
export function TableSkeleton({ rows = 4, columns = 4, selectable, hasActions }: TableSkeletonProps) {
  const rowCount = Math.min(5, Math.max(3, rows));
  return (
    <div className="psm-table-skeleton" role="status" aria-live="polite" aria-busy="true" aria-label="正在加载列表">
      {Array.from({ length: rowCount }).map((_, rowIndex) => (
        <div key={rowIndex} className="psm-table-skeleton__row">
          {selectable && <div className="psm-table-skeleton__cell psm-table-skeleton__cell--check" />}
          {Array.from({ length: columns }).map((_, colIndex) => (
            <div
              key={colIndex}
              className="psm-table-skeleton__cell"
              style={{ flex: colIndex === 0 ? '1.4 1 120px' : '1 1 80px' }}
            />
          ))}
          {hasActions && <div className="psm-table-skeleton__cell psm-table-skeleton__cell--actions" />}
        </div>
      ))}
    </div>
  );
}
