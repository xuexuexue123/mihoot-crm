package com.ruoyi.web.config;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.web.utils.IpLocationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

@Configuration
public class IpLocationConfig {

    private static final Logger log = LoggerFactory.getLogger(IpLocationConfig.class);

    @PostConstruct
    public void initGeoIpDatabase() throws IOException {
        File dbFile = resolveDatabaseFile();
        if (!dbFile.isFile()) {
            throw new IllegalStateException(
                    "GeoLite2 database not found at " + dbFile.getAbsolutePath()
                            + ". Place GeoLite2-City.mmdb under ${ruoyi.profile}/ip/.");
        }
        IpLocationUtil.init(dbFile);
        log.info("Loaded GeoLite2 database from {}", dbFile.getAbsolutePath());
    }

    private File resolveDatabaseFile() {
        File configured = new File(RuoYiConfig.getProfile(), "ip/GeoLite2-City.mmdb");
        if (configured.isFile()) {
            return configured;
        }

        File[] devCandidates = {
                new File("src/main/resources/ip/GeoLite2-City.mmdb"),
                new File("ruoyi-admin/src/main/resources/ip/GeoLite2-City.mmdb")
        };
        for (File candidate : devCandidates) {
            if (candidate.isFile()) {
                return candidate;
            }
        }

        return configured;
    }
}
