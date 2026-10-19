package com.ruoyi.tenant.form;

import com.ruoyi.common.core.domain.model.LoginBody;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @Author: eddie
 * @Date: 2022/1/22
 * @Description:
 */
@Data
public class TenantRegisterBody extends LoginBody {

    /** 公司名 */
    @NotBlank(message = "公司名不能为空")
    @Size(max = 100, message = "公司名长度不能超过100个字符")
    public String companyName;

    /** 联系人名 */
    @Size(max = 50, message = "联系人名长度不能超过50个字符")
    public String contactName;

    /** email */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    public String email;

    /** mobile */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^\\d{11}$", message = "手机号格式不正确")
    public String mobile;

    /** 公司缩写（必填），作为租户标识。仅限小写字母、数字、下划线，3-30位 */
    @NotBlank
    @Pattern(regexp = "^[a-z][a-z0-9_]{2,29}$", message = "公司缩写格式不正确（需为小写字母开头，3-30位的字母、数字、下划线组合）")
    public String tenantAlias;

    /** 租户ID，由服务端生成 */
    public String tenantName;

    /** 管理员用户名 */
    public String adminName;

    /** 管理员密码 */
    @NotBlank(message = "管理员密码不能为空")
    @Size(min = 6, max = 20, message = "管理员密码长度必须在6到20个字符之间")
    public String adminPass;

}
