package com.backend.debt.mapper.handler;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class IntegerListTypeHandler extends BaseTypeHandler<List<Integer>> {

  @Override
  public void setNonNullParameter(
      PreparedStatement ps, int i, List<Integer> parameter, JdbcType jdbcType) throws SQLException {
    if (parameter == null) {
      ps.setNull(i, Types.ARRAY);
      return;
    }
    Integer[] array = parameter.toArray(new Integer[0]);
    Connection conn = ps.getConnection();
    Array sqlArray = conn.createArrayOf("integer", array);
    ps.setArray(i, sqlArray);
  }

  @Override
  public List<Integer> getNullableResult(ResultSet rs, String columnName) throws SQLException {
    Array array = rs.getArray(columnName);
    if (array == null) {
      return null;
    }
    Integer[] intArray = (Integer[]) array.getArray();
    return Arrays.asList(intArray);
  }

  @Override
  public List<Integer> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    Array array = rs.getArray(columnIndex);
    if (array == null) {
      return null;
    }
    Integer[] intArray = (Integer[]) array.getArray();
    return Arrays.asList(intArray);
  }

  @Override
  public List<Integer> getNullableResult(CallableStatement cs, int columnIndex)
      throws SQLException {
    Array array = cs.getArray(columnIndex);
    if (array == null) {
      return null;
    }
    Integer[] intArray = (Integer[]) array.getArray();
    return Arrays.asList(intArray);
  }
}
