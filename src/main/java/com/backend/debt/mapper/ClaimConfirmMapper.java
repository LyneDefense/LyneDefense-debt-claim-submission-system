package com.backend.debt.mapper;

import com.backend.debt.mapper.query.LambdaQueryWrapperX;
import com.backend.debt.model.entity.ClaimConfirmEntity;
import org.apache.ibatis.annotations.Mapper;

/** 申报确认信息Mapper接口 */
@Mapper
public interface ClaimConfirmMapper extends BaseMapperX<ClaimConfirmEntity> {

  default ClaimConfirmEntity selectByClaimFillingId(String claimFillingId) {
    return this.selectOne(
        new LambdaQueryWrapperX<ClaimConfirmEntity>()
            .eq(ClaimConfirmEntity::getClaimFillingId, claimFillingId));
  }
}
