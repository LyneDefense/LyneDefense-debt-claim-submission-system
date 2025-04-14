package com.backend.debt.mapper;

import com.backend.debt.model.entity.ContactInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收件信息Mapper接口
 */
@Mapper
public interface ContactInfoMapper extends BaseMapperX<ContactInfoEntity> {
    // 可以添加自定义查询方法
} 