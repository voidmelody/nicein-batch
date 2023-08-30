package kr.co.nice.nicein.config.batch;

import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class BatchConfig extends DefaultBatchConfiguration {
    private final DataSource dataSource;

    public BatchConfig(@Qualifier("quartzDataSource")DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected DataSource getDataSource() {
        return this.dataSource;
    }

}
