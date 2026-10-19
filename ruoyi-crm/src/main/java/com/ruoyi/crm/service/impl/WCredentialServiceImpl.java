package com.ruoyi.crm.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.mapper.WCredentialMapper;
import com.ruoyi.crm.domain.WCredential;
import com.ruoyi.crm.service.IWCredentialService;

/**
 * WCredentialService业务层处理
 * 
 * @author luohd
 * @date 2026-05-19
 */
@Service
public class WCredentialServiceImpl implements IWCredentialService
{
    @Autowired
    private WCredentialMapper wCredentialMapper;

    /**
     * 查询WCredential
     * 
     * @param id WCredential主键
     * @return WCredential
     */
    @Override
    public WCredential selectWCredentialById(String id)
    {
        return wCredentialMapper.selectWCredentialById(id);
    }

    /**
     * 查询WCredential列表
     * 
     * @param wCredential WCredential
     * @return WCredential
     */
    @Override
    public List<WCredential> selectWCredentialList(WCredential wCredential)
    {
        return wCredentialMapper.selectWCredentialList(wCredential);
    }

    /**
     * 新增WCredential
     * 
     * @param wCredential WCredential
     * @return 结果
     */
    @Override
    public int insertWCredential(WCredential wCredential)
    {
        if (StringUtils.isBlank(wCredential.getId()))
        {
            wCredential.setId(IdUtils.fastSimpleUUID());
        }
        if (StringUtils.isBlank(wCredential.getUserId()))
        {
            wCredential.setUserId(String.valueOf(SecurityUtils.getUserId()));
        }
        if (StringUtils.isBlank(wCredential.getOrganizationId()))
        {
            SysUser user = SecurityUtils.getLoginUser().getUser();
            if (user.getOrganization() != null
                    && StringUtils.isNotBlank(user.getOrganization().getOrganizationId()))
            {
                wCredential.setOrganizationId(user.getOrganization().getOrganizationId());
            }
            else if (user.getOrganizationId() != null)
            {
                wCredential.setOrganizationId(String.valueOf(user.getOrganizationId()));
            }
        }
        normalizePrimary(wCredential);
        return wCredentialMapper.insertWCredential(wCredential);
    }

    /**
     * 修改WCredential
     * 
     * @param wCredential WCredential
     * @return 结果
     */
    @Override
    public int updateWCredential(WCredential wCredential)
    {
        if (StringUtils.isBlank(wCredential.getUserId()) || StringUtils.isBlank(wCredential.getType()))
        {
            WCredential existing = wCredentialMapper.selectWCredentialById(wCredential.getId());
            if (existing != null)
            {
                if (StringUtils.isBlank(wCredential.getUserId()))
                {
                    wCredential.setUserId(existing.getUserId());
                }
                if (StringUtils.isBlank(wCredential.getType()))
                {
                    wCredential.setType(existing.getType());
                }
            }
        }
        normalizePrimary(wCredential);
        return wCredentialMapper.updateWCredential(wCredential);
    }

    /**
     * 默认非主凭证；设为主凭证时，清除同用户同类型的其他主凭证标记
     */
    private void normalizePrimary(WCredential wCredential)
    {
        if (wCredential.getIsPrimary() == null)
        {
            wCredential.setIsPrimary(0L);
        }
        if (wCredential.getIsPrimary() == 1L
                && StringUtils.isNotBlank(wCredential.getUserId())
                && StringUtils.isNotBlank(wCredential.getType()))
        {
            wCredentialMapper.clearPrimaryByUserAndType(
                    wCredential.getUserId(), wCredential.getType(), wCredential.getId());
        }
    }

    /**
     * 批量删除WCredential
     * 
     * @param ids 需要删除的WCredential主键
     * @return 结果
     */
    @Override
    public int deleteWCredentialByIds(String[] ids)
    {
        return wCredentialMapper.deleteWCredentialByIds(ids);
    }

    /**
     * 删除WCredential信息
     * 
     * @param id WCredential主键
     * @return 结果
     */
    @Override
    public int deleteWCredentialById(String id)
    {
        return wCredentialMapper.deleteWCredentialById(id);
    }
}
