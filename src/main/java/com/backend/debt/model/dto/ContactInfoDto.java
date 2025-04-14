package com.backend.debt.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactInfoDto {
    /** 收件人 */
    @NotEmpty private String recipient;

    /** 联系电话 */
    @NotEmpty private String contactPhone;

    /** 邮寄地址 */
    @NotEmpty private String mailingAddress;

    /** 电子邮箱 */
    private String email;
}
