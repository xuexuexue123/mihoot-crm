package com.ruoyi.crm.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmTagChannelMapping;
import com.ruoyi.crm.domain.CrmTagDispatchTask;
import com.ruoyi.crm.domain.dto.CrmTagCreateRequest;
import com.ruoyi.crm.domain.dto.CrmTagDispatchRequest;
import com.ruoyi.crm.domain.dto.CrmTagMappingPutRequest;
import com.ruoyi.crm.domain.dto.CrmTagMergeRequest;
import com.ruoyi.crm.domain.dto.CrmTagUpdateRequest;
import com.ruoyi.crm.domain.vo.CrmTagDispatchTaskVo;
import com.ruoyi.crm.domain.vo.CrmTagMappingVo;
import com.ruoyi.crm.domain.vo.CrmTagMemberVo;
import com.ruoyi.crm.domain.vo.CrmTagMergeResultVo;
import com.ruoyi.crm.domain.vo.CrmTagOrphanVo;
import com.ruoyi.crm.domain.vo.CrmTagUsageVo;
import com.ruoyi.crm.domain.vo.CrmTagVo;
import com.ruoyi.crm.domain.vo.SegmentEnumValueVo;
import com.ruoyi.crm.mapper.CrmTagGovernanceMapper;
import com.ruoyi.crm.service.ICrmSegmentService;
import com.ruoyi.crm.service.ICrmTagService;
import com.ruoyi.crm.service.support.CrmTagDispatchWorker;
import com.ruoyi.crm.service.support.CrmTagValidationSupport;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.system.service.ISysDictDataService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CrmTagServiceImpl implements ICrmTagService
{
    private static final int MERGE_BATCH = 500;

    @Autowired
    private ISysDictDataService dictDataService;

    @Autowired
    private CrmTagGovernanceMapper governanceMapper;

    @Autowired
    @Lazy
    private ICrmSegmentService segmentService;

    @Autowired
    private CrmTagDispatchWorker dispatchWorker;

    @Override
    public List<CrmTagVo> listAll()
    {
        SysDictData query = new SysDictData();
        query.setDictType(CrmTagValidationSupport.DICT_TYPE);
        List<SysDictData> rows = dictDataService.selectDictDataList(query);
        List<CrmTagVo> result = new ArrayList<>(rows.size());
        for (SysDictData row : rows)
        {
            result.add(toVo(row));
        }
        return result;
    }

    @Override
    public List<SegmentEnumValueVo> listEnabledEnumValues()
    {
        SysDictData query = new SysDictData();
        query.setDictType(CrmTagValidationSupport.DICT_TYPE);
        query.setStatus(UserConstants.DICT_NORMAL);
        List<SysDictData> rows = dictDataService.selectDictDataList(query);
        List<SegmentEnumValueVo> result = new ArrayList<>();
        for (SysDictData row : rows)
        {
            if (!UserConstants.DICT_NORMAL.equals(row.getStatus()))
            {
                continue;
            }
            result.add(new SegmentEnumValueVo(row.getDictValue(), row.getDictLabel()));
        }
        return result;
    }

    @Override
    public CrmTagVo create(CrmTagCreateRequest request)
    {
        if (request == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmTagValidationSupport.validateDictValue(request.getDictValue());
        String category = CrmTagValidationSupport.requireCategory(request.getRemark());
        if (StringUtils.isBlank(request.getDictLabel()))
        {
            throw new ServiceException("dictLabel 不能为空", HttpStatus.BAD_REQUEST);
        }
        String dictValue = request.getDictValue().trim();
        if (findByDictValue(dictValue) != null)
        {
            throw new ServiceException("dictValue 已存在", HttpStatus.BAD_REQUEST);
        }
        int existing = countByCategory(category);
        SysDictData data = new SysDictData();
        data.setDictType(CrmTagValidationSupport.DICT_TYPE);
        data.setDictValue(dictValue);
        data.setDictLabel(request.getDictLabel().trim());
        data.setDictLabelEn(StringUtils.trimToEmpty(request.getDictLabelEn()));
        data.setDictLabelTh(StringUtils.trimToEmpty(request.getDictLabelTh()));
        data.setRemark(category);
        data.setDictSort((long) CrmTagValidationSupport.nextDictSort(category, existing));
        data.setStatus(normalizeStatus(request.getStatus()));
        data.setListClass("default");
        data.setIsDefault("N");
        data.setCreateBy(SecurityUtils.getUsername());
        dictDataService.insertDictData(data);
        SysDictData saved = findByDictValue(dictValue);
        return toVo(saved);
    }

    @Override
    public CrmTagVo update(CrmTagUpdateRequest request)
    {
        if (request == null || StringUtils.isBlank(request.getDictValue()))
        {
            throw new ServiceException("dictValue 不能为空", HttpStatus.BAD_REQUEST);
        }
        SysDictData existing = findByDictValue(request.getDictValue().trim());
        if (existing == null)
        {
            throw new ServiceException("标签不存在", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getDictLabel()))
        {
            throw new ServiceException("dictLabel 不能为空", HttpStatus.BAD_REQUEST);
        }
        String category = CrmTagValidationSupport.requireCategory(request.getRemark());
        existing.setDictLabel(request.getDictLabel().trim());
        existing.setDictLabelEn(StringUtils.trimToEmpty(request.getDictLabelEn()));
        existing.setDictLabelTh(StringUtils.trimToEmpty(request.getDictLabelTh()));
        if (!category.equals(existing.getRemark()))
        {
            existing.setRemark(category);
            existing.setDictSort((long) CrmTagValidationSupport.nextDictSort(category, countByCategory(category)));
        }
        else
        {
            existing.setRemark(category);
        }
        existing.setStatus(normalizeStatus(request.getStatus()));
        existing.setUpdateBy(SecurityUtils.getUsername());
        dictDataService.updateDictData(existing);
        return toVo(findByDictValue(existing.getDictValue()));
    }

    @Override
    public void remove(String dictValue)
    {
        if (StringUtils.isBlank(dictValue))
        {
            throw new ServiceException("dictValue 不能为空", HttpStatus.BAD_REQUEST);
        }
        SysDictData existing = findByDictValue(dictValue.trim());
        if (existing == null)
        {
            return;
        }
        CrmTagUsageVo usage = governanceMapper.selectUsageOne(dictValue.trim());
        if (usage != null && (usage.getContactCount() > 0 || usage.getLeadCount() > 0))
        {
            throw new ServiceException("标签仍有使用量，无法删除", HttpStatus.BAD_REQUEST);
        }
        dictDataService.deleteDictDataByIds(new Long[] { existing.getDictCode() });
    }

    @Override
    public List<CrmTagUsageVo> usage(String dictValue)
    {
        return governanceMapper.selectUsage(StringUtils.trimToNull(dictValue));
    }

    @Override
    public List<CrmTagMemberVo> listMembers(String dictValue, String entity, int pageNum, int pageSize)
    {
        String dv = requireMemberDictValue(dictValue);
        String ent = requireMemberEntity(entity);
        int safePageNum = Math.max(pageNum, 1);
        int safePageSize = Math.min(Math.max(pageSize, 1), 200);
        int offset = (safePageNum - 1) * safePageSize;
        if ("lead".equals(ent))
        {
            return governanceMapper.selectLeadMembers(dv, safePageSize, offset);
        }
        return governanceMapper.selectContactMembers(dv, safePageSize, offset);
    }

    @Override
    public long countMembers(String dictValue, String entity)
    {
        String dv = requireMemberDictValue(dictValue);
        String ent = requireMemberEntity(entity);
        if ("lead".equals(ent))
        {
            return governanceMapper.countLeadMembers(dv);
        }
        return governanceMapper.countContactMembers(dv);
    }

    private static String requireMemberDictValue(String dictValue)
    {
        if (StringUtils.isBlank(dictValue))
        {
            throw new ServiceException("dictValue 不能为空", HttpStatus.BAD_REQUEST);
        }
        return dictValue.trim();
    }

    private static String requireMemberEntity(String entity)
    {
        if (StringUtils.isBlank(entity))
        {
            throw new ServiceException("entity 须为 contact 或 lead", HttpStatus.BAD_REQUEST);
        }
        String ent = entity.trim().toLowerCase();
        if (!"contact".equals(ent) && !"lead".equals(ent))
        {
            throw new ServiceException("entity 须为 contact 或 lead", HttpStatus.BAD_REQUEST);
        }
        return ent;
    }

    @Override
    public List<CrmTagOrphanVo> orphans()
    {
        return governanceMapper.selectOrphans();
    }

    @Override
    public CrmTagMergeResultVo merge(CrmTagMergeRequest request)
    {
        if (request == null || StringUtils.isBlank(request.getToDictValue()))
        {
            throw new ServiceException("toDictValue 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getFromId()) && StringUtils.isBlank(request.getFromText()))
        {
            throw new ServiceException("fromId 与 fromText 至少填一个", HttpStatus.BAD_REQUEST);
        }
        SysDictData target = findByDictValue(request.getToDictValue().trim());
        if (target == null || !UserConstants.DICT_NORMAL.equals(target.getStatus()))
        {
            throw new ServiceException("目标标签不存在或已停用", HttpStatus.BAD_REQUEST);
        }
        String fromId = StringUtils.trimToNull(request.getFromId());
        String fromText = StringUtils.trimToNull(request.getFromText());
        String toDictValue = target.getDictValue();
        String toLabel = target.getDictLabel();

        int updatedContacts = 0;
        int updatedLeads = 0;
        while (true)
        {
            List<String> contactIds = governanceMapper.selectContactIdsForMerge(fromId, fromText, MERGE_BATCH);
            if (contactIds.isEmpty())
            {
                break;
            }
            updatedContacts += governanceMapper.mergeContactTagsBatch(contactIds, fromId, fromText, toDictValue, toLabel);
            if (contactIds.size() < MERGE_BATCH)
            {
                break;
            }
        }
        while (true)
        {
            List<String> leadIds = governanceMapper.selectLeadIdsForMerge(fromId, fromText, MERGE_BATCH);
            if (leadIds.isEmpty())
            {
                break;
            }
            updatedLeads += governanceMapper.mergeLeadTagsBatch(leadIds, fromId, fromText, toDictValue, toLabel);
            if (leadIds.size() < MERGE_BATCH)
            {
                break;
            }
        }
        log.info("crm tag merge fromId={} fromText={} to={} contacts={} leads={} by={}",
                fromId, fromText, toDictValue, updatedContacts, updatedLeads, SecurityUtils.getUsername());
        return new CrmTagMergeResultVo(updatedContacts, updatedLeads);
    }

    @Override
    public List<CrmTagMappingVo> listMappings()
    {
        List<CrmTagChannelMapping> rows = governanceMapper.selectAllMappings();
        Map<String, CrmTagMappingVo> byValue = new LinkedHashMap<>();
        for (CrmTagChannelMapping row : rows)
        {
            CrmTagMappingVo vo = byValue.computeIfAbsent(row.getDictValue(), k -> {
                CrmTagMappingVo m = new CrmTagMappingVo();
                m.setDictValue(k);
                return m;
            });
            if (CrmTagValidationSupport.CHANNEL_WECOM.equals(row.getChannel()))
            {
                vo.setWecomTagId(row.getChannelTagId());
                vo.setWecomTagName(row.getChannelTagName());
                vo.setWecomStatus(row.getStatus());
            }
            else if (CrmTagValidationSupport.CHANNEL_WECHAT_MP.equals(row.getChannel()))
            {
                vo.setMpTagId(row.getChannelTagId());
                vo.setMpTagName(row.getChannelTagName());
                vo.setMpStatus(row.getStatus());
            }
        }
        return new ArrayList<>(byValue.values());
    }

    @Override
    public void putMapping(CrmTagMappingPutRequest request)
    {
        if (request == null || StringUtils.isBlank(request.getDictValue())
                || StringUtils.isBlank(request.getChannel()))
        {
            throw new ServiceException("dictValue 与 channel 不能为空", HttpStatus.BAD_REQUEST);
        }
        String channel = request.getChannel().trim();
        if (!CrmTagValidationSupport.CHANNEL_WECOM.equals(channel)
                && !CrmTagValidationSupport.CHANNEL_WECHAT_MP.equals(channel))
        {
            throw new ServiceException("channel 须为 wecom 或 wechat_mp", HttpStatus.BAD_REQUEST);
        }
        if (findByDictValue(request.getDictValue().trim()) == null)
        {
            throw new ServiceException("标签不存在", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getChannelTagId()))
        {
            governanceMapper.deleteMapping(request.getDictValue().trim(), channel);
            return;
        }
        Date now = DateUtils.getNowDate();
        String username = SecurityUtils.getUsername();
        CrmTagChannelMapping row = new CrmTagChannelMapping();
        row.setDictValue(request.getDictValue().trim());
        row.setChannel(channel);
        row.setChannelTagId(request.getChannelTagId().trim());
        row.setChannelTagName(StringUtils.trimToNull(request.getChannelTagName()));
        row.setStatus("0");
        row.setCreateBy(username);
        row.setCreateTime(now);
        row.setUpdateBy(username);
        row.setUpdateTime(now);
        governanceMapper.upsertMapping(row);
    }

    @Override
    public CrmTagDispatchTaskVo dispatch(String tenantId, CrmTagDispatchRequest request)
    {
        if (request == null || request.getSegmentId() == null
                || StringUtils.isBlank(request.getDictValue())
                || StringUtils.isBlank(request.getChannel()))
        {
            throw new ServiceException("segmentId / dictValue / channel 不能为空", HttpStatus.BAD_REQUEST);
        }
        String channel = request.getChannel().trim();
        if (!CrmTagValidationSupport.CHANNEL_WECOM.equals(channel)
                && !CrmTagValidationSupport.CHANNEL_WECHAT_MP.equals(channel))
        {
            throw new ServiceException("channel 须为 wecom 或 wechat_mp", HttpStatus.BAD_REQUEST);
        }
        if (CrmTagValidationSupport.CHANNEL_WECHAT_MP.equals(channel) && StringUtils.isBlank(request.getAppId()))
        {
            throw new ServiceException("公众号下发需要 appId", HttpStatus.BAD_REQUEST);
        }
        if (segmentService.getById(tenantId, request.getSegmentId()) == null)
        {
            throw new ServiceException("分群不存在或不属于当前租户", HttpStatus.BAD_REQUEST);
        }
        CrmTagChannelMapping mapping = governanceMapper.selectMapping(request.getDictValue().trim(), channel);
        if (mapping == null || StringUtils.isBlank(mapping.getChannelTagId()))
        {
            throw new ServiceException("未配置渠道映射", HttpStatus.BAD_REQUEST);
        }
        if (!"0".equals(mapping.getStatus()))
        {
            throw new ServiceException("渠道映射已失效，请重新绑定", HttpStatus.BAD_REQUEST);
        }

        Date now = DateUtils.getNowDate();
        CrmTagDispatchTask task = new CrmTagDispatchTask();
        task.setSegmentId(request.getSegmentId());
        task.setDictValue(request.getDictValue().trim());
        task.setChannel(channel);
        task.setChannelTagId(mapping.getChannelTagId());
        task.setAppId(StringUtils.trimToNull(request.getAppId()));
        task.setStatus("pending");
        task.setTotal(0);
        task.setSucceeded(0);
        task.setFailed(0);
        task.setCreateBy(SecurityUtils.getUsername());
        task.setCreateTime(now);
        task.setUpdateTime(now);
        governanceMapper.insertDispatchTask(task);

        final Long taskId = task.getId();
        final String tenantKey = tenantId;
        AsyncManager.me().execute(new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    TenantDataSourceSupport.bindTenantKey(tenantKey);
                    dispatchWorker.processTask(tenantKey, taskId);
                }
                catch (Exception e)
                {
                    log.error("tag dispatch failed taskId={} tenant={}", taskId, tenantKey, e);
                }
                finally
                {
                    DynamicDataSourceContextHolder.clearDataSourceKey();
                }
            }
        });
        return toDispatchVo(governanceMapper.selectDispatchTaskById(taskId));
    }

    @Override
    public CrmTagDispatchTaskVo getDispatchTask(Long taskId)
    {
        if (taskId == null)
        {
            throw new ServiceException("taskId 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmTagDispatchTask task = governanceMapper.selectDispatchTaskById(taskId);
        if (task == null)
        {
            throw new ServiceException("任务不存在", HttpStatus.BAD_REQUEST);
        }
        return toDispatchVo(task);
    }

    private SysDictData findByDictValue(String dictValue)
    {
        SysDictData query = new SysDictData();
        query.setDictType(CrmTagValidationSupport.DICT_TYPE);
        query.setDictValue(dictValue);
        List<SysDictData> rows = dictDataService.selectDictDataList(query);
        for (SysDictData row : rows)
        {
            if (dictValue.equals(row.getDictValue()))
            {
                return row;
            }
        }
        return null;
    }

    private int countByCategory(String category)
    {
        SysDictData query = new SysDictData();
        query.setDictType(CrmTagValidationSupport.DICT_TYPE);
        int count = 0;
        for (SysDictData row : dictDataService.selectDictDataList(query))
        {
            if (category.equals(row.getRemark()))
            {
                count++;
            }
        }
        return count;
    }

    private String normalizeStatus(String status)
    {
        if (StringUtils.isBlank(status) || UserConstants.DICT_NORMAL.equals(status))
        {
            return UserConstants.DICT_NORMAL;
        }
        if ("1".equals(status))
        {
            return "1";
        }
        throw new ServiceException("status 须为 0 或 1", HttpStatus.BAD_REQUEST);
    }

    private CrmTagVo toVo(SysDictData row)
    {
        if (row == null)
        {
            return null;
        }
        CrmTagVo vo = new CrmTagVo();
        vo.setDictCode(row.getDictCode());
        vo.setDictValue(row.getDictValue());
        vo.setDictLabel(row.getDictLabel());
        vo.setDictLabelEn(row.getDictLabelEn());
        vo.setDictLabelTh(row.getDictLabelTh());
        vo.setRemark(row.getRemark());
        vo.setDictSort(row.getDictSort());
        vo.setStatus(row.getStatus());
        return vo;
    }

    private CrmTagDispatchTaskVo toDispatchVo(CrmTagDispatchTask task)
    {
        CrmTagDispatchTaskVo vo = new CrmTagDispatchTaskVo();
        vo.setTaskId(String.valueOf(task.getId()));
        vo.setStatus(task.getStatus());
        vo.setTotal(task.getTotal() == null ? 0 : task.getTotal());
        vo.setSucceeded(task.getSucceeded() == null ? 0 : task.getSucceeded());
        vo.setFailed(task.getFailed() == null ? 0 : task.getFailed());
        vo.setErrorMessage(task.getErrorMessage());
        return vo;
    }
}
