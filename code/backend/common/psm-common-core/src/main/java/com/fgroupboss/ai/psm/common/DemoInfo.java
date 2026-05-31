package com.fgroupboss.ai.psm.common;

import java.io.Serializable;

public class DemoInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String service;
    private String module;
    private String version;

    public DemoInfo() {
    }

    public DemoInfo(String service, String module, String version) {
        this.service = service;
        this.module = module;
        this.version = version;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

