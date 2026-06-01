import React from 'react';
import { TableEmptyState, type TableEmptyStateProps } from './TableEmptyState';

export interface TableEmptyRowProps extends TableEmptyStateProps {
  colSpan: number;
}

/** 表格内空状态行（跨列展示插画与重置筛选）。 */
export function TableEmptyRow({ colSpan, ...emptyProps }: TableEmptyRowProps) {
  return (
    <tr>
      <td colSpan={colSpan} className="psm-table-empty-cell">
        <TableEmptyState {...emptyProps} />
      </td>
    </tr>
  );
}
