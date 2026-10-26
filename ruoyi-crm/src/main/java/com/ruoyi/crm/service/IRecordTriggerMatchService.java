package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.dto.RecordTriggerMatchRequest;
import com.ruoyi.crm.domain.vo.RecordTriggerMatchVO;

/**
 * CRM Record Trigger 索引匹配（BFF 专用）
 */
public interface IRecordTriggerMatchService
{
    List<RecordTriggerMatchVO> match(RecordTriggerMatchRequest request);
}
