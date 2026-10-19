package com.ruoyi.ems.service;

import java.util.HashMap;
import java.util.List;
import com.ruoyi.ems.domain.SysReReportdefinition;

/**
 * 报表定义Service接口
 * 
 * @author luohd
 * @date 2023-03-06
 */
public interface ISysReReportdefinitionService 
{
    /**
     * 查询报表定义
     * 
     * @param id 报表定义主键
     * @return 报表定义
     */
    public SysReReportdefinition selectSysReReportdefinitionById(String id);

    /**
     * 查询报表定义列表
     * 
     * @param sysReReportdefinition 报表定义
     * @return 报表定义集合
     */
    public List<SysReReportdefinition> selectSysReReportdefinitionList(SysReReportdefinition sysReReportdefinition);


    /**
     *
     * @param dataobjId
     * @return
     */
    public List<SysReReportdefinition> selectSysReReportdefinitionListByDataobj(String dataobjId);

    /**
     * 新增报表定义
     * 
     * @param sysReReportdefinition 报表定义
     * @return 结果
     */
    public int insertSysReReportdefinition(SysReReportdefinition sysReReportdefinition);

    /**
     * 修改报表定义
     * 
     * @param sysReReportdefinition 报表定义
     * @return 结果
     */
    public int updateSysReReportdefinition(SysReReportdefinition sysReReportdefinition);

    /**
     * 批量删除报表定义
     * 
     * @param ids 需要删除的报表定义主键集合
     * @return 结果
     */
    public int deleteSysReReportdefinitionByIds(String[] ids);

    /**
     * 删除报表定义信息
     * 
     * @param id 报表定义主键
     * @return 结果
     */
    public int deleteSysReReportdefinitionById(String id);


    /**
     *
     * @param tableName
     * @return
     */
    public List<HashMap<String, Object>> selectReportDataByFilter(String columns, String tableName, String filters);

}
