package com.ruoyi.ems.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ruoyi.ems.domain.SysReDataobj;
import com.ruoyi.ems.domain.SysReFieldPOJO;

/**
 * 数据对象Service接口
 * 
 * @author luohd
 * @date 2023-03-02
 */
public interface ISysReDataobjService 
{
    /**
     * 查询数据对象
     * 
     * @param dataobjId 数据对象主键
     * @return 数据对象
     */
    public SysReDataobj selectSysReDataobjByDataobjId(String dataobjId);

    /**
     *
     * @param pyId
     * @return
     */
    public SysReDataobj selectSysReDataobjByActivityTarget(String pyId);

    /**
     * 查询数据对象
     *
     * @param dataobjName 数据对象名称
     * @return 数据对象
     */
    public SysReDataobj selectSysReDataobjByDataobjName(String dataobjName);
    /**
     * 查询数据对象列表
     * 
     * @param sysReDataobj 数据对象
     * @return 数据对象集合
     */
    public List<SysReDataobj> selectSysReDataobjList(SysReDataobj sysReDataobj);

    /**
     * 新增数据对象
     * 
     * @param sysReDataobj 数据对象
     * @return 结果
     */
    public int insertSysReDataobj(SysReDataobj sysReDataobj);

    /**
     * 修改数据对象
     * 
     * @param sysReDataobj 数据对象
     * @return 结果
     */
    public int updateSysReDataobj(SysReDataobj sysReDataobj);

    /**
     * 批量删除数据对象
     * 
     * @param dataobjIds 需要删除的数据对象主键集合
     * @return 结果
     */
    public int deleteSysReDataobjByDataobjIds(String[] dataobjIds);

    /**
     * 删除数据对象信息
     * 
     * @param dataobjId 数据对象主键
     * @return 结果
     */
    public int deleteSysReDataobjByDataobjId(String dataobjId);

    /**
     * @Param name : 动态生成表的表名
     * @Param mymap: 表的列名及类型
     */
    public void creatTemplateTable(String name, Map<String, String> mymap);

    /**
     *
     * @param tableName
     * @return
     */
    public List<HashMap<String, Object>> selectTemplateTableData(String columns, String tableName);

    /**
     *
     * @param tableName
     * @param id
     * @return
     */
    public int deleteTemplateTableById(String tableName, Long id);

    /**
     *
     * @param tableName
     * @param fields
     * @param values
     * @return
     */
    public int insertTemplateTableData(String tableName, String fields, String values);

    /**
     * 删除指定的表中所有的数据
     * @param tableName
     */
    public void truncateTable(String tableName);

    /**
     * 删除指定的表
     * @param tableName
     */
    public void deleteTable(String tableName);

    public void alterTableName(String tableName);

    public void createNewTableAndInsertData(String newTableName, String originalTableName);

    public int getRecordCount(String tableName);

    public String getCurDataBaseName();

    public String isTargetTableExistInDB(String dataBaseName, String tableName);

}
