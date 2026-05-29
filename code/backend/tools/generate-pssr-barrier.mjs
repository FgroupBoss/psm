import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const ROOT = path.join(__dirname, '..', 'services');
const POM = fs.readFileSync(path.join(ROOT, 'psm-dual-prevention-service', 'pom.xml'), 'utf8');

const JAVA_TYPE = {
  bigint: 'Long', varchar: 'String', tinyint: 'Integer', int: 'Integer',
  datetime: 'LocalDateTime', decimal: 'java.math.BigDecimal', text: 'String',
};

function write(filePath, content) {
  fs.mkdirSync(path.dirname(filePath), { recursive: true });
  fs.writeFileSync(filePath, content.replace(/\r\n/g, '\n').replace(/\n/g, '\n'), 'utf8');
}

function fieldJava(col, typ) {
  const jt = JAVA_TYPE[typ.split('(')[0]] || 'String';
  const prop = col.replace(/_([a-z])/g, (_, c) => c.toUpperCase());
  return [jt, prop];
}

function genSchema(tables) {
  const lines = [];
  for (const [table, cls, fields, def] of tables) {
    const cols = [
      "  id bigint not null auto_increment comment '主键',",
      "  tenant_id bigint not null comment '租户ID',",
    ];
    for (const [col, typ, comment] of fields) {
      const nn = col === 'status' ? ' not null' : '';
      let dv = '';
      if (def && col === 'status') dv = ` default '${def}'`;
      else if (col.endsWith('_flag')) dv = ' default 0';
      cols.push(`  ${col} ${typ}${nn}${dv} comment '${comment}',`);
    }
    cols.push(
      "  created_at datetime not null default current_timestamp comment '创建时间',",
      "  updated_at datetime not null default current_timestamp on update current_timestamp on update current_timestamp comment '更新时间',".replace(' on update current_timestamp on update current_timestamp', ' on update current_timestamp'),
      "  deleted tinyint not null default 0 comment '删除标识',",
      "  primary key (id),",
      `  key idx_${table}_tenant (tenant_id)`,
      `) engine=InnoDB default charset=utf8mb4 comment='${cls}';`,
      ''
    );
    lines.push(`create table if not exists ${table} (\n${cols.join('\n')}`);
  }
  return lines.join('\n');
}

function genEntity(pkg, table, cls, fields) {
  const props = ['    @TableId(type = IdType.AUTO)\n    private Long id;', '    private Long tenantId;'];
  for (const [col, typ] of fields) {
    const [jt, prop] = fieldJava(col, typ);
    props.push(`    private ${jt} ${prop};`);
  }
  props.push('    private LocalDateTime createdAt;', '    private LocalDateTime updatedAt;', '    private Integer deleted;');
  return `package com.fgroupboss.ai.psm.${pkg}.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("${table}")
public class ${cls}Entity {
${props.join('\n')}
}
`;
}

function genMapper(pkg, cls) {
  return `package com.fgroupboss.ai.psm.${pkg}.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.${pkg}.model.entity.${cls}Entity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ${cls}Mapper extends BaseMapper<${cls}Entity> {
}
`;
}

function genVo(pkg, cls, fields) {
  const lines = [`package com.fgroupboss.ai.psm.${pkg}.model.vo;`, '', 'import lombok.Data;', 'import java.time.LocalDateTime;', 'import java.math.BigDecimal;', '', '@Data', `public class ${cls}VO {`, '    private Long id;', '    private Long tenantId;'];
  for (const [col, typ] of fields) {
    const [jt, prop] = fieldJava(col, typ);
    lines.push(`    private ${jt} ${prop};`);
  }
  lines.push('    private LocalDateTime createdAt;', '    private LocalDateTime updatedAt;', '}');
  return lines.join('\n') + '\n';
}

function genPom(artifact) {
  return POM.replace(/psm-dual-prevention-service/g, artifact)
    .replace('<name>psm-dual-prevention-service</name>', `<name>${artifact}</name>`);
}

function genYml(svc) {
  const env = svc.artifact.toUpperCase().replace(/-/g, '_');
  return `server:
  port: ${svc.port}

spring:
  application:
    name: ${svc.artifact}
  datasource:
    url: \${${env}_DB_URL:jdbc:mysql://localhost:3306/${svc.db}?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai}
    username: \${${env}_DB_USERNAME:psm}
    password: \${${env}_DB_PASSWORD:psm}
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
`;
}

function genApp(pkg, cls) {
  return `package com.fgroupboss.ai.psm.${pkg};

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.fgroupboss.ai.psm.${pkg}.mapper")
@SpringBootApplication(scanBasePackages = "com.fgroupboss.ai.psm")
public class ${cls} {

    public static void main(String[] args) {
        SpringApplication.run(${cls}.class, args);
    }
}
`;
}

function genHandler(pkg, label) {
  return `package com.fgroupboss.ai.psm.${pkg}.controller;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseVO<Void> handleBusinessException(BusinessException e) {
        return ResponseVO.failure(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseVO<Void> handleValidationException(MethodArgumentNotValidException e) {
        FieldError error = e.getBindingResult().getFieldError();
        String message = error == null ? "request is invalid" : error.getDefaultMessage();
        return ResponseVO.failure(400, message);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseVO<Void> handleDataAccessException(DataAccessException e) {
        log.error("${label} database operation failed", e);
        return ResponseVO.failure(500, "database operation failed");
    }

    @ExceptionHandler(Exception.class)
    public ResponseVO<Void> handleException(Exception e) {
        log.error("${label} unexpected error", e);
        return ResponseVO.failure(500, "internal server error");
    }
}
`;
}

function pj(svc, ...parts) {
  return path.join(ROOT, svc.artifact, 'src', 'main', 'java', 'com', 'fgroupboss', 'ai', 'psm', svc.pkg, ...parts);
}

function tj(svc, ...parts) {
  return path.join(ROOT, svc.artifact, 'src', 'test', 'java', 'com', 'fgroupboss', 'ai', 'psm', svc.pkg, ...parts);
}

const PSSR_TABLES = [
  ['pssr_project', 'PssrProject', [
    ['pssr_no', 'varchar(64)', 'PSSR编号'], ['project_name', 'varchar(255)', '项目名称'],
    ['source_type', 'varchar(32)', '来源类型'], ['source_biz_id', 'bigint', '来源业务ID'],
    ['area_id', 'bigint', '区域'], ['equipment_id', 'bigint', '设备'],
    ['planned_startup_at', 'datetime', '计划开车时间'], ['approval_status', 'varchar(32)', '批准状态'],
    ['status', 'varchar(32)', '项目状态'],
  ], 'DRAFT'],
  ['pssr_template', 'PssrTemplate', [
    ['template_code', 'varchar(64)', '模板编码'], ['template_name', 'varchar(255)', '模板名称'],
    ['unit_type', 'varchar(32)', '装置类型'], ['status', 'varchar(32)', '状态'],
  ], 'ACTIVE'],
  ['pssr_check_item', 'PssrCheckItem', [
    ['template_id', 'bigint', '模板ID'], ['item_no', 'varchar(64)', '检查项编号'],
    ['item_desc', 'varchar(1024)', '检查项描述'], ['discipline', 'varchar(32)', '专业'],
    ['issue_level', 'varchar(8)', '问题等级'], ['startup_block_flag', 'tinyint', '开车前必关'],
  ], null],
  ['pssr_execution_record', 'PssrExecutionRecord', [
    ['project_id', 'bigint', '项目ID'], ['check_item_id', 'bigint', '检查项ID'],
    ['result', 'varchar(32)', '结果'], ['remark', 'varchar(1024)', '备注'],
    ['executor_user_id', 'bigint', '执行人'],
  ], null],
  ['pssr_issue', 'PssrIssue', [
    ['project_id', 'bigint', '项目ID'], ['check_item_id', 'bigint', '检查项ID'],
    ['issue_level', 'varchar(8)', '问题等级'], ['description', 'varchar(2048)', '问题描述'],
    ['owner_user_id', 'bigint', '责任人'], ['due_at', 'datetime', '整改期限'],
    ['close_required_before_startup', 'tinyint', '开车前必关'], ['status', 'varchar(32)', '状态'],
  ], 'PENDING_RECTIFY'],
  ['pssr_approval_record', 'PssrApprovalRecord', [
    ['project_id', 'bigint', '项目ID'], ['approver_user_id', 'bigint', '批准人'],
    ['decision', 'varchar(32)', '决策'], ['comment_text', 'varchar(1024)', '意见'],
  ], null],
];

const BARRIER_TABLES = [
  ['barrier', 'Barrier', [
    ['barrier_code', 'varchar(64)', '屏障编码'], ['barrier_name', 'varchar(255)', '屏障名称'],
    ['barrier_type', 'varchar(32)', '屏障类型'], ['major_hazard_id', 'bigint', '重大危险源ID'],
    ['hazop_scenario_id', 'bigint', '场景ID'], ['owner_org_id', 'bigint', '责任部门'],
    ['health_score', 'decimal(5,2)', '健康度'], ['status', 'varchar(32)', '状态'],
  ], 'NORMAL'],
  ['barrier_element', 'BarrierElement', [
    ['barrier_id', 'bigint', '屏障ID'], ['element_name', 'varchar(255)', '元素名称'],
    ['element_type', 'varchar(32)', '元素类型'],
  ], null],
  ['barrier_health_snapshot', 'BarrierHealthSnapshot', [
    ['barrier_id', 'bigint', '屏障ID'], ['health_score', 'decimal(5,2)', '健康度'],
    ['snapshot_at', 'datetime', '快照时间'],
  ], null],
  ['barrier_degradation', 'BarrierDegradation', [
    ['barrier_id', 'bigint', '屏障ID'], ['from_status', 'varchar(32)', '原状态'],
    ['to_status', 'varchar(32)', '目标状态'], ['reason', 'varchar(1024)', '原因'],
  ], null],
  ['compensating_measure', 'CompensatingMeasure', [
    ['barrier_id', 'bigint', '屏障ID'], ['measure_desc', 'varchar(1024)', '补偿措施'],
    ['status', 'varchar(32)', '状态'],
  ], 'ACTIVE'],
  ['mi_equipment', 'MiEquipment', [
    ['equipment_code', 'varchar(64)', '设备编码'], ['equipment_name', 'varchar(255)', '设备名称'],
    ['area_id', 'bigint', '区域'], ['critical_flag', 'tinyint', '关键设备'], ['status', 'varchar(32)', '状态'],
  ], 'ACTIVE'],
  ['mi_inspection_plan', 'MiInspectionPlan', [
    ['equipment_id', 'bigint', '设备ID'], ['plan_name', 'varchar(255)', '计划名称'],
    ['cycle_days', 'int', '周期天数'], ['next_due_at', 'datetime', '下次到期'], ['status', 'varchar(32)', '状态'],
  ], 'ACTIVE'],
  ['mi_inspection_record', 'MiInspectionRecord', [
    ['plan_id', 'bigint', '计划ID'], ['equipment_id', 'bigint', '设备ID'],
    ['inspected_at', 'datetime', '检验时间'], ['result', 'varchar(32)', '结果'],
  ], null],
  ['mi_defect', 'MiDefect', [
    ['defect_no', 'varchar(64)', '缺陷编号'], ['equipment_id', 'bigint', '设备ID'],
    ['defect_level', 'varchar(32)', '缺陷等级'], ['source_type', 'varchar(32)', '来源类型'],
    ['description', 'varchar(2048)', '描述'], ['repair_deadline', 'datetime', '维修期限'],
    ['status', 'varchar(32)', '状态'],
  ], 'PENDING_CONFIRM'],
  ['mi_maintenance_task', 'MiMaintenanceTask', [
    ['defect_id', 'bigint', '缺陷ID'], ['task_desc', 'varchar(1024)', '任务描述'],
    ['owner_user_id', 'bigint', '责任人'], ['status', 'varchar(32)', '状态'],
  ], 'PENDING'],
];

const PSSR_STATUS = `package com.fgroupboss.ai.psm.pssr.config;

import com.fgroupboss.ai.psm.common.BusinessException;

public enum PssrProjectStatus {
    DRAFT, PENDING_REVIEW, REVIEWING, RECTIFYING, PENDING_APPROVAL, APPROVED, ARCHIVED, RETURNED, CANCELLED;

    public static void assertApproveStartup(String current) {
        if (!PENDING_APPROVAL.name().equals(current)) {
            throw new BusinessException(400, "only PENDING_APPROVAL can approve startup");
        }
    }

    public static String targetAfterApproveStartup() {
        return APPROVED.name();
    }

    public static void assertRejectStartup(String current) {
        if (!PENDING_APPROVAL.name().equals(current)) {
            throw new BusinessException(400, "only PENDING_APPROVAL can reject startup");
        }
    }

    public static String targetAfterRejectStartup() {
        return RETURNED.name();
    }

    public static void assertDirectTransition(String from, String to) {
        if (DRAFT.name().equals(from) && APPROVED.name().equals(to)) {
            throw new BusinessException(400, "illegal transition DRAFT -> APPROVED");
        }
    }
}
`;

const BARRIER_STATUS = `package com.fgroupboss.ai.psm.barrier.config;

import com.fgroupboss.ai.psm.common.BusinessException;

public enum BarrierStatus {
    NORMAL, DEGRADED, FAILED, RESTORING, DISABLED;

    public static void assertDegrade(String current) {
        if (!NORMAL.name().equals(current) && !DEGRADED.name().equals(current)) {
            throw new BusinessException(400, "only NORMAL or DEGRADED can degrade further");
        }
    }

    public static String targetAfterDegrade(String current) {
        return NORMAL.name().equals(current) ? DEGRADED.name() : FAILED.name();
    }

    public static void assertRestore(String current) {
        if (!DEGRADED.name().equals(current) && !FAILED.name().equals(current) && !RESTORING.name().equals(current)) {
            throw new BusinessException(400, "barrier cannot restore from " + current);
        }
    }

    public static String targetAfterRestore() {
        return NORMAL.name();
    }

    public static void assertDirectTransition(String from, String to) {
        if (DISABLED.name().equals(from) && NORMAL.name().equals(to)) {
            throw new BusinessException(400, "DISABLED barrier must be enabled first");
        }
    }
}
`;

function genScaffold(svc, tables, statusSrc, statusTestBody, statusCls) {
  const base = path.join(ROOT, svc.artifact);
  write(path.join(base, 'pom.xml'), genPom(svc.artifact));
  write(path.join(base, 'src', 'main', 'resources', 'application.yml'), genYml(svc));
  write(path.join(base, 'src', 'main', 'resources', 'db', 'schema.sql'), genSchema(tables));
  write(pj(svc, `${svc.appClass}.java`), genApp(svc.pkg, svc.appClass));
  write(pj(svc, 'controller', 'GlobalExceptionHandler.java'), genHandler(svc.pkg, svc.handler));
  write(pj(svc, 'config', `${statusCls}.java`), statusSrc);
  write(tj(svc, 'config', `${statusCls}Test.java`), statusTestBody);
  for (const [table, cls, fields] of tables) {
    write(pj(svc, 'model', 'entity', `${cls}Entity.java`), genEntity(svc.pkg, table, cls, fields));
    write(pj(svc, 'mapper', `${cls}Mapper.java`), genMapper(svc.pkg, cls));
    write(pj(svc, 'model', 'vo', `${cls}VO.java`), genVo(svc.pkg, cls, fields));
  }
}

// Read Java service implementations from separate files
const implDir = path.join(__dirname, 'pssr-barrier-java');
function readImpl(name) {
  return fs.readFileSync(path.join(implDir, name), 'utf8');
}

const pssrSvc = {
  artifact: 'psm-pssr-service', port: 18113, db: 'psm_pssr', pkg: 'pssr',
  appClass: 'PssrServiceApplication', handler: 'pssr',
};
const barrierSvc = {
  artifact: 'psm-barrier-service', port: 18114, db: 'psm_barrier', pkg: 'barrier',
  appClass: 'BarrierServiceApplication', handler: 'barrier',
};

genScaffold(pssrSvc, PSSR_TABLES, PSSR_STATUS, `package com.fgroupboss.ai.psm.pssr.config;

import com.fgroupboss.ai.psm.common.BusinessException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PssrProjectStatusTest {
    @Test void draftCannotApproveDirectly() {
        assertThrows(BusinessException.class, () -> PssrProjectStatus.assertDirectTransition("DRAFT", "APPROVED"));
    }
    @Test void approveStartup() {
        assertEquals("APPROVED", PssrProjectStatus.targetAfterApproveStartup());
    }
}
`, 'PssrProjectStatus');

genScaffold(barrierSvc, BARRIER_TABLES, BARRIER_STATUS, `package com.fgroupboss.ai.psm.barrier.config;

import com.fgroupboss.ai.psm.common.BusinessException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BarrierStatusTest {
    @Test void normalDegradesToDegraded() {
        assertEquals("DEGRADED", BarrierStatus.targetAfterDegrade("NORMAL"));
    }
    @Test void disabledCannotRestoreToNormalDirectly() {
        assertThrows(BusinessException.class, () -> BarrierStatus.assertDirectTransition("DISABLED", "NORMAL"));
    }
}
`, 'BarrierStatus');

console.log('Scaffold done. Copy impl files from pssr-barrier-java if present.');
