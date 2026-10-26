package com.ruoyi.crm.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.crm.domain.dto.wecom.WecomMomentCancelTaskRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomMomentCommentsRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomMomentCommentsResponse;
import com.ruoyi.crm.domain.dto.wecom.WecomMomentCreateTaskRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomMomentCreateTaskResponse;
import com.ruoyi.crm.domain.dto.wecom.WecomMomentCustomerListRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomMomentCustomerListResponse;
import com.ruoyi.crm.domain.dto.wecom.WecomMomentListRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomMomentListResponse;
import com.ruoyi.crm.domain.dto.wecom.WecomMomentTaskRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomMomentTaskResponse;

/**
 * 企业微信客户朋友圈代理服务。
 */
public interface ICrmWecomMomentService
{
    /** #1 获取朋友圈发表列表 */
    WecomMomentListResponse getMomentList(String accessToken, WecomMomentListRequest request);

    /** #2 获取企业发表成员执行情况 */
    WecomMomentTaskResponse getMomentTask(String accessToken, WecomMomentTaskRequest request);

    /** #3 获取发表时选择的可见客户范围 */
    WecomMomentCustomerListResponse getMomentCustomerList(String accessToken, WecomMomentCustomerListRequest request);

    /** #4 获取发表后可见的客户列表 */
    WecomMomentCustomerListResponse getMomentSendResult(String accessToken, WecomMomentCustomerListRequest request);

    /** #5 获取互动数据（评论+点赞） */
    WecomMomentCommentsResponse getMomentComments(String accessToken, WecomMomentCommentsRequest request);

    /** #6 创建发表任务 */
    WecomMomentCreateTaskResponse addMomentTask(String accessToken, WecomMomentCreateTaskRequest request);

    /** #7 查询任务创建结果 */
    WecomMomentCreateTaskResponse getMomentTaskResult(String accessToken, String jobid);

    /** #8 停止未发送的发表任务 */
    WecomMomentCreateTaskResponse cancelMomentTask(String accessToken, WecomMomentCancelTaskRequest request);

    /** 上传朋友圈附件（图片/视频/文件封面），返回 media_id */
    Map<String, Object> uploadMomentMedia(String accessToken, String mediaType, MultipartFile file);
}
