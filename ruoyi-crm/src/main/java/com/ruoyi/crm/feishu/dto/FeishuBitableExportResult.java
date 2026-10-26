package com.ruoyi.crm.feishu.dto;

/**
 * 多维表格导出文件结果（由 Controller 写入 HttpServletResponse）。
 */
public class FeishuBitableExportResult
{
    private final byte[] content;
    private final String filename;
    private final String contentType;

    public FeishuBitableExportResult(byte[] content, String filename, String contentType)
    {
        this.content = content;
        this.filename = filename;
        this.contentType = contentType;
    }

    public byte[] getContent()
    {
        return content;
    }

    public String getFilename()
    {
        return filename;
    }

    public String getContentType()
    {
        return contentType;
    }
}
