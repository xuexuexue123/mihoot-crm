package com.ruoyi.ems.service;

import java.util.List;
import com.ruoyi.ems.domain.SysDatatype;

/**
 * datatypeService接口
 * 
 * @author luohd
 * @date 2022-12-13
 */
public interface ISysDatatypeService 
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
     * 批量删除datatype
     * 
     * @param dataIds 需要删除的datatype主键集合
     * @return 结果
     */
    public int deleteSysDatatypeByDataIds(Long[] dataIds);

    /**
     * 删除datatype信息
     * 
     * @param dataId datatype主键
     * @return 结果
     */
    public int deleteSysDatatypeByDataId(Long dataId);
}
