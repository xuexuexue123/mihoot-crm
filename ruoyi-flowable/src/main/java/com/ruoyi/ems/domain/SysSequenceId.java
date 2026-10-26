package com.ruoyi.ems.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * sequenceid对象 sys_sequence_id
 * 
 * @author luohd
 * @date 2022-12-05
 */
public class SysSequenceId extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 序列号 */
    private Long sequenceid;

    /** 前缀 */
    @Excel(name = "前缀")
    private String prefix;

    /** 当前数字 */
    @Excel(name = "当前数字")
    private Long currentNumber;

    public void setSequenceid(Long sequenceid) 
    {
        this.sequenceid = sequenceid;
    }

    public Long getSequenceid() 
    {
        return sequenceid;
    }
    public void setPrefix(String prefix) 
    {
        this.prefix = prefix;
    }

    public String getPrefix() 
    {
        return prefix;
    }
    public void setCurrentNumber(Long currentNumber) 
    {
        this.currentNumber = currentNumber;
    }

    public Long getCurrentNumber() 
    {
        return currentNumber;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("sequenceid", getSequenceid())
            .append("prefix", getPrefix())
            .append("currentNumber", getCurrentNumber())
            .toString();
    }
}
