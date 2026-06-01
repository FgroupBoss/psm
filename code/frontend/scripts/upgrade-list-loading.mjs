import fs from 'fs';
import path from 'path';

const targets = [
  'apps/admin-web/src/panels.tsx',
  'apps/admin-web/src/main.tsx',
  'apps/admin-web/src/work-permit-panel.tsx',
  'apps/admin-web/src/report-panel.tsx',
  'apps/admin-web/src/phase2-panels.tsx'
].map((p) => path.resolve(p));

const importLine = "import { ListTableFrame, TableEmptyState, TableSkeleton } from '@psm/ui';\n";

for (const file of targets) {
  if (!fs.existsSync(file)) continue;
  let src = fs.readFileSync(file, 'utf8');
  const original = src;

  if (!src.includes("from '@psm/ui'") && !src.includes('TableSkeleton')) {
    const reactImport = src.match(/^import React[^\n]*\n/m);
    if (reactImport) {
      src = src.replace(reactImport[0], reactImport[0] + importLine);
    }
  } else if (!src.includes('TableSkeleton') && src.includes("from '@psm/ui'")) {
    src = src.replace(
      /from '@psm\/ui';/,
      "from '@psm/ui';\nimport { ListTableFrame, TableEmptyState, TableSkeleton } from '@psm/ui';"
    );
  }

  src = src.replace(
    /\{loading && <div className="empty">正在加载\.\.\.<\/div>\}\s*\{!loading && \(\s*<>\s*<div className="psm-table-scroll"><table className="psm-data-table">/g,
    `<div className="psm-table-scroll">
          {loading ? (
            <TableSkeleton rows={4} columns={5} hasActions />
          ) : (
          <table className="psm-data-table">`
  );

  src = src.replace(
    /\{loading && <div className="empty">加载中…<\/div>\}\s*\{!loading && \(\s*<>\s*<div className="psm-table-scroll"><table className="psm-data-table">/g,
    `<div className="psm-table-scroll">
          {loading ? (
            <TableSkeleton rows={4} columns={5} hasActions />
          ) : (
          <table className="psm-data-table">`
  );

  src = src.replace(
    /\{loading && <div className="empty">加载中\.\.\.<\/div>\}\s*\{!loading && \(\s*<>\s*<div className="psm-table-scroll"><table className="psm-data-table">/g,
    `<div className="psm-table-scroll">
          {loading ? (
            <TableSkeleton rows={4} columns={5} hasActions />
          ) : (
          <table className="psm-data-table">`
  );

  src = src.replace(
    /<\/table><\/div>\s*<div className="pager">/g,
    `</table>
          )}
          </div>
          <div className="pager">`
  );

  src = src.replace(
    /<\/table><\/div>\s*<\/>\s*\)\}/g,
    `</table>
          )}
          </div>`
  );

  if (src !== original) {
    fs.writeFileSync(file, src, 'utf8');
    console.log('loading upgraded', path.basename(file));
  }
}
