package com.fgroupboss.ai.psm.operation.workpermit.support;

import com.fgroupboss.ai.psm.operation.workpermit.mapper.PermitSafetyMeasureMapper;
import com.fgroupboss.ai.psm.operation.workpermit.model.entity.PermitSafetyMeasureEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 按作业类型初始化默认安全措施（八大作业票）。
 */
@Component
@RequiredArgsConstructor
public class DefaultSafetyMeasuresSupport {

    private static final String CONFIRM_PENDING = "PENDING";

    private final PermitSafetyMeasureMapper safetyMeasureMapper;

    public void seedDefaultMeasures(Long tenantId, Long workPermitId, String workType) {
        List<MeasureTemplate> templates = resolveTemplates(workType);
        for (MeasureTemplate template : templates) {
            PermitSafetyMeasureEntity entity = new PermitSafetyMeasureEntity();
            entity.setTenantId(tenantId);
            entity.setWorkPermitId(workPermitId);
            entity.setMeasureCode(template.code);
            entity.setMeasureName(template.name);
            entity.setRequiredFlag(1);
            entity.setConfirmStatus(CONFIRM_PENDING);
            entity.setDeleted(0);
            safetyMeasureMapper.insert(entity);
        }
    }

    private List<MeasureTemplate> resolveTemplates(String workType) {
        if (workType == null) {
            return Collections.emptyList();
        }
        String normalized = workType.trim().toUpperCase();
        if ("HOT_WORK".equals(normalized)) {
            List<MeasureTemplate> list = new ArrayList<MeasureTemplate>();
            list.add(new MeasureTemplate("HW_CLEAR_FLAMMABLE", "清除动火点周围易燃物"));
            list.add(new MeasureTemplate("HW_FIRE_EXTINGUISHER", "配备足够灭火器"));
            list.add(new MeasureTemplate("HW_GUARDIAN", "设置专职监护人"));
            list.add(new MeasureTemplate("HW_ISOLATION", "动火设备管线隔离置换"));
            return list;
        }
        if ("CONFINED_SPACE".equals(normalized)) {
            List<MeasureTemplate> list = new ArrayList<MeasureTemplate>();
            list.add(new MeasureTemplate("CS_VENTILATION", "受限空间强制通风"));
            list.add(new MeasureTemplate("CS_RESCUE", "救援设备就位"));
            list.add(new MeasureTemplate("CS_GUARDIAN", "设置专职监护人"));
            list.add(new MeasureTemplate("CS_ISOLATION", "能量隔离与上锁挂牌"));
            return list;
        }
        if ("HEIGHT_WORK".equals(normalized)) {
            List<MeasureTemplate> list = new ArrayList<MeasureTemplate>();
            list.add(new MeasureTemplate("HW_HEIGHT_FACILITY", "登高设施合格"));
            list.add(new MeasureTemplate("HW_FULL_BODY_HARNESS", "全身式安全带系挂可靠"));
            list.add(new MeasureTemplate("HW_FALL_PROTECTION", "安全绳/生命线/防坠器适用"));
            list.add(new MeasureTemplate("HW_EDGE_PROTECTION", "临边孔洞防护完整"));
            list.add(new MeasureTemplate("HW_TOOL_TETHER", "工具材料防坠落"));
            list.add(new MeasureTemplate("HW_BELOW_GUARD", "下方警戒区与监护"));
            return list;
        }
        if ("LIFTING".equals(normalized)) {
            List<MeasureTemplate> list = new ArrayList<MeasureTemplate>();
            list.add(new MeasureTemplate("LF_PLAN", "吊装方案已审批"));
            list.add(new MeasureTemplate("LF_RIGGING", "索具吊具检查合格"));
            list.add(new MeasureTemplate("LF_GUARD", "警戒区设置与监护"));
            list.add(new MeasureTemplate("LF_TRIAL", "试吊合格"));
            return list;
        }
        if ("TEMPORARY_ELECTRIC".equals(normalized)) {
            List<MeasureTemplate> list = new ArrayList<MeasureTemplate>();
            list.add(new MeasureTemplate("TE_ELECTRICIAN", "持证电工操作"));
            list.add(new MeasureTemplate("TE_RCD", "漏保试验合格"));
            list.add(new MeasureTemplate("TE_GROUNDING", "接地可靠"));
            list.add(new MeasureTemplate("TE_CABLE", "电缆敷设规范"));
            return list;
        }
        if ("BLIND_PLATE".equals(normalized)) {
            List<MeasureTemplate> list = new ArrayList<MeasureTemplate>();
            list.add(new MeasureTemplate("BP_ISOLATION", "管线隔离与泄压"));
            list.add(new MeasureTemplate("BP_TAG", "盲板挂牌"));
            list.add(new MeasureTemplate("BP_DIAGRAM", "位置图一致"));
            return list;
        }
        if ("EXCAVATION".equals(normalized)) {
            List<MeasureTemplate> list = new ArrayList<MeasureTemplate>();
            list.add(new MeasureTemplate("EX_UNDERGROUND", "地下设施确认"));
            list.add(new MeasureTemplate("EX_COUNTERSIGN", "专业会签完成"));
            list.add(new MeasureTemplate("EX_SHORING", "支护与临边防护"));
            list.add(new MeasureTemplate("EX_DRAIN", "排水措施到位"));
            return list;
        }
        if ("ROAD_BREAK".equals(normalized)) {
            List<MeasureTemplate> list = new ArrayList<MeasureTemplate>();
            list.add(new MeasureTemplate("RB_TRAFFIC_PLAN", "交通组织方案"));
            list.add(new MeasureTemplate("RB_BARRIER", "路障与标志"));
            list.add(new MeasureTemplate("RB_EMERGENCY", "应急通道畅通"));
            return list;
        }
        return Collections.emptyList();
    }

    private static final class MeasureTemplate {
        private final String code;
        private final String name;

        private MeasureTemplate(String code, String name) {
            this.code = code;
            this.name = name;
        }
    }
}
