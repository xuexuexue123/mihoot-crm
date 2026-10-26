package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmAgentTool;

/**
 * Agent ToolService接口
 * 
 * @author luohd
 * @date 2025-06-05
 */
public interface ICrmAgentToolService 
{
    /**
     * 查询Agent Tool
     * 
     * @param id Agent Tool主键
     * @return Agent Tool
     */
    public CrmAgentTool selectCrmAgentToolById(Long id);

    /**
     * 查询Agent Tool列表
     * 
     * @param crmAgentTool Agent Tool
     * @return Agent Tool集合
     */
    public List<CrmAgentTool> selectCrmAgentToolList(CrmAgentTool crmAgentTool);

    /**
     * 新增Agent Tool
     * 
     * @param crmAgentTool Agent Tool
     * @return 结果
     */
    public int insertCrmAgentTool(CrmAgentTool crmAgentTool);

    /**
     * 修改Agent Tool
     * 
     * @param crmAgentTool Agent Tool
     * @return 结果
     */
    public int updateCrmAgentTool(CrmAgentTool crmAgentTool);

    /**
     * 批量删除Agent Tool
     * 
     * @param ids 需要删除的Agent Tool主键集合
     * @return 结果
     */
    public int deleteCrmAgentToolByIds(Long[] ids);

    /**
     * 删除Agent Tool信息
     * 
     * @param id Agent Tool主键
     * @return 结果
     */
    public int deleteCrmAgentToolById(Long id);
}
