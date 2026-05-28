package com.fgroupboss.ai.psm.video.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 视频平台 URL 模板与自动转报警配置。
 */
@Data
@Component
@ConfigurationProperties(prefix = "psm.video")
public class VideoPlatformProperties {

    private String liveUrlPattern = "https://video.example.com/live/{platformCode}/{cameraCode}?token={token}&expires={expiresAt}";
    private String playbackUrlPattern =
            "https://video.example.com/playback/{platformCode}/{cameraCode}?start={startAt}&end={endAt}&token={token}&expires={expiresAt}";
    private int urlTokenTtlSeconds = 300;
    private String autoAlarmSeverities = "HIGH,CRITICAL";
    private int ingestDedupMinutes = 5;

    public Set<String> autoAlarmSeveritySet() {
        if (autoAlarmSeverities == null || autoAlarmSeverities.trim().isEmpty()) {
            return Collections.emptySet();
        }
        Set<String> set = new HashSet<String>();
        for (String part : autoAlarmSeverities.split(",")) {
            if (part != null && !part.trim().isEmpty()) {
                set.add(part.trim().toUpperCase());
            }
        }
        return set;
    }

    public boolean shouldAutoAlarm(String severity) {
        if (severity == null) {
            return false;
        }
        return autoAlarmSeveritySet().contains(severity.trim().toUpperCase());
    }
}
