package com.achobeta.domain.ai.service.impl;

import com.achobeta.domain.ai.service.IAiService;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import io.reactivex.Flowable;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * @Auth : Malog
 * @Desc : 抽取第一个问题
 * @Time : 2025/11/2 14:34
 */
@Slf4j
@Service
@ConfigurationProperties(prefix = "dashscope")
public class aiService implements IAiService {

    /**
     * 日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger(aiService.class);

    /**
     * 用于收集所有content内容的StringBuilder
     */
    private static final StringBuilder completeResponse = new StringBuilder();

    /**
     * API密钥，用于访问大模型服务。
     */
    @Setter
    private String apiKey;

    /**
     * 使用的大模型名称，默认为 "qwen3-max"。
     */
    @Setter
    private String MODEL_NAME = "qwen3-max";

    /**
     * 抽取第一个问题
     *
     * @param content 输入的文本内容
     * @return 抽取到的第一个问题
     */
    @Override
    public String extractTheFirstQuestion(String content) {
        // 检查输入是否为空
        if (content == null || content.trim().isEmpty()) {
            log.warn("输入内容为空，无法提取问题");
            return "";
        }

        try {
            Generation gen = new Generation();
            // 构造提示词，明确提取规则和示例
            String prompt = """
                    请从以下大段文本中准确识别并提取第一个完整且可识别的题目。提取规则如下：
                    
                    如果该题目是选择题（包括单选或多选），请完整提取题干和所有选项（如 A、B、C、D 等），确保格式清晰、内容连贯；
                    如果该题目是非选择题（如填空题、简答题、编程题等），则仅提取完整的题干信息，不要包含答案、解析或后续无关内容；
                    忽略试卷标题、页眉页脚、考生信息、承诺声明、得分栏等与题目本身无关的内容；
                    以题目编号（如“1.”、“2.”、“一、”等）作为判断起点，但必须确保提取的是一个语义完整、结构清晰的问题；
                    若识别为填空题，需要填的空用四个下划线“____”占位置
                    若无法识别任何有效题目，请返回空字符串。
                    请严格遵循上述规则，仅输出提取结果，不要添加解释或额外内容。
                    
                    示例 1（选择题）：
                    输入文本包含：
                    
                    1.一个C程序的基本结构是(
                    (A)一个主函数和若干个非主函数
                    (B)若干个主函数和若干个非主函数
                    (C)一个主函数和最多一个非主函数
                    (D)若干个主函数和最多一个非主函数\s
                    
                    输出应为：
                    1.一个C程序的基本结构是(
                    (A)一个主函数和若干个非主函数
                    (B)若干个主函数和若干个非主函数
                    (C)一个主函数和最多一个非主函数
                    (D)若干个主函数和最多一个非主函数
                    
                    示例 2（非选择题）：
                    输入文本包含：
                    
                    编写一个C语言函数，实现输入一个整数n，输出其阶乘值。要求使用递归方法实现。
                    输出应为：
                    
                    编写一个C语言函数，实现输入一个整数n，输出其阶乘值。要求使用递归方法实现。
                    """;

            // 构建系统角色消息，定义助手的行为目标
            Message systemMsg = Message.builder()
                    .role(Role.SYSTEM.getValue())
                    .content("你是一个题目题干提取助手，善于从大段文字中提取出第一个出现的完整题目。")
                    .build();

            // 构建用户消息，将提示词与实际内容拼接传入
            Message userMsg = Message.builder()
                    .role(Role.USER.getValue())
                    .content(prompt + "\n" + "请从以下文本中提取第一个问题：\n\n" + content)
                    .build();

            // 设置调用参数，包括API Key、模型名及上下文消息列表
            GenerationParam param = GenerationParam.builder()
                    .apiKey(apiKey)
                    .model(MODEL_NAME)
                    .messages(Arrays.asList(systemMsg, userMsg))
                    .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                    .build();

            // 调用大模型接口获取结果
            GenerationResult result = gen.call(param);

            // 判断返回结果的有效性并提取内容
            if (result != null && result.getOutput() != null
                    && !result.getOutput().getChoices().isEmpty()
                    && result.getOutput().getChoices().get(0).getMessage() != null) {
                return result.getOutput().getChoices().get(0).getMessage().getContent().trim();
            } else {
                log.warn("模型返回结果为空或格式异常");
                return "";
            }

        } catch (Exception e) {
            // 记录异常日志便于排查问题
            log.error("调用大模型提取问题失败，输入内容: {}", content, e);
            return "";
        }
    }

    /**
     * 使用大模型进行问题解答
     *
     * @param question        问题内容
     * @param contentCallback 回调函数，用于处理大模型返回的答案内容
     */
    @Override
    public void aiSolveQuestion(String question, Consumer<String> contentCallback) {
        try {
            String prompt = """
                    你是一位专业的解题助手，请严格按照以下格式和要求解答用户提出的题目：
                    
                    **解题格式要求：**
                    
                    【题目分析】
                    - 仔细阅读题目，理解题目的背景、条件和要求
                    - 明确已知条件和待求目标
                    - 分析题目的类型和特点
                    - 指出解题的关键点和可能的突破口
                    
                    【解题过程】
                    - 按照逻辑顺序，逐步展示完整的解题步骤
                    - 每一步都要有清晰的推理和计算过程
                    - 重要的公式、定理要明确写出
                    - 数值计算要准确，单位要规范
                    - 如果有多种解法，选择最合理的一种详细展示
                    
                    【解题方法】
                    - 明确指出本题使用的核心解题方法（如：代数法、几何法、微积分法、方程法、图像法等）
                    - 说明为什么选择这种方法
                    - 简要介绍该方法的适用条件和优势
                    - 如果涉及特殊技巧或思路，要详细说明
                    
                    【考查知识点】
                    - 列出本题涉及的所有重要知识点
                    - 说明每个知识点在本题中的具体应用
                    - 指出这些知识点之间的联系
                    - 简要说明这些知识点在学科体系中的地位
                    
                    **注意事项：**
                    - 语言要专业、准确、简洁
                    - 逻辑要严密，推理要完整
                    - 重要的结论和答案要用【最终答案】标注
                    - 遇到复杂题目时，可以适当分步骤解释
                    - 确保解题过程的可读性和教学性
                    
                    请严格遵守以上要求，开始解答下面的题目，如果题目包含公式，请以markdown格式给出。
                    """;
            String combinedPrompt = prompt + "\n\n" + question;
            // 创建大模型实例
            Generation gen = new Generation();

            // 构建用户消息，将问题传入
            Message combineMsg = Message.builder()
                    .role(Role.USER.getValue())
                    .content(combinedPrompt)
                    .build();

             // 构建调用参数
            _streamCallWithMessage(gen, combineMsg, contentCallback);
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            logger.error("An exception occurred: {}", e.getMessage());
        }
    }

    // 构建调用参数
    private GenerationParam _buildGenerationParam(Message userMsg) {
        return GenerationParam.builder()
                .apiKey(apiKey)
                .model(MODEL_NAME)
                .messages(Arrays.asList(userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .incrementalOutput(true)
                .build();
    }

    // 处理大模型返回的答案结果
    private static void _handleGenerationResult(GenerationResult result, Consumer<String> contentCallback) {
        try {
            // 检查结果是否为空
            if (result == null || result.getOutput() == null || result.getOutput().getChoices() == null ||
                    result.getOutput().getChoices().isEmpty()) {
                return;
            }

            // 获取第一个choice的message内容
            Message message = result.getOutput().getChoices().get(0).getMessage();

            // 只获取content内容并追加到StringBuilder
            if (message != null && message.getContent() != null) {
                String content = message.getContent();
                completeResponse.append(content);
                // 实时打印当前片段，便于观察流式输出过程
                System.out.print(content);

                // 通过回调函数将内容传递给Controller
                if (contentCallback != null) {
                    contentCallback.accept(content);
                }
            }
        } catch (Exception e) {
            logger.error("处理GenerationResult时出错: {}", e.getMessage());
        }
    }

    // 流式调用大模型
    public void _streamCallWithMessage(Generation gen, Message userMsg, Consumer<String> contentCallback)
            throws NoApiKeyException, ApiException, InputRequiredException {
        GenerationParam param = _buildGenerationParam(userMsg);
        Flowable<GenerationResult> result = gen.streamCall(param);
        result.blockingForEach(resultItem -> _handleGenerationResult(resultItem, contentCallback));

        // TODO 将输出的结果保存在redis中，用于后续的问答
        // 流式输出结束后换行
        System.out.println();
        // 打印完整的收集内容
        System.out.println("\n=== 完整的AI回复内容 ===");
        System.out.println(completeResponse.toString());
    }
}

