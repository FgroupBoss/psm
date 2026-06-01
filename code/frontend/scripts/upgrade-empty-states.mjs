import fs from 'fs';
import path from 'path';

const files = [];
function walk(dir) {
  for (const name of fs.readdirSync(dir)) {
    const full = path.join(dir, name);
    if (fs.statSync(full).isDirectory()) walk(full);
    else if (name.endsWith('.tsx')) files.push(full);
  }
}
walk(path.resolve('apps/admin-web/src'));

const importLine = "import { TableEmptyRow, TableSkeleton } from '@psm/ui';\n";
const importMerge = /import \{([^}]+)\} from '@psm\/ui';/;

for (const file of files) {
  let src = fs.readFileSync(file, 'utf8');
  const original = src;

  if (src.includes('TableEmptyRow') || !src.includes('colSpan') && !src.includes('暂无') && !src.includes('加载中')) {
    continue;
  }

  if (src.includes("from '@psm/ui'")) {
    src = src.replace(importMerge, (_, inner) => {
      const parts = inner.split(',').map((s) => s.trim()).filter(Boolean);
      if (!parts.includes('TableEmptyRow')) parts.push('TableEmptyRow');
      if (!parts.includes('TableSkeleton') && (src.includes('TableSkeleton') || src.includes('加载中'))) {
        if (!parts.includes('TableSkeleton')) parts.push('TableSkeleton');
      }
      return `import { ${parts.join(', ')} } from '@psm/ui';`;
    });
  } else if (src.includes('colSpan') || src.match(/暂无|加载中\.\.\./)) {
    const reactImport = src.match(/^import React[^\n]*\n/m);
    if (reactImport) src = src.replace(reactImport[0], reactImport[0] + importLine);
  }

  // Simple empty cell -> TableEmptyRow (no filters)
  src = src.replace(
    /<tr>\s*<td colSpan=\{(\d+)\} className="empty">\s*([^<]+)\s*<\/td>\s*<\/tr>/g,
    '<TableEmptyRow colSpan={$1} message="$2" />'
  );
  src = src.replace(
    /<tr>\s*<td colSpan=\{(\d+)\}>暂无[^<]*<\/td>\s*<\/tr>/g,
    '<TableEmptyRow colSpan={$1} message="暂无数据" />'
  );
  src = src.replace(
    /<tr>\s*<td colSpan=\{(\d+)\} className="empty">([^<]+)<\/td>\s*<\/tr>/g,
    '<TableEmptyRow colSpan={$1} message="$2" />'
  );

  if (src !== original) {
    fs.writeFileSync(file, src, 'utf8');
    console.log('updated', path.relative(process.cwd(), file));
  }
}
