package com.backend.debt.mapper;

import com.backend.debt.model.entity.DeclarationRegistrationEntity;
import com.backend.debt.model.dto.DeclarationRegistrationDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 债权申报登记Mapper接口
 */
@Mapper
public interface DeclarationRegistrationMapper extends BaseMapperX<DeclarationRegistrationEntity> {
    // 可以添加自定义查询方法
    
    /**
     * 根据ID查询债权申报详情，包含关联的债权人、代理人、联系方式和申报详情等信息
     *
     * @param id 债权申报ID
     * @return 债权申报详情DTO
     */
    DeclarationRegistrationDto getDeclarationDetailById(@Param("id") String id);
} 