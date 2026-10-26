package com.ruoyi.crm.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Maps PostgreSQL jsonb string arrays ↔ {@code List<String>}.
 * Prefer pairing with {@code CAST(#{x, typeHandler=...} AS jsonb)} on write.
 */
@MappedTypes(List.class)
@MappedJdbcTypes(JdbcType.OTHER)
public class JsonbStringListTypeHandler extends BaseTypeHandler<List<String>>
{
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<List<String>> TYPE = new TypeReference<List<String>>() {};

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType)
            throws SQLException
    {
        try
        {
            ps.setString(i, MAPPER.writeValueAsString(parameter == null ? Collections.emptyList() : parameter));
        }
        catch (Exception e)
        {
            throw new SQLException("Failed to serialize List<String> to jsonb", e);
        }
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException
    {
        return parse(rs.getString(columnName));
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException
    {
        return parse(rs.getString(columnIndex));
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException
    {
        return parse(cs.getString(columnIndex));
    }

    private static List<String> parse(String json) throws SQLException
    {
        if (json == null || json.trim().isEmpty())
        {
            return Collections.emptyList();
        }
        try
        {
            List<String> list = MAPPER.readValue(json, TYPE);
            return list == null ? Collections.emptyList() : list;
        }
        catch (Exception e)
        {
            throw new SQLException("Failed to parse jsonb string array: " + json, e);
        }
    }
}
