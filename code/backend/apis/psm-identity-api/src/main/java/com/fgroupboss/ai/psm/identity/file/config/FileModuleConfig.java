package com.fgroupboss.ai.psm.identity.file.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 文件模块 Spring 配置：绑定 {@link FileProperties}。
 */
@Configuration
@EnableConfigurationProperties(FileProperties.class)
public class FileModuleConfig {
}
