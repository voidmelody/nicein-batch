package kr.co.nice.nicein.job.batch;

import kr.co.nice.nicein.service.ApiService;
import kr.co.nice.nicein.service.HrisService;
import kr.co.nice.nicein.service.SaveService;
import kr.co.nice.nicein.service.ValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@RequiredArgsConstructor
@Configuration
public class BatchJob {
    private final ApiService apiService;
    private final SaveService saveService;
    private final ValidateService validateService;
    private final HrisService hrisService;

    @Bean(name="BatchJob")
    public Job getUserApiJob(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new JobBuilder("BatchJob", jobRepository)
                .start(getDeptApiStep(jobRepository,platformTransactionManager))
                .next(getUserApiStep(jobRepository,platformTransactionManager))
                .next(setManagerStep(jobRepository,platformTransactionManager))
                .next(saveHrisStep(jobRepository,platformTransactionManager)) // HRIS 입퇴사정보.
//                .next(processCmpEndStep(jobRepository, platformTransactionManager)) // 퇴사자 처리 안하기로 결정.
                .build();
    }

    @Bean
    public Step saveHrisStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("saveHrisStep", jobRepository)
                .tasklet(new saveHrisTasklet(hrisService), platformTransactionManager)
                .build();
    }


    @Bean
    public Step processCmpEndStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("processCmpEndStep", jobRepository)
                .tasklet(new processCmpEndTasklet(validateService), platformTransactionManager)
                .build();
    }


    @Bean
    public Step getUserApiStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("getUserApiStep", jobRepository)
                .tasklet(new getUserApiTasklet(apiService), platformTransactionManager)
                .build();
    }
    @Bean
    public Step getDeptApiStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("getDeptApiStep", jobRepository)
                .tasklet(new getDeptApiTasklet(apiService), platformTransactionManager)
                .build();
    }

    @Bean
    public Step setManagerStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("setManagerStep", jobRepository)
                .tasklet(new setManagerTasklet(saveService), platformTransactionManager)
                .build();
    }
}
