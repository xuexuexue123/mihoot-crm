package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.tenant.config.TenantWebsiteProperties;

class TenantWebsiteAssetUploadValidatorTest
{
    private TenantWebsiteAssetUploadValidator validator;

    @BeforeEach
    void setUp() throws Exception
    {
        validator = new TenantWebsiteAssetUploadValidator();
        TenantWebsiteProperties props = new TenantWebsiteProperties();
        props.getAsset().setImageMaxBytes(1024);
        props.getAsset().setVideoMaxBytes(2048);
        Field field = TenantWebsiteAssetUploadValidator.class.getDeclaredField("tenantWebsiteProperties");
        field.setAccessible(true);
        field.set(validator, props);
    }

    @Test
    void detectAndValidate_acceptsPng()
    {
        byte[] png = new byte[] {
                (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
                0x00, 0x00, 0x00, 0x00
        };
        MockMultipartFile file = new MockMultipartFile(
                "file", "logo.png", "image/png", png);
        TenantWebsiteAssetUploadValidator.DetectedMedia media = validator.detectAndValidate(file);
        assertEquals("image", media.getMediaType());
    }

    @Test
    void detectAndValidate_rejectsOversizedImage()
    {
        byte[] png = new byte[1200];
        png[0] = (byte) 0x89;
        png[1] = 0x50;
        png[2] = 0x4E;
        png[3] = 0x47;
        MockMultipartFile file = new MockMultipartFile(
                "file", "big.png", "image/png", png);
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.detectAndValidate(file));
        assertEquals(413, ex.getCode());
    }

    @Test
    void detectAndValidate_rejectsMismatchedMagicBytes()
    {
        MockMultipartFile file = new MockMultipartFile(
                "file", "fake.png", "image/png", "not-a-png".getBytes());
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.detectAndValidate(file));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getCode());
    }

    @Test
    void detectAndValidate_acceptsMp4()
    {
        byte[] mp4 = new byte[] {
                0x00, 0x00, 0x00, 0x18, 'f', 't', 'y', 'p',
                'm', 'p', '4', '2', 0x00, 0x00, 0x00, 0x00
        };
        MockMultipartFile file = new MockMultipartFile(
                "file", "clip.mp4", "video/mp4", mp4);
        TenantWebsiteAssetUploadValidator.DetectedMedia media = validator.detectAndValidate(file);
        assertEquals("video", media.getMediaType());
    }
}
