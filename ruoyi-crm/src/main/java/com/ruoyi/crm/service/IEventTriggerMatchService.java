package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.dto.EventTriggerMatchRequest;
import com.ruoyi.crm.domain.vo.EventTriggerMatchVO;

/**
 * CRM_EVENT_TRIGGER 节点匹配（BFF 回调）
 */
public interface IEventTriggerMatchService
{
    List<EventTriggerMatchVO> match(EventTriggerMatchRequest request);
}
