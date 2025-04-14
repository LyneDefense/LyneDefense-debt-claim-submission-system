package com.backend.debt.mapper.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.postgresql.util.PGobject;

public class BaseJsonTypeHandler<T extends JsonNode> extends BaseTypeHandler<T> {
  private static final ObjectMapper mapper = new ObjectMapper();

  @Override
  public void setNonNullParameter(
      PreparedStatement preparedStatement, int i, T t, JdbcType jdbcType) throws SQLException {
    PGobject pgObject = new PGobject();
    pgObject.setType("jsonb");
    try {
      pgObject.setValue(mapper.writeValueAsString(t));
    } catch (JsonProcessingException e) {
      throw new SQLException(
          String.format(
              "map object %s to PgObject failed. Internal error %s", t.getClass(), e.getMessage()));
    }
    preparedStatement.setObject(i, pgObject);
  }

  @Override
  public T getNullableResult(ResultSet resultSet, String s) throws SQLException {
    TypeReference<T> clazz = new TypeReference<T>() {};
    try {
      byte[] resultSBytes = resultSet.getBytes(s);
      return resultSBytes != null ? mapper.readValue(resultSBytes, clazz) : null;
    } catch (IOException e) {
      throw new SQLException(
          String.format(
              "map PgObject to object %s failed. Internal error %s",
              clazz.getClass(), e.getMessage()));
    }
  }

  @Override
  public T getNullableResult(ResultSet resultSet, int i) throws SQLException {
    TypeReference<T> clazz = new TypeReference<T>() {};
    try {
      byte[] resultSBytes = resultSet.getBytes(i);
      return resultSBytes != null ? mapper.readValue(resultSBytes, clazz) : null;
    } catch (IOException e) {
      throw new SQLException(
          String.format(
              "map PgObject to object %s failed. Internal error %s",
              clazz.getClass(), e.getMessage()));
    }
  }

  @Override
  public T getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
    TypeReference<T> clazz = new TypeReference<T>() {};
    try {
      byte[] resultSBytes = callableStatement.getBytes(i);
      return resultSBytes != null ? mapper.readValue(resultSBytes, clazz) : null;
    } catch (IOException e) {
      throw new SQLException(
          String.format(
              "map PgObject to object %s failed. Internal error %s",
              clazz.getClass(), e.getMessage()));
    }
  }
}
