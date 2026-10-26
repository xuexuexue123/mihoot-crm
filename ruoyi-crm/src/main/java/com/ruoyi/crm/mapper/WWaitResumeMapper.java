package com.ruoyi.crm.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.WWaitResume;

/**
 * WWaitResumeMapper接口
 *
 * @author luohd
 * @date 2026-05-19
 */
public interface WWaitResumeMapper
{
    public WWaitResume selectWWaitResumeById(String id);

    public WWaitResume selectWWaitResumeByResumeKey(String resumeKey);

    public List<WWaitResume> selectWWaitResumeList(WWaitResume wWaitResume);

    public int insertWWaitResume(WWaitResume wWaitResume);

    public int updateWWaitResume(WWaitResume wWaitResume);

    public int updateResumeByKey(@Param("resumeKey") String resumeKey, @Param("payload") String payload,
            @Param("resumedAt") Date resumedAt);

    public int deleteWWaitResumeById(String id);

    public int deleteWWaitResumeByIds(String[] ids);
}
