package com.fgroupboss.ai.psm.identity.masterdata.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.identity.masterdata.config.MasterDataType;
import com.fgroupboss.ai.psm.identity.masterdata.model.dto.MasterDataRequest;
import com.fgroupboss.ai.psm.identity.masterdata.model.vo.MasterDataRecordVO;

import java.util.List;

/**
 * 主数据业务服务。
 *
 * <p>接口对外只暴露请求对象与响应对象，持久化实体由实现层内部转换，避免数据库字段外泄。</p>
 */
public interface MasterDataService {

    /**
     * 创建指定分类的主数据。
     *
     * @param type 主数据分类
     * @param request 创建请求
     * @param operator 操作人展示名
     * @return 创建后的主数据
     */
    MasterDataRecordVO create(MasterDataType type, MasterDataRequest request, String operator);

    /**
     * 更新指定分类的主数据，编码不允许被更新。
     *
     * @param type 主数据分类
     * @param id 主数据 ID
     * @param request 更新请求
     * @param operator 操作人展示名
     * @return 更新后的主数据
     */
    MasterDataRecordVO update(MasterDataType type, Long id, MasterDataRequest request, String operator);

    /**
     * 禁用主数据。
     *
     * @param type 主数据分类
     * @param tenantId 租户 ID
     * @param id 主数据 ID
     * @param operator 操作人展示名
     */
    void disable(MasterDataType type, Long tenantId, Long id, String operator);

    /**
     * 逻辑删除主数据。
     *
     * @param type 主数据分类
     * @param tenantId 租户 ID
     * @param id 主数据 ID
     * @param operator 操作人展示名
     */
    void delete(MasterDataType type, Long tenantId, Long id, String operator);

    /**
     * 查询主数据详情。
     *
     * @param type 主数据分类
     * @param tenantId 租户 ID
     * @param id 主数据 ID
     * @return 主数据详情
     */
    MasterDataRecordVO get(MasterDataType type, Long tenantId, Long id);

    /**
     * 分页查询主数据。
     *
     * @param type 主数据分类
     * @param tenantId 租户 ID
     * @param keyword 编码或名称关键字
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页大小，上限 200
     * @return 分页结果
     */
    PageResult<MasterDataRecordVO> page(MasterDataType type, Long tenantId, String keyword, int pageNo, int pageSize);

    /**
     * 按父子关系查询主数据树原始列表。
     *
     * @param type 主数据分类
     * @param tenantId 租户 ID
     * @return 按父级和 ID 排序后的主数据列表
     */
    List<MasterDataRecordVO> tree(MasterDataType type, Long tenantId);
}
