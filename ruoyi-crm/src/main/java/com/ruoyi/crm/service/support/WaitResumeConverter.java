package com.ruoyi.crm.service.support;

import org.springframework.beans.BeanUtils;
import com.ruoyi.crm.domain.WWaitResume;
import com.ruoyi.crm.domain.vo.WaitResumeVO;

/**
 * Wait resume 实体与 VO 转换
 */
public final class WaitResumeConverter
{
    private WaitResumeConverter()
    {
    }

    public static WaitResumeVO toVo(WWaitResume entity)
    {
        if (entity == null)
        {
            return null;
        }
        WaitResumeVO vo = new WaitResumeVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
