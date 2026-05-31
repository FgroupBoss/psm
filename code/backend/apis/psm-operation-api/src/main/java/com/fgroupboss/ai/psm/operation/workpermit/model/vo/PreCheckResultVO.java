package com.fgroupboss.ai.psm.operation.workpermit.model.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PreCheckResultVO {

    private boolean passed;
    private List<String> reasons = new ArrayList<String>();
}
