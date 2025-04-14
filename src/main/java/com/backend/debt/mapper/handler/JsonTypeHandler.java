package com.backend.debt.mapper.handler;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(JsonNode.class)
public class JsonTypeHandler extends BaseJsonTypeHandler<JsonNode> {}
