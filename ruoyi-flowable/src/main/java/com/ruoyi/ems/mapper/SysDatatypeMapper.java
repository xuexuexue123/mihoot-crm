package com.ruoyi.ems.mapper;

import java.util.List;
import com.ruoyi.ems.domain.SysDatatype;

/**
 * datatypeMapper接口
 * 
 * @author luohd
 * @date 2022-12-13
 */
public interface SysDatatypeMapper 
{
    /**
     * 查询datatype
     * 
     * @param dataId datatype主键
     * @return datatype
     */
    public SysDatatype selectSysDatatypeByDataId(Long dataId);

    /**
     * 查询datatype列表
     * 
     * @param sysDatatype datatype
     * @return datatype集合
     */
    public List<SysDatatype> selectSysDatatypeList(SysDatatype sysDatatype);

    /**
     * 新增datatype
     * 
     * @param sysDatatype datatype
     * @return 结果
     */
    public int insertSysDatatype(SysDatatype sysDatatype);

    /**
     * 修改datatype
     * 
     * @param sysDatatype datatype
     * @return 结果
     */
    public int updateSysDatatype(SysDatatype sysDatatype);

    /**
     * 删除datatype
     * 
     * @param dataId datatype主键
     * @return 结果
     */
    public int deleteSysDatatypeByDataId(Long dataId);

    /**
     * 批量删除datatype
     * 
     * @param dataIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysDatatypeByDataIds(Long[] dataIds);
}
