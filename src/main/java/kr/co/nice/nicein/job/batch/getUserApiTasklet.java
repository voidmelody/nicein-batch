package kr.co.nice.nicein.job.batch;


import kr.co.nice.nicein.dto.UserResponseDto;
import kr.co.nice.nicein.service.ApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class getUserApiTasklet implements Tasklet {
    private final ApiService apiService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        List<UserResponseDto> userApi = apiService.getUserApi();

        return RepeatStatus.FINISHED;
    }
}
