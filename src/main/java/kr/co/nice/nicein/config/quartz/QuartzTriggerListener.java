package kr.co.nice.nicein.config.quartz;


import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;

@Slf4j
public class QuartzTriggerListener implements TriggerListener {
    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        log.info("Trigger 실행");
    }

    // 결과가 True이면 QuartzJobListener의 jobExecutionVetoed(Job중단) 실행
    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        log.info("Trigger 상태 체크");
//        JobDataMap map = context.getJobDetail().getJobDataMap();
//
//        int executeCount = 0;
//        if(map.containsKey("executeCount")){
//            executeCount = map.getInt("executeCount");
//        }
//
//        log.info("executeCount는 " + executeCount + "회 입니다");
//        log.info("vetoJobExecution " + trigger.getKey().toString());
//        return executeCount >= 2;
        return false;
    }

    @Override
    public void triggerMisfired(Trigger trigger) {

    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context, Trigger.CompletedExecutionInstruction triggerInstructionCode) {
        log.info("Trigger 성공");
    }
}
