package com.fgroupboss.ai.psm.identity.file.storage;

import lombok.Data;

@Data
public class HealthCheckResult {

    private String status;
    private String message;

    public static HealthCheckResult ok(String message) {
        HealthCheckResult r = new HealthCheckResult();
        r.setStatus("OK");
        r.setMessage(message);
        return r;
    }

    public static HealthCheckResult failed(String message) {
        HealthCheckResult r = new HealthCheckResult();
        r.setStatus("FAILED");
        r.setMessage(message);
        return r;
    }

    public static HealthCheckResult skipped(String message) {
        HealthCheckResult r = new HealthCheckResult();
        r.setStatus("SKIPPED");
        r.setMessage(message);
        return r;
    }
}
