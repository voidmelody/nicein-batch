package kr.co.nice.nicein.config.quartz;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Slf4j

@Configuration
public class QuartzConfig {

    private final DataSource quartzDataSource;
    private final PlatformTransactionManager platformTransactionManager;
    private final ApplicationContext applicationContext;

    public QuartzConfig(@Qualifier("quartzDataSource") DataSource quartzDataSource, PlatformTransactionManager platformTransactionManager, ApplicationContext applicationContext) {
        this.quartzDataSource = quartzDataSource;
        this.platformTransactionManager = platformTransactionManager;
        this.applicationContext = applicationContext;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(){
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        AutoWiringSpringBeanJobFactory autoWiringSpringBeanJobFactory = new AutoWiringSpringBeanJobFactory();

        autoWiringSpringBeanJobFactory.setApplicationContext(applicationContext);
        schedulerFactoryBean.setJobFactory(autoWiringSpringBeanJobFactory);


        schedulerFactoryBean.setDataSource(quartzDataSource);

        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setAutoStartup(true);
        schedulerFactoryBean.setTransactionManager(platformTransactionManager);
        schedulerFactoryBean.setQuartzProperties(quartzProperties());
        return schedulerFactoryBean;
    }

    private Properties quartzProperties(){
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("quartz.properties"));
        Properties properties = null;
        try{
            propertiesFactoryBean.afterPropertiesSet();
            properties = propertiesFactoryBean.getObject();
        }catch (IOException e){
            log.error("quartzProperties parse error : {}", e);
        }
        return properties;
    }
}
