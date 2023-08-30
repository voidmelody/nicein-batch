package kr.co.nice.nicein.job.batch;

import kr.co.nice.nicein.service.HrisService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class saveHrisTasklet implements Tasklet {
    private final HrisService hrisService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        hrisService.start();
        return RepeatStatus.FINISHED;
    }
}
