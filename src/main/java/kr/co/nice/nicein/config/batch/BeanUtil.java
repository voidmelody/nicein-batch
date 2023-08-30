package kr.co.nice.nicein.config.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


// 등록한 Bean(Job)이름을 줌으로써 Context에서 해당하는 Bean(Job)을 찾아줌.
@RequiredArgsConstructor
@Component
public class BeanUtil {
    private final ApplicationContext applicationContext;

    public Object getBean(String name){
        return applicationContext.getBean(name);
    }
}