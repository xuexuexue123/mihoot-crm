package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.CrmSurveyresult;

/**
 * 调查结论Mapper接口
 * 
 * @author luohd
 * @date 2024-05-18
 */
public interface CrmSurveyresultMapper 
{
    /**
     * 查询调查结论
     * 
     * @param resultId 调查结论主键
     * @return 调查结论
     */
    public CrmSurveyresult selectCrmSurveyresultByResultId(String resultId);

    /**
     * 查询调查结论列表
     * 
     * @param crmSurveyresult 调查结论
     * @return 调查结论集合
     */
    public List<CrmSurveyresult> selectCrmSurveyresultList(CrmSurveyresult crmSurveyresult);

    /**
     * 新增调查结论
     * 
     * @param crmSurveyresult 调查结论
     * @return 结果
     */
    public int insertCrmSurveyresult(CrmSurveyresult crmSurveyresult);

    /**
     * 修改调查结论
     * 
     * @param crmSurveyresult 调查结论
     * @return 结果
     */
    public int updateCrmSurveyresult(CrmSurveyresult crmSurveyresult);

    /**
     * 删除调查结论
     * 
     * @param resultId 调查结论主键
     * @return 结果
     */
    public int deleteCrmSurveyresultByResultId(String resultId);

    /**
     * 批量删除调查结论
     * 
     * @param resultIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmSurveyresultByResultIds(String[] resultIds);
}
