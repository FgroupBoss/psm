import React from 'react';
import { login } from '@psm/api-client';
import type { AuthUser } from '@psm/domain-types';
import { errorMessage } from './ui-helpers';
import { ThemeToggle } from '@psm/ui';

type AuthMode = 'login' | 'register';

interface LoginViewProps {
  onLogin: (user: AuthUser) => void;
}

export function LoginView({ onLogin }: LoginViewProps) {
  const [mode, setMode] = React.useState<AuthMode>('login');
  const [account, setAccount] = React.useState('admin');
  const [password, setPassword] = React.useState('');
  const [confirmPassword, setConfirmPassword] = React.useState('');
  const [showPassword, setShowPassword] = React.useState(false);
  const [submitting, setSubmitting] = React.useState(false);
  const [accountError, setAccountError] = React.useState('');
  const [passwordError, setPasswordError] = React.useState('');
  const [formError, setFormError] = React.useState('');

  function switchMode(next: AuthMode) {
    setMode(next);
    setAccountError('');
    setPasswordError('');
    setFormError('');
    setConfirmPassword('');
  }

  function validate(): boolean {
    let valid = true;
    setAccountError('');
    setPasswordError('');
    setFormError('');

    const trimmedAccount = account.trim();
    if (!trimmedAccount) {
      setAccountError('请输入邮箱或手机号');
      valid = false;
    }

    if (!password) {
      setPasswordError('请输入密码');
      valid = false;
    } else if (mode === 'register' && password.length < 6) {
      setPasswordError('密码至少 6 位');
      valid = false;
    }

    if (mode === 'register' && password !== confirmPassword) {
      setPasswordError('两次输入的密码不一致');
      valid = false;
    }

    return valid;
  }

  async function submit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!validate()) return;

    if (mode === 'register') {
      setFormError('注册需由管理员开通，请联系系统管理员创建账号');
      return;
    }

    setSubmitting(true);
    setFormError('');
    try {
      const result = await login({
        tenantId: 1,
        username: account.trim(),
        password
      });
      onLogin(result.user);
    } catch (err: unknown) {
      const message = errorMessage(err, '登录失败，请检查账号或密码');
      setFormError(message);
    } finally {
      setSubmitting(false);
    }
  }

  function handlePasskeyHint() {
    setFormError('通行密钥登录即将支持，请暂时使用密码登录');
  }

  function handleFaceIdHint() {
    setFormError('Face ID 模拟：试点环境请使用密码登录');
  }

  return (
    <main className="auth-screen">
      <ThemeToggle className="theme-toggle--floating" />
      <div className="auth-bg" aria-hidden="true">
        <div className="auth-bg__orb auth-bg__orb--1" />
        <div className="auth-bg__orb auth-bg__orb--2" />
        <div className="auth-bg__orb auth-bg__orb--3" />
      </div>

      <div className="auth-card">
        <div className="auth-logo" aria-hidden="true">
          <svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect width="48" height="48" rx="12" fill="url(#auth-logo-grad)" />
            <path
              d="M24 12c-3.2 0-5.8 2.4-5.8 5.4 0 3 2.6 5.4 5.8 5.4s5.8-2.4 5.8-5.4C29.8 14.4 27.2 12 24 12zm0 22.2c-5.6 0-10.4 2.8-12.8 7 1.2 1.4 3 2.4 5 2.4h15.6c2 0 3.8-1 5-2.4-2.4-4.2-7.2-7-12.8-7z"
              fill="#fff"
              fillOpacity="0.95"
            />
            <defs>
              <linearGradient id="auth-logo-grad" x1="8" y1="4" x2="40" y2="44" gradientUnits="userSpaceOnUse">
                <stop stopColor="#0A84FF" />
                <stop offset="1" stopColor="#5E5CE6" />
              </linearGradient>
            </defs>
          </svg>
        </div>

        <div className="auth-tabs" role="tablist">
          <button
            type="button"
            role="tab"
            className={mode === 'login' ? 'active' : ''}
            aria-selected={mode === 'login'}
            onClick={() => switchMode('login')}
          >
            登录
          </button>
          <button
            type="button"
            role="tab"
            className={mode === 'register' ? 'active' : ''}
            aria-selected={mode === 'register'}
            onClick={() => switchMode('register')}
          >
            注册
          </button>
        </div>

        <h1 className="auth-title">{mode === 'login' ? '登录 PSM 账户' : '创建 PSM 账户'}</h1>
        <p className="auth-subtitle">安全管理平台 · 企业级过程安全</p>

        <form className="auth-form" onSubmit={submit} noValidate>
          <div className={`auth-field${accountError ? ' has-error' : ''}`}>
            <input
              id="auth-account"
              type="text"
              inputMode="email"
              autoComplete={mode === 'login' ? 'username' : 'email'}
              placeholder="邮箱或手机号"
              value={account}
              onChange={(e) => {
                setAccount(e.target.value);
                if (accountError) setAccountError('');
              }}
              aria-invalid={!!accountError}
              aria-describedby={accountError ? 'auth-account-error' : undefined}
            />
            {accountError && (
              <p id="auth-account-error" className="auth-field-error" role="alert">
                {accountError}
              </p>
            )}
          </div>

          <div className={`auth-field auth-field--password${passwordError ? ' has-error' : ''}`}>
            <input
              id="auth-password"
              type={showPassword ? 'text' : 'password'}
              autoComplete={mode === 'login' ? 'current-password' : 'new-password'}
              placeholder="密码"
              value={password}
              onChange={(e) => {
                setPassword(e.target.value);
                if (passwordError) setPasswordError('');
              }}
              aria-invalid={!!passwordError}
              aria-describedby={passwordError ? 'auth-password-error' : undefined}
            />
            <button
              type="button"
              className="auth-field-toggle"
              onClick={() => setShowPassword((v) => !v)}
              aria-label={showPassword ? '隐藏密码' : '显示密码'}
            >
              <i className={`fa-regular ${showPassword ? 'fa-eye-slash' : 'fa-eye'}`} />
            </button>
            {passwordError && (
              <p id="auth-password-error" className="auth-field-error" role="alert">
                {passwordError}
              </p>
            )}
          </div>

          {mode === 'register' && (
            <div className="auth-field auth-field--password">
              <input
                id="auth-confirm"
                type={showPassword ? 'text' : 'password'}
                autoComplete="new-password"
                placeholder="确认密码"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
              />
            </div>
          )}

          {formError && (
            <p className="auth-form-error" role="alert">
              {formError}
            </p>
          )}

          <button type="submit" className={`auth-submit${submitting ? ' is-loading' : ''}`} disabled={submitting}>
            <span className="auth-submit__label">{mode === 'login' ? '登录' : '注册'}</span>
            <span className="auth-submit__spinner" aria-hidden="true" />
          </button>
        </form>

        <div className="auth-links">
          <button type="button" className="auth-link" onClick={() => setFormError('请联系管理员重置密码')}>
            忘记密码？
          </button>
          <button
            type="button"
            className="auth-link"
            onClick={() => switchMode(mode === 'login' ? 'register' : 'login')}
          >
            {mode === 'login' ? '创建账户' : '已有账户？登录'}
          </button>
        </div>

        <div className="auth-divider">
          <span>或使用</span>
        </div>

        <button type="button" className="auth-faceid" onClick={handleFaceIdHint} aria-label="使用 Face ID 登录">
          <i className="fa-solid fa-face-smile" />
        </button>
        <p className="auth-passkey-hint">
          <button type="button" className="auth-link auth-link--inline" onClick={handlePasskeyHint}>
            使用通行密钥登录
          </button>
        </p>
      </div>
    </main>
  );
}

export function AuthBootScreen() {
  return (
    <main className="auth-screen auth-screen--boot">
      <ThemeToggle className="theme-toggle--floating" />
      <div className="auth-bg" aria-hidden="true">
        <div className="auth-bg__orb auth-bg__orb--1" />
        <div className="auth-bg__orb auth-bg__orb--2" />
      </div>
      <div className="auth-boot">
        <span className="auth-submit__spinner auth-submit__spinner--lg" aria-hidden="true" />
        <p>正在恢复登录状态…</p>
      </div>
    </main>
  );
}
