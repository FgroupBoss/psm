import React from 'react';
import { TableSkeleton } from './TableSkeleton';

export interface ListTableFrameProps {
  loading?: boolean;
  /** 骨架屏数据列数 */
  skeletonColumns?: number;
  skeletonRows?: number;
  selectable?: boolean;
  hasActions?: boolean;
  children: React.ReactNode;
}

/** 为原生 table 提供横向滚动容器与加载骨架屏。 */
export function ListTableFrame({
  loading,
  skeletonColumns = 4,
  skeletonRows = 4,
  selectable,
  hasActions,
  children
}: ListTableFrameProps) {
  return (
    <div className="psm-table-scroll">
      {loading ? (
        <TableSkeleton
          rows={skeletonRows}
          columns={skeletonColumns}
          selectable={selectable}
          hasActions={hasActions}
        />
      ) : (
        children
      )}
    </div>
  );
}
