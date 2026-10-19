package com.ruoyi.ems.mapper;

import java.util.HashMap;
import java.util.List;
import com.ruoyi.ems.domain.SysReReportdefinition;
import org.apache.ibatis.annotations.Param;

/**
 * 报表定义Mapper接口
 * 
 * @author luohd
 * @date 2023-03-06
 */
public interface SysReReportdefinitionMapper 
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
     * 删除报表定义
     * 
     * @param id 报表定义主键
     * @return 结果
     */
    public int deleteSysReReportdefinitionById(String id);

    /**
     * 批量删除报表定义
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysReReportdefinitionByIds(String[] ids);


    /**
     *  选择指定的表中所有的数据
     * @param tableName
     * @return
     */
    public List<HashMap<String,Object>> selectReportDataByFilter(@Param("columns") String columns,
                                                                 @Param("tableName") String tableName,
                                                                 @Param("filters") String filters);
}
