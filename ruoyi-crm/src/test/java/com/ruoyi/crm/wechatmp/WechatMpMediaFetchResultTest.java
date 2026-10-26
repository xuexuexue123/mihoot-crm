package com.ruoyi.crm.wechatmp;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

class WechatMpMediaFetchResultTest
{
    @Test
    void fromHttpResponse_successBinary()
    {
        byte[] imageBytes = new byte[] { (byte) 0xFF, (byte) 0xD8, 0x01 };
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        ResponseEntity<byte[]> resp = new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);

        WechatMpMediaFetchResult result = WechatMpMediaFetchResult.fromHttpResponse(resp);

        assertTrue(result.isSuccess());
        assertEquals(MediaType.IMAGE_JPEG_VALUE, result.getContentType());
        assertArrayEquals(imageBytes, result.getBody());
    }

    @Test
    void fromHttpResponse_jsonErrcode40007()
    {
        byte[] body = "{\"errcode\":40007,\"errmsg\":\"invalid media_id\"}".getBytes();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<byte[]> resp = new ResponseEntity<>(body, headers, HttpStatus.OK);

        WechatMpMediaFetchResult result = WechatMpMediaFetchResult.fromHttpResponse(resp);

        assertFalse(result.isSuccess());
        assertEquals(40007, result.getErrcode());
        assertTrue(result.isInvalidMediaId());
    }

    @Test
    void fromHttpResponse_bodyStartsWithBrace()
    {
        byte[] body = "{\"errcode\":40001,\"errmsg\":\"invalid credential\"}".getBytes();
        ResponseEntity<byte[]> resp = ResponseEntity.ok(body);

        WechatMpMediaFetchResult result = WechatMpMediaFetchResult.fromHttpResponse(resp);

        assertFalse(result.isSuccess());
        assertEquals(40001, result.getErrcode());
        assertTrue(result.isTokenInvalid());
    }
}
