package com.backend.debt.mapper;

import com.backend.debt.model.entity.DeclaredConfirmInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 申报确认信息Mapper接口
 */
@Mapper
public interface DeclaredConfirmInfoMapper extends BaseMapperX<DeclaredConfirmInfoEntity> {
    // 可以添加自定义查询方法
} 