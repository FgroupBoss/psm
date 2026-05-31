package com.fgroupboss.ai.psm.incidentgovernance.config.config;

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
 * 事件与治理域多数据源配置 — incident 库（主） + governance 库 + report 库。
 * 第一阶段不合库，保留原 psm_incident / psm_governance / psm_report 独立 schema。
 */
@Configuration
@MapperScan(basePackages = {
        "com.fgroupboss.ai.psm.incidentgovernance.incident.mapper",
        "com.fgroupboss.ai.psm.incidentgovernance.governance.mapper",
        "com.fgroupboss.ai.psm.incidentgovernance.report.mapper"
})
public class IncidentGovernanceDataSourceConfig {

    @Primary
    @Bean("incidentDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.incident")
    public DataSource incidentDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("governanceDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.governance")
    public DataSource governanceDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("reportDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.report")
    public DataSource reportDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean("incidentSqlSessionFactory")
    public SqlSessionFactory incidentSqlSessionFactory(@Qualifier("incidentDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:/mapper/incident/**/*.xml"));
        return factory.getObject();
    }

    @Bean("governanceSqlSessionFactory")
    public SqlSessionFactory governanceSqlSessionFactory(@Qualifier("governanceDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:/mapper/governance/**/*.xml"));
        return factory.getObject();
    }

    @Bean("reportSqlSessionFactory")
    public SqlSessionFactory reportSqlSessionFactory(@Qualifier("reportDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:/mapper/report/**/*.xml"));
        return factory.getObject();
    }

    @Primary
    @Bean("incidentTransactionManager")
    public DataSourceTransactionManager incidentTransactionManager(@Qualifier("incidentDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("governanceTransactionManager")
    public DataSourceTransactionManager governanceTransactionManager(@Qualifier("governanceDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("reportTransactionManager")
    public DataSourceTransactionManager reportTransactionManager(@Qualifier("reportDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
