package com.fgroupboss.ai.psm.identity.file.support;

import com.fgroupboss.ai.psm.common.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.UUID;

/**
 * 本地磁盘文件存储（一期试点，可替换为 MinIO 等对象存储）。
 */
@Slf4j
@Component
public class LocalFileStorageSupport {

  @Value("${psm.file.storage-root:./data/psm-files}")
  private String storageRoot;

  private Path rootPath;

  @PostConstruct
  public void init() throws IOException {
    rootPath = Paths.get(storageRoot).toAbsolutePath().normalize();
    Files.createDirectories(rootPath);
    log.info("file storage root={}", rootPath);
  }

  public StoredFile store(InputStream inputStream, String originalFileName) {
    String safeName = sanitizeFileName(originalFileName);
    String relative = UUID.randomUUID().toString().replace("-", "") + "_" + safeName;
    Path target = rootPath.resolve(relative).normalize();
    if (!target.startsWith(rootPath)) {
      throw new BusinessException(400, "invalid file path");
    }
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      DigestInputStream digestStream = new DigestInputStream(inputStream, digest);
      Files.createDirectories(target.getParent());
      long size = Files.copy(digestStream, target, StandardCopyOption.REPLACE_EXISTING);
      return new StoredFile(relative, size, toHex(digest.digest()));
    } catch (Exception ex) {
      throw new BusinessException(500, "file storage failed");
    }
  }

  public Path resolve(String storagePath) {
    Path path = rootPath.resolve(storagePath).normalize();
    if (!path.startsWith(rootPath) || !Files.exists(path)) {
      throw new BusinessException(404, "file not found on disk");
    }
    return path;
  }

  private String sanitizeFileName(String fileName) {
    if (!StringUtils.hasText(fileName)) {
      return "upload.bin";
    }
    return fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
  }

  private String toHex(byte[] digest) {
    StringBuilder builder = new StringBuilder(digest.length * 2);
    for (byte value : digest) {
      builder.append(String.format("%02x", value));
    }
    return builder.toString();
  }

  public static final class StoredFile {
    private final String storagePath;
    private final long sizeBytes;
    private final String sha256;

    public StoredFile(String storagePath, long sizeBytes, String sha256) {
      this.storagePath = storagePath;
      this.sizeBytes = sizeBytes;
      this.sha256 = sha256;
    }

    public String getStoragePath() {
      return storagePath;
    }

    public long getSizeBytes() {
      return sizeBytes;
    }

    public String getSha256() {
      return sha256;
    }
  }
}
