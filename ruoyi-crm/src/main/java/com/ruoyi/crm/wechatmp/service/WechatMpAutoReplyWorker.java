package com.ruoyi.crm.wechatmp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruoyi.crm.wechatmp.domain.CrmWechatMpAutoReply;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpAutoReplyMapper;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;

@Slf4j
@Component
public class WechatMpAutoReplyWorker
{
    @Autowired
    private CrmWechatMpAutoReplyMapper autoReplyMapper;

    /**
     * 同步匹配自动回复规则，供回调线程内构造被动回复 XML 使用。
     *
     * @return 命中的规则；未命中返回 null
     */
    public CrmWechatMpAutoReply matchRule(String appId, WxMpXmlMessage msg)
    {
        if (appId == null || msg == null)
        {
            return null;
        }
        if ("event".equals(msg.getMsgType()) && "subscribe".equals(msg.getEvent()))
        {
            List<CrmWechatMpAutoReply> rules = autoReplyMapper.selectEnabledByType(appId, "subscribe");
            return rules.isEmpty() ? null : rules.get(0);
        }
        if ("text".equals(msg.getMsgType()) && msg.getContent() != null)
        {
            List<CrmWechatMpAutoReply> rules = autoReplyMapper.selectEnabledByType(appId, "keyword");
            for (CrmWechatMpAutoReply rule : rules)
            {
                if (rule.getMatchValue() != null && msg.getContent().contains(rule.getMatchValue()))
                {
                    return rule;
                }
            }
        }
        if ("event".equals(msg.getMsgType()) && "CLICK".equals(msg.getEvent()))
        {
            List<CrmWechatMpAutoReply> rules = autoReplyMapper.selectEnabledByType(appId, "menu_click");
            for (CrmWechatMpAutoReply rule : rules)
            {
                if (rule.getMatchValue() != null && rule.getMatchValue().equals(msg.getEventKey()))
                {
                    return rule;
                }
            }
            if (!rules.isEmpty())
            {
                log.info("WeChat MP menu_click no rule matched: appId={}, eventKey={}, candidateMatchValues={}",
                        appId, msg.getEventKey(),
                        rules.stream().map(r -> r.getMatchValue()).collect(Collectors.toList()));
            }
        }
        return null;
    }
}
