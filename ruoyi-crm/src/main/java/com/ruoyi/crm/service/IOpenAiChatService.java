package com.ruoyi.crm.service;

import java.io.IOException;

public interface IOpenAiChatService {

    public String chatWithOpenAI(String userPrompt) throws IOException;

    public String chatWithDeepSeek(String userPrompt) throws IOException;

    public String chatWithBailian(String userPrompt) throws IOException;
}
