import React from 'react';
import {
  approveContractorCompany,
  approveContractorWorker,
  assignUserRoles,
  bindMajorHazardPoint,
  blacklistContractorCompany,
  blacklistContractorWorker,
  checkWorkerEligibility,
  createBaseData,
  createCompanyQualification,
  createContractorCompany,
  createContractorWorker,
  createMajorHazard,
  createMajorHazardAttachment,
  createIamUser,
  createMenu,
  createOrg,
  createRole,
  createWorkerCertificate,
  createWorkerTraining,
  createWorkerViolation,
  deleteBaseData,
  deleteCompanyQualification,
  deleteMenu,
  deleteOrg,
  deleteRole,
  deleteMajorHazardAttachment,
  deleteWorkerCertificate,
  disableBaseData,
  enableBaseData,
  changeMajorHazardStatus,
  fetchBaseDataPage,
  fetchCompanyQualifications,
  fetchContractorCompanies,
  fetchContractorCompany,
  fetchContractorWorker,
  fetchContractorWorkers,
  fetchMajorHazard,
  fetchMajorHazardAttachments,
  fetchMajorHazardAlarms,
  fetchMajorHazardPoints,
  fetchMajorHazardResponsibilities,
  fetchMajorHazards,
  fetchIamUsers,
  fetchMenuTree,
  fetchOrgTree,
  fetchRoles,
  fetchWorkerCertificates,
  fetchWorkerTrainings,
  fetchWorkerViolations,
  publishMajorHazard,
  replaceMajorHazardResponsibilities,
  submitContractorCompany,
  submitContractorWorker,
  suspendContractorCompany,
  suspendContractorWorker,
  unbindMajorHazardPoint,
  updateBaseData,
  updateContractorCompany,
  updateContractorWorker,
  updateMajorHazard,
  updateIamUser,
  updateIamUserStatus,
  updateMenu,
  updateOrg,
  updateRole
} from '@psm/api-client';
import type {
  BaseDataRecord,
  BaseDataRequest,
  ContractorCompanyRecord,
  ContractorCompanyRequest,
  ContractorQualificationRecord,
  ContractorQualificationRequest,
  ContractorWorkerRecord,
  ContractorWorkerRequest,
  EligibilityCheckResult,
  HazardAttachmentRecord,
  HazardAttachmentRequest,
  HazardAlarmSummaryRecord,
  HazardPointRecord,
  HazardPointRequest,
  MajorHazardRecord,
  MajorHazardRequest,
  MajorHazardResponsibilityRecord,
  ResponsibilityRequest,
  IamUserRecord,
  IamUserRequest,
  MenuResourceRequest,
  MenuTreeNode,
  OrgRequest,
  OrgTreeNode,
  RoleRecord,
  RoleRequest,
  WorkerCertificateRecord,
  WorkerCertificateRequest,
  WorkerTrainingRecord,
  WorkerTrainingRequest,
  WorkerViolationRecord,
  WorkerViolationRequest
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

const COMPANY_STATUS_OPTIONS = [
  { value: '', label: '全部状态' },
  { value: 'DRAFT', label: '待提交' },
  { value: 'PENDING_REVIEW', label: '待审核' },
  { value: 'APPROVED', label: '已准入' },
  { value: 'REJECTED', label: '已退回' },
  { value: 'SUSPENDED', label: '已停权' },
  { value: 'BLACKLIST', label: '黑名单' }
];

function companyStatusLabel(status: string) {
  const item = COMPANY_STATUS_OPTIONS.find((opt) => opt.value === status);
  return item?.label || status;
}

export function ContractorCompaniesPanel({ tenantId }: { tenantId: number }) {
  const [records, setRecords] = React.useState<ContractorCompanyRecord[]>([]);
  const [keyword, setKeyword] = React.useState('');
  const [status, setStatus] = React.useState('');
  const [pageNo, setPageNo] = React.useState(1);
  const [total, setTotal] = React.useState(0);
  const [loading, setLoading] = React.useState(true);
  const [detail, setDetail] = React.useState<ContractorCompanyRecord | null>(null);
  const [editing, setEditing] = React.useState<ContractorCompanyRecord | null | 'new'>(null);
  const { message, error, setError, run } = useAsyncAction();

  const load = React.useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const page = await fetchContractorCompanies({ tenantId, keyword, status, pageNo, pageSize: 10 });
      setRecords(page.records);
      setTotal(page.total);
    } catch (err: unknown) {
      setError(errorMessage(err, '承包商单位加载失败'));
    } finally {
      setLoading(false);
    }
  }, [tenantId, keyword, status, pageNo, setError]);

  React.useEffect(() => {
    load();
  }, [load]);

  const totalPages = Math.max(1, Math.ceil(total / 10));

  const refreshDetail = async (id: number) => {
    const company = await fetchContractorCompany(id, tenantId);
    setDetail(company);
    await load();
  };

  return (
    <section className="content-panel">
      <div className="panel-header">
        <div>
          <h2>承包商单位</h2>
          <p>维护承包商单位档案，完成提交、审核、停权与黑名单管理。</p>
        </div>
        <button type="button" onClick={() => setEditing('new')}>
          新增单位
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
          {COMPANY_STATUS_OPTIONS.map((opt) => (
            <option key={opt.value || 'all'} value={opt.value}>
              {opt.label}
            </option>
          ))}
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
                <th>联系人</th>
                <th>状态</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              {records.map((record) => (
                <tr key={record.id}>
                  <td>{record.companyCode}</td>
                  <td>{record.companyName}</td>
                  <td>{record.contactName || '-'}</td>
                  <td>
                    <StatusTag status={record.status} />
                    <span className="muted"> {companyStatusLabel(record.status)}</span>
                  </td>
                  <td className="actions">
                    <button type="button" className="secondary" onClick={() => setDetail(record)}>
                      详情
                    </button>
                    {(record.status === 'DRAFT' || record.status === 'REJECTED') && (
                      <button type="button" className="secondary" onClick={() => setEditing(record)}>
                        编辑
                      </button>
                    )}
                    {(record.status === 'DRAFT' || record.status === 'REJECTED' || record.status === 'APPROVED') && (
                      <button
                        type="button"
                        onClick={() =>
                          confirmAction('确认提交审核？', () =>
                            run(() => submitContractorCompany(record.id, tenantId).then(() => undefined), '已提交', load)
                          )
                        }
                      >
                        提交
                      </button>
                    )}
                    {record.status === 'APPROVED' && (
                      <button
                        type="button"
                        className="warning"
                        onClick={() => {
                          const reason = window.prompt('停权原因');
                          if (!reason) {
                            return;
                          }
                          run(
                            () => suspendContractorCompany(record.id, tenantId, { reason }).then(() => undefined),
                            '已停权',
                            load
                          );
                        }}
                      >
                        停权
                      </button>
                    )}
                  </td>
                </tr>
              ))}
              {records.length === 0 && (
                <tr>
                  <td colSpan={5} className="empty">
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
            <button type="button" className="secondary" disabled={pageNo <= 1} onClick={() => setPageNo(pageNo - 1)}>
              上一页
            </button>
            <button
              type="button"
              className="secondary"
              disabled={pageNo >= totalPages}
              onClick={() => setPageNo(pageNo + 1)}
            >
              下一页
            </button>
          </div>
        </>
      )}

      {editing && (
        <ContractorCompanyFormModal
          tenantId={tenantId}
          record={editing === 'new' ? null : editing}
          onClose={() => setEditing(null)}
          onSaved={() => {
            setEditing(null);
            load();
          }}
        />
      )}

      {detail && (
        <ContractorCompanyDetailModal
          tenantId={tenantId}
          record={detail}
          onClose={() => setDetail(null)}
          onChanged={() => refreshDetail(detail.id)}
          run={run}
        />
      )}
    </section>
  );
}

function ContractorCompanyFormModal({
  tenantId,
  record,
  onClose,
  onSaved
}: {
  tenantId: number;
  record: ContractorCompanyRecord | null;
  onClose: () => void;
  onSaved: () => void;
}) {
  const [form, setForm] = React.useState<ContractorCompanyRequest>({
    tenantId,
    companyCode: record?.companyCode || '',
    companyName: record?.companyName || '',
    contactName: record?.contactName || '',
    contactPhone: record?.contactPhone || '',
    businessScope: record?.businessScope || '',
    remark: record?.remark || ''
  });
  const [saving, setSaving] = React.useState(false);
  const [error, setError] = React.useState('');

  async function submit(event: React.FormEvent) {
    event.preventDefault();
    setSaving(true);
    setError('');
    try {
      if (record) {
        await updateContractorCompany(record.id, form);
      } else {
        await createContractorCompany(form);
      }
      onSaved();
    } catch (err: unknown) {
      setError(errorMessage(err, '保存失败'));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="modal-backdrop">
      <form className="modal" onSubmit={submit}>
        <header>
          <h3>{record ? '编辑承包商单位' : '新增承包商单位'}</h3>
        </header>
        <div className="form-grid">
          <label>
            <span>单位编码</span>
            <input
              value={form.companyCode}
              onChange={(event) => setForm({ ...form, companyCode: event.target.value })}
              required
              disabled={!!record}
            />
          </label>
          <label>
            <span>单位名称</span>
            <input
              value={form.companyName}
              onChange={(event) => setForm({ ...form, companyName: event.target.value })}
              required
            />
          </label>
          <label>
            <span>联系人</span>
            <input value={form.contactName || ''} onChange={(event) => setForm({ ...form, contactName: event.target.value })} />
          </label>
          <label>
            <span>联系电话</span>
            <input
              value={form.contactPhone || ''}
              onChange={(event) => setForm({ ...form, contactPhone: event.target.value })}
            />
          </label>
          <label className="wide-field">
            <span>业务范围</span>
            <input
              value={form.businessScope || ''}
              onChange={(event) => setForm({ ...form, businessScope: event.target.value })}
            />
          </label>
        </div>
        {error && <div className="error inline-error">{error}</div>}
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

function ContractorCompanyDetailModal({
  tenantId,
  record,
  onClose,
  onChanged,
  run
}: {
  tenantId: number;
  record: ContractorCompanyRecord;
  onClose: () => void;
  onChanged: () => Promise<void>;
  run: (action: () => Promise<void>, success: string, reload?: () => Promise<void>) => Promise<void>;
}) {
  const [tab, setTab] = React.useState<'info' | 'qual' | 'workers'>('info');
  const [company, setCompany] = React.useState(record);
  const [quals, setQuals] = React.useState<ContractorQualificationRecord[]>([]);
  const [qualForm, setQualForm] = React.useState<ContractorQualificationRequest | null>(null);

  const reload = React.useCallback(async () => {
    const latest = await fetchContractorCompany(record.id, tenantId);
    setCompany(latest);
    const list = await fetchCompanyQualifications(record.id, tenantId);
    setQuals(list);
    await onChanged();
  }, [record.id, tenantId, onChanged]);

  React.useEffect(() => {
    reload().catch(() => undefined);
  }, [reload]);

  return (
    <div className="modal-backdrop">
      <div className="modal modal-wide">
        <header>
          <h3>
            {company.companyName}（{company.companyCode}）
          </h3>
          <p>
            状态：<StatusTag status={company.status} /> {companyStatusLabel(company.status)}
          </p>
        </header>
        <div className="tab-bar">
          <button type="button" className={tab === 'info' ? 'active' : 'secondary'} onClick={() => setTab('info')}>
            基本信息
          </button>
          <button type="button" className={tab === 'qual' ? 'active' : 'secondary'} onClick={() => setTab('qual')}>
            资质证书
          </button>
          <button type="button" className={tab === 'workers' ? 'active' : 'secondary'} onClick={() => setTab('workers')}>
            人员
          </button>
        </div>
        {tab === 'info' && (
          <div className="detail-grid">
            <p>联系人：{company.contactName || '-'}</p>
            <p>电话：{company.contactPhone || '-'}</p>
            <p>业务范围：{company.businessScope || '-'}</p>
            <p>备注：{company.remark || '-'}</p>
            <div className="form-actions">
              {company.status === 'PENDING_REVIEW' && (
                <>
                  <button
                    type="button"
                    onClick={() =>
                      run(
                        () =>
                          approveContractorCompany(company.id, tenantId, { passed: true, opinion: '审核通过' }).then(
                            () => undefined
                          ),
                        '审核通过',
                        reload
                      )
                    }
                  >
                    审核通过
                  </button>
                  <button
                    type="button"
                    className="warning"
                    onClick={() => {
                      const opinion = window.prompt('退回原因', '资料不全');
                      if (opinion === null) {
                        return;
                      }
                      run(
                        () =>
                          approveContractorCompany(company.id, tenantId, { passed: false, opinion: opinion || '退回' }).then(
                            () => undefined
                          ),
                        '已退回',
                        reload
                      );
                    }}
                  >
                    退回
                  </button>
                </>
              )}
              {(company.status === 'APPROVED' || company.status === 'SUSPENDED') && (
                <button
                  type="button"
                  className="danger"
                  onClick={() => {
                    const reason = window.prompt('拉黑原因');
                    if (!reason) {
                      return;
                    }
                    run(
                      () => blacklistContractorCompany(company.id, tenantId, { reason }).then(() => undefined),
                      '已加入黑名单',
                      reload
                    );
                  }}
                >
                  加入黑名单
                </button>
              )}
            </div>
          </div>
        )}
        {tab === 'qual' && (
          <div>
            <div className="panel-header">
              <p>核心资质过期将在后续资格校验中拦截作业票选人。</p>
              <button
                type="button"
                onClick={() =>
                  setQualForm({
                    tenantId,
                    qualType: 'BUSINESS_LICENSE',
                    qualName: '',
                    coreFlag: true
                  })
                }
              >
                新增资质
              </button>
            </div>
            <table>
              <thead>
                <tr>
                  <th>类型</th>
                  <th>名称</th>
                  <th>有效期至</th>
                  <th>核心</th>
                  <th>状态</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                {quals.map((q) => (
                  <tr key={q.id}>
                    <td>{q.qualType}</td>
                    <td>{q.qualName}</td>
                    <td>{q.validTo || '-'}</td>
                    <td>{q.coreFlag ? '是' : '否'}</td>
                    <td>
                      {q.coreExpired ? <span className="error">核心已过期</span> : q.expired ? '已过期' : '有效'}
                    </td>
                    <td>
                      <button
                        type="button"
                        className="danger"
                        onClick={() =>
                          confirmAction('确认删除该资质？', () =>
                            run(() => deleteCompanyQualification(company.id, q.id, tenantId), '已删除', reload)
                          )
                        }
                      >
                        删除
                      </button>
                    </td>
                  </tr>
                ))}
                {quals.length === 0 && (
                  <tr>
                    <td colSpan={6} className="empty">
                      暂无资质
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
            {qualForm && (
              <ContractorQualificationForm
                companyId={company.id}
                form={qualForm}
                onChange={setQualForm}
                onCancel={() => setQualForm(null)}
                onSaved={() => {
                  setQualForm(null);
                  reload();
                }}
              />
            )}
          </div>
        )}
        {tab === 'workers' && (
          <ContractorCompanyWorkersTab tenantId={tenantId} companyId={company.id} />
        )}
        <footer>
          <button type="button" className="secondary" onClick={onClose}>
            关闭
          </button>
        </footer>
      </div>
    </div>
  );
}

function ContractorQualificationForm({
  companyId,
  form,
  onChange,
  onCancel,
  onSaved
}: {
  companyId: number;
  form: ContractorQualificationRequest;
  onChange: (value: ContractorQualificationRequest) => void;
  onCancel: () => void;
  onSaved: () => void;
}) {
  const [saving, setSaving] = React.useState(false);
  const [error, setError] = React.useState('');

  async function submit(event: React.FormEvent) {
    event.preventDefault();
    setSaving(true);
    setError('');
    try {
      await createCompanyQualification(companyId, form);
      onSaved();
    } catch (err: unknown) {
      setError(errorMessage(err, '保存资质失败'));
    } finally {
      setSaving(false);
    }
  }

  return (
    <form className="inline-form" onSubmit={submit}>
      <label>
        <span>类型</span>
        <input value={form.qualType} onChange={(e) => onChange({ ...form, qualType: e.target.value })} required />
      </label>
      <label>
        <span>名称</span>
        <input value={form.qualName} onChange={(e) => onChange({ ...form, qualName: e.target.value })} required />
      </label>
      <label>
        <span>有效期至</span>
        <input type="date" value={form.validTo || ''} onChange={(e) => onChange({ ...form, validTo: e.target.value })} />
      </label>
      <label>
        <span>核心资质</span>
        <input
          type="checkbox"
          checked={!!form.coreFlag}
          onChange={(e) => onChange({ ...form, coreFlag: e.target.checked })}
        />
      </label>
      {error && <div className="error inline-error">{error}</div>}
      <button type="button" className="secondary" onClick={onCancel}>
        取消
      </button>
      <button type="submit" disabled={saving}>
        保存
      </button>
    </form>
  );
}

const WORKER_STATUS_OPTIONS = [
  { value: '', label: '全部状态' },
  { value: 'INCOMPLETE', label: '待完善' },
  { value: 'PENDING_REVIEW', label: '待审核' },
  { value: 'APPROVED', label: '已准入' },
  { value: 'RESTRICTED', label: '受限准入' },
  { value: 'REJECTED', label: '已退回' },
  { value: 'SUSPENDED', label: '已停权' },
  { value: 'BLACKLIST', label: '黑名单' }
];

function workerStatusLabel(status: string) {
  const item = WORKER_STATUS_OPTIONS.find((opt) => opt.value === status);
  return item?.label || status;
}

function complianceLabel(value?: string) {
  if (value === 'VALID') return '有效';
  if (value === 'EXPIRED') return '已过期';
  if (value === 'EXPIRING') return '即将过期';
  if (value === 'INVALID') return '无效';
  if (value === 'MISSING') return '缺失';
  return value || '-';
}

export function ContractorWorkersPanel({ tenantId }: { tenantId: number }) {
  const [records, setRecords] = React.useState<ContractorWorkerRecord[]>([]);
  const [companies, setCompanies] = React.useState<ContractorCompanyRecord[]>([]);
  const [keyword, setKeyword] = React.useState('');
  const [accessStatus, setAccessStatus] = React.useState('');
  const [companyId, setCompanyId] = React.useState('');
  const [pageNo, setPageNo] = React.useState(1);
  const [total, setTotal] = React.useState(0);
  const [loading, setLoading] = React.useState(true);
  const [detail, setDetail] = React.useState<ContractorWorkerRecord | null>(null);
  const [editing, setEditing] = React.useState<ContractorWorkerRecord | null | 'new'>(null);
  const { message, error, setError, run } = useAsyncAction();

  React.useEffect(() => {
    fetchContractorCompanies({ tenantId, pageNo: 1, pageSize: 100 })
      .then((page) => setCompanies(page.records))
      .catch(() => undefined);
  }, [tenantId]);

  const load = React.useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const page = await fetchContractorWorkers({
        tenantId,
        keyword,
        accessStatus,
        companyId: companyId ? Number(companyId) : undefined,
        pageNo,
        pageSize: 10
      });
      setRecords(page.records);
      setTotal(page.total);
    } catch (err: unknown) {
      setError(errorMessage(err, '承包商人员加载失败'));
    } finally {
      setLoading(false);
    }
  }, [tenantId, keyword, accessStatus, companyId, pageNo, setError]);

  React.useEffect(() => {
    load();
  }, [load]);

  const totalPages = Math.max(1, Math.ceil(total / 10));

  return (
    <section className="content-panel">
      <div className="panel-header">
        <div>
          <h2>承包商人员</h2>
          <p>维护人员档案、证书、培训记录，并进行作业资格校验试算。</p>
        </div>
        <button type="button" onClick={() => setEditing('new')}>
          新增人员
        </button>
      </div>

      <div className="toolbar">
        <input
          placeholder="搜索编码或姓名"
          value={keyword}
          onChange={(event) => {
            setKeyword(event.target.value);
            setPageNo(1);
          }}
        />
        <select
          value={companyId}
          onChange={(event) => {
            setCompanyId(event.target.value);
            setPageNo(1);
          }}
        >
          <option value="">全部单位</option>
          {companies.map((company) => (
            <option key={company.id} value={company.id}>
              {company.companyName}
            </option>
          ))}
        </select>
        <select
          value={accessStatus}
          onChange={(event) => {
            setAccessStatus(event.target.value);
            setPageNo(1);
          }}
        >
          {WORKER_STATUS_OPTIONS.map((opt) => (
            <option key={opt.value || 'all'} value={opt.value}>
              {opt.label}
            </option>
          ))}
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
                <th>姓名</th>
                <th>工种</th>
                <th>准入状态</th>
                <th>证书</th>
                <th>培训</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              {records.map((record) => (
                <tr key={record.id}>
                  <td>{record.workerCode}</td>
                  <td>{record.name}</td>
                  <td>{record.tradeType || '-'}</td>
                  <td>
                    <StatusTag status={record.accessStatus} />
                    <span className="muted"> {workerStatusLabel(record.accessStatus)}</span>
                  </td>
                  <td>{complianceLabel(record.certificateStatus)}</td>
                  <td>{complianceLabel(record.trainingStatus)}</td>
                  <td className="actions">
                    <button type="button" className="secondary" onClick={() => setDetail(record)}>
                      详情
                    </button>
                  </td>
                </tr>
              ))}
              {records.length === 0 && (
                <tr>
                  <td colSpan={7} className="empty">
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
            <button type="button" className="secondary" disabled={pageNo <= 1} onClick={() => setPageNo(pageNo - 1)}>
              上一页
            </button>
            <button
              type="button"
              className="secondary"
              disabled={pageNo >= totalPages}
              onClick={() => setPageNo(pageNo + 1)}
            >
              下一页
            </button>
          </div>
        </>
      )}

      {editing && (
        <ContractorWorkerFormModal
          tenantId={tenantId}
          companies={companies}
          record={editing === 'new' ? null : editing}
          onClose={() => setEditing(null)}
          onSaved={() => {
            setEditing(null);
            load();
          }}
        />
      )}

      {detail && (
        <ContractorWorkerDetailModal
          tenantId={tenantId}
          record={detail}
          onClose={() => setDetail(null)}
          onChanged={load}
          run={run}
        />
      )}
    </section>
  );
}

function ContractorCompanyWorkersTab({ tenantId, companyId }: { tenantId: number; companyId: number }) {
  const [records, setRecords] = React.useState<ContractorWorkerRecord[]>([]);
  const [loading, setLoading] = React.useState(true);

  React.useEffect(() => {
    setLoading(true);
    fetchContractorWorkers({ tenantId, companyId, pageNo: 1, pageSize: 50 })
      .then((page) => setRecords(page.records))
      .catch(() => setRecords([]))
      .finally(() => setLoading(false));
  }, [tenantId, companyId]);

  if (loading) {
    return <div className="empty">正在加载人员...</div>;
  }

  return (
    <table>
      <thead>
        <tr>
          <th>编码</th>
          <th>姓名</th>
          <th>准入状态</th>
          <th>证书</th>
          <th>培训</th>
        </tr>
      </thead>
      <tbody>
        {records.map((record) => (
          <tr key={record.id}>
            <td>{record.workerCode}</td>
            <td>{record.name}</td>
            <td>{workerStatusLabel(record.accessStatus)}</td>
            <td>{complianceLabel(record.certificateStatus)}</td>
            <td>{complianceLabel(record.trainingStatus)}</td>
          </tr>
        ))}
        {records.length === 0 && (
          <tr>
            <td colSpan={5} className="empty">
              暂无人员
            </td>
          </tr>
        )}
      </tbody>
    </table>
  );
}

function ContractorWorkerFormModal({
  tenantId,
  companies,
  record,
  onClose,
  onSaved
}: {
  tenantId: number;
  companies: ContractorCompanyRecord[];
  record: ContractorWorkerRecord | null;
  onClose: () => void;
  onSaved: () => void;
}) {
  const [form, setForm] = React.useState<ContractorWorkerRequest>({
    tenantId,
    companyId: record?.companyId || companies[0]?.id || 0,
    workerCode: record?.workerCode || '',
    name: record?.name || '',
    phoneMasked: record?.phoneMasked,
    tradeType: record?.tradeType,
    gateCardNo: record?.gateCardNo,
    locationTagNo: record?.locationTagNo
  });
  const [saving, setSaving] = React.useState(false);
  const [error, setError] = React.useState('');

  async function submit(event: React.FormEvent) {
    event.preventDefault();
    setSaving(true);
    setError('');
    try {
      if (record) {
        await updateContractorWorker(record.id, form);
      } else {
        await createContractorWorker(form);
      }
      onSaved();
    } catch (err: unknown) {
      setError(errorMessage(err, '保存人员失败'));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="modal-backdrop">
      <div className="modal">
        <header>
          <h3>{record ? '编辑人员' : '新增人员'}</h3>
        </header>
        <form onSubmit={submit}>
          <label>
            <span>所属单位</span>
            <select
              value={form.companyId}
              onChange={(e) => setForm({ ...form, companyId: Number(e.target.value) })}
              required
            >
              {companies.map((company) => (
                <option key={company.id} value={company.id}>
                  {company.companyName}
                </option>
              ))}
            </select>
          </label>
          <label>
            <span>人员编码</span>
            <input value={form.workerCode} onChange={(e) => setForm({ ...form, workerCode: e.target.value })} required />
          </label>
          <label>
            <span>姓名</span>
            <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
          </label>
          <label>
            <span>工种</span>
            <input value={form.tradeType || ''} onChange={(e) => setForm({ ...form, tradeType: e.target.value })} />
          </label>
          <label>
            <span>手机号（脱敏）</span>
            <input
              value={form.phoneMasked || ''}
              onChange={(e) => setForm({ ...form, phoneMasked: e.target.value })}
            />
          </label>
          {error && <div className="error">{error}</div>}
          <footer>
            <button type="button" className="secondary" onClick={onClose}>
              取消
            </button>
            <button type="submit" disabled={saving}>
              保存
            </button>
          </footer>
        </form>
      </div>
    </div>
  );
}

function ContractorWorkerDetailModal({
  tenantId,
  record,
  onClose,
  onChanged,
  run
}: {
  tenantId: number;
  record: ContractorWorkerRecord;
  onClose: () => void;
  onChanged: () => Promise<void>;
  run: (action: () => Promise<void>, success: string, reload?: () => Promise<void>) => Promise<void>;
}) {
  const [tab, setTab] = React.useState<'info' | 'cert' | 'training' | 'violation' | 'check'>('info');
  const [worker, setWorker] = React.useState(record);
  const [certs, setCerts] = React.useState<WorkerCertificateRecord[]>([]);
  const [trainings, setTrainings] = React.useState<WorkerTrainingRecord[]>([]);
  const [violations, setViolations] = React.useState<WorkerViolationRecord[]>([]);
  const [checkResult, setCheckResult] = React.useState<EligibilityCheckResult | null>(null);
  const [certForm, setCertForm] = React.useState<WorkerCertificateRequest | null>(null);
  const [trainingForm, setTrainingForm] = React.useState<WorkerTrainingRequest | null>(null);

  const reload = React.useCallback(async () => {
    const latest = await fetchContractorWorker(record.id, tenantId);
    setWorker(latest);
    setCerts(await fetchWorkerCertificates(record.id, tenantId));
    setTrainings(await fetchWorkerTrainings(record.id, tenantId));
    setViolations(await fetchWorkerViolations(record.id, tenantId));
    await onChanged();
  }, [record.id, tenantId, onChanged]);

  React.useEffect(() => {
    reload().catch(() => undefined);
  }, [reload]);

  async function runEligibilityCheck() {
    const result = await checkWorkerEligibility({
      tenantId,
      companyId: worker.companyId,
      workerIds: [worker.id],
      workType: 'HOT_WORK',
      checkPoint: 'ADD_WORKER'
    });
    setCheckResult(result);
  }

  return (
    <div className="modal-backdrop">
      <div className="modal modal-wide">
        <header>
          <h3>
            {worker.name}（{worker.workerCode}）
          </h3>
          <p>
            准入：<StatusTag status={worker.accessStatus} /> {workerStatusLabel(worker.accessStatus)}
          </p>
        </header>
        <div className="tab-bar">
          {(['info', 'cert', 'training', 'violation', 'check'] as const).map((key) => (
            <button
              key={key}
              type="button"
              className={tab === key ? 'active' : 'secondary'}
              onClick={() => setTab(key)}
            >
              {key === 'info' && '基本信息'}
              {key === 'cert' && '证书'}
              {key === 'training' && '培训'}
              {key === 'violation' && '违章'}
              {key === 'check' && '资格试算'}
            </button>
          ))}
        </div>
        {tab === 'info' && (
          <div className="detail-grid">
            <p>工种：{worker.tradeType || '-'}</p>
            <p>证书状态：{complianceLabel(worker.certificateStatus)}</p>
            <p>培训状态：{complianceLabel(worker.trainingStatus)}</p>
            <div className="form-actions">
              {(worker.accessStatus === 'INCOMPLETE' || worker.accessStatus === 'REJECTED') && (
                <button
                  type="button"
                  onClick={() => run(() => submitContractorWorker(worker.id, tenantId).then(() => undefined), '已提交', reload)}
                >
                  提交审核
                </button>
              )}
              {worker.accessStatus === 'PENDING_REVIEW' && (
                <>
                  <button
                    type="button"
                    onClick={() =>
                      run(
                        () =>
                          approveContractorWorker(worker.id, tenantId, { passed: true, opinion: '审核通过' }).then(
                            () => undefined
                          ),
                        '审核通过',
                        reload
                      )
                    }
                  >
                    审核通过
                  </button>
                  <button
                    type="button"
                    className="warning"
                    onClick={() =>
                      run(
                        () =>
                          approveContractorWorker(worker.id, tenantId, { passed: false, opinion: '退回' }).then(
                            () => undefined
                          ),
                        '已退回',
                        reload
                      )
                    }
                  >
                    退回
                  </button>
                </>
              )}
              {(worker.accessStatus === 'APPROVED' || worker.accessStatus === 'RESTRICTED') && (
                <button
                  type="button"
                  className="warning"
                  onClick={() => {
                    const reason = window.prompt('停权原因');
                    if (!reason) return;
                    run(() => suspendContractorWorker(worker.id, tenantId, { reason }).then(() => undefined), '已停权', reload);
                  }}
                >
                  停权
                </button>
              )}
            </div>
          </div>
        )}
        {tab === 'cert' && (
          <div>
            <div className="panel-header">
              <button
                type="button"
                onClick={() => setCertForm({ tenantId, certType: 'SPECIAL_WELDER' })}
              >
                新增证书
              </button>
            </div>
            <table>
              <thead>
                <tr>
                  <th>类型</th>
                  <th>编号</th>
                  <th>有效期至</th>
                  <th>状态</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                {certs.map((cert) => (
                  <tr key={cert.id}>
                    <td>{cert.certType}</td>
                    <td>{cert.certNo || '-'}</td>
                    <td>{cert.validTo || '-'}</td>
                    <td>{cert.expired ? '已过期' : '有效'}</td>
                    <td>
                      <button
                        type="button"
                        className="danger"
                        onClick={() =>
                          confirmAction('确认删除该证书？', () =>
                            run(() => deleteWorkerCertificate(worker.id, cert.id, tenantId), '已删除', reload)
                          )
                        }
                      >
                        删除
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            {certForm && (
              <WorkerCertificateInlineForm
                workerId={worker.id}
                form={certForm}
                onCancel={() => setCertForm(null)}
                onSaved={() => {
                  setCertForm(null);
                  reload();
                }}
              />
            )}
          </div>
        )}
        {tab === 'training' && (
          <div>
            <div className="panel-header">
              <button
                type="button"
                onClick={() =>
                  setTrainingForm({ tenantId, trainingName: '入厂安全培训', trainingResult: 'PASSED' })
                }
              >
                新增培训
              </button>
            </div>
            <table>
              <thead>
                <tr>
                  <th>名称</th>
                  <th>结果</th>
                  <th>有效期至</th>
                  <th>状态</th>
                </tr>
              </thead>
              <tbody>
                {trainings.map((item) => (
                  <tr key={item.id}>
                    <td>{item.trainingName}</td>
                    <td>{item.trainingResult}</td>
                    <td>{item.validTo || '-'}</td>
                    <td>{item.valid ? '有效' : item.expired ? '已过期' : '无效'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
            {trainingForm && (
              <WorkerTrainingInlineForm
                workerId={worker.id}
                form={trainingForm}
                onCancel={() => setTrainingForm(null)}
                onSaved={() => {
                  setTrainingForm(null);
                  reload();
                }}
              />
            )}
          </div>
        )}
        {tab === 'violation' && (
          <div>
            <div className="panel-header">
              <button
                type="button"
                onClick={() =>
                  run(
                    () =>
                      createWorkerViolation(worker.id, {
                        tenantId,
                        violationTime: new Date().toISOString().slice(0, 19),
                        violationDesc: '现场违章（登记示例）',
                        severity: 'MINOR'
                      }).then(() => undefined),
                    '已登记违章',
                    reload
                  )
                }
              >
                登记违章
              </button>
            </div>
            <table>
              <thead>
                <tr>
                  <th>时间</th>
                  <th>描述</th>
                  <th>严重程度</th>
                </tr>
              </thead>
              <tbody>
                {violations.map((item) => (
                  <tr key={item.id}>
                    <td>{item.violationTime}</td>
                    <td>{item.violationDesc}</td>
                    <td>{item.severity || '-'}</td>
                  </tr>
                ))}
                {violations.length === 0 && (
                  <tr>
                    <td colSpan={3} className="empty">
                      暂无违章记录
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        )}
        {tab === 'check' && (
          <div>
            <p>模拟作业票添加人员时的资格校验（调试）。</p>
            <button type="button" onClick={runEligibilityCheck}>
              执行校验
            </button>
            {checkResult && (
              <div className={checkResult.passed ? 'success' : 'error'}>
                {checkResult.passed ? '校验通过' : '校验未通过'}
                {(checkResult.reasons || []).map((reason) => (
                  <p key={`${reason.code}-${reason.workerId || 0}`}>{reason.message}</p>
                ))}
              </div>
            )}
          </div>
        )}
        <footer>
          <button type="button" className="secondary" onClick={onClose}>
            关闭
          </button>
        </footer>
      </div>
    </div>
  );
}

function WorkerCertificateInlineForm({
  workerId,
  form,
  onCancel,
  onSaved
}: {
  workerId: number;
  form: WorkerCertificateRequest;
  onCancel: () => void;
  onSaved: () => void;
}) {
  const [value, setValue] = React.useState(form);
  const [saving, setSaving] = React.useState(false);
  const [error, setError] = React.useState('');

  async function submit(event: React.FormEvent) {
    event.preventDefault();
    setSaving(true);
    setError('');
    try {
      await createWorkerCertificate(workerId, value);
      onSaved();
    } catch (err: unknown) {
      setError(errorMessage(err, '保存证书失败'));
    } finally {
      setSaving(false);
    }
  }

  return (
    <form className="inline-form" onSubmit={submit}>
      <label>
        <span>类型</span>
        <input value={value.certType} onChange={(e) => setValue({ ...value, certType: e.target.value })} required />
      </label>
      <label>
        <span>编号</span>
        <input value={value.certNo || ''} onChange={(e) => setValue({ ...value, certNo: e.target.value })} />
      </label>
      <label>
        <span>有效期至</span>
        <input type="date" value={value.validTo || ''} onChange={(e) => setValue({ ...value, validTo: e.target.value })} />
      </label>
      {error && <div className="error inline-error">{error}</div>}
      <button type="button" className="secondary" onClick={onCancel}>
        取消
      </button>
      <button type="submit" disabled={saving}>
        保存
      </button>
    </form>
  );
}

function WorkerTrainingInlineForm({
  workerId,
  form,
  onCancel,
  onSaved
}: {
  workerId: number;
  form: WorkerTrainingRequest;
  onCancel: () => void;
  onSaved: () => void;
}) {
  const [value, setValue] = React.useState(form);
  const [saving, setSaving] = React.useState(false);
  const [error, setError] = React.useState('');

  async function submit(event: React.FormEvent) {
    event.preventDefault();
    setSaving(true);
    setError('');
    try {
      await createWorkerTraining(workerId, value);
      onSaved();
    } catch (err: unknown) {
      setError(errorMessage(err, '保存培训失败'));
    } finally {
      setSaving(false);
    }
  }

  return (
    <form className="inline-form" onSubmit={submit}>
      <label>
        <span>名称</span>
        <input
          value={value.trainingName}
          onChange={(e) => setValue({ ...value, trainingName: e.target.value })}
          required
        />
      </label>
      <label>
        <span>结果</span>
        <select value={value.trainingResult} onChange={(e) => setValue({ ...value, trainingResult: e.target.value })}>
          <option value="PASSED">合格</option>
          <option value="FAILED">不合格</option>
        </select>
      </label>
      <label>
        <span>有效期至</span>
        <input
          type="date"
          value={value.validTo || ''}
          onChange={(e) => setValue({ ...value, validTo: e.target.value })}
        />
      </label>
      {error && <div className="error inline-error">{error}</div>}
      <button type="button" className="secondary" onClick={onCancel}>
        取消
      </button>
      <button type="submit" disabled={saving}>
        保存
      </button>
    </form>
  );
}

const ATTACHMENT_TYPE_OPTIONS = [
  { value: 'EVAL_REPORT', label: '评估报告' },
  { value: 'FILING', label: '备案材料' },
  { value: 'SDS', label: 'SDS' },
  { value: 'PLAN', label: '应急预案' }
];

function attachmentTypeLabel(type: string) {
  const item = ATTACHMENT_TYPE_OPTIONS.find((opt) => opt.value === type);
  return item?.label || type;
}

const HAZARD_STATUS_OPTIONS = [
  { value: '', label: '全部状态' },
  { value: 'DRAFT', label: '草稿' },
  { value: 'PUBLISHED', label: '已发布' },
  { value: 'SUSPENDED', label: '停用' },
  { value: 'MAINTENANCE', label: '检修' },
  { value: 'ABNORMAL', label: '异常' }
];

const HAZARD_LEVEL_OPTIONS = [
  { value: '', label: '全部分级' },
  { value: 'LEVEL_1', label: '一级' },
  { value: 'LEVEL_2', label: '二级' },
  { value: 'LEVEL_3', label: '三级' },
  { value: 'LEVEL_4', label: '四级' }
];

const RESPONSIBILITY_TYPE_OPTIONS = [
  { value: 'PRIMARY', label: '主要负责人' },
  { value: 'TECHNICAL', label: '技术负责人' },
  { value: 'OPERATION', label: '操作负责人' }
];

function hazardStatusLabel(status: string) {
  const item = HAZARD_STATUS_OPTIONS.find((opt) => opt.value === status);
  return item?.label || status;
}

function hazardLevelLabel(level: string) {
  const item = HAZARD_LEVEL_OPTIONS.find((opt) => opt.value === level);
  return item?.label || level;
}

function responsibilityTypeLabel(type: string) {
  const item = RESPONSIBILITY_TYPE_OPTIONS.find((opt) => opt.value === type);
  return item?.label || type;
}

function defaultResponsibilityForms(): ResponsibilityRequest[] {
  return RESPONSIBILITY_TYPE_OPTIONS.map((item, index) => ({
    responsibilityType: item.value,
    personName: '',
    personPhone: '',
    sortNo: index + 1
  }));
}

function mergeResponsibilityForms(existing: MajorHazardResponsibilityRecord[]): ResponsibilityRequest[] {
  const defaults = defaultResponsibilityForms();
  return defaults.map((item) => {
    const found = existing.find((row) => row.responsibilityType === item.responsibilityType);
    if (!found) {
      return item;
    }
    return {
      responsibilityType: found.responsibilityType,
      personName: found.personName,
      personPhone: found.personPhone || '',
      personId: found.personId,
      sortNo: found.sortNo
    };
  });
}

export function MajorHazardsPanel({ tenantId }: { tenantId: number }) {
  const [records, setRecords] = React.useState<MajorHazardRecord[]>([]);
  const [keyword, setKeyword] = React.useState('');
  const [status, setStatus] = React.useState('');
  const [level, setLevel] = React.useState('');
  const [pageNo, setPageNo] = React.useState(1);
  const [total, setTotal] = React.useState(0);
  const [loading, setLoading] = React.useState(true);
  const [detail, setDetail] = React.useState<MajorHazardRecord | null>(null);
  const [editing, setEditing] = React.useState<MajorHazardRecord | null | 'new'>(null);
  const { message, error, setError, run } = useAsyncAction();

  const load = React.useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const page = await fetchMajorHazards({ tenantId, keyword, status, level, pageNo, pageSize: 10 });
      setRecords(page.records);
      setTotal(page.total);
    } catch (err: unknown) {
      setError(errorMessage(err, '危险源台账加载失败'));
    } finally {
      setLoading(false);
    }
  }, [tenantId, keyword, status, level, pageNo, setError]);

  React.useEffect(() => {
    load();
  }, [load]);

  const totalPages = Math.max(1, Math.ceil(total / 10));

  const refreshDetail = async (id: number) => {
    const hazard = await fetchMajorHazard(id, tenantId);
    setDetail(hazard);
    await load();
  };

  return (
    <section className="content-panel">
      <div className="panel-header">
        <div>
          <h2>危险源台账</h2>
          <p>维护重大危险源一源一档，完成区域绑定、包保责任人与发布。</p>
        </div>
        <button type="button" onClick={() => setEditing('new')}>
          新增危险源
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
          {HAZARD_STATUS_OPTIONS.map((opt) => (
            <option key={opt.value || 'all-status'} value={opt.value}>
              {opt.label}
            </option>
          ))}
        </select>
        <select
          value={level}
          onChange={(event) => {
            setLevel(event.target.value);
            setPageNo(1);
          }}
        >
          {HAZARD_LEVEL_OPTIONS.map((opt) => (
            <option key={opt.value || 'all-level'} value={opt.value}>
              {opt.label}
            </option>
          ))}
        </select>
        <button type="button" className="secondary" onClick={load}>
          刷新
        </button>
      </div>

      {message && <p className="success">{message}</p>}
      {error && <p className="error">{error}</p>}

      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>编码</th>
              <th>名称</th>
              <th>分级</th>
              <th>区域ID</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={6}>加载中...</td>
              </tr>
            ) : records.length === 0 ? (
              <tr>
                <td colSpan={6}>暂无数据</td>
              </tr>
            ) : (
              records.map((record) => (
                <tr key={record.id}>
                  <td>{record.hazardNo}</td>
                  <td>{record.name}</td>
                  <td>{hazardLevelLabel(record.level)}</td>
                  <td>{record.areaId || '-'}</td>
                  <td>
                    <StatusTag status={record.status} /> {hazardStatusLabel(record.status)}
                  </td>
                  <td>
                    <button type="button" className="link" onClick={() => setDetail(record)}>
                      详情
                    </button>
                    {record.status === 'DRAFT' && (
                      <button type="button" className="link" onClick={() => setEditing(record)}>
                        编辑
                      </button>
                    )}
                    {record.status === 'DRAFT' && (
                      <button
                        type="button"
                        className="link"
                        onClick={() =>
                          run(
                            () => publishMajorHazard(record.id, tenantId).then(() => undefined),
                            '发布成功',
                            load
                          )
                        }
                      >
                        发布
                      </button>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      <div className="pager">
        <button type="button" className="secondary" disabled={pageNo <= 1} onClick={() => setPageNo(pageNo - 1)}>
          上一页
        </button>
        <span>
          第 {pageNo} / {totalPages} 页，共 {total} 条
        </span>
        <button
          type="button"
          className="secondary"
          disabled={pageNo >= totalPages}
          onClick={() => setPageNo(pageNo + 1)}
        >
          下一页
        </button>
      </div>

      {editing && (
        <MajorHazardFormModal
          tenantId={tenantId}
          record={editing === 'new' ? null : editing}
          onClose={() => setEditing(null)}
          onSaved={async () => {
            setEditing(null);
            await load();
          }}
        />
      )}

      {detail && (
        <MajorHazardDetailModal
          tenantId={tenantId}
          record={detail}
          onClose={() => setDetail(null)}
          onChanged={() => refreshDetail(detail.id)}
          run={run}
        />
      )}
    </section>
  );
}

function MajorHazardFormModal({
  tenantId,
  record,
  onClose,
  onSaved
}: {
  tenantId: number;
  record: MajorHazardRecord | null;
  onClose: () => void;
  onSaved: () => Promise<void>;
}) {
  const [areas, setAreas] = React.useState<BaseDataRecord[]>([]);
  const [units, setUnits] = React.useState<BaseDataRecord[]>([]);
  const [form, setForm] = React.useState<MajorHazardRequest>({
    tenantId,
    hazardNo: record?.hazardNo || '',
    name: record?.name || '',
    hazardType: record?.hazardType || '',
    level: record?.level || 'LEVEL_1',
    areaId: record?.areaId,
    unitId: record?.unitId,
    material: record?.material || '',
    designCapacity: record?.designCapacity || '',
    actualCapacity: record?.actualCapacity || '',
    criticalQuantity: record?.criticalQuantity || ''
  });
  const [saving, setSaving] = React.useState(false);
  const [error, setError] = React.useState('');

  React.useEffect(() => {
    Promise.all([
      fetchBaseDataPage('areas', { tenantId, pageNo: 1, pageSize: 100, status: 'ENABLED' }),
      fetchBaseDataPage('units', { tenantId, pageNo: 1, pageSize: 100, status: 'ENABLED' })
    ])
      .then(([areaPage, unitPage]) => {
        setAreas(areaPage.records);
        setUnits(unitPage.records);
      })
      .catch(() => undefined);
  }, [tenantId]);

  const submit = async (event: React.FormEvent) => {
    event.preventDefault();
    setSaving(true);
    setError('');
    try {
      if (record) {
        await updateMajorHazard(record.id, form);
      } else {
        await createMajorHazard(form);
      }
      await onSaved();
    } catch (err: unknown) {
      setError(errorMessage(err, '保存失败'));
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="modal-backdrop">
      <div className="modal modal-wide">
        <header>
          <h3>{record ? '编辑危险源' : '新增危险源'}</h3>
        </header>
        <form className="form-grid" onSubmit={submit}>
          <label>
            危险源编码
            <input
              required
              value={form.hazardNo}
              onChange={(event) => setForm({ ...form, hazardNo: event.target.value })}
            />
          </label>
          <label>
            名称
            <input required value={form.name} onChange={(event) => setForm({ ...form, name: event.target.value })} />
          </label>
          <label>
            分级
            <select value={form.level} onChange={(event) => setForm({ ...form, level: event.target.value })}>
              {HAZARD_LEVEL_OPTIONS.filter((opt) => opt.value).map((opt) => (
                <option key={opt.value} value={opt.value}>
                  {opt.label}
                </option>
              ))}
            </select>
          </label>
          <label>
            类型
            <input value={form.hazardType || ''} onChange={(event) => setForm({ ...form, hazardType: event.target.value })} />
          </label>
          <label>
            关联区域
            <select
              value={form.areaId ?? ''}
              onChange={(event) => setForm({ ...form, areaId: Number(event.target.value) || undefined })}
            >
              <option value="">请选择区域</option>
              {areas.map((area) => (
                <option key={area.id} value={area.id}>
                  {area.code} - {area.name}
                </option>
              ))}
            </select>
          </label>
          <label>
            关联装置
            <select
              value={form.unitId ?? ''}
              onChange={(event) => setForm({ ...form, unitId: Number(event.target.value) || undefined })}
            >
              <option value="">请选择装置</option>
              {units.map((unit) => (
                <option key={unit.id} value={unit.id}>
                  {unit.code} - {unit.name}
                </option>
              ))}
            </select>
          </label>
          <label>
            主要介质
            <input value={form.material || ''} onChange={(event) => setForm({ ...form, material: event.target.value })} />
          </label>
          <label>
            设计容量
            <input
              value={form.designCapacity || ''}
              onChange={(event) => setForm({ ...form, designCapacity: event.target.value })}
            />
          </label>
          {error && <p className="error">{error}</p>}
          <div className="form-actions">
            <button type="button" className="secondary" onClick={onClose}>
              取消
            </button>
            <button type="submit" disabled={saving}>
              保存
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

function MajorHazardDetailModal({
  tenantId,
  record,
  onClose,
  onChanged,
  run
}: {
  tenantId: number;
  record: MajorHazardRecord;
  onClose: () => void;
  onChanged: () => Promise<void>;
  run: (action: () => Promise<void>, success: string, reload?: () => Promise<void>) => Promise<void>;
}) {
  const [tab, setTab] = React.useState<'info' | 'resp' | 'points' | 'attachments' | 'alarms' | 'permits'>('info');
  const [hazard, setHazard] = React.useState(record);
  const [responsibilities, setResponsibilities] = React.useState<ResponsibilityRequest[]>(defaultResponsibilityForms());
  const [points, setPoints] = React.useState<HazardPointRecord[]>([]);
  const [attachments, setAttachments] = React.useState<HazardAttachmentRecord[]>([]);
  const [alarms, setAlarms] = React.useState<HazardAlarmSummaryRecord[]>([]);
  const [monitorPoints, setMonitorPoints] = React.useState<BaseDataRecord[]>([]);
  const [selectedPointId, setSelectedPointId] = React.useState<number | ''>('');
  const [attachmentForm, setAttachmentForm] = React.useState<HazardAttachmentRequest>({
    attachmentType: 'EVAL_REPORT',
    fileId: 0,
    fileName: ''
  });
  const [savingResp, setSavingResp] = React.useState(false);

  const reload = React.useCallback(async () => {
    const latest = await fetchMajorHazard(record.id, tenantId);
    setHazard(latest);
    const resp = await fetchMajorHazardResponsibilities(record.id, tenantId);
    setResponsibilities(mergeResponsibilityForms(resp));
    const pointList = await fetchMajorHazardPoints(record.id, tenantId);
    setPoints(pointList);
    const attachmentList = await fetchMajorHazardAttachments(record.id, tenantId);
    setAttachments(attachmentList);
    const alarmList = await fetchMajorHazardAlarms(record.id, tenantId);
    setAlarms(alarmList);
    await onChanged();
  }, [record.id, tenantId, onChanged]);

  React.useEffect(() => {
    fetchBaseDataPage('monitor-points', { tenantId, pageNo: 1, pageSize: 200, status: 'ENABLED' })
      .then((page) => setMonitorPoints(page.records))
      .catch(() => undefined);
  }, [tenantId]);

  React.useEffect(() => {
    reload().catch(() => undefined);
  }, [reload]);

  const saveResponsibilities = async () => {
    setSavingResp(true);
    try {
      await replaceMajorHazardResponsibilities(hazard.id, tenantId, { responsibilities });
      await reload();
    } finally {
      setSavingResp(false);
    }
  };

  return (
    <div className="modal-backdrop">
      <div className="modal modal-wide">
        <header>
          <h3>
            {hazard.name}（{hazard.hazardNo}）
          </h3>
          <p>
            状态：<StatusTag status={hazard.status} /> {hazardStatusLabel(hazard.status)}
            {hazard.publishedAt ? ` · 发布时间 ${hazard.publishedAt}` : ''}
          </p>
        </header>
        <div className="tab-bar">
          <button type="button" className={tab === 'info' ? 'active' : 'secondary'} onClick={() => setTab('info')}>
            档案
          </button>
          <button type="button" className={tab === 'resp' ? 'active' : 'secondary'} onClick={() => setTab('resp')}>
            包保责任人
          </button>
          <button type="button" className={tab === 'points' ? 'active' : 'secondary'} onClick={() => setTab('points')}>
            监测点位
          </button>
          <button type="button" className={tab === 'attachments' ? 'active' : 'secondary'} onClick={() => setTab('attachments')}>
            附件资料
          </button>
          <button type="button" className={tab === 'alarms' ? 'active' : 'secondary'} onClick={() => setTab('alarms')}>
            关联报警
          </button>
          <button type="button" className={tab === 'permits' ? 'active' : 'secondary'} onClick={() => setTab('permits')}>
            关联作业
          </button>
        </div>

        {tab === 'info' && (
          <div className="detail-grid">
            <p>分级：{hazardLevelLabel(hazard.level)}</p>
            <p>类型：{hazard.hazardType || '-'}</p>
            <p>区域ID：{hazard.areaId || '-'}</p>
            <p>装置ID：{hazard.unitId || '-'}</p>
            <p>主要介质：{hazard.material || '-'}</p>
            <p>设计容量：{hazard.designCapacity || '-'}</p>
            <p>实际容量：{hazard.actualCapacity || '-'}</p>
            <div className="form-actions">
              {hazard.status === 'DRAFT' && (
                <button
                  type="button"
                  onClick={() =>
                    run(() => publishMajorHazard(hazard.id, tenantId).then(() => undefined), '发布成功', reload)
                  }
                >
                  发布
                </button>
              )}
              {hazard.status === 'PUBLISHED' && (
                <>
                  <button
                    type="button"
                    className="warning"
                    onClick={() => {
                      const reason = window.prompt('停用原因', '计划停用');
                      if (reason === null) {
                        return;
                      }
                      run(
                        () =>
                          changeMajorHazardStatus(hazard.id, tenantId, {
                            targetStatus: 'SUSPENDED',
                            reason: reason || '停用'
                          }).then(() => undefined),
                        '已停用',
                        reload
                      );
                    }}
                  >
                    停用
                  </button>
                  <button
                    type="button"
                    className="secondary"
                    onClick={() =>
                      run(
                        () =>
                          changeMajorHazardStatus(hazard.id, tenantId, { targetStatus: 'MAINTENANCE', reason: '检修' }).then(
                            () => undefined
                          ),
                        '已标记检修',
                        reload
                      )
                    }
                  >
                    检修
                  </button>
                </>
              )}
              {(hazard.status === 'SUSPENDED' || hazard.status === 'MAINTENANCE' || hazard.status === 'ABNORMAL') && (
                <button
                  type="button"
                  onClick={() =>
                    run(
                      () =>
                        changeMajorHazardStatus(hazard.id, tenantId, { targetStatus: 'PUBLISHED', reason: '恢复运行' }).then(
                          () => undefined
                        ),
                      '已恢复发布',
                      reload
                    )
                  }
                >
                  恢复发布
                </button>
              )}
            </div>
          </div>
        )}

        {tab === 'resp' && (
          <div className="detail-grid">
            {responsibilities.map((item, index) => (
              <div key={item.responsibilityType} className="form-grid">
                <h4>{responsibilityTypeLabel(item.responsibilityType)}</h4>
                <label>
                  姓名
                  <input
                    required
                    value={item.personName}
                    onChange={(event) => {
                      const next = [...responsibilities];
                      next[index] = { ...item, personName: event.target.value };
                      setResponsibilities(next);
                    }}
                  />
                </label>
                <label>
                  联系电话
                  <input
                    value={item.personPhone || ''}
                    onChange={(event) => {
                      const next = [...responsibilities];
                      next[index] = { ...item, personPhone: event.target.value };
                      setResponsibilities(next);
                    }}
                  />
                </label>
              </div>
            ))}
            <div className="form-actions">
              <button type="button" disabled={savingResp} onClick={saveResponsibilities}>
                保存责任人
              </button>
            </div>
          </div>
        )}

        {tab === 'points' && (
          <div className="detail-grid">
            <div className="toolbar">
              <select
                value={selectedPointId}
                onChange={(event) => setSelectedPointId(Number(event.target.value) || '')}
              >
                <option value="">选择启用监测点位</option>
                {monitorPoints.map((point) => (
                  <option key={point.id} value={point.id}>
                    {point.code} - {point.name}
                  </option>
                ))}
              </select>
              <button
                type="button"
                disabled={!selectedPointId}
                onClick={() => {
                  const point = monitorPoints.find((item) => item.id === selectedPointId);
                  if (!point) {
                    return;
                  }
                  const payload: HazardPointRequest = {
                    monitorPointId: point.id,
                    pointCode: point.code,
                    pointName: point.name
                  };
                  run(() => bindMajorHazardPoint(hazard.id, tenantId, payload).then(() => undefined), '绑定点位成功', reload);
                }}
              >
                绑定点位
              </button>
            </div>
            <table>
              <thead>
                <tr>
                  <th>点位ID</th>
                  <th>编码</th>
                  <th>名称</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                {points.length === 0 ? (
                  <tr>
                    <td colSpan={4}>暂无绑定点位</td>
                  </tr>
                ) : (
                  points.map((point) => (
                    <tr key={point.id}>
                      <td>{point.monitorPointId}</td>
                      <td>{point.pointCode || '-'}</td>
                      <td>{point.pointName || '-'}</td>
                      <td>
                        <button
                          type="button"
                          className="link danger"
                          onClick={() =>
                            run(
                              () => unbindMajorHazardPoint(hazard.id, tenantId, point.id).then(() => undefined),
                              '已解绑',
                              reload
                            )
                          }
                        >
                          解绑
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        )}

        {tab === 'attachments' && (
          <div className="detail-grid">
            <div className="form-grid">
              <label>
                附件分类
                <select
                  value={attachmentForm.attachmentType}
                  onChange={(event) => setAttachmentForm({ ...attachmentForm, attachmentType: event.target.value })}
                >
                  {ATTACHMENT_TYPE_OPTIONS.map((opt) => (
                    <option key={opt.value} value={opt.value}>
                      {opt.label}
                    </option>
                  ))}
                </select>
              </label>
              <label>
                文件ID（file-service 未就绪时可填 mock ID）
                <input
                  type="number"
                  min={1}
                  value={attachmentForm.fileId || ''}
                  onChange={(event) =>
                    setAttachmentForm({ ...attachmentForm, fileId: Number(event.target.value) || 0 })
                  }
                />
              </label>
              <label>
                文件名
                <input
                  value={attachmentForm.fileName || ''}
                  onChange={(event) => setAttachmentForm({ ...attachmentForm, fileName: event.target.value })}
                />
              </label>
              <div className="form-actions">
                <button
                  type="button"
                  disabled={!attachmentForm.fileId}
                  onClick={() =>
                    run(
                      () => createMajorHazardAttachment(hazard.id, tenantId, attachmentForm).then(() => undefined),
                      '附件已保存',
                      reload
                    )
                  }
                >
                  保存附件元数据
                </button>
              </div>
            </div>
            <table>
              <thead>
                <tr>
                  <th>分类</th>
                  <th>文件ID</th>
                  <th>文件名</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                {attachments.length === 0 ? (
                  <tr>
                    <td colSpan={4}>暂无附件</td>
                  </tr>
                ) : (
                  attachments.map((item) => (
                    <tr key={item.id}>
                      <td>{attachmentTypeLabel(item.attachmentType)}</td>
                      <td>{item.fileId}</td>
                      <td>{item.fileName || '-'}</td>
                      <td>
                        <button
                          type="button"
                          className="link danger"
                          onClick={() =>
                            run(
                              () => deleteMajorHazardAttachment(hazard.id, tenantId, item.id).then(() => undefined),
                              '已删除',
                              reload
                            )
                          }
                        >
                          删除
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        )}

        {tab === 'alarms' && (
          <div className="table-wrap">
            {alarms.length === 0 ? (
              <div className="empty-state">
                <p>暂无关联报警。</p>
              </div>
            ) : (
              <table>
                <thead>
                  <tr>
                    <th>编号</th>
                    <th>等级</th>
                    <th>状态</th>
                    <th>标题</th>
                    <th>次数</th>
                    <th>最近发生</th>
                  </tr>
                </thead>
                <tbody>
                  {alarms.map((item) => (
                    <tr key={item.id}>
                      <td>{item.alarmNo}</td>
                      <td>{item.alarmLevel}</td>
                      <td>
                        <StatusTag status={item.status} />
                      </td>
                      <td>{item.title}</td>
                      <td>{item.occurrenceCount ?? 1}</td>
                      <td>{item.lastOccurredAt || '-'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
            <p className="hint">完整处置请前往左侧「实时报警」菜单。</p>
          </div>
        )}

        {tab === 'permits' && (
          <div className="empty-state">
            <p>关联作业票将在第 4 迭代接入危险工作票后展示。</p>
          </div>
        )}

        <footer className="form-actions">
          <button type="button" className="secondary" onClick={onClose}>
            关闭
          </button>
        </footer>
      </div>
    </div>
  );
}
