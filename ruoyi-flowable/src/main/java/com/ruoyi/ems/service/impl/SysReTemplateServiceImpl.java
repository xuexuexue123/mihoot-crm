package com.ruoyi.ems.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ems.mapper.SysReTemplateMapper;
import com.ruoyi.ems.domain.SysReTemplate;
import com.ruoyi.ems.service.ISysReTemplateService;

/**
 * 项目模版Service业务层处理
 * 
 * @author luohd
 * @date 2023-05-21
 */
@Service
public class SysReTemplateServiceImpl implements ISysReTemplateService 
{
    @Autowired
    private SysReTemplateMapper sysReTemplateMapper;

    /**
     * 查询项目模版
     * 
     * @param templateId 项目模版主键
     * @return 项目模版
     */
    @Override
    public SysReTemplate selectSysReTemplateByTemplateId(String templateId)
    {
        return sysReTemplateMapper.selectSysReTemplateByTemplateId(templateId);
    }

    /**
     * 查询项目模版列表
     * 
     * @param sysReTemplate 项目模版
     * @return 项目模版
     */
    @Override
    public List<SysReTemplate> selectSysReTemplateList(SysReTemplate sysReTemplate)
    {
        return sysReTemplateMapper.selectSysReTemplateList(sysReTemplate);
    }

    /**
     * 新增项目模版
     * 
     * @param sysReTemplate 项目模版
     * @return 结果
     */
    @Override
    public int insertSysReTemplate(SysReTemplate sysReTemplate)
    {
        sysReTemplate.setCreateTime(DateUtils.getNowDate());
        return sysReTemplateMapper.insertSysReTemplate(sysReTemplate);
    }

    /**
     * 修改项目模版
     * 
     * @param sysReTemplate 项目模版
     * @return 结果
     */
    @Override
    public int updateSysReTemplate(SysReTemplate sysReTemplate)
    {
        sysReTemplate.setUpdateTime(DateUtils.getNowDate());
        return sysReTemplateMapper.updateSysReTemplate(sysReTemplate);
    }

    /**
     * 批量删除项目模版
     * 
     * @param templateIds 需要删除的项目模版主键
     * @return 结果
     */
    @Override
    public int deleteSysReTemplateByTemplateIds(String[] templateIds)
    {
        return sysReTemplateMapper.deleteSysReTemplateByTemplateIds(templateIds);
    }

    /**
     * 删除项目模版信息
     * 
     * @param templateId 项目模版主键
     * @return 结果
     */
    @Override
    public int deleteSysReTemplateByTemplateId(String templateId)
    {
        return sysReTemplateMapper.deleteSysReTemplateByTemplateId(templateId);
    }
}
