package com.backend.debt.mapper;

import com.backend.debt.model.entity.AgentEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 代理人信息Mapper接口
 */
@Mapper
public interface AgentMapper extends BaseMapperX<AgentEntity> {
    // 可以添加自定义查询方法
} 