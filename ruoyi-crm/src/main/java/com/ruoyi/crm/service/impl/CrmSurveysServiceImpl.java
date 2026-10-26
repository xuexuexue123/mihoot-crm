package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmSurveysMapper;
import com.ruoyi.crm.domain.CrmSurveys;
import com.ruoyi.crm.service.ICrmSurveysService;

/**
 * 调查Service业务层处理
 * 
 * @author luohd
 * @date 2025-03-29
 */
@Service
public class CrmSurveysServiceImpl implements ICrmSurveysService 
{
    @Autowired
    private CrmSurveysMapper crmSurveysMapper;

    /**
     * 查询调查
     * 
     * @param id 调查主键
     * @return 调查
     */
    @Override
    public CrmSurveys selectCrmSurveysById(String id)
    {
        return crmSurveysMapper.selectCrmSurveysById(id);
    }

    /**
     * 查询调查列表
     * 
     * @param crmSurveys 调查
     * @return 调查
     */
    @Override
    public List<CrmSurveys> selectCrmSurveysList(CrmSurveys crmSurveys)
    {
        return crmSurveysMapper.selectCrmSurveysList(crmSurveys);
    }

    /**
     * 新增调查
     * 
     * @param crmSurveys 调查
     * @return 结果
     */
    @Override
    public int insertCrmSurveys(CrmSurveys crmSurveys)
    {
        crmSurveys.setCreateTime(DateUtils.getNowDate());
        return crmSurveysMapper.insertCrmSurveys(crmSurveys);
    }

    /**
     * 修改调查
     * 
     * @param crmSurveys 调查
     * @return 结果
     */
    @Override
    public int updateCrmSurveys(CrmSurveys crmSurveys)
    {
        crmSurveys.setUpdateTime(DateUtils.getNowDate());
        return crmSurveysMapper.updateCrmSurveys(crmSurveys);
    }

    /**
     * 批量删除调查
     * 
     * @param ids 需要删除的调查主键
     * @return 结果
     */
    @Override
    public int deleteCrmSurveysByIds(String[] ids)
    {
        return crmSurveysMapper.deleteCrmSurveysByIds(ids);
    }

    /**
     * 删除调查信息
     * 
     * @param id 调查主键
     * @return 结果
     */
    @Override
    public int deleteCrmSurveysById(String id)
    {
        return crmSurveysMapper.deleteCrmSurveysById(id);
    }
}
