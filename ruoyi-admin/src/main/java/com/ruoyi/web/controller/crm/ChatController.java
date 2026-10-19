package com.ruoyi.web.controller.crm;

import com.ruoyi.crm.service.impl.OpenAiChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/crm/chat")
public class ChatController {

    @Autowired
    private OpenAiChatService chatService;

    @PostMapping("/openai")
    public ResponseEntity<String> chatWithOpenAI(@RequestBody Map<String, String> body) {
//        String prompt = body.get("prompt");
        String prompt = "你是一个销售顾问。请根据以下问卷回答，从BANT角度分析客户情况，并返回每个维度的等级（低/中/高）和一个总分（0~100）：\n" +
                "\n" +
                "问卷内容：\n" +
                "{\n" +
                "    \"answers\": {\n" +
                "      \"budget\": \"50万人民币\",\n" +
                "      \"authority\": \"部门主管\",\n" +
                "      \"need\": \"提高客户转化率\",\n" +
                "      \"timeline\": \"3个月内上线\"\n" +
                "    }\n" +
                " }\n" +
                "\n" +
                "请用以下格式返回：\n" +
                "{\n" +
                "  \"budget\": \"...\",\n" +
                "  \"authority\": \"...\",\n" +
                "  \"need\": \"...\",\n" +
                "  \"timeline\": \"...\",\n" +
                "  \"score\": ...\n" +
                "}";
        try {
            String reply = chatService.chatWithOpenAI(prompt);
            return ResponseEntity.ok(reply);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/deepseek")
    public ResponseEntity<String> chatWithDeepSeek(@RequestBody Map<String, String> body) {
//        String prompt = body.get("prompt");
        String prompt = "你是一个销售顾问。请根据以下问卷回答，从BANT角度分析客户情况，并返回每个维度的等级（低/中/高）和一个总分（0~100）：\n" +
                "\n" +
                "问卷内容：\n" +
                "{\n" +
                "    \"answers\": {\n" +
                "      \"budget\": \"50万人民币\",\n" +
                "      \"authority\": \"部门主管\",\n" +
                "      \"need\": \"提高客户转化率\",\n" +
                "      \"timeline\": \"3个月内上线\"\n" +
                "    }\n" +
                " }\n" +
                "\n" +
                "请用以下格式返回：\n" +
                "{\n" +
                "  \"budget\": \"...\",\n" +
                "  \"authority\": \"...\",\n" +
                "  \"need\": \"...\",\n" +
                "  \"timeline\": \"...\",\n" +
                "  \"score\": ...\n" +
                "}";

        try {
            String reply = chatService.chatWithDeepSeek(prompt);
            return ResponseEntity.ok(reply);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
    @PostMapping("/bailian")
    public ResponseEntity<String> chatWithBailian(@RequestBody Map<String, String> body) {
//        String prompt = body.get("prompt");
        String prompt = "你是一个销售顾问。请根据以下问卷回答，从BANT角度分析客户情况，并返回每个维度的等级（低/中/高）和一个总分（0~100）：\n" +
                "\n" +
                "问卷内容：\n" +
                "{\n" +
                "    \"answers\": {\n" +
                "      \"budget\": \"10万人民币\",\n" +
                "      \"authority\": \"项目经理\",\n" +
                "      \"need\": \"提高客户转化率\",\n" +
                "      \"timeline\": \"3个月内上线\"\n" +
                "    }\n" +
                " }\n" +
                "\n" +
                "请用以下格式返回：\n" +
                "{\n" +
                "  \"budget\": \"...\",\n" +
                "  \"authority\": \"...\",\n" +
                "  \"need\": \"...\",\n" +
                "  \"timeline\": \"...\",\n" +
                "  \"score\": ...\n" +
                "}";

        try {
            String reply = chatService.chatWithBailian(prompt);
            return ResponseEntity.ok(reply);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
