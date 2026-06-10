package cn.net.mall.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
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
    private Long id;

    /**
     * 创建人ID
     */
    @ExcelProperty(value = "创建人ID", index = -2)
    private Long createUserId;

    /**
     * 创建人名称
     */
    @ExcelProperty(value = "创建人名称", index = -3)
    private String createUserName;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间", index = -4)
    private Date createTime;

    /**
     * 修改人ID
     */
    @ExcelProperty(value = "修改人ID", index = -5)
    private Long updateUserId;

    /**
     * 修改人名称
     */
    @ExcelProperty(value = "修改人名称", index = -6)
    private String updateUserName;

    /**
     * 修改时间
     */
    @ExcelProperty(value = "修改时间", index = -7)
    private Date updateTime;

    /**
     * 是否删除
     */
    @ExcelProperty(value = "是否删除", index = -8)
    private Integer isDel;
}
