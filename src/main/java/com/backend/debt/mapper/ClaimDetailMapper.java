package com.backend.debt.mapper;

import com.backend.debt.model.entity.ClaimDetailEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 申报详情Mapper接口
 */
@Mapper
public interface ClaimDetailMapper extends BaseMapperX<ClaimDetailEntity> {
    // 可以添加自定义查询方法
} 