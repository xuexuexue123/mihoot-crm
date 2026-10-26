package com.ruoyi.crm.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmAgentToolMapper;
import com.ruoyi.crm.domain.CrmAgentTool;
import com.ruoyi.crm.service.ICrmAgentToolService;

/**
 * Agent ToolService业务层处理
 * 
 * @author luohd
 * @date 2025-06-05
 */
@Service
public class CrmAgentToolServiceImpl implements ICrmAgentToolService 
{
    @Autowired
    private CrmAgentToolMapper crmAgentToolMapper;

    /**
     * 查询Agent Tool
     * 
     * @param id Agent Tool主键
     * @return Agent Tool
     */
    @Override
    public CrmAgentTool selectCrmAgentToolById(Long id)
    {
        return crmAgentToolMapper.selectCrmAgentToolById(id);
    }

    /**
     * 查询Agent Tool列表
     * 
     * @param crmAgentTool Agent Tool
     * @return Agent Tool
     */
    @Override
    public List<CrmAgentTool> selectCrmAgentToolList(CrmAgentTool crmAgentTool)
    {
        return crmAgentToolMapper.selectCrmAgentToolList(crmAgentTool);
    }

    /**
     * 新增Agent Tool
     * 
     * @param crmAgentTool Agent Tool
     * @return 结果
     */
    @Override
    public int insertCrmAgentTool(CrmAgentTool crmAgentTool)
    {
        return crmAgentToolMapper.insertCrmAgentTool(crmAgentTool);
    }

    /**
     * 修改Agent Tool
     * 
     * @param crmAgentTool Agent Tool
     * @return 结果
     */
    @Override
    public int updateCrmAgentTool(CrmAgentTool crmAgentTool)
    {
        return crmAgentToolMapper.updateCrmAgentTool(crmAgentTool);
    }

    /**
     * 批量删除Agent Tool
     * 
     * @param ids 需要删除的Agent Tool主键
     * @return 结果
     */
    @Override
    public int deleteCrmAgentToolByIds(Long[] ids)
    {
        return crmAgentToolMapper.deleteCrmAgentToolByIds(ids);
    }

    /**
     * 删除Agent Tool信息
     * 
     * @param id Agent Tool主键
     * @return 结果
     */
    @Override
    public int deleteCrmAgentToolById(Long id)
    {
        return crmAgentToolMapper.deleteCrmAgentToolById(id);
    }
}
