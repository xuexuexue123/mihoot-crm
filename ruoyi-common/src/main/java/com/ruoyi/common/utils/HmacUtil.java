package com.ruoyi.common.utils;
import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;


public class HmacUtil {

    private static String secretKey = "91511800MABXM1U51C";  // 服务器端密钥

    public static String generateToken(String inputStr) {
        return Hashing.hmacSha256(secretKey.getBytes(StandardCharsets.UTF_8))
                .hashString(inputStr, StandardCharsets.UTF_8)
                .toString();
    }
    public static String generateTrackingToken(String email, String campaignId, String tenantId) {
        return Hashing.hmacSha256(secretKey.getBytes(StandardCharsets.UTF_8))
                .hashString(email + campaignId + tenantId, StandardCharsets.UTF_8)
                .toString();
    }
    public static String generateSurveyToken(String surveyId, String tenantId) {
        return Hashing.hmacSha256(secretKey.getBytes(StandardCharsets.UTF_8))
                .hashString(surveyId + tenantId, StandardCharsets.UTF_8)
                .toString();
    }

    public static String generateEventRegistrationToken(String eventId, String tenantId) {
        return generateSurveyToken(eventId, tenantId);
    }
}
