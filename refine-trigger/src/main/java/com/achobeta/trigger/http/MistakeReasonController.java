package com.achobeta.trigger.http;

import com.achobeta.api.dto.MistakeReasonRequestDTO;
import com.achobeta.api.dto.MistakeReasonResponseDTO;
import com.achobeta.domain.mistake.model.valobj.MistakeReasonVO;
import com.achobeta.domain.mistake.service.IMistakeReasonService;
import com.achobeta.types.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Auth : Malog
 * @Desc : 错因管理控制器
 * @Time : 2025/11/10
 */
@Slf4j
@Validated
@RestController()
@CrossOrigin("${app.config.cross-origin}:*")
@RequestMapping("/api/${app.config.api-version}/mistake-reason/")
@RequiredArgsConstructor
public class MistakeReasonController {

    private final IMistakeReasonService mistakeReasonService;

    /**
     * 切换错因状态
     *
     * @param requestDTO 错因管理请求DTO
     * @param reasonName 要切换的错因名称
     * @return 错因管理响应
     */
    @PostMapping("toggle/{reasonName}")
    public Response<MistakeReasonResponseDTO> toggleMistakeReason(
            @Valid @RequestBody MistakeReasonRequestDTO requestDTO,
            @PathVariable String reasonName) {
        try {
            log.info("用户切换错因状态开始，userId:{} questionId:{} reasonName:{}",
                    requestDTO.getUserId(), requestDTO.getQuestionId(), reasonName);

            // 转换DTO为领域值对象
            MistakeReasonVO reasonVO = convertToVO(requestDTO);

            // 调用领域服务
            MistakeReasonVO responseVO = mistakeReasonService.toggleMistakeReason(reasonVO, reasonName);

            // 转换为响应DTO
            MistakeReasonResponseDTO response = convertToResponseDTO(responseVO);

            if (response.getSuccess()) {
                log.info("用户切换错因状态成功，userId:{} questionId:{} reasonName:{}",
                        requestDTO.getUserId(), requestDTO.getQuestionId(), reasonName);
                return Response.SYSTEM_SUCCESS(response);
            } else {
                log.warn("用户切换错因状态失败，userId:{} questionId:{} reasonName:{} message:{}",
                        requestDTO.getUserId(), requestDTO.getQuestionId(), reasonName, response.getMessage());
                return Response.<MistakeReasonResponseDTO>builder()
                        .code(Response.SERVICE_ERROR().getCode())
                        .info(response.getMessage())
                        .data(response)
                        .build();
            }
        } catch (Exception e) {
            log.error("用户切换错因状态时发生异常，userId:{} questionId:{} reasonName:{}",
                    requestDTO.getUserId(), requestDTO.getQuestionId(), reasonName, e);
            return Response.SERVICE_ERROR("系统异常: " + e.getMessage());
        }
    }

    /**
     * 更新其他原因文本
     *
     * @param requestDTO 错因管理请求DTO
     * @return 错因管理响应
     */
    @PostMapping("update-other-reason")
    public Response<MistakeReasonResponseDTO> updateOtherReasonText(
            @Valid @RequestBody MistakeReasonRequestDTO requestDTO) {
        try {
            log.info("用户更新其他原因开始，userId:{} questionId:{}",
                    requestDTO.getUserId(), requestDTO.getQuestionId());

            // 转换DTO为领域值对象
            MistakeReasonVO reasonVO = convertToVO(requestDTO);

            // 调用领域服务
            MistakeReasonVO responseVO = mistakeReasonService.updateOtherReasonText(reasonVO);

            // 转换为响应DTO
            MistakeReasonResponseDTO response = convertToResponseDTO(responseVO);

            if (response.getSuccess()) {
                log.info("用户更新其他原因成功，userId:{} questionId:{}",
                        requestDTO.getUserId(), requestDTO.getQuestionId());
                return Response.SYSTEM_SUCCESS(response);
            } else {
                log.warn("用户更新其他原因失败，userId:{} questionId:{} message:{}",
                        requestDTO.getUserId(), requestDTO.getQuestionId(), response.getMessage());
                return Response.<MistakeReasonResponseDTO>builder()
                        .code(Response.SERVICE_ERROR().getCode())
                        .info(response.getMessage())
                        .data(response)
                        .build();
            }
        } catch (Exception e) {
            log.error("用户更新其他原因时发生异常，userId:{} questionId:{}",
                    requestDTO.getUserId(), requestDTO.getQuestionId(), e);
            return Response.SERVICE_ERROR("系统异常: " + e.getMessage());
        }
    }

    /**
     * 获取错因信息
     *
     * @param userId 用户ID
     * @param questionId 题目ID
     * @return 错因管理响应
     */
    @GetMapping("get")
    public Response<MistakeReasonResponseDTO> getMistakeReasons(
            @RequestParam String userId,
            @RequestParam String questionId) {
        try {
            log.info("获取错因信息开始，userId:{} questionId:{}", userId, questionId);

            // 调用领域服务
            MistakeReasonVO responseVO = mistakeReasonService.getMistakeReasons(userId, questionId);

            // 转换为响应DTO
            MistakeReasonResponseDTO response = convertToResponseDTO(responseVO);

            if (response.getSuccess()) {
                log.info("获取错因信息成功，userId:{} questionId:{}", userId, questionId);
                return Response.SYSTEM_SUCCESS(response);
            } else {
                log.warn("获取错因信息失败，userId:{} questionId:{} message:{}",
                        userId, questionId, response.getMessage());
                return Response.<MistakeReasonResponseDTO>builder()
                        .code(Response.SERVICE_ERROR().getCode())
                        .info(response.getMessage())
                        .data(response)
                        .build();
            }
        } catch (Exception e) {
            log.error("获取错因信息时发生异常，userId:{} questionId:{}", userId, questionId, e);
            return Response.SERVICE_ERROR("系统异常: " + e.getMessage());
        }
    }

    /**
     * 将请求DTO转换为领域值对象
     */
    private MistakeReasonVO convertToVO(MistakeReasonRequestDTO requestDTO) {
        return MistakeReasonVO.builder()
                .userId(requestDTO.getUserId())
                .questionId(requestDTO.getQuestionId())
                .isCareless(requestDTO.getIsCareless())
                .isUnfamiliar(requestDTO.getIsUnfamiliar())
                .isCalculateErr(requestDTO.getIsCalculateErr())
                .isTimeShortage(requestDTO.getIsTimeShortage())
                .otherReason(requestDTO.getOtherReason())
                .otherReasonText(requestDTO.getOtherReasonText())
                .build();
    }

    /**
     * 将领域值对象转换为响应DTO
     */
    private MistakeReasonResponseDTO convertToResponseDTO(MistakeReasonVO reasonVO) {
        if (reasonVO == null) {
            return MistakeReasonResponseDTO.builder()
                    .success(false)
                    .message("系统错误：响应对象为空")
                    .build();
        }

        return MistakeReasonResponseDTO.builder()
                .userId(reasonVO.getUserId())
                .questionId(reasonVO.getQuestionId())
                .isCareless(reasonVO.getIsCareless())
                .isUnfamiliar(reasonVO.getIsUnfamiliar())
                .isCalculateErr(reasonVO.getIsCalculateErr())
                .isTimeShortage(reasonVO.getIsTimeShortage())
                .otherReason(reasonVO.getOtherReason())
                .otherReasonText(reasonVO.getOtherReasonText())
                .success(reasonVO.getSuccess())
                .message(reasonVO.getMessage())
                .build();
    }
}
