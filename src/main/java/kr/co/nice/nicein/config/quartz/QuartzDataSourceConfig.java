package kr.co.nice.nicein.config.quartz;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Configuration
@ConfigurationProperties(prefix = "spring.datasource-quartz")
@Getter
@Setter
public class QuartzDataSourceConfig {
    private String driverClassName;
    private String url;
    private String username;
    private String password;

    @Bean(name = "quartzDataSource")
    public DataSource quartzDataSource() {
        return DataSourceBuilder.create()
                .driverClassName(driverClassName)
                .url(url)
                .username(username)
                .password(password)
                .build();
    }
}
