package com.backend.debt.service.impl;

import com.backend.debt.exceptions.CustomException;
import com.backend.debt.exceptions.HttpResponseStatus;
import com.backend.debt.mapper.CreditorMapper;
import com.backend.debt.mapper.query.LambdaQueryWrapperX;
import com.backend.debt.model.dto.CreditorDto;
import com.backend.debt.model.entity.CreditorEntity;
import com.backend.debt.service.IClaimCreditorService;
import com.backend.debt.service.IClaimService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class IClaimCreditorServiceImpl implements IClaimCreditorService {

  @Resource private CreditorMapper creditorMapper;
  @Resource private IClaimService claimService;

  @Override
  public List<CreditorDto> getCreditorsByClaimId(String claimId) {
    // 验证债权ID是否存在
    claimService.validateAndGet(claimId);

    // 查询该债权下的所有债权人
    List<CreditorEntity> creditorEntities =
        creditorMapper.selectList(
            new LambdaQueryWrapperX<CreditorEntity>().eq(CreditorEntity::getClaimId, claimId));

    return CreditorDto.ofList(creditorEntities);
  }

  @Override
  public Map<String, List<CreditorDto>> getCreditorsByClaimIds(List<String> claimIds) {
    if (CollectionUtils.isEmpty(claimIds)) {
      return new HashMap<>();
    }
    
    // 一次性查询所有债权对应的债权人
    List<CreditorEntity> allCreditors = creditorMapper.selectList(
        new LambdaQueryWrapperX<CreditorEntity>().in(CreditorEntity::getClaimId, claimIds));
    
    if (CollectionUtils.isEmpty(allCreditors)) {
      return claimIds.stream().collect(Collectors.toMap(
          claimId -> claimId,
          claimId -> new ArrayList<>()
      ));
    }
    
    // 按债权ID分组
    Map<String, List<CreditorEntity>> groupedByClaimId = allCreditors.stream()
        .collect(Collectors.groupingBy(CreditorEntity::getClaimId));
    
    // 转换为DTO
    Map<String, List<CreditorDto>> result = new HashMap<>();
    for (String claimId : claimIds) {
      List<CreditorEntity> entities = groupedByClaimId.getOrDefault(claimId, new ArrayList<>());
      result.put(claimId, CreditorDto.ofList(entities));
    }
    
    return result;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int addCreditors(String claimId, List<CreditorDto> creditorDtos) {
    // 验证债权ID是否存在
    claimService.validateAndGet(claimId);

    if (CollectionUtils.isEmpty(creditorDtos)) {
      return 0;
    }

    try {
      List<CreditorEntity> creditorEntities = new ArrayList<>();
      for (CreditorDto dto : creditorDtos) {
        CreditorEntity entity = new CreditorEntity();
        BeanUtils.copyProperties(dto, entity);
        entity.setClaimId(claimId);
        entity.setIdentificationNumber(dto.getIdNumber());
        creditorEntities.add(entity);
      }

      creditorMapper.insertBatch(creditorEntities);
      log.info("添加债权人信息成功，数量：{}，关联债权ID：{}", creditorEntities.size(), claimId);

      return creditorEntities.size();
    } catch (Exception e) {
      String errorMsg = "添加债权人信息异常，关联债权ID：" + claimId;
      log.error(errorMsg + "，异常信息：{}", e.getMessage(), e);
      throw new CustomException(
          HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "添加债权人信息失败", errorMsg);
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean updateCreditor(String creditorId, CreditorDto creditorDto) {
    // 验证债权人是否存在
    CreditorEntity creditorEntity = validateAndGetCreditor(creditorId);

    try {
      // 更新债权人信息
      BeanUtils.copyProperties(creditorDto, creditorEntity, "id", "claimId", "createTime");
      creditorEntity.setIdentificationNumber(creditorDto.getIdNumber());

      creditorMapper.updateById(creditorEntity);
      log.info("更新债权人信息成功，ID：{}", creditorId);

      return true;
    } catch (Exception e) {
      String errorMsg = "更新债权人信息异常，ID：" + creditorId;
      log.error(errorMsg + "，异常信息：{}", e.getMessage(), e);
      throw new CustomException(
          HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "更新债权人信息失败", errorMsg);
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean deleteCreditor(String creditorId) {
    // 验证债权人是否存在
    validateAndGetCreditor(creditorId);

    try {
      // 删除债权人记录
      creditorMapper.deleteById(creditorId);
      log.info("删除债权人信息成功，ID：{}", creditorId);

      return true;
    } catch (Exception e) {
      String errorMsg = "删除债权人信息异常，ID：" + creditorId;
      log.error(errorMsg + "，异常信息：{}", e.getMessage(), e);
      throw new CustomException(
          HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "删除债权人信息失败", errorMsg);
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int batchDeleteCreditors(List<String> creditorIds) {
    if (CollectionUtils.isEmpty(creditorIds)) {
      return 0;
    }

    try {
      // 批量删除债权人记录
      int count = creditorMapper.deleteBatchIds(creditorIds);
      log.info("批量删除债权人信息成功，数量：{}", count);

      return count;
    } catch (Exception e) {
      String errorMsg = "批量删除债权人信息异常";
      log.error(errorMsg + "，异常信息：{}", e.getMessage(), e);
      throw new CustomException(
          HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "批量删除债权人信息失败", errorMsg);
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int deleteCreditorsByClaimId(String claimId) {
    // 验证债权ID是否存在
    claimService.validateAndGet(claimId);

    try {
      // 查询该债权下的所有债权人
      List<CreditorEntity> creditorEntities =
          creditorMapper.selectList(
              new LambdaQueryWrapperX<CreditorEntity>().eq(CreditorEntity::getClaimId, claimId));

      if (CollectionUtils.isEmpty(creditorEntities)) {
        return 0;
      }

      // 收集债权人ID
      List<String> creditorIds = creditorEntities.stream().map(CreditorEntity::getId).toList();

      // 批量删除债权人记录
      int count = creditorMapper.deleteBatchIds(creditorIds);
      log.info("删除债权关联的债权人信息成功，数量：{}，关联债权ID：{}", count, claimId);

      return count;
    } catch (Exception e) {
      String errorMsg = "删除债权关联的债权人信息异常，关联债权ID：" + claimId;
      log.error(errorMsg + "，异常信息：{}", e.getMessage(), e);
      throw new CustomException(
          HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "删除债权关联的债权人信息失败", errorMsg);
    }
  }

  @Override
  public CreditorDto getCreditorById(String creditorId) {
    // 验证债权人是否存在
    CreditorEntity creditorEntity = validateAndGetCreditor(creditorId);

    // 转换为DTO
    CreditorDto creditorDto = new CreditorDto();
    BeanUtils.copyProperties(creditorEntity, creditorDto);
    creditorDto.setIdNumber(creditorEntity.getIdentificationNumber());

    return creditorDto;
  }

  /**
   * 验证并获取债权人信息
   *
   * @param creditorId 债权人ID
   * @return 债权人实体
   */
  private CreditorEntity validateAndGetCreditor(String creditorId) {
    if (!StringUtils.hasText(creditorId)) {
      throw new CustomException(HttpResponseStatus.BAD_REQUEST.code(), "债权人ID不能为空", "债权人ID为空");
    }

    CreditorEntity creditorEntity = creditorMapper.selectById(creditorId);
    if (creditorEntity == null) {
      throw new CustomException(
          HttpResponseStatus.NOT_FOUND.code(), "债权人不存在", "债权人不存在，ID：" + creditorId);
    }

    return creditorEntity;
  }
}
