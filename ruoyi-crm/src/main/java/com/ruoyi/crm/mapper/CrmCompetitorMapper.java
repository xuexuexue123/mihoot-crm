package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.CrmCompetitor;

/**
 * 竞争对手Mapper接口
 * 
 * @author luohd
 * @date 2024-02-07
 */
public interface CrmCompetitorMapper 
{
    /**
     * 查询竞争对手
     * 
     * @param competitorId 竞争对手主键
     * @return 竞争对手
     */
    public CrmCompetitor selectCrmCompetitorByCompetitorId(String competitorId);

    /**
     * 查询竞争对手列表
     * 
     * @param crmCompetitor 竞争对手
     * @return 竞争对手集合
     */
    public List<CrmCompetitor> selectCrmCompetitorList(CrmCompetitor crmCompetitor);

    /**
     * 新增竞争对手
     * 
     * @param crmCompetitor 竞争对手
     * @return 结果
     */
    public int insertCrmCompetitor(CrmCompetitor crmCompetitor);

    /**
     * 修改竞争对手
     * 
     * @param crmCompetitor 竞争对手
     * @return 结果
     */
    public int updateCrmCompetitor(CrmCompetitor crmCompetitor);

    /**
     * 删除竞争对手
     * 
     * @param competitorId 竞争对手主键
     * @return 结果
     */
    public int deleteCrmCompetitorByCompetitorId(String competitorId);

    /**
     * 批量删除竞争对手
     * 
     * @param competitorIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmCompetitorByCompetitorIds(String[] competitorIds);
}
