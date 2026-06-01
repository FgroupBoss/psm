# 前端可访问性改造方案（WCAG 2.1 AA）

> 适用范围：`code/frontend`（admin-web 为主，mobile-site / dashboard-web 待迁移）  
> 浏览器目标：Chrome / Edge 最新版  
> 试点页面：`ContractorCompaniesPanel`（承包商单位）

---

## 改造总览

在 `@psm/ui` 包中建立可访问组件层，以「键盘全操作 + 读屏器语义 + 触屏友好」三合一为目标，对齐 **WCAG 2.1 AA** 成功标准。业务页面逐步从原生 `<table>` / `window.confirm` 迁移至统一组件。

**组件入口**：`code/frontend/packages/ui/src/`  
**全局 Provider**：`AppProviders`（Toast + Confirm）  
**试点文件**：`apps/admin-web/src/panels.tsx` → `ContractorCompaniesPanel`

---

## 改进项与方案对照表

| # | 改进项 | WCAG / 标准依据 | 方案 | 实现位置 |
|---|--------|----------------|------|----------|
| 1 | 键盘全操作 | 2.1.1 键盘、2.4.3 焦点顺序 | 所有交互控件使用原生 `<button>` / `<input>`；排序按钮、分页、多选框均可 Tab 聚焦；模态框 **focus trap** + Esc 关闭 | `useFocusTrap`、`ModalDialog`、`DataTable`、`ConfirmProvider` |
| 2 | 可见焦点环 | 2.4.7 焦点可见 | 全局 `:focus-visible` 3px 轮廓；鼠标点击不显示焦点环（`:focus:not(:focus-visible)`） | `a11y.css` |
| 3 | 跳过导航 | 2.4.1 绕过区块 | 页面顶部 **Skip Link**「跳转到主内容」，指向 `#main-content` | `main.tsx` Shell |
| 4 | 导航当前页 | 4.1.2 名称、角色、值 | 侧边栏激活项设置 `aria-current="page"`；`<nav aria-label="主导航">` | `main.tsx` |
| 5 | 读屏器表格语义 | 1.3.1 信息与关系 | `<caption>`（sr-only）、`<th scope="col">`、排序列 `aria-sort`、分页 `<nav aria-label>` | `DataTable` |
| 6 | 表头固定 | 1.4.10 重排（Reflow） | `thead th { position: sticky; top: 0 }` + 滚动容器 `max-height` | `DataTable` + `a11y.css` |
| 7 | 操作列固定右侧 | 同上 | 末列 `position: sticky; right: 0` + 阴影分隔 | `DataTable` + `a11y.css` |
| 8 | 列排序 | 4.1.2 | 可排序列使用 `<button class="psm-sort-btn">` + `aria-sort` 三态（ascending/descending/none） | `DataTable` |
| 9 | 分页 | 2.4.4 链接目的 | `<nav aria-label="…分页">` + 上一页/下一页 `aria-label` + 页码摘要文本 | `DataTable` |
| 10 | 多选 | 4.1.2 | 表头全选 + 行级 checkbox，`aria-label` 描述选择对象 | `DataTable` |
| 11 | 批量操作 | 3.3.4 错误预防 | 选中后显示批量工具栏 + **二次确认**；试点为「批量提交审核」（后端暂无 DELETE 接口，批量删除复用同一 `batchActions` + `ConfirmProvider` 模式） | `ContractorCompaniesPanel` |
| 12 | 空状态 | 3.3.2 标签或说明 | 无数据时 `role="status"` 提示 + **清除筛选**按钮（有活跃筛选时显示） | `DataTable` |
| 13 | Label 始终可见 | 3.3.2、1.3.1 | `FormField` 使用 `<label htmlFor>` 显式关联，不用 placeholder 替代 label | `FormField` |
| 14 | 实时校验 | 3.3.1 错误识别 | 输入时 / 失焦时校验；`aria-invalid` + `aria-describedby` 关联错误文案 | `FormField` |
| 15 | 明确错误提示 | 3.3.3 错误建议 | 错误区域 `role="alert"`；必填项 sr-only「（必填）」 | `FormField` |
| 16 | 提交防抖 | 3.3.6 错误预防 | `useDebouncedSubmit` 300ms 锁 + `aria-busy` loading 态 | `useDebouncedSubmit`、`ContractorCompanyFormModal` |
| 17 | Toast 通知 | 4.1.3 状态消息 | `role="status"`（成功/info）/ `role="alert"`（错误/警告）+ `aria-live` polite/assertive | `ToastProvider` |
| 18 | 危险操作确认 | 3.3.4 | 替代 `window.confirm`：`ConfirmProvider` + `role="alertdialog"` + focus trap | `ConfirmProvider`、`ContractorCompaniesPanel` |
| 19 | 长时间操作进度 | 1.4.13 悬停或聚焦内容 | 操作 >1s 显示 `role="progressbar"` + `aria-valuenow`；完成自动隐藏 | `useProgressAction`、`ProgressBar` |
| 20 | 触屏优化 | 2.5.5 目标尺寸 | 按钮最小 44×44px（触屏 48px）；`-webkit-overflow-scrolling: touch`；`touch-action: manipulation` | `a11y.css` `@media (pointer: coarse)` |
| 21 | 减少动画 | 2.3.3 动画触发 | `prefers-reduced-motion: reduce` 关闭过渡/动画 | `a11y.css` |
| 22 | 模态框可访问 | 2.4.3 | `role="dialog"`、`aria-modal="true"`、`aria-labelledby`、关闭按钮 `aria-label` | `ModalDialog` |
| 23 | 装饰性图标隐藏 | 1.1.1 非文本内容 | Font Awesome 图标 `aria-hidden="true"`（已有，继续沿用） | `main.tsx`、`login-view.tsx` |
| 24 | 语言声明 | 3.1.1 页面语言 | `index.html` 保持 `lang="zh-CN"` | 已有 |
| 25 | 树形导航键盘 | 2.1.1、4.1.2 | `role="tree"` + 方向键移动/展开收起；选中节点背景+轮廓；深度>3 横向滚动 | `AccessibleTree` |
| 26 | 树拖拽排序 | 3.3.4 | 可选拖动手柄 + 占位线 + Esc 取消提示（`role="status"`） | `AccessibleTree` `draggable` |
| 27 | 列表斑马纹/悬停 | 1.4.3 对比度 | 偶数行底色 + hover 高亮，`line-height: 1.4` | `psm-data-table` |
| 28 | 固定列 | 1.4.10 | 多选列 `sticky left`、操作列 `sticky right` | `psm-data-table` |
| 29 | 空状态插画 | 3.3.2 | `TableEmptyState` + 重置筛选按钮 | `TableEmptyState` |
| 30 | 骨架屏 | 4.1.3 | 加载时 3~5 行 `TableSkeleton` 占位 | `TableSkeleton` |
| 31 | 危险操作确认 | 3.3.4 | `showConfirm` 红色 alertdialog，替代 `window.confirm` | `confirmBridge` |
| 32 | 操作按钮触达 | 2.5.5 | 行内按钮最小 44px，`focus-visible` 轮廓 | `TableRowActions` |

---

## 组件 API 速查

### DataTable

```tsx
<DataTable
  caption="列表标题"           // sr-only caption
  columns={[...]}              // sortable + sortValue 支持客户端排序
  rows={records}
  selectable                   // 多选
  selectedIds={ids}
  onSelectionChange={setIds}
  batchActions={<button>…</button>}
  pageNo={1} pageSize={10} total={100}
  onPageChange={setPage}
  hasActiveFilters={bool}
  onClearFilters={fn}
  toolbar={<>筛选控件</>}
  rowActions={(row) => <>…</>}
/>
```

### FormField

```tsx
<FormField
  label="单位名称"             // 始终可见
  name="companyName"
  value={v}
  onChange={setV}
  required
  onChangeValidate={(v) => …}  // 实时校验
  error={externalError}        // 服务端错误
/>
```

### AccessibleTree

```tsx
<AccessibleTree
  nodes={tree}
  getId={(n) => n.id}
  getChildren={(n) => n.children}
  getLabel={(n) => n.orgName}
  ariaLabel="组织树"
  defaultExpandAll
  columns={[{ key: 'code', header: '编码', render: (n) => n.orgCode }]}
  rowActions={(n) => <button>编辑</button>}
  draggable={Boolean(onReorder)}
  onReorder={async ({ dragId, targetId, position }) => { /* 调排序 API */ }}
/>
```

键盘：`↑`/`↓` 移动焦点，`→` 展开或进入子级，`←` 收起或回到父级，`Enter` 选中。拖拽时按 `Esc` 取消。

### 反馈

```tsx
const toast = useToast();
toast.success('已保存');
toast.error('保存失败');

const { confirm } = useConfirm();
const ok = await confirm({ title: '…', message: '…', variant: 'danger' });

const { run, progress, visible } = useProgressAction(1000);
await run(async () => { /* 批量 API */ });
```

---

## 迁移路线图

| 阶段 | 范围 | 状态 |
|------|------|------|
| P0 | `@psm/ui` 组件库 + `ContractorCompaniesPanel` 试点 | ✅ 已完成 |
| P1 | `panels.tsx` 组织树/菜单树 → `AccessibleTree` | ✅ 已完成 |
| P1 | 全站列表 `psm-data-table` 样式/骨架屏/固定列 | ✅ 已完成 |
| P1 | `phase3-panels.tsx` `LedgerTable` → `DataTable` | ✅ 已完成 |
| P1 | 全站列表空状态插画 + 重置筛选 | ✅ 已完成 |
| P1 | `panels.tsx` 其余 Panel 迁移 `DataTable` | 待办（样式与空状态已全局生效） |
| P2 | `work-permit-panel.tsx` 多步骤表单 | 待办 |
| P2 | `login-view.tsx` 接入 `useDebouncedSubmit`（已有 a11y 基线） | 待办 |
| P3 | `mobile-site` 全屏表单 + 触屏优化 | 待办 |
| P3 | `eslint-plugin-jsx-a11y` + axe 自动化检测 | 待办 |

---

## 验收清单（试点页）

- [ ] 仅键盘 Tab/Enter/Space/Esc 可完成：筛选 → 排序 → 多选 → 批量提交 → 编辑保存 → 停权确认
- [ ] NVDA / VoiceOver 朗读表格列名、排序状态、选中数量、Toast 内容
- [ ] 触屏设备（或 Chrome DevTools 设备模拟）按钮可点区域 ≥ 44px
- [ ] Lighthouse Accessibility ≥ 90（承包商单位页）
- [ ] 空列表 + 有筛选条件时出现「清除筛选」

---

## 相关文件

| 文件 | 说明 |
|------|------|
| `packages/ui/src/components/DataTable.tsx` | 可访问表格 |
| `packages/ui/src/components/FormField.tsx` | 表单字段 |
| `packages/ui/src/components/ModalDialog.tsx` | 模态框 |
| `packages/ui/src/providers/ToastProvider.tsx` | Toast |
| `packages/ui/src/providers/ConfirmProvider.tsx` | 确认弹窗 |
| `packages/ui/src/components/ProgressBar.tsx` | 进度条 |
| `packages/ui/src/a11y.css` | 可访问性样式 |
| `packages/ui/src/components/AccessibleTree.tsx` | 可访问树（组织/菜单/风险单元） |
| `packages/ui/src/hooks/useTreeNavigation.ts` | 树键盘与展开状态 |
| `apps/admin-web/src/panels.tsx` | 试点页面 |
| `apps/admin-web/src/main.tsx` | Skip Link + AppProviders |
