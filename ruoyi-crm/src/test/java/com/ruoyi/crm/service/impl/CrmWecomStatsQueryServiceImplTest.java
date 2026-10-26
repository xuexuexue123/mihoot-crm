package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.crm.domain.dto.wecom.CrmWecomStatsMemberRow;
import com.ruoyi.crm.domain.dto.wecom.WecomBehaviorDataRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomBehaviorDataResponse;
import com.ruoyi.crm.mapper.CrmWecomBehaviorDailyMapper;
import com.ruoyi.crm.mapper.CrmWecomGroupchatDailyMapper;
import com.ruoyi.crm.mapper.CrmWecomStatsMemberNameRow;
import com.ruoyi.crm.service.ICrmWecomStatsInitService;
import com.ruoyi.system.mapper.SysUserMapper;

@ExtendWith(MockitoExtension.class)
class CrmWecomStatsQueryServiceImplTest
{
    @Mock
    private CrmWecomBehaviorDailyMapper behaviorDailyMapper;

    @Mock
    private CrmWecomGroupchatDailyMapper groupchatDailyMapper;

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private ICrmWecomStatsInitService statsInitService;

    @InjectMocks
    private CrmWecomStatsQueryServiceImpl queryService;

    @Test
    void listHistoricalMembers_marksDeletedMemberInactive()
    {
        CrmWecomStatsMemberNameRow deleted = new CrmWecomStatsMemberNameRow();
        deleted.setUserid("ninestars");
        deleted.setName("九星(已离职)");

        CrmWecomStatsMemberNameRow active = new CrmWecomStatsMemberNameRow();
        active.setUserid("zhangsan");
        active.setName("张三");

        whenBehaviorAndGroupchatMembers(deleted, active);

        SysUser activeUser = new SysUser();
        activeUser.setSnUserid("zhangsan");
        when(sysUserMapper.selectUsersWithSnUserid()).thenReturn(Collections.singletonList(activeUser));

        List<CrmWecomStatsMemberRow> members = queryService.listHistoricalMembers();

        CrmWecomStatsMemberRow deletedRow = findByUserid(members, "ninestars");
        CrmWecomStatsMemberRow activeRow = findByUserid(members, "zhangsan");

        assertFalse(deletedRow.getActive());
        assertEquals("九星(已离职)", deletedRow.getName());
        assertTrue(activeRow.getActive());
    }

    @Test
    void queryBehaviorFromSnapshot_triggersEnsureInitialized()
    {
        WecomBehaviorDataRequest request = new WecomBehaviorDataRequest();
        request.setUserid(Collections.singletonList("zhangsan"));
        request.setStartTime(1716681600L);
        request.setEndTime(1716768000L);

        when(behaviorDailyMapper.aggregateBehaviorByStatTime(
                request.getStartTime(), request.getEndTime(), request.getUserid()))
                .thenReturn(Collections.emptyList());

        WecomBehaviorDataResponse response = queryService.queryBehaviorFromSnapshot(request);

        verify(statsInitService).ensureInitialized();
        assertEquals(0, response.getErrcode());
        assertTrue(response.getBehaviorData().isEmpty());
    }

    private void whenBehaviorAndGroupchatMembers(CrmWecomStatsMemberNameRow deleted, CrmWecomStatsMemberNameRow active)
    {
        when(behaviorDailyMapper.selectDistinctMemberNames())
                .thenReturn(Arrays.asList(deleted, active));
        when(groupchatDailyMapper.selectDistinctOwnerNames())
                .thenReturn(Collections.emptyList());
    }

    private static CrmWecomStatsMemberRow findByUserid(List<CrmWecomStatsMemberRow> members, String userid)
    {
        return members.stream()
                .filter(row -> userid.equals(row.getUserid()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("userid not found: " + userid));
    }
}
