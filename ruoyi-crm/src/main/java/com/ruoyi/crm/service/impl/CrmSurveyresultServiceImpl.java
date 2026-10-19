package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmSurveyresultMapper;
import com.ruoyi.crm.domain.CrmSurveyresult;
import com.ruoyi.crm.service.ICrmSurveyresultService;

/**
 * 调查结论Service业务层处理
 * 
 * @author luohd
 * @date 2024-05-18
 */
@Service
public class CrmSurveyresultServiceImpl implements ICrmSurveyresultService 
{
    @Autowired
    private CrmSurveyresultMapper crmSurveyresultMapper;

    /**
     * 查询调查结论
     * 
     * @param resultId 调查结论主键
     * @return 调查结论
     */
    @Override
    public CrmSurveyresult selectCrmSurveyresultByResultId(String resultId)
    {
        return crmSurveyresultMapper.selectCrmSurveyresultByResultId(resultId);
    }

    /**
     * 查询调查结论列表
     * 
     * @param crmSurveyresult 调查结论
     * @return 调查结论
     */
    @Override
    public List<CrmSurveyresult> selectCrmSurveyresultList(CrmSurveyresult crmSurveyresult)
    {
        return crmSurveyresultMapper.selectCrmSurveyresultList(crmSurveyresult);
    }

    /**
     * 新增调查结论
     * 
     * @param crmSurveyresult 调查结论
     * @return 结果
     */
    @Override
    public int insertCrmSurveyresult(CrmSurveyresult crmSurveyresult)
    {
        crmSurveyresult.setCreateTime(DateUtils.getNowDate());
        return crmSurveyresultMapper.insertCrmSurveyresult(crmSurveyresult);
    }

    /**
     * 修改调查结论
     * 
     * @param crmSurveyresult 调查结论
     * @return 结果
     */
    @Override
    public int updateCrmSurveyresult(CrmSurveyresult crmSurveyresult)
    {
        crmSurveyresult.setUpdateTime(DateUtils.getNowDate());
        return crmSurveyresultMapper.updateCrmSurveyresult(crmSurveyresult);
    }

    /**
     * 批量删除调查结论
     * 
     * @param resultIds 需要删除的调查结论主键
     * @return 结果
     */
    @Override
    public int deleteCrmSurveyresultByResultIds(String[] resultIds)
    {
        return crmSurveyresultMapper.deleteCrmSurveyresultByResultIds(resultIds);
    }

    /**
     * 删除调查结论信息
     * 
     * @param resultId 调查结论主键
     * @return 结果
     */
    @Override
    public int deleteCrmSurveyresultByResultId(String resultId)
    {
        return crmSurveyresultMapper.deleteCrmSurveyresultByResultId(resultId);
    }
}
