package com.backend.debt.mapper.handler;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class DoubleListTypeHandler extends BaseTypeHandler<List<Double>> {

  @Override
  public void setNonNullParameter(
      PreparedStatement ps, int i, List<Double> parameter, JdbcType jdbcType) throws SQLException {
    if (parameter == null) {
      ps.setNull(i, Types.ARRAY);
      return;
    }

    // 将 List<Double> 转换为 Double[] 数组
    Double[] array = parameter.toArray(new Double[0]);

    // 获取数据库连接
    Connection conn = ps.getConnection();
    if (conn == null) {
      throw new SQLException("Connection is null, unable to create SQL Array.");
    }

    // 使用 PostgreSQL 的 float8 类型来处理 Double 数组（数据库支持的类型）
    Array sqlArray = conn.createArrayOf("float8", array);
    ps.setArray(i, sqlArray);
  }

  @Override
  public List<Double> getNullableResult(ResultSet rs, String columnName) throws SQLException {
    Array array = rs.getArray(columnName);
    if (array == null) {
      return null;
    }
    Double[] intArray = (Double[]) array.getArray();
    return Arrays.asList(intArray);
  }

  @Override
  public List<Double> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    Array array = rs.getArray(columnIndex);
    if (array == null) {
      return null;
    }
    Double[] intArray = (Double[]) array.getArray();
    return Arrays.asList(intArray);
  }

  @Override
  public List<Double> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    Array array = cs.getArray(columnIndex);

    // 检查是否为 NULL 或空数组
    if (array == null) {
      return null;
    }
    Double[] doubleArray = (Double[]) array.getArray();
    return Arrays.asList(doubleArray);
  }
}
