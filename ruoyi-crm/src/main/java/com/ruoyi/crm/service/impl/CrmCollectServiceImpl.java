package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.aliyun.SendEmail;
import com.ruoyi.crm.domain.CrmCollectEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmCollectMapper;
import com.ruoyi.crm.domain.CrmCollect;
import com.ruoyi.crm.service.ICrmCollectService;

/**
 * 数据收集Service业务层处理
 * 
 * @author luohd
 * @date 2024-07-04
 */
@Service
public class CrmCollectServiceImpl implements ICrmCollectService 
{
    @Autowired
    private CrmCollectMapper crmCollectMapper;

    /**
     * 查询数据收集
     * 
     * @param collectId 数据收集主键
     * @return 数据收集
     */
    @Override
    public CrmCollect selectCrmCollectByCollectId(String collectId)
    {
        return crmCollectMapper.selectCrmCollectByCollectId(collectId);
    }

    /**
     * 查询数据收集列表
     * 
     * @param crmCollect 数据收集
     * @return 数据收集
     */
    @Override
    public List<CrmCollect> selectCrmCollectList(CrmCollect crmCollect)
    {
        return crmCollectMapper.selectCrmCollectList(crmCollect);
    }

    /**
     * 新增数据收集
     * 
     * @param crmCollect 数据收集
     * @return 结果
     */
    @Override
    public int insertCrmCollect(CrmCollectEmail crmCollect) throws Exception {
        //插入数据库
        crmCollect.setCreateTime(DateUtils.getNowDate());
        int result = crmCollectMapper.insertCrmCollect(crmCollect);

        //发送邮件
        SendEmail.SingleSendMailSample(
                crmCollect.getToAddress(),
                crmCollect.getSubject(),
                crmCollect.getHtmlBody());

        return result;
    }

    /**
     * 修改数据收集
     * 
     * @param crmCollect 数据收集
     * @return 结果
     */
    @Override
    public int updateCrmCollect(CrmCollect crmCollect)
    {
        crmCollect.setUpdateTime(DateUtils.getNowDate());
        return crmCollectMapper.updateCrmCollect(crmCollect);
    }

    /**
     *  by validation code
     * @param crmCollect
     * @return
     */
    @Override
    public int updateCrmCollectByValidationCode(CrmCollect crmCollect)
    {
        crmCollect.setUpdateTime(DateUtils.getNowDate());
        return crmCollectMapper.updateCrmCollectByValidationCode(crmCollect);
    }
    /**
     * 批量删除数据收集
     * 
     * @param collectIds 需要删除的数据收集主键
     * @return 结果
     */
    @Override
    public int deleteCrmCollectByCollectIds(String[] collectIds)
    {
        return crmCollectMapper.deleteCrmCollectByCollectIds(collectIds);
    }

    /**
     * 删除数据收集信息
     * 
     * @param collectId 数据收集主键
     * @return 结果
     */
    @Override
    public int deleteCrmCollectByCollectId(String collectId)
    {
        return crmCollectMapper.deleteCrmCollectByCollectId(collectId);
    }
}
