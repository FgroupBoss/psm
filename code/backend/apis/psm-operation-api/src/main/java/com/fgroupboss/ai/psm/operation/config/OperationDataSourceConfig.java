package com.fgroupboss.ai.psm.operation.config;

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
 * 作业许可与管控域多数据源配置 — work-permit 库（主） + contractor 库。
 * 第一阶段不合库，保留原 psm_work_permit / psm_contractor 独立 schema。
 */
@Configuration
@MapperScan(basePackages = {
        "com.fgroupboss.ai.psm.operation.workpermit.mapper",
        "com.fgroupboss.ai.psm.operation.contractor.mapper"
})
public class OperationDataSourceConfig {

    @Primary
    @Bean("workpermitDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.workpermit")
    public DataSource workpermitDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("contractorDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.contractor")
    public DataSource contractorDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean("workpermitSqlSessionFactory")
    public SqlSessionFactory workpermitSqlSessionFactory(@Qualifier("workpermitDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:/mapper/workpermit/**/*.xml"));
        return factory.getObject();
    }

    @Bean("contractorSqlSessionFactory")
    public SqlSessionFactory contractorSqlSessionFactory(@Qualifier("contractorDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:/mapper/contractor/**/*.xml"));
        return factory.getObject();
    }

    @Primary
    @Bean("workpermitTransactionManager")
    public DataSourceTransactionManager workpermitTransactionManager(@Qualifier("workpermitDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("contractorTransactionManager")
    public DataSourceTransactionManager contractorTransactionManager(@Qualifier("contractorDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
