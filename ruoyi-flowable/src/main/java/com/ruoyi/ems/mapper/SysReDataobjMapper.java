package com.ruoyi.ems.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ruoyi.ems.domain.SysReDataobj;
import com.ruoyi.ems.domain.SysReDatafield;
import com.ruoyi.ems.domain.SysReFieldPOJO;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

/**
 * 数据对象Mapper接口
 * 
 * @author luohd
 * @date 2023-03-02
 */
public interface SysReDataobjMapper 
{
    /**
     * 查询数据对象
     * 
     * @param dataobjId 数据对象主键
     * @return 数据对象
     */
    public SysReDataobj selectSysReDataobjByDataobjId(String dataobjId);

    /**
     * 查询数据对象
     *
     * @param dataobjName 数据对象名称
     * @return 数据对象
     */
    public SysReDataobj selectSysReDataobjByDataobjName(String dataobjName);

    /**
     *
     * @param pyId
     * @return
     */
    public SysReDataobj selectSysReDataobjByActivityTarget(String pyId);
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
     * 删除数据对象
     * 
     * @param dataobjId 数据对象主键
     * @return 结果
     */
    public int deleteSysReDataobjByDataobjId(String dataobjId);

    /**
     * 批量删除数据对象
     * 
     * @param dataobjIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysReDataobjByDataobjIds(String[] dataobjIds);

    /**
     * 批量删除字段
     * 
     * @param dataobjIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysReDatafieldByDataobjIds(String[] dataobjIds);
    
    /**
     * 批量新增字段
     * 
     * @param sysReDatafieldList 字段列表
     * @return 结果
     */
    public int batchSysReDatafield(List<SysReDatafield> sysReDatafieldList);
    

    /**
     * 通过数据对象主键删除字段信息
     * 
     * @param dataobjId 数据对象ID
     * @return 结果
     */
    public int deleteSysReDatafieldByDataobjId(String dataobjId);

    /**
     * 动态创建一个表
     * @Param name : 动态生成表的表名
     * @Param mymap: 表的列名及类型
     */
    public void creatTemplateTable(@Param("name") String name, @Param("mymap") Map<String, String> mymap);

    /**
     *  选择指定的表中所有的数据
     * @param tableName
     * @return
     */
    public List<HashMap<String,Object>> selectTemplateTableData(@Param("columns") String columns, @Param("tableName") String tableName);

    /**
     *  删除动态表中指定的数据
     * @param tableName
     * @param id
     * @return
     */
    public int deleteTemplateTableById(@Param("tableName") String tableName, @Param("id") Long id);

    /**
     * 动态插入数据
     * @param tableName
     * @param fields
     * @param values
     * @return
     */
    public int insertTemplateTableData(@Param("tableName") String tableName, @Param("fields") String fields, @Param("values") String values);

    /**
     * 删除表数据
     * @param tableName
     */
    public void truncateTable(@Param("tableName") String tableName);

    /**
     * 删除表
     * @param tableName
     */
    public void deleteTable(@Param("tableName") String tableName);

    public void alterTableName(@Param("tableName") String tableName);

    public void createNewTableAndInsertData(@Param("newTableName") String newTableName, @Param("originalTableName") String originalTableName);

    public int getRecordCount(@Param("tableName") String tableName);

    public String getCurDataBaseName();

    public String isTargetTableExistInDB(@Param("dataBaseName") String dataBaseName, @Param("tableName") String tableName);
}
