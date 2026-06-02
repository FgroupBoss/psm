package com.fgroupboss.ai.psm.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemoInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String service;
    private String module;
    private String version;
}
