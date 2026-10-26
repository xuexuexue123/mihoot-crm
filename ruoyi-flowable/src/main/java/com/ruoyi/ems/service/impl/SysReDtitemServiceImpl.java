package com.ruoyi.ems.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ems.mapper.SysReDtitemMapper;
import com.ruoyi.ems.domain.SysReDtitem;
import com.ruoyi.ems.service.ISysReDtitemService;

/**
 * 数据转换项Service业务层处理
 * 
 * @author luohd
 * @date 2023-03-09
 */
@Service
public class SysReDtitemServiceImpl implements ISysReDtitemService 
{
    @Autowired
    private SysReDtitemMapper sysReDtitemMapper;

    /**
     * 查询数据转换项
     * 
     * @param pyId 数据转换项主键
     * @return 数据转换项
     */
    @Override
    public SysReDtitem selectSysReDtitemByPyId(String pyId)
    {
        return sysReDtitemMapper.selectSysReDtitemByPyId(pyId);
    }

    /**
     * 查询数据转换项列表
     * 
     * @param sysReDtitem 数据转换项
     * @return 数据转换项
     */
    @Override
    public List<SysReDtitem> selectSysReDtitemList(SysReDtitem sysReDtitem)
    {
        return sysReDtitemMapper.selectSysReDtitemList(sysReDtitem);
    }

    /**
     * 新增数据转换项
     * 
     * @param sysReDtitem 数据转换项
     * @return 结果
     */
    @Override
    public int insertSysReDtitem(SysReDtitem sysReDtitem)
    {
        sysReDtitem.setCreateTime(DateUtils.getNowDate());
        return sysReDtitemMapper.insertSysReDtitem(sysReDtitem);
    }

    /**
     * 修改数据转换项
     * 
     * @param sysReDtitem 数据转换项
     * @return 结果
     */
    @Override
    public int updateSysReDtitem(SysReDtitem sysReDtitem)
    {
        sysReDtitem.setUpdateTime(DateUtils.getNowDate());
        return sysReDtitemMapper.updateSysReDtitem(sysReDtitem);
    }

    /**
     * 批量删除数据转换项
     * 
     * @param pyIds 需要删除的数据转换项主键
     * @return 结果
     */
    @Override
    public int deleteSysReDtitemByPyIds(String[] pyIds)
    {
        return sysReDtitemMapper.deleteSysReDtitemByPyIds(pyIds);
    }

    /**
     * 删除数据转换项信息
     * 
     * @param pyId 数据转换项主键
     * @return 结果
     */
    @Override
    public int deleteSysReDtitemByPyId(String pyId)
    {
        return sysReDtitemMapper.deleteSysReDtitemByPyId(pyId);
    }


    /**
     * 删除数据转换项信息
     *
     * @param dtId 数据转换项主键
     * @return 结果
     */
    @Override
    public int deleteSysReDtitemByDtId(Long dtId)
    {
        return sysReDtitemMapper.deleteSysReDtitemByDtId(dtId);
    }


    /**
     * 批量新增字段
     *
     * @param sysReDtitemList 字段列表
     * @return 结果
     */
    @Override
    public int batchSysReDtitemList(List<SysReDtitem> sysReDtitemList) {
        return sysReDtitemMapper.batchSysReDtitemList(sysReDtitemList);
    }
}
