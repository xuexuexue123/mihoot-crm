package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmSurveys;

/**
 * 调查Service接口
 * 
 * @author luohd
 * @date 2025-03-29
 */
public interface ICrmSurveysService 
{
    /**
     * 查询调查
     * 
     * @param id 调查主键
     * @return 调查
     */
    public CrmSurveys selectCrmSurveysById(String id);

    /**
     * 查询调查列表
     * 
     * @param crmSurveys 调查
     * @return 调查集合
     */
    public List<CrmSurveys> selectCrmSurveysList(CrmSurveys crmSurveys);

    /**
     * 新增调查
     * 
     * @param crmSurveys 调查
     * @return 结果
     */
    public int insertCrmSurveys(CrmSurveys crmSurveys);

    /**
     * 修改调查
     * 
     * @param crmSurveys 调查
     * @return 结果
     */
    public int updateCrmSurveys(CrmSurveys crmSurveys);

    /**
     * 批量删除调查
     * 
     * @param ids 需要删除的调查主键集合
     * @return 结果
     */
    public int deleteCrmSurveysByIds(String[] ids);

    /**
     * 删除调查信息
     * 
     * @param id 调查主键
     * @return 结果
     */
    public int deleteCrmSurveysById(String id);
}
