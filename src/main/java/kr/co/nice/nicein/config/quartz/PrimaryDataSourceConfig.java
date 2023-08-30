package kr.co.nice.nicein.config.quartz;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
@Getter
@Setter
public class PrimaryDataSourceConfig {

    private String driverClassName;
    private String url;
    private String username;
    private String password;

    @Primary
    @Bean(name = "primaryDataSource")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create()
                .driverClassName(driverClassName)
                .url(url)
                .username(username)
                .password(password)
                .build();
    }
}
