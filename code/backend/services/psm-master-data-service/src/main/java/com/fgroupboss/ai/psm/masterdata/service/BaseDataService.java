package com.fgroupboss.ai.psm.masterdata.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.masterdata.config.BaseDataType;
import com.fgroupboss.ai.psm.masterdata.model.dto.BaseDataRequest;
import com.fgroupboss.ai.psm.masterdata.model.vo.BaseDataRecordVO;

import java.util.List;

/**
 * 基础台账业务服务。
 *
 * <p>区域、装置、设备和监测点位共用一套 API 语义，由实现层按类型路由到固定 Mapper。</p>
 */
public interface BaseDataService {

    /**
     * 创建基础台账记录。
     *
     * @param type 台账类型
     * @param request 创建请求
     * @param operator 操作人展示名
     * @return 创建后的台账记录
     */
    BaseDataRecordVO create(BaseDataType type, BaseDataRequest request, String operator);

    /**
     * 更新基础台账记录，编码不允许被更新。
     *
     * @param type 台账类型
     * @param id 台账记录 ID
     * @param request 更新请求
     * @param operator 操作人展示名
     * @return 更新后的台账记录
     */
    BaseDataRecordVO update(BaseDataType type, Long id, BaseDataRequest request, String operator);

    /**
     * 启用基础台账记录。
     *
     * @param type 台账类型
     * @param tenantId 租户 ID
     * @param id 台账记录 ID
     * @param operator 操作人展示名
     */
    void enable(BaseDataType type, Long tenantId, Long id, String operator);

    /**
     * 禁用基础台账记录。
     *
     * @param type 台账类型
     * @param tenantId 租户 ID
     * @param id 台账记录 ID
     * @param operator 操作人展示名
     */
    void disable(BaseDataType type, Long tenantId, Long id, String operator);

    /**
     * 逻辑删除基础台账记录。
     *
     * @param type 台账类型
     * @param tenantId 租户 ID
     * @param id 台账记录 ID
     * @param operator 操作人展示名
     */
    void delete(BaseDataType type, Long tenantId, Long id, String operator);

    /**
     * 查询基础台账详情。
     *
     * @param type 台账类型
     * @param tenantId 租户 ID
     * @param id 台账记录 ID
     * @return 台账详情
     */
    BaseDataRecordVO get(BaseDataType type, Long tenantId, Long id);

    /**
     * 分页查询基础台账。
     *
     * @param type 台账类型
     * @param tenantId 租户 ID
     * @param keyword 编码或名称关键字
     * @param status 状态过滤
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页大小，上限 200
     * @return 分页结果
     */
    PageResult<BaseDataRecordVO> page(BaseDataType type, Long tenantId, String keyword, String status, int pageNo, int pageSize);

    /**
     * 查询基础台账树原始列表。
     *
     * @param type 台账类型
     * @param tenantId 租户 ID
     * @return 台账列表
     */
    List<BaseDataRecordVO> tree(BaseDataType type, Long tenantId);
}
