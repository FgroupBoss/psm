# psm-file-service

文件中心。

## 能力（已实现）

-  multipart 上传、元数据查询、下载
- 本地磁盘存储（`psm.file.storage-root`）
- `file_object` 表记录 SHA256、业务关联

## 端口

- 默认 `18092`
- 数据库 `psm_file`

## API

- `POST /api/files/upload`
- `GET /api/files/{id}`
- `GET /api/files/{id}/download`
