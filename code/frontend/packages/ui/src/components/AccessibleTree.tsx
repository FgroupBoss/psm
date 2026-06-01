import React from 'react';
import { useIdPrefix } from '../hooks/useIdPrefix';
import { useTreeNavigation } from '../hooks/useTreeNavigation';
import { TableEmptyState } from './TableEmptyState';

export interface TreeGridColumn<T> {
  key: string;
  header: string;
  render: (node: T) => React.ReactNode;
  className?: string;
}

export interface AccessibleTreeProps<T> {
  /** 树根数据 */
  nodes: T[];
  getId: (node: T) => string | number;
  getChildren: (node: T) => T[] | undefined;
  getLabel: (node: T) => React.ReactNode;
  ariaLabel: string;
  /** 表格列（组织树/菜单树等多列场景） */
  columns?: Array<TreeGridColumn<T>>;
  rowActions?: (node: T) => React.ReactNode;
  /** 默认展开节点 */
  defaultExpandedIds?: Iterable<string | number>;
  /** 默认展开所有含子节点的节点（管理端树表常用） */
  defaultExpandAll?: boolean;
  /** 受控选中 */
  selectedId?: string | number | null;
  onSelect?: (node: T) => void;
  indentPx?: number;
  /** 可见深度超过该值时启用横向滚动 */
  deepScrollThreshold?: number;
  emptyMessage?: string;
  hasActiveFilters?: boolean;
  onClearFilters?: () => void;
  /** 拖拽排序（需同时提供 onReorder） */
  draggable?: boolean;
  onReorder?: (payload: {
    dragId: string | number;
    targetId: string | number;
    position: 'before' | 'after';
  }) => void | Promise<void>;
}

type DropPosition = 'before' | 'after' | null;

function toKey(id: string | number) {
  return String(id);
}

/** WCAG 友好树：键盘导航、选中高亮、深层缩进横向滚动、可选拖拽排序。 */
export function AccessibleTree<T>(props: AccessibleTreeProps<T>) {
  const {
    nodes,
    getId,
    getChildren,
    getLabel,
    ariaLabel,
    columns = [],
    rowActions,
    defaultExpandedIds,
    defaultExpandAll = false,
    selectedId: selectedIdProp,
    onSelect,
    indentPx = 20,
    deepScrollThreshold = 3,
    emptyMessage = '暂无数据',
    hasActiveFilters,
    onClearFilters,
    draggable = false,
    onReorder
  } = props;

  const idPrefix = useIdPrefix('psm-tree');
  const dragEnabled = draggable && Boolean(onReorder);
  const treeRef = React.useRef<HTMLDivElement>(null);

  const resolvedDefaultExpanded = React.useMemo(() => {
    if (defaultExpandedIds) {
      return defaultExpandedIds;
    }
    if (!defaultExpandAll) {
      return undefined;
    }
    const ids: Array<string | number> = [];
    function walk(list: T[]) {
      for (const node of list) {
        const children = getChildren(node) ?? [];
        if (children.length > 0) {
          ids.push(getId(node));
          walk(children);
        }
      }
    }
    walk(nodes);
    return ids;
  }, [defaultExpandedIds, defaultExpandAll, nodes, getId, getChildren]);

  const nav = useTreeNavigation({
    nodes,
    getId,
    getChildren,
    defaultExpandedIds: resolvedDefaultExpanded
  });

  const [dragId, setDragId] = React.useState<string | number | null>(null);
  const [dropTarget, setDropTarget] = React.useState<{ id: string | number; position: DropPosition } | null>(null);
  const [dragHint, setDragHint] = React.useState('');

  const selectedId = selectedIdProp ?? nav.selectedId;

  React.useEffect(() => {
    if (!dragId) {
      return undefined;
    }
    function onKeyDown(event: KeyboardEvent) {
      if (event.key === 'Escape') {
        event.preventDefault();
        setDragId(null);
        setDropTarget(null);
        setDragHint('已取消拖拽排序');
      }
    }
    window.addEventListener('keydown', onKeyDown);
    return () => window.removeEventListener('keydown', onKeyDown);
  }, [dragId]);

  React.useEffect(() => {
    if (!dragHint) {
      return undefined;
    }
    const timer = window.setTimeout(() => setDragHint(''), 3200);
    return () => window.clearTimeout(timer);
  }, [dragHint]);

  function isSelected(id: string | number) {
    return selectedId != null && toKey(selectedId) === toKey(id);
  }

  function handleSelect(node: T, id: string | number) {
    nav.setSelectedId(id);
    onSelect?.(node);
  }

  function handleDragStart(event: React.DragEvent, id: string | number) {
    if (!dragEnabled) {
      return;
    }
    event.dataTransfer.effectAllowed = 'move';
    event.dataTransfer.setData('text/plain', toKey(id));
    setDragId(id);
    setDragHint('拖动调整顺序，按 Esc 取消');
  }

  function handleDragOver(event: React.DragEvent, id: string | number) {
    if (!dragEnabled || dragId == null || toKey(dragId) === toKey(id)) {
      return;
    }
    event.preventDefault();
    event.dataTransfer.dropEffect = 'move';
    const rect = (event.currentTarget as HTMLElement).getBoundingClientRect();
    const position: DropPosition = event.clientY < rect.top + rect.height / 2 ? 'before' : 'after';
    setDropTarget({ id, position });
  }

  function handleDrop(event: React.DragEvent, targetId: string | number) {
    if (!dragEnabled || dragId == null) {
      return;
    }
    event.preventDefault();
    const position = dropTarget?.id === targetId ? dropTarget.position : 'after';
    if (position && toKey(dragId) !== toKey(targetId)) {
      void onReorder?.({ dragId, targetId, position });
    }
    setDragId(null);
    setDropTarget(null);
    setDragHint('');
  }

  function endDrag() {
    setDragId(null);
    setDropTarget(null);
  }

  const deepScroll = nav.maxVisibleDepth > deepScrollThreshold;
  const gridCols =
    columns.length > 0 || rowActions
      ? `minmax(220px, 1.4fr) ${columns.map(() => 'minmax(88px, auto)').join(' ')}${rowActions ? ' minmax(160px, auto)' : ''}`
      : '1fr';

  if (nodes.length === 0) {
    return (
      <TableEmptyState
        message={emptyMessage}
        hasActiveFilters={hasActiveFilters}
        onClearFilters={onClearFilters}
      />
    );
  }

  return (
    <div className="psm-tree-grid">
      {(columns.length > 0 || rowActions) && (
        <div className="psm-tree-grid__header" role="row" style={{ gridTemplateColumns: gridCols }}>
          <div role="columnheader">名称</div>
          {columns.map((col) => (
            <div key={col.key} role="columnheader" className={col.className}>
              {col.header}
            </div>
          ))}
          {rowActions && (
            <div role="columnheader" className="psm-tree-grid__actions-header">
              操作
            </div>
          )}
        </div>
      )}

      <div
        ref={treeRef}
        id={idPrefix}
        className={`psm-tree-scroll${deepScroll ? ' psm-tree-scroll--deep' : ''}`}
        role="tree"
        aria-label={ariaLabel}
        aria-describedby={`${idPrefix}-help`}
        aria-multiselectable={false}
      >
        <div className="psm-tree-inner" style={{ ['--psm-tree-indent' as string]: `${indentPx}px` }}>
          {nav.flatItems.map((item) => {
            const focused = nav.focusedId != null && item.id === nav.focusedId;
            const selected = isSelected(item.id);
            const isDragging = dragId != null && toKey(dragId) === toKey(item.id);
            const showDropBefore =
              dropTarget != null && dropTarget.id === item.id && dropTarget.position === 'before';
            const showDropAfter =
              dropTarget != null && dropTarget.id === item.id && dropTarget.position === 'after';

            return (
              <div
                key={toKey(item.id)}
                className={`psm-tree-row${focused ? ' psm-tree-row--focused' : ''}${selected ? ' psm-tree-row--selected' : ''}${isDragging ? ' psm-tree-row--dragging' : ''}${item.depth > deepScrollThreshold ? ' psm-tree-row--deep' : ''}`}
                role="treeitem"
                aria-level={item.depth}
                aria-setsize={item.setSize}
                aria-posinset={item.posInSet}
                aria-expanded={item.hasChildren ? item.expanded : undefined}
                aria-selected={selected}
                tabIndex={focused ? 0 : -1}
                style={{ gridTemplateColumns: gridCols, ['--depth' as string]: String(item.depth) }}
                onFocus={() => nav.setFocusedId(item.id)}
                onKeyDown={nav.handleKeyDown}
                onDragOver={(event) => handleDragOver(event, item.id)}
                onDrop={(event) => handleDrop(event, item.id)}
                onDragEnd={endDrag}
              >
                {showDropBefore && <div className="psm-tree-drop-placeholder psm-tree-drop-placeholder--before" aria-hidden="true" />}
                <div
                  className="psm-tree-label-cell"
                  style={{ paddingLeft: `calc((${item.depth} - 1) * var(--psm-tree-indent))` }}
                >
                  {dragEnabled && (
                    <button
                      type="button"
                      className="psm-tree-drag-handle"
                      draggable
                      aria-label="拖动排序"
                      title="拖动排序，Esc 取消"
                      onDragStart={(event) => handleDragStart(event, item.id)}
                      onClick={(event) => event.stopPropagation()}
                    >
                      <span aria-hidden="true">⠿</span>
                    </button>
                  )}
                  {item.hasChildren ? (
                    <button
                      type="button"
                      className="psm-tree-expand"
                      data-tree-expand
                      aria-label={item.expanded ? '收起' : '展开'}
                      aria-expanded={item.expanded}
                      onClick={(event) => {
                        event.stopPropagation();
                        nav.toggleExpanded(item.id);
                      }}
                    >
                      <span aria-hidden="true">{item.expanded ? '▾' : '▸'}</span>
                    </button>
                  ) : (
                    <span className="psm-tree-expand psm-tree-expand--leaf" aria-hidden="true" />
                  )}
                  <button
                    type="button"
                    className="psm-tree-label"
                    onClick={() => handleSelect(item.node, item.id)}
                  >
                    {getLabel(item.node)}
                  </button>
                </div>
                {columns.map((col) => (
                  <div key={col.key} className={`psm-tree-cell${col.className ? ` ${col.className}` : ''}`}>
                    {col.render(item.node)}
                  </div>
                ))}
                {rowActions && <div className="psm-tree-cell psm-tree-grid__actions">{rowActions(item.node)}</div>}
                {showDropAfter && <div className="psm-tree-drop-placeholder psm-tree-drop-placeholder--after" aria-hidden="true" />}
              </div>
            );
          })}
        </div>
      </div>

      {dragHint && (
        <p className="psm-tree-drag-hint" role="status" aria-live="polite">
          {dragHint}
        </p>
      )}
      <p className="psm-sr-only" id={`${idPrefix}-help`}>
        使用上下方向键移动焦点，左右方向键展开或收起节点，Enter 选中当前节点。
        {dragEnabled ? ' 拖动左侧手柄可调整顺序，按 Esc 取消拖拽。' : ''}
      </p>
    </div>
  );
}
