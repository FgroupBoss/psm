import React from 'react';

export interface FlatTreeItem<T> {
  node: T;
  id: string | number;
  depth: number;
  parentId: string | number | null;
  hasChildren: boolean;
  expanded: boolean;
  /** 同级序号（1-based，供 aria-posinset） */
  posInSet: number;
  /** 同级总数（供 aria-setsize） */
  setSize: number;
}

export interface UseTreeNavigationOptions<T> {
  nodes: T[];
  getId: (node: T) => string | number;
  getChildren: (node: T) => T[] | undefined;
  defaultExpandedIds?: Iterable<string | number>;
}

function toKey(id: string | number) {
  return String(id);
}

/** 将树按展开状态拍平为可见行，并维护展开/焦点/选中状态。 */
export function useTreeNavigation<T>(options: UseTreeNavigationOptions<T>) {
  const { nodes, getId, getChildren, defaultExpandedIds } = options;

  const [expandedKeys, setExpandedKeys] = React.useState<Set<string>>(() => new Set<string>());

  const defaultExpandedKey = React.useMemo(() => {
    if (!defaultExpandedIds) {
      return '';
    }
    const ids: string[] = [];
    for (const id of defaultExpandedIds) {
      ids.push(toKey(id));
    }
    return ids.sort().join(',');
  }, [defaultExpandedIds]);

  React.useEffect(() => {
    if (!defaultExpandedKey) {
      return;
    }
    const ids = defaultExpandedKey.split(',').filter(Boolean);
    setExpandedKeys((prev) => {
      const next = new Set(prev);
      for (const id of ids) {
        next.add(id);
      }
      return next;
    });
  }, [defaultExpandedKey]);

  const [focusedId, setFocusedId] = React.useState<string | number | null>(null);
  const [selectedId, setSelectedId] = React.useState<string | number | null>(null);

  const flatItems = React.useMemo(() => {
    const items: FlatTreeItem<T>[] = [];

    function walk(siblings: T[], depth: number, parentId: string | number | null) {
      const setSize = siblings.length;
      siblings.forEach((node, index) => {
        const id = getId(node);
        const children = getChildren(node) ?? [];
        const hasChildren = children.length > 0;
        const expanded = hasChildren && expandedKeys.has(toKey(id));
        items.push({
          node,
          id,
          depth,
          parentId,
          hasChildren,
          expanded,
          posInSet: index + 1,
          setSize
        });
        if (expanded) {
          walk(children, depth + 1, id);
        }
      });
    }

    walk(nodes, 1, null);
    return items;
  }, [nodes, getId, getChildren, expandedKeys]);

  const maxVisibleDepth = React.useMemo(
    () => flatItems.reduce((max, item) => Math.max(max, item.depth), 1),
    [flatItems]
  );

  React.useEffect(() => {
    if (flatItems.length === 0) {
      setFocusedId(null);
      return;
    }
    const exists = focusedId != null && flatItems.some((item) => item.id === focusedId);
    if (!exists) {
      setFocusedId(flatItems[0].id);
    }
  }, [flatItems, focusedId]);

  const focusIndex = React.useMemo(() => {
    if (focusedId == null) {
      return -1;
    }
    return flatItems.findIndex((item) => item.id === focusedId);
  }, [flatItems, focusedId]);

  function toggleExpanded(id: string | number) {
    const key = toKey(id);
    setExpandedKeys((prev) => {
      const next = new Set(prev);
      if (next.has(key)) {
        next.delete(key);
      } else {
        next.add(key);
      }
      return next;
    });
  }

  function expand(id: string | number) {
    setExpandedKeys((prev) => new Set(prev).add(toKey(id)));
  }

  function collapse(id: string | number) {
    setExpandedKeys((prev) => {
      const next = new Set(prev);
      next.delete(toKey(id));
      return next;
    });
  }

  function focusItemAt(index: number) {
    const item = flatItems[index];
    if (item) {
      setFocusedId(item.id);
    }
  }

  function focusParent() {
    if (focusIndex < 0) {
      return;
    }
    const current = flatItems[focusIndex];
    if (!current?.parentId) {
      return;
    }
    setFocusedId(current.parentId);
  }

  function focusFirstChild() {
    if (focusIndex < 0) {
      return;
    }
    const next = flatItems[focusIndex + 1];
    if (next && next.parentId === flatItems[focusIndex].id) {
      setFocusedId(next.id);
    }
  }

  function handleKeyDown(event: React.KeyboardEvent) {
    if (flatItems.length === 0) {
      return;
    }
    const current = focusIndex >= 0 ? flatItems[focusIndex] : flatItems[0];

    switch (event.key) {
      case 'ArrowDown':
        event.preventDefault();
        if (focusIndex < flatItems.length - 1) {
          focusItemAt(focusIndex + 1);
        }
        break;
      case 'ArrowUp':
        event.preventDefault();
        if (focusIndex > 0) {
          focusItemAt(focusIndex - 1);
        }
        break;
      case 'ArrowRight':
        event.preventDefault();
        if (current.hasChildren) {
          if (!current.expanded) {
            expand(current.id);
          } else {
            focusFirstChild();
          }
        }
        break;
      case 'ArrowLeft':
        event.preventDefault();
        if (current.hasChildren && current.expanded) {
          collapse(current.id);
        } else {
          focusParent();
        }
        break;
      case 'Home':
        event.preventDefault();
        focusItemAt(0);
        break;
      case 'End':
        event.preventDefault();
        focusItemAt(flatItems.length - 1);
        break;
      case 'Enter':
      case ' ':
        if ((event.target as HTMLElement).closest('[data-tree-expand]')) {
          return;
        }
        event.preventDefault();
        setSelectedId(current.id);
        break;
      default:
        break;
    }
  }

  return {
    flatItems,
    maxVisibleDepth,
    expandedKeys,
    focusedId,
    selectedId,
    setFocusedId,
    setSelectedId,
    toggleExpanded,
    expand,
    collapse,
    handleKeyDown
  };
}
