package com.ruoyi.crm.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.crm.domain.CrmWecomMaterial;
import com.ruoyi.crm.domain.dto.wecom.WecomMaterialQueryRequest;

public interface ICrmWecomMaterialService
{
    /**
     * 上传素材到服务器，创建 pending 记录
     * @param file 上传文件
     * @param fileTitle 素材标题
     * @param mediaType 媒体类型(image/video/file)
     * @param attachmentType 附件类型(0:普通临时素材 1:朋友圈附件 2:商品图册)
     */
    CrmWecomMaterial upload(MultipartFile file, String fileTitle, String mediaType, Integer attachmentType);

    /**
     * 根据 ID 查询素材
     */
    CrmWecomMaterial getById(Long id);

    /**
     * 分页查询素材列表
     */
    List<CrmWecomMaterial> list(WecomMaterialQueryRequest query);

    /**
     * 上传素材到企业微信获取 media_id
     */
    CrmWecomMaterial uploadToWecom(Long id);

    /**
     * 获取素材文件下载URL
     */
    String getDownloadUrl(Long id);

    /**
     * 删除素材记录及服务器文件
     */
    void delete(Long id);

    /**
     * 定时任务：扫描过期记录并更新状态
     */
    void updateExpiredStatus();
}
