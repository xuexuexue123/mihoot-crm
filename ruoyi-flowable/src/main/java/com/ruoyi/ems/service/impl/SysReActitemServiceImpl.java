package com.ruoyi.ems.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.ems.domain.SysReDtitem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ems.mapper.SysReActitemMapper;
import com.ruoyi.ems.domain.SysReActitem;
import com.ruoyi.ems.service.ISysReActitemService;

/**
 * activity项Service业务层处理
 * 
 * @author luohd
 * @date 2023-03-14
 */
@Service
public class SysReActitemServiceImpl implements ISysReActitemService 
{
    @Autowired
    private SysReActitemMapper sysReActitemMapper;

    /**
     * 查询activity项
     * 
     * @param pyId activity项主键
     * @return activity项
     */
    @Override
    public SysReActitem selectSysReActitemByPyId(String pyId)
    {
        return sysReActitemMapper.selectSysReActitemByPyId(pyId);
    }

    /**
     * 查询activity项列表
     * 
     * @param sysReActitem activity项
     * @return activity项
     */
    @Override
    public List<SysReActitem> selectSysReActitemList(SysReActitem sysReActitem)
    {
        return sysReActitemMapper.selectSysReActitemList(sysReActitem);
    }

    /**
     * 新增activity项
     * 
     * @param sysReActitem activity项
     * @return 结果
     */
    @Override
    public int insertSysReActitem(SysReActitem sysReActitem)
    {
        sysReActitem.setCreateTime(DateUtils.getNowDate());
        return sysReActitemMapper.insertSysReActitem(sysReActitem);
    }

    /**
     * 修改activity项
     * 
     * @param sysReActitem activity项
     * @return 结果
     */
    @Override
    public int updateSysReActitem(SysReActitem sysReActitem)
    {
        sysReActitem.setUpdateTime(DateUtils.getNowDate());
        return sysReActitemMapper.updateSysReActitem(sysReActitem);
    }

    /**
     * 批量删除activity项
     * 
     * @param pyIds 需要删除的activity项主键
     * @return 结果
     */
    @Override
    public int deleteSysReActitemByPyIds(String[] pyIds)
    {
        return sysReActitemMapper.deleteSysReActitemByPyIds(pyIds);
    }

    /**
     * 删除activity项信息
     * 
     * @param pyId activity项主键
     * @return 结果
     */
    @Override
    public int deleteSysReActitemByPyId(String pyId)
    {
        return sysReActitemMapper.deleteSysReActitemByPyId(pyId);
    }


    /**
     * 删除activity项信息
     *
     * @param actId activity项主键
     * @return 结果
     */
    @Override
    public int deleteSysReActitemByActId(String actId)
    {
        return sysReActitemMapper.deleteSysReActitemByActId(actId);
    }
    /**
     * 批量新增字段
     *
     * @param sysReActitemList 字段列表
     * @return 结果
     */
    @Override
    public int batchSysReActitemList(List<SysReActitem> sysReActitemList) {
        return sysReActitemMapper.batchSysReActitemList(sysReActitemList);
    }
}
