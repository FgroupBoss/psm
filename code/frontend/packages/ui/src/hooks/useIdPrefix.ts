import React from 'react';

let counter = 0;

/** 生成页面内唯一的 id 前缀，用于 label/input 关联。 */
export function useIdPrefix(prefix = 'psm') {
  return React.useMemo(() => `${prefix}-${++counter}`, [prefix]);
}
