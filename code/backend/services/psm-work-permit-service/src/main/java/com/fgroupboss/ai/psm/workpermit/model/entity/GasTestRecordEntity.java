package com.fgroupboss.ai.psm.workpermit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("gas_test_record")
public class GasTestRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long workPermitId;
    private String gasName;
    private String measuredValue;
    private String unit;
    private Integer qualified;
    private Date testedAt;
    private String testerName;
    private String remark;
    private Date createdAt;
    private Integer deleted;
}
