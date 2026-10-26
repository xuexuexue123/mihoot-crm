package com.ruoyi.ems.mapper;

import java.util.List;
import com.ruoyi.ems.domain.SysCasetype;
import com.ruoyi.ems.domain.SysDatatype;
import com.ruoyi.ems.domain.SysStage;

/**
 * casetypeMapper接口
 *
 * @author luohd
 * @date 2022-12-15
 */
public interface SysCasetypeMapper
{
    /**
     * 查询casetype
     *
     * @param id casetype主键
     * @return casetype
     */
    public SysCasetype selectSysCasetypeById(Long id);

    /**
     * 获取指定的casetype
     * @param caseTypeId
     * @return
     */
    public SysCasetype selectSysCasetypeByCaseTypeId(String caseTypeId);

    /**
     * 查询casetype
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
     * 删除casetype
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
    /**
     * 批量删除casetype
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysCasetypeByIds(String[] ids);

    /**
     * 批量删除stage
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysStageByCaseTypeIds(String[] ids);

    /**
     * 批量新增stage
     *
     * @param sysStageList stage列表
     * @return 结果
     */
    public int batchSysStage(List<SysStage> sysStageList);

    /**
     *
     * @param sysDatatypeList
     * @return
     */
    public int batchSysDatatype(List<SysDatatype> sysDatatypeList);


    /**
     * 通过casetype主键删除stage信息
     *
     * @param caseTypeId casetypeID
     * @return 结果
     */
    public int deleteSysStageByCaseTypeId(String caseTypeId);
//    public int deleteSysStageByCaseTypeId(String id);

    public int deleteSysProcessByCaseTypeId(String caseTypeId);
    public int deleteSysDatatypeByCaseTypeId(String caseTypeId);
    public int deleteSysReDatatransformByCaseTypeId(String caseTypeId);

    public int deleteSysReDtitemByCaseTypeId(String caseTypeId);
    public int deleteSysReDecisiontreeByCaseTypeId(String caseTypeId);
    public int deleteSysReDecisiontableByCaseTypeId(String caseTypeId);
    public int deleteSysReWhenByCaseTypeId(String caseTypeId);

    public int deleteSysReActivityByCaseTypeId(String caseTypeId);
    public int deleteSysReActitemByCaseTypeId(String caseTypeId);
}
