package com.fgroupboss.ai.psm.identity.config;

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
 * 身份域多数据源配置 — 合并 auth / iam / audit / masterdata 独立 schema。
 * 暂不合库，保留各业务独立数据库。
 */
@Configuration
@MapperScan(basePackages = {
        "com.fgroupboss.ai.psm.identity.auth.mapper",
        "com.fgroupboss.ai.psm.identity.iam.mapper",
        "com.fgroupboss.ai.psm.identity.audit.mapper",
        "com.fgroupboss.ai.psm.identity.masterdata.mapper"
})
public class IdentityDataSourceConfig {

    @Primary
    @Bean("authDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.auth")
    public DataSource authDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("iamDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.iam")
    public DataSource iamDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean("authSqlSessionFactory")
    public SqlSessionFactory authSqlSessionFactory(@Qualifier("authDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:/mapper/auth/**/*.xml"));
        return factory.getObject();
    }

    @Bean("iamSqlSessionFactory")
    public SqlSessionFactory iamSqlSessionFactory(@Qualifier("iamDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:/mapper/iam/**/*.xml"));
        return factory.getObject();
    }

    @Primary
    @Bean("authTransactionManager")
    public DataSourceTransactionManager authTransactionManager(@Qualifier("authDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("iamTransactionManager")
    public DataSourceTransactionManager iamTransactionManager(@Qualifier("iamDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
