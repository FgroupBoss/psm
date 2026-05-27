package com.fgroupboss.ai.psm.workpermit.support;

import com.fgroupboss.ai.psm.workpermit.mapper.PermitSafetyMeasureMapper;
import com.fgroupboss.ai.psm.workpermit.model.entity.PermitSafetyMeasureEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 按作业类型初始化默认安全措施（动火/受限空间）。
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
