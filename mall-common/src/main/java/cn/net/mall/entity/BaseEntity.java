package cn.net.mall.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 公共实体类
 *
 * @date 2024/1/4 下午3:32
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseEntity implements Serializable {

    /**
     * 系统ID
     */
    @ExcelProperty(value = "系统ID", index = -1)
    @Schema(description = "系统ID")
    private Long id;

    /**
     * 创建人ID
     */
    @ExcelProperty(value = "创建人ID", index = -2)
    @Schema(description = "create User Id")
    private Long createUserId;

    /**
     * 创建人名称
     */
    @ExcelProperty(value = "创建人名称", index = -3)
    @Schema(description = "create User Name")
    private String createUserName;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间", index = -4)
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 修改人ID
     */
    @ExcelProperty(value = "修改人ID", index = -5)
    @Schema(description = "update User Id")
    private Long updateUserId;

    /**
     * 修改人名称
     */
    @ExcelProperty(value = "修改人名称", index = -6)
    @Schema(description = "update User Name")
    private String updateUserName;

    /**
     * 修改时间
     */
    @ExcelProperty(value = "修改时间", index = -7)
    @Schema(description = "修改时间")
    private Date updateTime;

    /**
     * 是否删除
     */
    @ExcelProperty(value = "是否删除", index = -8)
    @Schema(description = "是否删除")
    private Integer isDel;
}
