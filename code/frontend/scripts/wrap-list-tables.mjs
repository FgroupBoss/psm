import fs from 'fs';
import path from 'path';

const root = path.resolve('apps/admin-web/src');
const files = [];

function walk(dir) {
  for (const name of fs.readdirSync(dir)) {
    const full = path.join(dir, name);
    if (fs.statSync(full).isDirectory()) walk(full);
    else if (name.endsWith('.tsx')) files.push(full);
  }
}

walk(root);

for (const file of files) {
  let src = fs.readFileSync(file, 'utf8');
  const original = src;

  src = src.replace(/<table className="data-table">/g, '<div className="psm-table-scroll"><table className="psm-data-table">');
  src = src.replace(/<table className='data-table'>/g, "<div className=\"psm-table-scroll\"><table className=\"psm-data-table\">");

  src = src.replace(/<table>/g, '<div className="psm-table-scroll"><table className="psm-data-table">');

  const closeCount = (src.match(/<\/table>/g) || []).length;
  let replaced = 0;
  src = src.replace(/<\/table>/g, () => {
    replaced += 1;
    return '</table></div>';
  });

  if (src !== original) {
    fs.writeFileSync(file, src, 'utf8');
    console.log('updated', path.relative(process.cwd(), file), 'tables:', closeCount);
  }
}
