package com.backend.debt.model.query;

import com.backend.debt.model.dto.CreditorDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 批量添加债权人请求DTO */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "批量添加债权人请求")
public class CreditorAddQuery {

  @ApiModelProperty(value = "债权人列表")
  @Valid
  @NotEmpty(message = "债权人列表不能为空")
  private List<CreditorDto> creditors;
}
