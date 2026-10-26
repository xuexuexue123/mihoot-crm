package com.ruoyi.ems.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.ems.domain.*;
import com.ruoyi.ems.mapper.SysReDatatransformMapper;
import com.ruoyi.ems.mapper.SysReDtitemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.UUID;

import com.ruoyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.ems.mapper.SysCasetypeMapper;
import com.ruoyi.ems.service.ISysCasetypeService;

/**
 * casetypeService业务层处理
 *
 * @author luohd
 * @date 2022-12-15
 */
@Service
public class SysCasetypeServiceImpl implements ISysCasetypeService
{
    @Autowired
    private SysCasetypeMapper sysCasetypeMapper;

    @Autowired
    private SysReDatatransformMapper sysReDatatransformMapper;

    @Autowired
    private SysReDtitemMapper sysReDtitemMapper;


    /**
     * 查询casetype
     *
     * @param id casetype主键
     * @return casetype
     */
    @Override
    public SysCasetype selectSysCasetypeById(Long id)
    {
        return sysCasetypeMapper.selectSysCasetypeById(id);
    }

    /**
     * 获取指定的case type
     * @param caseTypeId
     * @return
     */
    @Override
    public SysCasetype selectSysCasetypeByCaseTypeId(String caseTypeId) {
        return sysCasetypeMapper.selectSysCasetypeByCaseTypeId(caseTypeId);
    }
    /**
     * 查询casetype
     *
     * @param id casetype主键
     * @return casetype
     */
    @Override
    public SysCasetype selectSysCasetypeDatatypeById(Long id)
    {
        return sysCasetypeMapper.selectSysCasetypeDatatypeById(id);
    }

    /**
     * 查询casetype列表
     *
     * @param sysCasetype casetype
     * @return casetype
     */
    @Override
    public List<SysCasetype> selectSysCasetypeList(SysCasetype sysCasetype)
    {
        return sysCasetypeMapper.selectSysCasetypeList(sysCasetype);
    }

    /**
     * 新增casetype
     *
     * @param sysCasetype casetype
     * @return 结果
     */
    @Transactional
    @Override
    public int insertSysCasetype(SysCasetype sysCasetype)
    {

        sysCasetype.setCreateTime(DateUtils.getNowDate());
        UUID caseId = UUID.randomUUID();
        sysCasetype.setCaseTypeId(caseId.toString());
        int rows = sysCasetypeMapper.insertSysCasetype(sysCasetype);

        insertSysStage(sysCasetype);

        insertSysDatatype(sysCasetype);

        //to do 插入数据转换 data transform
        SysReDatatransform sysReDatatransform = new SysReDatatransform();
        sysReDatatransform.setCaseTypeId(caseId.toString());
        sysReDatatransform.setPyLabel("pyDefault");
        sysReDatatransform.setPyName("pyDefault");
        sysReDatatransform.setSource(caseId.toString());
        /*
        todo : 把这里的HCP换成其他的默认数据对象
         */
        sysReDatatransform.setTarget("HCP");
        sysReDatatransform.setCreateTime(DateUtils.getNowDate());

        sysReDatatransformMapper.insertSysReDatatransform(sysReDatatransform);
        Long dtId = sysReDatatransform.getPyId();

                //插入data transform里面的 item
        SysReDtitem sysReDtitem = new SysReDtitem();
        sysReDtitem.setPyId(UUID.randomUUID().toString());
        sysReDtitem.setDtId(dtId);
        sysReDtitem.setPySort((long)0);
        sysReDtitem.setPyName("Set");
        sysReDtitem.setAction("Set");
        sysReDtitem.setTarget("pyLabel");
        sysReDtitem.setRelation("=");
        sysReDtitem.setSource(sysCasetype.getCaseLabel());
        sysReDtitem.setCreateTime(DateUtils.getNowDate());

        sysReDtitemMapper.insertSysReDtitem(sysReDtitem);

        //to do 插入前缀

        return rows;
    }

    /**
     * 修改casetype
     *
     * @param sysCasetype casetype
     * @return 结果
     */
    @Transactional
    @Override
    public int updateSysCasetype(SysCasetype sysCasetype)
    {
        sysCasetype.setUpdateTime(DateUtils.getNowDate());
        sysCasetypeMapper.deleteSysStageByCaseTypeId(sysCasetype.getCaseTypeId());
        insertSysStage(sysCasetype);
        return sysCasetypeMapper.updateSysCasetype(sysCasetype);
    }
    /**
     * 修改casetype
     *
     * @param sysCasetype casetype
     * @return 结果
     */
    @Transactional
    @Override
    public int updateSysCasetypeOnly(SysCasetype sysCasetype)
    {
        sysCasetype.setUpdateTime(DateUtils.getNowDate());

        return sysCasetypeMapper.updateSysCasetype(sysCasetype);
    }
    /**
     * 批量删除casetype
     *
     * @param ids 需要删除的casetype主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteSysCasetypeByIds(String[] ids)
    {
        sysCasetypeMapper.deleteSysStageByCaseTypeIds(ids);
        return sysCasetypeMapper.deleteSysCasetypeByIds(ids);
    }

    /**
     * 删除casetype信息
     *
     * @param id casetype主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteSysCasetypeById(Long id) {

        //删除casetype
        return sysCasetypeMapper.deleteSysCasetypeById(id);
    }
    /**
     * 删除casetype信息
     *
     * @param caseTypeId casetype主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteSysCasetypeByCaseTypeId(String caseTypeId)
    {
//        SysCasetype caseType = sysCasetypeMapper.selectSysCasetypeById(id);
//        String caseTypeId = caseType.getCaseTypeId();
        //删除stage
        sysCasetypeMapper.deleteSysStageByCaseTypeId(caseTypeId);
        //删除流程process
        sysCasetypeMapper.deleteSysProcessByCaseTypeId(caseTypeId);
        //删除Datatype
        sysCasetypeMapper.deleteSysDatatypeByCaseTypeId(caseTypeId);
        //删除Datatransform
        sysCasetypeMapper.deleteSysReDatatransformByCaseTypeId(caseTypeId);
        //删除Dataitem
        sysCasetypeMapper.deleteSysReDtitemByCaseTypeId(caseTypeId);
        //删除Decision tree
        sysCasetypeMapper.deleteSysReDecisiontreeByCaseTypeId(caseTypeId);
        //删除decision table
        sysCasetypeMapper.deleteSysReDecisiontableByCaseTypeId(caseTypeId);
        //删除when
        sysCasetypeMapper.deleteSysReWhenByCaseTypeId(caseTypeId);

        //删除activity
        sysCasetypeMapper.deleteSysReActivityByCaseTypeId(caseTypeId);
        //删除activity item
        sysCasetypeMapper.deleteSysReDtitemByCaseTypeId(caseTypeId);


        //删除casetype
        return sysCasetypeMapper.deleteSysCasetypeByCaseTypeId(caseTypeId);
    }

    /**
     * 新增stage信息
     *
     * @param sysCasetype casetype对象
     */
    public void insertSysStage(SysCasetype sysCasetype)
    {
        List<SysStage> sysStageList = sysCasetype.getSysStageList();
        String id = sysCasetype.getCaseTypeId();
        if (StringUtils.isNotNull(sysStageList))
        {
            List<SysStage> list = new ArrayList<SysStage>();
            for (SysStage sysStage : sysStageList)
            {
                sysStage.setCaseTypeId(id);
                list.add(sysStage);
            }
            if (list.size() > 0)
            {
                sysCasetypeMapper.batchSysStage(list);
            }
        }
    }

    public void insertSysDatatype(SysCasetype sysCasetype) {
        List<SysDatatype> sysDatatypeList = sysCasetype.getSysDatatypeList();
        String id = sysCasetype.getCaseTypeId();
        if (StringUtils.isNotNull(sysDatatypeList)) {
            List<SysDatatype> list = new ArrayList<SysDatatype>();
            for(SysDatatype sysDatatype : sysDatatypeList) {
                sysDatatype.setCaseTypeId(id);
                list.add(sysDatatype);
            }
            if (list.size() > 0) {
                sysCasetypeMapper.batchSysDatatype(list);
            }
        }
    }
}
