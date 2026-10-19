package com.ruoyi.crm.mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.vo.CampaignRoiVo;

public interface CrmClvAggregateMapper
{
    BigDecimal selectBookedTotalByContactId(@Param("contactId") String contactId);

    BigDecimal selectInflightTotalByContactId(@Param("contactId") String contactId,
            @Param("closedWonStages") List<String> closedWonStages);

    BigDecimal selectRefundTotalByContactId(@Param("contactId") String contactId);

    BigDecimal selectPaidTotalByContactId(@Param("contactId") String contactId);

    BigDecimal selectBookedTotalLast12mByContactId(@Param("contactId") String contactId);

    Integer selectVisitCount12mByContactId(@Param("contactId") String contactId);

    Date selectFirstVisitAtByContactId(@Param("contactId") String contactId);

    Date selectLastVisitAtByContactId(@Param("contactId") String contactId);

    Integer selectTotalVisitCountByContactId(@Param("contactId") String contactId);

    List<Map<String, Object>> selectTreatmentAmountsByContactId(@Param("contactId") String contactId,
            @Param("closedWonStages") List<String> closedWonStages);

    BigDecimal selectBookedTotalByAccountIdDistinct(@Param("accountId") String accountId);

    BigDecimal selectInflightTotalByAccountIdDistinct(@Param("accountId") String accountId,
            @Param("closedWonStages") List<String> closedWonStages);

    BigDecimal selectRefundTotalByAccountId(@Param("accountId") String accountId);

    BigDecimal selectPaidTotalByAccountId(@Param("accountId") String accountId);

    BigDecimal selectPendingReceivableByAccountId(@Param("accountId") String accountId);

    Integer selectMemberCountByAccountId(@Param("accountId") String accountId);

    Map<String, Object> selectCacAllocationForEvent(@Param("eventId") String eventId);

    Map<String, Object> selectCacAllocationForBulkemail(@Param("bulkId") String bulkId);

    List<Map<String, Object>> selectAllEventCacCampaigns();

    List<Map<String, Object>> selectAllBulkemailCacCampaigns();

    List<CampaignRoiVo> selectCampaignRoiList(@Param("campaignType") String campaignType,
            @Param("campaignId") String campaignId,
            @Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo,
            @Param("closedWonStages") List<String> closedWonStages);

    List<Map<String, Object>> selectContactIdsForCampaign(@Param("campaignId") String campaignId,
            @Param("conversionType") String conversionType);

    void resetAllContactAcquisitionCost();

    void updateContactAcquisitionCost(@Param("contactId") String contactId,
            @Param("acquisitionCost") BigDecimal acquisitionCost);

    void updateContactVisitStats(@Param("contactId") String contactId,
            @Param("firstVisitAt") Date firstVisitAt, @Param("lastVisitAt") Date lastVisitAt,
            @Param("visitCount") Integer visitCount);
}
