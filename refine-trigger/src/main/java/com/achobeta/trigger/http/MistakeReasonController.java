package com.achobeta.trigger.http;

import com.achobeta.api.dto.MistakeReasonRequestDTO;
import com.achobeta.api.dto.MistakeReasonResponseDTO;
import com.achobeta.api.dto.MistakeReasonToggleRequestDTO;
import com.achobeta.api.dto.StudyNoteRequestDTO;
import com.achobeta.api.dto.StudyNoteResponseDTO;
import com.achobeta.domain.mistake.model.valobj.MistakeReasonVO;
import com.achobeta.domain.mistake.model.valobj.StudyNoteVO;
import com.achobeta.domain.mistake.service.IMistakeReasonService;
import com.achobeta.domain.mistake.service.IStudyNoteService;
import com.achobeta.types.Response;
import com.achobeta.types.annotation.GlobalInterception;
import com.achobeta.types.common.UserContext;
import com.achobeta.types.enums.GlobalServiceStatusCode;
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
    private final IStudyNoteService studyNoteService;

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
            MistakeReasonVO reasonVO = convertToMistakeReasonVO(requestDTO);

            // 调用领域服务
            MistakeReasonVO responseVO = mistakeReasonService.toggleMistakeReason(reasonVO, reasonName);

            // 转换为响应DTO
            MistakeReasonResponseDTO response = convertToMistakeReasonResponseDTO(responseVO);

            if (response.getSuccess()) {
                log.info("用户切换错因状态成功，userId:{} questionId:{} reasonName:{}",
                        requestDTO.getUserId(), requestDTO.getQuestionId(), reasonName);
                return Response.<MistakeReasonResponseDTO>builder()
                        .code(GlobalServiceStatusCode.MISTAKE_REASON_TOGGLE_SUCCESS.getCode())
                        .info(GlobalServiceStatusCode.MISTAKE_REASON_TOGGLE_SUCCESS.getMessage())
                        .data(response)
                        .build();
            } else {
                log.warn("用户切换错因状态失败，userId:{} questionId:{} reasonName:{} message:{}",
                        requestDTO.getUserId(), requestDTO.getQuestionId(), reasonName, response.getMessage());
                return Response.<MistakeReasonResponseDTO>builder()
                        .code(GlobalServiceStatusCode.MISTAKE_REASON_TOGGLE_FAILED.getCode())
                        .info(response.getMessage())
                        .data(response)
                        .build();
            }
        } catch (Exception e) {
            log.error("用户切换错因状态时发生异常，userId:{} questionId:{} reasonName:{}",
                    requestDTO.getUserId(), requestDTO.getQuestionId(), reasonName, e);
            return Response.<MistakeReasonResponseDTO>builder()
                    .code(GlobalServiceStatusCode.MISTAKE_REASON_SYSTEM_ERROR.getCode())
                    .info("系统异常: " + e.getMessage())
                    .build();
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
            MistakeReasonVO reasonVO = convertToMistakeReasonVO(requestDTO);

            // 调用领域服务
            MistakeReasonVO responseVO = mistakeReasonService.updateOtherReasonText(reasonVO);

            // 转换为响应DTO
            MistakeReasonResponseDTO response = convertToMistakeReasonResponseDTO(responseVO);

            if (response.getSuccess()) {
                log.info("用户更新其他原因成功，userId:{} questionId:{}",
                        requestDTO.getUserId(), requestDTO.getQuestionId());
                return Response.<MistakeReasonResponseDTO>builder()
                        .code(GlobalServiceStatusCode.MISTAKE_REASON_UPDATE_SUCCESS.getCode())
                        .info(GlobalServiceStatusCode.MISTAKE_REASON_UPDATE_SUCCESS.getMessage())
                        .data(response)
                        .build();
            } else {
                log.warn("用户更新其他原因失败，userId:{} questionId:{} message:{}",
                        requestDTO.getUserId(), requestDTO.getQuestionId(), response.getMessage());
                return Response.<MistakeReasonResponseDTO>builder()
                        .code(GlobalServiceStatusCode.MISTAKE_REASON_UPDATE_FAILED.getCode())
                        .info(response.getMessage())
                        .data(response)
                        .build();
            }
        } catch (Exception e) {
            log.error("用户更新其他原因时发生异常，userId:{} questionId:{}",
                    requestDTO.getUserId(), requestDTO.getQuestionId(), e);
            return Response.<MistakeReasonResponseDTO>builder()
                    .code(GlobalServiceStatusCode.MISTAKE_REASON_SYSTEM_ERROR.getCode())
                    .info("系统异常: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 简化的错因状态切换接口
     * 只需传入错因参数名，自动查询数据库并切换状态（0变1，1变0）
     *
     * @param requestDTO 简化错因切换请求DTO
     * @return 错因管理响应
     */
    @GlobalInterception
    @PostMapping("toggle")
    public Response<MistakeReasonResponseDTO> toggleMistakeReasonSimple(
            @Valid @RequestBody MistakeReasonToggleRequestDTO requestDTO) {
        String userId = UserContext.getUserId();
        try {
            log.info("用户简化切换错因状态开始，userId:{} questionId:{} reasonName:{}",
                    userId, requestDTO.getQuestionId(), requestDTO.getReasonName());

            // 调用领域服务
            MistakeReasonVO responseVO = mistakeReasonService.toggleMistakeReasonByName(
                    userId,
                    requestDTO.getQuestionId(), 
                    requestDTO.getReasonName());

            // 转换为响应DTO
            MistakeReasonResponseDTO response = convertToMistakeReasonResponseDTO(responseVO);

            if (response.getSuccess()) {
                log.info("用户简化切换错因状态成功，userId:{} questionId:{} reasonName:{}",
                        userId, requestDTO.getQuestionId(), requestDTO.getReasonName());
                return Response.<MistakeReasonResponseDTO>builder()
                        .code(GlobalServiceStatusCode.MISTAKE_REASON_TOGGLE_SUCCESS.getCode())
                        .info(GlobalServiceStatusCode.MISTAKE_REASON_TOGGLE_SUCCESS.getMessage())
                        .data(response)
                        .build();
            } else {
                log.warn("用户简化切换错因状态失败，userId:{} questionId:{} reasonName:{} message:{}",
                        userId, requestDTO.getQuestionId(), requestDTO.getReasonName(), response.getMessage());
                return Response.<MistakeReasonResponseDTO>builder()
                        .code(GlobalServiceStatusCode.MISTAKE_REASON_TOGGLE_FAILED.getCode())
                        .info(response.getMessage())
                        .data(response)
                        .build();
            }
        } catch (Exception e) {
            log.error("用户简化切换错因状态时发生异常，userId:{} questionId:{} reasonName:{}",
                    userId, requestDTO.getQuestionId(), requestDTO.getReasonName(), e);
            return Response.<MistakeReasonResponseDTO>builder()
                    .code(GlobalServiceStatusCode.MISTAKE_REASON_SYSTEM_ERROR.getCode())
                    .info("系统异常: " + e.getMessage())
                    .build();
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
            MistakeReasonResponseDTO response = convertToMistakeReasonResponseDTO(responseVO);

            if (response.getSuccess()) {
                log.info("获取错因信息成功，userId:{} questionId:{}", userId, questionId);
                return Response.<MistakeReasonResponseDTO>builder()
                        .code(GlobalServiceStatusCode.MISTAKE_REASON_GET_SUCCESS.getCode())
                        .info(GlobalServiceStatusCode.MISTAKE_REASON_GET_SUCCESS.getMessage())
                        .data(response)
                        .build();
            } else {
                log.warn("获取错因信息失败，userId:{} questionId:{} message:{}",
                        userId, questionId, response.getMessage());
                return Response.<MistakeReasonResponseDTO>builder()
                        .code(GlobalServiceStatusCode.MISTAKE_REASON_NOT_FOUND.getCode())
                        .info(response.getMessage())
                        .data(response)
                        .build();
            }
        } catch (Exception e) {
            log.error("获取错因信息时发生异常，userId:{} questionId:{}", userId, questionId, e);
            return Response.<MistakeReasonResponseDTO>builder()
                    .code(GlobalServiceStatusCode.MISTAKE_REASON_SYSTEM_ERROR.getCode())
                    .info("系统异常: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 提交错题笔记
     *
     * @param requestDTO 错题笔记请求DTO
     * @return 错题笔记响应
     */
    @PostMapping("study-note/submit")
    public Response<StudyNoteResponseDTO> submitStudyNote(
            @Valid @RequestBody StudyNoteRequestDTO requestDTO) {
        try {
            log.info("用户提交错题笔记开始，userId:{} questionId:{}",
                    requestDTO.getUserId(), requestDTO.getQuestionId());

            // 转换DTO为领域值对象
            StudyNoteVO studyNoteVO = convertToStudyNoteVO(requestDTO);

            // 调用领域服务
            StudyNoteVO responseVO = studyNoteService.updateStudyNote(studyNoteVO);

            // 转换为响应DTO
            StudyNoteResponseDTO response = convertToStudyNoteResponseDTO(responseVO);

            if (response.getSuccess()) {
                log.info("用户提交错题笔记成功，userId:{} questionId:{}",
                        requestDTO.getUserId(), requestDTO.getQuestionId());
                return Response.<StudyNoteResponseDTO>builder()
                        .code(GlobalServiceStatusCode.STUDY_NOTE_SUBMIT_SUCCESS.getCode())
                        .info(GlobalServiceStatusCode.STUDY_NOTE_SUBMIT_SUCCESS.getMessage())
                        .data(response)
                        .build();
            } else {
                log.warn("用户提交错题笔记失败，userId:{} questionId:{} message:{}",
                        requestDTO.getUserId(), requestDTO.getQuestionId(), response.getMessage());
                return Response.<StudyNoteResponseDTO>builder()
                        .code(GlobalServiceStatusCode.STUDY_NOTE_UPDATE_FAILED.getCode())
                        .info(response.getMessage())
                        .data(response)
                        .build();
            }
        } catch (Exception e) {
            log.error("用户提交错题笔记时发生异常，userId:{} questionId:{}",
                    requestDTO.getUserId(), requestDTO.getQuestionId(), e);
            return Response.<StudyNoteResponseDTO>builder()
                    .code(GlobalServiceStatusCode.MISTAKE_REASON_SYSTEM_ERROR.getCode())
                    .info("系统异常: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 获取错题笔记
     *
     * @param userId 用户ID
     * @param questionId 题目ID
     * @return 错题笔记响应
     */
    @GetMapping("study-note/get")
    public Response<StudyNoteResponseDTO> getStudyNote(
            @RequestParam String userId,
            @RequestParam String questionId) {
        try {
            log.info("获取错题笔记开始，userId:{} questionId:{}", userId, questionId);

            // 调用领域服务
            StudyNoteVO responseVO = studyNoteService.getStudyNote(userId, questionId);

            // 转换为响应DTO
            StudyNoteResponseDTO response = convertToStudyNoteResponseDTO(responseVO);

            if (response.getSuccess()) {
                log.info("获取错题笔记成功，userId:{} questionId:{}", userId, questionId);
                return Response.<StudyNoteResponseDTO>builder()
                        .code(GlobalServiceStatusCode.STUDY_NOTE_GET_SUCCESS.getCode())
                        .info(GlobalServiceStatusCode.STUDY_NOTE_GET_SUCCESS.getMessage())
                        .data(response)
                        .build();
            } else {
                log.warn("获取错题笔记失败，userId:{} questionId:{} message:{}",
                        userId, questionId, response.getMessage());
                return Response.<StudyNoteResponseDTO>builder()
                        .code(GlobalServiceStatusCode.MISTAKE_REASON_NOT_FOUND.getCode())
                        .info(response.getMessage())
                        .data(response)
                        .build();
            }
        } catch (Exception e) {
            log.error("获取错题笔记时发生异常，userId:{} questionId:{}", userId, questionId, e);
            return Response.<StudyNoteResponseDTO>builder()
                    .code(GlobalServiceStatusCode.MISTAKE_REASON_SYSTEM_ERROR.getCode())
                    .info("系统异常: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 将MistakeReasonRequestDTO转换为MistakeReasonVO
     */
    private MistakeReasonVO convertToMistakeReasonVO(MistakeReasonRequestDTO requestDTO) {
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
     * 将MistakeReasonVO转换为MistakeReasonResponseDTO
     */
    private MistakeReasonResponseDTO convertToMistakeReasonResponseDTO(MistakeReasonVO reasonVO) {
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

    /**
     * 将StudyNoteRequestDTO转换为StudyNoteVO
     */
    private StudyNoteVO convertToStudyNoteVO(StudyNoteRequestDTO requestDTO) {
        return StudyNoteVO.builder()
                .userId(requestDTO.getUserId())
                .questionId(requestDTO.getQuestionId())
                .studyNote(requestDTO.getStudyNote())
                .build();
    }

    /**
     * 将StudyNoteVO转换为StudyNoteResponseDTO
     */
    private StudyNoteResponseDTO convertToStudyNoteResponseDTO(StudyNoteVO studyNoteVO) {
        if (studyNoteVO == null) {
            return StudyNoteResponseDTO.builder()
                    .success(false)
                    .message("系统错误：响应对象为空")
                    .build();
        }

        return StudyNoteResponseDTO.builder()
                .userId(studyNoteVO.getUserId())
                .questionId(studyNoteVO.getQuestionId())
                .studyNote(studyNoteVO.getStudyNote())
                .success(studyNoteVO.getSuccess())
                .message(studyNoteVO.getMessage())
                .build();
    }
}
