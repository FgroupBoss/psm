import React from 'react';
import { useTheme } from './theme';

interface ThemeToggleProps {
  className?: string;
  showLabel?: boolean;
}

export function ThemeToggle({ className, showLabel }: ThemeToggleProps) {
  const { theme, toggleTheme } = useTheme();
  const isDark = theme === 'dark';

  return (
    <button
      type="button"
      className={`theme-toggle${className ? ` ${className}` : ''}`}
      onClick={toggleTheme}
      aria-label={isDark ? '切换浅色模式' : '切换深色模式'}
      title={isDark ? '浅色模式' : '深色模式'}
    >
      <i className={`fa-solid ${isDark ? 'fa-sun' : 'fa-moon'}`} aria-hidden="true" />
      {showLabel && <span>{isDark ? '浅色' : '深色'}</span>}
    </button>
  );
}
