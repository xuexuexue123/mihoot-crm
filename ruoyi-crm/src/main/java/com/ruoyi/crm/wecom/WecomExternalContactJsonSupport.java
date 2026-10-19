package com.ruoyi.crm.wecom;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.domain.dto.wecom.WecomFollowUserSnapshot;

/**
 * 从 {@code externalcontact/get} 或 batch 导入的 raw_json 解析 follow 维度字段。
 */
public final class WecomExternalContactJsonSupport
{
    private WecomExternalContactJsonSupport()
    {
    }

    /**
     * 从 raw_json 解析与 {@code followUserid} 匹配的跟进人信息。
     * 兼容 {@code follow_user[]}（get 响应）与 {@code follow_info}（batch 响应）。
     */
    public static WecomFollowUserSnapshot resolveFollowUser(String rawJson, String followUserid)
    {
        if (!StringUtils.hasText(rawJson))
        {
            return null;
        }
        try
        {
            JSONObject root = JSONObject.parseObject(rawJson);
            if (root == null)
            {
                return null;
            }

            JSONObject followInfo = root.getJSONObject("follow_info");
            if (followInfo != null && matchesFollowUser(followInfo.getString("userid"), followUserid))
            {
                return toSnapshot(followInfo);
            }

            JSONArray followUsers = root.getJSONArray("follow_user");
            if (followUsers != null && !followUsers.isEmpty())
            {
                if (StringUtils.hasText(followUserid))
                {
                    for (int i = 0; i < followUsers.size(); i++)
                    {
                        JSONObject fu = followUsers.getJSONObject(i);
                        if (fu != null && matchesFollowUser(fu.getString("userid"), followUserid))
                        {
                            return toSnapshot(fu);
                        }
                    }
                }
                JSONObject first = followUsers.getJSONObject(0);
                if (first != null)
                {
                    return toSnapshot(first);
                }
            }
        }
        catch (Exception ignored)
        {
            // best-effort
        }
        return null;
    }

    private static boolean matchesFollowUser(String candidateUserid, String followUserid)
    {
        if (!StringUtils.hasText(followUserid))
        {
            return true;
        }
        return followUserid.trim().equals(candidateUserid != null ? candidateUserid.trim() : "");
    }

    private static WecomFollowUserSnapshot toSnapshot(JSONObject fu)
    {
        if (fu == null)
        {
            return null;
        }
        WecomFollowUserSnapshot snap = new WecomFollowUserSnapshot();
        snap.setUserid(fu.getString("userid"));
        snap.setRemark(fu.getString("remark"));
        snap.setDescription(fu.getString("description"));
        snap.setAddWay(fu.getInteger("add_way"));
        snap.setOperUserid(fu.getString("oper_userid"));
        snap.setCreatetime(fu.getLong("createtime"));
        JSONArray tags = fu.getJSONArray("tags");
        if (tags != null)
        {
            snap.setTags(tags);
        }
        JSONArray remarkMobiles = fu.getJSONArray("remark_mobiles");
        if (remarkMobiles != null)
        {
            snap.setRemarkMobiles(remarkMobiles);
        }
        return snap;
    }

    public static String firstRemarkMobile(WecomFollowUserSnapshot follow)
    {
        if (follow == null || follow.getRemarkMobiles() == null || follow.getRemarkMobiles().isEmpty())
        {
            return null;
        }
        for (int i = 0; i < follow.getRemarkMobiles().size(); i++)
        {
            String mobile = follow.getRemarkMobiles().getString(i);
            if (StringUtils.hasText(mobile))
            {
                return mobile.trim();
            }
        }
        return null;
    }
}
