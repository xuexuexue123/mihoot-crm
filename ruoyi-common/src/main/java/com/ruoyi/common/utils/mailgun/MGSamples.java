package com.ruoyi.common.utils.mailgun;

import java.io.File;

import com.mashape.unirest.http.HttpResponse; // unirest v1.4.9
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
public class MGSamples {
    public static JsonNode sendSimpleMessage(
            String toAddress,
            String subject,
            String htmlBody) throws UnirestException {
        String apiKey = System.getenv("API_KEY");
        if (apiKey == null) {
            apiKey = "2afsdfsdf9a2374-b39sdf1a";
        }


        HttpResponse<JsonNode> request = Unirest.post("https://api.mailgun.net/v3/www.mihoot.com/messages")
                .basicAuth("api", apiKey)
                .queryString("from", "Mailgun Sandbox <postmaster@www.mihoot.com>")
                .queryString("to", toAddress)
                .queryString("subject", subject)
                .queryString("html", htmlBody)
                .asJson();

        return request.getBody();
    }
}