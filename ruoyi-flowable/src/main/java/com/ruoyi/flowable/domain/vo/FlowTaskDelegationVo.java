package com.ruoyi.flowable.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel("工作流任务转派相关--请求参数")
public class FlowTaskDelegationVo extends FlowTaskVo{

    @ApiModelProperty("转派的类型")
    private String targetType;      //user or group

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("组名")
    private String groupName;

}
