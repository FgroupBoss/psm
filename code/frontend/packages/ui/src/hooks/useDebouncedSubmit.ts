import React from 'react';

/** 提交防抖：防止重复点击，配合 loading 状态使用。 */
export function useDebouncedSubmit(delayMs = 300) {
  const [submitting, setSubmitting] = React.useState(false);
  const lockRef = React.useRef(false);

  const run = React.useCallback(
    async (action: () => Promise<void>) => {
      if (lockRef.current) {
        return;
      }
      lockRef.current = true;
      setSubmitting(true);
      try {
        await action();
      } finally {
        setTimeout(() => {
          lockRef.current = false;
          setSubmitting(false);
        }, delayMs);
      }
    },
    [delayMs]
  );

  return { submitting, run };
}
