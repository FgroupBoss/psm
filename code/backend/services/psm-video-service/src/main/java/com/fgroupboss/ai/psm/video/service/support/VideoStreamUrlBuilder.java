package com.fgroupboss.ai.psm.video.service.support;

import com.fgroupboss.ai.psm.video.config.VideoPlatformProperties;
import com.fgroupboss.ai.psm.video.model.entity.VideoCameraEntity;
import com.fgroupboss.ai.psm.video.model.vo.VideoStreamUrlVO;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 根据配置模板生成短时有效的直播/回放 URL 桩。
 */
@Component
public class VideoStreamUrlBuilder {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final VideoPlatformProperties properties;

    public VideoStreamUrlBuilder(VideoPlatformProperties properties) {
        this.properties = properties;
    }

    public VideoStreamUrlVO buildLiveUrl(VideoCameraEntity camera) {
        long expiresAt = Instant.now().getEpochSecond() + properties.getUrlTokenTtlSeconds();
        String token = buildToken(camera, expiresAt);
        String url = applyPattern(properties.getLiveUrlPattern(), camera, token, expiresAt, null, null);
        return toVo(camera.getId(), url, expiresAt);
    }

    public VideoStreamUrlVO buildPlaybackUrl(VideoCameraEntity camera, LocalDateTime startAt, LocalDateTime endAt) {
        long expiresAt = Instant.now().getEpochSecond() + properties.getUrlTokenTtlSeconds();
        String token = buildToken(camera, expiresAt);
        String url = applyPattern(properties.getPlaybackUrlPattern(), camera, token, expiresAt, startAt, endAt);
        return toVo(camera.getId(), url, expiresAt);
    }

    private String applyPattern(String pattern, VideoCameraEntity camera, String token, long expiresAt,
                                LocalDateTime startAt, LocalDateTime endAt) {
        String resolved = pattern == null ? "" : pattern;
        resolved = resolved.replace("{platformCode}", safe(camera.getPlatformCode()));
        resolved = resolved.replace("{cameraCode}", safe(camera.getCameraCode()));
        resolved = resolved.replace("{cameraId}", String.valueOf(camera.getId()));
        resolved = resolved.replace("{token}", token);
        resolved = resolved.replace("{expiresAt}", String.valueOf(expiresAt));
        resolved = resolved.replace("{startAt}", formatTime(startAt));
        resolved = resolved.replace("{endAt}", formatTime(endAt));
        return resolved;
    }

    private String buildToken(VideoCameraEntity camera, long expiresAt) {
        String raw = camera.getTenantId() + ":" + camera.getId() + ":" + expiresAt;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                builder.append(String.format("%02x", hash[i]));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException ex) {
            return String.valueOf(raw.hashCode());
        }
    }

    private String formatTime(LocalDateTime time) {
        if (time == null) {
            return "";
        }
        return FORMATTER.format(time.atZone(ZoneId.systemDefault()));
    }

    private String safe(String value) {
        return StringUtils.hasText(value) ? value.trim() : "";
    }

    private VideoStreamUrlVO toVo(Long cameraId, String url, long expiresAt) {
        VideoStreamUrlVO vo = new VideoStreamUrlVO();
        vo.setCameraId(cameraId);
        vo.setUrl(url);
        vo.setExpiresAtEpochSeconds(expiresAt);
        return vo;
    }
}
