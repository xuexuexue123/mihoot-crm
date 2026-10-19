package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmOnepageMapper;
import com.ruoyi.crm.domain.CrmOnepage;
import com.ruoyi.crm.service.ICrmOnepageService;

/**
 * 一页Service业务层处理
 * 
 * @author luohd
 * @date 2024-08-15
 */
@Service
public class CrmOnepageServiceImpl implements ICrmOnepageService 
{
    @Autowired
    private CrmOnepageMapper crmOnepageMapper;

    /**
     * 查询一页
     * 
     * @param pageId 一页主键
     * @return 一页
     */
    @Override
    public CrmOnepage selectCrmOnepageByPageId(String pageId)
    {
        return crmOnepageMapper.selectCrmOnepageByPageId(pageId);
    }

    /**
     * 查询一页
     *
     * @param relateId 一页主键
     * @return 一页
     */
    public CrmOnepage selectCrmOnepageByRelateId(String relateId) {

        return crmOnepageMapper.selectCrmOnepageByRelateId(relateId);
    }
    /**
     * 查询一页列表
     * 
     * @param crmOnepage 一页
     * @return 一页
     */
    @Override
    public List<CrmOnepage> selectCrmOnepageList(CrmOnepage crmOnepage)
    {
        return crmOnepageMapper.selectCrmOnepageList(crmOnepage);
    }

    /**
     * 新增一页
     * 
     * @param crmOnepage 一页
     * @return 结果
     */
    @Override
    public int insertCrmOnepage(CrmOnepage crmOnepage)
    {
        crmOnepage.setCreateTime(DateUtils.getNowDate());
        return crmOnepageMapper.insertCrmOnepage(crmOnepage);
    }

    /**
     * 修改一页
     * 
     * @param crmOnepage 一页
     * @return 结果
     */
    @Override
    public int updateCrmOnepage(CrmOnepage crmOnepage)
    {
        crmOnepage.setUpdateTime(DateUtils.getNowDate());
        return crmOnepageMapper.updateCrmOnepage(crmOnepage);
    }

    /**
     * 批量删除一页
     * 
     * @param pageIds 需要删除的一页主键
     * @return 结果
     */
    @Override
    public int deleteCrmOnepageByPageIds(String[] pageIds)
    {
        return crmOnepageMapper.deleteCrmOnepageByPageIds(pageIds);
    }

    /**
     * 删除一页信息
     * 
     * @param pageId 一页主键
     * @return 结果
     */
    @Override
    public int deleteCrmOnepageByPageId(String pageId)
    {
        return crmOnepageMapper.deleteCrmOnepageByPageId(pageId);
    }
}
