import React from 'react';
import { useIdPrefix } from '../hooks/useIdPrefix';

export interface FormFieldProps {
  label: string;
  name: string;
  value: string;
  onChange: (value: string) => void;
  error?: string;
  required?: boolean;
  disabled?: boolean;
  type?: 'text' | 'email' | 'tel' | 'password' | 'number';
  hint?: string;
  /** blur 时触发校验 */
  onBlurValidate?: (value: string) => string | undefined;
  /** 输入时实时校验 */
  onChangeValidate?: (value: string) => string | undefined;
  multiline?: boolean;
  rows?: number;
  autoComplete?: string;
}

/** 始终可见 label + 实时/失焦校验 + 明确错误提示。 */
export function FormField({
  label,
  name,
  value,
  onChange,
  error: externalError,
  required,
  disabled,
  type = 'text',
  hint,
  onBlurValidate,
  onChangeValidate,
  multiline,
  rows = 3,
  autoComplete
}: FormFieldProps) {
  const baseId = useIdPrefix(`field-${name}`);
  const inputId = `${baseId}-input`;
  const errorId = `${baseId}-error`;
  const hintId = hint ? `${baseId}-hint` : undefined;
  const [touched, setTouched] = React.useState(false);
  const [internalError, setInternalError] = React.useState('');

  const error = externalError || (touched ? internalError : '');

  function validate(val: string) {
    if (required && !val.trim()) {
      return `请填写${label}`;
    }
    if (onChangeValidate) {
      return onChangeValidate(val);
    }
    return undefined;
  }

  function handleChange(next: string) {
    onChange(next);
    if (onChangeValidate || required) {
      setInternalError(validate(next) || '');
    }
  }

  function handleBlur() {
    setTouched(true);
    if (onBlurValidate) {
      setInternalError(onBlurValidate(value) || '');
    } else {
      setInternalError(validate(value) || '');
    }
  }

  const describedBy = [error ? errorId : null, hintId].filter(Boolean).join(' ') || undefined;

  const commonProps = {
    id: inputId,
    name,
    value,
    disabled,
    required,
    'aria-invalid': error ? true : undefined,
    'aria-describedby': describedBy,
    autoComplete,
    onBlur: handleBlur
  };

  return (
    <div className={`psm-field ${error ? 'psm-field--error' : ''}`}>
      <label htmlFor={inputId} className="psm-field__label">
        {label}
        {required && (
          <span className="psm-field__required" aria-hidden="true">
            *
          </span>
        )}
        {required && <span className="psm-sr-only">（必填）</span>}
      </label>
      {multiline ? (
        <textarea
          {...commonProps}
          rows={rows}
          className="psm-field__input"
          onChange={(e) => handleChange(e.target.value)}
        />
      ) : (
        <input
          {...commonProps}
          type={type}
          className="psm-field__input"
          onChange={(e) => handleChange(e.target.value)}
        />
      )}
      {hint && !error && (
        <p id={hintId} className="psm-field__hint">
          {hint}
        </p>
      )}
      {error && (
        <p id={errorId} className="psm-field__error" role="alert">
          {error}
        </p>
      )}
    </div>
  );
}

/** 下拉选择字段，label 始终可见。 */
export function SelectField({
  label,
  name,
  value,
  onChange,
  options,
  required,
  disabled,
  error
}: {
  label: string;
  name: string;
  value: string;
  onChange: (value: string) => void;
  options: Array<{ value: string; label: string }>;
  required?: boolean;
  disabled?: boolean;
  error?: string;
}) {
  const baseId = useIdPrefix(`select-${name}`);
  const selectId = `${baseId}-select`;
  const errorId = `${baseId}-error`;

  return (
    <div className={`psm-field ${error ? 'psm-field--error' : ''}`}>
      <label htmlFor={selectId} className="psm-field__label">
        {label}
        {required && <span className="psm-sr-only">（必填）</span>}
      </label>
      <select
        id={selectId}
        name={name}
        value={value}
        disabled={disabled}
        required={required}
        className="psm-field__input"
        aria-invalid={error ? true : undefined}
        aria-describedby={error ? errorId : undefined}
        onChange={(e) => onChange(e.target.value)}
      >
        {options.map((opt) => (
          <option key={opt.value || '__empty'} value={opt.value}>
            {opt.label}
          </option>
        ))}
      </select>
      {error && (
        <p id={errorId} className="psm-field__error" role="alert">
          {error}
        </p>
      )}
    </div>
  );
}
