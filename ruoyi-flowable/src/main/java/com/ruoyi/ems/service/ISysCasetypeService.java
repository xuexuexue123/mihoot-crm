package com.ruoyi.ems.service;

import java.util.List;
import com.ruoyi.ems.domain.SysCasetype;

/**
 * casetypeService接口
 *
 * @author luohd
 * @date 2022-12-15
 */
public interface ISysCasetypeService
{
    /**
     * 查询casetype
     *
     * @param id casetype主键
     * @return casetype
     */
    public SysCasetype selectSysCasetypeById(Long id);


    public SysCasetype selectSysCasetypeByCaseTypeId(String caseTypeId);

    /**
     * 查询casetype data type
     *
     * @param id casetype主键
     * @return casetype
     */
    public SysCasetype selectSysCasetypeDatatypeById(Long id);

    /**
     * 查询casetype列表
     *
     * @param sysCasetype casetype
     * @return casetype集合
     */
    public List<SysCasetype> selectSysCasetypeList(SysCasetype sysCasetype);

    /**
     * 新增casetype
     *
     * @param sysCasetype casetype
     * @return 结果
     */
    public int insertSysCasetype(SysCasetype sysCasetype);

    /**
     * 修改casetype
     *
     * @param sysCasetype casetype
     * @return 结果
     */
    public int updateSysCasetype(SysCasetype sysCasetype);

    /**
     * 修改casetype
     *
     * @param sysCasetype casetype
     * @return 结果
     */
    public int updateSysCasetypeOnly(SysCasetype sysCasetype);
    /**
     * 批量删除casetype
     *
     * @param ids 需要删除的casetype主键集合
     * @return 结果
     */
    public int deleteSysCasetypeByIds(String[] ids);

    /**
     * 删除casetype信息
     *
     * @param id casetype主键
     * @return 结果
     */
    public int deleteSysCasetypeById(Long id);

    /**
     *
     * @param caseTypeId
     * @return
     */
    public int deleteSysCasetypeByCaseTypeId(String caseTypeId);
}
