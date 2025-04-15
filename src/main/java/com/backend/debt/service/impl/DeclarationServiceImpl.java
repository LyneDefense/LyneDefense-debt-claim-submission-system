package com.backend.debt.service.impl;

import com.backend.debt.mapper.CreditorMapper;
import com.backend.debt.mapper.DeclarationRegistrationMapper;
import com.backend.debt.mapper.query.LambdaQueryWrapperX;
import com.backend.debt.model.dto.CreditorDto;
import com.backend.debt.model.dto.DeclarationRegistrationDto;
import com.backend.debt.model.dto.DeclarationRegistrationSimpleDto;
import com.backend.debt.model.entity.CreditorEntity;
import com.backend.debt.model.entity.DeclarationRegistrationEntity;
import com.backend.debt.model.page.PageResult;
import com.backend.debt.model.query.DeclarationPageQuery;
import com.backend.debt.service.DeclarationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/** 债权申报服务实现类 */
@Service
@RequiredArgsConstructor
public class DeclarationServiceImpl implements DeclarationService {

  private final DeclarationRegistrationMapper declarationRegistrationMapper;
  private final CreditorMapper creditorMapper;

  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  @Override
  public PageResult<DeclarationRegistrationSimpleDto> pageDeclarationWithCreditors(
      DeclarationPageQuery query) {
    // 构建查询条件
    LambdaQueryWrapper<DeclarationRegistrationEntity> queryWrapper = new LambdaQueryWrapperX<>();

    // 添加债权申报筛选条件
    if (StringUtils.isNotBlank(query.getClaimNumber())) {
      queryWrapper.like(DeclarationRegistrationEntity::getClaimNumber, query.getClaimNumber());
    }

    if (StringUtils.isNotBlank(query.getRegistrar())) {
      queryWrapper.like(DeclarationRegistrationEntity::getRegistrar, query.getRegistrar());
    }

    // 申报日期范围筛选
    if (query.getDeclarationDateStart() != null) {
      queryWrapper.ge(
          DeclarationRegistrationEntity::getDeclarationDate, query.getDeclarationDateStart());
    }

    if (query.getDeclarationDateEnd() != null) {
      queryWrapper.le(
          DeclarationRegistrationEntity::getDeclarationDate, query.getDeclarationDateEnd());
    }

    // 添加排序，按创建时间降序
    queryWrapper.orderByDesc(DeclarationRegistrationEntity::getCreateTime);

    // 执行债权申报分页查询
    PageResult<DeclarationRegistrationEntity> pageResult =
        declarationRegistrationMapper.selectPage(query, queryWrapper);

    if (pageResult.getList().isEmpty()) {
      return new PageResult<>(Collections.emptyList(), 0L);
    }

    // 获取所有申报ID
    List<String> declarationIds =
        pageResult.getList().stream()
            .map(DeclarationRegistrationEntity::getId)
            .collect(Collectors.toList());

    // 构建债权人查询条件
    LambdaQueryWrapper<CreditorEntity> creditorQueryWrapper = new LambdaQueryWrapper<>();
    creditorQueryWrapper.in(CreditorEntity::getDeclarationId, declarationIds);

    // 如果有债权人名称条件，添加筛选
    if (StringUtils.isNotBlank(query.getCreditorName())) {
      creditorQueryWrapper.like(CreditorEntity::getName, query.getCreditorName());
    }

    // 查询相关的债权人信息
    List<CreditorEntity> creditors = creditorMapper.selectList(creditorQueryWrapper);

    // 按申报ID分组债权人
    Map<String, List<CreditorDto>> creditorMap =
        creditors.stream()
            .collect(
                Collectors.groupingBy(
                    CreditorEntity::getDeclarationId,
                    Collectors.mapping(this::convertToCreditorDto, Collectors.toList())));

    // 转换为组合DTO
    List<DeclarationRegistrationSimpleDto> dtoList =
        pageResult.getList().stream()
            .map(
                entity -> {
                  DeclarationRegistrationSimpleDto dto = convertToDeclarationWithCreditorsDto(entity);
                  dto.setCreditors(
                      creditorMap.getOrDefault(entity.getId(), Collections.emptyList()));
                  return dto;
                })
            .collect(Collectors.toList());

    // 返回转换后的分页结果
    return new PageResult<>(dtoList, pageResult.getTotal());
  }

  @Override
  public DeclarationRegistrationDto getDeclarationById(String id) {
    DeclarationRegistrationDto declarationDto =
        declarationRegistrationMapper.getDeclarationDetailById(id);
    // 根据已有的数据计算统计数据
    if (declarationDto == null) {
      // 如果找不到记录，可以考虑抛出自定义异常
      // throw new CustomException("未找到指定ID的债权申报信息");
      return null;
    }

    return declarationDto;
  }

  /**
   * 将申报实体转换为包含债权人的DTO
   *
   * @param entity 申报实体
   * @return 包含债权人的DTO
   */
  private DeclarationRegistrationSimpleDto convertToDeclarationWithCreditorsDto(
      DeclarationRegistrationEntity entity) {
    DeclarationRegistrationSimpleDto dto = new DeclarationRegistrationSimpleDto();
    BeanUtils.copyProperties(entity, dto);

    // 格式化创建时间
    if (entity.getCreateTime() != null) {
      dto.setCreateTime(entity.getCreateTime().format(DATE_FORMATTER));
    }

    return dto;
  }

  /**
   * 将债权人实体转换为DTO
   *
   * @param entity 债权人实体
   * @return 债权人DTO
   */
  private CreditorDto convertToCreditorDto(CreditorEntity entity) {
    CreditorDto dto = new CreditorDto();
    BeanUtils.copyProperties(entity, dto);
    return dto;
  }
}
