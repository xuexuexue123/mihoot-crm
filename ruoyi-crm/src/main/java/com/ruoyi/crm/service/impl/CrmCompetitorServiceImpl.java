package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmCompetitorMapper;
import com.ruoyi.crm.domain.CrmCompetitor;
import com.ruoyi.crm.service.ICrmCompetitorService;

/**
 * 竞争对手Service业务层处理
 * 
 * @author luohd
 * @date 2024-02-07
 */
@Service
public class CrmCompetitorServiceImpl implements ICrmCompetitorService 
{
    @Autowired
    private CrmCompetitorMapper crmCompetitorMapper;

    /**
     * 查询竞争对手
     * 
     * @param competitorId 竞争对手主键
     * @return 竞争对手
     */
    @Override
    public CrmCompetitor selectCrmCompetitorByCompetitorId(String competitorId)
    {
        return crmCompetitorMapper.selectCrmCompetitorByCompetitorId(competitorId);
    }

    /**
     * 查询竞争对手列表
     * 
     * @param crmCompetitor 竞争对手
     * @return 竞争对手
     */
    @Override
    public List<CrmCompetitor> selectCrmCompetitorList(CrmCompetitor crmCompetitor)
    {
        return crmCompetitorMapper.selectCrmCompetitorList(crmCompetitor);
    }

    /**
     * 新增竞争对手
     * 
     * @param crmCompetitor 竞争对手
     * @return 结果
     */
    @Override
    public int insertCrmCompetitor(CrmCompetitor crmCompetitor)
    {
        crmCompetitor.setCreateTime(DateUtils.getNowDate());
        return crmCompetitorMapper.insertCrmCompetitor(crmCompetitor);
    }

    /**
     * 修改竞争对手
     * 
     * @param crmCompetitor 竞争对手
     * @return 结果
     */
    @Override
    public int updateCrmCompetitor(CrmCompetitor crmCompetitor)
    {
        crmCompetitor.setUpdateTime(DateUtils.getNowDate());
        return crmCompetitorMapper.updateCrmCompetitor(crmCompetitor);
    }

    /**
     * 批量删除竞争对手
     * 
     * @param competitorIds 需要删除的竞争对手主键
     * @return 结果
     */
    @Override
    public int deleteCrmCompetitorByCompetitorIds(String[] competitorIds)
    {
        return crmCompetitorMapper.deleteCrmCompetitorByCompetitorIds(competitorIds);
    }

    /**
     * 删除竞争对手信息
     * 
     * @param competitorId 竞争对手主键
     * @return 结果
     */
    @Override
    public int deleteCrmCompetitorByCompetitorId(String competitorId)
    {
        return crmCompetitorMapper.deleteCrmCompetitorByCompetitorId(competitorId);
    }
}
