package com.ruoyi.crm.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmDataTableColumn;
import com.ruoyi.crm.domain.dto.datatable.DataTableConditionDTO;
import com.ruoyi.crm.domain.dto.datatable.DataTableRowQuery;
import com.ruoyi.crm.service.support.DataTableConstants;

/**
 * JSONB 条件查询构建与校验
 */
@Service
public class DataTableConditionService
{
    private static final Set<String> ALLOWED_OPERATORS = new HashSet<>();

    static
    {
        ALLOWED_OPERATORS.add("equals");
        ALLOWED_OPERATORS.add("not_equals");
        ALLOWED_OPERATORS.add("contains");
        ALLOWED_OPERATORS.add("not_contains");
        ALLOWED_OPERATORS.add("starts_with");
        ALLOWED_OPERATORS.add("ends_with");
        ALLOWED_OPERATORS.add("gt");
        ALLOWED_OPERATORS.add("gte");
        ALLOWED_OPERATORS.add("lt");
        ALLOWED_OPERATORS.add("lte");
        ALLOWED_OPERATORS.add("exists");
    }

    public DataTableRowQuery buildQuery(String dataTableId, List<DataTableConditionDTO> conditions,
            String combineWith, Integer limit, List<CrmDataTableColumn> columns)
    {
        DataTableRowQuery query = new DataTableRowQuery();
        query.setDataTableId(dataTableId);
        query.setCombineWith(normalizeCombineWith(combineWith));
        query.setLimit(normalizeLimit(limit));
        if (conditions != null && !conditions.isEmpty())
        {
            for (DataTableConditionDTO condition : conditions)
            {
                validateCondition(condition, columns);
            }
            query.setConditions(conditions);
        }
        return query;
    }

    public String normalizeCombineWith(String combineWith)
    {
        if (StringUtils.isBlank(combineWith))
        {
            return DataTableConstants.COMBINE_AND;
        }
        String upper = combineWith.trim().toUpperCase();
        if (DataTableConstants.COMBINE_AND.equals(upper) || DataTableConstants.COMBINE_OR.equals(upper))
        {
            return upper;
        }
        throw new ServiceException("combineWith 必须为 AND 或 OR");
    }

    public int normalizeLimit(Integer limit)
    {
        if (limit == null)
        {
            return DataTableConstants.DEFAULT_LIST_LIMIT;
        }
        if (limit <= 0)
        {
            return DataTableConstants.DEFAULT_LIST_LIMIT;
        }
        return Math.min(limit, DataTableConstants.MAX_LIST_LIMIT);
    }

    private void validateCondition(DataTableConditionDTO condition, List<CrmDataTableColumn> columns)
    {
        if (condition == null || StringUtils.isBlank(condition.getField()))
        {
            throw new ServiceException("条件 field 不能为空");
        }
        String operator = condition.getOperator();
        if (StringUtils.isBlank(operator) || !ALLOWED_OPERATORS.contains(operator))
        {
            throw new ServiceException("不支持的条件运算符: " + operator);
        }
        if (!isAllowedField(condition.getField(), columns))
        {
            throw new ServiceException("条件字段不在表定义中: " + condition.getField());
        }
        if (!"exists".equals(operator) && condition.getValue() == null)
        {
            throw new ServiceException("条件 value 不能为空: " + condition.getField());
        }
    }

    private boolean isAllowedField(String field, List<CrmDataTableColumn> columns)
    {
        if (DataTableConstants.isSystemColumn(field))
        {
            return true;
        }
        for (CrmDataTableColumn column : columns)
        {
            if (field.equals(column.getName()))
            {
                return true;
            }
        }
        return false;
    }
}
