package kr.co.nice.nicein.job.quartz;

import kr.co.nice.nicein.config.batch.BeanUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@PersistJobDataAfterExecution // jobDataMap 데이터 수정 이후 다음 실행에도 데이터 반영
@DisallowConcurrentExecution
@Component
public class QuartzBatchJob implements org.quartz.Job {
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private BeanUtil beanUtil;

    @Value("${job.name}")
    private String jobName;

//    private int executeCount = 0;
    @SneakyThrows
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDetail jobDetail = context.getJobDetail();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
//        if(jobDataMap.containsKey("executeCount")){
//            executeCount = jobDataMap.getInt("executeCount");
//        }
//        jobDataMap.put("executeCount", ++executeCount);

        Job job = (Job) beanUtil.getBean((String) jobDataMap.get(jobName));

        JobParameters jobParameters = new JobParametersBuilder()
                .addDate("curDate", new Date())
                .toJobParameters();

        jobLauncher.run(job,jobParameters);
    }
}
