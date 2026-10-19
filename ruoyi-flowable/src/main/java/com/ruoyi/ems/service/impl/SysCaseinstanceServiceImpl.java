package com.ruoyi.ems.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ems.mapper.SysCaseinstanceMapper;
import com.ruoyi.ems.domain.SysCaseinstance;
import com.ruoyi.ems.service.ISysCaseinstanceService;

/**
 * case实例Service业务层处理
 * 
 * @author luohd
 * @date 2022-12-29
 */
@Service
public class SysCaseinstanceServiceImpl implements ISysCaseinstanceService 
{
    @Autowired
    private SysCaseinstanceMapper sysCaseinstanceMapper;

    /**
     * 查询case实例
     * 
     * @param id case实例主键
     * @return case实例
     */
    @Override
    public SysCaseinstance selectSysCaseinstanceById(Long id)
    {
        return sysCaseinstanceMapper.selectSysCaseinstanceById(id);
    }

    public SysCaseinstance selectSysCaseinstanceByCaseId(String pyCaseId) {

        return sysCaseinstanceMapper.selectSysCaseinstanceByCaseId(pyCaseId);
    }
    /**
     * 查询case实例列表
     * 
     * @param sysCaseinstance case实例
     * @return case实例
     */
    @Override
    public List<SysCaseinstance> selectSysCaseinstanceList(SysCaseinstance sysCaseinstance)
    {
        return sysCaseinstanceMapper.selectSysCaseinstanceList(sysCaseinstance);
    }

    /**
     * 新增case实例
     * 
     * @param sysCaseinstance case实例
     * @return 结果
     */
    @Override
    public int insertSysCaseinstance(SysCaseinstance sysCaseinstance)
    {
        return sysCaseinstanceMapper.insertSysCaseinstance(sysCaseinstance);
    }

    /**
     * 修改case实例
     * 
     * @param sysCaseinstance case实例
     * @return 结果
     */
    @Override
    public int updateSysCaseinstance(SysCaseinstance sysCaseinstance)
    {
        return sysCaseinstanceMapper.updateSysCaseinstance(sysCaseinstance);
    }

    /**
     * 批量删除case实例
     * 
     * @param ids 需要删除的case实例主键
     * @return 结果
     */
    @Override
    public int deleteSysCaseinstanceByIds(Long[] ids)
    {
        return sysCaseinstanceMapper.deleteSysCaseinstanceByIds(ids);
    }

    /**
     * 删除case实例信息
     * 
     * @param id case实例主键
     * @return 结果
     */
    @Override
    public int deleteSysCaseinstanceById(Long id)
    {
        return sysCaseinstanceMapper.deleteSysCaseinstanceById(id);
    }
}
