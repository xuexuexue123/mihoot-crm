package com.ruoyi.ems.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ems.mapper.SysReDatatransformMapper;
import com.ruoyi.ems.domain.SysReDatatransform;
import com.ruoyi.ems.service.ISysReDatatransformService;

/**
 * 数据转换Service业务层处理
 * 
 * @author luohd
 * @date 2023-03-09
 */
@Service
public class SysReDatatransformServiceImpl implements ISysReDatatransformService 
{
    @Autowired
    private SysReDatatransformMapper sysReDatatransformMapper;

    /**
     * 查询数据转换
     * 
     * @param pyId 数据转换主键
     * @return 数据转换
     */
    @Override
    public SysReDatatransform selectSysReDatatransformByPyId(Long pyId)
    {
        return sysReDatatransformMapper.selectSysReDatatransformByPyId(pyId);
    }

    @Override
    public SysReDatatransform selectSysReDatatransformByCase(SysReDatatransform sysReDatatransform) {
        return sysReDatatransformMapper.selectSysReDatatransformByCase(sysReDatatransform);
    }

    /**
     * 查询数据转换列表
     * 
     * @param sysReDatatransform 数据转换
     * @return 数据转换
     */
    @Override
    public List<SysReDatatransform> selectSysReDatatransformList(SysReDatatransform sysReDatatransform)
    {
        return sysReDatatransformMapper.selectSysReDatatransformList(sysReDatatransform);
    }

    /**
     * 新增数据转换
     * 
     * @param sysReDatatransform 数据转换
     * @return 结果
     */
    @Override
    public int insertSysReDatatransform(SysReDatatransform sysReDatatransform)
    {
        sysReDatatransform.setCreateTime(DateUtils.getNowDate());
//        return sysReDatatransformMapper.insertSysReDatatransform(sysReDatatransform);
        sysReDatatransformMapper.insertSysReDatatransform(sysReDatatransform);
        return sysReDatatransform.getPyId().intValue();
    }

    /**
     * 修改数据转换
     * 
     * @param sysReDatatransform 数据转换
     * @return 结果
     */
    @Override
    public int updateSysReDatatransform(SysReDatatransform sysReDatatransform)
    {
        sysReDatatransform.setUpdateTime(DateUtils.getNowDate());
        return sysReDatatransformMapper.updateSysReDatatransform(sysReDatatransform);
    }

    /**
     * 批量删除数据转换
     * 
     * @param pyIds 需要删除的数据转换主键
     * @return 结果
     */
    @Override
    public int deleteSysReDatatransformByPyIds(Long[] pyIds)
    {
        return sysReDatatransformMapper.deleteSysReDatatransformByPyIds(pyIds);
    }

    /**
     * 删除数据转换信息
     * 
     * @param pyId 数据转换主键
     * @return 结果
     */
    @Override
    public int deleteSysReDatatransformByPyId(Long pyId)
    {
        return sysReDatatransformMapper.deleteSysReDatatransformByPyId(pyId);
    }
}
