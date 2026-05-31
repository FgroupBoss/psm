package com.fgroupboss.ai.psm.risk.config.config;

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
 * 风险管控域多数据源配置 — dual-prevention 库（主） + major-hazard 库 + inspection 库。
 * 第一阶段不合库，保留原 psm_dual_prevention / psm_major_hazard / psm_inspection 独立 schema。
 */
@Configuration
@MapperScan(basePackages = {
        "com.fgroupboss.ai.psm.risk.dualprevention.mapper",
        "com.fgroupboss.ai.psm.risk.majorhazard.mapper",
        "com.fgroupboss.ai.psm.risk.inspection.mapper"
})
public class RiskDataSourceConfig {

    @Primary
    @Bean("dualpreventionDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.dualprevention")
    public DataSource dualpreventionDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("majorhazardDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.majorhazard")
    public DataSource majorhazardDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("inspectionDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.inspection")
    public DataSource inspectionDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean("dualpreventionSqlSessionFactory")
    public SqlSessionFactory dualpreventionSqlSessionFactory(@Qualifier("dualpreventionDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:/mapper/dualprevention/**/*.xml"));
        return factory.getObject();
    }

    @Bean("majorhazardSqlSessionFactory")
    public SqlSessionFactory majorhazardSqlSessionFactory(@Qualifier("majorhazardDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:/mapper/majorhazard/**/*.xml"));
        return factory.getObject();
    }

    @Bean("inspectionSqlSessionFactory")
    public SqlSessionFactory inspectionSqlSessionFactory(@Qualifier("inspectionDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:/mapper/inspection/**/*.xml"));
        return factory.getObject();
    }

    @Primary
    @Bean("dualpreventionTransactionManager")
    public DataSourceTransactionManager dualpreventionTransactionManager(@Qualifier("dualpreventionDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("majorhazardTransactionManager")
    public DataSourceTransactionManager majorhazardTransactionManager(@Qualifier("majorhazardDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("inspectionTransactionManager")
    public DataSourceTransactionManager inspectionTransactionManager(@Qualifier("inspectionDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
