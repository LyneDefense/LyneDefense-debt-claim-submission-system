package com.backend.debt.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgentDto {

    /** 代理人姓名 */
    @NotEmpty
    private String name;

    /** 代理人职务 */
    private String position;

    /** 联系电话 */
    @NotEmpty private String phone;

    /** 是否有债权人会议表决权 */
    private Boolean hasVotingRight;
}
