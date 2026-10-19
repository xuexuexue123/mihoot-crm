package com.ruoyi.web.controller.crm;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.HmacUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.domain.CrmSurveys;
import com.ruoyi.crm.domain.vo.OutreachEventRegistrationVo;
import com.ruoyi.crm.domain.vo.OutreachEventTicketVo;
import com.ruoyi.crm.domain.vo.OutreachRegistrationSubmitResultVo;
import com.ruoyi.crm.domain.vo.OutreachSurveyVo;
import com.ruoyi.crm.domain.vo.EventRegistrationSettingsVo;
import com.ruoyi.crm.domain.vo.EventSessionVo;
import com.ruoyi.crm.service.IEventCheckInService;
import com.ruoyi.crm.service.IEventRegistrationService;
import com.ruoyi.crm.service.IEventSessionService;
import com.ruoyi.crm.enums.EventType;
import com.ruoyi.crm.event.EventDispatcher;
import com.ruoyi.crm.service.ICrmCollectService;
import com.ruoyi.crm.service.ICrmEventService;
import com.ruoyi.crm.service.ICrmRespondentsService;
import com.ruoyi.crm.service.ICrmSurveysService;
import com.ruoyi.crm.service.support.EventRegistrationFormSupport;
import com.ruoyi.crm.service.support.LeadDefaults;
import com.ruoyi.crm.service.support.OutreachLeadValidationSupport;
import com.ruoyi.crm.service.support.OutreachRegistrationLeadAttributionService;
import com.ruoyi.crm.service.support.OutreachRegistrationTokenService;
import com.ruoyi.crm.service.support.OutreachRegistrationTokenService.SurveyVerifiedPayload;
import com.ruoyi.crm.service.support.OutreachRegistrationTokenService.VerifiedPayload;
import com.ruoyi.crm.service.support.SurveyOutreachRespondentService;
import com.ruoyi.crm.service.support.OutreachTicketTokenService;
import com.ruoyi.crm.service.support.OutreachTicketTokenService.VerifiedTicketPayload;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.tenant.domain.enums.TenantStatus;
import com.ruoyi.web.utils.IpLocationUtil;
import com.ruoyi.common.utils.sign.RsaUtils;
import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.crm.domain.CrmOutreachRespondent;
import com.ruoyi.crm.domain.CrmOutreachdata;
import com.ruoyi.crm.domain.CrmOutreachlead;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.service.IMasterTenantService;
import org.apache.ibatis.jdbc.SqlRunner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import ua_parser.Parser;
import ua_parser.Client;

@RestController
public class CrmOutreachleadController extends BaseController {

    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Autowired
    private ICrmSurveysService crmSurveysService;

    @Autowired
    private ICrmRespondentsService crmRespondentsService;

    @Autowired
    private ICrmEventService crmEventService;

    @Autowired
    private ICrmCollectService crmCollectService;

    @Autowired
    private EventDispatcher eventDispatcher;

    @Autowired
    private OutreachRegistrationTokenService outreachRegistrationTokenService;

    @Autowired
    private OutreachRegistrationLeadAttributionService outreachRegistrationLeadAttributionService;

    @Autowired
    private IEventSessionService eventSessionService;

    @Autowired
    private IEventRegistrationService eventRegistrationService;

    @Autowired
    private IEventCheckInService eventCheckInService;

    @Autowired
    private OutreachTicketTokenService outreachTicketTokenService;

    @Autowired
    private SurveyOutreachRespondentService surveyOutreachRespondentService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/track")
    public AjaxResult trackEmailOpen(HttpServletRequest request, HttpServletResponse response,
                                     @RequestParam("e") String email,
                                     @RequestParam("c") String uniqueId,
                                     @RequestParam("t") String tenantId,
                                     @RequestParam("k") String token,
                                     @RequestParam(value = "test", required = false) String test) {
        try {
            MasterTenant masterTenant = masterTenantService.selectTenantByTenantId(tenantId);
            String expectedToken = HmacUtil.generateTrackingToken(email, uniqueId, tenantId);

            if (!expectedToken.equals(token)) {
                return AjaxResult.error("Wrong request"); // 拒绝非法请求
            }

            if (masterTenant == null) {
                return AjaxResult.error("租户信息未找到！");
            }

            if ("1".equals(test)) {
                return AjaxResult.success("test track ignored");
            }
            // 解析 User-Agent
            String userAgent = request.getHeader("User-Agent");
            Parser uaParser = new Parser();
            Client client = uaParser.parse(userAgent);
            String device = client.device.family;
            String os = client.os.family;
            String browser = client.userAgent.family;

            // 获取 IP + 地理信息
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null) ip = request.getRemoteAddr();
            ip = IpLocationUtil.normalizeIp(ip);

            // 加载 ip2region.db 数据库文件
//            String dbPath = "ip/ip2region.db";  // 数据库文件路径
//            Searcher searcher = Searcher.newWithBuffer(Searcher.loadContentFromFile(dbPath));
//            Searcher searcher = IpLocationUtil.createSearcher();

            // 查询 IP 地址信息
//            String region = searcher.search(ip);
            Map<String, String> location = IpLocationUtil.getLocation(ip);


            // 方式 A：ip2region 或 MaxMind 查询地理信息（略）

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("email", email);
            metadata.put("campaignId", uniqueId);
            metadata.put("tenantId", tenantId);
            metadata.put("ip", ip);
            metadata.put("userAgent", userAgent);
            metadata.put("openedAt", Instant.now().toString());

            metadata.put("device", device);

            metadata.put("os", os);

            metadata.put("browser", browser);

            metadata.put("location", location);


            try (Connection conn = getConnection(masterTenant.getUrl(), masterTenant.getUsername(), masterTenant.getPassword())) {
                boolean updated = updateBulkEmailTracking(conn, email, uniqueId, metadata);
                if (updated) {
                    return AjaxResult.success("邮件追踪更新成功");
                } else {
                    return AjaxResult.error("未找到匹配的 uniqueId 或 email，未执行更新");
                }
            }
        } catch (Exception ex) {
            logger.error("邮件追踪更新失败: ", ex);
            return AjaxResult.error("邮件追踪更新失败");
        }
    }

    @GetMapping("/outreachsurvey")
    public AjaxResult getSurveyJson(
            @RequestParam(value = "token", required = false) String token,
            @RequestParam(value = "t", required = false) String tenantId,
            @RequestParam(value = "c", required = false) String uniqueId,
            @RequestParam(value = "k", required = false) String legacyToken)
    {
        if (StringUtils.isNotBlank(tenantId) || StringUtils.isNotBlank(uniqueId) || StringUtils.isNotBlank(legacyToken))
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "Missing required parameters");
        }
        if (StringUtils.isBlank(token))
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "Missing required parameters");
        }

        try
        {
            SurveyVerifiedPayload payload = outreachRegistrationTokenService.verifySurvey(token);
            MasterTenant tenant = masterTenantService.selectTenantByTenantId(payload.getTenantId());
            if (tenant == null)
            {
                return AjaxResult.error(HttpStatus.NOT_FOUND, "Failed to found tenant");
            }

            ensureTenantDataSourceReady(tenant);
            DynamicDataSourceContextHolder.setDataSourceKey(tenant.getTenant());

            CrmSurveys survey = crmSurveysService.selectCrmSurveysById(payload.getSurveyId());
            if (survey == null)
            {
                return AjaxResult.error(HttpStatus.NOT_FOUND, "Survey not found");
            }

            OutreachSurveyVo vo = new OutreachSurveyVo();
            vo.setSurveyId(survey.getId());
            vo.setTitle(survey.getTitle());
            vo.setDescription(survey.getDescription());
            vo.setSurveyJson(survey.getSurveyJson());
            vo.setContactId(payload.getContactId());
            vo.setChannel(payload.getChannel());
            return AjaxResult.success(vo);
        }
        catch (ServiceException ex)
        {
            Integer code = ex.getCode();
            if (code != null)
            {
                return AjaxResult.error(code, ex.getMessage());
            }
            return AjaxResult.error(ex.getMessage());
        }
        catch (Exception ex)
        {
            logger.error("获取问卷失败: ", ex);
            return AjaxResult.error("Failed to load survey");
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    @GetMapping("/outreacheventregistration")
    public AjaxResult getEventRegistrationForm(@RequestParam(value = "token", required = false) String token)
    {
        if (StringUtils.isBlank(token))
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "Missing required parameters");
        }

        try
        {
            VerifiedPayload payload = outreachRegistrationTokenService.verify(token);
            MasterTenant tenant = masterTenantService.selectTenantByTenantId(payload.getTenantId());
            if (tenant == null)
            {
                return AjaxResult.error(HttpStatus.NOT_FOUND, "Failed to found tenant");
            }

            ensureTenantDataSourceReady(tenant);
            DynamicDataSourceContextHolder.setDataSourceKey(tenant.getTenant());

            CrmEvent event = crmEventService.selectCrmEventByEventId(payload.getEventId());
            if (event == null)
            {
                return AjaxResult.error(HttpStatus.NOT_FOUND, "Event not found");
            }

            String formJson;
            if (StringUtils.isNotBlank(event.getRegistrationFormJson()))
            {
                if (!EventRegistrationFormSupport.isValidSurveyJson(event.getRegistrationFormJson()))
                {
                    return AjaxResult.error("registration form snapshot is corrupted");
                }
                formJson = event.getRegistrationFormJson();
            }
            else
            {
                formJson = EventRegistrationFormSupport.DEFAULT_REGISTRATION_FORM_JSON;
            }

            OutreachEventRegistrationVo vo = new OutreachEventRegistrationVo();
            vo.setEventId(event.getEventId());
            vo.setEventSubject(event.getSubject());
            vo.setRegistrationFormJson(formJson);

            eventSessionService.ensureDefaultSession(payload.getTenantId(), event.getEventId(), "system");
            EventRegistrationSettingsVo settings = eventSessionService.getRegistrationSettings(
                    payload.getTenantId(), event.getEventId());
            vo.setDefaultSessionId(settings.getDefaultSessionId());

            OutreachEventRegistrationVo.OutreachRegistrationSettingsBrief settingsBrief =
                    new OutreachEventRegistrationVo.OutreachRegistrationSettingsBrief();
            settingsBrief.setRegistrationEnabled(settings.getRegistrationEnabled());
            settingsBrief.setRegistrationDeadline(settings.getRegistrationDeadline());
            settingsBrief.setWaitlistEnabled(settings.getWaitlistEnabled());
            vo.setRegistrationSettings(settingsBrief);

            java.util.List<EventSessionVo> sessions = eventSessionService.listSessions(
                    payload.getTenantId(), event.getEventId(), "active");
            java.util.List<OutreachEventRegistrationVo.OutreachSessionBrief> sessionBriefs = new java.util.ArrayList<>();
            for (EventSessionVo s : sessions)
            {
                OutreachEventRegistrationVo.OutreachSessionBrief brief =
                        new OutreachEventRegistrationVo.OutreachSessionBrief();
                brief.setSessionId(s.getSessionId());
                brief.setName(s.getName());
                brief.setStartTime(s.getStartTime());
                brief.setEndTime(s.getEndTime());
                brief.setLocation(s.getLocation());
                brief.setCapacity(s.getCapacity());
                brief.setConfirmedCount(s.getConfirmedCount());
                brief.setFull(s.isFull());
                brief.setRegistrationDeadline(s.getRegistrationDeadline());
                sessionBriefs.add(brief);
            }
            vo.setSessions(sessionBriefs);
            return AjaxResult.success(vo);
        }
        catch (ServiceException ex)
        {
            Integer code = ex.getCode();
            if (code != null)
            {
                return AjaxResult.error(code, ex.getMessage());
            }
            return AjaxResult.error(ex.getMessage());
        }
        catch (Exception ex)
        {
            logger.error("获取活动报名表单失败: ", ex);
            return AjaxResult.error("Failed to load registration form");
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    @PostMapping("/outreachrespondent")
    public AjaxResult addRespondent(@RequestBody CrmOutreachRespondent crmOutreachRespondent) {
        if (StringUtils.isBlank(crmOutreachRespondent.getSurveyToken()))
        {
            return AjaxResult.error(HttpStatus.FORBIDDEN, "Wrong request");
        }

        try {
            SurveyVerifiedPayload payload = outreachRegistrationTokenService.verifySurvey(
                    crmOutreachRespondent.getSurveyToken());

            MasterTenant tenant = masterTenantService.selectTenantByTenantId(payload.getTenantId());
            if (tenant == null) {
                return AjaxResult.error(HttpStatus.NOT_FOUND, "Failed to found tenant");
            }

            ensureTenantDataSourceReady(tenant);
            DynamicDataSourceContextHolder.setDataSourceKey(tenant.getTenant());

            int result = surveyOutreachRespondentService.submit(crmOutreachRespondent);
            return AjaxResult.success(result);
        } catch (ServiceException ex) {
            Integer code = ex.getCode();
            if (code != null) {
                return AjaxResult.error(code, ex.getMessage());
            }
            return AjaxResult.error(ex.getMessage());
        } catch (Exception ex) {
            logger.error("提交问卷答卷失败: ", ex);
            return AjaxResult.error("Failed to submit survey response");
        } finally {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    @PostMapping("/outreachdata")
    public AjaxResult addData(@RequestBody CrmOutreachdata crmOutreachdata) {
        int result = 0;
        try {
            MasterTenant tenant = masterTenantService.selectTenantByTenantId(crmOutreachdata.getTenantId());
            if (tenant == null) {
                return AjaxResult.error("租户信息未找到！");
            }

            crmOutreachdata.setUpdateTime(DateUtils.getNowDate());
            ensureTenantDataSourceReady(tenant);

            DynamicDataSourceContextHolder.setDataSourceKey(tenant.getTenant());

            result = crmCollectService.updateCrmCollectByValidationCode(crmOutreachdata);

        } catch (Exception ex) {
            logger.error("创建租户时发生错误: ", ex);
            return AjaxResult.error("失败，创建租户时发生错误");
        }
        return AjaxResult.success(result);
    }

    @PostMapping("/outreachlead")
    public AjaxResult add(@RequestBody CrmOutreachlead crmOutreachlead) {
        if (StringUtils.isBlank(crmOutreachlead.getRegistrationToken()))
        {
            return AjaxResult.error(HttpStatus.FORBIDDEN, "Wrong request");
        }

        try {
            VerifiedPayload payload = outreachRegistrationTokenService.verify(crmOutreachlead.getRegistrationToken());

            if (StringUtils.isNotBlank(crmOutreachlead.getTenantId())
                    && !payload.getTenantId().equals(crmOutreachlead.getTenantId()))
            {
                return AjaxResult.error(HttpStatus.FORBIDDEN, "Wrong request");
            }
            if (StringUtils.isNotBlank(crmOutreachlead.getSid())
                    && !payload.getEventId().equals(crmOutreachlead.getSid()))
            {
                return AjaxResult.error(HttpStatus.FORBIDDEN, "Wrong request");
            }

            MasterTenant tenant = masterTenantService.selectTenantByTenantId(payload.getTenantId());
            if (tenant == null) {
                return AjaxResult.error(HttpStatus.NOT_FOUND, "Failed to found tenant!");
            }

            ensureTenantDataSourceReady(tenant);

            DynamicDataSourceContextHolder.setDataSourceKey(tenant.getTenant());

            CrmEvent event = crmEventService.selectCrmEventByEventId(payload.getEventId());
            if (event == null)
            {
                return AjaxResult.error(HttpStatus.NOT_FOUND, "Event not found");
            }

            OutreachLeadValidationSupport.validateEventAcceptsRegistration(event);
            OutreachLeadValidationSupport.validateOutreachLeadSubmission(crmOutreachlead, event);

            crmOutreachlead.setSid(payload.getEventId());
            crmOutreachlead.setLeadSource("Marketing Event");
            crmOutreachlead.setStage(LeadDefaults.DEFAULT_STAGE);

            OutreachRegistrationSubmitResultVo submitResult = eventRegistrationService.registerFromH5(
                    payload.getTenantId(), crmOutreachlead, event, crmOutreachlead.getSessionId(),
                    payload.getOpenId(), payload.getAppId());
            return AjaxResult.success(submitResult);

        } catch (ServiceException ex) {
            Integer code = ex.getCode();
            if (code != null) {
                return AjaxResult.error(code, ex.getMessage());
            }
            return AjaxResult.error(ex.getMessage());
        } catch (Exception ex) {
            logger.error("创建租户时发生错误: ", ex);
            return AjaxResult.error("失败，创建租户时发生错误");
        } finally {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    @GetMapping("/outreacheventticket")
    public AjaxResult getEventTicket(@RequestParam(value = "ticket", required = false) String ticket)
    {
        if (StringUtils.isBlank(ticket))
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "Missing required parameters");
        }
        try
        {
            VerifiedTicketPayload payload = outreachTicketTokenService.verify(ticket, null);
            MasterTenant tenant = masterTenantService.selectTenantByTenantId(payload.getTenantId());
            if (tenant == null)
            {
                return AjaxResult.error(HttpStatus.NOT_FOUND, "Failed to found tenant");
            }
            ensureTenantDataSourceReady(tenant);
            DynamicDataSourceContextHolder.setDataSourceKey(tenant.getTenant());
            OutreachEventTicketVo vo = eventCheckInService.getTicketInfo(ticket);
            return AjaxResult.success(vo);
        }
        catch (ServiceException ex)
        {
            Integer code = ex.getCode();
            if (code != null)
            {
                return AjaxResult.error(code, ex.getMessage());
            }
            return AjaxResult.error(ex.getMessage());
        }
        catch (Exception ex)
        {
            logger.error("获取票券信息失败: ", ex);
            return AjaxResult.error("Failed to load ticket");
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    @PostMapping("/correspreview")
    public AjaxResult getcorresurl(@RequestBody CrmOutreachdata crmOutreachdata) throws Exception {
        AjaxResult ajaxResult = AjaxResult.success();
        //公钥加密、私钥解密
        //获取当前的url
        String url = crmOutreachdata.getUrl();
        String action = crmOutreachdata.getAction();
        String field = crmOutreachdata.getField();
        String value = crmOutreachdata.getValue();
        String type = crmOutreachdata.getType();


        url = url.replace(' ', '+');
        //对当前的url进行解析
        String decryptUrl = "";
        if (url!="") {
            decryptUrl = RsaUtils.decryptByPrivateKey(url);

//            logger.info("decryptUrl = " + decryptUrl);
            //获取解析后的url
            //得到tenant id， 得到corresId
            String[] params = decryptUrl.split("\\|");

            if (params.length>0) {
                String corresId = params[0];
                String tenantId = params[1];
                String vCode = params[2];

                if (corresId!=""&&tenantId!="") {

                    ajaxResult = processingCorres(tenantId, corresId, vCode, action, field, value, type);
                }

            }
        } else {
            ajaxResult = AjaxResult.error("url is null");

        }

        return  ajaxResult;
    }

    private AjaxResult processingCorres(String tenantId, String corresId, String vCode, String action, String field, String value, String type) {
        Map<String, Object> result;
        try {
            MasterTenant masterTenant = masterTenantService.selectTenantByTenantId(tenantId);
//            logger.info("tenant = " + masterTenant.toString());


            Connection conn = getConnection(masterTenant.getUrl(), masterTenant.getUsername(), masterTenant.getPassword());


            // 切换到
            conn.setCatalog(masterTenant.getDatabaseName());

            // 获取当前数据库名称
            logger.info("当前数据库：{}", conn.getCatalog()); // 若未选择数据库，则 getCatalog 返回空
            conn.getCatalog();
            //获取指定的项目BP
            result = getCorresUrl(conn, corresId);
            String status = "已打开";

            String updateSql;

            if (type.equals("opty")) {

                updateSql = "UPDATE crm_optyrelation SET ";

            } else {

                updateSql = "UPDATE crm_accountrelation SET ";
            }

            if (action.equals("preview")) {

                updateSql = updateSql + " open_status='"+ status +"',";
                updateSql = updateSql + " open_count = open_count + 1";

            } else if (action.equals("update")) {

                updateSql = updateSql + " "+ field + "='"+ value +"'";

            }
            updateSql += " WHERE validation_code='" + vCode + "'";

            //更新对应的项目optyrelation，其状态为： 已打开
            updateOptyrelationByVCode(conn, updateSql);

        } catch (NullPointerException nullPointerException) {

            return AjaxResult.error("数据提交错误！请稍后重试！");
        } catch (Exception ex){
            ex.printStackTrace();
            return AjaxResult.error("失败，创建租户时发生错误");
        }

        return AjaxResult.success(result);
    }

    /**
     * 更新指定的optyrelation状态
     *
     * @param conn
     * @param updateSql
     */
    private void updateOptyrelationByVCode(Connection conn, String updateSql) throws SQLException {
        try {

            conn.setAutoCommit(false);
            SqlRunner sqlRunner = new SqlRunner(conn);

            logger.debug(updateSql);

            sqlRunner.run(updateSql);

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            conn.rollback();
            throw new ServiceException("初始化用户数据脚本时出错");
        }catch (Exception ex){
            ex.printStackTrace();
            throw new ServiceException("执行初始用户数据时出错");
        }
    }


    /**
     * 获取指定的项目BP
     * @param conn
     * @param corresId
     * @return
     * @throws SQLException
     */
    private Map<String, Object> getCorresUrl(Connection conn, String corresId) throws SQLException {
        String sql = "SELECT corres_id, category, belongs_case, folder_id, case_id, task_id, " +
                "file_title, file_name, file_type, file_size, order_num, status, del_flag, " +
                "create_by, create_time, update_by, update_time, user_id, dept_id " +
                "FROM sys_re_corres WHERE corres_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, corresId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> result = new HashMap<>();
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        result.put(metaData.getColumnName(i), rs.getObject(i));
                    }
                    return result;
                }
            }
        }
        return null; // 如果没有查到数据，返回 null
    }

    /**
     * 获取数据库连接
     */
    private Connection getConnection(String url, String username, String password) throws SQLException {

        return DriverManager.getConnection(url, username, password);
    }

    private boolean updateBulkEmailTracking(Connection conn, String email, String uniqueId, Map<String, Object> metadata) throws SQLException, JsonProcessingException {
        String selectSql = "SELECT bulk_id, opens FROM crm_bulkemail WHERE bulk_id = ?";
        String updateBulkEmailSql = "UPDATE crm_bulkemail SET opens = ? WHERE bulk_id = ?";
        String updateContactSql = "UPDATE crm_campaign_contacts SET open = open + 1, metadata = ?, status = ?, opened_at = ? WHERE campaign_id = ? AND email = ?";

        try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            selectStmt.setString(1, uniqueId);
            ResultSet rs = selectStmt.executeQuery();

            if (!rs.next()) {
                return false; // 如果 uniqueId 不存在，不执行更新
            }

            String bulkId = rs.getString("bulk_id");
            int currentOpens = rs.getInt("opens");

            // 递增 opens 计数
            int updatedOpens = currentOpens + 1;

            // 更新 crm_bulkemail 的 opens 字段
            try (PreparedStatement updateBulkStmt = conn.prepareStatement(updateBulkEmailSql)) {
                updateBulkStmt.setInt(1, updatedOpens);
                updateBulkStmt.setString(2, bulkId);
                updateBulkStmt.executeUpdate();
            }

            // 将 metadata 转换为字符串
            String metadataString = metadata != null ? objectMapper.writeValueAsString(metadata) : null;

            // 更新 crm_campaign_contacts 表
            try (PreparedStatement updateContactStmt = conn.prepareStatement(updateContactSql)) {
                updateContactStmt.setString(1, metadataString);
                updateContactStmt.setString(2, "opened");
                updateContactStmt.setString(3, DateUtils.parseDateToStr("yyyy-MM-dd hh:mm:ss", DateUtils.getNowDate()));

                updateContactStmt.setString(4, bulkId); // 假设 campaign_id 与 bulk_id 相同，若不一致请调整
                updateContactStmt.setString(5, email);
                updateContactStmt.executeUpdate();
            }

            logger.info("邮件追踪更新成功: email={}, uniqueId={}, opens={}", email, uniqueId, updatedOpens);
            return true;
        }
    }

    private void ensureTenantDataSourceReady(MasterTenant tenant) {
        if (StringUtils.isBlank(tenant.getTenant())) {
            throw new RuntimeException("Failed to get required information.");
        }

        if (!dynamicRoutingDataSource.existDataSource(tenant.getTenant())) {
            // 检查租户状态
            if (TenantStatus.DISABLE.getCode().equals(tenant.getStatus())) {
                throw new RuntimeException("Application [" + tenant.getTenant() + "] expired");
            }

            if (tenant.getExpirationDate() != null && tenant.getExpirationDate().before(DateUtils.getNowDate())) {
                throw new RuntimeException("Application [" + tenant.getTenant() + "] expired");
            }

            // 注册数据源
            Map<String, Object> map = new HashMap<>();
            map.put("driverClassName", driverClassName);
            map.put("url", tenant.getUrl());
            map.put("username", tenant.getUsername());
            map.put("password", tenant.getPassword());

            dynamicRoutingDataSource.addDataSource(tenant.getTenant(), map);
        }
    }

}
