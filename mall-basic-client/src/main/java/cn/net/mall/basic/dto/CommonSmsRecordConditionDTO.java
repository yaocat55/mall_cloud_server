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
@Schema(description = "短信发送记录查询条件DTO", example = "string")
public class CommonSmsRecordConditionDTO extends RequestConditionEntity {

    @Schema(description = "系统ID列表", example = "string")
    private List<Long> idList;

    @Schema(description = "系统ID", example = "0")
    private Long id;

    @Schema(description = "手机号", example = "string")
    private String phone;

    @Schema(description = "sms Code", example = "string")
    private String smsCode;

    @Schema(description = "expire Second", example = "0")
    private Integer expireSecond;

    @Schema(description = "send Time", example = "2024-01-01")
    private Date sendTime;

    @Schema(description = "create User Id", example = "0")
    private Long createUserId;

    @Schema(description = "create User Name", example = "string")
    private String createUserName;

    @Schema(description = "update User Id", example = "0")
    private Long updateUserId;

    @Schema(description = "update User Name", example = "string")
    private String updateUserName;

    @Schema(description = "是否删除", example = "0")
    private Integer isDel;

    @Schema(description = "短信类型", example = "0")
    private Integer type;
}
