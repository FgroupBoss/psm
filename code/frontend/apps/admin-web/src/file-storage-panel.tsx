import React from 'react';
import {
  fetchDefaultFileStorageProfile,
  fetchFileBackends,
  fetchFileStorageProfiles,
  saveFileStorageProfile,
  testFileStorageProfile,
  uploadFile
} from '@psm/api-client';
import type { AuthUser, FileBackendSchemaRecord, FileStorageProfileRecord } from '@psm/domain-types';
import { errorMessage, formatTime } from './ui-helpers';

const DEFAULT_CONFIG = '{\n  "mode": "SDK",\n  "storageRoot": "./data/psm-files"\n}';

export function FileStoragePanel({ user }: { user: AuthUser }) {
  const [profiles, setProfiles] = React.useState<FileStorageProfileRecord[]>([]);
  const [schemas, setSchemas] = React.useState<FileBackendSchemaRecord[]>([]);
  const [editing, setEditing] = React.useState<FileStorageProfileRecord | 'new' | null>(null);
  const [profileCode, setProfileCode] = React.useState('default');
  const [profileName, setProfileName] = React.useState('默认存储');
  const [storageBackend, setStorageBackend] = React.useState('LOCAL');
  const [configJson, setConfigJson] = React.useState(DEFAULT_CONFIG);
  const [asDefault, setAsDefault] = React.useState(true);
  const [loading, setLoading] = React.useState(true);
  const [error, setError] = React.useState('');
  const [message, setMessage] = React.useState('');

  const load = React.useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const [list, backendSchemas, current] = await Promise.all([
        fetchFileStorageProfiles(user.tenantId),
        fetchFileBackends(),
        fetchDefaultFileStorageProfile(user.tenantId)
      ]);
      setProfiles(list);
      setSchemas(backendSchemas);
      if (current && editing === null) {
        setMessage(current.configured ? `当前默认：${current.profileName} (${current.storageBackend})` : '默认存储未完整配置');
      }
    } catch (err: unknown) {
      setError(errorMessage(err, '存储配置加载失败'));
    } finally {
      setLoading(false);
    }
  }, [user.tenantId, editing]);

  React.useEffect(() => {
    load();
  }, [load]);

  function openEdit(record: FileStorageProfileRecord | 'new') {
    setEditing(record);
    if (record === 'new') {
      setProfileCode('minio-prod');
      setProfileName('MinIO 生产');
      setStorageBackend('MINIO');
      setConfigJson(
        '{\n  "mode": "SDK",\n  "endpoint": "http://minio:9000",\n  "accessKey": "",\n  "secretKey": "",\n  "bucket": "psm-files"\n}'
      );
      setAsDefault(false);
      return;
    }
    setProfileCode(record.profileCode);
    setProfileName(record.profileName);
    setStorageBackend(record.storageBackend);
    setConfigJson(JSON.stringify(record.config || {}, null, 2));
    setAsDefault(record.defaultProfile);
  }

  async function handleSave() {
    setError('');
    let config: Record<string, unknown>;
    try {
      config = JSON.parse(configJson) as Record<string, unknown>;
    } catch {
      setError('配置 JSON 格式不正确');
      return;
    }
    try {
      await saveFileStorageProfile({
        tenantId: user.tenantId,
        profileCode,
        profileName,
        storageBackend,
        config,
        enabled: true,
        defaultProfile: asDefault
      });
      setMessage('存储配置已保存');
      setEditing(null);
      load();
    } catch (err: unknown) {
      setError(errorMessage(err, '保存失败'));
    }
  }

  async function handleTest(profileId: number) {
    try {
      const result = await testFileStorageProfile(user.tenantId, profileId);
      setMessage(`连通性测试：${result.status} - ${result.message}`);
      load();
    } catch (err: unknown) {
      setError(errorMessage(err, '测试失败'));
    }
  }

  async function handleProbeUpload(file: File) {
    try {
      const uploaded = await uploadFile(user.tenantId, file, 'STORAGE_PROBE', undefined, profileCode);
      setMessage(`探针上传成功 #${uploaded.id} backend=${uploaded.storageBackend || 'LOCAL'}`);
    } catch (err: unknown) {
      setError(errorMessage(err, '探针上传失败'));
    }
  }

  const schemaHint = schemas.find((s) => s.backend === storageBackend);

  return (
    <div className="panel-stack">
      <div className="toolbar">
        <button type="button" className="primary" onClick={() => openEdit('new')}>
          新建配置档
        </button>
        <button type="button" className="secondary" onClick={load}>
          刷新
        </button>
        <label className="btn btn-secondary" style={{ cursor: 'pointer' }}>
          探针上传
          <input
            type="file"
            hidden
            onChange={(e) => {
              const file = e.target.files?.[0];
              if (file) void handleProbeUpload(file);
              e.target.value = '';
            }}
          />
        </label>
      </div>
      {message && <div className="hint success">{message}</div>}
      {error && <div className="hint error">{error}</div>}

      {editing && (
        <div className="detail-card">
          <h3>{editing === 'new' ? '新建存储配置' : '编辑存储配置'}</h3>
          <div className="form-grid">
            <label>
              编码
              <input value={profileCode} onChange={(e) => setProfileCode(e.target.value)} />
            </label>
            <label>
              名称
              <input value={profileName} onChange={(e) => setProfileName(e.target.value)} />
            </label>
            <label>
              后端
              <select value={storageBackend} onChange={(e) => setStorageBackend(e.target.value)}>
                {schemas.map((s) => (
                  <option key={s.backend} value={s.backend}>
                    {s.label} ({s.backend})
                  </option>
                ))}
              </select>
            </label>
            <label style={{ gridColumn: '1 / -1' }}>
              <input type="checkbox" checked={asDefault} onChange={(e) => setAsDefault(e.target.checked)} /> 设为租户默认
            </label>
          </div>
          {schemaHint && <p className="meta">{schemaHint.hint} · 字段：{schemaHint.requiredFields.join(', ')}</p>}
          <p className="meta">云厂商可设 mode=HTTP 与 httpUrl 对接统一网关；MinIO/OSS 可设 mode=SDK 并填写 endpoint/密钥/bucket。</p>
          <textarea
            rows={12}
            style={{ width: '100%', fontFamily: 'monospace', marginTop: 8 }}
            value={configJson}
            onChange={(e) => setConfigJson(e.target.value)}
          />
          <div className="toolbar" style={{ marginTop: 12 }}>
            <button type="button" className="primary" onClick={handleSave}>
              保存
            </button>
            <button type="button" className="secondary" onClick={() => setEditing(null)}>
              取消
            </button>
          </div>
        </div>
      )}

      <div className="table-wrap">
        <table className="data-table">
          <thead>
            <tr>
              <th>编码</th>
              <th>名称</th>
              <th>后端</th>
              <th>默认</th>
              <th>状态</th>
              <th>测试</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            {loading && (
              <tr>
                <td colSpan={7}>加载中…</td>
              </tr>
            )}
            {!loading && profiles.length === 0 && (
              <tr>
                <td colSpan={7}>暂无配置，将使用平台 LOCAL 兜底</td>
              </tr>
            )}
            {profiles.map((p) => (
              <tr key={p.id}>
                <td>{p.profileCode}</td>
                <td>{p.profileName}</td>
                <td>{p.storageBackend}</td>
                <td>{p.defaultProfile ? '是' : ''}</td>
                <td>{p.configured ? '已配置' : '未完成'}</td>
                <td>
                  {p.lastTestStatus || '—'}
                  {p.lastTestAt && <span className="meta"> {formatTime(p.lastTestAt)}</span>}
                </td>
                <td>
                  <button type="button" className="btn-sm secondary" onClick={() => openEdit(p)}>
                    编辑
                  </button>{' '}
                  <button type="button" className="btn-sm secondary" onClick={() => handleTest(p.id)}>
                    测试
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
