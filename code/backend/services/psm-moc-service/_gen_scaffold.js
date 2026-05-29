const fs = require("fs");
const path = require("path");
const ROOT = "d:/project/demo/code/backend/services/psm-moc-service/src/main/java/com/fgroupboss/ai/psm/moc";
const TEST_ROOT = "d:/project/demo/code/backend/services/psm-moc-service/src/test/java/com/fgroupboss/ai/psm/moc";
function write(rel, content) {
  const full = path.join(ROOT, rel);
  fs.mkdirSync(path.dirname(full), { recursive: true });
  fs.writeFileSync(full, content, "utf8");
}
function writeTest(rel, content) {
  const full = path.join(TEST_ROOT, rel);
  fs.mkdirSync(path.dirname(full), { recursive: true });
  fs.writeFileSync(full, content, "utf8");
}
const files = {};
