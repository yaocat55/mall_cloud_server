package cn.net.mall.basic.dto;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 短信发送记录查询条件 DTO（分页查询用）
 *
 * @date 2025-07-03
 */
@Data
@Schema(description = "短信发送记录查询条件DTO")
public class CommonSmsRecordConditionDTO extends RequestConditionEntity {

    @Schema(description = "系统ID列表")
    private List<Long> idList;

    @Schema(description = "系统ID")
    private Long id;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "sms Code")
    private String smsCode;

    @Schema(description = "expire Second")
    private Integer expireSecond;

    @Schema(description = "send Time")
    private Date sendTime;

    @Schema(description = "create User Id")
    private Long createUserId;

    @Schema(description = "create User Name")
    private String createUserName;

    @Schema(description = "update User Id")
    private Long updateUserId;

    @Schema(description = "update User Name")
    private String updateUserName;

    @Schema(description = "是否删除")
    private Integer isDel;

    @Schema(description = "短信类型")
    private Integer type;
}
