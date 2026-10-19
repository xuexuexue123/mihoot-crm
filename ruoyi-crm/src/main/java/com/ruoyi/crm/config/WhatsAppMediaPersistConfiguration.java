package com.ruoyi.crm.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.ruoyi.crm.whatsapp.WhatsAppMediaPersistProperties;

@Configuration
@EnableConfigurationProperties(WhatsAppMediaPersistProperties.class)
public class WhatsAppMediaPersistConfiguration
{
}
