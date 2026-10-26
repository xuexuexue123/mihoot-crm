package com.ruoyi.ems.service.impl;

import java.util.HashMap;
import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ems.mapper.SysReReportdefinitionMapper;
import com.ruoyi.ems.domain.SysReReportdefinition;
import com.ruoyi.ems.service.ISysReReportdefinitionService;

/**
 * 报表定义Service业务层处理
 * 
 * @author luohd
 * @date 2023-03-06
 */
@Service
public class SysReReportdefinitionServiceImpl implements ISysReReportdefinitionService 
{
    @Autowired
    private SysReReportdefinitionMapper sysReReportdefinitionMapper;

    /**
     * 查询报表定义
     * 
     * @param id 报表定义主键
     * @return 报表定义
     */
    @Override
    public SysReReportdefinition selectSysReReportdefinitionById(String id)
    {
        return sysReReportdefinitionMapper.selectSysReReportdefinitionById(id);
    }

    /**
     * 查询报表定义列表
     * 
     * @param sysReReportdefinition 报表定义
     * @return 报表定义
     */
    @Override
    public List<SysReReportdefinition> selectSysReReportdefinitionList(SysReReportdefinition sysReReportdefinition)
    {
        return sysReReportdefinitionMapper.selectSysReReportdefinitionList(sysReReportdefinition);
    }

    @Override
    public List<SysReReportdefinition> selectSysReReportdefinitionListByDataobj(String dataobjId) {
        return sysReReportdefinitionMapper.selectSysReReportdefinitionListByDataobj(dataobjId);
    }

    /**
     * 新增报表定义
     * 
     * @param sysReReportdefinition 报表定义
     * @return 结果
     */
    @Override
    public int insertSysReReportdefinition(SysReReportdefinition sysReReportdefinition)
    {
        sysReReportdefinition.setCreateTime(DateUtils.getNowDate());
        return sysReReportdefinitionMapper.insertSysReReportdefinition(sysReReportdefinition);
    }

    /**
     * 修改报表定义
     * 
     * @param sysReReportdefinition 报表定义
     * @return 结果
     */
    @Override
    public int updateSysReReportdefinition(SysReReportdefinition sysReReportdefinition)
    {
        sysReReportdefinition.setUpdateTime(DateUtils.getNowDate());
        return sysReReportdefinitionMapper.updateSysReReportdefinition(sysReReportdefinition);
    }

    /**
     * 批量删除报表定义
     * 
     * @param ids 需要删除的报表定义主键
     * @return 结果
     */
    @Override
    public int deleteSysReReportdefinitionByIds(String[] ids)
    {
        return sysReReportdefinitionMapper.deleteSysReReportdefinitionByIds(ids);
    }

    /**
     * 删除报表定义信息
     * 
     * @param id 报表定义主键
     * @return 结果
     */
    @Override
    public int deleteSysReReportdefinitionById(String id)
    {
        return sysReReportdefinitionMapper.deleteSysReReportdefinitionById(id);
    }

    /**
     *
     * @param tableName
     * @return
     */
    public List<HashMap<String, Object>> selectReportDataByFilter(String columns, String tableName, String filters) {
        return sysReReportdefinitionMapper.selectReportDataByFilter(columns, tableName, filters);
    }
}
