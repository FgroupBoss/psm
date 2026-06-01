import React from 'react';

interface ProgressBarProps {
  /** 0–100 */
  value: number;
  label?: string;
  visible?: boolean;
}

/** 长时间操作进度条，符合 WCAG progressbar 语义。 */
export function ProgressBar({ value, label = '处理中', visible = true }: ProgressBarProps) {
  if (!visible) {
    return null;
  }
  const clamped = Math.min(100, Math.max(0, value));
  return (
    <div className="psm-progress" role="progressbar" aria-valuenow={clamped} aria-valuemin={0} aria-valuemax={100} aria-label={label}>
      <div className="psm-progress__track">
        <div className="psm-progress__fill" style={{ width: `${clamped}%` }} />
      </div>
      <span className="psm-sr-only">
        {label}：{clamped}%
      </span>
    </div>
  );
}

/** 包装异步操作：超过 thresholdMs 后显示进度条。 */
export function useProgressAction(thresholdMs = 1000) {
  const [progress, setProgress] = React.useState(0);
  const [visible, setVisible] = React.useState(false);
  const timerRef = React.useRef<number | null>(null);
  const tickRef = React.useRef<number | null>(null);

  const run = React.useCallback(
    async <T,>(action: () => Promise<T>, label = '处理中'): Promise<T> => {
      setProgress(0);
      setVisible(false);

      timerRef.current = window.setTimeout(() => {
        setVisible(true);
        tickRef.current = window.setInterval(() => {
          setProgress((p) => (p >= 90 ? p : p + 8));
        }, 200);
      }, thresholdMs);

      try {
        const result = await action();
        setProgress(100);
        return result;
      } finally {
        if (timerRef.current) {
          window.clearTimeout(timerRef.current);
        }
        if (tickRef.current) {
          window.clearInterval(tickRef.current);
        }
        window.setTimeout(() => {
          setVisible(false);
          setProgress(0);
        }, 400);
      }
    },
    [thresholdMs]
  );

  return { progress, visible, run, ProgressBar: () => <ProgressBar value={progress} visible={visible} /> };
}

export { ProgressBar as default };
