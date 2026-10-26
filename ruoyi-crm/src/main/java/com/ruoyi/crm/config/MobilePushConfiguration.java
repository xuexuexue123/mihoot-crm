package com.ruoyi.crm.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.ruoyi.crm.mobile.push.MobilePushProperties;

@Configuration
@EnableConfigurationProperties(MobilePushProperties.class)
public class MobilePushConfiguration
{
}
