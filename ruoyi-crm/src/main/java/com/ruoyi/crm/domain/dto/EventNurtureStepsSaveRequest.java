package com.ruoyi.crm.domain.dto;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ruoyi.crm.domain.vo.EventNurtureStepVo;

/**
 * 全量替换培育步骤
 *
 * 兼容两种请求体：
 * 1. { "steps": [ ... ] }
 * 2. [ ... ]（前端联调文档「全量替换 steps 数组」）
 */
@JsonDeserialize(using = EventNurtureStepsSaveRequest.Deserializer.class)
public class EventNurtureStepsSaveRequest
{
    private List<EventNurtureStepVo> steps;

    public List<EventNurtureStepVo> getSteps()
    {
        return steps;
    }

    public void setSteps(List<EventNurtureStepVo> steps)
    {
        this.steps = steps;
    }

    static class Deserializer extends JsonDeserializer<EventNurtureStepsSaveRequest>
    {
        @Override
        public EventNurtureStepsSaveRequest deserialize(JsonParser parser, DeserializationContext ctxt)
                throws IOException
        {
            JsonNode node = parser.getCodec().readTree(parser);
            EventNurtureStepsSaveRequest request = new EventNurtureStepsSaveRequest();
            if (node == null || node.isNull())
            {
                request.setSteps(Collections.emptyList());
                return request;
            }
            if (node.isArray())
            {
                List<EventNurtureStepVo> steps = parseStepList(parser, node);
                request.setSteps(steps);
                return request;
            }
            if (node.isObject())
            {
                JsonNode stepsNode = node.get("steps");
                if (stepsNode == null || stepsNode.isNull())
                {
                    request.setSteps(Collections.emptyList());
                }
                else if (stepsNode.isArray())
                {
                    request.setSteps(parseStepList(parser, stepsNode));
                }
                else
                {
                    request.setSteps(Collections.emptyList());
                }
                return request;
            }
            request.setSteps(Collections.emptyList());
            return request;
        }

        private List<EventNurtureStepVo> parseStepList(JsonParser parser, JsonNode arrayNode)
                throws IOException
        {
            EventNurtureStepVo[] items = parser.getCodec().treeToValue(
                    arrayNode, EventNurtureStepVo[].class);
            if (items == null || items.length == 0)
            {
                return Collections.emptyList();
            }
            return java.util.Arrays.asList(items);
        }
    }
}
