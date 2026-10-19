package com.ruoyi.crm.knowledge;

import java.util.ArrayList;
import java.util.List;

/**
 * 知识库切块（P0 固定策略）：
 * 按段落拆分，目标块长 600–800 字符，overlap 80；过短块合并，超长硬切。
 */
public final class KbTextChunker
{
    /** 目标块长上限 */
    private static final int MAX_CHUNK_CHARS = 800;

    /** 硬切窗口（超长段落） */
    private static final int HARD_SPLIT_CHARS = 700;

    /** 相邻块重叠 */
    private static final int OVERLAP_CHARS = 80;

    private KbTextChunker()
    {
    }

    public static List<String> chunk(String text)
    {
        List<String> chunks = new ArrayList<>();
        if (text == null)
        {
            return chunks;
        }
        String normalized = text.replace("\r\n", "\n").replace('\r', '\n').trim();
        if (normalized.isEmpty())
        {
            return chunks;
        }

        List<String> paragraphs = splitParagraphs(normalized);
        StringBuilder current = new StringBuilder();
        for (String para : paragraphs)
        {
            if (para.length() > MAX_CHUNK_CHARS)
            {
                // 先落当前累积块，再对超长段落硬切
                flush(chunks, current);
                hardSplit(chunks, para);
                continue;
            }
            if (current.length() > 0 && current.length() + para.length() + 1 > MAX_CHUNK_CHARS)
            {
                String closed = flush(chunks, current);
                // overlap：新块以上一块尾部 80 字开头，保持语义连续
                current.append(tail(closed, OVERLAP_CHARS)).append('\n');
            }
            if (current.length() > 0)
            {
                current.append('\n');
            }
            current.append(para);
        }
        flush(chunks, current);
        return chunks;
    }

    private static List<String> splitParagraphs(String text)
    {
        List<String> paragraphs = new ArrayList<>();
        for (String part : text.split("\n{2,}"))
        {
            String trimmed = part.trim();
            if (!trimmed.isEmpty())
            {
                paragraphs.add(trimmed);
            }
        }
        return paragraphs;
    }

    private static void hardSplit(List<String> chunks, String para)
    {
        int step = HARD_SPLIT_CHARS - OVERLAP_CHARS;
        for (int start = 0; start < para.length(); start += step)
        {
            int end = Math.min(start + HARD_SPLIT_CHARS, para.length());
            chunks.add(para.substring(start, end).trim());
            if (end >= para.length())
            {
                break;
            }
        }
    }

    private static String flush(List<String> chunks, StringBuilder current)
    {
        String value = current.toString().trim();
        current.setLength(0);
        if (!value.isEmpty())
        {
            chunks.add(value);
        }
        return value;
    }

    private static String tail(String value, int chars)
    {
        if (value == null || value.length() <= chars)
        {
            return value == null ? "" : value;
        }
        return value.substring(value.length() - chars);
    }
}
