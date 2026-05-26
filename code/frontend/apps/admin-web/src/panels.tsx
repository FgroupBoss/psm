import React from 'react';
import {
  assignUserRoles,
  createBaseData,
  createIamUser,
  createMenu,
  createOrg,
  createRole,
  deleteBaseData,
  deleteMenu,
  deleteOrg,
  deleteRole,
  disableBaseData,
  enableBaseData,
  fetchBaseDataPage,
  fetchIamUsers,
  fetchMenuTree,
  fetchOrgTree,
  fetchRoles,
  updateBaseData,
  updateIamUser,
  updateIamUserStatus,
  updateMenu,
  updateOrg,
  updateRole
} from '@psm/api-client';
import type {
  BaseDataRecord,
  BaseDataRequest,
  IamUserRecord,
  IamUserRequest,
  MenuResourceRequest,
  MenuTreeNode,
  OrgRequest,
  OrgTreeNode,
  RoleRecord,
  RoleRequest
} from '@psm/domain-types';
import type { AppView } from './nav';
import { confirmAction, errorMessage, StatusTag, useAsyncAction } from './ui-helpers';

type BaseDataType = 'areas' | 'units' | 'equipments' | 'monitor-points';

interface BaseDataMeta {
  type: BaseDataType;
  title: string;
  description: string;
  codeLabel: string;
  nameLabel: string;
}

const BASE_DATA_META: Record<BaseDataType, BaseDataMeta> = {
  areas: {
    type: 'areas',
    title: '区域台账',
    description: '维护厂区/车间等区域主数据，支持启停、删除与审计。',
    codeLabel: '区域编码',
    nameLabel: '区域名称'
  },
  units: {
    type: 'units',
    title: '装置台账',
    description: '维护生产装置，需关联所属区域。',
    codeLabel: '装置编码',
    nameLabel: '装置名称'
  },
  equipments: {
    type: 'equipments',
    title: '设备台账',
    description: '维护设备主数据，需关联所属装置。',
    codeLabel: '设备编码',
    nameLabel: '设备名称'
  },
  'monitor-points': {
    type: 'monitor-points',
    title: '监测点位',
    description: '维护 DCS/GDS 等监测点位与阈值信息。',
    codeLabel: '点位编码',
    nameLabel: '点位名称'
  }
};

export function BaseDataLedgerPanel({ tenantId, view }: { tenantId: number; view: AppView }) {
  const dataType = view.replace('master-data:', '') as BaseDataType;
  const meta = BASE_DATA_META[dataType];
  const [records, setRecords] = React.useState<BaseDataRecord[]>([]);
  const [keyword, setKeyword] = React.useState<string>('');
  const [status, setStatus] = React.useState<string>('');
  const [pageNo, setPageNo] = React.useState<number>(1);
  const [total, setTotal] = React.useState<number>(0);
  const [loading, setLoading] = React.useState<boolean>(true);
  const [editing, setEditing] = React.useState<BaseDataRecord | null | 'new'>(null);
  const { message, error, setError, run } = useAsyncAction();

  const load = React.useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const page = await fetchBaseDataPage(dataType, { tenantId, keyword, status, pageNo, pageSize: 10 });
      setRecords(page.records);
      setTotal(page.total);
    } catch (err: unknown) {
      setError(errorMessage(err, `${meta.title}加载失败`));
    } finally {
      setLoading(false);
    }
  }, [dataType, tenantId, keyword, status, pageNo, meta.title, setError]);

  React.useEffect(() => {
    load();
  }, [load]);

  const totalPages = Math.max(1, Math.ceil(total / 10));

  return (
    <section className="content-panel">
      <div className="panel-header">
        <div>
          <h2>{meta.title}</h2>
          <p>{meta.description}</p>
        </div>
        <button type="button" onClick={() => setEditing('new')}>
          新增
        </button>
      </div>

      <div className="toolbar">
        <input
          placeholder="搜索编码或名称"
          value={keyword}
          onChange={(event) => {
            setKeyword(event.target.value);
            setPageNo(1);
          }}
        />
        <select
          value={status}
          onChange={(event) => {
            setStatus(event.target.value);
            setPageNo(1);
          }}
        >
          <option value="">全部状态</option>
          <option value="ENABLED">启用</option>
          <option value="DISABLED">禁用</option>
        </select>
        <button type="button" className="secondary" onClick={load}>
          刷新
        </button>
      </div>

      {message && <div className="success">{message}</div>}
      {error && <div className="error">{error}</div>}
      {loading && <div className="empty">正在加载...</div>}
      {!loading && (
        <>
          <table>
            <thead>
              <tr>
                <th>编码</th>
                <th>名称</th>
                {dataType === 'areas' && (
                  <>
                    <th>风险等级</th>
                    <th>重大危险源</th>
                  </>
                )}
                {dataType === 'units' && <th>区域 ID</th>}
                {dataType === 'equipments' && (
                  <>
                    <th>装置 ID</th>
                    <th>运行状态</th>
                  </>
                )}
                {dataType === 'monitor-points' && (
                  <>
                    <th>设备 ID</th>
                    <th>测点类型</th>
                    <th>位号</th>
                  </>
                )}
                <th>状态</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              {records.map((record) => (
                <tr key={record.id}>
                  <td>{record.code}</td>
                  <td>{record.name}</td>
                  {dataType === 'areas' && (
                    <>
                      <td>{record.riskLevel || '-'}</td>
                      <td>{record.majorHazardFlag ? '是' : '否'}</td>
                    </>
                  )}
                  {dataType === 'units' && <td>{record.areaId || '-'}</td>}
                  {dataType === 'equipments' && (
                    <>
                      <td>{record.unitId || '-'}</td>
                      <td>{record.runningStatus || '-'}</td>
                    </>
                  )}
                  {dataType === 'monitor-points' && (
                    <>
                      <td>{record.equipmentId || '-'}</td>
                      <td>{record.metricType || '-'}</td>
                      <td>{record.sourceTag || '-'}</td>
                    </>
                  )}
                  <td>
                    <StatusTag status={record.status} />
                  </td>
                  <td className="actions">
                    <button type="button" className="secondary" onClick={() => setEditing(record)}>
                      编辑
                    </button>
                    {record.status === 'ENABLED' ? (
                      <button
                        type="button"
                        className="warning"
                        onClick={() =>
                          confirmAction('确认禁用该记录？', () =>
                            run(() => disableBaseData(dataType, record.id, tenantId), '已禁用', load)
                          )
                        }
                      >
                        禁用
                      </button>
                    ) : (
                      <button
                        type="button"
                        className="secondary"
                        onClick={() => run(() => enableBaseData(dataType, record.id, tenantId), '已启用', load)}
                      >
                        启用
                      </button>
                    )}
                    <button
                      type="button"
                      className="danger"
                      onClick={() =>
                        confirmAction('删除后仅做软删除，确认删除？', () =>
                          run(() => deleteBaseData(dataType, record.id, tenantId), '已删除', load)
                        )
                      }
                    >
                      删除
                    </button>
                  </td>
                </tr>
              ))}
              {records.length === 0 && (
                <tr>
                  <td colSpan={8} className="empty">
                    暂无数据
                  </td>
                </tr>
              )}
            </tbody>
          </table>
          <div className="pager">
            <span>
              共 {total} 条，第 {pageNo} / {totalPages} 页
            </span>
            <div>
              <button className="secondary" disabled={pageNo <= 1} onClick={() => setPageNo(pageNo - 1)}>
                上一页
              </button>
              <button className="secondary" disabled={pageNo >= totalPages} onClick={() => setPageNo(pageNo + 1)}>
                下一页
              </button>
            </div>
          </div>
        </>
      )}

      {editing && (
        <BaseDataFormDialog
          tenantId={tenantId}
          dataType={dataType}
          meta={meta}
          record={editing === 'new' ? null : editing}
          onClose={() => setEditing(null)}
          onSaved={async () => {
            setEditing(null);
            await load();
          }}
          onError={setError}
        />
      )}
    </section>
  );
}

function BaseDataFormDialog({
  tenantId,
  dataType,
  meta,
  record,
  onClose,
  onSaved,
  onError
}: {
  tenantId: number;
  dataType: BaseDataType;
  meta: BaseDataMeta;
  record: BaseDataRecord | null;
  onClose: () => void;
  onSaved: () => Promise<void>;
  onError: (error: string) => void;
}) {
  const [form, setForm] = React.useState<BaseDataRequest>(buildInitialForm(dataType, tenantId, record));
  const [saving, setSaving] = React.useState<boolean>(false);

  async function submit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSaving(true);
    onError('');
    try {
      if (record) {
        await updateBaseData(dataType, record.id, form);
      } else {
        await createBaseData(dataType, form);
      }
      await onSaved();
    } catch (err: unknown) {
      onError(errorMessage(err, '保存失败'));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="modal-backdrop">
      <form className="modal" onSubmit={submit}>
        <header>
          <h2>
            {record ? '编辑' : '新增'}
            {meta.title}
          </h2>
          <button type="button" className="icon-button" onClick={onClose}>
            ×
          </button>
        </header>
        <div className="form-grid">
          <label>
            <span>{meta.codeLabel}</span>
            <input
              value={form.code || ''}
              disabled={Boolean(record)}
              onChange={(event) => setForm({ ...form, code: event.target.value })}
              required
            />
          </label>
          <label>
            <span>{meta.nameLabel}</span>
            <input value={form.name} onChange={(event) => setForm({ ...form, name: event.target.value })} required />
          </label>
          {dataType === 'areas' && (
            <>
              <label>
                <span>区域类型</span>
                <input value={form.type || ''} onChange={(event) => setForm({ ...form, type: event.target.value })} />
              </label>
              <label>
                <span>风险等级</span>
                <select
                  value={form.riskLevel || ''}
                  onChange={(event) => setForm({ ...form, riskLevel: event.target.value })}
                >
                  <option value="">未设置</option>
                  <option value="LOW">低</option>
                  <option value="MEDIUM">中</option>
                  <option value="HIGH">高</option>
                  <option value="MAJOR">重大</option>
                </select>
              </label>
              <label className="checkbox-row">
                <input
                  type="checkbox"
                  checked={Boolean(form.majorHazardFlag)}
                  onChange={(event) => setForm({ ...form, majorHazardFlag: event.target.checked })}
                />
                <span>重大危险源区域</span>
              </label>
            </>
          )}
          {dataType === 'units' && (
            <label>
              <span>所属区域 ID</span>
              <input
                type="number"
                value={form.areaId ?? ''}
                onChange={(event) => setForm({ ...form, areaId: Number(event.target.value) || undefined })}
              />
            </label>
          )}
          {dataType === 'equipments' && (
            <>
              <label>
                <span>所属装置 ID</span>
                <input
                  type="number"
                  value={form.unitId ?? ''}
                  onChange={(event) => setForm({ ...form, unitId: Number(event.target.value) || undefined })}
                />
              </label>
              <label>
                <span>运行状态</span>
                <input
                  value={form.runningStatus || ''}
                  onChange={(event) => setForm({ ...form, runningStatus: event.target.value })}
                />
              </label>
            </>
          )}
          {dataType === 'monitor-points' && (
            <>
              <label>
                <span>所属设备 ID</span>
                <input
                  type="number"
                  value={form.equipmentId ?? ''}
                  onChange={(event) => setForm({ ...form, equipmentId: Number(event.target.value) || undefined })}
                />
              </label>
              <label>
                <span>测点类型</span>
                <input
                  value={form.metricType || ''}
                  onChange={(event) => setForm({ ...form, metricType: event.target.value })}
                />
              </label>
              <label>
                <span>位号</span>
                <input
                  value={form.sourceTag || ''}
                  onChange={(event) => setForm({ ...form, sourceTag: event.target.value })}
                />
              </label>
            </>
          )}
          <label>
            <span>状态</span>
            <select value={form.status || 'ENABLED'} onChange={(event) => setForm({ ...form, status: event.target.value })}>
              <option value="ENABLED">启用</option>
              <option value="DISABLED">禁用</option>
            </select>
          </label>
        </div>
        <footer>
          <button type="button" className="secondary" onClick={onClose}>
            取消
          </button>
          <button type="submit" disabled={saving}>
            {saving ? '保存中...' : '保存'}
          </button>
        </footer>
      </form>
    </div>
  );
}

function buildInitialForm(dataType: BaseDataType, tenantId: number, record: BaseDataRecord | null): BaseDataRequest {
  return {
    tenantId,
    code: record?.code || '',
    name: record?.name || '',
    type: record?.type || '',
    riskLevel: record?.riskLevel || '',
    majorHazardFlag: Boolean(record?.majorHazardFlag),
    areaId: record?.areaId,
    unitId: record?.unitId,
    equipmentId: record?.equipmentId,
    runningStatus: record?.runningStatus || '',
    metricType: record?.metricType || '',
    sourceTag: record?.sourceTag || '',
    status: record?.status || 'ENABLED'
  };
}

export function OrgPanel({ tenantId }: { tenantId: number }) {
  const [tree, setTree] = React.useState<OrgTreeNode[]>([]);
  const [loading, setLoading] = React.useState<boolean>(true);
  const [editing, setEditing] = React.useState<OrgTreeNode | null | 'new'>(null);
  const [parentId, setParentId] = React.useState<number | undefined>(undefined);
  const { message, error, setError, run } = useAsyncAction();

  const load = React.useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      setTree(await fetchOrgTree(tenantId));
    } catch (err: unknown) {
      setError(errorMessage(err, '组织树加载失败'));
    } finally {
      setLoading(false);
    }
  }, [tenantId, setError]);

  React.useEffect(() => {
    load();
  }, [load]);

  return (
    <section className="content-panel">
      <div className="panel-header">
        <div>
          <h2>组织管理</h2>
          <p>维护企业/厂区/车间组织树，支持新增子级与删除。</p>
        </div>
        <button
          type="button"
          onClick={() => {
            setParentId(undefined);
            setEditing('new');
          }}
        >
          新增根组织
        </button>
      </div>
      {message && <div className="success">{message}</div>}
      {error && <div className="error">{error}</div>}
      {loading && <div className="empty">正在加载...</div>}
      {!loading && (
        <table>
          <thead>
            <tr>
              <th>组织名称</th>
              <th>编码</th>
              <th>类型</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <OrgTreeRows
              nodes={tree}
              onAddChild={(node) => {
                setParentId(node.id);
                setEditing('new');
              }}
              onEdit={(node) => setEditing(node)}
              onDelete={(node) =>
                confirmAction('确认删除该组织？', () =>
                  run(() => deleteOrg(node.id, tenantId), '组织已删除', load)
                )
              }
            />
          </tbody>
        </table>
      )}
      {editing && (
        <OrgFormDialog
          tenantId={tenantId}
          record={editing === 'new' ? null : editing}
          parentId={parentId}
          onClose={() => setEditing(null)}
          onSaved={async () => {
            setEditing(null);
            await load();
          }}
          onError={setError}
        />
      )}
    </section>
  );
}

function OrgTreeRows({
  nodes,
  depth = 0,
  onAddChild,
  onEdit,
  onDelete
}: {
  nodes: OrgTreeNode[];
  depth?: number;
  onAddChild: (node: OrgTreeNode) => void;
  onEdit: (node: OrgTreeNode) => void;
  onDelete: (node: OrgTreeNode) => void;
}) {
  return (
    <>
      {nodes.map((node) => (
        <React.Fragment key={node.id}>
          <tr>
            <td style={{ paddingLeft: `${depth * 20 + 12}px` }}>{node.orgName}</td>
            <td>{node.orgCode}</td>
            <td>{node.orgType}</td>
            <td>
              <StatusTag status={node.status} />
            </td>
            <td className="actions">
              <button type="button" className="secondary" onClick={() => onAddChild(node)}>
                子级
              </button>
              <button type="button" className="secondary" onClick={() => onEdit(node)}>
                编辑
              </button>
              <button type="button" className="danger" onClick={() => onDelete(node)}>
                删除
              </button>
            </td>
          </tr>
          {node.children && node.children.length > 0 && (
            <OrgTreeRows nodes={node.children} depth={depth + 1} onAddChild={onAddChild} onEdit={onEdit} onDelete={onDelete} />
          )}
        </React.Fragment>
      ))}
    </>
  );
}

function OrgFormDialog({
  tenantId,
  record,
  parentId,
  onClose,
  onSaved,
  onError
}: {
  tenantId: number;
  record: OrgTreeNode | null;
  parentId?: number;
  onClose: () => void;
  onSaved: () => Promise<void>;
  onError: (error: string) => void;
}) {
  const [form, setForm] = React.useState<OrgRequest>({
    tenantId,
    parentId: record?.parentId ?? parentId,
    orgCode: record?.orgCode || '',
    orgName: record?.orgName || '',
    orgType: record?.orgType || 'DEPARTMENT',
    sortOrder: record?.sortOrder || 0,
    status: record?.status || 'ENABLED'
  });
  const [saving, setSaving] = React.useState<boolean>(false);

  async function submit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSaving(true);
    onError('');
    try {
      if (record) {
        await updateOrg(record.id, form);
      } else {
        await createOrg(form);
      }
      await onSaved();
    } catch (err: unknown) {
      onError(errorMessage(err, '保存失败'));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="modal-backdrop">
      <form className="modal" onSubmit={submit}>
        <header>
          <h2>{record ? '编辑组织' : '新增组织'}</h2>
          <button type="button" className="icon-button" onClick={onClose}>
            ×
          </button>
        </header>
        <div className="form-grid">
          <label>
            <span>组织编码</span>
            <input
              value={form.orgCode}
              disabled={Boolean(record)}
              onChange={(event) => setForm({ ...form, orgCode: event.target.value })}
              required
            />
          </label>
          <label>
            <span>组织名称</span>
            <input value={form.orgName} onChange={(event) => setForm({ ...form, orgName: event.target.value })} required />
          </label>
          <label>
            <span>组织类型</span>
            <select value={form.orgType} onChange={(event) => setForm({ ...form, orgType: event.target.value })}>
              <option value="ENTERPRISE">企业</option>
              <option value="SITE">厂区</option>
              <option value="DEPARTMENT">部门/车间</option>
            </select>
          </label>
          <label>
            <span>上级组织 ID</span>
            <input
              type="number"
              value={form.parentId ?? ''}
              onChange={(event) => setForm({ ...form, parentId: Number(event.target.value) || undefined })}
            />
          </label>
        </div>
        <footer>
          <button type="button" className="secondary" onClick={onClose}>
            取消
          </button>
          <button type="submit" disabled={saving}>
            {saving ? '保存中...' : '保存'}
          </button>
        </footer>
      </form>
    </div>
  );
}

export function UsersPanel({ tenantId }: { tenantId: number }) {
  const [records, setRecords] = React.useState<IamUserRecord[]>([]);
  const [roles, setRoles] = React.useState<RoleRecord[]>([]);
  const [keyword, setKeyword] = React.useState<string>('');
  const [pageNo, setPageNo] = React.useState<number>(1);
  const [total, setTotal] = React.useState<number>(0);
  const [loading, setLoading] = React.useState<boolean>(true);
  const [editing, setEditing] = React.useState<IamUserRecord | null | 'new'>(null);
  const { message, error, setError, run } = useAsyncAction();

  const load = React.useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const page = await fetchIamUsers({ tenantId, keyword, pageNo, pageSize: 10 });
      setRecords(page.records);
      setTotal(page.total);
      const rolePage = await fetchRoles({ tenantId, pageNo: 1, pageSize: 100 });
      setRoles(rolePage.records);
    } catch (err: unknown) {
      setError(errorMessage(err, '用户列表加载失败'));
    } finally {
      setLoading(false);
    }
  }, [tenantId, keyword, pageNo, setError]);

  React.useEffect(() => {
    load();
  }, [load]);

  const totalPages = Math.max(1, Math.ceil(total / 10));

  return (
    <section className="content-panel">
      <div className="panel-header">
        <div>
          <h2>用户管理</h2>
          <p>维护 IAM 用户资料、状态与角色绑定。</p>
        </div>
        <button type="button" onClick={() => setEditing('new')}>
          新增用户
        </button>
      </div>
      <div className="toolbar">
        <input
          placeholder="搜索用户名或姓名"
          value={keyword}
          onChange={(event) => {
            setKeyword(event.target.value);
            setPageNo(1);
          }}
        />
        <button type="button" className="secondary" onClick={load}>
          刷新
        </button>
      </div>
      {message && <div className="success">{message}</div>}
      {error && <div className="error">{error}</div>}
      {loading && <div className="empty">正在加载...</div>}
      {!loading && (
        <>
          <table>
            <thead>
              <tr>
                <th>用户名</th>
                <th>姓名</th>
                <th>手机</th>
                <th>状态</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              {records.map((record) => (
                <tr key={record.id}>
                  <td>{record.username}</td>
                  <td>{record.displayName}</td>
                  <td>{record.mobile || '-'}</td>
                  <td>
                    <StatusTag status={record.status} />
                  </td>
                  <td className="actions">
                    <button type="button" className="secondary" onClick={() => setEditing(record)}>
                      编辑
                    </button>
                    {record.status === 'ENABLED' ? (
                      <button
                        type="button"
                        className="warning"
                        onClick={() =>
                          run(() => updateIamUserStatus(record.id, tenantId, 'DISABLED'), '用户已禁用', load)
                        }
                      >
                        禁用
                      </button>
                    ) : (
                      <button
                        type="button"
                        className="secondary"
                        onClick={() => run(() => updateIamUserStatus(record.id, tenantId, 'ENABLED'), '用户已启用', load)}
                      >
                        启用
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          <div className="pager">
            <span>
              共 {total} 条，第 {pageNo} / {totalPages} 页
            </span>
            <div>
              <button className="secondary" disabled={pageNo <= 1} onClick={() => setPageNo(pageNo - 1)}>
                上一页
              </button>
              <button className="secondary" disabled={pageNo >= totalPages} onClick={() => setPageNo(pageNo + 1)}>
                下一页
              </button>
            </div>
          </div>
        </>
      )}
      {editing && (
        <UserFormDialog
          tenantId={tenantId}
          record={editing === 'new' ? null : editing}
          roles={roles}
          onClose={() => setEditing(null)}
          onSaved={load}
          onError={setError}
        />
      )}
    </section>
  );
}

function UserFormDialog({
  tenantId,
  record,
  roles,
  onClose,
  onSaved,
  onError
}: {
  tenantId: number;
  record: IamUserRecord | null;
  roles: RoleRecord[];
  onClose: () => void;
  onSaved: () => Promise<void>;
  onError: (error: string) => void;
}) {
  const [form, setForm] = React.useState<IamUserRequest>({
    tenantId,
    authUserId: record?.authUserId,
    username: record?.username || '',
    displayName: record?.displayName || '',
    mobile: record?.mobile || '',
    email: record?.email || '',
    orgId: record?.orgId,
    status: record?.status || 'ENABLED',
    accountType: record?.accountType || 'LOCAL'
  });
  const [roleIds, setRoleIds] = React.useState<number[]>([]);
  const [saving, setSaving] = React.useState<boolean>(false);

  async function submit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSaving(true);
    onError('');
    try {
      let saved: IamUserRecord;
      if (record) {
        saved = await updateIamUser(record.id, form);
      } else {
        saved = await createIamUser(form);
      }
      if (roleIds.length > 0) {
        await assignUserRoles(saved.id, { tenantId, roleIds });
      }
      await onSaved();
      onClose();
    } catch (err: unknown) {
      onError(errorMessage(err, '保存失败'));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="modal-backdrop">
      <form className="modal" onSubmit={submit}>
        <header>
          <h2>{record ? '编辑用户' : '新增用户'}</h2>
          <button type="button" className="icon-button" onClick={onClose}>
            ×
          </button>
        </header>
        <div className="form-grid">
          <label>
            <span>用户名</span>
            <input
              value={form.username}
              disabled={Boolean(record)}
              onChange={(event) => setForm({ ...form, username: event.target.value })}
              required
            />
          </label>
          <label>
            <span>姓名</span>
            <input
              value={form.displayName}
              onChange={(event) => setForm({ ...form, displayName: event.target.value })}
              required
            />
          </label>
          <label>
            <span>认证用户 ID</span>
            <input
              type="number"
              value={form.authUserId ?? ''}
              onChange={(event) => setForm({ ...form, authUserId: Number(event.target.value) || undefined })}
            />
          </label>
          <label>
            <span>手机</span>
            <input value={form.mobile || ''} onChange={(event) => setForm({ ...form, mobile: event.target.value })} />
          </label>
          <label className="wide-field">
            <span>绑定角色</span>
            <div className="checkbox-list">
              {roles.map((role) => (
                <label key={role.id} className="checkbox-row">
                  <input
                    type="checkbox"
                    checked={roleIds.includes(role.id)}
                    onChange={(event) => {
                      if (event.target.checked) {
                        setRoleIds([...roleIds, role.id]);
                      } else {
                        setRoleIds(roleIds.filter((id) => id !== role.id));
                      }
                    }}
                  />
                  <span>
                    {role.roleName} ({role.roleCode})
                  </span>
                </label>
              ))}
              {roles.length === 0 && <span className="empty">暂无角色，请先在角色管理创建。</span>}
            </div>
          </label>
        </div>
        <footer>
          <button type="button" className="secondary" onClick={onClose}>
            取消
          </button>
          <button type="submit" disabled={saving}>
            {saving ? '保存中...' : '保存'}
          </button>
        </footer>
      </form>
    </div>
  );
}

export function RolesPanel({ tenantId }: { tenantId: number }) {
  const [records, setRecords] = React.useState<RoleRecord[]>([]);
  const [keyword, setKeyword] = React.useState<string>('');
  const [pageNo, setPageNo] = React.useState<number>(1);
  const [total, setTotal] = React.useState<number>(0);
  const [loading, setLoading] = React.useState<boolean>(true);
  const [editing, setEditing] = React.useState<RoleRecord | null | 'new'>(null);
  const { message, error, setError, run } = useAsyncAction();

  const load = React.useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const page = await fetchRoles({ tenantId, keyword, pageNo, pageSize: 10 });
      setRecords(page.records);
      setTotal(page.total);
    } catch (err: unknown) {
      setError(errorMessage(err, '角色列表加载失败'));
    } finally {
      setLoading(false);
    }
  }, [tenantId, keyword, pageNo, setError]);

  React.useEffect(() => {
    load();
  }, [load]);

  const totalPages = Math.max(1, Math.ceil(total / 10));

  return (
    <section className="content-panel">
      <div className="panel-header">
        <div>
          <h2>角色管理</h2>
          <p>维护业务角色编码、名称与状态。</p>
        </div>
        <button type="button" onClick={() => setEditing('new')}>
          新增角色
        </button>
      </div>
      <div className="toolbar">
        <input
          placeholder="搜索角色编码或名称"
          value={keyword}
          onChange={(event) => {
            setKeyword(event.target.value);
            setPageNo(1);
          }}
        />
        <button type="button" className="secondary" onClick={load}>
          刷新
        </button>
      </div>
      {message && <div className="success">{message}</div>}
      {error && <div className="error">{error}</div>}
      {loading && <div className="empty">正在加载...</div>}
      {!loading && (
        <>
          <table>
            <thead>
              <tr>
                <th>角色编码</th>
                <th>角色名称</th>
                <th>类型</th>
                <th>状态</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              {records.map((record) => (
                <tr key={record.id}>
                  <td>{record.roleCode}</td>
                  <td>{record.roleName}</td>
                  <td>{record.roleType || '-'}</td>
                  <td>
                    <StatusTag status={record.status} />
                  </td>
                  <td className="actions">
                    <button type="button" className="secondary" onClick={() => setEditing(record)}>
                      编辑
                    </button>
                    <button
                      type="button"
                      className="danger"
                      onClick={() =>
                        confirmAction('确认删除该角色？', () => run(() => deleteRole(record.id, tenantId), '角色已删除', load))
                      }
                    >
                      删除
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          <div className="pager">
            <span>
              共 {total} 条，第 {pageNo} / {totalPages} 页
            </span>
            <div>
              <button className="secondary" disabled={pageNo <= 1} onClick={() => setPageNo(pageNo - 1)}>
                上一页
              </button>
              <button className="secondary" disabled={pageNo >= totalPages} onClick={() => setPageNo(pageNo + 1)}>
                下一页
              </button>
            </div>
          </div>
        </>
      )}
      {editing && (
        <RoleFormDialog
          tenantId={tenantId}
          record={editing === 'new' ? null : editing}
          onClose={() => setEditing(null)}
          onSaved={load}
          onError={setError}
        />
      )}
    </section>
  );
}

function RoleFormDialog({
  tenantId,
  record,
  onClose,
  onSaved,
  onError
}: {
  tenantId: number;
  record: RoleRecord | null;
  onClose: () => void;
  onSaved: () => Promise<void>;
  onError: (error: string) => void;
}) {
  const [form, setForm] = React.useState<RoleRequest>({
    tenantId,
    roleCode: record?.roleCode || '',
    roleName: record?.roleName || '',
    roleType: record?.roleType || 'BUSINESS',
    description: record?.description || '',
    status: record?.status || 'ENABLED'
  });
  const [saving, setSaving] = React.useState<boolean>(false);

  async function submit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSaving(true);
    onError('');
    try {
      if (record) {
        await updateRole(record.id, form);
      } else {
        await createRole(form);
      }
      await onSaved();
      onClose();
    } catch (err: unknown) {
      onError(errorMessage(err, '保存失败'));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="modal-backdrop">
      <form className="modal" onSubmit={submit}>
        <header>
          <h2>{record ? '编辑角色' : '新增角色'}</h2>
          <button type="button" className="icon-button" onClick={onClose}>
            ×
          </button>
        </header>
        <div className="form-grid">
          <label>
            <span>角色编码</span>
            <input
              value={form.roleCode}
              disabled={Boolean(record)}
              onChange={(event) => setForm({ ...form, roleCode: event.target.value })}
              required
            />
          </label>
          <label>
            <span>角色名称</span>
            <input value={form.roleName} onChange={(event) => setForm({ ...form, roleName: event.target.value })} required />
          </label>
          <label>
            <span>描述</span>
            <input
              value={form.description || ''}
              onChange={(event) => setForm({ ...form, description: event.target.value })}
            />
          </label>
        </div>
        <footer>
          <button type="button" className="secondary" onClick={onClose}>
            取消
          </button>
          <button type="submit" disabled={saving}>
            {saving ? '保存中...' : '保存'}
          </button>
        </footer>
      </form>
    </div>
  );
}

export function MenusPanel({ tenantId }: { tenantId: number }) {
  const [tree, setTree] = React.useState<MenuTreeNode[]>([]);
  const [loading, setLoading] = React.useState<boolean>(true);
  const [editing, setEditing] = React.useState<MenuTreeNode | null | 'new'>(null);
  const [parentId, setParentId] = React.useState<number | undefined>(undefined);
  const { message, error, setError, run } = useAsyncAction();

  const load = React.useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      setTree(await fetchMenuTree(tenantId));
    } catch (err: unknown) {
      setError(errorMessage(err, '菜单树加载失败'));
    } finally {
      setLoading(false);
    }
  }, [tenantId, setError]);

  React.useEffect(() => {
    load();
  }, [load]);

  return (
    <section className="content-panel">
      <div className="panel-header">
        <div>
          <h2>菜单资源</h2>
          <p>维护管理端菜单 routePath，与角色权限绑定后控制侧栏可见性。</p>
        </div>
        <button
          type="button"
          onClick={() => {
            setParentId(undefined);
            setEditing('new');
          }}
        >
          新增菜单
        </button>
      </div>
      {message && <div className="success">{message}</div>}
      {error && <div className="error">{error}</div>}
      {loading && <div className="empty">正在加载...</div>}
      {!loading && (
        <table>
          <thead>
            <tr>
              <th>菜单名称</th>
              <th>编码</th>
              <th>路由</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <MenuTreeRows
              nodes={tree}
              onAddChild={(node) => {
                setParentId(node.id);
                setEditing('new');
              }}
              onEdit={(node) => setEditing(node)}
              onDelete={(node) =>
                confirmAction('确认删除该菜单？', () => run(() => deleteMenu(node.id, tenantId), '菜单已删除', load))
              }
            />
          </tbody>
        </table>
      )}
      {editing && (
        <MenuFormDialog
          tenantId={tenantId}
          record={editing === 'new' ? null : editing}
          parentId={parentId}
          onClose={() => setEditing(null)}
          onSaved={load}
          onError={setError}
        />
      )}
    </section>
  );
}

function MenuTreeRows({
  nodes,
  depth = 0,
  onAddChild,
  onEdit,
  onDelete
}: {
  nodes: MenuTreeNode[];
  depth?: number;
  onAddChild: (node: MenuTreeNode) => void;
  onEdit: (node: MenuTreeNode) => void;
  onDelete: (node: MenuTreeNode) => void;
}) {
  return (
    <>
      {nodes.map((node) => (
        <React.Fragment key={node.id}>
          <tr>
            <td style={{ paddingLeft: `${depth * 20 + 12}px` }}>{node.resourceName}</td>
            <td>{node.resourceCode}</td>
            <td>{node.routePath || '-'}</td>
            <td>
              <StatusTag status={node.status} />
            </td>
            <td className="actions">
              <button type="button" className="secondary" onClick={() => onAddChild(node)}>
                子级
              </button>
              <button type="button" className="secondary" onClick={() => onEdit(node)}>
                编辑
              </button>
              <button type="button" className="danger" onClick={() => onDelete(node)}>
                删除
              </button>
            </td>
          </tr>
          {node.children && node.children.length > 0 && (
            <MenuTreeRows nodes={node.children} depth={depth + 1} onAddChild={onAddChild} onEdit={onEdit} onDelete={onDelete} />
          )}
        </React.Fragment>
      ))}
    </>
  );
}

function MenuFormDialog({
  tenantId,
  record,
  parentId,
  onClose,
  onSaved,
  onError
}: {
  tenantId: number;
  record: MenuTreeNode | null;
  parentId?: number;
  onClose: () => void;
  onSaved: () => Promise<void>;
  onError: (error: string) => void;
}) {
  const [form, setForm] = React.useState<MenuResourceRequest>({
    tenantId,
    parentId: record?.parentId ?? parentId,
    resourceType: record?.resourceType || 'MENU',
    resourceCode: record?.resourceCode || '',
    resourceName: record?.resourceName || '',
    routePath: record?.routePath || '',
    sortOrder: record?.sortOrder || 0,
    visible: record?.visible !== false,
    status: record?.status || 'ENABLED'
  });
  const [saving, setSaving] = React.useState<boolean>(false);

  async function submit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSaving(true);
    onError('');
    try {
      if (record) {
        await updateMenu(record.id, form);
      } else {
        await createMenu(form);
      }
      await onSaved();
      onClose();
    } catch (err: unknown) {
      onError(errorMessage(err, '保存失败'));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="modal-backdrop">
      <form className="modal" onSubmit={submit}>
        <header>
          <h2>{record ? '编辑菜单' : '新增菜单'}</h2>
          <button type="button" className="icon-button" onClick={onClose}>
            ×
          </button>
        </header>
        <div className="form-grid">
          <label>
            <span>资源编码</span>
            <input
              value={form.resourceCode}
              disabled={Boolean(record)}
              onChange={(event) => setForm({ ...form, resourceCode: event.target.value })}
              required
            />
          </label>
          <label>
            <span>菜单名称</span>
            <input
              value={form.resourceName}
              onChange={(event) => setForm({ ...form, resourceName: event.target.value })}
              required
            />
          </label>
          <label>
            <span>路由标识</span>
            <input
              value={form.routePath || ''}
              onChange={(event) => setForm({ ...form, routePath: event.target.value })}
              placeholder="如 master-data:areas"
            />
          </label>
          <label>
            <span>排序</span>
            <input
              type="number"
              value={form.sortOrder ?? 0}
              onChange={(event) => setForm({ ...form, sortOrder: Number(event.target.value) })}
            />
          </label>
        </div>
        <footer>
          <button type="button" className="secondary" onClick={onClose}>
            取消
          </button>
          <button type="submit" disabled={saving}>
            {saving ? '保存中...' : '保存'}
          </button>
        </footer>
      </form>
    </div>
  );
}
