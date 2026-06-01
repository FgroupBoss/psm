import React from 'react';

export type SortDirection = 'asc' | 'desc' | 'none';

export interface DataTableColumn<T> {
  key: string;
  label: string;
  sortable?: boolean;
  /** 客户端排序取值 */
  sortValue?: (row: T) => string | number;
  render: (row: T) => React.ReactNode;
  /** 列最小宽度，用于 sticky 布局 */
  minWidth?: number;
}

export interface DataTableProps<T extends { id: number | string }> {
  caption: string;
  columns: Array<DataTableColumn<T>>;
  rows: T[];
  loading?: boolean;
  /** 行级操作列 */
  rowActions?: (row: T) => React.ReactNode;
  /** 多选 */
  selectable?: boolean;
  selectedIds?: Set<string | number>;
  onSelectionChange?: (ids: Set<string | number>) => void;
  /** 批量操作工具栏 */
  batchActions?: React.ReactNode;
  /** 分页 */
  pageNo?: number;
  pageSize?: number;
  total?: number;
  onPageChange?: (page: number) => void;
  /** 空状态 */
  emptyMessage?: string;
  hasActiveFilters?: boolean;
  onClearFilters?: () => void;
  toolbar?: React.ReactNode;
}

function compareValues(a: string | number, b: string | number, dir: SortDirection) {
  if (dir === 'none') {
    return 0;
  }
  const factor = dir === 'asc' ? 1 : -1;
  if (typeof a === 'number' && typeof b === 'number') {
    return (a - b) * factor;
  }
  return String(a).localeCompare(String(b), 'zh-CN') * factor;
}

function nextSort(current: SortDirection, key: string, activeKey: string | null): { key: string; dir: SortDirection } {
  if (activeKey !== key || current === 'none') {
    return { key, dir: 'asc' };
  }
  if (current === 'asc') {
    return { key, dir: 'desc' };
  }
  return { key: '', dir: 'none' };
}

function ariaSort(dir: SortDirection): 'ascending' | 'descending' | 'none' | undefined {
  if (dir === 'asc') return 'ascending';
  if (dir === 'desc') return 'descending';
  return dir === 'none' ? 'none' : undefined;
}

/** WCAG 友好数据表格：排序、分页、多选、固定表头与操作列。 */
export function DataTable<T extends { id: number | string }>(props: DataTableProps<T>) {
  const {
    caption,
    columns,
    rows,
    loading,
    rowActions,
    selectable,
    selectedIds = new Set(),
    onSelectionChange,
    batchActions,
    pageNo = 1,
    pageSize = 10,
    total = rows.length,
    onPageChange,
    emptyMessage = '暂无数据',
    hasActiveFilters,
    onClearFilters,
    toolbar
  } = props;

  const [sortKey, setSortKey] = React.useState<string | null>(null);
  const [sortDir, setSortDir] = React.useState<SortDirection>('none');

  const sortedRows = React.useMemo(() => {
    if (!sortKey || sortDir === 'none') {
      return rows;
    }
    const col = columns.find((c) => c.key === sortKey);
    if (!col?.sortValue) {
      return rows;
    }
    return [...rows].sort((a, b) => compareValues(col.sortValue!(a), col.sortValue!(b), sortDir));
  }, [rows, sortKey, sortDir, columns]);

  const totalPages = Math.max(1, Math.ceil(total / pageSize));
  const colCount = columns.length + (rowActions ? 1 : 0) + (selectable ? 1 : 0);
  const allSelected = sortedRows.length > 0 && sortedRows.every((r) => selectedIds.has(r.id));

  function toggleAll() {
    if (!onSelectionChange) return;
    if (allSelected) {
      onSelectionChange(new Set());
    } else {
      onSelectionChange(new Set(sortedRows.map((r) => r.id)));
    }
  }

  function toggleRow(id: string | number) {
    if (!onSelectionChange) return;
    const next = new Set(selectedIds);
    if (next.has(id)) {
      next.delete(id);
    } else {
      next.add(id);
    }
    onSelectionChange(next);
  }

  function handleSort(key: string) {
    const col = columns.find((c) => c.key === key);
    if (!col?.sortable) return;
    const next = nextSort(sortDir, key, sortKey);
    setSortKey(next.key || null);
    setSortDir(next.dir);
  }

  return (
    <div className="psm-table-wrap">
      {(toolbar || (selectable && selectedIds.size > 0 && batchActions)) && (
        <div className="psm-table-toolbar" role="toolbar" aria-label={`${caption}工具栏`}>
          {toolbar}
          {selectable && selectedIds.size > 0 && (
            <div className="psm-table-batch" aria-live="polite">
              <span>已选 {selectedIds.size} 项</span>
              {batchActions}
            </div>
          )}
        </div>
      )}

      <div className="psm-table-scroll">
        <table className="psm-data-table">
          <caption className="psm-sr-only">{caption}</caption>
          <thead>
            <tr>
              {selectable && (
                <th scope="col" className="psm-data-table__check-col">
                  <input
                    type="checkbox"
                    aria-label={allSelected ? '取消全选' : '全选当前页'}
                    checked={allSelected}
                    onChange={toggleAll}
                    disabled={loading || sortedRows.length === 0}
                  />
                </th>
              )}
              {columns.map((col) => (
                <th
                  key={col.key}
                  scope="col"
                  style={col.minWidth ? { minWidth: col.minWidth } : undefined}
                  aria-sort={col.sortable ? ariaSort(sortKey === col.key ? sortDir : 'none') : undefined}
                >
                  {col.sortable ? (
                    <button
                      type="button"
                      className="psm-sort-btn"
                      onClick={() => handleSort(col.key)}
                      aria-label={`按${col.label}排序`}
                    >
                      {col.label}
                      <span aria-hidden="true" className="psm-sort-icon">
                        {sortKey === col.key ? (sortDir === 'asc' ? '↑' : sortDir === 'desc' ? '↓' : '↕') : '↕'}
                      </span>
                    </button>
                  ) : (
                    col.label
                  )}
                </th>
              ))}
              {rowActions && (
                <th scope="col" className="psm-data-table__actions-col">
                  操作
                </th>
              )}
            </tr>
          </thead>
          <tbody>
            {loading && (
              <tr>
                <td colSpan={colCount}>
                  <div className="psm-table-loading" role="status" aria-live="polite">
                    正在加载…
                  </div>
                </td>
              </tr>
            )}
            {!loading &&
              sortedRows.map((row) => (
                <tr key={row.id}>
                  {selectable && (
                    <td className="psm-data-table__check-col">
                      <input
                        type="checkbox"
                        aria-label={`选择行 ${row.id}`}
                        checked={selectedIds.has(row.id)}
                        onChange={() => toggleRow(row.id)}
                      />
                    </td>
                  )}
                  {columns.map((col) => (
                    <td key={col.key}>{col.render(row)}</td>
                  ))}
                  {rowActions && <td className="psm-data-table__actions-col">{rowActions(row)}</td>}
                </tr>
              ))}
            {!loading && sortedRows.length === 0 && (
              <tr>
                <td colSpan={colCount}>
                  <div className="psm-table-empty" role="status">
                    <p>{emptyMessage}</p>
                    {hasActiveFilters && onClearFilters && (
                      <button type="button" className="psm-btn psm-btn--secondary" onClick={onClearFilters}>
                        清除筛选
                      </button>
                    )}
                  </div>
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {onPageChange && (
        <nav className="psm-pager" aria-label={`${caption}分页`}>
          <span className="psm-pager__info">
            共 {total} 条，第 {pageNo} / {totalPages} 页
          </span>
          <button
            type="button"
            className="psm-btn psm-btn--secondary"
            disabled={pageNo <= 1 || loading}
            aria-label="上一页"
            onClick={() => onPageChange(pageNo - 1)}
          >
            上一页
          </button>
          <button
            type="button"
            className="psm-btn psm-btn--secondary"
            disabled={pageNo >= totalPages || loading}
            aria-label="下一页"
            onClick={() => onPageChange(pageNo + 1)}
          >
            下一页
          </button>
        </nav>
      )}
    </div>
  );
}
