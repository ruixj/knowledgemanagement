package com.huadian.config;


import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.mybatis.spring.boot.autoconfigure.SqlSessionFactoryBeanCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.beans.FeatureDescriptor;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@MapperScan(
        basePackages = "com.huadian.dao",
        sqlSessionTemplateRef = "mysqlSqlSessionTemplate"
)
@EnableConfigurationProperties({MybatisProperties.class})
public class MyBatisConfig implements InitializingBean
{
    private static final Logger logger = LoggerFactory.getLogger(MyBatisConfig.class);
    private final MybatisProperties properties;
    private final Interceptor[] interceptors;
    private final TypeHandler[] typeHandlers;
    private final LanguageDriver[] languageDrivers;
    private final ResourceLoader resourceLoader;
    private final DatabaseIdProvider databaseIdProvider;
    private final List<ConfigurationCustomizer> configurationCustomizers;
    private final List<SqlSessionFactoryBeanCustomizer> sqlSessionFactoryBeanCustomizers;

    public MyBatisConfig(MybatisProperties properties,
                         ObjectProvider<Interceptor[]> interceptorsProvider,
                         ObjectProvider<TypeHandler[]> typeHandlersProvider,
                         ObjectProvider<LanguageDriver[]> languageDriversProvider,
                         ResourceLoader resourceLoader,
                         ObjectProvider<DatabaseIdProvider> databaseIdProvider,
                         ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider, ObjectProvider<List<SqlSessionFactoryBeanCustomizer>> sqlSessionFactoryBeanCustomizers) {
        this.properties = properties;
        this.interceptors = (Interceptor[])interceptorsProvider.getIfAvailable();
        this.typeHandlers = (TypeHandler[])typeHandlersProvider.getIfAvailable();
        this.languageDrivers = (LanguageDriver[])languageDriversProvider.getIfAvailable();
        this.resourceLoader = resourceLoader;
        this.databaseIdProvider = (DatabaseIdProvider)databaseIdProvider.getIfAvailable();
        this.configurationCustomizers = (List)configurationCustomizersProvider.getIfAvailable();
        this.sqlSessionFactoryBeanCustomizers = (List)sqlSessionFactoryBeanCustomizers.getIfAvailable();
    }

    public void afterPropertiesSet() {
        this.checkConfigFileExists();
    }

    private void checkConfigFileExists() {
        if (this.properties.isCheckConfigLocation() && StringUtils.hasText(this.properties.getConfigLocation())) {
            Resource resource = this.resourceLoader.getResource(this.properties.getConfigLocation());
            Assert.state(resource.exists(), "Cannot find config location: " + resource + " (please add config file or check your Mybatis configuration)");
        }

    }



    private void applyConfiguration(SqlSessionFactoryBean factory) {
        MybatisProperties.CoreConfiguration coreConfiguration = this.properties.getConfiguration();
        org.apache.ibatis.session.Configuration configuration = null;
        if (coreConfiguration != null || !StringUtils.hasText(this.properties.getConfigLocation())) {
            configuration = new org.apache.ibatis.session.Configuration();
        }

        if (configuration != null && coreConfiguration != null) {
            coreConfiguration.applyTo(configuration);
        }

        if (configuration != null && !CollectionUtils.isEmpty(this.configurationCustomizers)) {
            Iterator var4 = this.configurationCustomizers.iterator();

            while(var4.hasNext()) {
                ConfigurationCustomizer customizer = (ConfigurationCustomizer)var4.next();
                customizer.customize(configuration);
            }
        }

        factory.setConfiguration(configuration);
    }

    private void applySqlSessionFactoryBeanCustomizers(SqlSessionFactoryBean factory) {
        if (!CollectionUtils.isEmpty(this.sqlSessionFactoryBeanCustomizers)) {
            Iterator var2 = this.sqlSessionFactoryBeanCustomizers.iterator();

            while(var2.hasNext()) {
                SqlSessionFactoryBeanCustomizer customizer = (SqlSessionFactoryBeanCustomizer)var2.next();
                customizer.customize(factory);
            }
        }

    }
      @Primary
      @Bean(name = "mysqlSqlSessionFactory")
      public SqlSessionFactory sqlSessionFactory(@Qualifier("mysqlDataSource") DataSource dataSource) throws Exception {
          SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
          factory.setDataSource(dataSource);
          if (this.properties.getConfiguration() == null || this.properties.getConfiguration().getVfsImpl() == null) {
              factory.setVfs(SpringBootVFS.class);
          }

          if (StringUtils.hasText(this.properties.getConfigLocation())) {
              factory.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
          }

          this.applyConfiguration(factory);
          if (this.properties.getConfigurationProperties() != null) {
              factory.setConfigurationProperties(this.properties.getConfigurationProperties());
          }

          if (!ObjectUtils.isEmpty(this.interceptors)) {
              factory.setPlugins(this.interceptors);
          }

          if (this.databaseIdProvider != null) {
              factory.setDatabaseIdProvider(this.databaseIdProvider);
          }

          if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
              factory.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
          }

          if (this.properties.getTypeAliasesSuperType() != null) {
              factory.setTypeAliasesSuperType(this.properties.getTypeAliasesSuperType());
          }

          if (StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
              factory.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
          }

          if (!ObjectUtils.isEmpty(this.typeHandlers)) {
              factory.setTypeHandlers(this.typeHandlers);
          }

          Resource[] mapperLocations = this.properties.resolveMapperLocations();
          if (!ObjectUtils.isEmpty(mapperLocations)) {
              factory.setMapperLocations(mapperLocations);
          }

          Set<String> factoryPropertyNames = (Set) Stream.of((new BeanWrapperImpl(SqlSessionFactoryBean.class)).getPropertyDescriptors()).map(FeatureDescriptor::getName).collect(Collectors.toSet());
          Class<? extends LanguageDriver> defaultLanguageDriver = this.properties.getDefaultScriptingLanguageDriver();
          if (factoryPropertyNames.contains("scriptingLanguageDrivers") && !ObjectUtils.isEmpty(this.languageDrivers)) {
              factory.setScriptingLanguageDrivers(this.languageDrivers);
              if (defaultLanguageDriver == null && this.languageDrivers.length == 1) {
                  defaultLanguageDriver = this.languageDrivers[0].getClass();
              }
          }

          if (factoryPropertyNames.contains("defaultScriptingLanguageDriver")) {
              factory.setDefaultScriptingLanguageDriver(defaultLanguageDriver);
          }

          this.applySqlSessionFactoryBeanCustomizers(factory);
          return factory.getObject();
      }

      @Primary
      @Bean(name = "mysqlSqlSessionTemplate")
      public SqlSessionTemplate sqlSessionTemplate( @Qualifier("mysqlSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
            ExecutorType executorType = this.properties.getExecutorType();
            return executorType != null ? new SqlSessionTemplate(sqlSessionFactory, executorType) : new SqlSessionTemplate(sqlSessionFactory);
      }

        @Primary
        @Bean(name = "mysqlTransactionManager")
        public DataSourceTransactionManager mysqlTransactionManager(
                @Qualifier("mysqlDataSource") DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }


}
