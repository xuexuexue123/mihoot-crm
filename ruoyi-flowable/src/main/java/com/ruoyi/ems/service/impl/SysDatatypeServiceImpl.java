package com.ruoyi.ems.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ems.mapper.SysDatatypeMapper;
import com.ruoyi.ems.domain.SysDatatype;
import com.ruoyi.ems.service.ISysDatatypeService;

/**
 * datatypeService业务层处理
 * 
 * @author luohd
 * @date 2022-12-13
 */
@Service
public class SysDatatypeServiceImpl implements ISysDatatypeService 
{
    @Autowired
    private SysDatatypeMapper sysDatatypeMapper;

    /**
     * 查询datatype
     * 
     * @param dataId datatype主键
     * @return datatype
     */
    @Override
    public SysDatatype selectSysDatatypeByDataId(Long dataId)
    {
        return sysDatatypeMapper.selectSysDatatypeByDataId(dataId);
    }

    /**
     * 查询datatype列表
     * 
     * @param sysDatatype datatype
     * @return datatype
     */
    @Override
    public List<SysDatatype> selectSysDatatypeList(SysDatatype sysDatatype)
    {
        return sysDatatypeMapper.selectSysDatatypeList(sysDatatype);
    }

    /**
     * 新增datatype
     * 
     * @param sysDatatype datatype
     * @return 结果
     */
    @Override
    public int insertSysDatatype(SysDatatype sysDatatype)
    {
        sysDatatype.setCreateTime(DateUtils.getNowDate());
        return sysDatatypeMapper.insertSysDatatype(sysDatatype);
    }

    /**
     * 修改datatype
     * 
     * @param sysDatatype datatype
     * @return 结果
     */
    @Override
    public int updateSysDatatype(SysDatatype sysDatatype)
    {
        sysDatatype.setUpdateTime(DateUtils.getNowDate());
        return sysDatatypeMapper.updateSysDatatype(sysDatatype);
    }

    /**
     * 批量删除datatype
     * 
     * @param dataIds 需要删除的datatype主键
     * @return 结果
     */
    @Override
    public int deleteSysDatatypeByDataIds(Long[] dataIds)
    {
        return sysDatatypeMapper.deleteSysDatatypeByDataIds(dataIds);
    }

    /**
     * 删除datatype信息
     * 
     * @param dataId datatype主键
     * @return 结果
     */
    @Override
    public int deleteSysDatatypeByDataId(Long dataId)
    {
        return sysDatatypeMapper.deleteSysDatatypeByDataId(dataId);
    }
}
