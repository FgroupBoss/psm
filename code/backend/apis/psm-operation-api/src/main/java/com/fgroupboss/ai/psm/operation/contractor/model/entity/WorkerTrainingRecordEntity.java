package com.fgroupboss.ai.psm.operation.contractor.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("worker_training_record")
public class WorkerTrainingRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long workerId;
    private String trainingName;
    private String trainingResult;
    private LocalDate validFrom;
    private LocalDate validTo;
    private Long fileId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
