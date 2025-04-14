package com.backend.debt.mapper;

import com.backend.debt.model.entity.CreditorEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 债权人信息Mapper接口
 */
@Mapper
public interface CreditorMapper extends BaseMapperX<CreditorEntity> {
    // 可以添加自定义查询方法
} 