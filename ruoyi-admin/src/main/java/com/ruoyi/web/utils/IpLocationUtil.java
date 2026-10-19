package com.ruoyi.web.utils;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Subdivision;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class IpLocationUtil {
    private static DatabaseReader reader;

    public static void init(File dbFile) throws IOException {
        reader = new DatabaseReader.Builder(dbFile).build();
    }

//    public static Searcher createSearcher() throws IOException {

//        ClassPathResource resource = new ClassPathResource("ip/GeoLite2-City.mmdb");


//        try (InputStream inputStream = resource.getInputStream()) {
//            byte[] dbBinStr = readAllBytes(inputStream);
//            return Searcher.newWithBuffer(dbBinStr);
//        }
//    }


    public static Map<String, String> getLocation(String ip) {
        ip = normalizeIp(ip);
        Map<String, String> location = new HashMap<>();
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            CityResponse response = reader.city(ipAddress);

            Country country = response.getCountry();
            Subdivision subdivision = response.getMostSpecificSubdivision();
            City city = response.getCity();

            location.put("country", country.getName());         // e.g. "China"
            location.put("region", subdivision.getName());      // e.g. "Beijing"
            location.put("city", city.getName());               // e.g. "Beijing"

        } catch (Exception e) {
            e.printStackTrace();
            location.put("error", "IP lookup failed");
        }

        return location;
    }

    public static String normalizeIp(String ip) {
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            return "8.8.8.8";
//            return "127.0.0.1";
        }
        return ip;
    }
}
