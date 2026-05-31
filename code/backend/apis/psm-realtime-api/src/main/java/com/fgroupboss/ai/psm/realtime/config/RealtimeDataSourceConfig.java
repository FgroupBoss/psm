package com.fgroupboss.ai.psm.realtime.config.config;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * 实时感知域多数据源配置 — alarm 库（主） + location 库 + video 库。
 * 第一阶段不合库，保留原 psm_alarm / psm_location / psm_video 独立 schema。
 */
@Configuration
@MapperScan(basePackages = {
        "com.fgroupboss.ai.psm.realtime.alarm.mapper",
        "com.fgroupboss.ai.psm.realtime.location.mapper",
        "com.fgroupboss.ai.psm.realtime.video.mapper"
})
public class RealtimeDataSourceConfig {

    @Primary
    @Bean("alarmDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.alarm")
    public DataSource alarmDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("locationDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.location")
    public DataSource locationDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("videoDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.video")
    public DataSource videoDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean("alarmSqlSessionFactory")
    public SqlSessionFactory alarmSqlSessionFactory(@Qualifier("alarmDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:/mapper/alarm/**/*.xml"));
        return factory.getObject();
    }

    @Bean("locationSqlSessionFactory")
    public SqlSessionFactory locationSqlSessionFactory(@Qualifier("locationDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:/mapper/location/**/*.xml"));
        return factory.getObject();
    }

    @Bean("videoSqlSessionFactory")
    public SqlSessionFactory videoSqlSessionFactory(@Qualifier("videoDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:/mapper/video/**/*.xml"));
        return factory.getObject();
    }

    @Primary
    @Bean("alarmTransactionManager")
    public DataSourceTransactionManager alarmTransactionManager(@Qualifier("alarmDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("locationTransactionManager")
    public DataSourceTransactionManager locationTransactionManager(@Qualifier("locationDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("videoTransactionManager")
    public DataSourceTransactionManager videoTransactionManager(@Qualifier("videoDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
