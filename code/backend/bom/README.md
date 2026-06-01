# BOM (Bill of Materials)

**Layer 0** — 版本管理中心，最先构建。

| 模块 | 说明 |
|------|------|
| `psm-common-bom` | 统一管理所有 shared 模块版本，其他模块通过 `dependencyManagement` 导入 |

> BOM 模块不包含任何 Java 源码，仅在 `<dependencyManagement>` 中声明版本。
