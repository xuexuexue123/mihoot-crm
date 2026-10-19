package com.ruoyi.ems.service.impl;

import java.util.*;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.ems.domain.SysReFieldPOJO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.ems.domain.SysReDatafield;
import com.ruoyi.ems.mapper.SysReDataobjMapper;
import com.ruoyi.ems.domain.SysReDataobj;
import com.ruoyi.ems.service.ISysReDataobjService;

/**
 * 数据对象Service业务层处理
 * 
 * @author luohd
 * @date 2023-03-02
 */
@Service
public class SysReDataobjServiceImpl implements ISysReDataobjService 
{
    @Autowired
    private SysReDataobjMapper sysReDataobjMapper;

    /**
     * 查询数据对象
     * 
     * @param dataobjId 数据对象主键
     * @return 数据对象
     */
    @Override
    public SysReDataobj selectSysReDataobjByDataobjId(String dataobjId)
    {
        return sysReDataobjMapper.selectSysReDataobjByDataobjId(dataobjId);
    }

    @Override
    public SysReDataobj selectSysReDataobjByActivityTarget(String pyId) {
        return sysReDataobjMapper.selectSysReDataobjByActivityTarget(pyId);
    }
    /**
     * 查询数据对象
     *
     * @param dataobjName 数据对象名称
     * @return 数据对象
     */
    @Override
    public SysReDataobj selectSysReDataobjByDataobjName(String dataobjName)
    {
        return sysReDataobjMapper.selectSysReDataobjByDataobjName(dataobjName);
    }
    /**
     * 查询数据对象列表
     * 
     * @param sysReDataobj 数据对象
     * @return 数据对象
     */
    @Override
    public List<SysReDataobj> selectSysReDataobjList(SysReDataobj sysReDataobj)
    {
        return sysReDataobjMapper.selectSysReDataobjList(sysReDataobj);
    }

    /**
     * 新增数据对象
     * 
     * @param sysReDataobj 数据对象
     * @return 结果
     */
    @Transactional
    @Override
    public int insertSysReDataobj(SysReDataobj sysReDataobj)
    {
        sysReDataobj.setCreateTime(DateUtils.getNowDate());
        int rows = sysReDataobjMapper.insertSysReDataobj(sysReDataobj);
        insertSysReDatafield(sysReDataobj);
        return rows;
    }

    /**
     * 修改数据对象
     * 
     * @param sysReDataobj 数据对象
     * @return 结果
     */
    @Transactional
    @Override
    public int updateSysReDataobj(SysReDataobj sysReDataobj)
    {
        sysReDataobj.setUpdateTime(DateUtils.getNowDate());
        sysReDataobjMapper.deleteSysReDatafieldByDataobjId(sysReDataobj.getDataobjId());
        insertSysReDatafield(sysReDataobj);
        return sysReDataobjMapper.updateSysReDataobj(sysReDataobj);
    }

    /**
     * 批量删除数据对象
     * 
     * @param dataobjIds 需要删除的数据对象主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteSysReDataobjByDataobjIds(String[] dataobjIds)
    {
        sysReDataobjMapper.deleteSysReDatafieldByDataobjIds(dataobjIds);
        return sysReDataobjMapper.deleteSysReDataobjByDataobjIds(dataobjIds);
    }

    /**
     * 删除数据对象信息
     * 
     * @param dataobjId 数据对象主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteSysReDataobjByDataobjId(String dataobjId)
    {
        sysReDataobjMapper.deleteSysReDatafieldByDataobjId(dataobjId);
        return sysReDataobjMapper.deleteSysReDataobjByDataobjId(dataobjId);
    }

    /**
     * 新增字段信息
     * 
     * @param sysReDataobj 数据对象对象
     */
    public void insertSysReDatafield(SysReDataobj sysReDataobj)
    {
        List<SysReDatafield> sysReDatafieldList = sysReDataobj.getSysReDatafieldList();
        String dataobjId = sysReDataobj.getDataobjId();
        if (StringUtils.isNotNull(sysReDatafieldList))
        {
            List<SysReDatafield> list = new ArrayList<SysReDatafield>();
            for (SysReDatafield sysReDatafield : sysReDatafieldList)
            {

                UUID dataFieldId = UUID.randomUUID();
                sysReDatafield.setFieldId(dataFieldId.toString());
                sysReDatafield.setDataobjId(dataobjId);
                list.add(sysReDatafield);
            }
            if (list.size() > 0)
            {
                sysReDataobjMapper.batchSysReDatafield(list);
            }
        }
    }

    /**
     * @Param name : 动态生成表的表名
     * @Param mymap: 表的列名及类型
     */
    @Override
    public void creatTemplateTable(String name, Map<String, String> mymap) {
        sysReDataobjMapper.creatTemplateTable(name,mymap);
    }

    /**
     *
     * @param tableName
     * @return
     */
    public List<HashMap<String, Object>> selectTemplateTableData(String columns, String tableName) {
        return sysReDataobjMapper.selectTemplateTableData(columns, tableName);
    }

    @Transactional
    @Override
    public int deleteTemplateTableById(String tableName, Long id)
    {
        return sysReDataobjMapper.deleteTemplateTableById(tableName, id);
    }

    @Override
    public int insertTemplateTableData(String tableName, String fields, String values) {
        return sysReDataobjMapper.insertTemplateTableData(tableName, fields, values);
    }

    @Transactional
    @Override
    public void truncateTable(String tableName) {
        sysReDataobjMapper.truncateTable(tableName);
    }

    @Transactional
    @Override
    public void deleteTable(String tableName) {
        sysReDataobjMapper.deleteTable(tableName);
    }

    @Override
    public void alterTableName(String tableName) {
        sysReDataobjMapper.alterTableName(tableName);
    }

    @Override
    public void createNewTableAndInsertData(String newTableName, String originalTableName) {
        sysReDataobjMapper.createNewTableAndInsertData(newTableName,originalTableName);
    }

    @Override
    public int getRecordCount(String tableName) {
        return sysReDataobjMapper.getRecordCount(tableName);
    }

    @Override
    public String getCurDataBaseName() {
        return sysReDataobjMapper.getCurDataBaseName();
    }

    @Override
    public String isTargetTableExistInDB(String dataBaseName, String tableName) {
        return sysReDataobjMapper.isTargetTableExistInDB(dataBaseName, tableName);
    }
}
