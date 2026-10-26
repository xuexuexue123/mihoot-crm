package com.ruoyi.ems.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ems.mapper.SysReDatafieldMapper;
import com.ruoyi.ems.domain.SysReDatafield;
import com.ruoyi.ems.service.ISysReDatafieldService;

/**
 * 字段Service业务层处理
 * 
 * @author luohd
 * @date 2023-04-19
 */
@Service
public class SysReDatafieldServiceImpl implements ISysReDatafieldService 
{
    @Autowired
    private SysReDatafieldMapper sysReDatafieldMapper;

    /**
     * 查询字段
     * 
     * @param fieldId 字段主键
     * @return 字段
     */
    @Override
    public SysReDatafield selectSysReDatafieldByFieldId(String fieldId)
    {
        return sysReDatafieldMapper.selectSysReDatafieldByFieldId(fieldId);
    }

    /**
     * 查询字段列表
     * 
     * @param sysReDatafield 字段
     * @return 字段
     */
    @Override
    public List<SysReDatafield> selectSysReDatafieldList(SysReDatafield sysReDatafield)
    {
        return sysReDatafieldMapper.selectSysReDatafieldList(sysReDatafield);
    }

    /**
     * 新增字段
     * 
     * @param sysReDatafield 字段
     * @return 结果
     */
    @Override
    public int insertSysReDatafield(SysReDatafield sysReDatafield)
    {
        sysReDatafield.setCreateTime(DateUtils.getNowDate());
        return sysReDatafieldMapper.insertSysReDatafield(sysReDatafield);
    }

    /**
     * 修改字段
     * 
     * @param sysReDatafield 字段
     * @return 结果
     */
    @Override
    public int updateSysReDatafield(SysReDatafield sysReDatafield)
    {
        sysReDatafield.setUpdateTime(DateUtils.getNowDate());
        return sysReDatafieldMapper.updateSysReDatafield(sysReDatafield);
    }

    /**
     * 批量删除字段
     * 
     * @param fieldIds 需要删除的字段主键
     * @return 结果
     */
    @Override
    public int deleteSysReDatafieldByFieldIds(String[] fieldIds)
    {
        return sysReDatafieldMapper.deleteSysReDatafieldByFieldIds(fieldIds);
    }

    /**
     * 删除字段信息
     * 
     * @param fieldId 字段主键
     * @return 结果
     */
    @Override
    public int deleteSysReDatafieldByFieldId(String fieldId)
    {
        return sysReDatafieldMapper.deleteSysReDatafieldByFieldId(fieldId);
    }

    /**
     * 批量插入数据表字段
     * @param sysReDatafieldList
     * @return
     */
    @Override
    public int batchInsertSysReDatafieldList(List<SysReDatafield> sysReDatafieldList) {
        return sysReDatafieldMapper.batchInsertSysReDatafieldList(sysReDatafieldList);
    }
}
