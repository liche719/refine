package port;

import com.achobeta.Application;
import com.achobeta.api.dto.question.QuestionResponseDTO;
import com.achobeta.domain.question.adapter.port.AiGenerationService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = Application.class)
class AiGenerationServiceTest {

    @Resource
    private AiGenerationService aiGenerationService;

    @Test
    void generation() {
        QuestionResponseDTO generation = aiGenerationService.Generation("出一道物理学科的，关于的题目");
        System.out.println(generation);
    }
}