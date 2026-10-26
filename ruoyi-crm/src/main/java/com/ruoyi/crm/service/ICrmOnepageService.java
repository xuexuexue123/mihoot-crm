package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmOnepage;

/**
 * 一页Service接口
 * 
 * @author luohd
 * @date 2024-08-15
 */
public interface ICrmOnepageService 
{
    /**
     * 查询一页
     * 
     * @param pageId 一页主键
     * @return 一页
     */
    public CrmOnepage selectCrmOnepageByPageId(String pageId);

    /**
     * 查询一页
     *
     * @param relateId 一页主键
     * @return 一页
     */
    public CrmOnepage selectCrmOnepageByRelateId(String relateId);

    /**
     * 查询一页列表
     * 
     * @param crmOnepage 一页
     * @return 一页集合
     */
    public List<CrmOnepage> selectCrmOnepageList(CrmOnepage crmOnepage);

    /**
     * 新增一页
     * 
     * @param crmOnepage 一页
     * @return 结果
     */
    public int insertCrmOnepage(CrmOnepage crmOnepage);

    /**
     * 修改一页
     * 
     * @param crmOnepage 一页
     * @return 结果
     */
    public int updateCrmOnepage(CrmOnepage crmOnepage);

    /**
     * 批量删除一页
     * 
     * @param pageIds 需要删除的一页主键集合
     * @return 结果
     */
    public int deleteCrmOnepageByPageIds(String[] pageIds);

    /**
     * 删除一页信息
     * 
     * @param pageId 一页主键
     * @return 结果
     */
    public int deleteCrmOnepageByPageId(String pageId);
}
