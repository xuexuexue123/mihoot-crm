package com.ruoyi.ems.mapper;

import java.util.List;
import com.ruoyi.ems.domain.SysReDatatransform;

/**
 * 数据转换Mapper接口
 * 
 * @author luohd
 * @date 2023-03-09
 */
public interface SysReDatatransformMapper 
{
    /**
     * 查询数据转换
     * 
     * @param pyId 数据转换主键
     * @return 数据转换
     */
    public SysReDatatransform selectSysReDatatransformByPyId(Long pyId);

    /**
     *
     * @param sysReDatatransform
     * @return
     */
    public SysReDatatransform selectSysReDatatransformByCase(SysReDatatransform sysReDatatransform);

    /**
     * 查询数据转换列表
     * 
     * @param sysReDatatransform 数据转换
     * @return 数据转换集合
     */
    public List<SysReDatatransform> selectSysReDatatransformList(SysReDatatransform sysReDatatransform);

    /**
     * 新增数据转换
     * 
     * @param sysReDatatransform 数据转换
     * @return 结果
     */
    public int insertSysReDatatransform(SysReDatatransform sysReDatatransform);

    /**
     * 修改数据转换
     * 
     * @param sysReDatatransform 数据转换
     * @return 结果
     */
    public int updateSysReDatatransform(SysReDatatransform sysReDatatransform);

    /**
     * 删除数据转换
     * 
     * @param pyId 数据转换主键
     * @return 结果
     */
    public int deleteSysReDatatransformByPyId(Long pyId);

    /**
     * 批量删除数据转换
     * 
     * @param pyIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysReDatatransformByPyIds(Long[] pyIds);
}
