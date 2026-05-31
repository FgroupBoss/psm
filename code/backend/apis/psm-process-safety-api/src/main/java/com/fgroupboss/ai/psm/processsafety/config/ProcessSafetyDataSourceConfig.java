package com.fgroupboss.ai.psm.processsafety.config.config;

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
 * 工艺安全域多数据源配置 — moc 库（主） + pha 库 + pssr 库 + barrier 库。
 * 第一阶段不合库，保留原 psm_moc / psm_pha / psm_pssr / psm_barrier 独立 schema。
 */
@Configuration
@MapperScan(basePackages = {
        "com.fgroupboss.ai.psm.processsafety.moc.mapper",
        "com.fgroupboss.ai.psm.processsafety.pha.mapper",
        "com.fgroupboss.ai.psm.processsafety.pssr.mapper",
        "com.fgroupboss.ai.psm.processsafety.barrier.mapper"
})
public class ProcessSafetyDataSourceConfig {

    @Primary
    @Bean("mocDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.moc")
    public DataSource mocDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("phaDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.pha")
    public DataSource phaDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("pssrDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.pssr")
    public DataSource pssrDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("barrierDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.barrier")
    public DataSource barrierDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean("mocSqlSessionFactory")
    public SqlSessionFactory mocSqlSessionFactory(@Qualifier("mocDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:/mapper/moc/**/*.xml"));
        return factory.getObject();
    }

    @Bean("phaSqlSessionFactory")
    public SqlSessionFactory phaSqlSessionFactory(@Qualifier("phaDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:/mapper/pha/**/*.xml"));
        return factory.getObject();
    }

    @Bean("pssrSqlSessionFactory")
    public SqlSessionFactory pssrSqlSessionFactory(@Qualifier("pssrDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:/mapper/pssr/**/*.xml"));
        return factory.getObject();
    }

    @Bean("barrierSqlSessionFactory")
    public SqlSessionFactory barrierSqlSessionFactory(@Qualifier("barrierDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:/mapper/barrier/**/*.xml"));
        return factory.getObject();
    }

    @Primary
    @Bean("mocTransactionManager")
    public DataSourceTransactionManager mocTransactionManager(@Qualifier("mocDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("phaTransactionManager")
    public DataSourceTransactionManager phaTransactionManager(@Qualifier("phaDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("pssrTransactionManager")
    public DataSourceTransactionManager pssrTransactionManager(@Qualifier("pssrDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("barrierTransactionManager")
    public DataSourceTransactionManager barrierTransactionManager(@Qualifier("barrierDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
