package com.ruoyi.crm.wechatmp.support;

import java.util.Arrays;

import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.toc.TocExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;

/**
 * Markdown 转 HTML 工具，基于 flexmark-java。
 * 用于将用户输入的 Markdown 正文转换为微信草稿 API 所需的 HTML 格式。
 */
public final class MarkdownToHtmlConverter
{
    private static final Parser PARSER;
    private static final HtmlRenderer RENDERER;

    static
    {
        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, Arrays.asList(
                TablesExtension.create(),
                TocExtension.create()
        ));
        PARSER = Parser.builder(options).build();
        RENDERER = HtmlRenderer.builder(options).build();
    }

    private MarkdownToHtmlConverter()
    {
    }

    /**
     * 将 Markdown 文本转换为 HTML。
     *
     * @param markdown Markdown 格式文本
     * @return HTML 字符串
     */
    public static String toHtml(String markdown)
    {
        if (markdown == null || markdown.isEmpty())
        {
            return "";
        }
        return RENDERER.render(PARSER.parse(markdown));
    }
}
