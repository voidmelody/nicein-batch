package kr.co.nice.nicein.service;

import jakarta.annotation.PostConstruct;
import kr.co.nice.nicein.config.quartz.QuartzJobListener;
import kr.co.nice.nicein.config.quartz.QuartzTriggerListener;
import kr.co.nice.nicein.job.quartz.QuartzBatchJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class QuartzService {
    private final Scheduler scheduler;

    @Value("${job.name}")
    public String JOB_NAME;

    @Value("${job.cron}")
    public String JOB_CRON;

    @PostConstruct
    public void init() throws Exception{
        scheduler.clear();
        scheduler.getListenerManager().addJobListener(new QuartzJobListener());
        scheduler.getListenerManager().addTriggerListener(new QuartzTriggerListener());

        addJob(QuartzBatchJob.class, "BatchJob", "batch Job", null, JOB_CRON);
    }

    // Job 추가
    public <T extends Job> void addJob(Class<? extends Job> job, String name, String desc, Map paramMap, String cron) throws SchedulerException {
        JobDetail jobDetail = buildJobDetail(job,name,desc,paramMap);
        Trigger trigger = BuildCronTrigger(cron);
        if(scheduler.checkExists(jobDetail.getKey())){
            scheduler.deleteJob(jobDetail.getKey());
        }
        scheduler.scheduleJob(jobDetail,trigger);
    }

    // JobDetail 생성
    public <T extends Job> JobDetail buildJobDetail(Class<? extends Job> job, String name, String desc, Map paramMap){
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(JOB_NAME, name);
//        jobDataMap.put("executeCount", 1);

        return JobBuilder
                .newJob(job)
                .withIdentity(name)
                .withDescription(desc)
                .usingJobData(jobDataMap)
                .build();
    }

    //Trigger 생성
    private Trigger BuildCronTrigger(String cronExp){
        return TriggerBuilder.newTrigger()
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExp))
                .build();
    }
}
