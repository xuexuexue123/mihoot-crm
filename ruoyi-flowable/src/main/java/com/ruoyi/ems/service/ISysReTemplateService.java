package com.ruoyi.ems.service;

import java.util.List;
import com.ruoyi.ems.domain.SysReTemplate;

/**
 * 项目模版Service接口
 * 
 * @author luohd
 * @date 2023-05-21
 */
public interface ISysReTemplateService 
{
    /**
     * 查询项目模版
     * 
     * @param templateId 项目模版主键
     * @return 项目模版
     */
    public SysReTemplate selectSysReTemplateByTemplateId(String templateId);

    /**
     * 查询项目模版列表
     * 
     * @param sysReTemplate 项目模版
     * @return 项目模版集合
     */
    public List<SysReTemplate> selectSysReTemplateList(SysReTemplate sysReTemplate);

    /**
     * 新增项目模版
     * 
     * @param sysReTemplate 项目模版
     * @return 结果
     */
    public int insertSysReTemplate(SysReTemplate sysReTemplate);

    /**
     * 修改项目模版
     * 
     * @param sysReTemplate 项目模版
     * @return 结果
     */
    public int updateSysReTemplate(SysReTemplate sysReTemplate);

    /**
     * 批量删除项目模版
     * 
     * @param templateIds 需要删除的项目模版主键集合
     * @return 结果
     */
    public int deleteSysReTemplateByTemplateIds(String[] templateIds);

    /**
     * 删除项目模版信息
     * 
     * @param templateId 项目模版主键
     * @return 结果
     */
    public int deleteSysReTemplateByTemplateId(String templateId);
}
