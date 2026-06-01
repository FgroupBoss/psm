package com.fgroupboss.ai.psm.operation.workpermit.hotwork.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.PermitActionRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HotWorkApprovalNodeProgressVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HotWorkApprovalProgressVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HotWorkApprovalTaskVO;
import com.fgroupboss.ai.psm.operation.client.HotWorkWorkflowApi;
import com.fgroupboss.ai.psm.operation.client.dto.hotwork.HotWorkApproverDTO;
import com.fgroupboss.ai.psm.operation.client.dto.hotwork.HotWorkApproverResolveDTO;
import com.fgroupboss.ai.psm.operation.client.dto.hotwork.HotWorkApproverResolveResultDTO;
import com.fgroupboss.ai.psm.operation.client.dto.hotwork.HotWorkWorkflowNodeSnapshotDTO;
import com.fgroupboss.ai.psm.operation.client.dto.hotwork.HotWorkWorkflowSnapshotDTO;
import com.fgroupboss.ai.psm.operation.workpermit.config.HotWorkApprovalProperties;
import com.fgroupboss.ai.psm.operation.workpermit.config.WorkPermitStatus;
import com.fgroupboss.ai.psm.operation.workpermit.hotwork.config.ApprovalInstanceStatus;
import com.fgroupboss.ai.psm.operation.workpermit.hotwork.config.ApprovalNodeStatus;
import com.fgroupboss.ai.psm.operation.workpermit.hotwork.config.ApprovalTaskStatus;
import com.fgroupboss.ai.psm.operation.workpermit.hotwork.config.HotWorkSignMode;
import com.fgroupboss.ai.psm.operation.workpermit.hotwork.config.HotWorkWorkType;
import com.fgroupboss.ai.psm.operation.workpermit.hotwork.mapper.PermitApprovalInstanceMapper;
import com.fgroupboss.ai.psm.operation.workpermit.hotwork.mapper.PermitApprovalNodeInstanceMapper;
import com.fgroupboss.ai.psm.operation.workpermit.hotwork.mapper.PermitApprovalTaskMapper;
import com.fgroupboss.ai.psm.operation.workpermit.hotwork.model.entity.PermitApprovalInstanceEntity;
import com.fgroupboss.ai.psm.operation.workpermit.hotwork.model.entity.PermitApprovalNodeInstanceEntity;
import com.fgroupboss.ai.psm.operation.workpermit.hotwork.model.entity.PermitApprovalTaskEntity;
import com.fgroupboss.ai.psm.operation.workpermit.mapper.PermitApprovalRecordMapper;
import com.fgroupboss.ai.psm.operation.workpermit.model.entity.PermitApprovalRecordEntity;
import com.fgroupboss.ai.psm.operation.workpermit.model.entity.WorkPermitEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 动火多节点或签/会签审批引擎。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HotWorkApprovalEngine {

    private final HotWorkApprovalProperties properties;
    private final HotWorkWorkflowApi hotWorkWorkflowClient;
    private final PermitApprovalInstanceMapper instanceMapper;
    private final PermitApprovalNodeInstanceMapper nodeInstanceMapper;
    private final PermitApprovalTaskMapper taskMapper;
    private final PermitApprovalRecordMapper approvalRecordMapper;
    private final ObjectMapper objectMapper;

    public boolean supportsMultiNode(WorkPermitEntity permit) {
        return properties.isEnabled()
                && HotWorkWorkType.isHotWork(permit.getWorkType())
                && permit.getWorkflowTemplateId() != null;
    }

    public void validateWorkflowSelection(WorkPermitEntity permit) {
        if (!HotWorkWorkType.isHotWork(permit.getWorkType())) {
            return;
        }
        if (!properties.isEnabled()) {
            return;
        }
        if (permit.getWorkflowTemplateId() == null) {
            throw new BusinessException(400, "动火票必须选择审批流");
        }
        if (!StringUtils.hasText(permit.getHotWorkLevel())) {
            throw new BusinessException(400, "动火票必须选择动火级别");
        }
    }

    @Transactional
    public void startOnSubmit(WorkPermitEntity permit, String operator) {
        if (!supportsMultiNode(permit)) {
            return;
        }
        HotWorkWorkflowSnapshotDTO snapshot = hotWorkWorkflowClient.getSnapshot(
                permit.getTenantId(), permit.getWorkflowTemplateId(), permit.getWorkflowTemplateVersion());
        validateApproversResolvable(permit, snapshot);
        cancelRunningInstances(permit.getTenantId(), permit.getId());
        PermitApprovalInstanceEntity instance = createInstance(permit, snapshot);
        activateNode(instance, permit, snapshot.getNodes().get(0), operator);
    }

    /**
     * @return 主票下一状态；仍审批中则返回 APPROVING
     */
    @Transactional
    public WorkPermitStatus actOnTask(WorkPermitEntity permit, PermitActionRequest request,
                                      String operator, Long operatorUserId) {
        if (!supportsMultiNode(permit)) {
            throw new BusinessException(409, "当前票未启用多节点审批");
        }
        Long taskId = request.getApprovalTaskId();
        if (taskId == null) {
            throw new BusinessException(400, "approvalTaskId is required");
        }
        String action = normalizeAction(request.getAction());
        PermitApprovalTaskEntity task = requirePendingTask(permit.getTenantId(), permit.getId(), taskId, operatorUserId);
        PermitApprovalNodeInstanceEntity node = requireNode(task.getNodeInstanceId(), permit.getTenantId());
        PermitApprovalInstanceEntity instance = requireRunningInstance(node.getInstanceId(), permit.getTenantId());

        if ("REJECT".equals(action)) {
            finalizeInstance(instance, ApprovalInstanceStatus.CANCELLED);
            markTaskDone(task, ApprovalTaskStatus.REJECTED, action, request, operator);
            cancelPendingTasks(node.getId(), permit.getTenantId(), task.getId());
            saveRecord(permit, instance, node, task, action, request, operator);
            return WorkPermitStatus.CLOSED;
        }
        if ("RETURN".equals(action)) {
            finalizeInstance(instance, ApprovalInstanceStatus.CANCELLED);
            markTaskDone(task, ApprovalTaskStatus.RETURNED, action, request, operator);
            cancelPendingTasks(node.getId(), permit.getTenantId(), task.getId());
            saveRecord(permit, instance, node, task, action, request, operator);
            return WorkPermitStatus.RETURNED;
        }

        if (!markTaskApproved(task, operator)) {
            throw new BusinessException(409, "该审批任务已被处理");
        }
        saveRecord(permit, instance, node, task, action, request, operator);

        if (HotWorkSignMode.ANY.equals(node.getSignMode())) {
            cancelPendingTasks(node.getId(), permit.getTenantId(), task.getId());
            completeNode(node, 1);
        } else {
            int approved = countApprovedTasks(node.getId(), permit.getTenantId());
            node.setApprovedCount(approved);
            if (approved < node.getRequiredCount()) {
                nodeInstanceMapper.updateById(node);
                return WorkPermitStatus.APPROVING;
            }
            completeNode(node, approved);
        }

        HotWorkWorkflowSnapshotDTO snapshot = readSnapshot(instance.getTemplateSnapshotJson());
        HotWorkWorkflowNodeSnapshotDTO next = findNextNode(snapshot, node.getNodeSeq());
        if (next != null) {
            instance.setCurrentNodeSeq(next.getNodeSeq());
            instanceMapper.updateById(instance);
            activateNode(instance, permit, next, operator);
            return WorkPermitStatus.APPROVING;
        }
        finalizeInstance(instance, ApprovalInstanceStatus.COMPLETED);
        return WorkPermitStatus.PENDING_SITE_PERMIT;
    }

    public HotWorkApprovalProgressVO getProgress(Long tenantId, Long permitId) {
        PermitApprovalInstanceEntity instance = findLatestInstance(tenantId, permitId);
        HotWorkApprovalProgressVO vo = new HotWorkApprovalProgressVO();
        if (instance == null) {
            return vo;
        }
        vo.setInstanceId(instance.getId());
        vo.setInstanceStatus(instance.getStatus());
        vo.setCurrentNodeSeq(instance.getCurrentNodeSeq());
        HotWorkWorkflowSnapshotDTO snapshot = readSnapshot(instance.getTemplateSnapshotJson());
        vo.setTotalNodes(snapshot.getNodes().size());
        List<PermitApprovalNodeInstanceEntity> nodes = listNodes(instance.getId(), tenantId);
        for (PermitApprovalNodeInstanceEntity node : nodes) {
            HotWorkApprovalNodeProgressVO item = new HotWorkApprovalNodeProgressVO();
            item.setNodeSeq(node.getNodeSeq());
            item.setNodeName(node.getNodeName());
            item.setSignMode(node.getSignMode());
            item.setStatus(node.getStatus());
            item.setApprovedCount(node.getApprovedCount());
            item.setRequiredCount(node.getRequiredCount());
            vo.getNodes().add(item);
            if (instance.getCurrentNodeSeq() != null && instance.getCurrentNodeSeq().equals(node.getNodeSeq())) {
                vo.setCurrentNodeName(node.getNodeName());
                vo.setSignMode(node.getSignMode());
                vo.setApprovedCount(node.getApprovedCount());
                vo.setRequiredCount(node.getRequiredCount());
            }
        }
        for (PermitApprovalTaskEntity task : listPendingTasksForPermit(tenantId, permitId)) {
            vo.getPendingTasks().add(toTaskVO(task, nodes));
        }
        return vo;
    }

    public List<HotWorkApprovalTaskVO> listPendingTasks(Long tenantId, Long permitId, Long assigneeUserId) {
        List<HotWorkApprovalTaskVO> result = new ArrayList<HotWorkApprovalTaskVO>();
        LambdaQueryWrapper<PermitApprovalTaskEntity> wrapper = new LambdaQueryWrapper<PermitApprovalTaskEntity>()
                .eq(PermitApprovalTaskEntity::getTenantId, tenantId)
                .eq(PermitApprovalTaskEntity::getWorkPermitId, permitId)
                .eq(PermitApprovalTaskEntity::getStatus, ApprovalTaskStatus.PENDING)
                .eq(PermitApprovalTaskEntity::getDeleted, 0);
        if (assigneeUserId != null) {
            wrapper.eq(PermitApprovalTaskEntity::getAssigneeUserId, assigneeUserId);
        }
        List<PermitApprovalNodeInstanceEntity> nodes = listNodesByPermit(tenantId, permitId);
        for (PermitApprovalTaskEntity task : taskMapper.selectList(wrapper)) {
            result.add(toTaskVO(task, nodes));
        }
        return result;
    }

    private void validateApproversResolvable(WorkPermitEntity permit, HotWorkWorkflowSnapshotDTO snapshot) {
        if (CollectionUtils.isEmpty(snapshot.getNodes())) {
            throw new BusinessException(409, "审批流未配置节点");
        }
        for (HotWorkWorkflowNodeSnapshotDTO node : snapshot.getNodes()) {
            HotWorkApproverResolveResultDTO resolved = resolve(permit, node);
            if (!resolved.isResolved() || CollectionUtils.isEmpty(resolved.getApprovers())) {
                throw new BusinessException(409, "节点【" + node.getNodeName() + "】未找到审批人");
            }
        }
    }

    private HotWorkApproverResolveResultDTO resolve(WorkPermitEntity permit, HotWorkWorkflowNodeSnapshotDTO node) {
        HotWorkApproverResolveDTO dto = new HotWorkApproverResolveDTO();
        dto.setTenantId(permit.getTenantId());
        dto.setHotWorkLevel(permit.getHotWorkLevel());
        dto.setAreaId(permit.getAreaId());
        dto.setUnitId(permit.getUnitId());
        dto.setPermitIssuerUserId(permit.getPermitIssuerUserId());
        dto.setSupervisorUserId(permit.getSupervisorUserId());
        dto.setContractorCompanyId(permit.getContractorCompanyId());
        dto.setNode(node);
        return hotWorkWorkflowClient.resolveApprovers(dto);
    }

    private PermitApprovalInstanceEntity createInstance(WorkPermitEntity permit, HotWorkWorkflowSnapshotDTO snapshot) {
        Date now = new Date();
        PermitApprovalInstanceEntity instance = new PermitApprovalInstanceEntity();
        instance.setTenantId(permit.getTenantId());
        instance.setWorkPermitId(permit.getId());
        instance.setWorkflowTemplateId(permit.getWorkflowTemplateId());
        instance.setWorkflowTemplateVersion(permit.getWorkflowTemplateVersion());
        try {
            instance.setTemplateSnapshotJson(objectMapper.writeValueAsString(snapshot));
        } catch (Exception ex) {
            throw new BusinessException(500, "serialize snapshot failed");
        }
        instance.setStatus(ApprovalInstanceStatus.RUNNING);
        instance.setCurrentNodeSeq(snapshot.getNodes().get(0).getNodeSeq());
        instance.setStartedAt(now);
        instance.setDeleted(0);
        instance.setCreatedAt(now);
        instance.setUpdatedAt(now);
        instanceMapper.insert(instance);
        return instance;
    }

    private void activateNode(PermitApprovalInstanceEntity instance, WorkPermitEntity permit,
                              HotWorkWorkflowNodeSnapshotDTO nodeDef, String operator) {
        HotWorkApproverResolveResultDTO resolved = resolve(permit, nodeDef);
        Date now = new Date();
        PermitApprovalNodeInstanceEntity node = new PermitApprovalNodeInstanceEntity();
        node.setTenantId(permit.getTenantId());
        node.setInstanceId(instance.getId());
        node.setNodeSeq(nodeDef.getNodeSeq());
        node.setNodeName(nodeDef.getNodeName());
        node.setSignMode(nodeDef.getSignMode());
        node.setStatus(ApprovalNodeStatus.PENDING);
        node.setRequiredCount(HotWorkSignMode.ANY.equals(nodeDef.getSignMode()) ? 1 : resolved.getApprovers().size());
        node.setApprovedCount(0);
        node.setStartedAt(now);
        node.setDeleted(0);
        node.setCreatedAt(now);
        node.setUpdatedAt(now);
        nodeInstanceMapper.insert(node);
        for (HotWorkApproverDTO approver : resolved.getApprovers()) {
            PermitApprovalTaskEntity task = new PermitApprovalTaskEntity();
            task.setTenantId(permit.getTenantId());
            task.setNodeInstanceId(node.getId());
            task.setWorkPermitId(permit.getId());
            task.setAssigneeUserId(approver.getUserId());
            task.setAssigneeName(approver.getUserName());
            task.setStatus(ApprovalTaskStatus.PENDING);
            task.setDeleted(0);
            task.setCreatedAt(now);
            task.setUpdatedAt(now);
            taskMapper.insert(task);
        }
    }

    private boolean markTaskApproved(PermitApprovalTaskEntity task, String operator) {
        Date now = new Date();
        LambdaUpdateWrapper<PermitApprovalTaskEntity> wrapper = new LambdaUpdateWrapper<PermitApprovalTaskEntity>()
                .eq(PermitApprovalTaskEntity::getId, task.getId())
                .eq(PermitApprovalTaskEntity::getStatus, ApprovalTaskStatus.PENDING)
                .set(PermitApprovalTaskEntity::getStatus, ApprovalTaskStatus.APPROVED)
                .set(PermitApprovalTaskEntity::getAction, "APPROVE")
                .set(PermitApprovalTaskEntity::getActedAt, now)
                .set(PermitApprovalTaskEntity::getUpdatedAt, now);
        return taskMapper.update(null, wrapper) > 0;
    }

    private void markTaskDone(PermitApprovalTaskEntity task, String status, String action,
                            PermitActionRequest request, String operator) {
        task.setStatus(status);
        task.setAction(action);
        task.setOpinion(resolveOpinion(request));
        task.setActedAt(new Date());
        task.setUpdatedAt(new Date());
        taskMapper.updateById(task);
    }

    private void completeNode(PermitApprovalNodeInstanceEntity node, int approvedCount) {
        node.setStatus(ApprovalNodeStatus.APPROVED);
        node.setApprovedCount(approvedCount);
        node.setCompletedAt(new Date());
        node.setUpdatedAt(new Date());
        nodeInstanceMapper.updateById(node);
    }

    private void cancelPendingTasks(Long nodeInstanceId, Long tenantId, Long excludeTaskId) {
        LambdaQueryWrapper<PermitApprovalTaskEntity> wrapper = new LambdaQueryWrapper<PermitApprovalTaskEntity>()
                .eq(PermitApprovalTaskEntity::getTenantId, tenantId)
                .eq(PermitApprovalTaskEntity::getNodeInstanceId, nodeInstanceId)
                .eq(PermitApprovalTaskEntity::getStatus, ApprovalTaskStatus.PENDING)
                .eq(PermitApprovalTaskEntity::getDeleted, 0);
        if (excludeTaskId != null) {
            wrapper.ne(PermitApprovalTaskEntity::getId, excludeTaskId);
        }
        for (PermitApprovalTaskEntity task : taskMapper.selectList(wrapper)) {
            task.setStatus(ApprovalTaskStatus.CANCELLED);
            task.setUpdatedAt(new Date());
            taskMapper.updateById(task);
        }
    }

    private int countApprovedTasks(Long nodeInstanceId, Long tenantId) {
        LambdaQueryWrapper<PermitApprovalTaskEntity> wrapper = new LambdaQueryWrapper<PermitApprovalTaskEntity>()
                .eq(PermitApprovalTaskEntity::getTenantId, tenantId)
                .eq(PermitApprovalTaskEntity::getNodeInstanceId, nodeInstanceId)
                .eq(PermitApprovalTaskEntity::getStatus, ApprovalTaskStatus.APPROVED)
                .eq(PermitApprovalTaskEntity::getDeleted, 0);
        return taskMapper.selectCount(wrapper).intValue();
    }

    private void finalizeInstance(PermitApprovalInstanceEntity instance, String status) {
        instance.setStatus(status);
        instance.setCurrentNodeSeq(null);
        instance.setCompletedAt(new Date());
        instance.setUpdatedAt(new Date());
        instanceMapper.updateById(instance);
    }

    private void cancelRunningInstances(Long tenantId, Long permitId) {
        LambdaQueryWrapper<PermitApprovalInstanceEntity> wrapper = new LambdaQueryWrapper<PermitApprovalInstanceEntity>()
                .eq(PermitApprovalInstanceEntity::getTenantId, tenantId)
                .eq(PermitApprovalInstanceEntity::getWorkPermitId, permitId)
                .eq(PermitApprovalInstanceEntity::getStatus, ApprovalInstanceStatus.RUNNING)
                .eq(PermitApprovalInstanceEntity::getDeleted, 0);
        for (PermitApprovalInstanceEntity item : instanceMapper.selectList(wrapper)) {
            finalizeInstance(item, ApprovalInstanceStatus.CANCELLED);
        }
    }

    private HotWorkWorkflowNodeSnapshotDTO findNextNode(HotWorkWorkflowSnapshotDTO snapshot, int currentSeq) {
        for (HotWorkWorkflowNodeSnapshotDTO node : snapshot.getNodes()) {
            if (node.getNodeSeq() != null && node.getNodeSeq() > currentSeq) {
                return node;
            }
        }
        return null;
    }

    private HotWorkWorkflowSnapshotDTO readSnapshot(String json) {
        try {
            return objectMapper.readValue(json, HotWorkWorkflowSnapshotDTO.class);
        } catch (Exception ex) {
            throw new BusinessException(500, "invalid approval snapshot");
        }
    }

    private PermitApprovalInstanceEntity findLatestInstance(Long tenantId, Long permitId) {
        LambdaQueryWrapper<PermitApprovalInstanceEntity> wrapper = new LambdaQueryWrapper<PermitApprovalInstanceEntity>()
                .eq(PermitApprovalInstanceEntity::getTenantId, tenantId)
                .eq(PermitApprovalInstanceEntity::getWorkPermitId, permitId)
                .eq(PermitApprovalInstanceEntity::getDeleted, 0)
                .orderByDesc(PermitApprovalInstanceEntity::getId)
                .last("limit 1");
        return instanceMapper.selectOne(wrapper);
    }

    private List<PermitApprovalNodeInstanceEntity> listNodes(Long instanceId, Long tenantId) {
        return nodeInstanceMapper.selectList(new LambdaQueryWrapper<PermitApprovalNodeInstanceEntity>()
                .eq(PermitApprovalNodeInstanceEntity::getTenantId, tenantId)
                .eq(PermitApprovalNodeInstanceEntity::getInstanceId, instanceId)
                .eq(PermitApprovalNodeInstanceEntity::getDeleted, 0)
                .orderByAsc(PermitApprovalNodeInstanceEntity::getNodeSeq));
    }

    private List<PermitApprovalNodeInstanceEntity> listNodesByPermit(Long tenantId, Long permitId) {
        PermitApprovalInstanceEntity instance = findLatestInstance(tenantId, permitId);
        if (instance == null) {
            return new ArrayList<PermitApprovalNodeInstanceEntity>();
        }
        return listNodes(instance.getId(), tenantId);
    }

    private List<PermitApprovalTaskEntity> listPendingTasksForPermit(Long tenantId, Long permitId) {
        return taskMapper.selectList(new LambdaQueryWrapper<PermitApprovalTaskEntity>()
                .eq(PermitApprovalTaskEntity::getTenantId, tenantId)
                .eq(PermitApprovalTaskEntity::getWorkPermitId, permitId)
                .eq(PermitApprovalTaskEntity::getStatus, ApprovalTaskStatus.PENDING)
                .eq(PermitApprovalTaskEntity::getDeleted, 0));
    }

    private PermitApprovalTaskEntity requirePendingTask(Long tenantId, Long permitId, Long taskId, Long operatorUserId) {
        PermitApprovalTaskEntity task = taskMapper.selectById(taskId);
        if (task == null || task.getDeleted() != null && task.getDeleted() != 0
                || !tenantId.equals(task.getTenantId()) || !permitId.equals(task.getWorkPermitId())) {
            throw new BusinessException(404, "approval task not found");
        }
        if (!ApprovalTaskStatus.PENDING.equals(task.getStatus())) {
            throw new BusinessException(409, "approval task is not pending");
        }
        if (operatorUserId != null && !operatorUserId.equals(task.getAssigneeUserId())) {
            throw new BusinessException(403, "not assigned approver");
        }
        return task;
    }

    private PermitApprovalNodeInstanceEntity requireNode(Long nodeId, Long tenantId) {
        PermitApprovalNodeInstanceEntity node = nodeInstanceMapper.selectById(nodeId);
        if (node == null || !tenantId.equals(node.getTenantId())) {
            throw new BusinessException(404, "approval node not found");
        }
        return node;
    }

    private PermitApprovalInstanceEntity requireRunningInstance(Long instanceId, Long tenantId) {
        PermitApprovalInstanceEntity instance = instanceMapper.selectById(instanceId);
        if (instance == null || !tenantId.equals(instance.getTenantId())) {
            throw new BusinessException(404, "approval instance not found");
        }
        if (!ApprovalInstanceStatus.RUNNING.equals(instance.getStatus())) {
            throw new BusinessException(409, "approval instance is not running");
        }
        return instance;
    }

    private void saveRecord(WorkPermitEntity permit, PermitApprovalInstanceEntity instance,
                          PermitApprovalNodeInstanceEntity node, PermitApprovalTaskEntity task,
                          String action, PermitActionRequest request, String operator) {
        PermitApprovalRecordEntity record = new PermitApprovalRecordEntity();
        record.setTenantId(permit.getTenantId());
        record.setWorkPermitId(permit.getId());
        record.setInstanceId(instance.getId());
        record.setNodeInstanceId(node.getId());
        record.setNodeSeq(node.getNodeSeq());
        record.setNodeName(node.getNodeName());
        record.setSignMode(node.getSignMode());
        record.setTaskId(task.getId());
        record.setAssigneeUserId(task.getAssigneeUserId());
        record.setAction(action);
        record.setOpinion(resolveOpinion(request));
        record.setOperatorName(operator);
        record.setOperatedAt(new Date());
        approvalRecordMapper.insert(record);
    }

    private HotWorkApprovalTaskVO toTaskVO(PermitApprovalTaskEntity task, List<PermitApprovalNodeInstanceEntity> nodes) {
        HotWorkApprovalTaskVO vo = new HotWorkApprovalTaskVO();
        vo.setId(task.getId());
        vo.setNodeInstanceId(task.getNodeInstanceId());
        vo.setAssigneeUserId(task.getAssigneeUserId());
        vo.setAssigneeName(task.getAssigneeName());
        vo.setStatus(task.getStatus());
        for (PermitApprovalNodeInstanceEntity node : nodes) {
            if (node.getId().equals(task.getNodeInstanceId())) {
                vo.setNodeSeq(node.getNodeSeq());
                vo.setNodeName(node.getNodeName());
                vo.setSignMode(node.getSignMode());
                break;
            }
        }
        return vo;
    }

    private String normalizeAction(String action) {
        if (!StringUtils.hasText(action)) {
            return "APPROVE";
        }
        return action.trim().toUpperCase();
    }

    private String resolveOpinion(PermitActionRequest request) {
        if (request == null) {
            return null;
        }
        if (StringUtils.hasText(request.getOpinion())) {
            return request.getOpinion().trim();
        }
        return StringUtils.hasText(request.getReason()) ? request.getReason().trim() : null;
    }
}
